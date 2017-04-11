//$Id: LBSCRecenChangeHandler.java,v 1.57 2007/06/11 07:45:58 krishnakumar Exp $
package com.motorola.emh.core.inventory;


// java imports
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// NMS imports
import com.adventnet.management.config.snmp.SnmpAttribute;
import com.adventnet.management.config.xml.Attribute;
import com.adventnet.management.log.Log;
import com.adventnet.management.transaction.ConnectionPool;
import com.adventnet.management.transaction.UserTransactionException;
import com.adventnet.nms.store.NmsStorageException;
import com.adventnet.nms.store.relational.RelationalUtil;
import com.adventnet.nms.topodb.ManagedObject;
import com.adventnet.snmp.snmp2.SnmpAPI;

//EMH imports
import com.motorola.emh.common.constants.DeviceConstants;
import com.motorola.emh.common.exception.EMHException;
import com.motorola.emh.common.log.EMHLogManager;
import com.motorola.emh.common.util.EMHUtil;
import com.motorola.emh.common.util.ObjectNamingUtility;
import com.motorola.emh.common.util.VirtualHostUtil;
import com.motorola.emh.core.config.EMHConfigurationModule;
import com.motorola.emh.core.config.EMHCoreProvUtil;

import com.motorola.emh.core.constants.ConfigCommandConstants;
//import com.motorola.emh.core.fault.CPDataDownloadProcessor;
import com.motorola.emh.core.modeling.DataObject;
import com.motorola.emh.core.modeling.RelationObject;
import com.motorola.emh.core.modeling.LMCCDO;
//import com.motorola.emh.core.modeling.Sector;
import com.motorola.emh.core.necf.NECBDataGeneration;
import com.motorola.emh.core.necf.NECFUtility;
import com.motorola.emh.core.necf.NLCBDataGeneration;
import com.motorola.emh.core.necf.NLCBDataHandler;
import com.motorola.emh.core.perf.CoreDcUtil;
import com.motorola.emh.core.server.attredt.LBSCAttributeEdtHandler;
import com.motorola.emh.core.util.AttribDetails;
import com.motorola.emh.core.util.CageUtil;
import com.motorola.emh.core.util.CoreUtil;
import com.motorola.emh.core.util.DeviceTypeConstants;
import com.motorola.emh.core.util.ObjectDetails;
import com.motorola.emh.core.util.ObjectDetailsConstants;
import com.motorola.emh.core.util.ObjectDetailsHandler;
//import com.motorola.emh.core.license.EMHLicenseHandler;
//import com.motorola.emh.core.license.LicenseUtil;

public class LBSCRecenChangeHandler implements DeviceTypeConstants,EMHTopoActionListener
{
	private String journelEntryOid=".1.3.6.1.4.1.161.2052.1.44.1.9.0";

	//Assume the MessageSizeCheckSR < 512 KB
	private static final int MAX_ATTRIBUTE_COUNT_FOR_SINGLE_SET=10;

	private static final String CAGE_VS_CMD="CAGE_VS_CMD";

	private static final String MCCDO_LIST="MCCDO_LIST";

	private static final String SECTOR_LIST="SECTOR_LIST";

	private static final String CARRIER_LIST="CARRIER_LIST";

	private static final String MODIFIED_APC_LIST="MODIFIED_APC_LIST";

	private static final String UPDATE_NLCB="UPDATE_NLCB";

	private static final String CAGE_VS_NECJ_OBJ_NAME_LIST="CAGE_VS_NECJ_OBJ_NAME_LIST";

	private static final String CAGE_VS_NECJ_PROPS_LIST="CAGE_VS_NECJ_PROPS_LIST";

	private static final int ADD_OBJECT=1;

	private static final int DELETE_OBJECT=2;

	private static final int UPDATE_OBJECT=3;

	private static LBSCRecenChangeHandler listener = null;

	private Hashtable resRequiredThVsResult=null;

	private Hashtable multiTransThreadList = null;

	private Hashtable localThVsObjHash=null;

	private ArrayList nlcbObjTypes = null;

	private LBSCRecenChangeHandler()
	{
		resRequiredThVsResult=new Hashtable();
		localThVsObjHash=new Hashtable();
		multiTransThreadList = new Hashtable(3);
		nlcbObjTypes = NLCBDataHandler.getObjTypes();
		try{
			EMHCoreTopoMgr.getInstance().register(this);
		}catch(Exception e)
		{
			EMHUtil.printErr("Unable to register LBSCRecenChangeHandler in the EMHCoreTopoMgr ", e);
		}
	}

	public synchronized static LBSCRecenChangeHandler getInstance()
	{
		if(listener == null)listener = new LBSCRecenChangeHandler();
		return listener;
	}

	public void beginMultipleTransaction()
	{
		multiTransThreadList.put(Thread.currentThread(), Long.valueOf(System.currentTimeMillis()));
	}

	public void commitMultipleTransaction()
	{
		try{
			if(localThVsObjHash.containsKey(Thread.currentThread()))
			{
				handlePostOperation();
			}
		}
		catch (EMHException ex)
		{
			EMHLogManager.EMH_ERR.fail("Failed while executing config Task based on recent Change .",ex);
		}
		finally
		{
			cleanup();
		}
	}

	public void rollBackMultipleTransaction()
	{
		//This method will never be used since the first transaction itself will update the database.

		cleanup();
	}

	private boolean isInMultipleTransaction(){
		return multiTransThreadList.containsKey(Thread.currentThread());
	}

	public void addResultRequiredThread(Thread resultRequiredThread)
	{
		Hashtable result=new Hashtable();
		result.put("SUCCESS_LIST", new Hashtable());
		result.put("FAILED_LIST", new Hashtable());
		resRequiredThVsResult.put(resultRequiredThread, result);
	}

	public Hashtable removeResult(Thread resultRequiredThread)
	{
		return (Hashtable)resRequiredThVsResult.remove(resultRequiredThread);
	}


	public void update(EMHTopoNotificationEvent event){
		p("update Notification called.");
		initCurrentThread();
		try
		{
			if(event.isBulkNotificationEvent()){
				p("Trying to handle Bulk notification ");
				Vector v = event.getBulkNotificationEvent();
				for(int i=0;i<v.size();i++){
					EMHTopoNotificationEvent evt1 = (EMHTopoNotificationEvent)v.elementAt(i);
					handleTopoEvent(evt1);
				}
			}else
			{
				handleTopoEvent(event);
			}
			if(!isInMultipleTransaction()){
				handlePostOperation();
			}

		}
		catch (EMHException ex)
		{
			EMHLogManager.EMH_ERR.fail("Failed while executing config Task based on recent Change .",ex);
		}
		finally
		{
			if(!isInMultipleTransaction()){
				cleanup();
			}
		}
	}

	private void handlePostOperation() throws EMHException
	{
		handlePMApcModemList();
		handleNLCBGeneration();
		handleRecentChangeOperations();
	}

	private void handleNLCBGeneration() throws EMHException
	{
		String updateNlCp=(String)getCurrentObject(UPDATE_NLCB);
		if(new Boolean(updateNlCp).booleanValue())
		{
			NLCBDataGeneration.getInstance().generateNLCBData();
		}
	}

	private void handlePMApcModemList()
	{
		ArrayList modifiedApcList=(ArrayList)getCurrentObject(MODIFIED_APC_LIST);

		ArrayList mccdoList=(ArrayList)getCurrentObject(MCCDO_LIST);

		ArrayList sectorList=(ArrayList)getCurrentObject(SECTOR_LIST);

		ArrayList carrierList=(ArrayList)getCurrentObject(CARRIER_LIST);

		Statement stmt=null;

		if(carrierList.size()!=0)
		{
			String query="select "+RelationalUtil.getAlias("parentKey")+" from ManagedObject where "+RelationalUtil.getAlias("name")+" IN ("+convertDBQueryINStr(carrierList)+")";
			ResultSet rs=null;
			try{
				if(stmt==null)
				{
					stmt=ConnectionPool.getInstance().getConnection().createStatement();
				}
				rs=stmt.executeQuery(query);
				while(rs.next())
				{
					sectorList.add(rs.getString(1));
				}
			}catch(Exception e)
			{
				EMHLogManager.logError("Problem occured in getting Sector for Carrier. query "+query,e ,EMHLogManager.EMH_PERF);
			}
			finally
			{
				try{if(rs!=null )rs.close();}catch(Exception e){}
			}
		}

		if(sectorList.size()!=0)
		{
			String query="select "+RelationalUtil.getAlias("name")+" from ManagedObject where "+RelationalUtil.getAlias("parentKey")+" in " +
					"(select "+RelationalUtil.getAlias("parentKey")+" from ManagedObject where "+RelationalUtil.getAlias("name")+" IN ("+convertDBQueryINStr(sectorList)+")) and "+RelationalUtil.getAlias("type")+"='"+MCCDO+"'";
			ResultSet rs=null;
			try{
				if(stmt==null)
				{
					stmt=ConnectionPool.getInstance().getConnection().createStatement();
				}
				rs=stmt.executeQuery(query);
				while(rs.next())
				{
					mccdoList.add(rs.getString(1));
				}
			}catch(Exception e)
			{
				EMHLogManager.logError("Problem occured in getting Sector for Carrier. query "+query,e ,EMHLogManager.EMH_PERF);
			}
			finally
			{
				try{if(rs!=null )rs.close();}catch(Exception e){}
			}
		}

		if(mccdoList.size()!=0)
		{
			String query="select APC."+RelationalUtil.getAlias("name")+" from APC, RelationObject dg,RelationObject pm,MCCDOCard " +
						"where MCCDOCard."+RelationalUtil.getAlias("name")+" in ( "+convertDBQueryINStr(mccdoList)+" ) and pm."+RelationalUtil.getAlias("relationship")+"='"+active_mapping+"' and pm."+RelationalUtil.getAlias("source")+"=dg."+RelationalUtil.getAlias("source")+" and " +
						"APC."+RelationalUtil.getAlias("name")+"=dg."+RelationalUtil.getAlias("target")+" and dg."+RelationalUtil.getAlias("relationship")+" = '"+data_grouping+"'";
			ResultSet rs=null;
			try{
				if(stmt==null)
				{
					stmt=ConnectionPool.getInstance().getConnection().createStatement();
				}
				rs=stmt.executeQuery(query);
				while(rs.next())
				{
					modifiedApcList.add(rs.getString(1));
				}
			}catch(Exception e)
			{
				EMHLogManager.logError("Problem occured in getting APC for MCCDO. query "+query,e ,EMHLogManager.EMH_PERF);
			}
			finally
			{
				try{if(rs!=null )rs.close();}catch(Exception e){}
			}
		}

		if(stmt!=null)
		{
			try{stmt.close();}catch(Exception e){}
		}

		if(modifiedApcList.size()!=0)
		{
			CoreDcUtil.getInstance().notifyPMApcModemChanges(modifiedApcList);
		}
	}

	private void handleRecentChangeOperations() throws EMHException
	{
		Hashtable cageVsObjNames=(Hashtable)getCurrentObject(CAGE_VS_NECJ_OBJ_NAME_LIST);
		Hashtable cageVsPropsList=(Hashtable)getCurrentObject(CAGE_VS_NECJ_PROPS_LIST);



		Hashtable cageVsCmd=(Hashtable)getCurrentObject(CAGE_VS_CMD);
		Hashtable resultHash=(Hashtable)resRequiredThVsResult.get(Thread.currentThread());
		for(Enumeration enu=cageVsCmd.keys();enu.hasMoreElements();)
		{
			String cageName=(String)enu.nextElement();

			//Start
			int necjEntryNo=-1;
			ArrayList objNamesList=(ArrayList)cageVsObjNames.get(cageName);
			ArrayList propsList=(ArrayList)cageVsPropsList.get(cageName);
			if(objNamesList!=null && propsList!=null && objNamesList.size()!=0 && propsList.size()!=0)
			{
				int count=objNamesList.size();
				
				// Send the properties one at at time...
				for (int i = 0; i < count; i++)
				{
					String[] objNames=new String[1];
					objNames[0] = (String) objNamesList.get(i);

					Properties[] propsArray=new Properties[1];
					propsArray[0] = (Properties) propsList.get(i);

					try
					{
						necjEntryNo = NECBDataGeneration.getInstance().addModEntry(cageName, NECFUtility.NECJ,objNames, propsArray);
					}
					catch(EMHException e)
					{
						EMHUtil.printErr("Problem occured in add NECJ entry...", e);
						((Hashtable)resultHash.get("FAILED_LIST")).put(cageName,"Unable to add NECJ entry. Reason : "+e.getMessage());
						continue;
					}
				}

			}

			//End
			CageCmdObj cageCmdObj=getCageCmdObj(cageName);
			//CageCmdObj cageCmdObj=(CageCmdObj)cageVsCmd.get(cageName);
			boolean result=false;
			if(cageCmdObj.isResetRequired)
			{
				String configResult=null;
				try{
					p(" Trying to execute RESET command for Cage : cageName ="+cageName);
					configResult=EMHConfigurationModule.getInstance().performConfigOperation(ConfigCommandConstants.RESET,cageName, new Properties());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				if(configResult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configResult))
				{
					result=true;
				}
				p(" Result of RESET command for Cage : cageName ="+cageName+"; result = "+result);
			}
			else
			{
				//preAction operations
				boolean isResetRequired=false;
				int preActionSize=cageCmdObj.preActionObjNames.size();
				for(int i=0;i<preActionSize;i++)
				{
					boolean preActionResult=false;
					String objName=(String)cageCmdObj.preActionObjNames.get(i);
					String preAction=(String)cageCmdObj.preActions.get(i);
					String configResult=null;
					try{
						p(" Trying to execute preAction command for Cage : cageName ="+cageName+"; objName = "+objName+" ; operation = "+preAction);
						Properties configProps=new Properties();
						//Need not forward failure event to O3.
						configProps.setProperty("$correlated#","100");
						configProps.setProperty("$testMgmt#", "true");
						configResult=EMHConfigurationModule.getInstance().performConfigOperation(preAction,objName, configProps);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					if(configResult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configResult))
					{
						preActionResult=true;
					}
					p(" Result of preAction command for Cage : cageName ="+cageName+"; objName = "+objName+" ; operation = "+preAction+" ; RESULT = "+preActionResult);
					if(!preActionResult)
					{
						isResetRequired=true;
						p("Unable to perform preAction "+preAction+" for "+objName+". Hence trying to perform RESET for Cage ="+cageName);
						break;
					}

				}
				if(!isResetRequired)
				{
					//preAction not failed
					if(cageCmdObj.isBulkDownloadCommitRequired)
					{
						int endVersion=-1;
						String endVersionStr=NECBDataGeneration.getInstance().getCurrentFileVersionFromDB(cageName, NECFUtility.NECJ);
						if(!EMHUtil.getInstance().isNull(endVersionStr))
						{
							endVersion=Integer.parseInt(endVersionStr);
						}
						if( (cageCmdObj.startVersion!=-1) && (endVersion!=-1) )
						{ 
							String necj[] = NECBDataGeneration.getInstance().generateXMLFile(cageName, NECFUtility.BCC,cageCmdObj.startVersion,endVersion,null);
							Properties configProps=new Properties();
							String destinationIP = VirtualHostUtil.getVirtualIP();
							if(destinationIP == null)
							{
								try
								{
									destinationIP = EMHUtil.getInstance().getLocalPhysicalIp();
								}
								catch(Exception e)
								{
									e.printStackTrace();
									destinationIP="";
								}
							}
							configProps.setProperty("$destinationIn#",destinationIP);
							configProps.setProperty("$fileNameIn#",necj[0]);
							String configResult=null;
							try{
								p(" Trying to execute BULK_CONFIG_OPT_DOWNLOAD_COMMIT command for Cage : cageName ="+cageName);
								configResult=EMHConfigurationModule.getInstance().performConfigOperation(ConfigCommandConstants.BULK_CONFIG_OPT_DOWNLOAD_COMMIT,cageName, configProps);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							if(configResult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configResult))
							{
								result=true;
							}
							p(" Result of BULK_CONFIG_OPT_DOWNLOAD_COMMIT command for Cage : cageName ="+cageName+"; result = "+result);
						}
					}
					else if(cageCmdObj.attrList!=null && cageCmdObj.attrList.size()>0)
					{
						String taskNodeStr="";
						if(necjEntryNo!=-1)
						{
							cageCmdObj.attrList.add(getNecjEntryAttribute(necjEntryNo));
						}
						for(Enumeration attrEnum=(cageCmdObj.attrList).elements();attrEnum.hasMoreElements();)
						{
							Attribute attrib=(Attribute)attrEnum.nextElement();
							taskNodeStr+=attrib.toString();
						}
						Properties configProps=new Properties();
						configProps.setProperty("$ATTRIBUTE_NODE#", taskNodeStr);
						String configRestult=null;
						try{
							p("CONFIG Props -->"+configProps);
							configRestult=EMHConfigurationModule.getInstance().performConfigOperation(ConfigCommandConstants.RECENT_CHANGE,cageName, configProps);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						p("CONFIG RESULT = "+configRestult);
						if(configRestult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configRestult))
						{
							result=true;
							p("CONFIG RESULT for Singe Message Sent is SUCCESS .");
						}
					}else{
						EMHUtil.printOut("Nothing to do with the update", Log.DEBUG);
					}

	// 				//cp download process is called
// 					if(cageCmdObj.isCPDownLoadRequired)
// 					{
// 						p(" Trying to call CP_DOWNLOAD command for Cage : cageName ="+cageName);
// 						CPDataDownloadProcessor cpProcessor=new CPDataDownloadProcessor(true);
// 						result=cpProcessor.startCPDownloadOp(cageName);
// 						p(" CP_DOWNLOAD Result for Cage ["+cageName+" ] is "+result);
// 					}

					//postAction operations
					int postActionSize=cageCmdObj.postActionObjNames.size();
					for(int i=0;i<postActionSize;i++)
					{
						boolean postActionResult=false;
						String objName=(String)cageCmdObj.postActionObjNames.get(i);
						String postAction=(String)cageCmdObj.postActions.get(i);
						String configResult=null;
						try{
							p(" Trying to execute postAction command for Cage : cageName ="+cageName+"; objName = "+objName+" ; operation = "+postAction);
							Properties configProps=new Properties();
							//Need not forward failure event to o3.
							configProps.setProperty("$correlated#","100");
							configProps.setProperty("$testMgmt#", "true");
							configResult=EMHConfigurationModule.getInstance().performConfigOperation(postAction,objName, configProps);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						if(configResult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configResult))
						{
							postActionResult=true;
						}
						p(" Result of postAction command for Cage : cageName ="+cageName+"; objName = "+objName+" ; operation = "+postAction+" ; RESULT = "+postActionResult);
						if(!postActionResult)
						{
							isResetRequired=true;
							p("Unable to perform postAction "+postAction+" for "+objName+". Hence trying to perform RESET for Cage ="+cageName);
							break;
						}
					}
				}
				if(isResetRequired)
				{
					String configResult=null;
					try{
						p("PreAction (or) PostAction failed for Cage. So, trying to execute RESET command for Cage : cageName ="+cageName);
						configResult=EMHConfigurationModule.getInstance().performConfigOperation(ConfigCommandConstants.RESET,cageName, new Properties());
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					if(configResult!=null && EMHCoreProvUtil.getInstance().checkConfigResult(configResult))
					{
						result=true;
					}
					p("PreAction (or) PostAction failed for Cage. So, result of RESET command for Cage : cageName ="+cageName+"; result = "+result);
				}
			}

			if(resultHash!=null)
			{
				if(result)
				{
					((Hashtable)resultHash.get("SUCCESS_LIST")).put(cageName,"Changes successfully updated in the database as well as Cage.");
				}
				else
				{
					((Hashtable)resultHash.get("SUCCESS_LIST")).put(cageName,"Changes successfully updated in the database, but failed on Cage. The changes will automatically take effect on next synchronization.");
				}
			}
		}
		p(" FINAL RESULT HASH = "+resultHash);
	}

	private void handleTopoEvent(EMHTopoNotificationEvent event) throws EMHException
	{
		if(event.getUpdateType() == EMHTopoNotificationEvent.ADD_RELATION ||
				event.getUpdateType() == EMHTopoNotificationEvent.DELETE_RELATION){
			handleRelationShipEntries(event);
		}
		else if(event.isManagedObject()){
			p("Trying to handle ManagedObject Notification ");
			updateManagedObject(event);
		}else
		{
			p("Trying to handle final else part Notification ");
			updateDataObject(event);
		}
	}

	private void checkinLicense(ManagedObject newObj)
	{
// 		try{
//             String objName = newObj.getName();
// 			if(newObj.getType().equals(lmccdo))
// 			{
// 				EMHUtil.printOut("DELETED LMCCDO Name " + objName,Log.DEBUG);

// 				int revA = ((LMCCDO)newObj).getRevAFeatureLicense();
// 				if(revA == 1)
// 				{
// 					EMHUtil.printOut("Check in of RevA FR Flag called for the deleted LMCCDO " + objName,Log.DEBUG );
// 					EMHLicenseHandler.getInstance().checkInLicense(LicenseUtil.RevAFeatureFlag,objName);
// 				}
// 			}
// 			else if(newObj.getType().equals(sectoremh))
// 			{
// 				EMHUtil.printOut("DELETED SECTOR Name " + objName,Log.DEBUG);
// 				int rtd = ((Sector)newObj).getRTDEdgeSensingLicense();
// 				if(rtd == 1)
// 				{
// 					EMHUtil.printOut("Check in of RTD FR Flag called for the deleted Sector " + objName,Log.DEBUG );
// 					EMHLicenseHandler.getInstance().checkInLicense(LicenseUtil.RTDEdgeSensingFlag,objName);
// 				}
// 			}
// 		}catch(Exception ex){
// 			EMHUtil.printErr("Error occured while check in the license.", ex);
// 		}
	}

	public void updateManagedObject(EMHTopoNotificationEvent evt) throws EMHException
	{
		int updateType = evt.getUpdateType();
		ManagedObject newObj = (ManagedObject)evt.getNewObject();
		if(updateType == EMHTopoNotificationEvent.ADD_OBJECT){
			objectAdded(newObj.getName(),newObj.getType(),newObj.getProperties());
		}
		else if(updateType == EMHTopoNotificationEvent.DELETE_OBJECT){
			//delete operation returns the deleted object as old object.
			newObj = (ManagedObject)evt.getOldObject();
			checkinLicense(newObj);
			deleteObject(newObj.getName(), newObj.getType(), newObj.getProperties());
		}
		else if(updateType == EMHTopoNotificationEvent.UPDATE_OBJECT){
			Properties changedProps = evt.getModifiedPropertyKeys();
			if(changedProps.containsKey("mccdo1NodeId"))
			{
				((ArrayList)getCurrentObject(MCCDO_LIST)).add(newObj.getName());
			}
			else if(changedProps.containsKey("channelNo"))
			{
				((ArrayList)getCurrentObject(CARRIER_LIST)).add(newObj.getName());
			}
			else if( changedProps.containsKey("bandClass"))
			{
				((ArrayList)getCurrentObject(SECTOR_LIST)).add(newObj.getName());
			}
			modifyObject(newObj.getName(),newObj.getType(),changedProps,newObj.getProperties());
		}
	}

	public void updateDataObject(EMHTopoNotificationEvent evt) throws EMHException

    {
		int updateType = evt.getUpdateType();
		if(updateType == EMHTopoNotificationEvent.ADD_OBJECT){
			DataObject obj = (DataObject)evt.getNewObject();
			objectAdded(obj.getName(),obj.getType(),obj.getProperties());
		}
		else if(updateType == EMHTopoNotificationEvent.DELETE_OBJECT){
			DataObject obj = (DataObject)evt.getOldObject();
			deleteObject(obj.getName(), obj.getType(), obj.getProperties());
		}
		else if(updateType == EMHTopoNotificationEvent.UPDATE_OBJECT){
			DataObject obj = (DataObject)evt.getNewObject();
			Properties changedProps = evt.getModifiedPropertyKeys();
			modifyObject(obj.getName(),obj.getType(),changedProps,obj.getProperties());
		}
	}

	protected void objectAdded(String objName, String objType, Properties props) throws EMHException
	{
		if(nlcbObjTypes.contains(objType)){
			putCurrentObject(UPDATE_NLCB,"true");
		}
		EMHUtil.printOut("Sujit Object Name : "+objName, Log.DEBUG);	
		EMHUtil.printOut("Sujit Object Type : "+objType, Log.DEBUG);	

		ObjectDetails objDetails = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		if(objDetails!=null){
			if((objDetails.isEntryNeededInNECF() || objDetails.isAltDeliveryNeeded()) && checkISValidEntry(objType , objName)){
				String neName = null;

				if(objDetails.isManagedObject()){
					neName = props.getProperty("neName");
				}else{
					neName = props.getProperty("MOName");
				}
				long entityIdentifier = Long.parseLong(props.getProperty("entityIdentifier","0"));
				Properties requiredProps = new Properties();
				String attr[] = objDetails.getCFAttribNames();
				if(attr!=null && attr.length>0){
					for(int attrId=0;attrId<attr.length;attrId++){
						AttribDetails attr1 = objDetails.getAttribDetails(attr[attrId]);
						int instance = objDetails.getInstanceNoAcrossParents(entityIdentifier, attr1.getContainerLevel());
						String value = props.getProperty(attr1.getObjectKey(),attr1.getDefaultValue(instance));
						if(value != null)requiredProps.put(attr1.getName(),value);
					}
				}
				if(objDetails.isEntryNeededInNECF())
				{
					Vector cageVec = getAffectedCages(neName, objType, props,true);
					if(cageVec!=null && cageVec.size()>0){
						for (int i = 0; i < cageVec.size(); i++) {
							String cageName = cageVec.elementAt(i).toString() + "/" + DeviceConstants.CAGE_TYPE + "-1";
							if(NECFUtility.getInstance().isEntryExists(cageName, NECFUtility.NECJ))
							{
								int entryNo = NECBDataGeneration.getInstance().addCRTEntry(cageName, objName, requiredProps, NECFUtility.NECJ);
								CageCmdObj cageCmdObj=getCageCmdObj(cageName);
								if(cageCmdObj.isResetRequired)
								{
									continue;
								}
								if(!cageCmdObj.isBulkDownloadCommitRequired)
								{
									checkAndAddCageConfigCmdXml(ADD_OBJECT,objName,requiredProps,objDetails,cageCmdObj,entryNo);
								}
								EMHUtil.printOut(objName+" NECJ CRT Entry added for the cage :"+cageName,Log.DEBUG);
							}
						}//end of for adding NECJ entries for cages
					}
				}//end of NECJ needed check
				if(objDetails.isAltDeliveryNeeded()){
					Vector cageVec = getAffectedCages(neName, objType, props,false);
					if(cageVec!=null && cageVec.size()>0){
						for (int i = 0; i < cageVec.size(); i++) {
							String cageName = cageVec.elementAt(i).toString();

							String baseName = objDetails.getAltDelivery();
							if(NECFUtility.getInstance().isEntryExists(cageName, "CPCJ_"+baseName)){
								NECBDataGeneration.getInstance().addCRTEntry(cageName, objName, requiredProps, "CPCJ_" + baseName);
								//setting the command to be executed
								CageCmdObj cageCmdObj=getCageCmdObj(cageName);
								cageCmdObj.isCPDownLoadRequired = true;
								EMHUtil.printOut(objName+" CPCJ CRT Entry added for the cage :"+cageName,Log.DEBUG);
							}
						}//end of for adding CPCJ entires for cages
					}
				}//end of CPCJ needed check
			}
		}else{
			EMHUtil.printOut("Unknown type received : "+objType, Log.DEBUG);
		}
	}

	protected void modifyObject(String objName, String objType, Properties changedProps,Properties newObjProps)throws EMHException
	{
		if(!((Hashtable)localThVsObjHash.get(Thread.currentThread())).containsKey(UPDATE_NLCB))
		{
            EMHUtil.printOut(" UPDATE_NLCB ",Log.SUMMARY);
			if(objType.equals(DeviceTypeConstants.MCCDO)){
				if(changedProps.containsKey("mccdo1NodeId") || changedProps.containsKey("revAFRFlag")){
					putCurrentObject(UPDATE_NLCB,"true");
				}
			}
			else if(objType.equals(DeviceTypeConstants.lmccdo)){
				if(changedProps.containsKey("bscIndex")){
					putCurrentObject(UPDATE_NLCB,"true");
				}
			}
			else if(objType.equals(DeviceTypeConstants.lmccdo)){
				if(changedProps.containsKey("dataAddress")){
					putCurrentObject(UPDATE_NLCB,"true");
				}
			}
			else if(nlcbObjTypes.contains(objType))
			{
				putCurrentObject(UPDATE_NLCB,"true");
			}
		}

		String newSystemName = "";
		p(" modifyObject Called : objName = "+objName+"; objType ="+objType+"; changedProps="+changedProps+"; newObjProps ="+newObjProps);

		if (objName.indexOf("mpSystemConfig-1") != -1) {
		    p("***** mpSystemConfig-1 object modification *****");
		    newSystemName = changedProps.getProperty("systemName");
		    p("New system name = " + newSystemName);

		    String path = File.separator + "shared" + File.separator + "ems" + File.separator;
		    FileWriter fw;
		    try {
			fw = new FileWriter(path+"title-ems1");
			BufferedWriter out = new BufferedWriter(fw);
			out.write(newSystemName);
			out.close();
			fw = new FileWriter(path+"title-ems2");
			out = new BufferedWriter(fw);
			out.write(newSystemName);
			out.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
		ObjectDetails objDetails = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		if(objDetails!=null){
		    if((objDetails.isEntryNeededInNECF() || objDetails.isAltDeliveryNeeded())  && checkISValidEntry(objType , objName)){
				String neName = null;
				if(objDetails.isManagedObject()){
					neName = newObjProps.getProperty("neName");
				}
				else{
					neName = newObjProps.getProperty("MOName");
				}
				Properties requiredProps = new Properties();
				String attr[] = objDetails.getCFAttribNames();
				if(attr!=null && attr.length>0){
					for(int attrId=0;attrId<attr.length;attrId++){
						AttribDetails attr1 = objDetails.getAttribDetails(attr[attrId]);
						if(changedProps.containsKey(attr1.getObjectKey())){
							String value = changedProps.getProperty(attr1.getObjectKey());
							if(value != null)requiredProps.put(attr1.getName(),value);
						}
					}
				}//end of if(attr!=null && attr.length>0)
				if(requiredProps.size()>0){
					if(objDetails.isEntryNeededInNECF())
					{
						Vector cageVec = getAffectedCages(neName, objType, newObjProps,true);
						if(cageVec!=null && cageVec.size()>0){
							for (int i = 0; i < cageVec.size(); i++) {
								String cageName = cageVec.elementAt(i).toString() + "/" + DeviceConstants.CAGE_TYPE + "-1";
								if(NECFUtility.getInstance().isEntryExists(cageName, NECFUtility.NECJ))
								{
									CageCmdObj cageCmdObj=getCageCmdObj(cageName);
									Hashtable cageVsObjNameList=(Hashtable)getCurrentObject(CAGE_VS_NECJ_OBJ_NAME_LIST);
									Hashtable cageVsPropsList=(Hashtable)getCurrentObject(CAGE_VS_NECJ_PROPS_LIST);

									ArrayList objNameList=(ArrayList)cageVsObjNameList.get(cageName);
									if(objNameList==null)
									{
										objNameList=new ArrayList();
										cageVsObjNameList.put(cageName,objNameList);
									}
									objNameList.add(objName);

									ArrayList propsList=(ArrayList)cageVsPropsList.get(cageName);
									if(propsList==null)
									{
										propsList=new ArrayList();
										cageVsPropsList.put(cageName,propsList);
									}
									propsList.add(requiredProps);


									EMHUtil.printOut(objName+" NECJ MOD Entry added in the cache for the cage :"+cageName,Log.DEBUG);
									if(cageCmdObj.isResetRequired)
									{
										continue;
									}
									boolean isResetRequired=false;
									for(Enumeration enu=requiredProps.keys();enu.hasMoreElements();)
									{
										String chAttrName=(String)enu.nextElement();
										isResetRequired=LBSCAttributeEdtHandler.getInstance().isResetRequired(chAttrName);
										if(isResetRequired)
										{
											break;
										}
										String preAction=LBSCAttributeEdtHandler.getInstance().getPreAction(chAttrName,objType);
										if(preAction!=null)
										{
											cageCmdObj.setPreAction(objName, preAction);
										}
										String postAction=LBSCAttributeEdtHandler.getInstance().getPostAction(chAttrName,objType);
										if(postAction!=null)
										{
											cageCmdObj.setPostAction(objName, postAction);
										}
									}
									if(isResetRequired)
									{
										cageCmdObj.isResetRequired=true;
									}
									else
									{
										if(!cageCmdObj.isBulkDownloadCommitRequired)
										{
											checkAndAddCageConfigCmdXml(UPDATE_OBJECT,objName,requiredProps,objDetails,cageCmdObj,-1);
										}
									}
								}
								else
								{
									Hashtable resultHash=(Hashtable)resRequiredThVsResult.get(Thread.currentThread());
									if(resultHash!=null)
									{
										((Hashtable)resultHash.get("SUCCESS_LIST")).put(cageName,"Changes successfully updated in the database. Since the Cage is not yet initialized, the changes are not applied on Cage. The changes will automatically take effect on next synchronization.");
									}
								}
							}//end of for adding cages
						}
					}
					if(objDetails.isAltDeliveryNeeded()){
						if(changedProps.containsKey("QoSIDtoPortIndex") ||changedProps.containsKey("PID_QoSID") || changedProps.containsKey("PID_TrafficClass") || changedProps.containsKey("P1toAPPort"))
						{
							p(" modifyObject Called : PM Mappgin needs to be refreshed.Calling CoreDcUtil.getInstance().refreshPMDataMapping().");
							CoreDcUtil.getInstance().refreshPMDataMapping();
						}
						Vector cageVec = getAffectedCages(neName, objType, newObjProps,false);
						if(cageVec!=null && cageVec.size()>0){
							for (int i = 0; i < cageVec.size(); i++) {
								String cageName = cageVec.elementAt(i).toString();

								String baseName = objDetails.getAltDelivery();
								if(NECFUtility.getInstance().isEntryExists(cageName, "CPCJ_"+baseName)){
									NECBDataGeneration.getInstance().addModEntry(cageName, objName, requiredProps, "CPCJ_" + baseName);
									EMHUtil.printOut(objName+" CPCJ MOD Entry added for the cage :"+cageName,Log.DEBUG);
									CageCmdObj cageCmdObj=getCageCmdObj(cageName);
									if(cageCmdObj.isResetRequired)
									{
										continue;
									}
									boolean isResetRequired=false;
									for(Enumeration enu=requiredProps.keys();enu.hasMoreElements();)
									{
										String chAttrName=(String)enu.nextElement();
										isResetRequired=LBSCAttributeEdtHandler.getInstance().isResetRequired(chAttrName);
										if(isResetRequired)
										{
											break;
										}
										String preAction=LBSCAttributeEdtHandler.getInstance().getPreAction(chAttrName,objType);
										if(preAction!=null)
										{
											cageCmdObj.setPreAction(objName, preAction);
										}
										String postAction=LBSCAttributeEdtHandler.getInstance().getPostAction(chAttrName,objType);
										if(postAction!=null)
										{
											cageCmdObj.setPostAction(objName, postAction);
										}
									}
									if(isResetRequired)
									{
										cageCmdObj.isResetRequired=true;
									}
									else
									{
										cageCmdObj.isCPDownLoadRequired=true;
									}
								}
								else
								{
									Hashtable resultHash=(Hashtable)resRequiredThVsResult.get(Thread.currentThread());
									if(resultHash !=null)
									{
										((Hashtable)resultHash.get("SUCCESS_LIST")).put(cageName,"Changes successfully updated in the database. Since the Cage is not yet initialized, the changes are not applied on Cage. The changes will automatically take effect on next synchronization.");
									}
								}
							}//end of for adding cages
						}
					}
				}//end of if(requiredProps.size()>0)
			}
		}else{
			EMHUtil.printOut("Unknown type received : "+objType, Log.DEBUG);
		}
	}



	private void checkAndAddCageConfigCmdXml(int type,String objName,Properties requiredProps,ObjectDetails objDetails,CageCmdObj cageCmdObj,int entryNo)
	{
		// type can be ADD_OBJECT , MODIFY_OBJECT and DELETE_OBJECT
		//If it can be set single message set the value cageCmdObj.configXmlNode. otherwise set cageCmdObj.isBulkDownloadCommitRequired =true
		if(!cageCmdObj.isBulkDownloadCommitRequired && !cageCmdObj.isResetRequired){

			/*if(objDetails.getObjectType().equals(DeviceTypeConstants.calltracecontrol)){
				EMHUtil.printOut("CallTrace control object is handled seperately", Log.SUMMARY);
				return;
			}*/

			if(type == ADD_OBJECT)
			{
				if(cageCmdObj.attrList.size() > 0 || objDetails.getRowStatusOID()== null){
					cageCmdObj.setBulkDownloadCommitRequired(true);
					return;
				}else{
					String indexStr = getIndexString(objName, objDetails.getObjectType());

					Attribute rowStatus = new SnmpAttribute(objDetails.getRowStatusOID()+indexStr,"rowStatus",
							getSNMPDataType(ObjectDetailsConstants.INTEGER),"1");
					cageCmdObj.attrList.addElement(rowStatus);
					for(Enumeration e = requiredProps.keys();e.hasMoreElements();){
						String attrName = e.nextElement().toString();
						AttribDetails ad = objDetails.getAttribDetails(attrName);
						if(ad.getAttributeOID()!=null)
						{
							Attribute colAttr = new SnmpAttribute(ad.getAttributeOID()+indexStr,attrName,
									getSNMPDataType(ad.getDataType()),requiredProps.getProperty(attrName));
							cageCmdObj.attrList.addElement(colAttr);
						}
					}
					cageCmdObj.attrList.addElement(getNecjEntryAttribute(entryNo));
				}
			}
			else if(type == DELETE_OBJECT)
			{
				if(cageCmdObj.attrList.size()>0){
					cageCmdObj.setBulkDownloadCommitRequired(true);
					return;
				}else{
					String indexStr = getIndexString(objName, objDetails.getObjectType());
					Attribute rowStatus = new SnmpAttribute(objDetails.getRowStatusOID()+indexStr,"rowStatus",
							getSNMPDataType(ObjectDetailsConstants.INTEGER),"2");
					cageCmdObj.attrList.addElement(rowStatus);
					cageCmdObj.attrList.addElement(getNecjEntryAttribute(entryNo));
				}
			}else if(type == UPDATE_OBJECT)
			{
				if(cageCmdObj.attrList.size()>0 && !cageCmdObj.onlyModList){
					cageCmdObj.setBulkDownloadCommitRequired(true);
					return;
				}else{
					String indexStr = getIndexString(objName, objDetails.getObjectType());
					for(Enumeration e = requiredProps.keys();e.hasMoreElements();){
						String attrName = e.nextElement().toString();
						AttribDetails ad = objDetails.getAttribDetails(attrName);
						if(ad.getAttributeOID()!=null)
						{
							Attribute colAttr = new SnmpAttribute(ad.getAttributeOID()+indexStr,attrName,
									getSNMPDataType(ad.getDataType()),requiredProps.getProperty(attrName));
							cageCmdObj.attrList.addElement(colAttr);
							if(cageCmdObj.attrList.size() > MAX_ATTRIBUTE_COUNT_FOR_SINGLE_SET)
							{
								cageCmdObj.setBulkDownloadCommitRequired(true);
								break;
							}
						}
                        else
                        {
                            cageCmdObj.setBulkDownloadCommitRequired(true);
                            break;
                        }
					}
                    cageCmdObj.attrList.addElement(getNecjEntryAttribute(entryNo));
					cageCmdObj.onlyModList = true;
				}
			}else
			{
				p("unknown type received so marking it as bulk config download");
				cageCmdObj.setBulkDownloadCommitRequired(true);
			}
		}
	}


	private Attribute getNecjEntryAttribute(int entryNo)
	{
		return (new SnmpAttribute(journelEntryOid,"cmpCjEntryNumberIn",SnmpAPI.INTEGER,entryNo+""));
	}


	byte getSNMPDataType(int dataType)
	{
		if(dataType==ObjectDetailsConstants.INTEGER){
			return SnmpAPI.INTEGER;
		}
		return SnmpAPI.STRING;
	}

	String getIndexString(String objName,String type)
	{
		String indexStr = "." + String.valueOf(ObjectNamingUtility.getEntityTypeForObjType(type, ObjectNamingUtility.SB));
		Properties props = ObjectNamingUtility.getObjProps(objName, ObjectNamingUtility.SB);
		String sbName = props.getProperty("name",objName);
		int[] ids = ObjectNamingUtility.getIdsFromOrl(sbName, type);
		indexStr += "."+ ObjectNamingUtility.getEntityID(new int[]{ids[0],ids[1]});
		indexStr += "."+ ObjectNamingUtility.getEntityID(new int[]{ids[2],ids[3]});
		return indexStr;

	}
	private void checkAndAddCageConfigCmdXmlForRelationship(boolean added,RelationObject rObj,ObjectDetails objDetails,CageCmdObj cageCmdObj, int entryNo)
	{
		if(!cageCmdObj.isBulkDownloadCommitRequired && !cageCmdObj.isResetRequired){
			if(cageCmdObj.attrList.size()>0)
			{
				cageCmdObj.setBulkDownloadCommitRequired(true);
				return;
			}
			String sourceName = rObj.getSource();
			String targetName = rObj.getTarget();
			String indexStr = getIndexString(sourceName, objDetails.getObjectType());
			Properties props = ObjectNamingUtility.getObjProps(targetName, ObjectNamingUtility.SB);
			String tgtType = props.getProperty("type");
			int ids[] = ObjectNamingUtility.getIdsFromOrl(props.getProperty("name",targetName), tgtType);

			String oid[] = null ,val[]= null, label[] = null;
			byte dtype[] = null;
			if(rObj.getRelationship() == DeviceTypeConstants.member_of_protection_group)
			{
				oid = new String[]{".1.3.6.1.4.1.161.2052.1.16.1.4.1.9"+indexStr,
						".1.3.6.1.4.1.161.2052.1.16.1.4.1.6"+indexStr,
						".1.3.6.1.4.1.161.2052.1.16.1.4.1.7"+indexStr,
				".1.3.6.1.4.1.161.2052.1.16.1.4.1.8"+indexStr};
				dtype = new byte[]{SnmpAPI.INTEGER,SnmpAPI.UNSIGNED32,SnmpAPI.UNSIGNED32,SnmpAPI.UNSIGNED32};
				label = new String[]{"rowsatus","tgtType","tgid1","tgid2"};
				val = new String[]{added?"1":"2",
						String.valueOf(ObjectNamingUtility.getEntityTypeForObjType(tgtType, ObjectNamingUtility.SB)),
						String.valueOf(ObjectNamingUtility.getEntityID(new int[]{ids[0],ids[1]})),
						String.valueOf(ObjectNamingUtility.getEntityID(new int[]{ids[2],ids[3]}))};

			}
			else if(rObj.getRelationship() == DeviceTypeConstants.potential_mapping)
			{
				String tgtIndexStr = getIndexString(targetName,tgtType);
				oid = new String[]{".1.3.6.1.4.1.161.2052.1.16.1.4.1.8"+indexStr+tgtIndexStr,
				".1.3.6.1.4.1.161.2052.1.16.1.4.1.7"+indexStr+tgtIndexStr};
				label = new String[]{"rowsatus","mappingPriority"};
				dtype = new byte[]{SnmpAPI.INTEGER,SnmpAPI.UNSIGNED32};
				val = new String[]{added?"1":"2",//row status
						String.valueOf(rObj.getMappingPriority())};
			}
			else if(rObj.getRelationship() == DeviceTypeConstants.dependency)
			{
				String tgtIndexStr = getIndexString(targetName,tgtType);
				oid = new String[]{".1.3.6.1.4.1.161.2052.1.7.1.1.7"+	indexStr+tgtIndexStr};
				dtype = new byte[]{SnmpAPI.INTEGER};
				label = new String[]{"rowsatus"};
				val = new String[]{added?"1":"2"};//row status
			}else
			{
				p("Unknow type received so initiated bulk config upload.");
				cageCmdObj.setBulkDownloadCommitRequired(true);
			}
			if(oid !=null && dtype!=null && val!=null && label!=null){
				for(int i=0;i<oid.length;i++){
					Attribute attr = new SnmpAttribute(oid[i],label[i],dtype[i],val[i]);
					cageCmdObj.attrList.addElement(attr);
				}
			}
		}
	}


	private Vector getAffectedCages(String neName, String objType, Properties objProps,boolean isPlatforDelivery){
		Vector cageVec = new Vector();
		if(objType.equals(lapcrg) && isPlatforDelivery){
			cageVec.addElement(CageUtil.getInstance().getCageNameForLapcRG(objProps, ACT));
			cageVec.addElement(CageUtil.getInstance().getCageNameForLapcRG(objProps, SBY));
		}else if(objType.equals(apcrg) && isPlatforDelivery){
			cageVec.addElement(CageUtil.getInstance().getCageNameForAPCRG(objProps, ACT));
			cageVec.addElement(CageUtil.getInstance().getCageNameForAPCRG(objProps, SBY));
		}else if(objType.equals(apc)&& isPlatforDelivery){
			cageVec.addElement(CageUtil.getInstance().getCageNameForAPCRG(objProps.getProperty("parentKey"), ACT));
			cageVec.addElement(CageUtil.getInstance().getCageNameForAPCRG(objProps.getProperty("parentKey"), SBY));
		}
		else if(neName == null || neName.equals(EMHCoreTopoMgr.LBSCDO_NAME)){
			//assumption here is neName will be null or ne name will be LBSCDO Name if it is a CP objects or a common object for all the shelfs.
			cageVec = getAllAvailableCageNames();
			//cageVec = CoreDBUtil.getInstance().getAllActiveCageNames();
		}else
		{
			cageVec.addElement(neName);
		}
		return cageVec;
	}


	private CageCmdObj getCageCmdObj(String cageName) throws EMHException
	{
		Hashtable cageVsCmd=(Hashtable)getCurrentObject(CAGE_VS_CMD);
		CageCmdObj cageCmdObj=(CageCmdObj)cageVsCmd.get(cageName);
		if(cageCmdObj==null)
		{
			cageCmdObj=new CageCmdObj(cageName);
			String version=NECBDataGeneration.getInstance().getCurrentFileVersionFromDB(cageName, NECFUtility.NECJ);
			if(!EMHUtil.getInstance().isNull(version))
			{
				//cageCmdObj.startVersion=(Integer.parseInt(version)+1); // Increment Start Version
				cageCmdObj.startVersion=(Integer.parseInt(version)); // Don't Increment Start Version
			}
			else
			{
				throw new EMHException("Something wrong in getting File NECJ version from DB ");
			}
			try{
				NECFUtility.getInstance().lock(cageName);
			}catch(EMHException e)
			{
				e.printStackTrace();
				throw e;
			}
			cageVsCmd.put(cageName,cageCmdObj);
		}
		return cageCmdObj;
	}

	private Vector getAllAvailableCageNames(){
		Vector cageVec = new Vector();
		Properties props = new Properties();
		props.put("type",cage);
		try {
			cageVec = CoreUtil.getInstance().getTopoAPI().getObjectNamesWithProps(props);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NmsStorageException e) {
			e.printStackTrace();
		} catch (UserTransactionException e) {
			e.printStackTrace();
		}
		return cageVec;
	}

	protected void deleteObject(String objName,String objType, Properties objProps) throws EMHException
	{
		ObjectDetails objDetails = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		NECFUtility necf = NECFUtility.getInstance();

		if(nlcbObjTypes.contains(objType)){
			putCurrentObject(UPDATE_NLCB,"true");
		}

		if(objDetails!=null){
			if(objType.equals(cage))
			{
				String cageName=objName;
				CageCmdObj cageCmdObj=getCageCmdObj(cageName);
				cageCmdObj.cageDeleted = true;
				//END rameshj
				if(necf.isEntryExists(objName,NECFUtility.NECJ))necf.deleteNECFEntries(objName, NECFUtility.NECJ);
				if(necf.isEntryExists(objName,NECFUtility.NECB))necf.deleteNECFEntries(objName, NECFUtility.NECB);
				Object bNames[] = ObjectDetailsHandler.getInstance().getBaseList();
				for(int i=0;i<bNames.length;i++){
					String baseName = String.valueOf(bNames[i]);
					if(necf.isEntryExists(objName,"CPCJ_"+baseName))necf.deleteNECFEntries(objName, "CPCJ_"+baseName);
				}
				return;
			}
			else if((objDetails.isEntryNeededInNECF() || objDetails.isAltDeliveryNeeded())  && checkISValidEntry(objType , objName))
			{
				String neName = null;
				if(objDetails.isManagedObject()){
					neName = objProps.getProperty("neName");
				}
				else{
					neName = objProps.getProperty("MOName");
				}
				String sbName =null;
				if(objDetails.hasSBName())
				{
					sbName = objProps.getProperty("sbName");
				}
				if(objDetails.isEntryNeededInNECF())
				{
					Vector cageVec = getAffectedCages(neName, objType, objProps,true);
					if(cageVec!=null && cageVec.size()>0){
						for (int i = 0; i < cageVec.size(); i++) {
							String cageName = cageVec.elementAt(i).toString() + "/" + DeviceConstants.CAGE_TYPE + "-1";
							if(NECFUtility.getInstance().isEntryExists(cageName, NECFUtility.NECJ))
							{
								int entryNo = NECBDataGeneration.getInstance().addDeleteEntry(cageName, objName, NECFUtility.NECJ,sbName);
								EMHUtil.printOut(objName+" NECJ DEL Entry added for the cage :"+cageName,Log.DEBUG);
								CageCmdObj cageCmdObj=getCageCmdObj(cageName);
								if(cageCmdObj.isResetRequired || cageCmdObj.cageDeleted){
									continue;
								}else if(!cageCmdObj.isBulkDownloadCommitRequired)
								{
									checkAndAddCageConfigCmdXml(DELETE_OBJECT,objName,null,objDetails,cageCmdObj,entryNo);
								}

							}
						}//end of for adding delete NECJ entries for cages
					}
				}
				if(objDetails.isAltDeliveryNeeded()){
					Vector cageVec = getAffectedCages(neName, objType, objProps,false);
					if(cageVec!=null && cageVec.size()>0){
						for (int i = 0; i < cageVec.size(); i++) {
							String cageName = cageVec.elementAt(i).toString();

							String baseName = objDetails.getAltDelivery();
							if(NECFUtility.getInstance().isEntryExists(cageName, "CPCJ_"+baseName)){
								NECBDataGeneration.getInstance().addDeleteEntry(cageName, objName, "CPCJ_"+baseName,sbName);
								EMHUtil.printOut(objName+" CPCJ DEL Entry added for the cage :"+cageName,Log.DEBUG);
								CageCmdObj cageCmdObj=getCageCmdObj(cageName);
								if(cageCmdObj.isResetRequired)
								{
									continue;
								}
								else
								{
									cageCmdObj.isCPDownLoadRequired=true;
								}
							}
						}//end of for adding delete CPCJ entries for cages
					}
				}
			}
		}
	}


	private void handleRelationShipEntries(EMHTopoNotificationEvent event) throws EMHException
	{
		RelationObject robj = null;
		boolean added = true;
		if(event.getUpdateType() == EMHTopoNotificationEvent.ADD_RELATION){
			robj = (RelationObject)event.getNewObject();
		}else
		{
			added = false;
			robj = (RelationObject)event.getOldObject();
		}
		String relationType = robj.getRelationship();
		if(nlcbObjTypes.contains(relationType)){
			putCurrentObject(UPDATE_NLCB,"true");
		}
		if(!relationType.equals(active_mapping)){
			//determine the shelfs that need to be sent this informations. This has to be based on the source object types
			ManagedObject srcObj = (ManagedObject)event.getSourceObject(); //TODO need to determine whether we need to add lmmcdo relation entries here.
			try {
				if(srcObj == null)
					srcObj = CoreUtil.getInstance().getTopoAPI().getByName(robj.getSource());
			} catch (Exception e) {
				throw new EMHException("Unable to get the ManagedObject :"+robj.getSource(),e);
			}
			if(srcObj == null){
				EMHUtil.printOut("Unable to get the source object while trying to add necf entries for :"+robj.getName(),Log.SUMMARY);
				return;
			}
			Properties objProps = srcObj.getProperties();
			ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(srcObj.getType());
			if(od!=null){
				String neName = null;
				if(od.isEntryNeededInNECF())
				{
					if(od.isManagedObject()){
						neName = objProps.getProperty("neName");
					}
					else{
						neName = objProps.getProperty("MOName");
					}
					Vector cageVec = getAffectedCages(neName, srcObj.getType(), objProps,true);
					if(cageVec!=null && cageVec.size()>0){
						for (int i = 0; i < cageVec.size(); i++) {
							String cageName = cageVec.elementAt(i).toString()+ "/" + DeviceConstants.CAGE_TYPE + "-1";
							if(NECFUtility.getInstance().isEntryExists(cageName, NECFUtility.NECJ))
							{
								int entryNo = NECBDataGeneration.getInstance().addRELEntry(cageName, robj.getSource(), robj.getTarget(), relationType,
										added, null, NECFUtility.NECJ);
								EMHUtil.printOut(relationType+" NECJ REL Entry added for the cage :"+cageName,Log.DEBUG);

								CageCmdObj cageCmdObj=getCageCmdObj(cageName);
								if(cageCmdObj.isResetRequired || cageCmdObj.cageDeleted)
								{
									continue;
								}else if(!cageCmdObj.isBulkDownloadCommitRequired){
									checkAndAddCageConfigCmdXmlForRelationship(added,robj,od,cageCmdObj,entryNo);
								}
							}
						}//end of for adding rel NECJ entries for cages
					}else{
						EMHUtil.printOut("unable to get the cagenames for updating necf for the object :"+srcObj.getName(), Log.DEBUG);
					}
				}else{
					EMHUtil.printOut("No NECF entry needed for this source object :"+srcObj.getName(), Log.DEBUG);
				}
			}
			if(relationType.equals(data_grouping))
			{
				ManagedObject targetObj=(ManagedObject)event.getTargetObject();
				if(targetObj==null)
				{
					EMHUtil.printErr("unable to get the target Object for relationship change source Object :"+srcObj.getName(), Log.DEBUG);
					return;
				}
				if(srcObj.getType().equals(lmccdo) && targetObj.getType().equals(apc))
				{
					((ArrayList)getCurrentObject(MODIFIED_APC_LIST)).add(targetObj.getName());
				}
			}
		}
	}


	private Object getCurrentObject(String key)
	{
		Hashtable objHash=(Hashtable)localThVsObjHash.get(Thread.currentThread());
		return objHash.get(key);
	}

	private void putCurrentObject(String key,String value)
	{
		((Hashtable)localThVsObjHash.get(Thread.currentThread())).put(key,value);
	}

	private void initCurrentThread()
	{
		//init object
		if(isInMultipleTransaction() && localThVsObjHash.containsKey(Thread.currentThread()))
		{
			return;
		}
		Hashtable objHash=new Hashtable();
		objHash.put(CAGE_VS_CMD, new Hashtable());
		objHash.put(MCCDO_LIST, new ArrayList());
		objHash.put(CARRIER_LIST, new ArrayList());
		objHash.put(SECTOR_LIST, new ArrayList());
		objHash.put(MODIFIED_APC_LIST, new ArrayList());
		objHash.put(CAGE_VS_NECJ_OBJ_NAME_LIST, new Hashtable());
		objHash.put(CAGE_VS_NECJ_PROPS_LIST, new Hashtable());
		localThVsObjHash.put(Thread.currentThread(), objHash);
	}

	private void cleanup()
	{
		localThVsObjHash.remove(Thread.currentThread());
		multiTransThreadList.remove(Thread.currentThread());
		NECFUtility.getInstance().clearCurrentThread();
	}

	private String convertDBQueryINStr(ArrayList list)
	{
		String criteria="";
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			criteria+="'"+((String)list.get(i))+"'";
			if(i != (size -1) )
			{
				criteria+=",";
			}
		}
		return criteria;
	}


	private class CageCmdObj
	{
		String cageName="";

		boolean isResetRequired=false;

		boolean isCPDownLoadRequired=false;

		boolean isBulkDownloadCommitRequired=false;

		boolean cageDeleted = false;

		int startVersion=-1;

		Vector attrList = new Vector(5);

		boolean onlyModList = false;

		ArrayList preActionObjNames=new ArrayList();

		ArrayList postActionObjNames=new ArrayList();

		ArrayList preActions=new ArrayList();

		ArrayList postActions=new ArrayList();

		CageCmdObj(String cName)
		{
			cageName=cName;
		}


		public void setBulkDownloadCommitRequired(boolean req){
			isBulkDownloadCommitRequired = req;
			if(isBulkDownloadCommitRequired){
				attrList.clear();
			}
		}

		public void setPreAction(String objName,String action)
		{
			int objIndex=preActionObjNames.indexOf(objName);
			int actIndex=preActions.indexOf(action);
			if(objIndex ==-1 || (objIndex!=actIndex))
			{
				preActionObjNames.add(objName);
				preActions.add(action);
			}
		}

		public void setPostAction(String objName,String action)
		{
			int objIndex=postActionObjNames.indexOf(objName);
			int actIndex=postActions.indexOf(action);
			if(objIndex ==-1 || (objIndex!=actIndex))
			{
				postActionObjNames.add(objName);
				postActions.add(action);
			}
		}



	}
	
	private boolean checkISValidEntry( String objType , String objName)
    {
   //  	if(objType.equals(DeviceTypeConstants.sectorNeighborBsc) || objType.equals(DeviceTypeConstants.neighboremh))
//     	{
//     		int instanceid =  ObjectNamingUtility.getObjectInstance(objName, objType);
//     		if(instanceid > 31){
//     			p("The Object "+objName +" is not being sent to device as it exceeds the required instance count");
//     			return false;
//     		}
//     	}else if(objType.equals(DeviceTypeConstants.neighborApcListBsc) || objType.equals(DeviceTypeConstants.apcneighboremh))
//     	{
//     		int instanceid =  ObjectNamingUtility.getObjectInstance(objName, objType);
//     		if(instanceid > 20){
//     			p("The Object "+objName +" is not being sent to device as it exceeds the required instance count");
//     			return false; 
//     		}
//     	}
    	return true;
    }

	private void p(String msg)
	{
		System.err.println("JR : LBSCRecentChangeHandler -->  : "+msg);
	}
}

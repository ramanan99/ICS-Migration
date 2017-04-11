//$Id: EMHCoreTopoMgr.java,v 1.172.2.1 2007/07/04 14:45:20 murugesan Exp $
/**
 *
 */
package com.motorola.emh.core.inventory;

//java imports
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.TimeZone;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;


//nms imports
import com.adventnet.management.log.Log;
import com.adventnet.management.transaction.TransactionAPI;
import com.adventnet.nms.store.UserStorageAPI;
import com.adventnet.nms.topodb.ManagedObject;
import com.adventnet.nms.topodb.TopoAPI;
import com.adventnet.nms.util.NmsUtil;
import com.adventnet.nms.store.relational.RelationalAPI;
import com.adventnet.nms.topodb.DBServer;
import com.adventnet.nms.severity.SeverityInfo;

//common imports
import com.motorola.emh.common.exception.EMHException;
import com.motorola.emh.common.exception.EMHInventoryException;
import com.motorola.emh.common.log.EMHLogManager;
import com.motorola.emh.common.util.EMHUtil;
import com.motorola.emh.common.util.LockObjectUtil;
import com.motorola.emh.common.util.ObjectNamingUtility;
import com.motorola.emh.common.util.VirtualHostUtil;

//core imports
import com.motorola.emh.core.common.IPCalculator;
import com.motorola.emh.core.constants.CoreConstants;
import com.motorola.emh.core.inventory.utils.BladeProvisioned;
import com.motorola.emh.core.inventory.utils.CageDetails;
import com.motorola.emh.core.inventory.utils.CageTemplateHandler;
import com.motorola.emh.core.inventory.utils.PGDetails;
import com.motorola.emh.core.inventory.utils.RGDetails;
import com.motorola.emh.core.modeling.DataObject;
import com.motorola.emh.core.modeling.LMCCDO;
import com.motorola.emh.core.modeling.RelationObject;
import com.motorola.emh.core.modeling.SCARG;
import com.motorola.emh.core.modeling.APCRG;
import com.motorola.emh.core.modeling.LAPCRG;
import com.motorola.emh.core.modeling.EMHMediation;
import com.motorola.emh.core.necf.NLCBDataGeneration;
import com.motorola.emh.core.statuspoller.StatusPollInitializer;
import com.motorola.emh.core.util.AttribDetails;
import com.motorola.emh.core.util.CoreDBUtil;
import com.motorola.emh.core.util.CoreUtil;
import com.motorola.emh.core.util.CageUtil;
import com.motorola.emh.core.util.DeviceTypeConstants;
import com.motorola.emh.core.util.ObjectDetails;
import com.motorola.emh.core.util.ObjectDetailsHandler;
import com.motorola.emh.core.util.StateConstantsUtil;
import com.motorola.emh.core.perf.CoreDcUtil;
import com.motorola.emh.core.dst.TimeZoneParser;
import com.motorola.emh.core.dst.DSTHandler;
import com.motorola.emh.common.communication.fw.EMHCommunicationConstants;
import com.motorola.emh.common.communication.fw.EMHCommunicationData;
import com.motorola.emh.common.communication.fw.EMHCommunicationHandler;
import com.motorola.emh.common.exception.EMHCommunicationException;
import com.motorola.emh.core.config.EMHConfigurationModule;
import com.motorola.emh.core.config.EMHCoreProvUtil;
import com.motorola.emh.core.constants.ConfigCommandConstants;

import com.motorola.emh.core.necf.NECBDataGeneration;

/**
 * @author sarang
 *
 */
public class EMHCoreTopoMgr implements DeviceTypeConstants,CoreConstants{

	/** Singleton instance */
	private static EMHCoreTopoMgr mgr = null;

	//TopoAPI instance
	private TopoAPI topoApi=null;

	private UserStorageAPI userApi = null;

	//instance of RelationalAPI
	private RelationalAPI relapi = null;

	public static int controlChannelOffset = 0;

	public static int rabOffset = 0;


	public static EMHCoreTopoMgr getInstance(){

		if(mgr == null)mgr = new EMHCoreTopoMgr();
		return mgr;
	}

	final int cage_authIPSize = 16;
	final int cage_dataIPSize = 64;

	private int global_APC_ID = 0;
	private int global_SCA_ID = 0;
	private int global_TC_ID = 0;
	private Vector missing_APC_Ids = null;
	private Vector missing_TC_Ids = null;
	private Vector missing_SCA_Ids = null;

	private int global_channelPtn_id = 0;

	private static final int MAX_APC_COUNT = 200;
	private static final int MAX_TC_COUNT  = 127;
	private static final int MAX_SCA_COUNT = 32;

	private int emhCoreCount = 0;
	private int emhMedCount = 0;
	private Hashtable tempMap = new Hashtable();

	public static String LBSCDO_NAME = null;
	public static String HRPDA_NAME = null;

	private BulkNotifier notifier = null;

	private boolean toBeNotified = true;

	private Hashtable listenerTable = new Hashtable();
	
	static final int def_lock_timeout = 20 * 1000;
	
	//private constructor to create singleton instances and get the NMS API's
	private EMHCoreTopoMgr()
	{
		topoApi=CoreUtil.getInstance().getTopoAPI();
		userApi = CoreUtil.getInstance().getUserStorageAPI();
		notifier = new BulkNotifier();
		relapi = NmsUtil.relapi;
		(new EMHCoreTopoListener()).registerWithTopoAPI();
		initialize();
	}

	private void initialize()
	{
		String lbscdoquery = "SELECT NAME from ManagedObject where TYPE='"+lbscdo+"'";
		String hrpdaquery = "SELECT NAME from ManagedObject where TYPE='"+hrpda+"'";
		try {
			ArrayList list = CoreDBUtil.getInstance().getDataFromDB(lbscdoquery);
			if(list!=null && list.size()>0){
				Object obj[] = (Object[])list.get(0);
				if(obj!=null&&obj.length>0){
					LBSCDO_NAME = String.valueOf(obj[0]);
				}
			}
			list = CoreDBUtil.getInstance().getDataFromDB(hrpdaquery);
			if(list!=null && list.size()>0){
				Object obj[] = (Object[])list.get(0);
				if(obj!=null&&obj.length>0){
					HRPDA_NAME = String.valueOf(obj[0]);
				}
			}
		} catch (EMHException e) {
			e.printStackTrace();
		}

		/* VC: we don't need it unless using NeighborManagement functionality
		try{
			//Generating the NLCB File on the Startup of EMH so that it will be present in the Stand-By EMH upon Failover
			NLCBDataGeneration.getInstance().generateNLCBData();
		} catch (EMHException e) {
			e.printStackTrace();
		}
		*/

		/* VC: don't need it unless using addLBSCDOCage api 
		initializeObjectCounts();
		*/
	}

	private void initializeObjectCounts()
	{
		String coreQuery = "select count(*) from EMHCore";

		String mediationQuery = "select count(*) from EMHMediation";

		String apcQuery = "select max(LogicalElement.ENTITYIDENTIFIER & 65535) from LogicalElement,APC where APC.NAME=LogicalElement.NAME";

		String tcQuery = "select max(LogicalElement.ENTITYIDENTIFIER & 65535) from LogicalElement,TC where TC.NAME=LogicalElement.NAME";

		String scaQuery = "select max(LogicalElement.ENTITYIDENTIFIER & 65535) from LogicalElement,SCA where SCA.NAME=LogicalElement.NAME";

		String channelptnquery = "SELECT max(DataObject.ENTITYIDENTIFIER & 65535) from DataObject where TYPE='"+channelPtnListBsc+"'";

		String query[] = {coreQuery, mediationQuery, apcQuery, tcQuery, scaQuery,channelptnquery};

		int values[] = {0, 0, 0, 0, 0,0};

		for (int i = 0; i < query.length; i++){
			try{
				ArrayList list = CoreDBUtil.getInstance().getDataFromDB(query[i]);
				System.err.println("The list in the initialize "+list+" "+list.isEmpty());
				while(!list.isEmpty()){
					Object obj[] = (Object[])list.remove(0);
					System.err.println("The object "+obj[0]);
					if(obj[0]!=null){
						values[i] = Integer.parseInt(obj[0].toString());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		emhCoreCount = values[0]+1;
		emhMedCount = values[1]+1;
		global_APC_ID = values[2]+1;
		global_TC_ID = values[3]+1;
		global_SCA_ID = values[4]+1;
		global_channelPtn_id = values[5]+1;
		missing_APC_Ids = getMissingIdsFor(apc);
		missing_TC_Ids = getMissingIdsFor(tc);
		missing_SCA_Ids = getMissingIdsFor(sca);

		System.err.println("Initialize "+emhCoreCount+" "+emhMedCount+" "+global_SCA_ID+" "+global_APC_ID+" "+global_TC_ID);
		log("Missing ids for APC = "+missing_APC_Ids);
		log("Missing ids for TC  = "+missing_TC_Ids);
		log("Missing ids for SCA = "+missing_SCA_Ids);
	}

	protected void resetChannelPtnId()
	{
		String channelptnquery = "SELECT max(DataObject.ENTITYIDENTIFIER & 65535) from DataObject where TYPE='"+channelPtnListBsc+"'";
		int value = 0;
		try{
			ArrayList list = CoreDBUtil.getInstance().getDataFromDB(channelptnquery);
			System.err.println("The list in the initialize "+list+" "+list.isEmpty());
			while(!list.isEmpty()){
				Object obj[] = (Object[])list.remove(0);
				System.err.println("The object "+obj[0]);
				if(obj[0]!=null){
					value = Integer.parseInt(obj[0].toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		global_channelPtn_id = value + 1;
	}

	/******************** Start Generic Object related Inventory Methods ****************************/

	protected String addAnyObject(Properties objProps, Properties otherProps)throws EMHInventoryException
	{
		log("[addAnyObject] : adding object with props "+objProps);
		//log("[addAnyObject] : otherProps="+otherProps);

		//type,entityId,parentKey and any other specific attribuetes to be added in database should be in this properties
		String type = objProps.getProperty("type");
		long entityId = Long.parseLong(objProps.getProperty("entityIdentifier","1"));

		if(type == null){
			throw new EMHInventoryException("Error in adding Object: the type is null.");
		}

		ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(type);
		if(od == null){
			throw new EMHInventoryException("Error in adding Object: Unknown object type "+type);
		}
		if(otherProps == null) otherProps = new Properties();

		int ids[] = ObjectNamingUtility.getID(entityId);
		int instance = ids[3];

		//TODO need to check for Cardinality don't know how to check this also.
		if(type.equals(apc)){
			if(instance>MAX_APC_COUNT)
			{
				throw new EMHInventoryException("Cannot add APC-"+instance+". Maximum number of APCs ("+MAX_APC_COUNT+" per IP-BSC-DO) exceeded.");
			}
		}else if(type.equals(sca)){
			if(instance>MAX_SCA_COUNT)
			{
				throw new EMHInventoryException("Cannot add SCA-"+instance+". Maximum number of SCAs ("+MAX_SCA_COUNT+" per IP-BSC-DO) exceeded.");
			}
		}else if(type.equals(tc)){
			if(instance>MAX_TC_COUNT)
			{
				throw new EMHInventoryException("Cannot add TC-"+instance+". Maximum number of TCs ("+MAX_TC_COUNT+" per IP-BSC-DO) exceeded.");
			}
		}
		//Always this will be NB AEMI ID if  other wise it will take SB. This is done to reduce the
		// load in NB Interface to un-necessaryly keep the mapping.

		Properties dfProps = ObjectNamingUtility.getDbPropsForType(entityId, type);
		String objName = objProps.getProperty("name",dfProps.getProperty("name")); //This is to override the generated name by the given name
		String entityType = dfProps.getProperty("entityType");
		objProps.put("entityType",entityType);
		Properties objPropsLeftOvers = (Properties)objProps.clone();
		String displayName = ObjectNamingUtility.getDefaultString(type, ids);
		
		// remove basic props
		objPropsLeftOvers.remove("name");
		objPropsLeftOvers.remove("type");
		objPropsLeftOvers.remove("NENAME");
		objPropsLeftOvers.remove("entityType");
		objPropsLeftOvers.remove("parentKey");
		
		//checking for mandatory attributes are provided and populate default values if there is no value and
		// check validity of the configured values also done here.
		String attrs[] = od.getCFAttribNames();
		if(attrs!=null){
            log("[addAnyObject] : Found " + attrs.length + " Mandatory Attributes" );
			for(int i=0;i<attrs.length;i++){
				AttribDetails attr = od.getAttribDetails(attrs[i]);
				if(attr!=null){
					String key = attr.getObjectKey();
					String defaultValue = attr.getDefaultValue(od.getInstanceNoAcrossParents(entityId, attr.getContainerLevel()));
					String configValue = objProps.getProperty(key);
					objPropsLeftOvers.remove(key);
                    log("[addAnyObject] : Mandatory Attribute key=" + key + " defValue=" + defaultValue + " cfgValue=" + configValue );
					if(configValue==null && otherProps.containsKey(key))
					{
						configValue = otherProps.getProperty(key);
                        log("[addAnyObject] : Cfg was NULL - Attribute key=" + key + " defValue=" + defaultValue + " cfgValue=" + configValue );
						objProps.put(key,configValue);
					}
					if(defaultValue== null && configValue==null ){
						throw new EMHInventoryException("In "+objName+": No value given for the attribute "+attrs[i]+ " is not valid");
					}else if(configValue == null && defaultValue!=null){
						objProps.setProperty(key, defaultValue);
					}else if(configValue!=null && !attr.isValidValue(configValue)){
						System.err.println("attr "+configValue+" "+key+" "+defaultValue);
						throw new EMHInventoryException("In "+objName+": Given value for the attribute "+attrs[i]+ " is not valid");
					}
				}
			}
		}

		//add any internal properties that needs to be added to be added;
		String otherAttrs[] = od.getOtherAttribNames();
        log("[addAnyObject] : Found " + otherAttrs.length + " Other Attributes" );		if(otherAttrs!=null){
			for(int attrNo=0;attrNo<otherAttrs.length;attrNo++){
				AttribDetails attr = od.getAttribDetails(otherAttrs[attrNo]);
				if(attr!=null){
					String key = attr.getObjectKey();
					String configValue = objProps.getProperty(key,otherProps.getProperty(key));
					objPropsLeftOvers.remove(key);
                    log("[addAnyObject] : Other Attribute key=" + key + " cfgValue=" + configValue );
					if(configValue == null)
					{
						configValue = attr.getDefaultValue(od.getInstanceNoAcrossParents(entityId, attr.getContainerLevel()));
						if(configValue!=null)
						{
                            log("[addAnyObject] : Other Attribute key=" + key + " new cfgValue=" + configValue );
							objProps.put(key,configValue);
						}
					}else if(!attr.isValidValue(configValue)) //check whether the provisioned value is valid for other attributes also
					{
						System.err.println("attr ="+otherAttrs[attrNo]+" given value ="+configValue);
						throw new EMHInventoryException("In "+objName+": Given value for the attribute "+otherAttrs[attrNo]+ " is not valid");
					}
				}
			}
		}
		if(!objPropsLeftOvers.isEmpty())
		{
			System.err.println("WARNING: In "+objName+": Extra Attribute(s): "+objPropsLeftOvers.toString());
			// XXX Don't throw an error until the ObjectDetails aggregation issue has been resolved.
			// throw new EMHInventoryException("In "+objName+": Extra Attribute(s): "+objPropsLeftOvers.toString());
		}
		String className = od.getEmhClass();

		//getting initialStates.
		Properties stateProps = od.getInitialStates();
        System.err.println("className=" + className + " isMO=" + od.isManagedObject() );
		if(od.isManagedObject()){
			if(className == null || className.equals("")) className="com.adventnet.nms.topodb.ManagedObject";
			ManagedObject obj = (ManagedObject)getObjectFromClass(className);
			obj.setName(objName);
			objProps.setProperty("displayName", displayName);
			obj.setProperties(stateProps);
			obj.setProperties((Properties)objProps.clone());
            
			//TODO need to update the below props in the object constructor itsef or some other way. Since this list may grow.
			obj.setIsContainer(true);
			if(type.equals(DeviceTypeConstants.cage) && objName.endsWith("-1")) {
			    obj.setStatusPollEnabled(true);
			    obj.setTester("usertest");
			    obj.setUClass("com.motorola.emh.mediation.statuspoller.CageMedStatusPoller");
			    obj.setPollInterval(60);
			} else {
			    obj.setStatusPollEnabled(false);
			}

			try {
				
				if( obj.getParentKey() != null && topoApi.getByName(obj.getParentKey()) == null )
				{
					throw new EMHInventoryException("Unable to add Object "+objName+": Parent Object " + obj.getParentKey() + " does not exist");
				}
				
			    log("[addAnyObject] adding MO - with props: "+obj.getProperties());
				String resultString = topoApi.addObject(obj, false);
				log("[addAnyObject] result for adding MO - "+objName+" :"+resultString);
				if(!((resultString != null) && (resultString.trim().indexOf("Successfully Added to Database") != -1)))
				{
					throw new EMHInventoryException("Unable to add Object "+objName+":"+resultString);
				}

				if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectAddedNotification(obj,true));
			} catch (EMHInventoryException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new EMHInventoryException("Unable to add Object : "+objName+" Error :"+e.toString());
			}
		}else
		{
			if(className == null || className.equals("")) className="com.motorola.emh.core.modeling.DataObject";
			DataObject obj = (DataObject)getObjectFromClass(className);
			obj.setName(objName);
			if(objProps.containsKey("neName")){
				obj.setMOName(objProps.getProperty("neName"));
				objProps.remove("neName");
			}
			//removing entitytype
			objProps.remove("entityType");
			obj.setProperties(stateProps);
			obj.setProperties((Properties)objProps.clone());
			try {
				if(!userApi.addObject(obj, obj.getName()))
				{
					throw new EMHInventoryException("Unable to add Object :"+objName);
				}
				if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectAddedNotification(userApi.getObject(objName, od.getTableName()),false));
			} catch (EMHInventoryException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new EMHInventoryException("Unable to add Object : "+objName+" Error :"+e.toString());
			}
		}
		//log("[addAnyObject] : added object " +objName + " with props "+objProps);

		if(type.equals(DeviceTypeConstants.cage)|| type.equals(lbscdo)){
			//setting nename in operatordata;
			otherProps.put("neName",objName);
		}
		if(od.isRelationShipNeeded()){
			log("[addAnyObject] : adding relationship details");
			ArrayList relList = od.getRelationShipDetails();
			for(int i=0;i<relList.size();i++){
				String rels[] = (String[])relList.get(i);
				if(rels.length>=3 && rels[2].equals("true")){
					Vector tarObjVec = null;
					if(type.equals(pam)){
						int ins[] = ObjectNamingUtility.getID(entityId);
						String pgName = otherProps.getProperty("Row"+ins[3]);
						if (pgName != null){
							tarObjVec = new Vector();
							tarObjVec.addElement(pgName);
						}
					}
					else if(rels[1] != null && !rels[1].equals("")){
						tarObjVec= getAllObjectsForType(otherProps.getProperty("neName"), rels[1]);
					}
					if(tarObjVec == null)continue;
					for(int t=0;t<tarObjVec.size();t++){
						addRelationShip(rels[0], objName, tarObjVec.elementAt(t).toString(), otherProps.getProperty("neName"));
					}
				}
			}
		}
		objProps.remove("protecting");
		//removing this attribute since this is not valid to pass on the protecting attribute to the side provisioned and its children
		//log("[addAnyObject] : object added with props "+objProps);
		//if the object type is ssc add RTM object
		if (type.equals(ssc)){
			int slotNo = Integer.parseInt(objProps.getProperty("slotNo"));
			int cageNumber = Integer.parseInt(otherProps.getProperty("cageNumber"));
			addRTM(cageNumber, slotNo, objProps, ssc);
		}
		//adding side provisioned Objects
		Vector vec = ObjectDetailsHandler.getInstance().getSideProvisionedObjects(type);
		if(vec!=null){
			for(int i=0;i<vec.size();i++){
				String childObj = vec.elementAt(i).toString();
				log("[addAnyObject] : adding side provisioned Object :" +childObj);
				ObjectDetails childOd = ObjectDetailsHandler.getInstance().getObjectDetails(childObj);
				String parentType = childOd.getParentObject();
				int ins[] = childOd.getInstance(otherProps);
				for(int j = 0; j < ins.length; j++){
					Properties childProps = new Properties();
					childProps.setProperty("type", childObj);
					int childIds[] = new int[4];
					childIds[3] = ins[j] ;
					//log("instance for "+childObj+"="+childIds[3]);
					if(parentType!=null ){
						if(parentType.equals(type)){
							childIds[2] = ids[3];
							childIds[1] = ids[2];
							childIds[0] = ids[1];
							childProps.setProperty("parentKey", objName);
						}else if(otherProps.containsKey(parentType)){
							// TODO needs to change this shabby assumption.
							// Need to get the parent object and get the identifier.
							// but don't know how to get the same.
							long peid = ((Long)otherProps.get(parentType)).longValue();
							childProps.put("parentKey", ObjectNamingUtility.getDbPropsForType(peid,parentType).getProperty("name"));
							int pids[] = ObjectNamingUtility.getID(peid);
							childIds[2] = pids[3];
							childIds[1] = pids[2];
							childIds[0] = pids[1];
						}
					}
					//if parent type is not configured treat this object has no parent;

					if(childObj.equals(ssc)){ //may be we can make it provisioned and instead of auto provisioned
						List omIPList = (List)otherProps.get("omIPList");
						List dataIPList = (List)otherProps.get("dataIPList");
						List authIPList = (List)otherProps.get("authIPList");

						int cageNumber = ObjectDetailsHandler.getInt(otherProps.getProperty("cageNumber"),-1);
						if(cageNumber!=-1 && omIPList!=null){
							String ipAddress = omIPList.get(15 + 1 + ((cageNumber-1)*3) + (1+j) ).toString();
							childProps.put("omAddress", ipAddress);
						}
						if(dataIPList!=null){
							childProps.put("dataAddress",dataIPList.get(3+(1+j)));
						}
						if(authIPList!=null){
							childProps.put("authAddress",authIPList.get(3+(1+j)));
						}
						int slotNo = childIds[3];
						childProps.setProperty("slotNo", String.valueOf(slotNo));
						childProps.setProperty("equiped", "true");
						//setting IP address from the omLIST.
					}else if(childObj.equals(networkgtp)){
						List omIPList = (List)otherProps.get("omIPList");
						List dataIPList = (List)otherProps.get("dataIPList");
						List authIPList = (List)otherProps.get("authIPList");

						int cageNumber = ObjectDetailsHandler.getInt(otherProps.getProperty("cageNumber"),-1);
						if(cageNumber!=-1 && omIPList!=null){
							String ipAddress = omIPList.get(15 + 1 + ((cageNumber-1)*3) + (1+j) ).toString();
							childProps.put("omIpAddress", ipAddress);
						}
						if(dataIPList!=null){
							childProps.put("dataIpAddress",dataIPList.get(3+(1+j)));
						}
						if(authIPList!=null){
							childProps.put("authIpAddress",authIPList.get(3+(1+j)));
						}
					}else if(childObj.equals(cagerg)){
						childProps.setProperty("equiped", "true");
						childProps.setProperty("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
					}
					else if(childObj.equals(pam)){
						if(!(otherProps.getProperty("template").equals("BETA") && childIds[3]==2)){
							childProps.setProperty("controlStatus",String.valueOf(objProps.getProperty("controlStatus",""+StateConstantsUtil.NOT_SUSPENDED)));
							childProps.setProperty("equiped", objProps.getProperty("equiped"));
						}
					}
					else if(childObj.equals(MCCDO1MODEM)){
						childProps.setProperty("mccdo1ModId",String.valueOf(ins[j]));
					}
					else if(childObj.equals(bscdocagene))
					{
						childProps.setProperty("runningSwVersion",otherProps.getProperty("runningSwVersion"));
						childProps.setProperty("currentSWVersion",otherProps.getProperty("runningSwVersion"));
					}
					else if(childObj.equals(pdsncluster))
					{
						childProps.setProperty("a11Authkey",otherProps.getProperty("A11AuthenticatorKey"));
					}
					else if(childObj.equals(hrpdadataemh))
					{
						childProps.setProperty("sshRemoteAddress", EMHUtil.AEMSIP);
						childProps.setProperty("emhCoreLogIP",System.getProperty("nms.server.host","0.0.0.0"));
						childProps.setProperty("emhCoreVlanID",EMHUtil.getCommissiongParameter("EMH_OM_VLAN_ID","1"));
						childProps.setProperty("emhCoreGatewayAddress",EMHUtil.getCommissiongParameter("EMH_OM_DEFAULT_GATEWAY","0.0.0.0"));
					}
					if(type.equals(sectorBsc))
					{
						String carrierName = objProps.getProperty("carrierName");
						String[] linkedObjects = childOd.getLinkedObjects();
						if(linkedObjects!=null)
						{
							for(int lk =0;lk<linkedObjects.length;lk++){
								String linkObjType = linkedObjects[lk];
								System.out.println("The link object type is " + linkObjType);
								String linkObjName = CoreDBUtil.getInstance().getLinkObjectName(linkObjType,carrierName,ins[j]);
								childProps.setProperty(childOd.getLinkedObjectKey(linkObjType),linkObjName);
								//TODO need to populate the link properties in child objects.
							}
						}
					}
					if(otherProps.containsKey("neName")){
						childProps.setProperty("neName", otherProps.getProperty("neName"));
					}else if(type.equals(cage)){
						childProps.setProperty("neName", objName);
					}
					//DST Calculation
					//LBSCLevel
					if(childObj.equals(dstInfo)){
						log("[addAnyObject] : adding DSTInfo Object :" +childObj);
						String timeZone = otherProps.getProperty("timezone");
						TimeZone zone = TimeZone.getTimeZone(timeZone);
						TimeZoneParser tzParser = new TimeZoneParser(zone);
						long DSTStartTime = tzParser.getDSTStartTime();
						long DSTEndTime = tzParser.getDSTEndTime();
						int localTimeOffset = zone.getRawOffset()/(60*1000); //UTC Offset
						long curTime = System.currentTimeMillis()/1000L;
						//if(zone.inDaylightTime(new Date(new GregorianCalendar(zone).getTimeInMillis())))
						//{
						/**
						 * If the EMHTime is(EMH starts) in DSTPeriod that is
						 * CurrentTime between DSTStartTime & DSTEndTime then
						 * EMH should calculate the localTimeOffset inclusive of
						 * DSTOffset
						 */
						if(curTime > DSTStartTime && curTime < DSTEndTime)
							localTimeOffset = (zone.getRawOffset() +  zone.getDSTSavings()) / (60*1000);//java API UTCOffset + DSTOffset
						//}
						/**
						 * If the EMHTime is outside the DSTPeriod (i.e.)
						 * Both Start & End Transition period for the current year is over then
						 * EMH should calculate the next years' DSTStart & EndTime
						 * In the case of America/New_York DSTRules 2007
						 * states that DST starts on March 2nd Sunday and ends on 1st Sunday of November.
						 * EMH starts for the first time after the 1st sunday of November
						 * then DSTStart & End should hold values of the upcoming year
						 */
						int isDSTEnabled = 0;
						if(zone.useDaylightTime())
						{
							isDSTEnabled = 1;
						}
						int DSTOffset = zone.getDSTSavings()/(60*1000);
						childProps.setProperty("isDSTEnabled",String.valueOf(isDSTEnabled));
						childProps.setProperty("DSTOffset",String.valueOf(DSTOffset));
						childProps.setProperty("localTimeOffset",String.valueOf(localTimeOffset));
						childProps.setProperty("DSTStartTime",String.valueOf(DSTStartTime));//TODO
						childProps.setProperty("DSTEndTime",String.valueOf(DSTEndTime));//TODO
					}

/*					//DSTCalculation
					//BSCDO Level
					if(childObj.equals(dstInfoBsc)){
						log("[addAnyObject] : adding DSTInfoBsc Object :" +childObj);
						String timeZone = otherProps.getProperty("timezone");
						TimeZone zone = TimeZone.getTimeZone(timeZone);
						int localTimeOffset = zone.getRawOffset()/(60*1000);
						if(zone.inDaylightTime(new Date()))
						{
							localTimeOffset = zone.getOffset(new GregorianCalendar().getTimeInMillis())/(60*1000);//Java API UTCOffset + DSTOffset
						}
						childProps.setProperty("localTimeOffset",String.valueOf(localTimeOffset));
					}
*/
					otherProps.put(type, Long.valueOf(entityId));
					childProps.setProperty("entityIdentifier",String.valueOf(ObjectNamingUtility.getEntityID(childIds)));
					addAnyObject(childProps,otherProps);
				}
			}
		}
		return objName;
	}

	/**
	 * Method which actually update the object and notify all its dependent objects
	 * about the changes done on it except the given linkObject to the registered Observers
	 * @param objName - Name of the Object which needs update
	 * @param objType - Type of the object which needs update
	 * @param changedProps - Proeprties that are changed
	 * @param od - ObjectDetails class for the respective type.
	 * @param linkObject - Name of the LinkObject if any that this update is from
	 * @param isNotificationNeeded - true if this change has to be notfied or suppressed from notification.
	 * @throws EMHException - If any error occurs during update of the object.
	 */
	private void modifyAnyObject(String objName,String objType,Properties changedProps,ObjectDetails od,String linkObject,boolean isNotificationNeeded) throws EMHException
	{
		try{
			if(changedProps.size() == 0){
				log("[modifyAnyObject]: No props is there to modify in this object :"+objName+ " type:"+objType);
				return;
			}
			//check whether the given value is valid value or not.
			String attrNames[] = od.getCFAttribNames();
			if(attrNames!=null && attrNames.length>0){
				for(int attrId=0;attrId<attrNames.length;attrId++){
					AttribDetails attr1 = od.getAttribDetails(attrNames[attrId]);
					if(changedProps.containsKey(attr1.getObjectKey())){
						String value = changedProps.getProperty(attr1.getObjectKey());
						if(value!=null && !attr1.isValidValue(value))
						{
							System.err.println("attrribute ["+attrNames[attrId]+"/"+attr1.getObjectKey()+"]="+value);
							throw new EMHInventoryException("Given value for the attribute "+attrNames[attrId]+ " is not valid.");
						}
					}
				}
			}
			LockObjectUtil.getInstance().lock(objName,def_lock_timeout);
			log("[modifyAnyObject:2]: modify called for object :"+objName+ " type:"+objType+" with props:"+changedProps+" linkObject:"+linkObject);
			if(od.isManagedObject()){
				ManagedObject moObj = topoApi.getByName(objName);
				if(moObj!=null){
					ManagedObject oldObj = (ManagedObject)moObj.clone();
					moObj.setProperties((Properties)changedProps.clone());
					topoApi.updateObject(moObj, false, false);
					if(toBeNotified && isNotificationNeeded)notifier.notify(EMHTopoNotificationEvent.getObjectUpdatedNotification(moObj, oldObj, changedProps, true));
				}
			}else
			{
				DataObject dobj = (DataObject)userApi.getObject(objName, od.getTableName());
				if(dobj!=null){
					//create a clone to notify it to the listeners and observers.
					DataObject oldObj = (DataObject)dobj.getClass().newInstance();
					oldObj.setProperties(dobj.getProperties());

					//update the objects and update it in the database.
					dobj.setProperties((Properties)changedProps.clone());
					userApi.updateObject(dobj, dobj.getName());
					//notifiy to the registerd observars.
					if(toBeNotified && isNotificationNeeded)notifier.notify(EMHTopoNotificationEvent.getObjectUpdatedNotification(dobj, oldObj, changedProps, false));
				}
			}
			//need to notify dependant objects except the linkObject passed as an argument here above.
			if(toBeNotified && isNotificationNeeded){
				Vector v = ObjectDetailsHandler.getInstance().getDependantObjects(objType);
				if(v!=null && v.size()>0)
				{
					int numOfdepObj = v.size();
					for(int i=0;i<numOfdepObj;i++)
					{
						String depObjType = v.elementAt(i).toString();
						ObjectDetails depod = ObjectDetailsHandler.getInstance().getObjectDetails(depObjType);
						String linkAttrs[] = depod.getLinkAttribDetails(objType);
						Properties depChangedProps = new Properties();
						for(int attr=0;attr<linkAttrs.length;attr++){
							if(changedProps.containsKey(linkAttrs[attr])){
								depChangedProps.put(linkAttrs[attr], changedProps.getProperty(linkAttrs[attr]));
							}
						}
						Vector depVec = getDependentObject(objName, objType, depod);
						for(int dep=0;dep<depVec.size();dep++){
							Object depObj = depVec.elementAt(dep);
							if(depod.isManagedObject())
							{
								//This will never happen since the linking of attributes is supported only
								// for DataObject. So this loop will never be called.
								String depObjName = depObj.toString();
								if(depObjName.equals(linkObject))continue;
								ManagedObject depMo = topoApi.getByName(depObjName);
								if(depMo!=null)
									notifier.notify(EMHTopoNotificationEvent.getObjectUpdatedNotification(depMo, depMo, depChangedProps, true));
							}else
							{
								if(((DataObject)depObj).getName().equals(linkObject)) continue;
								notifier.notify(EMHTopoNotificationEvent.getObjectUpdatedNotification(depObj, depObj, depChangedProps, false));
							}
						}
					}
				}
			}
		}catch (java.util.concurrent.TimeoutException e) {
			throw new EMHException("Unable to lock the object for modify : "+objName);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new EMHException("Unabled to modify the Object."+objName,exp);
		}finally{
			LockObjectUtil.getInstance().unlock(objName);
		}
	}
	private Vector getDependentObject(String objName, String objType, ObjectDetails depod) throws Exception
	{

		Properties criteria = new Properties();
		criteria.put("type", depod.getObjectType());
		String linkObjKey = depod.getLinkedObjectKey(objType);
		criteria.put(linkObjKey, objName);
		if(depod.isManagedObject()){
			return topoApi.getObjectNamesWithProps(criteria);
		}else{
			return userApi.getObjects(depod.getTableName(), criteria);
		}
	}

	/**
	 * Method which will update the object and will update the linked Object if any of its attributes are also changed
	 * and also notify about the changes done on  on the objects including the linkedObjects to the registered Observers
	 * @param objName - Name of the Object which needs update
	 * @param objType - Type of the object which needs update
	 * @param changedProps - Proeprties that are changed
	 * @throws EMHException - If any error occurs during update of the object.
	 */

	public void modifyAnyObject(String objName, String objType, Properties changedProps)throws EMHException
	{
		modifyAnyObject(objName, objType, changedProps,null,true);
	}

	void begin(int i) throws NotSupportedException, SystemException
	{
		TransactionAPI.getInstance().begin(i);
	}
	void rollback() throws SystemException
	{
		TransactionAPI.getInstance().rollback();
	}
	void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException
	{
		TransactionAPI.getInstance().commit();
	}

	/**
	 * Method that will update the object and will update the linked Object if any of its
	 * attributes are also changed. Will be called recursively by itself to update the linked objects.
	 * This will also notify about the changes done on the objects including the linkedObjects to the registered Observers
	 * @param objName - Name of the Object which needs update
	 * @param objType - Type of the object which needs update
	 * @param changedProps - Proeprties that are changed
	 * @throws EMHException - If any error occurs during update of the object.
	 */

	// new modify method to call recursively to update the linked objects also.
	private void modifyAnyObject(String objName, String objType, Properties changedProps,String linkObject,boolean isNotificationNeeded)throws EMHException
	{	
		log("[modifyAnyObject:1]: modify called for object :"+objName+ " type:"+objType+" with props:"+changedProps+" linkObject:"+linkObject);
		ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		if(od!=null && !objName.trim().equals("")){
			try{
				begin(-1);
				Properties objProps = getObjectProps(objName, objType);
				//TODO hardcoding of calling dst attributes need to be more generic call so that we can capture all other dependancy attributes from here

				if(objType.equals(dstInfo)){
					EMHUtil.printOut("DSTHandling via RecenChange starts for LBSC.....",Log.SUMMARY);
					if(!changedProps.containsKey("localTimeOffset")){
						DSTHandler.getInstance().calculateOffSet(objName, changedProps, objType, objProps);
					}
				}else if(objType.equals(sectoremh)){
					if(!changedProps.containsKey("localTimeOffsetSect") &&
							( changedProps.containsKey("DSTStartTime") || changedProps.containsKey("DSTEndTime")
							|| changedProps.containsKey("DSTOffset") || changedProps.containsKey("isDSTEnabled"))){
						EMHUtil.printOut("DSTHandling via RecenChange starts for Sector.....",Log.SUMMARY);
						DSTHandler.getInstance().calculateOffSet(objName, changedProps, objType, objProps);
					}
				}
				//determine if there is any update on the linked attributes if any needs to update that object also.
				String linkObjTypes[] = od.getLinkedObjects();
				if(linkObjTypes!=null && linkObjTypes.length>0){
					for(int i=0;i<linkObjTypes.length;i++){
						boolean linkObjNameChanged = changedProps.containsKey(od.getLinkedObjectKey(linkObjTypes[i]));
						String linkObjName = changedProps.getProperty(od.getLinkedObjectKey(linkObjTypes[i]));
						String linkAttrs[] = od.getLinkAttribDetails(linkObjTypes[i]);
						Properties linkObjProps = null;
						Properties linkChangedProps = new Properties();
						if(linkAttrs!=null && linkAttrs.length>0)
						{
							if(linkObjNameChanged)linkObjProps = getObjectProps(linkObjName, linkObjTypes[i]);
							for(int attr=0;attr<linkAttrs.length;attr++){
								if(changedProps.containsKey(linkAttrs[attr])){
									linkChangedProps.put(linkAttrs[attr],changedProps.get(linkAttrs[attr]));
								}else if(linkObjNameChanged && linkObjProps!=null && linkObjProps.containsKey(linkAttrs[attr]))
								{
									changedProps.put(linkAttrs[attr],linkObjProps.get(linkAttrs[attr]));
								}
							}
						}
						if(linkChangedProps.size()>0){
							if(linkObjName == null) linkObjName = objProps.getProperty(od.getLinkedObjectKey(linkObjTypes[i]));
							if(linkObjName!=null)
							{
								modifyAnyObject(linkObjName, linkObjTypes[i], linkChangedProps,objName,isNotificationNeeded);
							}
						}
					}
				}
				//updating the object here only.
				modifyAnyObject(objName, objType, changedProps, od,linkObject,isNotificationNeeded);
				commit();
			}catch(Exception exp){
				try {
					rollback();
				} catch (SystemException e) {
					EMHUtil.printErr("[modifyAnyObject] Error while rollBack.", e);
				}
				exp.printStackTrace();
				throw new EMHException("Unabled to modify the Object."+objName,exp);
			}
		}
	}

	public synchronized void reconfigureCage(Properties guiProps)throws EMHException
	{
		try{
			begin(-1);
			//pausing status poll since this may delete mediation objects which will affect other objects also.
			StatusPollInitializer.getInstance().pauseStatusPoll();

			Properties newConfigProps = (Properties)guiProps.get("NewConfig");
			Properties oldConfigProps = (Properties)guiProps.get("CurrentConfig");

			Properties newBscConfig = (Properties)newConfigProps.get("BSCConfiguration");
			Properties oldBscConfig = (Properties)oldConfigProps.get("BSCConfiguration");

			String stdTemplate = newBscConfig.getProperty("stdTemplate");
			if(stdTemplate!=null && LBSCDO_NAME!=null){
				Properties lbscprops = new Properties();
				lbscprops.put("stdTemplate",stdTemplate);
				modifyAnyObject(LBSCDO_NAME, lbscdo, lbscprops);
			}

			int noOfNewCages = Integer.parseInt(newBscConfig.getProperty("noCages"));
			int noOfOldCages = Integer.parseInt(oldBscConfig.getProperty("noCages"));

			Vector newCageVec = (Vector)newBscConfig.get("Cage");
			Vector oldCageVec = (Vector)oldBscConfig.get("Cage");

			int newCageVecSize = newCageVec.size();
			int oldCageVecSize = oldCageVec.size();
			log("[reconfigureCage] Cage count  "+noOfOldCages+ " "+noOfNewCages);

			//since we doesn't know which address added we are deleting the table and adding completely
			try{
				String deleteCagePoolDetails = "delete from CagePoolDetails";
				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(deleteCagePoolDetails));
			}catch(Exception e){
				e.printStackTrace();
				throw new EMHException("Unable to delete the CagePoolDetails in the EMHDB",6004);
			}

			int datastartIp=0;
			int authStartIp=0;
			String currentDataPoolNo = null;
			String currentAuthPoolNo = null;

			//constructing data,auth,om ipaddress from ip pool configuration for the cages.
			Properties ipPoolProps = (Properties)newConfigProps.get("IP-POOL-ALLOCATION");
			Vector omSubnetVec = (Vector)ipPoolProps.get("OMSubnet");
			Vector dataSubnetVec = (Vector)ipPoolProps.get("DataSubnet");
			Vector authSubnetVec = (Vector)ipPoolProps.get("AuthSubnet");
			ArrayList omIPList = null;
			if (omSubnetVec.size() > 0)
			{
				Properties omSubnetProp = (Properties)omSubnetVec.elementAt(0);
				String baseAddress = omSubnetProp.getProperty("baseAddress");
				String subnetSize = omSubnetProp.getProperty("subnetSize");
				try {
					omIPList = IPCalculator.getIPList(baseAddress, subnetSize);
					String insertQuery = "insert into CagePoolDetails values ("+"1,'"+"OMADDRESS','"+baseAddress+"','"+subnetSize+"')";
					log("[reconfigureCage] The om query is  "+insertQuery);
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
				} catch (Exception e) {
					e.printStackTrace();
					throw new EMHException("Unable to get the OM IP List from the given baseAddress and subnetSize.",6003);
				}
			}
			Properties dataPool = new Properties();
			int dataVecSize = dataSubnetVec.size();
			for (int i = 0; i < dataVecSize; i++)
			{
				Properties dataSubnetProp = (Properties)dataSubnetVec.elementAt(i);
				String baseAddress = dataSubnetProp.getProperty("baseAddress");
				String subnetSize = dataSubnetProp.getProperty("subnetSize");
				String poolNo = dataSubnetProp.getProperty("no");
				try {
					dataPool.put(poolNo,IPCalculator.getIPList(baseAddress, subnetSize));
					dataPool.put("subnetSize_"+poolNo , subnetSize);
					String insertQuery = "insert into CagePoolDetails values ("+Integer.parseInt(poolNo)+",'"+"DATAADDRESS','"+baseAddress+"','"+subnetSize+"')";
					log("[reconfigureCage] The om query is  "+insertQuery);
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
				} catch (Exception e) {
					e.printStackTrace();
					throw new EMHException("Unable to get the Data IPs  from the given baseAddress and subnetSize.",6004);
				}
			}
			Properties authPool = new Properties();
			int authSize = authSubnetVec.size();
			for (int i = 0; i < authSize; i++)
			{
				Properties authProp = (Properties)authSubnetVec.elementAt(i);
				String baseAddress = authProp.getProperty("baseAddress");
				String subnetSize = authProp.getProperty("subnetSize");
				String poolNo = authProp.getProperty("no");
				try {
					authPool.put(poolNo,IPCalculator.getIPList(baseAddress, subnetSize));
					authPool.put("subnetSize_"+poolNo , subnetSize);
					String insertQuery = "insert into CagePoolDetails values ("+poolNo+",'"+"AUTHADDRESS','"+baseAddress+"','"+subnetSize+"')";
					log("[reconfigureCage] The om query is  "+insertQuery);
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
				} catch (Exception e) {
					e.printStackTrace();
					throw new EMHException("Unable to get the Data IPs  from the given baseAddress and subnetSize.",6004);
				}
			}
			ArrayList lastCageDataIPList = null;
			int lasttcpos = 31;
			if(newCageVecSize==9){
				lastCageDataIPList = getDefaultArraylist("0.0.0.0", cage_dataIPSize);
			}
			int maxSize = (newCageVecSize>oldCageVecSize)?newCageVecSize:oldCageVecSize;
			for(int i = 0; i < maxSize; i++){

				if(i<oldCageVecSize && i<newCageVecSize)
				{
					//modify cage
					Properties oldCageProps = (Properties)oldCageVec.elementAt(i);
					Properties newCageProps = (Properties)newCageVec.elementAt(i);
					if(oldCageProps != null){
						//getting datasublist
						List datasubList = null;


						String newCageTemplate = newCageProps.getProperty("template");
						int newBladeCount = Integer.parseInt(newCageProps.getProperty("noOfBlades"));
						String oldCageTemplate = oldCageProps.getProperty("template");
						int oldBladeCount = Integer.parseInt(oldCageProps.getProperty("noOfBlades"));
						int cageNo = Integer.parseInt(newCageProps.getProperty("no"));
						long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo});
						Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,cage);
						String cageName = objProps.getProperty("name");
						if(cageNo !=9){
							String prevPoolDataNo = currentDataPoolNo;
							currentDataPoolNo = newCageProps.getProperty("dataPoolNo");
							if(!currentDataPoolNo.equals(prevPoolDataNo)){
								datastartIp =0;
							}
							else
							{
								datastartIp +=cage_dataIPSize;
							}
							ArrayList datalist = (ArrayList)dataPool.get(currentDataPoolNo);
							datasubList = datalist.subList(datastartIp, datastartIp+cage_dataIPSize);
							if(lastCageDataIPList!=null && cageNo!=8){
								lastCageDataIPList.remove(lasttcpos);
								lastCageDataIPList.add(lasttcpos++, datasubList.get(61));
								lastCageDataIPList.remove(lasttcpos);
								lastCageDataIPList.add(lasttcpos++, datasubList.get(62));
							}else if(lastCageDataIPList!=null)
							{
								lastCageDataIPList.remove(4);
								lastCageDataIPList.add(4,datasubList.get(61));
								lastCageDataIPList.remove(5);
								lastCageDataIPList.add(5,datasubList.get(62));
							}

						}else
						{
							datasubList = lastCageDataIPList;
						}
						//To maintain authStartIp while this flow go along through every Cage- starts here.
						String prevPoolAuthNo = currentAuthPoolNo;
						currentAuthPoolNo = newCageProps.getProperty("authPoolNo");
						if(!currentAuthPoolNo.equals(prevPoolAuthNo)){
							authStartIp =0;
						}
						else
						{
							authStartIp +=cage_authIPSize;
						}
						//ends here.
						if(i == oldCageVecSize-1 && oldCageVecSize!=newCageVecSize){
							log("[reconfigureCage] : changing redundant to active cage.");
							//change old redundant to newly active cage.
							//1.  set the protecting attribute to true
							Properties cageprops = new Properties();
							cageprops.put("protecting", "false");
							modifyAnyObject(cageName, cage, cageprops);
							//2. adding a lapcrg,apcpgs and apcs for this cage.
							int newRedundantCageNo = noOfNewCages;
							Properties propsOfCage = new Properties();
							propsOfCage.setProperty("cageNumber",cageNo+"");
							propsOfCage.setProperty("standbyCageNumber",newRedundantCageNo+"");
							//String dataPoolNo = newCageProps.getProperty("dataPoolNo");
							propsOfCage.setProperty("no",cageNo+"");
							propsOfCage.setProperty("redundant","false");
							propsOfCage.setProperty("noOfBlades",newBladeCount+"");
							propsOfCage.setProperty("template",newCageTemplate);
							propsOfCage.setProperty("bscid",oldConfigProps.getProperty("bscid"));
							String lapcRGName = addLAPCRG(cageName,cageNo,newRedundantCageNo,propsOfCage);

							//create APCRG, APCPG, APC & update LAPCRG prefferedCage
							CageDetails cageDetails = CageTemplateHandler.getInstance().getCageDetails(newCageTemplate);
							int pgSize = cageDetails.pgList.size();
							int apcCount = 1;
							for(int pgNo = 0; pgNo < pgSize; pgNo++){
								log("[reconfigureCage] The pgsize is  "+pgSize);
								PGDetails pg = (PGDetails)cageDetails.pgList.get(pgNo);
								if (!pg.pgType.equals("APCPG"))
									continue;
								int rgSize = pg.rgList.size();
								for(int j=0;j<rgSize;j++){
									RGDetails rg = (RGDetails)pg.rgList.get(j);
									Properties rgProps = new Properties();
									rgProps.put("type", rg.rgType);
									rgProps.put("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(new int[]{cageNo,rg.instance})));
									rgProps.setProperty("parentKey", lapcRGName);
									rgProps.put("neName", cageName);
									rgProps.setProperty("controlStatus", String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
									String rgName = addAnyObject(rgProps, propsOfCage);
									int ins = Integer.parseInt(String.valueOf(cageNo)+String.valueOf(pg.rowInstance));
									long pgEntityId = ObjectNamingUtility.getEntityID(new int[]{ins});
									Properties pgProps = ObjectNamingUtility.getDbPropsForType(pgEntityId,apcpg);
									String pgName = pgProps.getProperty("name");
									log("[reconfigureCage] The name of the pg is "+pgName);
									deleteAllRelationShips(potential_mapping,null,pgName);
									addRelationShip(potential_mapping, rgName, pgName, cageName);
									int luToBeEquiped = getNoOfLUToBeEquiped(newBladeCount, rg.logicalType, j+1);
									for(int log=1;log<=rg.logicalCount;log++){
										Properties lunit = new Properties();
										lunit.setProperty("type", rg.logicalType);
										lunit.setProperty("parentKey",rgName);
										long eid = ObjectNamingUtility.getEntityID(new int[]{cageNo,rg.instance,getNextLogicalUnitId(rg.logicalType,cageDetails.cageConfigType)});
										lunit.setProperty("entityIdentifier", String.valueOf(eid));
										if(luToBeEquiped >= log){
											lunit.setProperty("controlStatus", String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
											lunit.setProperty("equiped","true");
										}else
										{
											lunit.setProperty("equiped","false");
										}
										int pos = getDataAddressPossition(rg.logicalType, apcCount++);//TODO
										lunit.put("dataAddress", datasubList.get(pos));
										lunit.put("neName", cageName);
										lunit.put("cageName", cageName);
										addAnyObject(lunit, propsOfCage);
									}
								}
							}
						}
						else if(i == newCageVecSize-1 && oldCageVecSize!=newCageVecSize){
							// change the current active cage  to standby cage. for that:

							//1.  set the protecting attribute to true
							Properties cageprops = new Properties();
							cageprops.put("protecting", "true");
							modifyAnyObject(cageName, cage, cageprops);
							//2. remove the corresponding LAPCRG
							deleteLapcRG(cageNo+"");
							//2. update all lapcrg objects with the lattest standby card.

							//LAPCRG Updation only when an LAPCRG Object is deleted
							updateLAPCRG(newCageVec);

							/*
                            Properties props = new Properties();
                            props.setProperty("type",lapcrg);
                            Vector lapcRGObj = topoApi.getObjectNamesWithProps(props);
                            for (int j = 0; j < lapcRGObj.size(); j++){
                                String lapcRGName = (String) lapcRGObj.elementAt(j);
                                Properties changedProps = new Properties();
                                changedProps.setProperty("preferredStandbyCage", String.valueOf(cageNo));
                                modifyAnyObject(lapcRGName, lapcrg, changedProps);
                            }
							 */

							CageDetails cageDetails = CageTemplateHandler.getInstance().getCageDetails(newCageTemplate);
							int pgSize = cageDetails.pgList.size();
							for(int pgNo = 0; pgNo < pgSize; pgNo++){
								log("[reconfigureCage] create potentialMapping with active APCRG  "+pgSize);
								PGDetails pg = (PGDetails)cageDetails.pgList.get(pgNo);
								if (!pg.pgType.equals("APCPG"))
									continue;
								int ins1 = Integer.parseInt(String.valueOf(cageNo)+String.valueOf(pg.rowInstance));
								long pgEntityId1 = ObjectNamingUtility.getEntityID(new int[]{ins1});
								Properties pgProps = ObjectNamingUtility.getDbPropsForType(pgEntityId1,apcpg);
								String pgName = pgProps.getProperty("name");
								int rgSize = pg.rgList.size();
								for(int j=0;j<rgSize;j++){
									RGDetails rg = (RGDetails)pg.rgList.get(j);
									for(int cNo=1;cNo<cageNo;cNo++){
										System.out.println("[reconfigureCage] PotentialMapping  will be made for"+pgName);
										long rgEntityId = ObjectNamingUtility.getEntityID(new int[]{cNo,rg.instance});
										Properties pgProps1 = ObjectNamingUtility.getDbPropsForType(rgEntityId,apcrg);
										String rgName = pgProps1.getProperty("name");
										addRelationShip(potential_mapping, rgName, pgName,"");
										System.err.println("[reconfigureCage]  PotentialMapping is successfully made for "+rgName+"-"+pgName);
									}
								}
							}
						}
						//template count and blade count change has to happen irrespective of standby or mode.
						log("[reconfigureCage] Inside modification "+cageName+" "+cageNo+" "+oldBladeCount+" "+newBladeCount+" "+oldCageTemplate+" "+newCageTemplate);
						if(!newCageTemplate.equals(oldCageTemplate)){//Template Modification
							log("[reconfigureCage] Template modification "+cageName+" "+oldCageTemplate+" "+newCageTemplate);
							modifyCage(cageName,oldCageTemplate,newCageTemplate,newBladeCount,omIPList,datasubList);
							String updateConfigDetails = "update CageConfigDetails set TEMPLATE='"+newCageTemplate+"' where CAGENAME='"+cageNo+"'";
							relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(updateConfigDetails));
						}
						if(newBladeCount != oldBladeCount){//Blade count modification
							log("[reconfigureCage] Blade Count modification "+cageName+" "+oldBladeCount+" "+newBladeCount);
							modifyBladeCount(cageName,oldBladeCount, newBladeCount);
							String updateConfigDetails = "update CageConfigDetails set NOOFBLADES="+newBladeCount+" where CAGENAME='"+cageNo+"'";
							relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(updateConfigDetails));
						}
					}

				}else if(i>oldCageVecSize-1 && i<newCageVecSize)
				{
					//addCage
					Properties newCageProps = (Properties)newCageVec.elementAt(i);
					int cageNo = Integer.parseInt(newCageProps.getProperty("no"));
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,cage);
					String cageName = objProps.getProperty("name");
					log("[reconfigureCage] Cage Addition "+cageName);

					Properties lbscdoProps = new Properties();
					lbscdoProps.setProperty("bscid",oldConfigProps.getProperty("bscid"));
					lbscdoProps.setProperty("NTPAddress1",oldConfigProps.getProperty("NTPAddress1"));
					lbscdoProps.setProperty("NTPAddress2",oldConfigProps.getProperty("NTPAddress2"));
					lbscdoProps.setProperty("swVersion",oldConfigProps.getProperty("swVersion","1.0"));//TODO need to give current sw version

					String redundant = newCageProps.getProperty("redundant");
					String noOfBlades = newCageProps.getProperty("noOfBlades");
					String template = newCageProps.getProperty("template");
					String currentOMPoolNo = newCageProps.getProperty("omPoolNo");
					List datasubList = null;
					if(cageNo !=9){
						String prevPoolDataNo = currentDataPoolNo;
						currentDataPoolNo = newCageProps.getProperty("dataPoolNo");
						if(!currentDataPoolNo.equals(prevPoolDataNo)){
							datastartIp =0;
						}
						else
						{
							datastartIp +=cage_dataIPSize;
						}
						ArrayList datalist = (ArrayList)dataPool.get(currentDataPoolNo);
						datasubList = datalist.subList(datastartIp, datastartIp+cage_dataIPSize);
						if(lastCageDataIPList!=null && cageNo!=8){
							lastCageDataIPList.remove(lasttcpos);
							lastCageDataIPList.add(lasttcpos++, datasubList.get(61));
							lastCageDataIPList.remove(lasttcpos);
							lastCageDataIPList.add(lasttcpos++, datasubList.get(62));
						}else if(lastCageDataIPList!=null)
						{
							lastCageDataIPList.remove(4);
							lastCageDataIPList.add(4,datasubList.get(61));
							lastCageDataIPList.remove(5);
							lastCageDataIPList.add(5,datasubList.get(62));
						}

					}else
					{
						datasubList = lastCageDataIPList;
					}

					String prevPoolAuthNo = currentAuthPoolNo;
					currentAuthPoolNo = newCageProps.getProperty("authPoolNo");
					if(!currentAuthPoolNo.equals(prevPoolAuthNo)){
						authStartIp =0;
					}
					else
					{
						authStartIp +=cage_authIPSize;
					}
					ArrayList authlist = (ArrayList)authPool.get(currentAuthPoolNo);

					Properties propsOfCage = new Properties();
					propsOfCage.put("omIPList",omIPList);
					propsOfCage.put("authIPList", authlist.subList(authStartIp, authStartIp+cage_authIPSize));
					propsOfCage.put("auth_subnetSize", authPool.get("subnetSize_"+currentAuthPoolNo));
					propsOfCage.put("dataIPList", datasubList);
					propsOfCage.put("data_subnetSize", dataPool.get("subnetSize_"+currentDataPoolNo));
					propsOfCage.setProperty("cageNumber",cageNo+"");
					propsOfCage.setProperty("redundant",redundant);
					propsOfCage.setProperty("noOfBlades",noOfBlades);
					propsOfCage.setProperty("template",template);
					propsOfCage.setProperty("standbyCageNumber",noOfNewCages+"");
					propsOfCage.setProperty("bscid",oldConfigProps.getProperty("bscid"));

					//The following properties are addedas they are needed by NetworkTpPool Object.
					propsOfCage.putAll(CoreDBUtil.getInstance().getNetworkTPPoolProps());
					log("[reconfigureCage] The propsOfCage "+propsOfCage);
					log("[reconfigureCage] The lbscdoProps "+lbscdoProps);
					addLBSCDOCage(propsOfCage, lbscdoProps);
					addTransportConfigBsc(cageNo,newCageVecSize,propsOfCage);
					addTransportConfigBscForPendingCages(cageNo ,oldCageVec);
					String insertQuery = "insert into CageConfigDetails VALUES( '"+cageNo+"',"+Integer.parseInt(currentDataPoolNo)+","+Integer.parseInt(currentAuthPoolNo)+","+currentOMPoolNo+",'"+template+"',"+noOfBlades+")";
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
				}
				else if(i < oldCageVecSize && i > newCageVecSize-1){
					Properties oldCageProps = (Properties)oldCageVec.elementAt(i);
					int cageNo = Integer.parseInt(oldCageProps.getProperty("no"));
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,cage);
					String cageName = objProps.getProperty("name");
					log("[reconfigureCage] Cage Deletion "+cageName);
					//deleteCage(cageName);
					String lAPCRGName =	CoreDBUtil.getInstance().getLapcRGForCage(cageName,true);
					String cageRGName =	CageUtil.getInstance().getCageRGForCage(cageName);
					Vector deviceList = new Vector();
					deviceList.addElement(cageRGName);
					if(lAPCRGName!= null)
						deviceList.addElement(lAPCRGName);
					int list = deviceList.size();
					for(int l=0;l<list-1;l++)
					{
						Properties configProps = new Properties();
						String corTag =	String.valueOf(CoreUtil.getInstance().getEMHCorrelationTag(-1));
						configProps.setProperty("$OMCCorrelationTag#",corTag);
						configProps.setProperty("$OMCSequenceTag#",corTag);
						try{

						String lockResult =	EMHConfigurationModule.getInstance().performConfigOperation(ConfigCommandConstants.LOCK,(String)deviceList.elementAt(i),configProps);

						if(!EMHCoreProvUtil.getInstance().checkConfigResult(lockResult))
						{
							EMHUtil.printErr("Unable to lock "+(String)deviceList.elementAt(i)+ "  while deleting Cage "+cageName, Log.DEBUG);
						}
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					deleteAnyObject(cageName, cage);
					deleteLapcRG(cageNo+"");
					deletePendingTransportConfigBSc(cageNo , newCageVecSize);
					String deleteConfigDetails = "delete from CageConfigDetails where CAGENAME='"+cageNo+"'";
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(deleteConfigDetails));
				}
			}
			updateBandClassDetails(newConfigProps);

			//LAPCRG Updation only when new LAPCRG Object is added
			if(newCageVecSize > oldCageVecSize)
				updateLAPCRG(newCageVec);

			commit();
		}catch (Exception ex) {
			try {
				rollback();
			} catch (SystemException sex) {
				sex.printStackTrace();
			}
			ex.printStackTrace();
			throw new EMHException("Exception while reconfigureCage."+ex.getMessage());
		}finally{

			StatusPollInitializer.getInstance().resumeStatusPoll(); //resuming status polling for the objects

			CoreUtil.getInstance().releaseAllBlockedStatusPoll();
			
			/* VC: don't need it unless using addLBSCDOCage api 
			initializeObjectCounts();//this is to handled delete cage objects also.
			*/

		}

	}

	private void deleteLapcRG(String cageNo) throws EMHException
	{
		try{
			Properties criteriaProps = new Properties();
			criteriaProps.setProperty("preferredActiveCage",String.valueOf(cageNo));
			criteriaProps.setProperty("type",lapcrg);
			Vector lapcRGVec = topoApi.getObjectNamesWithProps(criteriaProps);
			if(lapcRGVec.size()>0){
				deleteAnyObject(lapcRGVec.elementAt(0).toString(), lapcrg);
			}
		}catch(Exception ex){
			throw new EMHException("Unable to delte the lapcrg for cage "+cageNo,ex);
		}

	}

	private void addTransportConfigBsc(int cageNumber , int totalCages , Properties propsOfCage) throws EMHInventoryException
	{
		ObjectDetails transportConfigDetails = ObjectDetailsHandler.getInstance().getObjectDetails(transportconfigbsc);
		int instanceCount = transportConfigDetails.getInstanceCount();
		if(instanceCount == 1)
		{
			Properties props = new Properties();
			long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber,1});
			props.put("entityIdentifier", String.valueOf(eid));
			props.put("type", transportconfigbsc);
			props.put("MOName" ,  ObjectNamingUtility.getIDForType(cageNumber , cage , ObjectNamingUtility.NB));
			addTransportConfigBscProps(props, propsOfCage);
		}
		else
		{
			for(int k = 1 ; k < totalCages+1 ; k ++)
			{
				Properties props = new Properties();
				long eid = ObjectNamingUtility.getEntityID(new int[]{k,cageNumber});
				props.put("entityIdentifier", String.valueOf(eid));
				props.put("type", transportconfigbsc);
				props.put("MOName" ,  ObjectNamingUtility.getIDForType(k , cage , ObjectNamingUtility.NB));
				addTransportConfigBscProps(props, propsOfCage);
			}
		}
	}

	private void addTransportConfigBscProps(Properties objProps ,Properties propsOfCage) throws EMHInventoryException
	{
		String authSubnetSize = "";
		String dataSubnetSize = "";
		if(propsOfCage.containsKey("auth_subnetSize"))
		{
			authSubnetSize = propsOfCage.getProperty("auth_subnetSize");
			String authNetmask = IPCalculator.getNetMask(Integer.parseInt(authSubnetSize.substring(authSubnetSize.indexOf("/")+1)));
			List authIPlist =  (List)propsOfCage.get("authIPList");
			objProps.put("authNetworkAddress", authIPlist.get(0));
			objProps.put("authNetmask",authNetmask);
		}
		if (propsOfCage.containsKey("data_subnetSize"))
		{
			dataSubnetSize = propsOfCage.getProperty("data_subnetSize");
			String dataNetmask = IPCalculator.getNetMask(Integer.parseInt(dataSubnetSize.substring(dataSubnetSize.indexOf("/")+1)));
			List dataIPlist =  (List)propsOfCage.get("dataIPList");
			objProps.put("dataNetworkAddress", dataIPlist.get(0));
			objProps.put("dataNetmask", dataNetmask);
		}
		addAnyObject(objProps, propsOfCage);
	}

	private void deletePendingTransportConfigBSc(int cageNo , int totalCages)
	{
		//This is a temporary fix need to be removed. here we are deleteing the
		//remaining transportconfigbsc which are not deleted after the cage is deleted.
		try{
			ObjectDetails transportConfigDetails = ObjectDetailsHandler.getInstance().getObjectDetails(transportconfigbsc);
			int instanceCount = transportConfigDetails.getInstanceCount();
			if(instanceCount != 1)
			{
				for(int k = 1 ; k < totalCages+1 ; k ++)
				{
					long eid = ObjectNamingUtility.getEntityID(new int[]{k,cageNo});
					String name = ObjectNamingUtility.getIDForType(eid , transportconfigbsc , ObjectNamingUtility.NB);
					deleteAnyObject(name,transportconfigbsc);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private void addTransportConfigBscForPendingCages(int cageNumber , Vector oldCageVec) throws EMHInventoryException
	{
		ObjectDetails transportConfigDetails = ObjectDetailsHandler.getInstance().getObjectDetails(transportconfigbsc);
		int instanceCount = transportConfigDetails.getInstanceCount();
		if(instanceCount != 1)
		{
			int oldCageVecSize = oldCageVec.size();
			for(int k = 0 ; k < oldCageVecSize ; k ++)
			{
				Properties oldCageProps = (Properties)oldCageVec.elementAt(k);
				Properties props = new Properties();
				int oldCageNo = Integer.parseInt(oldCageProps.getProperty("no"));
				long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber,oldCageNo});
				props.put("entityIdentifier", String.valueOf(eid));
				props.put("type", transportconfigbsc);
				props.put("MOName" ,  ObjectNamingUtility.getIDForType(cageNumber , cage , ObjectNamingUtility.NB));

				long oldCageEid = ObjectNamingUtility.getEntityID(new int[]{oldCageNo,oldCageNo});
				String oldTptName = ObjectNamingUtility.getIDForType(oldCageEid , transportconfigbsc , ObjectNamingUtility.NB);
				Properties dbProps = CoreDBUtil.getInstance().getTransportConfigBscProps(oldTptName);
				props.putAll(dbProps);
				addAnyObject(props, oldCageProps);
			}
		}
	}

	private void updateLAPCRG(Vector cageVec){
		try{
			int cageVecSize = cageVec.size();
			int standbyCage = 0;
			for(int i=cageVecSize;i>0;i--){
				Properties propsOfCage = (Properties)cageVec.elementAt(i-1);
				if(Boolean.valueOf(propsOfCage.getProperty("redundant")).booleanValue()){
					standbyCage = ObjectDetailsHandler.getInt(propsOfCage.getProperty("no"), standbyCage);
					break;
				}
			}
			Properties criteriaProps = new Properties();
			criteriaProps.setProperty("type",lapcrg);
			Vector lapcRGVec = topoApi.getObjectNamesWithProps(criteriaProps);
			if (lapcRGVec != null){
				for (int i = 0; i < lapcRGVec.size(); i++){
					String lapcRGName = (String)lapcRGVec.elementAt(i);
					/*					LAPCRG lapcrgObj = (LAPCRG)topoApi.getByName(lapcRGName);
lapcrgObj.setPreferredStandbyCage(standbyCage);
topoApi.updateObject(lapcrgObj);*/

					//LAPCRG modified only when the prferredStandbyCage changes
					LAPCRG lapcrgObj = (LAPCRG)topoApi.getByName(lapcRGName);
					if(standbyCage != lapcrgObj.getPreferredStandbyCage()){
						Properties changedProps = new Properties();
						changedProps.setProperty("preferredStandbyCage", String.valueOf(standbyCage));
						modifyAnyObject(lapcRGName, lapcrg, changedProps);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateBandClassDetails(Properties ipbscdoProps)throws EMHException
	{

		//deleting all the three tables and inserting the new details
		try{
			String deleteBandClassDetails = "delete from BandClassDetails";
			relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(deleteBandClassDetails));
		}catch(Exception e){
			e.printStackTrace();
			throw new EMHException("Unable to delete the Band Class in the EMHDB",6004);
		}

		Properties channelProps = (Properties)ipbscdoProps.get("CHANNEL");
		Vector carrierInfo = (Vector)channelProps.get("CARRIERINFO");
		int carrierInfoSize = carrierInfo.size();
		for (int i = 0; i < carrierInfoSize; i++){
			Properties carrierInfoProps = (Properties) carrierInfo.elementAt(i);
			int bandClass = Integer.parseInt(carrierInfoProps.getProperty("bandClass"));
			int channelList = Integer.parseInt(carrierInfoProps.getProperty("channelList"));
			int id = Integer.parseInt(carrierInfoProps.getProperty("id"));
			try{
				String insertQuery = "insert into BandClassDetails values ("+id+","+bandClass+","+channelList+")";

				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
			}catch(Exception e){

				e.printStackTrace();
				throw new EMHException("Unable to set the Band Class values in the EMHDB",6004);
			}
		}
	}

	private void sendDeleteNotificationToMediation(String objName)
	{
		try{
			ArrayList moNameList=new ArrayList();
			moNameList.add(objName);
			String mediationId=CoreDBUtil.getInstance().getManagerIpAddress(objName);
			if(mediationId!=null && !mediationId.equals(CoreConstants.UNKNOWN_MGR))
			{
				EMHCommunicationData data=new EMHCommunicationData(mediationId,EMHCommunicationConstants.TOPO_ID,EMHCommunicationConstants.UN_ASSIGN_MO,moNameList);
				EMHCommunicationHandler.getInstance().send(data);
				log("Successfully sent the deleted object information to Mediation. Object : "+objName+" ; Mediation IpAddress = "+mediationId);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isObjectPresent(String objName, String objType) throws EMHException
	{
		ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		if(od !=null)
		{
			try{
				if(od.isManagedObject())
				{
//					this is different because managed objects will also be in cache. so we don't need to go to DB to check its presence.
					return topoApi.isManagedObjectPresent(objName);
				}
				else
				{
					String query = "select NAME from DataObject where NAME='"+objName+"'";
					ArrayList list = CoreDBUtil.getInstance().getDataFromDB(query);
					if(list!=null && list.size() > 0) return true;
				}
			}catch(Exception ex)
			{
				throw new EMHException("Unable to check the object is present for :"+objName+" with type :"+objType);
			}
		}
		return false;
	}
	public boolean deleteAnyObject(String objName, String objType)throws EMHException
	{
		if(!isObjectPresent(objName, objType))
		{
			log("Object does not exists :"+objName);
			return false;
		}
		try{
			begin(-1);
			log("[deleteAnyObject] delete called for "+objName);
			if(objType.equals(cage) || objType.equals(MCCDO) )
			{
				sendDeleteNotificationToMediation(objName);
			}
			Vector vec = ObjectDetailsHandler.getInstance().getSideDeletedObjects(objType);
			if (vec != null){
				int objVecSize = vec.size();
				for (int i = 0; i < objVecSize; i++){
					String sd_type = (String)vec.elementAt(i);
					ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(sd_type);
					if (od.isManagedObject()){
						if (!sd_type.equals(cage)){
							Vector subObjVec = getObjNames(objName,sd_type);
							if (subObjVec != null){
								for (int k = 0; k < subObjVec.size(); k++){
									String subObjName = (String)subObjVec.elementAt(k);
									//now recursively calling delete any object itself instead of traversing here.
									log("[deleteAnyObject] calling recursivly for deleting the side subObjName "+subObjName);
									deleteAnyObject(subObjName, sd_type);
								}
							}
						}
					}else{
						//dataObject is not recursively called to avoid double getting of objects.
						Vector userVec = getUserStorageObjects(od.getTableName(), objName, sd_type);
						if (userVec != null){
							for (int j = 0; j < userVec.size(); j++){
								DataObject dataObj = (DataObject)userVec.elementAt(j);
								String name = dataObj.getName();
								try{
									deleteDataObjectChildrens(dataObj.getName());
									log("[deleteAnyObject] The name is  "+name);
									userApi.deleteObject(dataObj,name);
									if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectDeletedNotification(dataObj, false));
								}catch(Exception ex){
									ex.printStackTrace();
								}
							}
						}

					}
				}
			}
			ObjectDetails parentOD = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
			//need to get the list of objects which have relationships
			if (parentOD.isManagedObject()){

				//delete any relationship to this object
				if (parentOD.isRelationShipNeeded()){

				    /* VCDEBUG: no MCCDO fo EMS
					if(objType.equals(MCCDO))
					{
						ManagedObject mo=topoApi.getByName(objName);
						String lmccdoName = mo.getProperties().getProperty("lmccdoname");
						//String protecting=mo.getProperties().getProperty("protecting");
						//if(protecting.equals("true"))
						if(mo.getProperties().getProperty("cardType").equals(DeviceTypeConstants.MCCDO_REDUNDANT))
						{
							deleteAllRelationShips(null, null, objName);
							//deleteObjects("target",objName,"type",relationobject,"com.motorola.emh.core.modeling.RelationObject");

							String updateQuery = "Update MCCDOCard set STANDBYMCCDO=''  where STANDBYMCCDO = '"+objName+"'";
							try{
								relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(updateQuery));
								//clear the memory because of update.
								DBServer.comapi.clearMemory();
							}catch(Exception ex){
								ex.printStackTrace();
							}

						}else
						{
							// This will delete the lmccdo object
							MCCDOInventoryInterface.getInstance().deleteSectorForSixtyConfiguration(lmccdoName);
							deleteAllRelationShips(null, null, objName);
							deleteAnyObject(lmccdoName,lmccdo);
						}
					}else{
				    
						if(objType.equals(lmccdo))
						{
							deleteRelatedLmccdoBsc(objName);
						}
				    */
						deleteAllRelationShips(null, objName, null);
						//					}

				}//check whether current object relationship is needed.
				if(objType.equals(apcpg))
				{
					System.out.println("All relationship made with standby APCPG will be deleted");
					deleteAllRelationShips(null,null,objName);
					System.out.println("All relationship made with standby APCPG has been deleted");
				}

				//delete relationships of its child object
				ManagedObject moObj = topoApi.getByName(objName);
				notifyBulkDeleteAndDeleteRelationShips(moObj);
				topoApi.deleteObjectAndSubElements(objName,-1);
				if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectDeletedNotification(moObj, true)); // notifier for the current object.
			}else
			{
				DataObject dataObj = (DataObject)userApi.getObject(objName, parentOD.getTableName());
				deleteDataObjectChildrens(dataObj.getName());
				log("[deleteAnyObject] deleting the object : "+dataObj.getName());
				userApi.deleteObject(dataObj,dataObj.getName());
				if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectDeletedNotification(dataObj, false));
			}

			commit();
		}
		catch(Exception e){
			try {
				rollback();
			} catch (SystemException e1) {
				EMHUtil.printErr("[deleteAnyObject] : Error in rollback.", e1);
			}
			if(e instanceof EMHException) throw (EMHException)e;
			throw new EMHException("Error occured in deleting the object : "+objName,e);
		}
		return true;
	}


	private void deleteDataObjectChildrens(String parentName)throws EMHException{
		try{

			ArrayList list = getAllChildrenNamesWithType("DataObject", parentName);
			deleteDataObjectList(list);
		}catch(Exception ex){
			throw new EMHException("Error while deleting the childrens  for :"+parentName);
		}

	}

	private void deleteDataObjectList(ArrayList list)throws Exception{
		if(list!=null){
			for(int i=list.size();i>0;i--){
				String childName = ((Object[])list.get(i-1))[0].toString();
				String childType = ((Object[])list.get(i-1))[1].toString();
				ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(childType);
				if(childType.equals(sectorBsc)){
					deleteDataObjectList(getLinkedObjects(childName , childType));
				}
				DataObject childObj = (DataObject)userApi.getObject(childName, od.getTableName());
				log("[deleteAnyObject] deleting child objects :  "+childName);
				userApi.deleteObject(childObj,childObj.getName());
				if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectDeletedNotification(childObj, false));
			}
		}
	}

	private ArrayList getLinkedObjects(String parentName , String parentType)throws EMHException
	{
		//TODO Need to remove this hardcoded query to a genric one
		String getSectorBSCLinks = "select sn.NAME,do.TYPE from NEIGHBORemh sn INNER JOIN DataObject do ON do.NAME=sn.NAME where sn.LOCALNEIGHBOR='"+parentName+"'";
		return CoreDBUtil.getInstance().getDataFromDB(getSectorBSCLinks);
	}

	private void notifyBulkDeleteAndDeleteRelationShips(ManagedObject obj)throws EMHException{
		ArrayList list = getAllChildrenNamesWithType("ManagedObject", obj.getName());
		if(list!=null){
			for(int i=list.size();i>0;i--){
				String childName = ((Object[])list.get(i-1))[0].toString();
				ManagedObject delObj;
				try {
//					deleteDataObjectChildrens(childName);
					delObj = topoApi.getByName(childName);
				} catch (Exception e) {
					throw new EMHException("Error while getting the Object using TopoAPI :"+childName,e);
				}
				if(delObj!=null){
					log("[deleteAnyObject] The subchild name is  "+childName+" type is "+delObj.getType());
					deleteAllRelationShips(null, childName, null);
					if(toBeNotified)notifier.notify(EMHTopoNotificationEvent.getObjectDeletedNotification(delObj, true));
				}
			}
		}
//		deleteDataObjectChildrens(obj.getName()); //getting and deleting all the childs for this MO which is a DataObject.
	}
	/*
	 * The below method is to get the Childrens with their hirearchy if any for the given name in the specific table.
	 * Right now this method will work for both ManagedObejct and others. Also this method will traverse upto fourth
	 * level inclusive of the given object. i.e, it will return children, grand children and the great grand children.
	 * This is restricted because in EMH CNEOMI model the containment hirearchy is upto four levels only.
	 */
	private ArrayList getAllChildrenNamesWithType(String tableName, String parentName)throws EMHException
	{
		String query = "select NAME,TYPE from "+tableName+" where PARENTKEY='" + parentName+ "' " +
		" OR PARENTKEY in (select NAME from "+tableName+" where PARENTKEY='" + parentName+ "' OR" +
		" PARENTKEY in (select NAME from " + tableName+ " where PARENTKEY = '" + parentName+ "'))";
		return CoreDBUtil.getInstance().getDataFromDB(query);
	}

	private Vector getUserStorageObjects(String className, String moName, String type)
	{
		Vector toRet = new Vector();
		try{
			String query = "select NAME from DataObject where TYPE='"+type +"' AND (PARENTKEY='"+moName+"' OR MONAME='"+moName+"')";
			ArrayList objNames = CoreDBUtil.getInstance().getDataFromDB(query);
			ObjectDetails childOD = ObjectDetailsHandler.getInstance().getObjectDetails(type);
			if(objNames!=null){
				for(int i=0 ; i < objNames.size() ; i++){
					Object[] child = (Object[])objNames.get(i);
					String childName = child[0].toString();
					String chTableName = childOD.getTableName();
					DataObject childObj = (DataObject)userApi.getObject(childName , chTableName);
					if(childObj != null)
						toRet.add(childObj);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return toRet;
	}

	private Vector getObjNames(String objName, String objType)
	{
		try{
			Properties criteriaProps = new Properties();
			criteriaProps.setProperty("neName",objName);
			criteriaProps.setProperty("type",objType);
			return topoApi.getObjectNamesWithProps(criteriaProps);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private boolean modifyBladeCount(String cageName, int previousBladeCount, int currentBladeCount)
	{
		try{

			int cageNo = Integer.parseInt(cageName.substring(cageName.indexOf("-")+1));

			if (previousBladeCount > currentBladeCount){
				for (int i = currentBladeCount+2 ; i < previousBladeCount+2 ; i++){
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,i});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,blade);
					String bladeName = objProps.getProperty("name");
					log("[modifyBladeCount] The blade name in the modifyBladeCount "+bladeName+" "+previousBladeCount+" "+currentBladeCount);
					Properties bladeProps = new Properties();
					bladeProps.setProperty("equiped","false");
					bladeProps.setProperty("controlStatus",(String.valueOf(StateConstantsUtil.SUSPENDED)));
					modifyAnyObject(bladeName, blade, bladeProps);
					modifyPAM(bladeName,"false",String.valueOf(StateConstantsUtil.SUSPENDED));
				}
			}
			else if (previousBladeCount < currentBladeCount){
				for (int i = previousBladeCount+2 ; i < currentBladeCount+2 ; i++){
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,i});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,blade);
					String bladeName = objProps.getProperty("name");
					log("[modifyBladeCount] The blade name in the modifyBladeCount "+bladeName+" "+previousBladeCount+" "+currentBladeCount);
					Properties bladeProps = new Properties();
					bladeProps.setProperty("equiped","true");
					bladeProps.setProperty("controlStatus",(String.valueOf(StateConstantsUtil.NOT_SUSPENDED)));
					modifyAnyObject(bladeName, blade, bladeProps);
					modifyPAM(bladeName,"true",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	private boolean modifyPAM(String bladeName, String equiped, String controlStatus)
	{
		try{
			if(topoApi.isManagedObjectPresent(bladeName)){
				ManagedObject pamObj = topoApi.getByName(bladeName);
				String children[] = pamObj.getChildrenKeys();
				for (int i = 0 ; i < children.length ; i++){
					String pamName = children[i];
					Properties pamProps = new Properties();
					pamProps.setProperty("equiped",equiped);
					pamProps.setProperty("controlStatus",controlStatus);
					modifyAnyObject(pamName, pam, pamProps);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	private boolean modifyCage(String cageName, String oldTemplate, String newTemplate, int bladeToBeCutover, ArrayList omIPList, List dataIPList)
	{
		try
		{
			int cageNo = Integer.parseInt(cageName.substring(cageName.indexOf("-")+1));

			//get the no of emh cards based on the template
			CageDetails oldCageDetails = CageTemplateHandler.getInstance().getCageDetails(oldTemplate);
			ArrayList oldEMHList = oldCageDetails.emhList;
			log("[modifyCage] The old EMH ArrayList "+oldEMHList);
			int oldEMHSize = oldEMHList.size();

			CageDetails newCageDetails = CageTemplateHandler.getInstance().getCageDetails(newTemplate);
			ArrayList newEMHList = newCageDetails.emhList;
			//System.err.println("The new EMH ArrayList "+newEMHList);
			int newEMHSize = newEMHList.size();

			//add emh blade
			if (newEMHSize > oldEMHSize){
				log("[modifyCage] Inside upgrade of EMH ");
				for (int j = 0; j < newEMHSize; j++){
					Properties newEMHProp = (Properties)newEMHList.get(j);
					int slotNo = Integer.parseInt(newEMHProp.getProperty("slotno"));
					String type = newEMHProp.getProperty("type");
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,slotNo});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,type);
					String emhMed = objProps.getProperty("name");

					Properties addEMHProps= new Properties();
					addEMHProps.put("name",emhMed);
					addEMHProps.put("entityIdentifier",entityId+"");
					addEMHProps.put("parentKey", cageName);
					addEMHProps.put("type", type);
					addEMHProps.put("slotNo", slotNo+"");
					addEMHProps.setProperty("equiped", "true");


					String ipAddress = omIPList.get(6 + emhMedCount).toString();//om ip space hardcoded
					addEMHProps.put("ipAddress", ipAddress);

					addEMHProps.setProperty("port","4000");
					addEMHProps.setProperty("cageNumber",cageNo+"");
					addEMHProps.put("statusPollEnabled","true");
					addEMHProps.put("tester","usertest");
					// addEMHProps.put("uClass","com.motorola.emh.core.statuspoller.MediationStatusPoller");
					addEMHProps.put("uClass","com.motorola.emh.mediation.statuspoller.CageMedStatusPoller");
					Properties pollIntervalProps = EMHUtil.initPollIntervalDetails();
					System.out.println("pollInterval for mediation is : "+pollIntervalProps.getProperty("mediation"));
					//addEMHProps.put("pollInterval",pollIntervalProps.getProperty("mediation"));
					addEMHProps.put("pollInterval","30");
					emhMedCount++;

					Properties propsOfCage = new Properties();
					propsOfCage.put("emhSlot", String.valueOf(slotNo));
					propsOfCage.put("cageNumber",cageNo+"");

					String emhName = addAnyObject(addEMHProps, propsOfCage);
					Vector tpNames = addEMHTP(cageName, emhName, propsOfCage,false,false);//Setting activeEP/TP and overridedefaluInitialValues to false, because these EP/Tp associated Mediation.The States will be updated when the mediation is comes up.
					addEMHEP(cageName, cageNo, emhName, slotNo,propsOfCage,ipAddress,tpNames,false,false);
					addRTM(cageNo, slotNo, propsOfCage, emhmed);

					//delete blade, pam, and its relationships
					int bladeSlotNo = slotNo - 2;
					long bladeEntityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,bladeSlotNo});
					Properties bladeProps = ObjectNamingUtility.getDbPropsForType(bladeEntityId,blade);
					String bladeName = bladeProps.getProperty("name");
					//TOOD delete last APC,TC & SCA if any.
					//for each blade we need to delete 1 TC,1TC,1APC,1APC in rows 1,2,3,4 respectively
					//query to get the rg name lu type and lu max instance.
					String query="select mo.PARENTKEY,mo.TYPE, max(le.ENTITYIDENTIFIER & 65535) from LogicalElement le "
						+ " INNER JOIN ManagedObject mo on mo.NAME=le.NAME "
						+ " INNER JOIN RelationObject pm on pm.SOURCE=mo.PARENTKEY "
						+ " INNER JOIN RelationObject mem on pm.TARGET=mem.TARGET "
						+ " INNER JOIN ManagedObject pamo on pamo.NAME=mem.SOURCE "
						+ " where pamo.PARENTKEY='" + bladeName + "' and pamo.type='" + pam + "' and mo.TYPE != '" + sca +"'"
						+ " and pm.RELATIONSHIP='"+potential_mapping+"' group by mo.PARENTKEY";
					ArrayList list = CoreDBUtil.getInstance().getDataFromDB(query);
					for(int i=0;i<list.size();i++){
						Object obj[] = (Object[])list.get(i);
						if(obj!=null){
							String parentName=String.valueOf(obj[0]);
							String luType = String.valueOf(obj[1]);
							int instance = ObjectDetailsHandler.getInt(String.valueOf(obj[2]),0);
							if(parentName!=null && luType!=null && instance>0){
								Properties props = ObjectNamingUtility.getObjProps(parentName,ObjectNamingUtility.NB);
								int parentid[] = ObjectNamingUtility.getID(Long.parseLong(props.getProperty("eid")));
								int childid[] = {parentid[1],parentid[2],parentid[3],instance};
								deleteAnyObject(ObjectNamingUtility.getDbPropsForType(ObjectNamingUtility.getEntityID(childid), luType).getProperty("name",""), luType);
							}
						}
					}
					deleteAnyObject(bladeName,blade);
				}
			}else if (newEMHSize < oldEMHSize){//remove the emh blade
				log("[modifyCage] Inside downgrade of EMH");
				for (int j = 0; j < oldEMHSize; j++){
					Properties oldEMHProp = (Properties)oldEMHList.get(j);
					//System.err.println("The properties object of old "+oldEMHProp);
					int slotNo = Integer.parseInt(oldEMHProp.getProperty("slotno"));
					String type = oldEMHProp.getProperty("type");
					long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,slotNo});
					Properties objProps = ObjectNamingUtility.getDbPropsForType(entityId,type);
					//System.err.println("The obj props  "+objProps+"entityId "+entityId+" type "+type+" slotNo "+slotNo);
					String emhMed = objProps.getProperty("name");
					EMHMediation medObj = (EMHMediation)topoApi.getByName(emhMed);
					String ipAddress=medObj.getIpAddress();
					log("[modifyCage] The name of the mediation to delete is "+emhMed);
					try{
						EMHCommunicationData data=new EMHCommunicationData(ipAddress,EMHCommunicationConstants.TOPO_ID,EMHCommunicationConstants.DELETE_MEDIATION);
						EMHCommunicationHandler.getInstance().send(data);
					}catch(EMHCommunicationException e)
					{
						EMHUtil.printErr("Unable to send deleteMediation request to the Meidation Server.",null);
						EMHUtil.logException(e,Log.DEBUG);
					}
					topoApi.deleteObjectAndSubElements(emhMed,-1);//TOOD need to use deleteAnyObject
					EMHCommunicationHandler.getInstance().removeSession(ipAddress);

					//need to delete EMHTP
					for (int i = 1; i < 3; i++){
						int tpId = Integer.parseInt((String.valueOf(cageNo)+slotNo+i));
						long tpEntityId = ObjectNamingUtility.getEntityID(new int[]{tpId});
						Properties tpObjProps = ObjectNamingUtility.getDbPropsForType(tpEntityId,emhtp);
						String emhTP = tpObjProps.getProperty("name");
						topoApi.deleteObjectAndSubElements(emhTP,-1);
					}

					//need to add Blade and PAM
					int bladeSlotNo = slotNo - 2;
					long bladeEntityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,bladeSlotNo});
					//Properties bladeObjProps = ObjectNamingUtility.getDbPropsForType(bladeEntityId,blade);
					//String bladeName = bladeObjProps.getProperty("name");
					Properties bladeProps = new Properties();
					bladeProps.put("type", blade);
					bladeProps.setProperty("slotNo",bladeSlotNo+"");
					bladeProps.setProperty("entityIdentifier",bladeEntityId+"");
					//log("baldeToBeCutover="+bladeToBeCutover+" current baldeSlot="+bladeSlotNo);
					if (bladeToBeCutover >= bladeSlotNo){
						bladeProps.put("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
						bladeProps.put("equiped", "true");
					}else{
						bladeProps.put("equiped", "false");
					}
					bladeProps.setProperty("parentKey", cageName);
					bladeProps.setProperty("neName", cageName);
					Properties propsOfCage = new Properties();
					propsOfCage.put("cageNumber",cageNo+"");
					propsOfCage.put("neName",cageName);
					propsOfCage.put("template",newCageDetails.cageConfigType);
					int pgSize = newCageDetails.pgList.size();
					for(int i=1;i<=pgSize;i++){
						PGDetails pg = (PGDetails)newCageDetails.pgList.get(i-1);
						String pgType = pg.pgType;
						int pgId = Integer.parseInt((String.valueOf(cageNo)+pg.rowInstance));
						long pgEntityId = ObjectNamingUtility.getEntityID(new int[]{pgId});
						Properties pgObjProps = ObjectNamingUtility.getDbPropsForType(pgEntityId,pgType);
						String pgName = pgObjProps.getProperty("name");
						propsOfCage.put("Row"+pg.rowid,pgName);
					}
					//System.err.println("The props of Cage before adding PAM "+propsOfCage);
					addAnyObject(bladeProps, propsOfCage);

					//Addition LogicalUnits
					//Addition of ipaddress, controlstatus, equiped

					int prevApcPos =0, prevTcPos=0;
					for (int rowNo = 1; rowNo < 5; rowNo++){
						Vector rgNames = getRGNames(propsOfCage.getProperty("Row"+rowNo),potential_mapping);
						//System.err.println("The rgNames are "+rgNames);
						if (rgNames != null){
							int rgSize = rgNames.size();
							for (int a = 0; a < rgSize; a++){
								String rgName = (String)rgNames.elementAt(a);
								String luType = null;
								ManagedObject luMO = topoApi.getByName(rgName);
								if (luMO instanceof SCARG)
									continue;
								else if (luMO instanceof APCRG)
									luType = apc;
								else
									luType = tc;
								int parentRGIndex = rgName.indexOf("-");
								int parentRGNo = Integer.parseInt(rgName.substring(parentRGIndex+1,parentRGIndex+2));
								int rgIndex = rgName.lastIndexOf("-");
								int rgNo = Integer.parseInt(rgName.substring(rgIndex+1,rgIndex+2));
								long luEntityId = ObjectNamingUtility.getEntityID(new int[]{parentRGNo,rgNo,getNextLogicalUnitId(luType,newTemplate)});
								Properties luObjProps = ObjectNamingUtility.getDbPropsForType(luEntityId,luType);
								String name = luObjProps.getProperty("name");
								Properties lunit = new Properties();
								lunit.setProperty("name", name);
								lunit.setProperty("type", luType);
								lunit.setProperty("parentKey",rgName);
								lunit.setProperty("entityIdentifier", String.valueOf(luEntityId));
								lunit.setProperty("equiped","false");//TODO
								lunit.put("neName", cageName);
								lunit.put("cageName", cageName);
								int luPos=0;
								if(luType.equals(apc)){
									prevApcPos = prevApcPos+luMO.getChildrenKeys().length;
									luPos =prevApcPos;
									prevApcPos++;
								}else
								{
									prevTcPos = prevTcPos+luMO.getChildrenKeys().length;
									luPos =prevTcPos;
									prevTcPos++;
								}
								luPos++;
								int pos = getDataAddressPossition(luType, luPos);
								lunit.put("dataAddress", dataIPList.get(pos));
								addAnyObject(lunit, propsOfCage);
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return true;
	}

	private Vector getRGNames(String pgName, String relType)
	{
		try{
			Properties criteriaProps = new Properties();
			criteriaProps.setProperty("target",pgName);
			criteriaProps.setProperty("relationship",relType);
			criteriaProps.setProperty("type",relationobject);
			Vector rgNames = topoApi.getObjectNamesWithProps(criteriaProps);
			if (rgNames != null){
				Vector nameVec = new Vector();
				for (int i = 0; i < rgNames.size(); i++){
					String objName = (String)rgNames.elementAt(i);
					RelationObject relObj = (RelationObject)topoApi.getByName(objName);
					String source = relObj.getSource();
					nameVec.addElement(source);
				}
				return nameVec;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void addCageGUI(Properties ipbscdoProps)throws EMHException
	{
		log("addCageGUI called with props :"+ipbscdoProps);
		String bscdocage = "/BSCDOCAGE-1";
		try
		{
			if(topoApi.isManagedObjectPresent(bscdocage))
			{
				throw new EMHException("Already Configured. Please use Re-Configure Wizard for any changes.",6001);
			}
		}
		catch (EMHException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new EMHException("Unable to check whether IP-BSC-DO is configured or not",6002,e);
		}
		try {
			toBeNotified = false;
			begin(-1);
		} catch (NotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//constructing data,auth,om ipaddress from ip pool configuration for the cages.
		Properties ipPoolProps = (Properties)ipbscdoProps.get("IP-POOL-ALLOCATION");
		Vector omSubnetVec = (Vector)ipPoolProps.get("OMSubnet");
		Vector dataSubnetVec = (Vector)ipPoolProps.get("DataSubnet");
		Vector authSubnetVec = (Vector)ipPoolProps.get("AuthSubnet");
		ArrayList omIPList = null;
		if (omSubnetVec.size() > 0)
		{
			Properties omSubnetProp = (Properties)omSubnetVec.elementAt(0);
			String baseAddress = omSubnetProp.getProperty("baseAddress");
			String subnetSize = omSubnetProp.getProperty("subnetSize");
			try {
				omIPList = IPCalculator.getIPList(baseAddress, subnetSize);
				String insertQuery = "insert into CagePoolDetails values ("+"1,'"+"OMADDRESS','"+baseAddress+"','"+subnetSize+"')";
				System.err.println("The om query is  "+insertQuery);
				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
			} catch (Exception e) {
				e.printStackTrace();
				throw new EMHException("Unable to get the OM IP List from the given baseAddress and subnetSize.",6003);
			}
		}
		Properties dataPool = new Properties();
		int dataVecSize = dataSubnetVec.size();
		for (int i = 0; i < dataVecSize; i++)
		{
			Properties dataSubnetProp = (Properties)dataSubnetVec.elementAt(i);
			String baseAddress = dataSubnetProp.getProperty("baseAddress");
			String subnetSize = dataSubnetProp.getProperty("subnetSize");
			String poolNo = dataSubnetProp.getProperty("no");
			try {
				dataPool.put(poolNo,IPCalculator.getIPList(baseAddress, subnetSize));
				dataPool.put("subnetSize_"+poolNo , subnetSize);
				String insertQuery = "insert into CagePoolDetails values ("+Integer.parseInt(poolNo)+",'"+"DATAADDRESS','"+baseAddress+"','"+subnetSize+"')";
				System.err.println("The om query is  "+insertQuery);
				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
			} catch (Exception e) {
				e.printStackTrace();
				throw new EMHException("Unable to get the Data IPs  from the given baseAddress and subnetSize.",6004);
			}
		}
		Properties authPool = new Properties();
		int authSize = authSubnetVec.size();
		for (int i = 0; i < authSize; i++)
		{
			Properties authProp = (Properties)authSubnetVec.elementAt(i);
			String baseAddress = authProp.getProperty("baseAddress");
			String subnetSize = authProp.getProperty("subnetSize");
			String poolNo = authProp.getProperty("no");
			try {
				authPool.put(poolNo,IPCalculator.getIPList(baseAddress, subnetSize));
				authPool.put("subnetSize_"+poolNo , subnetSize);
				String insertQuery = "insert into CagePoolDetails values ("+poolNo+",'"+"AUTHADDRESS','"+baseAddress+"','"+subnetSize+"')";
				System.err.println("The om query is  "+insertQuery);
				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
			} catch (Exception e) {
				e.printStackTrace();
				throw new EMHException("Unable to get the Data IPs  from the given baseAddress and subnetSize.",6004);
			}
		}

		Properties channelProps = (Properties)ipbscdoProps.get("CHANNEL");
		Vector carrierInfo = (Vector)channelProps.get("CARRIERINFO");
		int carrierInfoSize = carrierInfo.size();

		for (int i = 0; i < carrierInfoSize; i++){
			Properties carrierInfoProps = (Properties) carrierInfo.elementAt(i);
			int bandClass = Integer.parseInt(carrierInfoProps.getProperty("bandClass"));
			int channelList = Integer.parseInt(carrierInfoProps.getProperty("channelList"));
			int id = Integer.parseInt(carrierInfoProps.getProperty("id"));
			try{
				String insertQuery = "insert into BandClassDetails values ("+id+","+bandClass+","+channelList+")";
				System.err.println("The bandclass query is  "+insertQuery);
				relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
			}catch(Exception e){
				e.printStackTrace();
				throw new EMHException("Unable to set the Band Class values in the EMHDB",6004);
			}
		}

		try{
			StatusPollInitializer.getInstance().pauseStatusPoll();
			CageUtil.getInstance().suppressEvents(true);
			// add hrpda objects with its side provisioned obejcts
			updateHRPDA(ipbscdoProps);

			//adds the LBSCDO objects with its side provisioned objects before adding the cages
			updateLBSCDO(ipbscdoProps);

			// adding objects that needs to be added atinit before adding cages
			addAtInit();

			//add ANAAALinks
			Properties anAAALinkProps = (Properties)ipbscdoProps.get("ANAAALINK");
			addANAAALink(anAAALinkProps);

			//add PDSNLinks
			Properties pdsnLinkProps = (Properties)ipbscdoProps.get("PDSNLINK");
			addPDSNLink(pdsnLinkProps);
			Properties cageProps = (Properties)ipbscdoProps.get("BSCConfiguration");

			//gets the list of cage properties
			Vector cageVec = (Vector)cageProps.get("Cage");

			int cageVecSize = cageVec.size();
			int standbyCage = 0;
			for(int i=cageVecSize;i>0;i--){
				Properties propsOfCage = (Properties)cageVec.elementAt(i-1);
				if(Boolean.valueOf(propsOfCage.getProperty("redundant")).booleanValue()){
					standbyCage = ObjectDetailsHandler.getInt(propsOfCage.getProperty("no"), standbyCage);
					break;
				}
			}
			int datastartIp=0;
			int authStartIp=0;
			String currentDataPoolNo = null;
			String currentAuthPoolNo = null;
			ArrayList lastCageDataIPList = null;
			int lasttcpos =31;
			if(cageVecSize == 9)
				lastCageDataIPList = getDefaultArraylist("0.0.0.0", cage_dataIPSize);

			for(int i=0;i<cageVecSize;i++)
			{
				try
				{
					Properties propsOfCage = (Properties)cageVec.elementAt(i);
					String cageNumber = propsOfCage.getProperty("no");
					propsOfCage.setProperty("cageNumber", cageNumber);
					propsOfCage.setProperty("standbyCageNumber", String.valueOf(standbyCage));
					//getting all associated IP address for this cage
					propsOfCage.put("omIPList",omIPList);
					String prevPoolDataNo = currentDataPoolNo;
					if(!cageNumber.equals("9")){
						currentDataPoolNo = propsOfCage.getProperty("dataPoolNo");
						if(!currentDataPoolNo.equals(prevPoolDataNo)){
							datastartIp =0;
						}
						else
						{
							datastartIp +=cage_dataIPSize;
						}
						//TODO need to handle data address for standby cage.
						ArrayList datalist = (ArrayList)dataPool.get(currentDataPoolNo);
						List subList = datalist.subList(datastartIp, datastartIp+cage_dataIPSize);
						propsOfCage.put("dataIPList", subList);
						propsOfCage.put("data_subnetSize", dataPool.get("subnetSize_"+currentDataPoolNo));

						if(lastCageDataIPList!=null && !cageNumber.equals("8")){
							lastCageDataIPList.remove(lasttcpos);
							lastCageDataIPList.add(lasttcpos++, subList.get(61));
							lastCageDataIPList.remove(lasttcpos);
							lastCageDataIPList.add(lasttcpos++, subList.get(62));
						}else if(lastCageDataIPList!=null)
						{
							lastCageDataIPList.remove(4);
							lastCageDataIPList.add(4,subList.get(61));
							lastCageDataIPList.remove(5);
							lastCageDataIPList.add(5,subList.get(62));
						}
					}else if(lastCageDataIPList!=null){
						log("DATA IP LIST FOR 9th Cage:"+lastCageDataIPList);
						propsOfCage.put("dataIPList", lastCageDataIPList);
					}
					String prevPoolAuthNo = currentAuthPoolNo;
					currentAuthPoolNo = propsOfCage.getProperty("authPoolNo");
					if(!currentAuthPoolNo.equals(prevPoolAuthNo)){
						authStartIp =0;
					}
					else
					{
						authStartIp +=cage_authIPSize;
					}
					ArrayList authlist = (ArrayList)authPool.get(currentAuthPoolNo);
					propsOfCage.put("authIPList", authlist.subList(authStartIp, authStartIp+cage_authIPSize));
					propsOfCage.put("auth_subnetSize", authPool.get("subnetSize_"+currentAuthPoolNo));

					//The following properties are addedas they are needed by NetworkTpPool Object.
					if (omSubnetVec.size() > 0)
					{
						Properties omSubnetProp = (Properties)omSubnetVec.elementAt(0);
						String omBaseAddress = omSubnetProp.getProperty("baseAddress");
						String omSubnetSize = omSubnetProp.getProperty("subnetSize");
						String omNetmask = IPCalculator.getNetMask(Integer.parseInt(omSubnetSize.substring(omSubnetSize.indexOf("/")+1)));
						propsOfCage.setProperty("omNetworkAddress", omBaseAddress);
						propsOfCage.setProperty("omNetmask",omNetmask);
					}
					propsOfCage.setProperty("authenticationVlan",ipPoolProps.getProperty("authVLANID") );
					propsOfCage.setProperty("dataVlan",ipPoolProps.getProperty("dataVLANID") );
					propsOfCage.setProperty("authDefaultGateway",ipPoolProps.getProperty("authDefaultGatewayIPAddress") );
					propsOfCage.setProperty("dataDefaultGateway",ipPoolProps.getProperty("dataDefaultGatewayIPAddress") );
					propsOfCage.setProperty("omVlan",EMHUtil.getCommissiongParameter("EMH_OM_VLAN_ID","1"));
					propsOfCage.setProperty("omDefaultGateway",EMHUtil.getCommissiongParameter("EMH_OM_DEFAULT_GATEWAY","0.0.0.0"));
					String template = propsOfCage.getProperty("template");
					int noOfBlades = Integer.parseInt(propsOfCage.getProperty("noOfBlades"));
					int currentOMPoolNo = Integer.parseInt(propsOfCage.getProperty("omPoolNo"));
					addLBSCDOCage(propsOfCage,ipbscdoProps);
					addTransportConfigBsc(i+1,cageVecSize,propsOfCage);
					String insertQuery = "insert into CageConfigDetails VALUES( "+"'"+cageNumber+"',"+Integer.parseInt(currentDataPoolNo)+","+Integer.parseInt(currentAuthPoolNo)+","+currentOMPoolNo+",'"+template+"',"+noOfBlades+")";
					relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(insertQuery));
				}catch(Exception ex){
					ex.printStackTrace();
					throw ex;
				}
			}
			commit();

            updateStandbyStates();
			//EMHTopoListener.getInstance().setJournalEnabled(true);
			CoreUtil.getInstance().releaseAllBlockedStatusPoll();
			CageUtil.getInstance().suppressEvents(false);
			NLCBDataGeneration.getInstance().generateNLCBData();
			//calling status poll for mediation objects.

			//Performing the 8 pegs to 4 pegs conversion algorithm.
			CoreDcUtil.getInstance().refreshPMDataMapping();

/*			This is commented because the staus poll be will be called by the StatusPolling scheduler at the next interval
 * 				try{
				for(Enumeration e = tempMap.keys();e.hasMoreElements();){
					Vector v = (Vector)tempMap.get(e.nextElement());
					for(int i=0;i<v.size();i++){
						topoApi.doStatusPoll(v.elementAt(i).toString());
					}
				}
			}catch(Exception e){
				log("Error while calling status poll for mediation objects."+e.toString());
				//don't want to propagate this error.
			}
			*/
		}catch(Exception ex){
			try {
				rollback();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			/* VC: don't need it unless using addLBSCDOCage api 
			initializeObjectCounts();
			*/
			CageUtil.getInstance().suppressEvents(false);
			ex.printStackTrace();
			CoreUtil.getInstance().releaseAllBlockedStatusPoll();
			throw new EMHException("Unable to add Cage "+ex.toString());
		}finally{
			toBeNotified = true;
			StatusPollInitializer.getInstance().resumeStatusPoll();
		}

		//EMHAgentUtil.getInstance().setSynchReqMgmtReason(CNEOMIConstants.event_buffer_overflow);
		//EMHAgentUtil.getInstance().constructAndSendSynchRequiredEvent("A IPBSCDO Cage has been Added through GUI");
		//AuditManagementInformation.setOverFlow(true);
		log("adding cage ends....");
		/**
		 * DSTHandling starts
		 */
		/* VC: don't use DSTHandler
		DSTHandler.getInstance().checkDSTForLBSC();
		*/
	}

	private void addANAAALink(Properties anAAALinkProp)	throws EMHException
	{
		Vector anAAALinkVec = (Vector)anAAALinkProp.get("ANAAA");
		int vecSize = anAAALinkVec.size();
		for (int i = 1; i <= vecSize; i++)
		{
			//TODO make a change in gui such that all keys in the props matches with that of object.
			long entityIdentifier = ObjectNamingUtility.getEntityID(new int[]{1,i}); //parent cluster id is hardcoded.
			Properties aaProps = (Properties)anAAALinkVec.elementAt(i-1);
			Properties props = new Properties();
			props.put("entityIdentifier", String.valueOf(entityIdentifier));
			props.put("type", anaaalink);
			props.put("parentKey", "/ANAAACLUSTER-1"); //hardcoding parent object.
			props.put("neName", LBSCDO_NAME);
			props.setProperty("equiped", "true");
			props.setProperty("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));

			props.put("anAAAIPAddress", aaProps.getProperty("ipaddress"));
			props.put("anAAAAuthKey", aaProps.getProperty("authKey"));
			addAnyObject(props, anAAALinkProp);
		}
	}


	private void addPDSNLink(Properties pdsnLinkProps)throws EMHException
	{
		log("PDSN PROPS:"+pdsnLinkProps);
		Vector pdsnLinkVec = (Vector)pdsnLinkProps.get("PDSN");
		log("PDSN VEC"+pdsnLinkVec);
		int vecSize = pdsnLinkVec.size();
		for (int i = 1; i <= vecSize; i++)
		{
			//TODO make a change in gui such that all keys in the props matches with that of object.
			long entityIdentifier = ObjectNamingUtility.getEntityID(new int[]{1,i}); //parent cluster id is hardcoded.
			Properties pdsnprops = (Properties)pdsnLinkVec.elementAt(i-1); //ipaddress sharedkey etc will be populated from the gui;
			Properties props = new Properties();
			props.put("entityIdentifier", String.valueOf(entityIdentifier));
			props.put("type", pdsnlink);
			props.put("parentKey", "/PDSNCLUSTER-1");//hardcoding parent object.
			props.put("neName", LBSCDO_NAME);
			props.put("pdsnIPAddress",pdsnprops.getProperty("ipaddress"));
			props.setProperty("equiped", "true");
			props.setProperty("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
			//props.put(arg0, arg1)
			addAnyObject(props, pdsnLinkProps);
		}

	}



	private void updateHRPDA(Properties props)throws EMHException
	{
		Properties hrpdaProps = new Properties();
		hrpdaProps.setProperty("timeServer", props.getProperty("NTPAddress1",""));
		hrpdaProps.setProperty("alternateTimeServer1",props.getProperty("NTPAddress1",""));
		hrpdaProps.setProperty("networkElementId", props.getProperty("bscid"));
		hrpdaProps.setProperty("entityIdentifier",props.getProperty("bscid"));
		hrpdaProps.setProperty("type", hrpda);
		hrpdaProps.setProperty("runningSwVersion", props.getProperty("swVersion", EMHUtil.getInstance().getServerRunningSoftwareVersion()));
		hrpdaProps.setProperty("neManagerLocation", EMHUtil.AEMSIP);
                hrpdaProps.setProperty("allInstalledSwPackages", CoreDBUtil.getInstance().getAllInstalledSwPackages());
		HRPDA_NAME = addAnyObject(hrpdaProps, props);
	}

	//TODO to be called from addCageGUI
	private void updateLBSCDO(Properties lbscdoProps)throws EMHException
	{
		controlChannelOffset = Integer.parseInt(lbscdoProps.getProperty("ControlChannelOffset"));
		rabOffset = Integer.parseInt(lbscdoProps.getProperty("RABOffset"));
		int bscId = Integer.parseInt(lbscdoProps.getProperty("bscid"));
		EMHUtil.BSCID = bscId;
		String hexSid = lbscdoProps.getProperty("sid");
		int sid = Integer.decode("0X".concat(hexSid)).intValue();
		//int sid = Integer.parseInt(lbscdoProps.getProperty("sid"));
		String hexNid = lbscdoProps.getProperty("nid");
		int nid = Integer.decode("0X".concat(hexNid)).intValue();
		//int nid = Integer.parseInt(lbscdoProps.getProperty("nid"));
		//int pzid = Integer.parseInt(lbscdoProps.getProperty("pzid"));
		String hexPzid = lbscdoProps.getProperty("pzid");
		int pzid = Integer.decode("0X".concat(hexPzid)).intValue();
		String countryCode = lbscdoProps.getProperty("country");
		String hexColorCode = lbscdoProps.getProperty("colorcode");
		int colorCode = Integer.decode("0X".concat(hexColorCode)).intValue();
		//int colorCode = Integer.parseInt(lbscdoProps.getProperty("colorcode"));
		String systemName = lbscdoProps.getProperty("systemName","");
		String systemDescription = lbscdoProps.getProperty("systemDescription","");
		String contactName = lbscdoProps.getProperty("contactName","");
		String locationArea = lbscdoProps.getProperty("locationArea","");
		String timezone = lbscdoProps.getProperty("timezone");
		String subnet = lbscdoProps.getProperty("subnet");
		Properties bscConfig = (Properties)lbscdoProps.get("BSCConfiguration");
		String mode = bscConfig.getProperty("mode","");
		String stdTemplate = bscConfig.getProperty("stdTemplate","");
		Properties objProps = new Properties();

		objProps.setProperty("bscid",String.valueOf(bscId));
		objProps.setProperty("sid",String.valueOf(sid));
		objProps.setProperty("nid",String.valueOf(nid));
		objProps.setProperty("pzid",String.valueOf(pzid));
		objProps.setProperty("countryCode",countryCode);
		objProps.setProperty("ownColorCode",String.valueOf(colorCode));
		objProps.setProperty("systemName",systemName);
		objProps.setProperty("sysDescr",systemDescription);
		objProps.setProperty("sysContact",contactName);
		objProps.setProperty("sysLocation",locationArea);
		objProps.setProperty("timezone", timezone);
		objProps.setProperty("mode",mode);
		objProps.setProperty("subnet",subnet);
		objProps.setProperty("stdTemplate",stdTemplate);
		objProps.setProperty("entityIdentifier",String.valueOf(bscId));
		objProps.setProperty("type", lbscdo);
		objProps.setProperty("ControlChannelOffset",String.valueOf(controlChannelOffset));
		objProps.setProperty("RABOffset",String.valueOf(rabOffset));
		LBSCDO_NAME = addAnyObject(objProps, lbscdoProps);

		//String neighborBscdoName = ObjectNamingUtility.getIDForType(colorCode+1, DeviceTypeConstants.neighborBSCDOInfoListBsc, ObjectNamingUtility.SB);
		Properties neighborBscdoProps = new Properties();
		neighborBscdoProps.setProperty("type",DeviceTypeConstants.neighborBSCDOInfoListBsc );
		neighborBscdoProps.setProperty("entityIdentifier",String.valueOf(colorCode+1));
		neighborBscdoProps.setProperty("neighborobj" , LBSCDO_NAME);
		neighborBscdoProps.setProperty("MOName" , LBSCDO_NAME);
		neighborBscdoProps.setProperty("vendorFlag" , String.valueOf(0));
		neighborBscdoProps.setProperty("parentKey" , LBSCDO_NAME);
		addAnyObject(neighborBscdoProps , new Properties());
	}


	private void addLBSCDOCage(Properties propsOfCage,Properties lbscdoProps) throws EMHException
	{
		int cageNumber = ObjectDetailsHandler.getInt(propsOfCage.getProperty("cageNumber"),-1);
		List omIPList = (ArrayList)propsOfCage.get("omIPList");
		List dataIPList = (List)propsOfCage.get("dataIPList");
		List authIPList = (List)propsOfCage.get("authIPList");

		if(cageNumber!=9 && (dataIPList == null || dataIPList.size() < cage_dataIPSize)){
			throw new EMHException("required data ip address is not generated",6010);
		}
		if(authIPList == null || authIPList.size() < cage_authIPSize){
			throw new EMHException("unable to generate required auth ip address is not generated",6010);
		}
		int standbyCageNo = -1;
		if(cageNumber == -1){
			throw new EMHInventoryException("Unknown cage number "+cageNumber);
		}
		if(cageNumber > 9 ){
			throw new EMHInventoryException("Currently EMH  supports only 9 Cages and not more than that :"+cageNumber);
		}
		boolean isStandbyCage = Boolean.valueOf(propsOfCage.getProperty("redundant")).booleanValue();

		if (isStandbyCage)
			standbyCageNo = cageNumber;
		else
			standbyCageNo = ObjectDetailsHandler.getInt(propsOfCage.getProperty("standbyCageNumber"), -1);

		int bladeToBeCutover = Integer.parseInt(propsOfCage.getProperty("noOfBlades"));

		String cageTemplate = propsOfCage.getProperty("template");
		CageDetails cageDetails = CageTemplateHandler.getInstance().getCageDetails(cageTemplate);
		if(cageTemplate == null){
			throw new EMHInventoryException("Unknown Template configured.");
		}
		Properties props = new Properties();
		props.put("type", cage);
		props.put("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(new int[]{cageNumber})));
		props.put("bscid", propsOfCage.getProperty("bscid",lbscdoProps.getProperty("bscid")));
		props.put("displayName","Cage "+cageNumber);
		props.put("entityType",ObjectNamingUtility.getEntityTypeForObjType(cage, ObjectNamingUtility.NB)+"");
		props.put("statusPollEnabled","true");
		props.put("tester","usertest");
		props.put("uClass","com.motorola.emh.mediation.statuspoller.CageMedStatusPoller");
		//props.put("uClass","com.motorola.emh.core.statuspoller.CageStatusPoller");
		Properties pollIntervalProps = EMHUtil.initPollIntervalDetails();
		System.out.println("pollInterval for lbsc is : "+pollIntervalProps.getProperty("lbsc"));
		//props.put("pollInterval",pollIntervalProps.getProperty("lbsc"));
		props.put("pollInterval","150");
		props.put("parentKey",LBSCDO_NAME);
		props.put("managerIPAddress",CoreConstants.UNKNOWN_MGR);
		props.put("equiped", "true");

		props.put("ipAddress",omIPList.get(15 + ((cageNumber-1)*3) + 1 )); //This is hardcoded as of now.
		if (isStandbyCage){
			props.put("protecting","true");
		}else{
			props.put("protecting","false");
		}
		props.put("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
		propsOfCage.setProperty("runningSwVersion",lbscdoProps.getProperty("swVersion"));
		propsOfCage.put("agentAddress",props.getProperty("ipAddress"));//this will be used by HAPNEControl
		/**
		 * Timezone field will not be available in the props during reconfigure.
		 */
		String timezone = lbscdoProps.getProperty("timezone");
		if(timezone==null)
			propsOfCage.put("timezone",CoreDBUtil.getInstance().getLBSCDOTimeZone());
		else
			propsOfCage.put("timezone",timezone);
		String cageName = addAnyObject(props, propsOfCage);
		CoreUtil.getInstance().blockeStatusPoll(cageName); //This gets released once the transaction is commited
		/*
		 * Below are the list of side provisioned Objects with Cage. The order should be maintained
		 * in the XML.
		 * 1 * BSCDOCAGENE, 1*SSCPG, 1*SAMPG, 4*FANTRAY, 2*PEM, 2*SAM, 2*SSC+(2*NETWORKEP), 1 * NETWORKTPPOOL
		 * 2 * NETWORKGTP+(2*NETWORKTP),1*CAGERG
		 *
		 */
		//props used for networkelementmanagement
		Properties nemProps = new Properties();
		nemProps.put("type",networkelementmanagement);
		nemProps.put("entityIdentifier", ObjectNamingUtility.getEntityID(new int[]{cageNumber,1})+"");
		String lBscDoNumber = LBSCDO_NAME.substring(LBSCDO_NAME.indexOf("-")+1);
		nemProps.put("networkElementId", "LBSC-"+lBscDoNumber+"-"+cageNumber);//Format of the string is "LBSC-X-Y" where X is the lBscDoBsc numeric id and Y is the ipBscDoCage id SR : MOTCM00775635
		nemProps.put("timeServer",lbscdoProps.getProperty("NTPAddress1","0.0.0.0"));
		nemProps.put("alternateTimeServer1",lbscdoProps.getProperty("NTPAddress1","0.0.0.0"));
		nemProps.put("alternateTimeServer2",lbscdoProps.getProperty("NTPAddress2","0.0.0.0"));
		String vip = VirtualHostUtil.getVirtualIP();
		nemProps.put("neManagerLocation", (vip!=null)?vip:getLocalIPAddress());
		nemProps.put("runningSwVersion", lbscdoProps.getProperty("swVersion", EMHUtil.getInstance().getServerRunningSoftwareVersion()));
		nemProps.put("parentKey", cageName);
		nemProps.put("MOName", cageName);
		addAnyObject(nemProps, propsOfCage); //removed from side provisioned need to get it from operator data

		ArrayList list = cageDetails.emhList;
		for( int i=0; i< list.size();i++){
			Properties emhProps = (Properties)list.get(i);
			int emhSlotNo = Integer.parseInt(emhProps.getProperty("slotno"));
			String emhType = emhProps.getProperty("type");
			String ipAddress ="";
			Properties addEMHProps= new Properties();

			addEMHProps.put("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(
					new int[]{cageNumber,emhSlotNo})));
			addEMHProps.put("parentKey", cageName);
			addEMHProps.put("type", emhType);
			addEMHProps.put("slotNo", emhSlotNo+"");
			addEMHProps.setProperty("equiped", "true");
			addEMHProps.put("runningSwVersion", lbscdoProps.getProperty("swVersion", EMHUtil.getInstance().getServerRunningSoftwareVersion()));

			String activeCoreName = null;
			String standbyCoreName = null;
			boolean isActive = false;
			boolean isStandby = false;
			boolean isStandbyExistsAlready = false;
			boolean activeTP_EP = false;

			if(emhType.equals(emhcore)){
				if(CoreUtil.getInstance().isEMHDebugLogEnabled())
				{
					addEMHProps.put("debug","true");
				}
				ipAddress = omIPList.get(4 + emhCoreCount).toString();//om ip space hardcoded
				String activeIpAddress = getLocalIPAddress();
				if (activeIpAddress.equals(ipAddress)){
					System.out.println("Adding active core");
					addEMHProps.put("protecting","false");
					addEMHProps.put("opState","1");
					addEMHProps.put("usageState","1");
					addEMHProps.put("controlStatus","255");
					addEMHProps.putAll(EMHUtil.getInstance().getEMHHardwareProperties(emhcore));
					isActive = true;
				}else{
					isStandby = true;
					System.out.println("Adding standby core");
					addEMHProps.put("protecting","false");
					if (CoreDBUtil.getInstance().checkStandbyAvailability())
					{
						EMHUtil.printOut("Standby core already connected to the Active core.",Log.DEBUG);
						addEMHProps.put("opState","1");
						addEMHProps.put("controlStatus","255");
						addEMHProps.put("protecting","true");
						isStandbyExistsAlready = true;
					}
				}
				emhCoreCount++;
			}else
			{
				ipAddress = omIPList.get(6 + emhMedCount).toString();//om ip space hardcoded
				addEMHProps.setProperty("port","4000");
				addEMHProps.setProperty("cageNumber",String.valueOf(cageNumber));
				addEMHProps.put("statusPollEnabled","true");
				addEMHProps.put("tester","usertest");
				addEMHProps.put("uClass","com.motorola.emh.core.statuspoller.MediationStatusPoller");
				Properties pollIntervalMedProps = EMHUtil.initPollIntervalDetails();
				System.out.println("pollInterval for addmediation is : "+pollIntervalMedProps.getProperty("mediation"));
				//addEMHProps.put("pollInterval",pollIntervalMedProps.getProperty("mediation"));
				addEMHProps.put("pollInterval","150");
				emhMedCount++;
				//EMHInventoryMgr.getInstance().discoverMediation(addEMHProps);
				//medPropsList.add(prop);
				//CoreUtil.getInstance().addBlockedSPMediation(emhName);
			}

			System.out.println("ADD EMH Props ::" + addEMHProps );
			addEMHProps.put("ipAddress", ipAddress);
			String emhName = addAnyObject(addEMHProps, propsOfCage);
			if(emhType.equals(emhmed)){
				Vector vec = (Vector)tempMap.get(cageName);
				if(vec == null) {
					vec = new Vector();
					tempMap.put(cageName, vec);
				}
				vec.addElement(emhName);
				CoreUtil.getInstance().blockeStatusPoll(emhName); //This gets released once the transaction is commited
			}
			propsOfCage.put("emhSlot", String.valueOf(emhSlotNo));

			if(emhType.equals(emhcore) )
			{
				if(isActive && activeCoreName == null)
				{
					activeCoreName = emhName;
				}

				if(activeCoreName != null && !activeTP_EP )
				{
					activeTP_EP = true;
				}
				else
				{
					activeTP_EP = false;
				}

				Vector tpNames = addEMHTP(cageName, emhName, propsOfCage,activeTP_EP,isStandbyExistsAlready);
				addEMHEP(cageName, cageNumber, emhName, emhSlotNo,propsOfCage,ipAddress,tpNames,activeTP_EP,isStandbyExistsAlready);
				if (isActive)
				{
					propsOfCage.putAll(EMHUtil.getInstance().getEMHHardwareProperties(rtm));
				}
				addRTM(cageNumber, emhSlotNo, propsOfCage, emhcore);
			}
			else
			{
				Vector tpNames = addEMHTP(cageName, emhName, propsOfCage,false,false);//Setting the Meditaions intial states from the objectDetails.xml.The states will be updated when the Mediation comes up.
				addEMHEP(cageName, cageNumber, emhName, emhSlotNo,propsOfCage,ipAddress,tpNames,false,false);
				addRTM(cageNumber, emhSlotNo, propsOfCage, emhmed);
			}

		}
		String lapcRgName = null;
		if(!isStandbyCage){
			lapcRgName = addLAPCRG(cageName,cageNumber,standbyCageNo,propsOfCage);
			propsOfCage.put(lapcrg, lapcRgName);
		}
		addLogicalPGsAndRGs(cageDetails, cageName, cageNumber, standbyCageNo, propsOfCage);
		for(int slotNo=cageDetails.startBladeSlot; slotNo<=cageDetails.endBladeSlot;slotNo++){
			Properties bladeProps = new Properties();
			bladeProps.put("type", blade);
			bladeProps.setProperty("slotNo", slotNo+"");
			bladeProps.setProperty("entityIdentifier", ObjectNamingUtility.getEntityID(new int[]{cageNumber,slotNo})+"");
			if(bladeToBeCutover > (slotNo-2))
			{
				bladeProps.put("controlStatus",String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
				bladeProps.put("equiped", "true");
			}else{
				bladeProps.put("equiped", "false");
			}
			bladeProps.setProperty("parentKey", cageName);
			bladeProps.setProperty("neName", cageName);

			addAnyObject(bladeProps, propsOfCage);
		}

		Vector epList = getAllObjectsForType(cageName, networkep);
		for(int i=0;i<epList.size();i++){
			String epName = epList.elementAt(i).toString();
			Properties epProps = ObjectNamingUtility.getObjProps(epName,ObjectNamingUtility.NB);
			int ins[] = ObjectNamingUtility.getID(Long.parseLong(epProps.getProperty("eid","-1")));
			if(ins[3] == -1){
				EMHUtil.printErr("Unable to get the NetworkEP instance. fro creating reationships", Log.VERBOSE);
				continue;
			}
			int sscid = ins[2];
			int cageid = ins[1];
			// 		int gtpInstance = Integer.parseInt(cageid+""+sscid);
			Properties tempProps = new Properties();
			tempProps.put("cageNumber", cageid+"");
			tempProps.put("sscSlot", sscid+"");
			int gtpInstanceCount[]=  ObjectDetailsHandler.getInstance().getObjectDetails(networkgtp).getInstance(tempProps);
			int gtpInstance =gtpInstanceCount[i];
			int instanceCount[] = ObjectDetailsHandler.getInstance().getObjectDetails(networktp).getInstance(tempProps);
			for(int j=0;j<instanceCount.length;j++)
			{
				String tpName = ObjectNamingUtility.getIDForType(
						ObjectNamingUtility.getEntityID(new int[]{gtpInstance,instanceCount[j]}),
						networktp,
						ObjectNamingUtility.NB);
				addRelationShip(dependency, tpName, epName, cageName);
			}
		}

		// Let's Set the EMH Platforms Timezone if this is a add request.
		if(timezone != null)
		{
			EMHUtil.printOut("Call to Perform EMH Time Synchronization " + timezone, Log.DEBUG);
			EMHUtil.getInstance().performEMHTimeSynchronization(timezone, null);
		}
		//all the above count to be taken from the template document to which it belongs.
	}

	private Vector getAllObjectsForType(String neName,String type) throws EMHInventoryException
	{
		Properties criteriaProps = new Properties();
		criteriaProps.setProperty("neName", neName);
		criteriaProps.setProperty("type",type);
		try {
			Vector vec = topoApi.getObjectNamesWithProps(criteriaProps);
			System.err.println(type+" in "+neName+" objects ==:"+vec);
			return vec;
		} catch (Exception e) {
			throw new EMHInventoryException("unabled to get the Object List from the Database.",e);
		}
	}


	private void addEMHEP(String cageName,int cageNumber,String emhName,int emhSlot,
			Properties propsOfCage,String ipAddress,Vector tpNames,boolean isActiveEP,boolean overrideDefaultInitialStates) throws EMHInventoryException{
		//This is not auto-provisioned since the parent name will be different in different case.
		ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(emhep);
		int inst[] = od.getInstance(propsOfCage);
		for(int i=0;i<inst.length;i++){
			Properties props = new Properties();
			long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber,emhSlot,inst[i]});
			props.setProperty("type", emhep);
			props.setProperty("entityIdentifier", String.valueOf(eid));
			props.setProperty("parentKey",emhName);
			props.setProperty("neName",cageName);
			props.setProperty("ipAddress",ipAddress);
			props.setProperty("name",emhName+"/EMHEP-"+inst[i]);
			if(! (!isActiveEP && ! overrideDefaultInitialStates)) // Not overriding the defalut intial states in the ObjectDetails.xml file, if it is standby EP/TP and the standby core not exists befor provisioning the cage.
			{
				overrideDefaultInitialStatesEP_TP(props);
			}
			String epName = addAnyObject(props, propsOfCage);
			System.out.println(epName+": The EMHEP properties for " + emhName + " :: "+props) ;
			/*    		for(int rel=0;rel<tpNames.size();rel++){
addRelationShip(relType, epName, tpNames.elementAt(rel).toString(), cageName);
}
			 */
		}
	}



	private Vector addEMHTP(String cageName, String emhName, Properties propsOfCage,boolean isActiveTP,boolean overrideDefaultInitialStates) throws EMHInventoryException{

		Vector emhTpNames = new Vector();
		ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(emhtp);
		int inst[] = od.getInstance(propsOfCage);
		for(int i=0;i<inst.length;i++){
			Properties emhtpProps = new Properties();
			long eid = ObjectNamingUtility.getEntityID(new int[]{inst[i]});
		//	emhtpProps.setProperty("neName", cageName);
			emhtpProps.setProperty("entityIdentifier", String.valueOf(eid));
			emhtpProps.setProperty("type",emhtp);
			emhtpProps.setProperty("emhName", emhName);
			if(!(!isActiveTP && ! overrideDefaultInitialStates)) // Not overriding the defalut intial states in the ObjectDetails.xml file, if it is standby EP/TP and the standby core not exists befor provisioning the cage.
			{
				overrideDefaultInitialStatesEP_TP(emhtpProps);
			}
			emhTpNames.addElement(addAnyObject(emhtpProps, propsOfCage));
			System.out.println("The EMHTP properties for "  + (String)emhTpNames.elementAt(i) +" :: "+emhtpProps);
		}
		return emhTpNames;
	}

	private void addRTM(int cageNumber,int slotNo, Properties propsOfCage, String type) throws EMHInventoryException{

		long entityId = ObjectNamingUtility.getEntityID(new int[]{cageNumber});
		Properties cageProps = ObjectNamingUtility.getDbPropsForType(entityId, cage);
		String cageName = cageProps.getProperty("name");

		Properties props = new Properties();
		long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber,slotNo});
		if (type.equals(ssc))
			props.setProperty("neName",cageName);

		props.setProperty("entityIdentifier", String.valueOf(eid));
		props.setProperty("type", rtm);

		Properties dfProps = ObjectNamingUtility.getDbPropsForType(eid, type);
		String parentName = dfProps.getProperty("name");
		props.setProperty("parentKey",parentName);

		String hardwareRevision = propsOfCage.getProperty("hardwareRevision");
		String serialNumber = propsOfCage.getProperty("serialNumber");
		String manufacturerName = propsOfCage.getProperty("manufacturerName");

		if (hardwareRevision != null) props.setProperty("hardwareRevision",hardwareRevision);
		if (serialNumber != null) props.setProperty("serialNumber",serialNumber);
		if (manufacturerName != null) props.setProperty("manufacturerName",manufacturerName);

		addAnyObject(props, propsOfCage);
	}

	private String addLAPCRG(String cageName,int cageNumber,int standbyCage,Properties propsOfCage) throws EMHInventoryException{
		Properties props = new Properties();
		long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber});
		props.put("entityIdentifier", String.valueOf(eid));
		props.put("type", lapcrg);
		props.put("preferredActiveCage", String.valueOf(cageNumber));
		props.put("preferredStandbyCage", String.valueOf(standbyCage));
		props.put("neName", cageName);
		props.put("equiped", "true");
		props.put("controlStatus", String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
		return addAnyObject(props, propsOfCage);
	}



	private void addLogicalPGsAndRGs(CageDetails cageDetails,String cageName,int cageNumber,
			int standbyCage, Properties propsOfCage) throws EMHInventoryException
			{
		int bladeToBeCutover = Integer.parseInt(propsOfCage.getProperty("noOfBlades"));
		List dataIPList = (List)propsOfCage.get("dataIPList");
		List authIPList = (List)propsOfCage.get("authIPList");
		int apcCount=1,tcCount=1,scaCount=1;

		int pgSize = cageDetails.pgList.size();
		for(int i=0;i<pgSize;i++){
			PGDetails pg = (PGDetails)cageDetails.pgList.get(i);
			Properties pgProps = new Properties();
			log("PGINSTANCE =="+String.valueOf(cageNumber)+ String.valueOf(pg.rowInstance));
			int ins = Integer.parseInt(String.valueOf(cageNumber)+ String.valueOf(pg.rowInstance));
			pgProps.put("type", pg.pgType);
			pgProps.put("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(new int[]{ins})));
			pgProps.put("neName", cageName);
			String pgName = addAnyObject(pgProps, propsOfCage);
			propsOfCage.put("Row"+(pg.rowid),pgName); //used for pam associations.
			int rgSize = pg.rgList.size();
			for(int j=0;j<rgSize;j++){
				RGDetails rg = (RGDetails)pg.rgList.get(j);
				Properties rgProps = new Properties();
				rgProps.put("type", rg.rgType);
				//the cagenumber will be populate for both cagerg and lapcrg  instance which is the parent of tc/sca and apc rgs.
				rgProps.put("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(new int[]{cageNumber,rg.instance})));
				if(rg.rgType.equals(apcrg)){
					if(cageNumber == standbyCage){
						for(int cageNo=1;cageNo<standbyCage;cageNo++){
							System.out.println("PotentialMapping  will be made for"+pgName);
							long rgEntityId = ObjectNamingUtility.getEntityID(new int[]{cageNo,rg.instance});
							Properties pgProps1 = ObjectNamingUtility.getDbPropsForType(rgEntityId,apcrg);
							String rgName = pgProps1.getProperty("name");
							addRelationShip(potential_mapping, rgName, pgName,"");
							System.err.println("PotentialMapping is successfully made for "+rgName+"-"+pgName);
						}
						continue;
					}
					rgProps.setProperty("parentKey", "/LAPCRG-"+cageNumber);//TODO get it from naming utility
				}
				else if(rg.rgType.equals(tcrg) || rg.rgType.equals(scarg)){
					rgProps.setProperty("parentKey", "/CAGERG-"+cageNumber);//TODO get it from naming utility
				}
				rgProps.put("neName", cageName);
				rgProps.setProperty("controlStatus", String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
				String rgName = addAnyObject(rgProps, propsOfCage);
				addRelationShip(potential_mapping, rgName, pgName, cageName);
				int luToBeEquiped =getNoOfLUToBeEquiped(bladeToBeCutover, rg.logicalType, i+1);
				for(int log=1;log<=rg.logicalCount;log++){
					Properties lunit = new Properties();
					lunit.setProperty("type", rg.logicalType);
					lunit.setProperty("parentKey",rgName);
					//int instance= (rg.logicalType.equals(apc))? global_APC_ID++ : ((rg.logicalType.equals(sca)) ? global_SCA_ID++ : global_TC_ID++);
					int instance = getNextLogicalUnitId(rg.logicalType,cageDetails.cageConfigType);
					if(rg.logicalType.equals(sca)){
						if(instance > 32) break; //This is to add not more than 32 SCA in an IP-BSC-DO Network.
					}
					long eid = ObjectNamingUtility.getEntityID(new int[]{cageNumber,rg.instance,instance});
					lunit.setProperty("entityIdentifier", String.valueOf(eid));
					if(luToBeEquiped >= log){
						lunit.setProperty("controlStatus", String.valueOf(StateConstantsUtil.NOT_SUSPENDED));
						lunit.setProperty("equiped","true");
					}else
					{
						lunit.setProperty("equiped","false");
					}
					int lccount= (rg.logicalType.equals(apc))?apcCount++:(rg.logicalType.equals(tc))?tcCount++:scaCount++;
					int pos = getDataAddressPossition(rg.logicalType, lccount);
					lunit.put("dataAddress", dataIPList.get(pos));
					if(rg.logicalType.equals(sca)){
						lunit.put("ccDataAddress", dataIPList.get(pos));
						lunit.put("traDataAddress", dataIPList.get(pos+1));
						lunit.put("authAddress",authIPList.get(5 + lccount));
						try {
							String neighborBscdoQuery = "select NAME from DataObject where TYPE='"+neighborBSCDOInfoListBsc+"' and PARENTKEY='"+LBSCDO_NAME+"'";
							ArrayList neighborBscdoList = CoreDBUtil.getInstance().getDataFromDB(neighborBscdoQuery);
							if(neighborBscdoList!=null && neighborBscdoList.size()>0){
								Object obj[] = (Object[])neighborBscdoList.get(0);
								if(obj!=null&&obj.length>0){
									String neighborBscdoName = String.valueOf(obj[0]);
									Properties neighborBscdoProps = new Properties();
									neighborBscdoProps.put("cc"+instance+"IPAddress" , dataIPList.get(pos));
									modifyAnyObject(neighborBscdoName, neighborBSCDOInfoListBsc,neighborBscdoProps);
								}
							}
						}catch (EMHException e) {
							throw new EMHInventoryException("Unable to Modify NeighborBscDOInfoListBsc "+e.getMessage());
						}
					}
					lunit.put("neName", cageName);
					lunit.put("cageName", cageName);
					addAnyObject(lunit, propsOfCage);

				}
			}
		}
			}


	/**
	 * List of objects that to be added during the EMH Server
	 * Startup itself (as per At Init of HRPDA Rules)
	 */
	public void addAtInit()
	{
		try {
			begin(-1);
		} catch (NotSupportedException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}
		try
		{
			Vector vec = ObjectDetailsHandler.getInstance().getObjectsAddedAtInit();
			if(vec!=null){
				Properties otherProps = new Properties();
				otherProps.put("neName", LBSCDO_NAME);
				//rightnow we don't identify any other props need by side provisioned objects
				for(int i=0;i<vec.size();i++){
					String type = vec.elementAt(i).toString();
					ObjectDetails od = ObjectDetailsHandler.getInstance().getObjectDetails(type);
					int ins = od.getInstanceCount();
					for(int j=1;j<=ins;j++){

						Properties props = new Properties();
						props.setProperty("entityIdentifier", String.valueOf(ObjectNamingUtility.getEntityID(new int[]{j})));
						props.setProperty("type", type);
						if(type.equals(lbscdo) || type.equals(hrpda)){
							props.setProperty("name","/"+type);
						}
						props.setProperty("displayName",type);
						if(type.equals(hrpda)){
							props.setProperty("neManagerLocation" , System.getProperty("aems.ip"));
						}
						addAnyObject(props, otherProps);
					}
				}
			}
			commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				rollback();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}

		}
	}

	private int getDataAddressPossition(String type,int lccount){
		if(type.equals(apc)){
			return 5 + lccount;
		}else if(type.equals(tc)){
			return 30 + lccount;
		}
		else if(type.equals(sca)){
			return 44 + (1 +((lccount-1)*2));//Assumption is each SCA there will be two ip address 1 for cc and tra
		}
		return -1;
	}

	public void addRelationShip(String relType, String source,String target,String neName) throws EMHInventoryException
	{
		relType = convertIntToStringType(relType);
		String objName = source+"_"+relType+"_"+target;
		try
		{
			if(relType==DeviceTypeConstants.active_mapping){
				deleteOldRelationObj(relType, source, target);
				try
				{
					if(topoApi.isManagedObjectPresent(objName))
					{
						EMHUtil.printOut("EMHCoreTopMgr(addRelationship) : RelationObject"+objName, Log.DEBUG);
						return;
					}
				}catch(Exception ex){
					EMHUtil.printErr("EMHCoreTopMgr(addRelationship) : Not able to fetch the RelationObject"+objName, ex);

				}

			}
			ManagedObject srcObj = topoApi.getByName(source);
			ManagedObject tgtObj = topoApi.getByName(target);
			if(srcObj == null || tgtObj == null)
			{
				EMHUtil.printOut("ERROR in creating relationship (src or tgt does not exist) for : "+source+" <"+relType+">"+target,Log.SUMMARY);
				throw new EMHInventoryException("Exception occurred while creation relationship, Please refer logs for further details",null);
			}
			RelationObject relObject = new RelationObject();
			relObject.setName(objName);
			relObject.setDisplayName(objName);
			relObject.setType(relationobject);
			relObject.setTarget(target);
			relObject.setSource(source);
			relObject.setStatusPollEnabled(false);
			relObject.setRelationship(relType);
			if(topoApi.addObject(relObject)){
				if(toBeNotified){
					notifier.notify(EMHTopoNotificationEvent.getRelationObjectAddedNotification(relObject, srcObj, tgtObj));
				}
				EMHUtil.printOut(relType+" Relationship between "+target+ " & "+source+" established",Log.DEBUG);
			}else{
				EMHUtil.printOut(relType+" Unable to add the relationship between "+target+ " & "+source,Log.DEBUG);
			}
		}
		catch(Exception e)
		{
			EMHUtil.printErr("ERROR in creating relationship for : "+source+" <"+relType+">"+target,e);
			throw new EMHInventoryException("Exception occurred while creation relationship, Please refer logs for further details",e);
		}
	}
	/**
	 * This method will delete the RelationObject if the incoming target or source had relation with different source
	 * or target respectively. Now this method is used only for active_mapping relation type.
	 */

	private void deleteOldRelationObj(String relType,String source, String target) throws EMHException
	{
		String query = "select NAME from RelationObject where RELATIONSHIP='"+relType+"' AND ((SOURCE='"+source+"' AND TARGET != '"+target+"') OR  (TARGET='"+target+"' AND SOURCE != '"+source+"'))";
		System.out.println("query to get RelationObject "+query);
		ArrayList list =CoreDBUtil.getInstance().getDataFromDB(query);
		while(!list.isEmpty()){
			Object obj[] = (Object[])list.remove(0);
			int size = obj.length;
			for(int i=0;i<size;i++)
			{
				if(obj[i]!=null){
					try{
						String relName = obj[i].toString();
						EMHUtil.printOut("EMHCoreTopoMgr : deleteOldRelationObj : object to be deleted "+relName,Log.DEBUG);
						RelationObject relObj = (RelationObject)topoApi.getByName(relName);
						topoApi.deleteObjectAndSubElements(relName);
						if(toBeNotified && relObj!=null ){
							ManagedObject srcObj = topoApi.getByName(relObj.getSource());
							ManagedObject tgtObj = topoApi.getByName(relObj.getTarget());
							notifier.notify(EMHTopoNotificationEvent.getRelationObjectDeletedNotification(relObj, srcObj, tgtObj));
						}
					}catch(Exception ex){
						throw new EMHException("Unable to detlet the relationship  :"+obj[i].toString(),ex);
					}
				}
			}
		}
	}


	public static String convertIntToStringType(String relation){
		if(relation == null) return null;
		if (relation.equals("1"))
		{
			relation = dependency;
		}
		else if (relation.equals("2"))
		{
			relation = member_of_capacity_unit;
		}
		else if (relation.equals("3"))
		{
			relation = member_of_protection_group;
		}
		else if (relation.equals("5"))
		{
			relation = potential_mapping;
		}
		else if (relation.equals("6"))
		{
			relation = active_mapping;
		}
		else if (relation.equals("7"))
		{
			relation = data_grouping;
		}
		return relation;
	}

	public void deleteAllRelationShips(String relType,String source,String target)throws EMHException{
		if(relType == null && source == null && target ==null){
			log("nothing to delete.");
			return;
		}
		relType = convertIntToStringType(relType);
		if(relType!=null && source!=null && target!=null){
			try{
				String relName = source+"_"+relType+"_"+target;
				RelationObject relObj = (RelationObject)topoApi.getByName(relName);
				log("[deleteAllRelationShips] deleting the relation ship :"+relName);
				topoApi.deleteObjectAndSubElements(relName);
				if(toBeNotified && relObj!=null){
					ManagedObject srcObj = topoApi.getByName(relObj.getSource());
					ManagedObject tgtObj = topoApi.getByName(relObj.getTarget());
					notifier.notify(EMHTopoNotificationEvent.getRelationObjectDeletedNotification(relObj, srcObj, tgtObj));
				}
			}catch(Exception ex){
				throw new EMHException("Unable to detlet the relationship  :"+source+"-"+relType+"-"+target,ex);
			}
		}
		String sep=" ";
		String query = "select NAME from RelationObject where ";
		if(relType!=null && !relType.equals("")){
			query+=" RELATIONSHIP='"+relType+"'";
			sep =" AND ";
		}
		if(source!=null && !source.equals("")){
			query+=sep+" SOURCE='"+source+"'";
			sep =" AND ";
		}
		if(target!=null && !target.equals("")){
			query+=sep+" TARGET='"+target+"'";
		}
		ArrayList list =CoreDBUtil.getInstance().getDataFromDB(query);
		while(!list.isEmpty()){
			Object obj[] = (Object[])list.remove(0);
			if(obj[0]!=null){
				try{
					RelationObject relObj = (RelationObject)topoApi.getByName(obj[0].toString());
					log("[deleteAllRelationShips] deleting the relation ship :"+obj[0]);
					topoApi.deleteObjectAndSubElements(obj[0].toString());
					if(toBeNotified && relObj != null){
						ManagedObject srcObj = topoApi.getByName(relObj.getSource());
						ManagedObject tgtObj = topoApi.getByName(relObj.getTarget());
						notifier.notify(EMHTopoNotificationEvent.getRelationObjectDeletedNotification(relObj, srcObj, tgtObj));
					}
				}catch(Exception ex){
					throw new EMHException("Unable to delete the relationship  :"+obj[0].toString(),ex);
				}
			}
		}
	}

	/**
	 * This method gets the ipaddress where the Active EMH
	 * Core is running. This address is used to determine
	 * which EMH Core is active and standby.
	 *
	 * @return ipAddress Active EMHCore ipaddress
	 * 		   null if any exception occurs
	 */
	private String getLocalIPAddress()
	{
		try
		{
			InetAddress address = InetAddress.getLocalHost();
			return InetAddress.getByName(address.getHostName()).getHostAddress();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	private void log(String msg){
		System.out.println(msg);
	}

	private Object getObjectFromClass(String className){
		try {
			Class c = Class.forName(className);
			return c.newInstance();
		} catch (Exception e) {
			EMHLogManager.logError("Unable to instantiate object "+className, e, EMHLogManager.EMH_ERR);
			e.printStackTrace();
		}
		return null;
	}

	protected Vector getRelationObjectNames(String relType, String source, String target ) throws EMHException
	{
		if(relType == null){
			throw new EMHException("Reltionship type should be specified for getting the relationship");
		}
		Properties props = new Properties();
		props.setProperty("relationship", relType);
		if(source!=null && !source.equals(""))props.setProperty("source", source);
		if(target!=null && !target.equals(""))props.setProperty("target",target);
		try {
			return topoApi.getObjectNamesWithProps(props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EMHException("Unable to get the Relation Objects.",e);
		}
	}

	private int getNoOfLUToBeEquiped(int bladeToBeCutover, String type, int rowNo)
	{
		int luToBeEquiped = bladeToBeCutover;
		try
		{
			BladeProvisioned bp = BladeProvisioned.getInstance();
			Properties equipProps = bp.getBladesToBeEquiped(bladeToBeCutover+"");

			if (type.equals("APC"))
			{
				if (rowNo == 3)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_3");
					luToBeEquiped = Integer.parseInt((String)rowList.get(0));
				}
				else if (rowNo == 4)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_4");
					luToBeEquiped = Integer.parseInt((String)rowList.get(0));
				}
			}
			else if (type.equals("SCA"))
			{
				if (rowNo == 1)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_1");
					luToBeEquiped =  Integer.parseInt((String)rowList.get(0));
				}
				else if (rowNo == 2)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_2");
					luToBeEquiped =  Integer.parseInt((String)rowList.get(0));
				}
			}
			else if (type.equals("TC"))
			{
				if (rowNo == 1)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_1");
					luToBeEquiped =  Integer.parseInt((String)rowList.get(1));
				}
				else if (rowNo == 2)
				{
					ArrayList rowList = (ArrayList)equipProps.get("ROW_2");
					luToBeEquiped =  Integer.parseInt((String)rowList.get(1));
				}
			}
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception occurred while getting the list of objects to be equiped ",e);
		}
		return luToBeEquiped;
	}

	private ArrayList getDefaultArraylist(Object defValue,int capacity){
		ArrayList list = new ArrayList(capacity);
		if(defValue !=null){
			for(int i=0;i<capacity;i++){
				list.add(defValue);
			}
		}
		return list;
	}

	private int getNextLogicalUnitId(String type,String templateType){
		if(type.equals(apc)){
			if(missing_APC_Ids !=null && missing_APC_Ids.size()>0)
			{
				int apcId = Integer.parseInt(missing_APC_Ids.remove(0).toString());
				if(templateType.equals("BETA") && apcId % 10 == 0)
					return getNextLogicalUnitId(apc,templateType);
				else
					return apcId;
			}
			else
			{
				int apcId = global_APC_ID++;
				if (templateType.equals("BETA") && apcId % 10 == 0)
					return global_APC_ID++;
				else
					return apcId;
			}
		}
		else if(type.equals(tc)){
			if(missing_TC_Ids !=null && missing_TC_Ids.size()>0){
				return Integer.parseInt(missing_TC_Ids.remove(0).toString());
			}
			else return global_TC_ID++;
		}
		else if(type.equals(sca)){
			if(missing_SCA_Ids !=null && missing_SCA_Ids.size()>0){
				return Integer.parseInt(missing_SCA_Ids.remove(0).toString());
			}
			else return global_SCA_ID++;
		}
		log("Unknown logical type "+type+" so returning -1 as instance id.");
		return -1;
	}

	private Vector getMissingIdsFor(String type) {
		try{
			String query = "select start, stop from ( "
				+" select ((m.ENTITYIDENTIFIER & 65535) + 1) as start, "
				+ " (select (min(ENTITYIDENTIFIER & 65535) - 1) from LogicalElement as x,ManagedObject"
				+ " where (x.ENTITYIDENTIFIER & 65535) > (m.ENTITYIDENTIFIER & 65535) "
				+ " and ManagedObject.NAME=x.NAME and ManagedObject.TYPE='"+type+"') as stop "
				+ " from LogicalElement m, ManagedObject mo "
				+ " where mo.NAME=m.NAME and mo.TYPE='"+type+"'"
				+ " and (m.ENTITYIDENTIFIER & 65535) + 1 not in ("
				+ " select (ENTITYIDENTIFIER & 65535) from LogicalElement , ManagedObject"
				+ " where LogicalElement.NAME=ManagedObject.NAME "
				+ " and ManagedObject.TYPE='"+type+"') order by start"
				+ ") as x";
			query += " where stop is not null"; // this will give only the hole within the added range
			ArrayList list = CoreDBUtil.getInstance().getDataFromDB(query);
			Vector vec = new Vector();
			if(list!=null){
				while(!list.isEmpty()){
					Object obj[] = (Object[])list.remove(0);
					if(obj!=null && obj.length==2 && obj[0]!=null && obj[1]!=null){
						int start = Integer.parseInt(String.valueOf(obj[0]));
						int end = Integer.parseInt(String.valueOf(obj[1]));
						for(;start<=end;start++){
							vec.addElement(String.valueOf(start));
						}
					}
				}
			}
			return vec;
		}catch(Exception ex){
			log("Error while querying for the missing seq no for :"+type);
			ex.printStackTrace();
			return new Vector();
		}
	}

	protected String checkAndAddChannelPtnList(int ownerChannel)throws EMHException
	{
		String query = "select DataObject.NAME from AttribTable,DataObject where "
			+" DataObject.NAME=AttribTable.NAME "
			+" AND AttribTable.ATTRIBNAME='ChannelPtn' "
			+" AND AttribTable.ATTRIBVALUE="+ownerChannel
			+" AND DataObject.TYPE='"+channelPtnListBsc+"'";

		ArrayList list = CoreDBUtil.getInstance().getDataFromDB(query);
		String name = null;
		if(list!=null && list.size()>0){
			Object obj[] = (Object[])list.get(0);
			if(obj!=null && obj.length >0 && obj[0]!=null){
				name = String.valueOf(obj[0]);
			}
		}
		if(name!=null){
			log("[checkAndAddChannelPtnList] Already an ChannelPtnList Object("+name+") exists for "+ownerChannel);
			return name;
		}
		//adding channelptnlist
		Properties props = new Properties();
		props.put("type", channelPtnListBsc);
		props.put("entityIdentifier", ""+ObjectNamingUtility.getEntityID(new int[]{global_channelPtn_id++}));
		props.put("ChannelPtn", String.valueOf(ownerChannel));
		props.put("bandClass",String.valueOf(ownerChannel>>16 & 65535));//bandClass is nothing but the second 16 LSB of ownerChannel
		props.put("neName", LBSCDO_NAME);
		return addAnyObject(props, props);
	}

	protected void deleteRelatedLmccdoBsc(String lmccdoName) throws EMHException
	{
		try{
			LMCCDO lmccdo = (LMCCDO)topoApi.getByName(lmccdoName);
			String oldApcName = lmccdo.getApcName();
			int bscIndex = lmccdo.getBscIndex();
			if(bscIndex!=-1 && oldApcName!=null){
				int apcId = ObjectNamingUtility.getObjectInstance(oldApcName,apc);
				if(apcId ==-1)
				{
					throw new EMHException("Unable to get the APC ID for the APC Object : "+oldApcName);
				}
				String lmccdoBscName = ObjectNamingUtility.getDbPropsForType(ObjectNamingUtility.getEntityID(new int[]{apcId,bscIndex}), lMccdoBsc).getProperty("name");
				deleteAnyObject(lmccdoBscName, lMccdoBsc);
			}
		}catch(Exception ex){
			throw new EMHException("Error while removing the related lmccdoBsc objects");
		}
	}

	public String addCallTraceObject(Properties props) throws EMHInventoryException
	{
		/*props.setProperty("name", props.getProperty("objName"));
        props.setProperty("type", calltracecontrol);
        int inst[]= (int[])props.get("instance");
        long entityIdentifier = ObjectNamingUtility.getEntityID(new int[]{inst[1],inst[2]});
        props.setProperty("entityIdentifier" , entityIdentifier+"");*/
		return addAnyObject(props, null);
	}

	public boolean register(EMHTopoActionListener listener)throws EMHException
	{
		if(listenerTable.containsKey(listener))
		{
			System.out.println(NmsUtil.GetString("Already registered the EMHTopoActionListener ")+listener+". "+NmsUtil.GetString("Cannot register."));
			return false;
		}
		EMHEventDispatcher dispatcher = new EMHEventDispatcher(listener);
		listenerTable.put(listener,dispatcher);
		notifier.addObserver(dispatcher);
		return true;
	}
	public boolean unRegister(EMHTopoActionListener listener)throws EMHException
	{
		EMHEventDispatcher dispatcher =( EMHEventDispatcher )listenerTable.remove(listener);
		notifier.deleteObserver(dispatcher);
		return true;
	}

	private void overrideDefaultInitialStatesEP_TP(Properties props)
	{
		props.setProperty("adminState","1");
		props.setProperty("opState","1");
		props.setProperty("usageState","1");
		props.setProperty("availabilityStatus","255");
		props.setProperty("controlStatus","255");
	}

	public Properties getObjectProps(String objName)
	{
		return getObjectProps(objName,ObjectNamingUtility.getObjProps(objName, ObjectNamingUtility.NB).getProperty("type"));
	}


	public Properties getObjectProps(String objName , String objType)
	{
		ObjectDetails linkObjdetails = ObjectDetailsHandler.getInstance().getObjectDetails(objType);
		Properties linkObjProps = new Properties();
		try{
			if(linkObjdetails !=  null){
				if(linkObjdetails.isManagedObject()){
					ManagedObject mObj = CoreUtil.getInstance().getTopoAPI().getByName(objName);
					if(mObj != null){
						linkObjProps = mObj.getProperties();
					}
				}
				else{
					String tableName = linkObjdetails.getTableName();
					DataObject dataObj = (DataObject)CoreUtil.getInstance().getUserStorageAPI().getObject(objName, tableName);
					if(dataObj != null){
						linkObjProps = dataObj.getProperties();
					}
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return linkObjProps;
	}

    private void updateStandbyStates()
    {
        try
        {
            Properties props = CoreUtil.getInstance().getStandbyProperties();
            String stbyName = props.getProperty("name");
            if(!CoreDBUtil.getInstance().checkStandbyAvailability())
            {
                  boolean alarmExists = CoreUtil.getInstance().isLossOfComAlarmExist(stbyName);
						System.out.println("EMHCoreTopoMgr: raiseStandbyCommLoss : standbyCore, alarmExists : " +
								stbyName + ", " + alarmExists);
						if (!alarmExists) CoreUtil.getInstance().raiseEMHCoreCommunicationLostEvent(stbyName);
            }
        }
        catch(Exception ex)
        {
            EMHUtil.printErr("Exception while updating the standby core states.",ex,Log.SUMMARY);
        }

    }
}

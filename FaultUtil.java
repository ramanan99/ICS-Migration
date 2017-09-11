//$Id: FaultUtil.java,v 1.21 2007/06/08 13:17:44 kashok Exp $
package com.motorola.emh.core.fault.utils;
//java imports
import java.util.*;

//AdventNet WebNMS imports

import com.adventnet.management.transaction.UserTransactionException;
import com.adventnet.management.scheduler.Scheduler;

import com.adventnet.nms.alertdb.AlertAPI;
import com.adventnet.nms.topodb.ManagedObject;
import com.adventnet.nms.store.NmsStorageException;
import com.adventnet.nms.util.NmsUtil;
import com.adventnet.nms.fault.FaultException;
import com.adventnet.nms.util.TimeoutException;
import com.adventnet.management.log.Log;

//EMH Core imports
import com.motorola.emh.core.modeling.EMHAlert;
import com.motorola.emh.core.modeling.EMHEvent;
import com.motorola.emh.core.util.CoreUtil;
import com.motorola.emh.core.util.CageUtil;
import com.motorola.emh.core.util.CoreDBUtil;
import com.motorola.emh.common.util.EMHUtil;

public class FaultUtil
{
	private static FaultUtil faultUtil=null;
	CageUtil cageUtil = null;
	/**
	 * Scheduler for status polling of south bound NEs.
	 */
	private Scheduler faultCorrelationScheduler;

	/**
	 * Default Constructor
	 */
	private FaultUtil()
	{
		cageUtil = CageUtil.getInstance();
	}

	/**
	 * Method to get the singleton instance of this Class.
	 * @return FaultUtil instance that is available
	 */
	public static FaultUtil getInstance()
	{
		if(faultUtil == null)
		{
			faultUtil = new FaultUtil();
		}
		return faultUtil;
	}

	/**
	 * Method to retrieve the FaultCorrelationSchedular, checks if an instance exists already
	 * else creates a new instance and returns it.
	 * @return Scheduler faultCorrelationSchedular instance.
	 */
	public Scheduler getFaultCorrelationSchedular()
	{
		if(faultCorrelationScheduler == null)
		{
			// Reads the threads.conf file to get the number of threads with which the scheduler has to be created.
			faultCorrelationScheduler = Scheduler.createScheduler("faultcorrelator");
			faultCorrelationScheduler.start();
		}
		return faultCorrelationScheduler;
	}

	/**
	 *This will collect the entities and its severity.This method will called from CoreFaultModuleListener
	 **/
	public Hashtable getEntityVsSeverity(String src1)
	{
		EMHUtil.printOut("[FaultUtil]: getEntityVsSeverity for "+src1,Log.DEBUG);
		EMHAlert emhAlert=null;
		Hashtable entityVsSeverity = new Hashtable();
		AlertAPI alertAPI = CoreUtil.getInstance().getAlertAPI();
		String[] srcArray =null;
		Vector objVector = new Vector();
		objVector = getChildObjects(src1,objVector);
		srcArray = new String[objVector.size()];
		srcArray =(String[]) (objVector.toArray(srcArray));
		Properties moProps= new Properties();
		Vector entities = null;
		int size = srcArray.length;
		for(int i=0;i<size;i++)
		{
			moProps.put("source",srcArray[i]);
			try{
				entities = alertAPI.getObjectNamesWithProps(moProps);
				EMHUtil.printOut("[FaultUtil]: getEntityVsSeverity: entities size for "+srcArray[i]+" = "+entities.size(),Log.DEBUG);
			}
			catch(NmsStorageException e )
			{
				e.printStackTrace();
			}
			catch(UserTransactionException e1)
			{
				e1.printStackTrace();
			}
			catch(java.rmi.RemoteException e2)
			{
			}

			if(entities != null)
			{
				for(int j=0;j<entities.size();j++)
				{
					String entity = entities.elementAt(j).toString();
					EMHUtil.printOut("[FaultUtil]: getEntityVsSeverity: ENTITY value"+entity,Log.DEBUG);
					System.err.println("###### Object before locking at 115 "+entity);
					try {
						emhAlert = (EMHAlert)alertAPI.checkOutIfAvailable(entity);
						System.err.println("###### Object locked at 118: "+entity);
					}
					catch(FaultException e1)
					{
						e1.printStackTrace();
					}
					catch(java.rmi.RemoteException e2)
					{
					}
					catch(TimeoutException e3)
					{
						e3.printStackTrace();
					}
					/*Added by Venkat for unlocking the object*/
					finally{
						try{ alertAPI.unlock(emhAlert);}
						catch (Exception exp){System.err.println("Exception in unlocking the alert object in FaultUtil "); exp.printStackTrace();}
					}
					System.err.println("###### Object unlocked at 136: "+entity);

					if(emhAlert != null)
					{
						int severity = emhAlert.getSeverity();
						EMHUtil.printOut("[FaultUtil]: getEntityVsSeverity: severity value "+severity,Log.DEBUG);
						entityVsSeverity.put(entity,Integer.valueOf(severity));
					}
				}

			}
		}
		return entityVsSeverity;

	}

	/**
	 * This method will be called during  Cage Alarm Resynchronization.This method will collect all the Alarm entity and
	 * severity belongs to a Cage.
	 */
	public Hashtable getEntityVsSeverityForCage(String src)
	{
		String[] sourceArray =null;
		try{
			String query = "select name from ManagedObject where nename='" +src+"'";
			//ArrayList alertInstance = CoreDBUtil.getInstance().getDataFromDB(query);

			ArrayList alertInstance = CoreDBUtil.getInstance().getSourceNamesForCageAlert(src);
		//	String lapcrg = CoreDBUtil.getInstance().getLapcRGForCage(src,true);
			alertInstance.add(src);
	/*		ArrayList alertInstance = cageUtil.getCageChildrenKeys(src , 2);
			String cageRgName = cageUtil.getCageRGForCage(src);
			alertInstance.addAll(cageUtil.getCageRGChildrenKeys(cageRgName , 2));
			String lapcRgName =  CoreDBUtil.getInstance().getLapcRGForCage(src);
			alertInstance.addAll(cageUtil.getLapcRGChildrenKeys(lapcRgName , 2));*/
			sourceArray = new String[alertInstance.size()];
			sourceArray =(String[]) (alertInstance.toArray(sourceArray));

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		EMHAlert emhAlert=null;
		Hashtable entityVsSeverity = new Hashtable();
		AlertAPI alertAPI = CoreUtil.getInstance().getAlertAPI();
		Properties moProps= new Properties();
		Vector entities = null;
		int size = sourceArray.length;
		for(int i=0;i<size;i++)
		{
			String  srcName = sourceArray[i];
			moProps.put("source",srcName);
			try{
				entities = alertAPI.getObjectNamesWithProps(moProps);
				EMHUtil.printOut("[FaultUtil]: entities size for the source "+srcName+"= "+entities.size(),Log.DEBUG);
			}
			catch(NmsStorageException e )
			{
				e.printStackTrace();
			}
			catch(UserTransactionException e1)
			{
				e1.printStackTrace();
			}
			catch(java.rmi.RemoteException e2)
			{
			}

			if(entities != null)
			{
				for(int j=0;j<entities.size();j++)
				{
					String entity = entities.elementAt(j).toString();
					EMHUtil.printOut("[FaultUtil]: getEntityVsSeverityForCage : ENTITY value"+entity,Log.DEBUG);
					System.err.println("###### Object before locking at 212: "+entity);
					try {
						emhAlert = (EMHAlert)alertAPI.checkOutIfAvailable(entity);
						System.err.println("###### Object locked at 215: "+entity);
						//alertAPI.unlock(emhAlert); //Venkat: Commented - moved to finally block
					}
					catch(FaultException e1)
					{
						e1.printStackTrace();
					}
					catch(java.rmi.RemoteException e2)
					{
					}
					catch(TimeoutException e3)
					{
						e3.printStackTrace();
					}
					/*Added by Venkat for unlocking the object*/
					finally{
						try{ alertAPI.unlock(emhAlert);}
						catch (Exception exp){System.err.println("Exception in unlocking the alert object in FaultUtil "); exp.printStackTrace();}
					}
					System.err.println("###### object unlocked at 234: "+entity);
					if(emhAlert != null)
					{
						int severity = emhAlert.getSeverity();
						EMHUtil.printOut("[FaultUtil]: getEntityVsSeverity: severity value "+severity,Log.DEBUG);
						entityVsSeverity.put(entity,Integer.valueOf(severity));
					}
				}

			}
		}
		return entityVsSeverity;

	}





	/**
	 * Clears the Alarms present in the EMH-DB and not in the device.  This method should be invoked to clear the left over      *  Alarms.  Retrieves all the left over alarms, sets the properties accordingly.  Most importantly clears these
	 set of alarms and raise this as a new Event.
	 * This method is called from CoreFaultModuleListener.
	 */
	public void constructClearEvents(Vector entities)
	{
		EMHUtil.printOut("[FaultUtil]: constructClearEvents for "+ entities,Log.DEBUG);
		AlertAPI alertAPI =(AlertAPI) NmsUtil.getAPI("AlertAPI");
		for (int i = 0; i < entities.size(); i++)
		{
			EMHUtil.printOut("[FaultUtil]: entity"+i+" = "+entities.elementAt(i),Log.DEBUG);
			EMHAlert emhAlert=null;
			EMHEvent emhEvent = new EMHEvent();
			String temp = ""; 
			try
			{
				emhAlert = (EMHAlert)alertAPI.checkOutIfAvailable((String)entities.elementAt(i));
				temp = (String)entities.elementAt(i);
				System.err.println("###### Object before locking at 272: "+temp);
				System.err.println("###### Object locked at 273: "+temp);
			//	alertAPI.unlock(emhAlert);

				if (emhAlert != null)
				{
					long time = System.currentTimeMillis();

					emhEvent.setTime(time);
					emhEvent.setSeverity(5);
					emhEvent.setText(emhAlert.getMessage());
					String category = emhAlert.getCategory();
					emhEvent.setCategory(category);
					emhEvent.setSource(emhAlert.getSource());
					emhEvent.setManagedObjectId(emhAlert.getManagedObjectId());
					emhEvent.setManagedObjectClass(emhAlert.getManagedObjectClass());
					emhEvent.setEntity(emhAlert.getEntity());
					emhEvent.setProbableCause(emhAlert.getProbableCause());
					long sp = emhAlert.getSpecificProblems();
					emhEvent.setSpecificProblems(sp);
					emhEvent.setAdditionalInformation("Clearing the Alarm by AlrmResynch Process");
					CoreUtil.getInstance().addEvent(emhEvent);
					EMHUtil.printOut("[FaultUtil]: constructClearEvents: added clearEvent = " +emhEvent.getEntity(),Log.DEBUG);

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally{
				try{ alertAPI.unlock(emhAlert);}
				catch (Exception exp){System.err.println("Exception in unlocking the alert object in FaultUtil "); exp.printStackTrace();}
			}
			System.err.println("###### object unlocked at 306: "+temp);
		}
	}

	/**
	 * This method is used to check whether the alarm exist with the given entity.
	 * @param entity Entity Value for Alarm
	 * @return a <code>boolean</code> value stating the existence of alarm with the given entity
	 */
	public boolean isAlarmExist(String entity)
	{
		boolean alarmExist = false;
		EMHAlert emhAlert = null;
		AlertAPI alertAPI = null;
		try
		{
			alertAPI =(AlertAPI) NmsUtil.getAPI("AlertAPI");
			EMHUtil.printOut("AlertAPI handle - " + alertAPI, Log.DEBUG);
			emhAlert = (EMHAlert)alertAPI.checkOutIfAvailable(entity);
			System.err.println("###### Object before locking at 325: "+emhAlert);
			System.err.println("###### Object locked at 326: "+entity);
			if(emhAlert != null)
			{
				//alertAPI.unlock(emhAlert);
				alarmExist = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try{ alertAPI.unlock(emhAlert);}
			catch (Exception exp){System.err.println("Exception in unlocking the alert object in FaultUtil "); exp.printStackTrace();}
		}
		System.err.println("###### object unlocked at 341: "+entity);
		return alarmExist;
	}
	/**
	 * Method to get the int value of severity by passing the string severity value. Supports
	 * All the Severities of Alarm any invalid string will return INFO Severity.
	 * @param stringSeverity severity string eg, Critical
	 * @return the int value for the given string severity value
	 */
	/*public static int getAlarmSeverityIntValue(String stringSeverity)
	  {
	  int toRet = 6;
	  if(stringSeverity.equalsIgnoreCase("Critical"))
	  {
	  toRet = 1;
	  }
	  else if(stringSeverity.equalsIgnoreCase("Major"))
	  {
	  toRet = 2;
	  }
	  else if(stringSeverity.equalsIgnoreCase("Minor"))
	  {
	  toRet = 3;
	  }
	  else if(stringSeverity.equalsIgnoreCase("Warning"))
	  {
	  toRet = 4;
	  }
	  else if(stringSeverity.equalsIgnoreCase("Clear"))
	  {
	  toRet = 5;
	  }
	  return toRet;
	  }*/

	/**
	 * Method to convert string represention of probable cause to int value equivalent.
	 * This is based on the RulesFile values. Not the same as it is in the MIB file definition.
	 * Currently it supports only Communications Subsystem Failure string.
	 * @param stringPC Probable Cause String value eg, Communications Subsystem Failure
	 * @return the int value for the given string probable cause value
	 */
	/*public static int getProbableCauseIntValue(String stringPC)
	  {
	  int toRet = 6;
	  if(stringPC.equalsIgnoreCase("Communications Subsystem Failure"))
	  {
	  toRet = 6;
	  }
	  return toRet;
	  }*/

	/**
	 * Method to convert the given Alert to an Event. Used for corrleation pruposes.
	 * @param alert the Alert from which Event has to be created
	 * @return The event object created using the Alert.
	 */
	public static EMHEvent createEventFromAlert(EMHAlert alert)
	{
		EMHEvent event = new EMHEvent();
		event.setSource(alert.getSource());
		event.setCorrelated(0);
		event.setSeverity(alert.getSeverity());
		event.setEntity(alert.getEntity());
		event.setText(alert.getMessage());
		event.setProbableCause(alert.getProbableCause());
		event.setSpecificProblems(alert.getSpecificProblems());
		event.setCorrelatedNotification(alert.getCorrelatedNotification());
		event.setManagedObjectClass(alert.getManagedObjectClass());
		event.setManagedObjectId(alert.getManagedObjectId());
		event.setCategory(alert.getCategory());
		event.setTime(alert.getModTime());
		event.setMode(alert.getMode());
		event.setInvokedIdentifier(alert.getInvokedIdentifier());
		event.setBackupObjectId(alert.getBackupObjectId());
		event.setThresholdLow(alert.getThresholdLow());
		event.setNotificationId(alert.getNotificationId());
		event.setUserDeviceName(alert.getUserDeviceName());
		event.setBackupObjectClass(alert.getBackupObjectClass());
		event.setAdditionalInformation(alert.getAdditionalInformation());
		event.setThresholdHigh(alert.getThresholdHigh());
		event.setTrendIndication(alert.getTrendIndication());
		//event.setAdditionalInfo(alert.getAdditionalInfo());
		event.setBackedupStatus(alert.getBackedupStatus());
		//event.setNode(alert.getNode());
		event.setThresholdUpDownIndicator(alert.getThresholdUpDownIndicator());
		//event.setTriggeredThresholdClass(alert.getTriggeredThresholdClass());
		//event.setTriggeredThresholdId(alert.getTriggeredThresholdId());
		event.setThresholdObservedValue(alert.getThresholdObservedValue());

		return event;
	}


	private Vector getChildObjects(String parentName, Vector childrenList)
	{
		try
		{
			childrenList.add(parentName);

			ManagedObject manageObject = CoreUtil.getInstance().getTopoAPI().getByName(parentName);

			if(manageObject.getIsContainer())
			{
				String[] childNames = manageObject.getChildrenKeys();

				if (childNames != null)
				{
					for (int i = 0; i < childNames.length; i++)
					{
						getChildObjects(childNames[i], childrenList); //Recursive call to get the child of the child and soon.
					}
				}
			}
		}
		catch(Exception e)
		{
			EMHUtil.printErr("[FaultUtil] : Not able to get the Child objects for "+parentName+" : " +e.getMessage(), e);
		}
		return childrenList;
	}

	/**
	 * Method to clear the EMH Platform Object alarms. This will just clear all the alarms
	 * associated the EMH Objects based on the ipAddress given.
	 * EMH Plat related objects includes EMH (Core/Mediation), EMHEP and 2 EMHTPs.
	 * @param ipAddress - address of the EMH whose associated alarms are to be cleared.
	 */
	public void clearPlatformAlarms(String ipAddress)
	{
		EMHUtil.printOut("[FaultUtil]: clearPlatformAlarms received for " + ipAddress, Log.DEBUG);
		// Clearing the EMH Object alarms. Note that currently all the alarms related to EMH are
		// generated by EMH Platform. EMH Application should not generate any alarms relating to this object
		// Otherwise those will also be cleared.
		// Using getEMHNameIfAvailable API as getting the default name would end up in clearing local object related alarms.
		String emhName = EMHUtil.getInstance().getEMHNameIfAvailable(ipAddress);
		if(emhName != null)
		{
			try
			{
				Vector outstandingAlerts = getOutstandingAlarmEntities(emhName);
				constructClearEvents(outstandingAlerts);

				// EMHEP alarms
				outstandingAlerts = getOutstandingAlarmEntities(EMHUtil.getInstance().getEMHEPName(emhName));
				constructClearEvents(outstandingAlerts);

				// EMHTP-1 alarms
				outstandingAlerts = getOutstandingAlarmEntities(EMHUtil.getInstance().getEMHTPName(emhName, 1));
				constructClearEvents(outstandingAlerts);

				// EMHTP-2 alarms
				outstandingAlerts = getOutstandingAlarmEntities(EMHUtil.getInstance().getEMHTPName(emhName, 2));
				constructClearEvents(outstandingAlerts);
			}
			catch(Exception e)
			{
				EMHUtil.printErr("[FaultUtil]: Exception while trying to Clear Platform Alarms : " +e.getMessage(), e);
			}
		}
	}

	/**
	 * Method to get the Outstanding Alarm entities against an Object.
	 * @param Vector containing the list of alarm entities against the given object.
	 */
	public Vector getOutstandingAlarmEntities(String objectName)
	{
		EMHUtil.printOut("[FaultUtil]: getOutstandingAlarmEntities for " + objectName,Log.DEBUG);
		AlertAPI alertAPI = CoreUtil.getInstance().getAlertAPI();
		Properties moProps= new Properties();
		moProps.put("source", objectName);
		Vector entities = null;
		try
		{
			entities = alertAPI.getObjectNamesWithProps(moProps);
			EMHUtil.printOut("[FaultUtil]: getOutstandingAlarmEntities : entities size for " + objectName + " = " + entities.size(),Log.DEBUG);
		}
		catch(NmsStorageException e )
		{
			e.printStackTrace();
		}
		catch(UserTransactionException e1)
		{
			e1.printStackTrace();
		}
		catch(java.rmi.RemoteException e2)
		{
		}
		return entities;
	}


}

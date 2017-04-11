package com.motorola.emh.core.server.cneomiops;


import com.adventnet.nms.mapdb.MapAPI;
import com.adventnet.nms.mapdb.MapSymbol;
import com.adventnet.nms.startnms.SocketSessionConnectionBE;
import com.adventnet.nms.startnms.MainSocketSessionBE;
import com.adventnet.nms.util.NmsUtil;
import com.adventnet.nms.topodb.TopoAPI;
import com.adventnet.management.log.Log;
import com.adventnet.management.transaction.TransactionAPI;

//motorola imports
import com.motorola.emh.common.util.EMHUtil;
import com.motorola.emh.common.exception.EMHException;
import com.motorola.emh.core.common.Msg;
import com.motorola.emh.core.common.EMHCoreClientConstants;
//import com.motorola.emh.core.server.map.EMHMapFilter;
import com.motorola.emh.core.util.CoreDBUtil;
//import com.motorola.emh.core.util.MccdoUtil;
//import com.motorola.emh.core.inventory.utils.CageDetails;
//import com.motorola.emh.core.inventory.utils.CageTemplateHandler;
//import com.motorola.emh.core.inventory.utils.GenerateNLFile;
import com.motorola.emh.core.constants.CoreConstants;
import com.motorola.emh.core.common.EMH_Msg;
import com.motorola.emh.core.inventory.EMHCoreTopoMgr;
import com.motorola.emh.core.necf.NECBDataGeneration;
import com.motorola.emh.core.util.DeviceTypeConstants;
import com.motorola.emh.core.util.CoreUtil;
//import com.motorola.emh.core.inventory.MCCDOInventoryInterface;
//import com.motorola.emh.core.inventory.MccdoSysParamGenerator;
//import com.motorola.emh.core.inventory.NeighborManagement;
//import com.motorola.emh.core.inventory.TestClass;
//import com.adventnet.nms.store.relational.RelationalAPI;
//import com.motorola.emh.core.modeling.LBSCDO;
//import com.motorola.emh.core.modeling.SBEM;
//import com.motorola.emh.core.modeling.Cage;
//import com.motorola.emh.core.necf.NECBDataGeneration;
import com.motorola.emh.common.util.ObjectNamingUtility;
//import com.motorola.emh.core.server.attredt.MccdoAttributeEdtHandler;
//import com.motorola.emh.core.server.attredt.LBSCAttributeEdtHandler;
//import com.motorola.emh.core.common.attredt.TaskDetails;
//import com.motorola.emh.core.license.EMHLicenseHandler;
//import com.motorola.emh.core.util.ProgressMonitorAdapter;

//java imports
import java.util.*;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.transaction.SystemException;

import com.motorola.emh.core.config.EMHConfigurationModule;
import com.motorola.emh.core.constants.ConfigCommandConstants;
import com.motorola.emh.core.config.EMHCoreProvUtil;
import com.motorola.emh.core.fault.ShelfInitialization;


/**
 * This class provides the implementation for using the common communication
 * to receive the requests and send responses to the client. Once registered,
 * any data from the corresponding client module will be passed to the receive()
 * method.  When the client closes the connection, the close method is called for
 * the module to clean up resources used for communicating with the client.
 *
 * This class is also setup to handle authorization for all msgs. Any msg that
 * comes down has checkAuthorization called on it based on the msgType. Internal to
 * this class is a mapping from msgType to authorization operation string. Using the
 * msgType to lookup the operation string we then make the call to check for
 * proper user authorization. Note: Anytime a new msgType is added, it must be
 * added to the mapping here so that authorization can be performed. If it isn't
 * then an exception will be thrown and an error sent back to the client.
 *
 * To implement the SocketSessionConnectionBE class we must provide the following
 *    close()
 *        Method invoked when the front end closes the connection to the BE server

 *    recieve(String sessionIdStr,byte[] data)
 *        This is method invoked when there is a request from the Front End that
 *        matches this Session ID. In our case the "EMH_CM_GUI" that we
 *        registered with the main socket session in the constructor.
 */
public class CneomiSocketSessionConnBE implements SocketSessionConnectionBE, EMHCoreClientConstants
{

	private MainSocketSessionBE mssbe=null;
	private static HashMap authorizationMap=null;
        private CoreUtil coreUtil = null;

	/**
	 * The constructor to setup this session. Registers this session with the main session
	 * server.
	 *
	 * @param mainSocketSession is the reference to the main session handler. This is used for a
	 *        variety of purposes but is mainly needed to send a response to the calling client.
	 */
	public CneomiSocketSessionConnBE(MainSocketSessionBE mainSocketSession)
	{
		mssbe=mainSocketSession;
		mssbe.registerForResponses(this,EMH_CM_GUI);
		this.setupAuthorizationMap();
		summaryLog("EMH_CORE: registering with MainSocketSession to handle request for "+EMH_CM_GUI);
                coreUtil=CoreUtil.getInstance();
	}


	// ---------------------------------------------------------------------------------------------
	/**
	 * This method is invoked when the Front End closes the connection with the
	 * Backend server. All resources used by the module for the particular Front
	 * end connection should be cleaned up when this method is invoked.
	 */
	public void close()
	{
		//as of now nothing to be done here
	}

	
	
	

	// ---------------------------------------------------------------------------
	// ********************    MSG RECEIVED HERE    ***********************
	// ---------------------------------------------------------------------------
	/**
	 * This method is invoked when there is a request from the Front End module
	 * matching the sessionIdStr.
	 * This method takes the incoming data and creates a Msg from it. Then depending
	 * on how the msg was set up it decides whether to process it synchronously or
	 * asynchronously. If synchronous it directly calls the handleMsg() method. If
	 * asynchronous then it creates a seperate thread, passes in the msg and the
	 * session Id, and then returns. That thread then calls the handle msg.
	 *
	 * This method also checks for proper user authorization.
	 *
	 * @param sessionIdStr - this is the string that the client side registered with
	 *        when it called:
	 *         PureClientUtils.commonSocket.registerForResponses(this, <sessionIdStr>);
	 *        when the client sends a message, the client base layer sticks this
	 *        string identifier in the msg and then ships it down. The backend
	 *        main socket session pulls it out and gives it to the reveive method
	 *        so we'll be able to use it to send our response back to that client
	 *        session.
	 * @param data - this is a byte array representation of the data we shipped from
	 *        the client. For the EMH_CORE classes, all msging done between client
	 *        and server uses objects extended from the base class "Msg". Because of
	 *        this we will simply use the incoming data to create an object
	 *        (deserialize the byte[]) and then cast the object as a Msg and look
	 *        in the Msg to see what type it is.
	 */
	public void receive(String sessionIdStr, byte[] data)
	{
		
		try{
			Msg incomingMsg = (Msg)(NmsUtil.deSerializeObject(data));
			CoreUtil.getInstance().addThVsClientSession(mssbe,sessionIdStr,incomingMsg);
			// Check the authorization of all incoming messages. If a new message type is added
			// then it is up to the user to add the handling of the msg type as well as
			// assigning authorization level. Add new msgtypes to the authorizationMap.
			// As of Now no authorization is used. This will be used in future if needed.
			/*       try
            {
                this.checkAuthorization(sessionIdStr, incomingMsg.msgType);
            }
            catch(EMHException aEx)
            {
                incomingMsg.error = true;
                incomingMsg.resultString = aEx.getMessage();
                sendMsgToClient(sessionIdStr, incomingMsg);
                return;
            }*/

			// We only have a 0 in the unique msg id if they user wants to be
			// processed synchronously.
			if ( incomingMsg.uniqueMsgId == UNREGISTERED_REQ_ID)
			{
				// These msgs are all sync. They are not regestered with a callback on the client side.
				debugLog("SYNC ** processing of msgType = "+incomingMsg.msgType+" uniqueReqId = "+incomingMsg.uniqueMsgId);
				handleMsg(sessionIdStr, incomingMsg);
				debugLog("SYNC ** completes for msgType = "+incomingMsg.msgType+" uniqueReqId = "+incomingMsg.uniqueMsgId);
			}
			else
			{
				// ********   THESE ARE ALL MSGs THAT WE WANT TO HANDLE ASYNC *********
				// This is an Async msg that the user sent! Create a new handler thread to
				// process the msg.
				//new AsyncMsgHandlerThread(this, sessionIdStr, incomingMsg);
				new AsyncMsgHandlerThread(sessionIdStr, incomingMsg);
				return;
			}
		}
		catch(Exception e)
		{
			//System.err.println("CneomiSocketSessionConnBE.receive() Exception at the server side " +e);
			EMHUtil.printErr("CneomiSocketSessionConnBE.receive() Exception at the server side ",e);
		}
		finally
		{
			CoreUtil.getInstance().removeClientSession();
		}
	}


	// *******************************************************************************************
	//                                   MSG PROCESSED HERE
	// *******************************************************************************************
	/**
	 * This method is called by the receive method directly for synchronous msg handling or
	 * it is called from a seperate thread for asynchronous msg handling.
	 * @param sessionIdStr - this is the string that the client side registered with
	 *        when it called:
	 *         PureClientUtils.commonSocket.registerForResponses(this, <sessionIdStr>);
	 *        when the client sends a message, the client base layer sticks this
	 *        string identifier in the msg and then ships it down. The backend
	 *        main socket session pulls it out and gives it to the reveive method
	 *        so we'll be able to use it to send our response back to that client
	 *        session.
	 * @param incomingMsg - The method is passed a msg of base type Msg that it then
	 *        casts as the appropriate type by looking in the base msg.msgType and
	 *        determining how to handle it. Once it has determined what type of msg
	 *        it is it calls then uses the msg to do whatever it was sent to do.
	 */
	public void handleMsg(String sessionIdStr,  Msg incomingMsg)
	{
		try
		{
			Msg returnMsg = null;
			boolean result = false;
			if (incomingMsg.msgType == READY)
			{
				summaryLog("EMH CORE Server: Ready signal from CM GUI CLIENT");
				return;
			}
			else if (incomingMsg.msgType == CNEOMI_OPERATION)
			{
				EMH_Msg msg = (EMH_Msg)incomingMsg;
				String configResult = "";

				Properties p =(Properties)msg.data;
				summaryLog("Cneomi Request has been received from client: "+p);

				try 
				{
					String cneomiOp = p.getProperty("cneomiop");
					final String name = p.getProperty("name");
					Properties devProps = EMHCoreProvUtil.getInstance().getPropertiesForMOName(name);

					// Get the cage name that we are going to send this message to
					String cagename = devProps.getProperty("neName");
					cagename += "/"+DeviceTypeConstants.cage+"-1";
    
					Properties cageProps = EMHCoreProvUtil.getInstance().getPropertiesForMOName(cagename);
					Properties[] cagePropsList={cageProps};
    
					Properties configProps = new Properties();
    
					// Fill in the properties for the message 
					Properties objProps = ObjectNamingUtility.getObjProps(name);
					String cneomiClass = objProps.getProperty("cneomiClass");
   
					// remove SBNE from front of name
					String sbneRegex = String.valueOf("/SBNE-\\d");
					String empty = String.valueOf("");
					String nameNoSbne = name.replaceAll(sbneRegex, empty);
 
					String corTag = String.valueOf(coreUtil.getEMHCorrelationTag(-1));
					configProps.setProperty("$OMCCorrelationTag#",corTag);
					configProps.setProperty("$OMCSequenceTag#","1");
					configProps.setProperty("$OpId#",nameNoSbne);
					configProps.setProperty("$OpClass#",cneomiClass);
    
    
					String cneomiOpVar = "";
					if( cneomiOp.equals("cneomiMiaOp"))
					{
					    summaryLog("Got operation " + cneomiOp);
					    EMHUtil.printOut("Request to send MIA to " + name,Log.SUMMARY);
					    Thread th = new Thread(new Runnable() {
					        public void run() {
					            try
					            {
					                NECBDataGeneration.getInstance().necfCompaction(name);
					            } 
					            catch(Exception e)
					            {
					                summaryLog("Caught error in NECF Compaction  while performing Config Operation");
					                e.printStackTrace();
					            }
					        }
					    });
					    th.start();

					    configResult = "OK";
					    result = true;
					}
					else 
					{
					    if( cneomiOp.equals("cneomiLockOp") ) 
					    {
					        summaryLog("Looking for MOTO_JOURNAL_CM_LOCK in Environment...");  
					        String moType = objProps.getProperty("type");
					        String journal = System.getProperty("MOTO_JOURNAL_CM_LOCK");
					        summaryLog("  MOTO_JOURNAL_CM_LOCK=" + journal + " type="+moType);  
					        if((journal != null && journal.equalsIgnoreCase("TRUE")) && 
					                (moType.equalsIgnoreCase("cnpClusterManager") || 
					                        moType.equalsIgnoreCase("cnpEmsServer")))
					        {
					            summaryLog("Cneomi Lock Request being sent as journaled.");
					            cneomiOpVar = ConfigCommandConstants.CNEOMI_LOCK;	
					        }
					        else
					        {
					            summaryLog("Cneomi Lock Request being sent as unjournaled.");
					            cneomiOpVar = ConfigCommandConstants.CNEOMI_LOCK_NON_JOURNALED;						        
					        }
					    }	
					    else if( cneomiOp.equals("cneomiUnlockOp") )
					        cneomiOpVar = ConfigCommandConstants.CNEOMI_UNLOCK;
					    else if( cneomiOp.equals("cneomiCutoverOp") )
					        cneomiOpVar = ConfigCommandConstants.CNEOMI_CUTOVER;
					    else if( cneomiOp.equals("cneomiUncutOp") )
					        cneomiOpVar = ConfigCommandConstants.CNEOMI_UNCUT;
					    else if( cneomiOp.equals("cneomiResetOp") )
					        cneomiOpVar = ConfigCommandConstants.CNEOMI_RESET;
					    else if( cneomiOp.equals("cneomiShutdownOp") )
					        cneomiOpVar = ConfigCommandConstants.CNEOMI_SHUTDOWN;

					    summaryLog("Got operation " + cneomiOpVar);

					    configResult = EMHConfigurationModule.getInstance().performConfigOperation(cneomiOpVar,cagePropsList,configProps, true);
					    result = EMHCoreProvUtil.getInstance().checkConfigResult(configResult);
					}

				}
				catch(Exception e)
				{
				    summaryLog("Caught error in performConfigOperation");
				    e.printStackTrace();
				}


				msg.setData("");
				msg.resultString = configResult;
				msg.error = result;
				returnMsg = msg;
			}

			if (returnMsg != null)
			{
				sendMsgToClient(sessionIdStr, returnMsg);
			}
		}
		catch(Exception e)
		{
			EMHUtil.printErr("CneomiSocketSessionConnBE.receive() Exception at the server side " ,e);
			incomingMsg.error = true;
			incomingMsg.resultString = getExceptionMessage(e);
			sendMsgToClient(sessionIdStr,incomingMsg);
		}
	}

	// ---------------------------------------------------------------------------------------------
	private String getExceptionMessage(Exception exp)
	{
		if(exp!=null)
		{
			if(exp.getMessage()!=null)
			{
				return exp.getMessage();
			}
			return exp.toString();
		}
		return "Unknown Exception";
	}



	// ---------------------------------------------------------------------------------------------
	/**
	 * This method is used to send responses or updates to the Front end. The
	 * sessionIdStr should be the same as what we receive for the reauest from the
	 * front_end. The sessionIdStr is a string the Front end sends to identify
	 * which session on the back end should handle the msg. All of my messaging goes to
	 * the CM GUI Client (EMH_CM_GUI).
	 */
	private void sendMsgToClient(String sessionIdStr, Msg msg)
	{
		try {
			debugLog("Sending back msg msgType = "+msg.msgType+" id = "+msg.uniqueMsgId);
			mssbe.send(EMH_CM_GUI, sessionIdStr, NmsUtil.serializeObject(msg));
		}
		catch (Exception ioex)
		{
			System.out.println("AuidoCodes: Error while sending data to client " + ioex);
		}
	}


	// ---------------------------------------------------------------------------------------------
	private void setupAuthorizationMap()
	{
		if (authorizationMap !=null)
			return;
		// Retrieve Config,Change Config,Node Maint,Reload Node and Security Config
		authorizationMap = new HashMap(32);
		//	Currently no authorisation is used in EMH. If needed this can be used.
		//                                                MSG TYPES                        OPERATION STRINGS
		//                                                *********                        *****************

	}

	// ---------------------------------------------------------------------------------------------
	/*
	 * Checks for user to have proper authoritiy to issue the request from the client.
	 */
	private void checkAuthorization(String sessionIdStr, int msgType) throws EMHException
	{
		Integer msgTypeObj = new Integer(msgType);

		if (authorizationMap.containsKey(msgTypeObj))
		{
			// Okay this was a registered msg so it has an operation string in the hashmap
			// that we can use to check to see if this user has the proper authorization
			// to perform the operation associated with this msg type.
			// First get the username and operation string
			String userName = mssbe.getUserNameForSession(sessionIdStr);
			String operationStr = (String)(authorizationMap.get(msgTypeObj));

			if (! NmsUtil.checkAuthorization(userName, operationStr))
			{
				summaryLog("EMH_CORE: MsgType "+msgType+" is NOT registered for authorization");
				throw new EMHException("Error: User "+userName+
						" is not permitted to do perform this Operation :"+operationStr);
			}
		}
		else
		{
			// This msg type wasn't registered. Maybe someone created a new msgType and forgot to add
			// it to the authoriztionMap here. Let's throw an exception to notify them.
			summaryLog("EMH_CORE: MsgType "+msgType+" is NOT registered for authorization");
			throw new EMHException("Error: MsgType "+msgType+" is NOT registered for authorization");
		}
	}
	// utility methods to log messages at summary level
	void summaryLog(String msg){
		EMHUtil.printOut(msg,Log.SUMMARY);
	}
	// utility methods to log messages at debug level
	void debugLog(String msg){
		EMHUtil.printOut(msg,Log.DEBUG);
	}
	void errorLog(String msg,Exception ex){
		EMHUtil.printErr(msg, ex);
	}


	// ***************************************************************************************
	//                                 ASYNC MSG PROCESSING
	// ***************************************************************************************
	/**
	 * This inner class is used to handle the request from the client in a
	 * asynchronous mode. This class will process each request in a seperate
	 * thread. The processing of the msg itself must decide whether to send a
	 * response back to the user. This class also handles the request that doesn't
	 * need any status or result update to the client. Again, the only catch is that it
	 * is the reponsibility of the handling of the msg to decide when to send a
	 * response and when not to.
	 */
	private class AsyncMsgHandlerThread extends Thread
	{
		private String sessionIdStr = null;
		private Msg    incomingMsg  = null;

		public AsyncMsgHandlerThread(String sessionId, Msg inMsg)
		{
			sessionIdStr = sessionId;
			incomingMsg = inMsg;
			start();
		}

		public void run()
		{
			CoreUtil.getInstance().addThVsClientSession(mssbe,sessionIdStr,incomingMsg);
			try{
				debugLog("Async ** processing of msgType = "+incomingMsg.msgType+" uniqueReqId = "+incomingMsg.uniqueMsgId);
				handleMsg(sessionIdStr, incomingMsg);
				debugLog("Async ** completes for msgType = "+incomingMsg.msgType+" uniqueReqId = "+incomingMsg.uniqueMsgId);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				CoreUtil.getInstance().removeClientSession();	
			}
		}
	} // inner class AsyncMsgHandlerThread
	// ***************************************************************************************




}

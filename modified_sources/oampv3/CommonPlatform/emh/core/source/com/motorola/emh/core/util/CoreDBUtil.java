//$Id: CoreDBUtil.java,v 1.129 2007/06/11 06:06:40 jerald Exp $
package com.motorola.emh.core.util;

//Java imports
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;

//AdventNet WebNMS imports
import com.adventnet.management.transaction.ConnectionPool;
import com.adventnet.management.transaction.PreparedStatementWrapper;
import com.adventnet.nms.store.relational.RelationalAPI;
import com.adventnet.nms.util.NmsUtil;
import com.adventnet.nms.topodb.ManagedObject;
import com.adventnet.nms.topodb.DBServer;
import com.adventnet.nms.store.relational.RelationalUtil;
import com.adventnet.management.log.Log;

//EMH common imports
import com.motorola.emh.common.util.EMHUtil;
import com.motorola.emh.common.util.EMHCommonIfc;
import com.motorola.emh.common.util.ObjectNamingUtility;
import com.motorola.emh.common.constants.DeviceConstants;
import com.motorola.emh.common.exception.EMHException;
import com.motorola.emh.common.constants.StateConstants;

//Core imports
import com.motorola.emh.core.inventory.EMHCoreTopoMgr;
import com.motorola.emh.core.inventory.IPBscDoCageInventoryInterface;
import com.motorola.emh.core.modeling.MCCDOCard;
import com.motorola.emh.core.modeling.Cage;
import com.motorola.emh.core.constants.ConfigCommandConstants;
import com.motorola.emh.core.constants.CoreConstants;
import com.motorola.emh.core.inventory.IPBscDoCageInventoryInterface;

/**
 * This Class contains database related utility methods for Core Server.
 **/

public class CoreDBUtil implements DeviceConstants
{

	//Singleton instance
	private static CoreDBUtil coreDBUtil=null;

	//RelationalAPI instance
	private RelationalAPI relapi = null;

	private int PS_FOR_GET_STANDBY_PAM_FOR_ACTIVE_PAM_ID;

	private String PS_FOR_GET_STANDBY_PAM_FOR_ACTIVE_PAM_QUERY = "select PhysicalSubUnit."+RelationalUtil.getAlias("name")
	+ " from PhysicalSubUnit where PhysicalSubUnit."+RelationalUtil.getAlias("protecting")+"='true' and PhysicalSubUnit."
	+ RelationalUtil.getAlias("name")+" in (select PAM."+RelationalUtil.getAlias("name")
	+ " from PAM where PAM."+RelationalUtil.getAlias("name")+ " in (select "+RelationalUtil.getAlias("source")
	+ " from RelationObject where "+RelationalUtil.getAlias("target")+"=(select "+RelationalUtil.getAlias("target")
	+" from RelationObject where "+RelationalUtil.getAlias("source")+" = ?)))";

	private int PS_FOR_GET_TOTAL_NE_COUNT_ID;

	private String PS_FOR_GET_TOTAL_NE_COUNT_QUERY= "select count(*) from ManagedObject where "+RelationalUtil.getAlias("type")+" = ? AND "+RelationalUtil.getAlias("statusPollEnabled")+" = 'false'";

	private int PS_FOR_GET_POLL_ENABLED_OBJ_NAMES_ID;

	private String PS_FOR_GET_POLL_ENABLED_OBJ_NAMES_QUERY= "select "+RelationalUtil.getAlias("name")+" from ManagedObject where "+RelationalUtil.getAlias("type")+" IN ('"+CAGE_TYPE+"','"+MCCDO_CARD_TYPE+"') AND "+RelationalUtil.getAlias("statusPollEnabled")+" = 'true'";


        private int PS_FOR_GET_POLL_ENABLED_CAGES_ID;
   
        private String PS_FOR_GET_POLL_ENABLED_CAGES_QUERY = 
	    "select " + RelationalUtil.getAlias("name") + "," + RelationalUtil.getAlias("ipAddress")
	    + " from ManagedObject where " + RelationalUtil.getAlias("type")
	    + " IN ('"+CAGE_TYPE+"') AND " + RelationalUtil.getAlias("statusPollEnabled")+" = 'true'";

	private int PS_FOR_GET_MED_VS_CAGE_ID;

	private String PS_FOR_GET_MED_VS_CAGE_QUERY = "select "+RelationalUtil.getAlias("name")+","+RelationalUtil.getAlias("managerIPAddress")+" from Cage ";


	private int PS_FOR_GET_MED_VS_MCCDO_ID;

	private String PS_FOR_GET_MED_VS_MCCDO_QUERY = "select "+RelationalUtil.getAlias("name")+","+RelationalUtil.getAlias("managerIPAddress")+" from MCCDOCard ";

	private int PS_FOR_GET_BALENCE_CAGE_NAMES_ID;

	private String PS_FOR_GET_BALENCE_CAGE_NAMES_QUERY="select "+RelationalUtil.getAlias("name")+" from Cage where "+RelationalUtil.getAlias("managerIPAddress")+" = ? limit ?";

	private int PS_FOR_GET_BALENCE_CAGE_NAMES_NO_LIMIT_ID;

	private String PS_FOR_GET_BALENCE_CAGE_NAMES_NO_LIMIT_QUERY="select "+RelationalUtil.getAlias("name")+" from Cage where "+RelationalUtil.getAlias("managerIPAddress")+" = ? ";

	private int PS_FOR_GET_BALENCE_MCCDO_NAMES_ID;

	private String PS_FOR_GET_BALENCE_MCCDO_NAMES_QUERY="select "+RelationalUtil.getAlias("name")+" from MCCDOCard where "+RelationalUtil.getAlias("managerIPAddress")+" = ? limit ?";

	private int PS_FOR_GET_BALENCE_MCCDO_NAMES_NO_LIMIT_ID;

	private String PS_FOR_GET_BALENCE_MCCDO_NAMES_NO_LIMIT_QUERY="select "+RelationalUtil.getAlias("name")+" from MCCDOCard where "+RelationalUtil.getAlias("managerIPAddress")+" = ? ";


	private int PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_CAGE_ID;

	private String PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_CAGE_QUERY="update Cage set "+RelationalUtil.getAlias("managerIPAddress")+" = '"+CoreConstants.UNKNOWN_MGR+"'";

	private int PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_MCCDO_ID;

	private String PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_MCCDO_QUERY="update MCCDOCard set "+RelationalUtil.getAlias("managerIPAddress")+" = '"+CoreConstants.UNKNOWN_MGR+"'";

	private int PS_FOR_UPDATE_MGR_IN_CAGE_ID;

	private String PS_FOR_UPDATE_MGR_IN_CAGE_QUERY="update Cage set "+RelationalUtil.getAlias("managerIPAddress")+" = ? where "+RelationalUtil.getAlias("name")+" = ? ";

	private int PS_FOR_UPDATE_MGR_IN_MCCDO_ID;

	private String PS_FOR_UPDATE_MGR_IN_MCCDO_QUERY="update MCCDOCard set "+RelationalUtil.getAlias("managerIPAddress")+" = ? where "+RelationalUtil.getAlias("name")+" = ? ";

	private int PS_FOR_UPDATE_STATUS_POLL_DISABLE_ID;

	private String PS_FOR_UPDATE_STATUS_POLL_DISABLE_QUERY="update ManagedObject set "+RelationalUtil.getAlias("statusPollEnabled")+" = 'false' where "+RelationalUtil.getAlias("name")+" = ? ";


	private int PS_FOR_GET_CAGE_MO_NAME_FOR_IPADDRESS_ID;

	private String PS_FOR_GET_CAGE_MO_NAME_FOR_IPADDRESS_QUERY="select "+RelationalUtil.getAlias("name")+" from Cage where "+RelationalUtil.getAlias("ipAddress")+" = ? ";


	private int PS_FOR_GET_MCCDO_MO_NAME_FOR_IPADDRESS_ID;

	private String PS_FOR_GET_MCCDO_MO_NAME_FOR_IPADDRESS_QUERY="select "+RelationalUtil.getAlias("name")+" from MCCDOCard where "+RelationalUtil.getAlias("ipAddress")+" = ? ";


	private int PS_FOR_GET_CAGE_MGR_IP_FOR_MO_ID;

	private String PS_FOR_GET_CAGE_MGR_IP_FOR_MO_QUERY ="select "+RelationalUtil.getAlias("managerIPAddress")+" from Cage where "+RelationalUtil.getAlias("name")+" = ? ";


	private int PS_FOR_GET_MCCDO_MGR_IP_FOR_MO_ID;

	private String PS_FOR_GET_MCCDO_MGR_IP_FOR_MO_QUERY="select "+RelationalUtil.getAlias("managerIPAddress")+" from MCCDOCard where "+RelationalUtil.getAlias("name")+" = ? ";


	private int PS_FOR_GET_NETWORKGTP_MO_NAME_FOR_CAGENAME_ID;

	private String PS_FOR_GET_NETWORKGTP_MO_NAME_FOR_CAGENAME_QUERY = "select " + RelationalUtil.getAlias("name") + " from ManagedObject where " + RelationalUtil.getAlias("type") + " = '" + DeviceTypeConstants.networkgtp + "' AND " + RelationalUtil.getAlias("neName")+" = ? ";

	private int PS_FOR_GET_MO_NAME_FOR_TYPE_AND_PARENT_ID;

	private String PS_FOR_GET_MO_NAME_FOR_TYPE_AND_PARENT_QUERY = "select " + RelationalUtil.getAlias("name") + " from ManagedObject where " + RelationalUtil.getAlias("type") + " = ? AND " + RelationalUtil.getAlias("parentKey") + " = ?";

	private int PS_FOR_GET_ALERT_ENTITY_FOR_MO_AND_SP_PROBLEMS_ID;

	private String PS_FOR_GET_ALERT_ENTITY_FOR_MO_AND_SP_PROBLEMS_STRING = "select " + RelationalUtil.getAlias("entity") + " from EMHAlert where " + RelationalUtil.getAlias("managedObjectId") + " = ? AND " + RelationalUtil.getAlias("specificProblems") + " = ?";

	private int PS_FOR_GET_CAGE_FOR_MCCDOCARD_ID;

	private String PS_FOR_GET_CAGE_FOR_MCCDOCARD_QUERY = "select LAPCRG." + RelationalUtil.getAlias("preferredActiveCage") +
	" from LAPCRG INNER JOIN ManagedObject rg ON LAPCRG." + RelationalUtil.getAlias("name") +
	" = rg." + RelationalUtil.getAlias("parentKey") + " INNER JOIN ManagedObject lu ON rg." +
	RelationalUtil.getAlias("name") + " = lu." + RelationalUtil.getAlias("parentKey") +
	" INNER JOIN RelationObject apcmccdoRL ON lu." + RelationalUtil.getAlias("name") +
	" = apcmccdoRL." + RelationalUtil.getAlias("target") +
	" INNER JOIN RelationObject pm ON apcmccdoRL." + RelationalUtil.getAlias("source") +
	" = pm." + RelationalUtil.getAlias("source") + " where apcmccdoRL." +
	RelationalUtil.getAlias("relationship") + " = '"+DeviceTypeConstants.data_grouping+"' AND pm." +
	RelationalUtil.getAlias("relationship") + " = '"+DeviceTypeConstants.potential_mapping+"' AND pm." +
	RelationalUtil.getAlias("target") + " = ?";

	private int PS_FOR_GET_PEER_CAGE_FOR_CAGE_ID;

	private String PS_FOR_GET_PEER_CAGE_FOR_CAGE_QUERY = "select " + RelationalUtil.getAlias("name") + " from Cage where " + RelationalUtil.getAlias("name") + " != ?";

	private int PS_FOR_GET_MCCDOCARD_FOR_CAGE_ID;

	private String PS_FOR_GET_MCCDOCARD_FOR_CAGE_QUERY = "select  MCCDOCard." + RelationalUtil.getAlias("name") +
	" from MCCDOCard INNER JOIN RelationObject rl ON MCCDOCard." + RelationalUtil.getAlias("name") + " = rl."
	+ RelationalUtil.getAlias("target") + " INNER JOIN RelationObject apcrl ON rl." +
	RelationalUtil.getAlias("source") + " = apcrl." + RelationalUtil.getAlias("source") +
	" INNER JOIN ManagedObject lu ON apcrl." + RelationalUtil.getAlias("target") +
	" = lu." + RelationalUtil.getAlias("name") + " INNER JOIN ManagedObject rg ON lu." +
	RelationalUtil.getAlias("parentKey") + " = rg." + RelationalUtil.getAlias("name") +
	" INNER JOIN LAPCRG ON rg." + RelationalUtil.getAlias("parentKey") +
	" = LAPCRG." + RelationalUtil.getAlias("name") + " where rl." + RelationalUtil.getAlias("relationship") +
	" ='"+DeviceTypeConstants.potential_mapping+"' AND apcrl." + RelationalUtil.getAlias("relationship") +
	" = '"+DeviceTypeConstants.data_grouping+"' AND (LAPCRG." + RelationalUtil.getAlias("preferredActiveCage") +
	" = ? OR LAPCRG." + RelationalUtil.getAlias("currentActiveCage") + " = ?)";

	private int PS_FOR_GET_SPAN_NUM_ID;

	private String PS_FOR_GET_SPAN_NUM_ID_QUERY = "select count(*) from ManagedObject where PARENTKEY =" + "  ? "+ " and TYPE = '"+DeviceTypeConstants.SPAN_PORT+"' " ;

	private int PS_FOR_GET_LMCCDONE_COUNT_ID;

	private String PS_FOR_GET_LMCCDONE_COUNT_ID_QUERY = "select max(ENTITYIDENTIFIER) from ManagedObject,SBNE where ManagedObject.TYPE = '"+DeviceTypeConstants.LMCCDONE+"' and ManagedObject.NAME=SBNE.NAME" ;

	private int PS_FOR_GET_EI_ID;

	private String PS_FOR_GET_EI_ID_QUERY="select " + RelationalUtil.getAlias("entityIdentifier") + " from PhysicalEntity where name  = ?" ;

	private int PS_FOR_GET_ACTIVE_MCCDO_COUNT;

	private String PS_FOR_GET_ACTIVE_MCCDO_COUNT_QUERY="select count(*) from LMCCDO";

	private int PS_FOR_GET_BSCID_ID;

	private String PS_FOR_GET_BSCID_QUERY="select BSCID from LBSCDO";

	private int PS_FOR_GET_APCPG_PCFPG_NAME_FOR_CAGENAME_ID;

	private String PS_FOR_GET_APCPG_PCFPG_NAME_FOR_CAGENAME_QUERY = "select " + RelationalUtil.getAlias("name") + " from ManagedObject where " + RelationalUtil.getAlias("neName") + " = ? AND (" + RelationalUtil.getAlias("type") + " = '"+DeviceTypeConstants.apcpg+"' OR " + RelationalUtil.getAlias("type") + " = '"+DeviceTypeConstants.pcfpg+"')";

	private int PS_FOR_GET_MCCDO_FOR_BLADE_ID;

	private String PS_FOR_GET_MCCDO_FOR_BLADE_ID_QUERY="select MCCDOCard.Name as MCCDOCARD from APC,RelationObject dg,RelationObject pm,MCCDOCard where MCCDOCard.NAME=pm.TARGET and pm.RELATIONSHIP='"+DeviceTypeConstants.potential_mapping+"' and pm.SOURCE=dg.SOURCE and dg.target=APC.NAME and APC.NAME in (select ro1.source from ManagedObject mo, RelationObject ro, RelationObject ro1 where mo.PARENTKEY=? and mo.name=ro.source and ro.target like '%/APCPG%' and ro.source=ro1.target and ro1.relationship='"+DeviceTypeConstants.active_mapping+"') and dg.relationship='"+DeviceTypeConstants.data_grouping+"'";

	private int PS_FOR_GET_APCADDRESS_ID;

	//private String PS_FOR_GET_APCADDRESS_ID_QUERY= "select APC.DATAADDRESS from APC,RelationObject dg,RelationObject pm,MCCDOCard where MCCDOCard.NAME=? and pm.RELATIONSHIP='"+DeviceTypeConstants.active_mapping+"' and pm.SOURCE=dg.SOURCE and APC.NAME=dg.TARGET and dg.RELATIONSHIP='"+DeviceTypeConstants.data_grouping+"'";

	private String PS_FOR_GET_APCADDRESS_ID_QUERY="select APC.DATAADDRESS from APC, RelationObject dg, RelationObject am where am.TARGET=? and am.SOURCE=dg.SOURCE and am.RELATIONSHIP='"+DeviceTypeConstants.active_mapping+"'and dg.RELATIONSHIP='" +DeviceTypeConstants.data_grouping+"' and dg.TARGET=APC.NAME";


	private int PS_FOR_GET_APC_RELEXPLOAD_COUNT_ID;

	private String PS_FOR_GET_APC_RELEXPLOAD_COUNT_ID_QUERY="select distinct(ManagedObject.NAME),count(MCCDOCard.NAME) CardCount, sum(MCCDOCard.RELATIVEEXPLOAD) CardLoad, ManagedObject.DISPLAYNAME from ManagedObject left join LogicalUnit lu on ManagedObject.NAME=lu.NAME left join RelationObject dgrel on ManagedObject.NAME=dgrel.TARGET left join RelationObject acrel on dgrel.SOURCE=acrel.SOURCE  left join MCCDOCard on acrel.TARGET=MCCDOCard.NAME  where ( dgrel.TARGET is null or (dgrel.TARGET is not null and dgrel.RELATIONSHIP='"+DeviceTypeConstants.data_grouping+"' and acrel.RELATIONSHIP='"+DeviceTypeConstants.active_mapping+"' and dgrel.TARGET=ManagedObject.NAME)) and ManagedObject.TYPE='APC' and lu.CONTROLSTATUS="+StateConstants.NOT_SUSPENDED+" group by ManagedObject.NAME";

	private int PS_FOR_GET_NO_APC_MCCODS_ID;

	private String PS_FOR_GET_NO_APC_MCCODS_ID_QUERY="Select " + RelationalUtil.getAlias("name") + " from MCCDOCard where " + RelationalUtil.getAlias("lmccdoname") + " not in (select distinct(" + RelationalUtil.getAlias("source") + ") from RelationObject where " + RelationalUtil.getAlias("relationship") + " ='"+DeviceTypeConstants.data_grouping+"') and " + RelationalUtil.getAlias("name") + " in (Select " + RelationalUtil.getAlias("name") + " from PhysicalUnit where " + RelationalUtil.getAlias("protecting") + " ='false')";

	private int PS_FOR_GET_MCCDOS_FOR_APC_ID;

	private String PS_FOR_GET_MCCDOS_FOR_APC_ID_QUERY="select " + RelationalUtil.getAlias("name") + " from MCCDOCard where " + RelationalUtil.getAlias("lmccdoname")  + " in ( select " + RelationalUtil.getAlias("source") + " from RelationObject where " + RelationalUtil.getAlias("target") + " =?) and " + RelationalUtil.getAlias("name") + " in (select " + RelationalUtil.getAlias("name") + " from PhysicalUnit where " + RelationalUtil.getAlias("protecting") + " ='false')";

	private String PS_FOR_STANDBY_AVAILABILITY_QUERY = "select * from BEFailOver where " + RelationalUtil.getAlias("SERVERROLE") + "='STANDBY'";


	//private constructor to restrict create multiple instance.
	private CoreDBUtil()
	{
		relapi=NmsUtil.relapi;

		PS_FOR_GET_STANDBY_PAM_FOR_ACTIVE_PAM_ID = relapi.getPreparedStatementID(PS_FOR_GET_STANDBY_PAM_FOR_ACTIVE_PAM_QUERY);

		//PS_FOR_GET_TOTAL_MCCDO_COUNT_ID=relapi.getPreparedStatementID(PS_FOR_GET_TOTAL_MCCDO_COUNT_QUERY);

		PS_FOR_GET_TOTAL_NE_COUNT_ID=relapi.getPreparedStatementID(PS_FOR_GET_TOTAL_NE_COUNT_QUERY);

		PS_FOR_GET_MED_VS_CAGE_ID=relapi.getPreparedStatementID(PS_FOR_GET_MED_VS_CAGE_QUERY);

		PS_FOR_GET_MED_VS_MCCDO_ID=relapi.getPreparedStatementID(PS_FOR_GET_MED_VS_MCCDO_QUERY);

		PS_FOR_GET_BALENCE_CAGE_NAMES_ID=relapi.getPreparedStatementID(PS_FOR_GET_BALENCE_CAGE_NAMES_QUERY);
		PS_FOR_GET_BALENCE_CAGE_NAMES_NO_LIMIT_ID=relapi.getPreparedStatementID(PS_FOR_GET_BALENCE_CAGE_NAMES_NO_LIMIT_QUERY);

		PS_FOR_GET_BALENCE_MCCDO_NAMES_ID=relapi.getPreparedStatementID(PS_FOR_GET_BALENCE_MCCDO_NAMES_QUERY);

		PS_FOR_GET_BALENCE_MCCDO_NAMES_NO_LIMIT_ID=relapi.getPreparedStatementID(PS_FOR_GET_BALENCE_MCCDO_NAMES_NO_LIMIT_QUERY);

		PS_FOR_UPDATE_MGR_IN_CAGE_ID=relapi.getPreparedStatementID(PS_FOR_UPDATE_MGR_IN_CAGE_QUERY);

		PS_FOR_UPDATE_MGR_IN_MCCDO_ID=relapi.getPreparedStatementID(PS_FOR_UPDATE_MGR_IN_MCCDO_QUERY);

		PS_FOR_UPDATE_STATUS_POLL_DISABLE_ID=relapi.getPreparedStatementID(PS_FOR_UPDATE_STATUS_POLL_DISABLE_QUERY);
		PS_FOR_GET_POLL_ENABLED_OBJ_NAMES_ID=relapi.getPreparedStatementID(PS_FOR_GET_POLL_ENABLED_OBJ_NAMES_QUERY);
		PS_FOR_GET_POLL_ENABLED_CAGES_ID = relapi.getPreparedStatementID(PS_FOR_GET_POLL_ENABLED_CAGES_QUERY);

		PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_CAGE_ID=relapi.getPreparedStatementID(PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_CAGE_QUERY);

		PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_MCCDO_ID=relapi.getPreparedStatementID(PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_MCCDO_QUERY);

		//PS_UPDATE_COLLECTION_TIME_MCCDO_ID=relapi.getPreparedStatementID(PS_UPDATE_COLLECTION_TIME_MCCDO_QUERY);

		//PS_UPDATE_COLLECTION_TIME_CAGE_ID=relapi.getPreparedStatementID(PS_UPDATE_COLLECTION_TIME_CAGE_QUERY);
		PS_FOR_GET_CAGE_MO_NAME_FOR_IPADDRESS_ID=relapi.getPreparedStatementID(PS_FOR_GET_CAGE_MO_NAME_FOR_IPADDRESS_QUERY);

		PS_FOR_GET_MCCDO_MO_NAME_FOR_IPADDRESS_ID=relapi.getPreparedStatementID(PS_FOR_GET_MCCDO_MO_NAME_FOR_IPADDRESS_QUERY);


		PS_FOR_GET_CAGE_MGR_IP_FOR_MO_ID=relapi.getPreparedStatementID(PS_FOR_GET_CAGE_MGR_IP_FOR_MO_QUERY);

		PS_FOR_GET_MCCDO_MGR_IP_FOR_MO_ID=relapi.getPreparedStatementID(PS_FOR_GET_MCCDO_MGR_IP_FOR_MO_QUERY);
//		PS_FOR_GET_CAGE_DETAILS_ID=relapi.getPreparedStatementID(PS_FOR_GET_CAGE_DETAILS_QUERY);
		PS_FOR_GET_NETWORKGTP_MO_NAME_FOR_CAGENAME_ID=relapi.getPreparedStatementID(PS_FOR_GET_NETWORKGTP_MO_NAME_FOR_CAGENAME_QUERY);

		PS_FOR_GET_MO_NAME_FOR_TYPE_AND_PARENT_ID = relapi.getPreparedStatementID(PS_FOR_GET_MO_NAME_FOR_TYPE_AND_PARENT_QUERY);

		PS_FOR_GET_ALERT_ENTITY_FOR_MO_AND_SP_PROBLEMS_ID = relapi.getPreparedStatementID(PS_FOR_GET_ALERT_ENTITY_FOR_MO_AND_SP_PROBLEMS_STRING);

		PS_FOR_GET_CAGE_FOR_MCCDOCARD_ID = relapi.getPreparedStatementID(PS_FOR_GET_CAGE_FOR_MCCDOCARD_QUERY);
		PS_FOR_GET_PEER_CAGE_FOR_CAGE_ID = relapi.getPreparedStatementID(PS_FOR_GET_PEER_CAGE_FOR_CAGE_QUERY);
		PS_FOR_GET_MCCDOCARD_FOR_CAGE_ID = relapi.getPreparedStatementID(PS_FOR_GET_MCCDOCARD_FOR_CAGE_QUERY);

		//PS_FOR_GET_APC_ID=relapi.getPreparedStatementID(PS_FOR_GET_APC_QUERY);

		/*		PS_FOR_GET_APC_COUNT_ID=relapi.getPreparedStatementID(PS_FOR_GET_APC_COUNT_QUERY);

		PS_FOR_GET_APCID_FOR_MCCDO_ID=relapi.getPreparedStatementID(PS_FOR_GET_APCID_FOR_MCCDO_QUERY);
		 */
		PS_FOR_GET_SPAN_NUM_ID=relapi.getPreparedStatementID(PS_FOR_GET_SPAN_NUM_ID_QUERY);

		PS_FOR_GET_LMCCDONE_COUNT_ID=relapi.getPreparedStatementID(PS_FOR_GET_LMCCDONE_COUNT_ID_QUERY);

		//PS_FOR_GET_ALL_INSTALLED_SW_VERSION_ID = relapi.getPreparedStatementID(PS_FOR_GET_ALL_INSTALLED_SW_VERSION_QUERY);
		PS_FOR_GET_EI_ID= relapi.getPreparedStatementID(PS_FOR_GET_EI_ID_QUERY);
		//PS_FOR_GET_STANDBYMCCDO_ID= relapi.getPreparedStatementID(PS_FOR_GET_STANDBYMCCDO_ID_QUERY);
		PS_FOR_GET_ACTIVE_MCCDO_COUNT = relapi.getPreparedStatementID(PS_FOR_GET_ACTIVE_MCCDO_COUNT_QUERY);

		PS_FOR_GET_BSCID_ID=relapi.getPreparedStatementID(PS_FOR_GET_BSCID_QUERY);

		PS_FOR_GET_APCPG_PCFPG_NAME_FOR_CAGENAME_ID = relapi.getPreparedStatementID(PS_FOR_GET_APCPG_PCFPG_NAME_FOR_CAGENAME_QUERY);

		PS_FOR_GET_MCCDO_FOR_BLADE_ID=relapi.getPreparedStatementID(PS_FOR_GET_MCCDO_FOR_BLADE_ID_QUERY);

		PS_FOR_GET_APCADDRESS_ID=relapi.getPreparedStatementID(PS_FOR_GET_APCADDRESS_ID_QUERY);
		PS_FOR_GET_APC_RELEXPLOAD_COUNT_ID=relapi.getPreparedStatementID(PS_FOR_GET_APC_RELEXPLOAD_COUNT_ID_QUERY);
		PS_FOR_GET_MCCDOS_FOR_APC_ID=relapi.getPreparedStatementID(PS_FOR_GET_MCCDOS_FOR_APC_ID_QUERY);
		PS_FOR_GET_NO_APC_MCCODS_ID=relapi.getPreparedStatementID(PS_FOR_GET_NO_APC_MCCODS_ID_QUERY);
		//PS_FOR_STANDBY_AVAILABILITY_ID = relapi.getPreparedStatementID(PS_FOR_STANDBY_AVAILABILITY_QUERY);
	}

	/**
	 * This method used to get the instance of the CoreDBUtil.
	 * @return instance of {@link CoreDBUtil}
	 */
	public static CoreDBUtil getInstance()
	{
		if(coreDBUtil==null)
		{
			coreDBUtil=new CoreDBUtil();
		}
		return coreDBUtil;
	}

	/**
	 * Used to get Total NE Count.
	 * @param devType - is CoreConstants.LB_MCCDO_TYPE (or) LB_CAGE_TYPE
	 * @return total NE count for the device type
	 */
	public int getTotalNECount(int devType)
	{
		int count=0;
		PreparedStatementWrapper psw=null;
		psw = relapi.fetchPreparedStatement(PS_FOR_GET_TOTAL_NE_COUNT_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1, (devType==CoreConstants.LB_MCCDO_TYPE)?MCCDO_CARD_TYPE:CAGE_TYPE);
			rs = ps.executeQuery();
			while(rs.next())
			{
				count=rs.getInt(1);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return count;
	}

	/**
	 * Used to get MediationId Vs ManagedObject mapping.
	 * @param devType - device type ie, CoreConstants.LB_MCCDO_TYPE (or) LB_CAGE_TYPE
	 * @return mapping of Mediation Name Vs Device List
	 */
	public Hashtable getMediationVsObjects(int devType)
	{
		Hashtable medVsObjNames=new Hashtable();

		ArrayList exceptMoList=getStatusPollEnabledObjNames();
		if(devType==CoreConstants.LB_CAGE_TYPE)
		{
			// Get Object Names from CAGE

			EMHUtil.printOut("StatusPollEnabled MCCDO and Cage MO Names : "+exceptMoList,Log.DEBUG);
			PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MED_VS_CAGE_ID);
			PreparedStatement ps = psw.getPreparedStatement();
			ResultSet rs = null;
			try
			{
				rs = ps.executeQuery();
				while(rs.next())
				{
					String name=rs.getString(1);
					if(exceptMoList.contains(name))
					{
						EMHUtil.printOut("Reject StatusPollEnabled Name from the getMediationVsObjects() list :"+name,Log.DEBUG);
						continue;
					}
					String mediationId=rs.getString(2);
					if(EMHUtil.getInstance().isNull(mediationId))
					{
						mediationId=CoreConstants.UNKNOWN_MGR;
					}
					ArrayList moNameList=(ArrayList)medVsObjNames.get(mediationId);
					if(moNameList==null)
					{
						moNameList=new ArrayList();
						medVsObjNames.put(mediationId,moNameList);
					}
					moNameList.add(name);
				}
			}catch(SQLException sqle)
			{
				sqle.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs != null)
					{
						rs.close();
					}
				}catch(SQLException sqle){}
				relapi.returnPreparedStatement(psw);
			}
		}
		else
		{

			// Get Object Names from MCCDO CARD
			PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MED_VS_MCCDO_ID);
			PreparedStatement ps = psw.getPreparedStatement();
			ResultSet rs = null;
			try
			{
				rs = ps.executeQuery();
				while(rs.next())
				{
					String name=rs.getString(1);
					if(exceptMoList.contains(name))
					{
						EMHUtil.printOut("Reject StatusPollEnabled Name from the getMediationVsObjects() list :"+name,Log.DEBUG);
						continue;
					}
					String mediationId=rs.getString(2);
					if(EMHUtil.getInstance().isNull(mediationId))
					{
						mediationId="UNKNOWN";
					}
					ArrayList moNameList=(ArrayList)medVsObjNames.get(mediationId);
					if(moNameList==null)
					{
						moNameList=new ArrayList();
						medVsObjNames.put(mediationId,moNameList);
					}
					moNameList.add(name);
				}
			}catch(SQLException sqle)
			{
				sqle.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs != null)
					{
						rs.close();
					}
				}catch(SQLException sqle){}
				relapi.returnPreparedStatement(psw);
			}
		}
		return medVsObjNames;
	}


	/**
	 * Used to get All the object Names which are assigned to the given mediationId.
	 * @param mediationId - mediation id
	 * @param devType - device type CoreConstants.LB_CAGE_TYPE (or) CoreConstants.LB_MCCDO_TYPE
	 * @return array list of object names
	 */
	public ArrayList getObjectNames(String mediationId,int devType)
	{
		return getObjectNames(mediationId,-1,devType);
	}


	/**
	 * Used to get the specified number of object Names which are assigned to the given mediationId.
	 * @param mediationId - mediation id
	 * @param noOfObjects - number of objects retrived from the mediation
	 * @param devType - device type CoreConstants.LB_MCCDO_TYPE (or) LB_CAGE_TYPE
	 * @return array list of object names
	 */
	public ArrayList getObjectNames(String mediationId,int noOfObjects,int devType)
	{
		ArrayList moNames = new ArrayList();
		PreparedStatementWrapper psw=null;
		if(devType==CoreConstants.LB_MCCDO_TYPE)
		{
			//GET MCCDO
			if(noOfObjects!=-1)
			{
				psw= relapi.fetchPreparedStatement(PS_FOR_GET_BALENCE_MCCDO_NAMES_ID);
			}
			else
			{
				psw= relapi.fetchPreparedStatement(PS_FOR_GET_BALENCE_MCCDO_NAMES_NO_LIMIT_ID);
			}
			PreparedStatement ps = psw.getPreparedStatement();
			ResultSet rs = null;
			try
			{
				ps.setString(1,mediationId);
				if(noOfObjects!=-1)
				{
					ps.setInt(2,noOfObjects);
				}
				rs = ps.executeQuery();
				while(rs.next())
				{
					moNames.add(rs.getString(1));
				}
			}catch(SQLException sqle)
			{
				sqle.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs != null)
					{
						rs.close();
					}
				}catch(SQLException sqle){}
				relapi.returnPreparedStatement(psw);
			}
		}else
		{
			//GET CAGE
			int balenceCount=noOfObjects-moNames.size();
			psw = relapi.fetchPreparedStatement(PS_FOR_GET_BALENCE_CAGE_NAMES_ID);

			if(noOfObjects!=-1)
			{
				psw= relapi.fetchPreparedStatement(PS_FOR_GET_BALENCE_CAGE_NAMES_ID);
			}
			else
			{
				psw= relapi.fetchPreparedStatement(PS_FOR_GET_BALENCE_CAGE_NAMES_NO_LIMIT_ID);
			}
			PreparedStatement ps = psw.getPreparedStatement();
			ResultSet rs = null;
			try
			{
				ps.setString(1,mediationId);
				if(noOfObjects!=-1)
				{
					ps.setInt(2,balenceCount);
				}
				rs = ps.executeQuery();
				while(rs.next())
				{
					moNames.add(rs.getString(1));
				}
			}catch(SQLException sqle)
			{
				sqle.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs != null)
					{
						rs.close();
					}
				}catch(SQLException sqle){}
				relapi.returnPreparedStatement(psw);
			}
		}
		return moNames;
	}

	public ArrayList getMccdoForBLADE(String bladeName)
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MCCDO_FOR_BLADE_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		ArrayList arrayOfMccdo = new ArrayList();
		String mccdoName=null;

		try
		{
			EMHUtil.printOut(" BLADE Name ::>>> . "+bladeName ,Log.DEBUG);
			{
				ps.setString(1,bladeName);
				rs = ps.executeQuery();
				while(rs.next())
				{
					mccdoName=rs.getString(1);
					arrayOfMccdo.add(mccdoName);
				}
			}
			EMHUtil.printOut("Vector of Mccdo :;>>>> . "+arrayOfMccdo,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return arrayOfMccdo;
	}
	/**
	 * Used to get Managed Object Names with the sepecified type and parentKey.
	 * @param type - managedobject type
	 * @param parentKey - managedobject parent key
	 * @return array list of object names
	 */
	public ArrayList getObjectNames(String type, String parentKey)
	{
		ArrayList moNameList=new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MO_NAME_FOR_TYPE_AND_PARENT_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,type);
			ps.setString(2,parentKey);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String name=rs.getString(1);
				moNameList.add(name);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return moNameList;
	}

	/**
	 * Used to disable Status poll of the given ManagedObject.
	 * @param moName - managed object name
	 * @return result of the operation
	 */
	private boolean disableStatusPoll(String moName)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_UPDATE_STATUS_POLL_DISABLE_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		try
		{
			ps.setString(1, moName);
			ps.execute();
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
			return false;
		}
		finally
		{
			relapi.returnPreparedStatement(psw);
		}
		return true;
	}

	/**
	 * Used to Bulk update Manager IPaddress as UNKNOWN.
	 **/
	public void bulkUpdateDeviceManagerAsUnknown()
	{
		//CAGE Update
		PreparedStatementWrapper cpsw = relapi.fetchPreparedStatement(PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_CAGE_ID);
		PreparedStatement cps = cpsw.getPreparedStatement();
		try
		{
			cps.execute();
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			relapi.returnPreparedStatement(cpsw);
		}

		//MCCDO Update
		PreparedStatementWrapper mpsw = relapi.fetchPreparedStatement(PS_FOR_BULK_UPDATE_MGR_AS_UNKNOWN_IN_MCCDO_ID);
		PreparedStatement mps = mpsw.getPreparedStatement();
		try
		{
			mps.execute();
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			relapi.returnPreparedStatement(mpsw);
		}

		//clear the memory because of bulk update.
		//DBServer.comapi.clearMemory();
	}

	/**
	 * Used to update Manager IPaddress for the given moList.
	 * @param moList - managed object list
	 * @param mediationId - mediation Id
	 * @param isCallFromLoadBalence - whether this method is called from Load balance of not
	 * @return
	 */
	public boolean updateDeviceManager(ArrayList moList,String mediationId,boolean isCallFromLoadBalence)
	{
		boolean overAllResult=true;
		int size=moList.size();
		for (int i=0;i<size;i++)
		{
			ManagedObject mo=(ManagedObject)moList.get(i);
			String moName=mo.getName();
			if(mo instanceof MCCDOCard)
			{
				boolean result=updateDeviceManager(PS_FOR_UPDATE_MGR_IN_MCCDO_ID,mediationId,moName);
				if(result && !isCallFromLoadBalence)
				{
					result=disableStatusPoll(moName);
				}
				if(result)
				{
					// Update in memory
					MCCDOCard card=(MCCDOCard)mo;
					card.setManagerIPAddress(mediationId);
					if(!isCallFromLoadBalence)
					{
						card.setStatusPollEnabled(false);
					}
					//if(DBServer.comapi.isObjectInMemory(card.getKey()))
				//	{
						DBServer.comapi.updateObjectInMemoryOnly(card);
					//}
				}
				else
				{
					overAllResult=false;
				}
			}
			else if(mo instanceof Cage)
			{

				boolean result=updateDeviceManager(PS_FOR_UPDATE_MGR_IN_CAGE_ID,mediationId,moName);
				if(result && !isCallFromLoadBalence)
				{
					result=disableStatusPoll(moName);
				}
				if(result)
				{
					// Update in memory
					Cage cage=(Cage)mo;
					cage.setManagerIPAddress(mediationId);
					if(!isCallFromLoadBalence)
					{
						cage.setStatusPollEnabled(false);
					}
					//if(DBServer.comapi.isObjectInMemory(cage.getKey()))
					//{
						DBServer.comapi.updateObjectInMemoryOnly(cage);
					//}
				}
				else
				{
					overAllResult=false;
				}
			}
		}
		return overAllResult;
	}


	/**
	 * Used to update Manager IPaddress for the given ManagedObject.
	 * @param psId - prepared statement id
	 * @param medId - mediation id
	 * @param name - name of the MO
	 * @return result of the operation
	 */
	private boolean updateDeviceManager(int psId,String medId,String name)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(psId);
		PreparedStatement ps = psw.getPreparedStatement();
		try
		{
			ps.setString(1, medId);
			ps.setString(2, name);
			ps.execute();
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
			return false;
		}
		finally
		{
			relapi.returnPreparedStatement(psw);
		}
		return true;
	}

	/**
	 * Used to get StatusPollEnabled Object Names.
	 * @return array list of status polled enabled mo names
	 */
	private ArrayList getStatusPollEnabledObjNames()
	{
		ArrayList moNameList=new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_POLL_ENABLED_OBJ_NAMES_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			rs = ps.executeQuery();
			while(rs.next())
			{
				String name=rs.getString(1);
				moNameList.add(name);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return moNameList;
	}


	/**
	 * This method is used to getMoName for given IpAddress.
	 * @param ipAddress - ip Address of the MO
	 * @return name of the MO (matching with ipaddress), return null, if no match found.
	 */
	public String getMONameForIpAddress(String ipAddress)
	{
        EMHCommonIfc commonIfc = EMHUtil.getInstance().getEMHCommonIfc();
        return commonIfc.getMOName(ipAddress);

// 		String moName=null; //getMCCDOCardNameForIpAddress(ipAddress);
// 		if(moName==null)
// 		{
// 			moName=getCageNameForIpAddress(ipAddress);
// 		}
// 		// This is to make sure that the Standby Core Address is handled in the Core.
// 		if(moName == null)
// 		{
// 			moName = getCoreNameForIpAddress(ipAddress);
// 		}
// 		// This is to make sure that the Mediation Address is handled in the Core.
// 		if(moName == null)
// 		{
// 			moName = getMediationNameForIpAddress(ipAddress);
// 		}
// 		return moName;
	}

	/**
	 * This method is used to get MCCDOCard Name for given IpAddress.
	 * @param ipAddress - ip Address of MCCDoCard
	 * @return name of the MCCDOCard for given ipAddress
	 */
	public String getMCCDOCardNameForIpAddress(String ipAddress)
	{
		return querySingleValue(PS_FOR_GET_MCCDO_MO_NAME_FOR_IPADDRESS_ID,ipAddress);
	}

	/**
	 * This method is used to get CageName for given IpAddress.
	 * @param ipAddress - ip Address of Cage
	 * @return name of the Cage for give ipAddress
	 */
	public String getCageNameForIpAddress(String ipAddress)
	{
		return querySingleValue(PS_FOR_GET_CAGE_MO_NAME_FOR_IPADDRESS_ID,ipAddress);
	}

	/**
	 * This method is used to getMoName for given IpAddress.
	 * @param psId - prepared statement id
	 * @param arg - value
	 * @return return query values
	 */
	private String querySingleValue(int psId,String arg)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(psId);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,arg);
			rs = ps.executeQuery();
			while(rs.next())
			{
				return rs.getString(1);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return null;
	}

	/**
	 * This method is used to get Manager ipAddress for given IpAddress.
	 * @param moName - mo name
	 * @return manager ipAddress
	 */
	public String getManagerIpAddress(String moName)
	{
		String mgrIp=querySingleValue(PS_FOR_GET_MCCDO_MGR_IP_FOR_MO_ID,moName);
		if(mgrIp==null)
		{
			mgrIp=querySingleValue(PS_FOR_GET_CAGE_MGR_IP_FOR_MO_ID,moName);
		}
		return mgrIp;
	}



	/**
	 * This method is used to get NetworkGTP Names for the given Cage
	 * @param cageName - cage name
	 * @return - array list of network GTP names
	 */
	public ArrayList getNetworkGTPNames(String cageName)
	{
		ArrayList networkGTPNameList=new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_NETWORKGTP_MO_NAME_FOR_CAGENAME_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,cageName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String name=rs.getString(1);
				networkGTPNameList.add(name);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return networkGTPNameList;
	}

	/**
	 * This method is used to check whether an alarm exists for the given moname & specific problems.
	 * @param moName - mo name
	 * @param specificProblems - specific problem
	 * @return true, if exists. otherwise return false.
	 */
	public boolean isAlarmExist(String moName, int specificProblems)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_ALERT_ENTITY_FOR_MO_AND_SP_PROBLEMS_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,moName);
			ps.setInt(2, specificProblems);
			rs = ps.executeQuery();
			return rs.next();
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return false;
	}

	/**
	 * This method is used to get the peer LBSC Cage Name for the given MCCDOCard Name.
	 * @param cardName MCCDOCard Name
	 * @return Peer LBSC Cage Name
	 */
	public String getPeerCageForMCCDOCard(String cardName)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_CAGE_FOR_MCCDOCARD_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,cardName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				//return rs.getString(1);
				int cageId = rs.getInt(1);
				int ids[] = {cageId};
				long entityId = ObjectNamingUtility.getEntityID(ids);
				return ObjectNamingUtility.getIDForType(entityId, DeviceTypeConstants.cage, ObjectNamingUtility.NB);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return null;
	}

	/**
	 * This method is used to get the peer LBSC Cage Names for the given LBSC Cage Name.
	 * @param cageName LBSC Cage Name
	 * @return List of peer LBSC Cage Names
	 */
	public ArrayList getPeerCageNamesForCage(String cageName)
	{
		ArrayList peerCageNames = new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_PEER_CAGE_FOR_CAGE_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,cageName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String peerCageName = rs.getString(1);
				peerCageNames.add(peerCageName);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return peerCageNames;
	}

	/**
	 * This method is used to get the subtending MCCDOCards for the given LBSC Cage Name.
	 * @param cageName LBSC Cage Name
	 * @return List of subtending MCCDOCard Names
	 */
	public ArrayList getSubtendingMCCDOCardsForCage(String cageName)
	{
		ArrayList subtendingCards = new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MCCDOCARD_FOR_CAGE_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			int cageId = ObjectNamingUtility.getObjectInstance(cageName, DeviceTypeConstants.cage);
			ps.setInt(1,cageId);
			ps.setInt(2, cageId);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String cardName = rs.getString(1);
				subtendingCards.add(cardName);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return subtendingCards;
	}


	public int getSpanCount(String mccdoName)
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_SPAN_NUM_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		int count=0;

		try
		{
			EMHUtil.printOut(" MCCDO Name ::>>> . "+mccdoName ,Log.DEBUG);
			{
				ps.setString(1,mccdoName);
				rs = ps.executeQuery();
				while(rs.next())
				{
					count=rs.getInt(1);
				}
			}
			EMHUtil.printOut("SpanCount :;>>>> . "+count,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}

		return count;
	}

	//TODO check whether this method is needed.
	public long getEntityIdentifier(String mccdoName)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_EI_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		long count=0;

		try
		{
			EMHUtil.printOut(" MCCDO Name ::>>> . "+mccdoName ,Log.DEBUG);
			{
				ps.setString(1,mccdoName);
				rs = ps.executeQuery();
				while(rs.next())
				{
					count=rs.getLong(1);
				}
			}
			EMHUtil.printOut("EntityIdentifier :;>>>> . "+count,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}

		return count;
	}


	private String bscId="-1";

	public String getBSCID()
	{
		if(bscId!=null && !bscId.trim().equals("") && !bscId.equals("-1"))
		{
			return bscId;
		}
		else
		{
			PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_BSCID_ID);
			PreparedStatement ps = psw.getPreparedStatement();
			ResultSet rs=null;
			try
			{
				rs = ps.executeQuery();
				while(rs.next())
				{
					bscId=rs.getString(1);
				}
				EMHUtil.printOut("BSC ID :;>>>> . "+bscId,Log.DEBUG);
			}catch(SQLException sqle)
			{
				sqle.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs != null)
					{
						rs.close();
					}
				}catch(SQLException sqle){}
				relapi.returnPreparedStatement(psw);
			}

			return bscId;
		}
	}

	/**
	 * Get the Standby PAM name for the given PAM name
	 *
	 * @param activePAMName Name of the PAM
	 *
	 * @return pamName Name of the Standby PAM
	 * 		   null if no Standby PAM
	 */
	public String getStandbyPAM(String activePAMName)
	{
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_STANDBY_PAM_FOR_ACTIVE_PAM_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		String pamName = null;

		try
		{
			ps.setString(1,activePAMName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				pamName = rs.getString(1);
			}
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}
			catch(SQLException sqle)
			{}
			relapi.returnPreparedStatement(psw);
		}
		return pamName;
	}


	/**
	 * Method to retrieve a relationship property from the database by passing the required values.
	 * @param propToGet the property to get
	 * @param propToUse the property to looked up
	 * @param moName the moName property with which the relationship has to be looked up
	 * @param relationshipType the relationshipType which has to looked up
	 * @return Vector if strings containing the property values requested
	 */
	public Vector getRelationShipProperty(String propToGet, String propToUse, String moName, String relationshipType)
	{
		Vector toRet = null;
		String query = "select " + RelationalUtil.getAlias(propToGet) + " from RelationObject where " + RelationalUtil.getAlias(propToUse) + " = '" + moName + "' AND " + RelationalUtil.getAlias("relationship") + " = '" + relationshipType + "'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			System.out.println("The query is " + query);
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);
			while(rs.next())
			{
				if(toRet == null)
				{
					toRet = new Vector();
				}
				toRet.addElement(rs.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}

			}catch(SQLException sqle)
			{
			}
			try
			{
				if(ps != null)
				{
					ps.close();
				}

			}catch(SQLException sqle1)
			{
			}
		}

		/*PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_RELATIONSHIP_PROPERTY_ID);
        PreparedStatement ps = psw.getPreparedStatement();
        ResultSet rs = null;
        try
        {
            ps.setString(1, propToGet);
            ps.setString(2, propToUse);
            ps.setString(3, moName);
            ps.setString(4, relationshipType);
            rs = ps.executeQuery();
            while(rs.next())
            {
				if(toRet == null)
				{
					toRet = new Vector();
				}
                toRet.addElement(rs.getString(1));
            }
        }catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }

        finally
        {
            try
            {
                if(rs != null)
                {
                    rs.close();
                }
            }catch(SQLException sqle)
            {
			}
            relapi.returnPreparedStatement(psw);
        }*/
		return toRet;
	}


	/**
	 *Method to get the total number of LMCCDONE in the EMH database.
	 *
	 * return int (number of lmccdone)
	 */


	public int getLmccdoneCount()
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_LMCCDONE_COUNT_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		int count=0;

		try
		{
			{
				rs = ps.executeQuery();
				while(rs.next())
				{
					count=rs.getInt(1);
				}
			}
			EMHUtil.printOut("LMCCDONE Count :;>>>> . "+count,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}

		return count;

	}

	public int getActiveMccdoCount()
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_ACTIVE_MCCDO_COUNT);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		int count=0;

		try
		{
			{
				rs = ps.executeQuery();
				while(rs.next())
				{
					count=rs.getInt(1);
				}
			}
			EMHUtil.printOut(" ACTIVE MCCDO Count :;>>>> . "+count,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}

		return count;

	}

	/*
	 * method to get the TIMEZONE value stored in the table LBSCDO
	 */
	public String getLBSCDOTimeZone() throws EMHException {
		ResultSet rs = null;
		Statement st = null;
		String lbscdoTimeZone = null;
		String query = "Select TIMEZONE from LBSCDO";
		try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if (rs.next()){
				lbscdoTimeZone = rs.getString("TIMEZONE");
			}
		}catch(Exception e){
			throw new EMHException("Unable to execute the Query : " + query, e);
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
			}
		}
		return lbscdoTimeZone;
	}

	/**
	 * Method to get Any Data stored in LBSCDO table.
	 * @param colmnName -Name of attribute for which the value is required.
	 * @return value of the requested attribute.
	 */
	public String getDataFromLBSCDO(String colmnName) throws EMHException {
		ResultSet rs = null;
		Statement st = null;
		String lbscdoData = "";
		String query = "Select "+colmnName+" from LBSCDO";
		try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if (rs.next()){
				lbscdoData = rs.getString(colmnName);
			}
		}catch(Exception e){
			throw new EMHException("Unable to execute the Query : " + query, e);
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
			}
		}
		return lbscdoData;
	}


	/**
	 * returns a Properties object containing a Vector of all installed software packages
	 * and the current software package installed.
	 */
	public Properties getInstalledSwPackageProps() {

		Properties swPackageProps = new Properties();

		try
		{
			String allInstalledSwPackages = getAllInstalledSwPackages();
			String currSwPackage = CoreUtil.getInstance().getActiveCoreRunningSoftwareVersion();
			//TODO check if we can remove the below code with the method "String[] getInstalledSwPackages()" for the ALL_SW_PACKAGES.
			Vector swPackageVec = new Vector();
			String delim = ",";
			int delimIndex = allInstalledSwPackages.indexOf(delim);
			while (delimIndex > 0) {
				String curPackage = allInstalledSwPackages.substring(0, delimIndex);
				swPackageVec.add(curPackage);
				allInstalledSwPackages = allInstalledSwPackages.substring(delimIndex+1);
				delimIndex = allInstalledSwPackages.indexOf(delim);
			}
			if (allInstalledSwPackages.length() > 0) swPackageVec.add(allInstalledSwPackages);

			int swPackagesSize = swPackageVec.size();
			String swPackageArr[] = new String[swPackagesSize];
			for (int i = 0; i < swPackagesSize; i++) {
				swPackageArr[i] = (String) swPackageVec.elementAt(i);
			}
			swPackageProps.put("ALL_SW_PACKAGES", swPackageArr);
			swPackageProps.put("CURRENT_SW_PACKAGE", currSwPackage);
			EMHUtil.printOut("SBEM - All Installed Software Packages Details -  " + swPackageProps, Log.DEBUG);

		}
		catch (Exception e)
		{
			EMHUtil.printErr("Exception when getting the all installed software package Props", e, Log.SUMMARY);
		}

		return swPackageProps;
	}

	/**
	 * Method to get the allInstalledSwPackages attribute of the SBEM object.
	 * @return String - Comma separated  list of software version
	 */
	public String getAllInstalledSwPackages()
	{
		String allInstalledSwPackages = EMHUtil.getInstance().getAllInstalledSwPackages();
		updateAllInstalledSwPackages(allInstalledSwPackages);
		EMHUtil.printOut("SBEM - All Installed Software Packages -  " + allInstalledSwPackages, Log.DEBUG);
		return allInstalledSwPackages;
	}

	/**
	 * Method to update the allInstalledSoftwarePackages of the SBEM object
	 * @param swInfo -
	 *            The string object that contains the data that should be
	 *            updated
	 */
	public void updateAllInstalledSwPackages(String allInstalledInfoToUpdate)
        {
            try
            {
                if( EMHCoreTopoMgr.HRPDA_NAME != null)
                {
                    Properties changedProps = new Properties();
                    changedProps.setProperty("allInstalledSwPackages", allInstalledInfoToUpdate);
                    EMHCoreTopoMgr.getInstance().modifyAnyObject(EMHCoreTopoMgr.HRPDA_NAME, DeviceTypeConstants.hrpda, changedProps);
                }
                else
                {
                    EMHUtil.printOut("HRPDA name not available. Not updating the AllInstalledSwPackages of SBEM", Log.SUMMARY);
                }
            }
            catch (Exception e)
            {
                EMHUtil.printErr("Exception when updating allInstalledSwPackages in SBEM", e, Log.SUMMARY);
            }
        }


	/**
	 * This method fetches the name of LAPCRG associated with the given CageName.
	 * @param cageName Name of the Cage for which LAPCRG Name is required.
	 * @param currentActive If true the LAPCRG name of the CurrentActive cage will be returned
	 *                      else LAPCRG name of the PreferredActive cage will be returned.
	 * @return  LAPCRG name associated with given cagename.
	 * Returns a null value if there is no LAPCRG object corresponding to the given cage
	 */
	public String getLapcRGForCage( String cageName , boolean currentActive)
	{
		String PS_FOR_GET_LAPCRG_FOR_CAGE_QUERY = "";
		int ids[] = ObjectNamingUtility.getIdsFromOrl(cageName, DeviceTypeConstants.cage);
		int cageNumber = ids[3];

		if(currentActive)
		{
			PS_FOR_GET_LAPCRG_FOR_CAGE_QUERY = "select LAPCRG." + RelationalUtil.getAlias("name")+" from LAPCRG where " + RelationalUtil.getAlias("currentActiveCage") + " = " + cageNumber;
		}
		else
		{
			PS_FOR_GET_LAPCRG_FOR_CAGE_QUERY = "select LAPCRG." +RelationalUtil.getAlias("name")+" from LAPCRG where " + RelationalUtil.getAlias("preferredActiveCage") + " = " + cageNumber;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		String lapcRGName = null;

		try
		{
			ps = relapi.getPreparedStatement(PS_FOR_GET_LAPCRG_FOR_CAGE_QUERY);
			rs = ps.executeQuery();

			while (rs.next())
			{
				lapcRGName = rs.getString(1);
			}
		}
		catch (Exception sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException sqle)
			{
			}
			try{if(ps!=null) ps.close();}catch(Exception e){}
		}
		return lapcRGName;
	}

	/**
	 * This method fetches the name of LAPCRG associated with the given CageName.
	 * @param cageName Name of the Cage for which LAPCRG Name is required.
	 * @return  LAPCRG name associated with given cagename.
	 * Returns a null value if there is no LAPCRG object corresponding to the given cage
	 */
	public String getLapcRGForCage(String cageName )
	{
		String lapcRGName = getLapcRGForCage(cageName , true);
		if(lapcRGName == null)
		{
			lapcRGName = getLapcRGForCage(cageName , false);
		}
		return lapcRGName;
	}

	/**
	 * A Generic method that fetches the data by executing the given query returns an array list
	 * @param query SQL query to execute.
	 * @return  ArrayList. This list consist of an Object Array which represents
	 * a row returned by the sql query.
	 * Returns an empty list if no data retrived from the databse.
	 * @throws EMHException if there is any error in executing the SQL Query.
	 */

	public ArrayList getDataFromDB(String query)throws EMHException{
		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Statement st = null;
		try{
			st = relapi.query(query,true);
			rs = st.getResultSet();
			int colCount = rs.getMetaData().getColumnCount();
			while(rs.next()){
				Object obj[] = new Object[colCount];
				for(int i=0;i<obj.length;i++){
					obj[i] = rs.getObject(i+1);
				}
				list.add(obj);
			}
		}catch(Exception e){
			throw new EMHException("Unable to execute the Query :"+query,e);
		}finally{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException sqle)
			{
			}
			try
			{
				if (st != null)
				{
					st.close();
				}
			}
			catch (SQLException sqle)
			{
			}
		}
		return list;
	}

	/**
	 * This method is used to get APCPG and PCFPG Names for the given Cage
	 * @param cageName - cage name
	 * @return - array list of APCPG and PCFPG names
	 */
	public ArrayList getAPCAndPCFPGNames(String cageName)
	{
		ArrayList apcAndPCFPGNameList = new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_APCPG_PCFPG_NAME_FOR_CAGENAME_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			ps.setString(1,cageName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String name = rs.getString(1);
				apcAndPCFPGNameList.add(name);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return apcAndPCFPGNameList;
	}

	/**
	 * This method is used to get the PAM count with the given state and value
	 * in the specified Protection Group.
	 * @param pgName - Protection Group Name
	 * @param stateName - PAM State Name
	 * @param stateValue - Value for the defined state
	 * @return - PAM count
	 */
	public int getPAMCountInPG(String pgName, String stateName, int stateValue)
	{
		String query = " select count(PhysicalEntity.NAME) from PhysicalEntity "
		 +" INNER JOIN ManagedObject on PhysicalEntity.NAME=ManagedObject.NAME"
		 +" INNER JOIN RelationObject on RelationObject.SOURCE=ManagedObject.NAME"
		 +" where RelationObject.TARGET='"+ pgName +"' AND "
		 +" RelationObject.RELATIONSHIP='"+DeviceTypeConstants.member_of_protection_group+"' AND "
		 +" PhysicalEntity."+RelationalUtil.getAlias(stateName)+" ='" + stateValue+ "'";

		ResultSet rs = null;
		Statement st = null;
		int count = 0;
		try
		{
			st = relapi.query(query, true);
			rs = st.getResultSet();
			if(rs.next())
			{
				count = rs.getInt(1);
			}
		}catch(Exception sqle)
		{
			EMHUtil.printErr("Error in query :"+query, sqle);
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			try
			{
				if(st != null)
				{
					st.close();
				}
			}catch(SQLException sqle){}
		}
		return count;
	}

	/**
	 * This method is used to get the LogicalUnit count with the given state and value
	 * within the potentially mapped resource group for the specified Protection Group.
	 * @param pgName - Protection Group Name
	 * @param stateName - LogicalUnit State Name
	 * @param stateValue - Value for the defined state
	 * @return - LogicalUnit count
	 */
	public int getLogicalUnitCountForPG(String pgName, String stateName, int stateValue)
	{
		String query ="select count(LogicalElement.NAME) from LogicalElement "
			+ " INNER JOIN ManagedObject on LogicalElement.NAME=ManagedObject.NAME "
			+ " INNER JOIN RelationObject on RelationObject.SOURCE=ManagedObject.PARENTKEY "
			+ " where RelationObject.TARGET='"+ pgName +"' AND "
			+ " RelationObject.RELATIONSHIP='"+DeviceTypeConstants.potential_mapping+"' AND "
			+ " LogicalElement."+RelationalUtil.getAlias(stateName)+" ='" + stateValue+ "'";

		int count = 0;
		ResultSet rs = null;
		Statement st = null;
		try
		{
			st = relapi.query(query, true);
			rs = st.getResultSet();
			if(rs.next())
			{
				count = rs.getInt(1);
			}
		}catch(Exception sqle)
		{
			EMHUtil.printErr("Error in query :"+query, sqle);
		}
		finally
		{

			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			try
			{
				if(st != null)
				{
					st.close();
				}
			}catch(SQLException sqle){}
		}
		return count;
	}

	public String getAPCAddress(String mccdoName)
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_APCADDRESS_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		String apcAddress=null;

		try
		{
			EMHUtil.printOut(" MCCDO Name ::>>> . "+mccdoName ,Log.DEBUG);
			{
				ps.setString(1,mccdoName);
				rs = ps.executeQuery();
				while(rs.next())
				{
					apcAddress=rs.getString(1);
				}
			}
			EMHUtil.printOut("APCAddress :;>>>> . "+apcAddress,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}

		return apcAddress;

	}

	public Properties getChangedSPAProps(String cards[]){
		Properties dbProps = new Properties();
		if(cards == null || cards.length == 0) return dbProps;
		String query = "select PROPNAME,PROPVAL from mccdospa where NAME in (";
		String sep="";
		for(int i=0;i<cards.length;i++){
			query = query + sep + "'" + cards[i]+"'";
			sep=",";
		}
		query+=")";
		ResultSet rs = null;
		Statement st = null;
		try{
			st = relapi.query(query, true);
			rs = st.getResultSet();
			while(rs.next()){
				dbProps.put(rs.getString(1), rs.getString(2));
			}
		}catch(Exception ex){
			EMHUtil.printErr("Error while executing the query : "+query, ex);
		}
		finally{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException sqle)
			{
			}
			try
			{
				if (st != null)
				{
					st.close();
				}
			}
			catch (SQLException sqle)
			{
			}
		}
		return dbProps;
	}

	/**
	 * Used to get Down Devices list among the neNames devices which needs to send to mediation.
	 * @param moProps - moProps which needs to be check whether the device is down or not.
	 * @return - down device lists
	 */
	public ArrayList getDownDeviceList(ArrayList moProps)
	{
		ArrayList downList=new ArrayList();
		int size=moProps.size();
		if(size==0)
		{
			return downList;
		}
		try{
			int inLimit=100;
			int noOfItr=size/inLimit;
			int balance=size%inLimit;
			EMHUtil.printOut("getDownDeviceList() = inLimit="+inLimit+"; noOfItr="+noOfItr+"; balance="+balance,Log.DEBUG);
			int index=0;
			for(int i=0;i<noOfItr;i++)
			{
				String query="select "+RelationalUtil.getAlias("source")+" from Alert where "+RelationalUtil.getAlias("entity")+" in ( ";
				for(int j=0;j<inLimit;j++)
				{
					query+="'"+((Properties)moProps.get(index++)).getProperty("name")+"-commAlm'";
					if(j!=(inLimit-1))
					{
						query+=",";
					}
				}
				query+=" )";
				try{
					ArrayList result=getDataFromDB(query);
					if(result.size()!=0)
					{
						Object[] names=(Object[])result.get(0);
						for(int k=0;k<names.length;k++)
						{
							downList.add(names[k]);
						}
					}
				}catch(EMHException e)
				{
					e.printStackTrace();
				}

			}
			String query="select "+RelationalUtil.getAlias("source")+" from Alert where "+RelationalUtil.getAlias("entity")+" in ( ";
			for(int i=0;i<balance;i++)
			{
				query+="'"+((Properties)moProps.get(index++)).getProperty("name")+"-commAlm'";
				if(i!=(balance-1))
				{
					query+=",";
				}
			}
			query+=" )";
			try{
				ArrayList result=getDataFromDB(query);
				if(result.size()!=0)
				{
					Object[] names=(Object[])result.get(0);
					for(int k=0;k<names.length;k++)
					{
						downList.add(names[k]);
					}
				}
			}catch(EMHException e)
			{
				e.printStackTrace();
			}
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}
		return downList;
	}

	public Hashtable getApcNameMccdoCountAndLoad()
	{

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_APC_RELEXPLOAD_COUNT_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		Hashtable apcHash= new Hashtable();

		try
		{
			rs = ps.executeQuery();

			while(rs.next()){
				Properties apcNCR=new Properties();
				String apcName=rs.getString(1);
				String apcCount=rs.getString(2);
				float relExpCount=rs.getFloat(3);
				apcNCR.setProperty("mccdoCount",apcCount);
				apcNCR.put("mccdoLoad",relExpCount+"");
				apcHash.put(apcName,apcNCR);
			}
			EMHUtil.printOut("APCNameCount And RelExpCount :;>>>> . "+apcHash,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return apcHash;
	}


	public Vector getUnAllocatedMccdos() {
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_NO_APC_MCCODS_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		Vector unallocatedMccdos = new Vector();

		try
		{
			rs = ps.executeQuery();

			while(rs.next()){
				String mccdoName=rs.getString(1);
				unallocatedMccdos.add(mccdoName);
			}
			EMHUtil.printOut("Unallocated MCCDOs :;>>>> . " + unallocatedMccdos,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return unallocatedMccdos;
	}

	public Vector getMccdosForApc (String apcName) {
		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_MCCDOS_FOR_APC_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs=null;
		Vector mccdos = new Vector();

		try
		{
			ps.setString(1, apcName);
			rs = ps.executeQuery();

			while(rs.next()){
				String mccdoName=rs.getString(1);
				mccdos.add(mccdoName);
			}
			EMHUtil.printOut("MCCDOs for APC \" " + apcName + "\" :;>>>> . "+ mccdos,Log.DEBUG);
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return mccdos;
	}

	/**
	 * Used to get Core Name for given ipaddress
	 * @param ipAddress - ipaddress
	 * @return core name , if corename not found return null
	 */
	public String getCoreNameForIpAddress(String ipAddress)
	{
		String coreName=null;
		ResultSet rs=null;
		Statement st=null;
		try{
			String query="select "+RelationalUtil.getAlias("name")+" from EMHCore where "+RelationalUtil.getAlias("ipAddress")+" = '"+ipAddress+"'";
			st = relapi.query(query,true);
			rs = st.getResultSet();
			while(rs.next())
			{
				coreName=rs.getString(1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{if(st!=null) st.close();}catch(Exception e){}
			try{if(rs!=null) rs.close();}catch(Exception e){}
		}
		return coreName;
	}

	/**
	 * Used to get Mediation Object Name for given ipaddress
	 * @param ipAddress - ipaddress
	 * @return Mediation name for the given address, if not found return null
	 */
	public String getMediationNameForIpAddress(String ipAddress)
	{
		String medName=null;
		ResultSet rs=null;
		Statement st=null;
		try
		{
			String query = "select "+RelationalUtil.getAlias("name")+" from EMHMediation where "+RelationalUtil.getAlias("ipAddress")+" = '"+ipAddress+"'";
			st = relapi.query(query,true);
			rs = st.getResultSet();
			while(rs.next())
			{
				medName=rs.getString(1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{if(st!=null) st.close();}catch(Exception e){}
			try{if(rs!=null) rs.close();}catch(Exception e){}
		}
		return medName;
	}

	/**
	 * Method to check if the Standby EMH Core server is INS or not
	 * @return true - If Standby is INS, false otherwise.
	 */
	public boolean isStandbyInservice()
	{
		boolean inService = false;
		//ResultSet rs = null;
		try
		{
			String query = "select "+RelationalUtil.getAlias("HOSTADDRESS")+" from BEFailOver where "+RelationalUtil.getAlias("SERVERROLE")+" = 'STANDBY'";
			ArrayList standbyList = getDataFromDB(query);
			if ( standbyList.size() > 0 )
				inService = true;
		}
		catch(Exception e)
		{
			inService = false;
			EMHUtil.printErr("Exception when checking if Standby Core server exists", e, Log.SUMMARY);
		}
		EMHUtil.printOut("Is the standby core in server - "+inService, Log.DEBUG);
		return inService;
	}

	public ArrayList getSourceNamesForCageAlert(String cageName)
	{
		String sbneName = IPBscDoCageInventoryInterface.getInstance().getSBNENameFromCageName(cageName);
		ArrayList objName = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			String query = "select name from ManagedObject where nename='" + sbneName +"'";
			System.out.println("The query is " + query);
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);
			while(rs.next())
			{
				objName.add(rs.getString(1));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			try{if(ps!=null) ps.close();}catch(Exception e){}
		}

		return objName;
	}

	public String getLinkObjectName(String type,String parentKey, int instance)
	{
		String objName = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			String query = "select NAME from DataObject where TYPE='"+type + "' and (ENTITYIDENTIFIER & 65535)="+instance+" and PARENTKEY='"+parentKey+"'";
			//System.out.println("The query is " + query);
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);
			while(rs.next())
			{
				objName = rs.getString(1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			try{if(ps!=null) ps.close();}catch(Exception e){}
		}

		return objName;
	}


	public String getHRPDAID()
	{
		String hrpdaID = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			String query = "Select "+RelationalUtil.getAlias("name") +" from SBEM limit 1";
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);
			while(rs.next())
			{
				hrpdaID = rs.getString(1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			try{if(ps!=null) ps.close();}catch(Exception e){}
		}
		return hrpdaID;
	}

	public void updateEMHRunningSwVersion(String version)
	{
		try
		{
			String query = "UPDATE EMHCore SET "+RelationalUtil.getAlias("RUNNINGSWVERSION")+" = '"+version+"'";
			NmsUtil.relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(query));
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when updating the EMH running software version - "+version, e, Log.SUMMARY);
		}
	}

	public void updateEMHSwUpgradeState(int value)
	{
		String coreQuery = "update EMHCore set "+RelationalUtil.getAlias("SWUPGRADESTATE")+" = '"+value+"'";
		String medQuery = "update EMHMediation set "+RelationalUtil.getAlias("SWUPGRADESTATE")+" = '"+value+"'";
		try
		{
			EMHUtil.printOut("Updating the EMH software upgrade state to : "+value, Log.SUMMARY);
			NmsUtil.relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(coreQuery));
			NmsUtil.relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(medQuery));
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when updating the Software upgrade state of EMH : "+value, e, Log.SUMMARY);
		}
	}


    public void updateEMHCoreSwUpgradeState(int value)  //TODO need to replace the above and this method with some generic one.
    {
        String coreQuery = "update EMHCore set "+RelationalUtil.getAlias("SWUPGRADESTATE")+" = '"+value+"'";
        try
		{
			EMHUtil.printOut("Updating the EMH Core software upgrade state to : "+value, Log.SUMMARY);
			NmsUtil.relapi.executeUpdate(NmsUtil.relapi.getPreparedStatement(coreQuery));
        }
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when updating the Software upgrade state of EMH Core  : "+value, e, Log.SUMMARY);
		}
    }

	public int getEMHSwUpgradeState()
	{
		int swUpgradeState = CoreConstants.READY;
		String query = "select "+RelationalUtil.getAlias("SWUPGRADESTATE")+" from EMHCore";
		ResultSet rs = null;
		Statement st = null;
		try
		{
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if(rs.next())
				swUpgradeState = rs.getInt(1);
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when getting the EMH software upgrade state", e, Log.SUMMARY);
		}
		finally
		{
			try{if(st!=null) st.close();}catch(Exception e){}
			try{if(rs!=null) rs.close();}catch(Exception e){}
		}
		EMHUtil.printOut("The software upgrade state of EMHCore returned is - "+swUpgradeState, Log.DEBUG);
		return swUpgradeState;
	}

	public boolean makeNewUpgradeEntry(String fromVer, String toVer, String mode, String status)
	{
		try
		{
			String delQuery = "delete from EMHUpgradeInfo";
			EMHUtil.printOut("Query to delete entries in EMHUpgradeInfo - "+delQuery, Log.DEBUG);
			NmsUtil.relapi.execute(delQuery);
			String insertQuery = "insert into EMHUpgradeInfo values ('"+ fromVer +"', '"+ toVer +"', '"+ mode +"', '"+ status +"')";
			EMHUtil.printOut("Query to insert entries in EMHUpgradeInfo - "+insertQuery, Log.DEBUG);
			NmsUtil.relapi.execute(insertQuery);
			return true;
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when updating EMHUpgradeInfo table", e, Log.SUMMARY);
			return false;
		}
	}

	public boolean updateUpgradeEntry(String key, String value)
	{
		try
		{
			String updateQuery = "update EMHUpgradeInfo set "+ key +" = '"+ value +"'";
			EMHUtil.printOut("Query to update entry in EMHUpgradeInfo - "+updateQuery, Log.DEBUG);
			NmsUtil.relapi.execute(updateQuery);
			return true;
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when updating EMHUpgradeInfo table", e, Log.SUMMARY);
			return false;
		}
	}

	public String getUpgradeInfoValue(String key)
	{
		String result = "";
		String query = "select "+ key +" from EMHUpgradeInfo";
		ResultSet rs = null;
		Statement st=null;
		try
		{
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if(rs.next())
				result = rs.getString(1);
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when getting the value for "+ key +" from EMHUpgradeInfo table", e, Log.SUMMARY);
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					EMHUtil.printErr("Exception when closing the result set in CoreDBUtil - getUpgradeInfoValue", e, Log.SUMMARY);
				}
			}
			try{if(st!=null) st.close();}catch(Exception e){}
		}
		EMHUtil.printOut("The value of "+key+" in table EMHUpgradeInfo is - "+result, Log.DEBUG);
		return result;
	}

	public boolean isUpgradeInProgress()
	{
		boolean upgradeInProgress = false;
		String result = "";
		String query = "select STATUS from EMHUpgradeInfo";
		ResultSet rs = null;
		Statement st=null;
		try
		{
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if(rs.next())
				result = rs.getString(1);
			if (result.equals(ConfigCommandConstants.UPGRADE_INPROGRESS))
				upgradeInProgress = true;
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception when checking if EMH upgrade is in progress", e, Log.SUMMARY);
			upgradeInProgress = false;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					EMHUtil.printErr("Exception when closing the result set in CoreDBUtil - isUpgradeInProgress", e, Log.SUMMARY);
				}
			}
			try{if(st!=null) st.close();}catch(Exception e){}
		}
		EMHUtil.printOut("Is EMH Upgrade in progress - return value - "+upgradeInProgress, Log.DEBUG);
		return upgradeInProgress;
	}

	public ArrayList getBscIndexList(String apcName)
	{
		ArrayList al=new ArrayList();
		String query = "select bscIndex from LMCCDO where APCNAME = '"+apcName+"'" ;
		PreparedStatement ps=null;
		ResultSet rs = null;
		try
		{
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);

			while(rs.next())
			{
				al.add( rs.getString(1));
			}
		}
		catch(Exception e)
		{
			EMHUtil.printErr("Exception while getting bscIndex ", e, Log.SUMMARY);
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					EMHUtil.printErr("Exception when closing the result set in CoreDBUtil - ", e, Log.SUMMARY);
				}
			}
			try{if(ps!=null) ps.close();}catch(Exception e){}
		}
		System.out.println(" BSCINDEX returned from CoreDBUtil " + al);
		return al;
	}


	/**
	 * This method used to fetch the CPObjectnames corresponding to the given object type.
	 * @param type The type of the object for which objectNames are to be fecthed.
	 * @return ArrayList The names of the objects for which states are to be fecthed.
	 */
	public ArrayList getCPObjectNames(String type )
	{
		ArrayList objectNames = new ArrayList();
		String query = "select " + RelationalUtil.getAlias("name") +" from DataObject where " + RelationalUtil.getAlias("type")+" ='" + type + "'";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = relapi.getConnection().prepareStatement(query);
			rs = relapi.executeQuery(ps);
			while(rs.next())
			{
				objectNames.add( rs.getString(1));
			}
		}
		catch(Exception sqle)
		{
			EMHUtil.printErr("Exception while fetching the Data from " + type + " Database  " , sqle , Log.SUMMARY);
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}

			}catch(SQLException sqle)
			{
			}
			try
			{
				if(ps != null)
				{
					ps.close();
				}

			}catch(SQLException sqle1)
			{
			}
		}

		return objectNames;

	}

	/**
	 * Used to fetch the properties of the TransportConfigBsc object for the given name.
	 * @param name - name of the TransportConfigBsc object.
	 * @return - Properties of the TransportConfigBsc Object.
	 */
	public Properties getTransportConfigBscProps(String name){
		Properties dbProps = new Properties();

		String query = "select AUTHNETWORKADDRESS, AUTHNETMASK , DATANETWORKADDRESS , DATANETMASK from TransportConfigBsc where NAME ='"+name+"'";

		ResultSet rs = null;
		Statement st = null;
		try{
			st = relapi.query(query, true);
			rs = st.getResultSet();
			while(rs.next()){
				dbProps.put("authNetworkAddress", rs.getString(1));
				dbProps.put("authNetmask", rs.getString(2));
				dbProps.put("dataNetworkAddress", rs.getString(3));
				dbProps.put("dataNetmask", rs.getString(4));
			}
		}catch(Exception ex){
			EMHUtil.printErr("Error while executing the query : "+query, ex);
		}
		finally{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException sqle)
			{
			}
			try
			{
				if (st != null)
				{
					st.close();
				}
			}
			catch (SQLException sqle)
			{
			}
		}
		return dbProps;
	}

	/**
	 * Used to fetch the properties of the NetworkTPPool object.
	 * @return - Properties of the TransportConfigBsc Object.
	 */
	public Properties getNetworkTPPoolProps(){
		Properties dbProps = new Properties();

		String query = "select OMVLAN, AUTHENTICATIONVLAN, DATAVLAN, OMNETWORKADDRESS, OMNETMASK, OMDEFAULTGATEWAY, AUTHDEFAULTGATEWAY, DATADEFAULTGATEWAY from NetworkTPPool limit 1";

		ResultSet rs = null;
		Statement st = null;
		try{
			st = relapi.query(query, true);
			rs = st.getResultSet();
			while(rs.next()){
				dbProps.setProperty("omVlan",rs.getString(1));
				dbProps.setProperty("authenticationVlan",rs.getString(2));
				dbProps.setProperty("dataVlan",rs.getString(3));
				dbProps.setProperty("omNetworkAddress", rs.getString(4));
				dbProps.setProperty("omNetmask",rs.getString(5));
				dbProps.setProperty("omDefaultGateway",rs.getString(6));
				dbProps.setProperty("authDefaultGateway",rs.getString(7));
				dbProps.setProperty("dataDefaultGateway",rs.getString(8));
			}
		}catch(Exception ex){
			EMHUtil.printErr("Error while executing the query : "+query, ex);
		}
		finally{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException sqle)
			{
			}
			try
			{
				if (st != null)
				{
					st.close();
				}
			}
			catch (SQLException sqle)
			{
			}
		}
		return dbProps;
	}

	public boolean checkStandbyAvailability()
	{
		System.err.println("Check standby avilabity caleed---->>>");
		boolean stanbyExists = false;
		ResultSet rs=null;
		Statement ps = null;
		try
		{
			//PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_STANDBY_AVAILABILITY_ID);
			ps = relapi.query(PS_FOR_STANDBY_AVAILABILITY_QUERY, true);
			rs = ps.getResultSet();
			if(rs != null)
			{
				if(rs.next())
				{
					stanbyExists = true;
				}
			}

		}
		catch(Exception ex)
		{
			EMHUtil.printErr("Exception while check whether stanby core exits or not.",ex,Log.DEBUG);
		}

		finally
		{
			try
			{
				if(rs != null )
				{
					rs.close();
				}
			}
			catch(Exception ex)
			{
			}
			try
			{
				if(ps != null)
				{
					ps.close();
				}
			}
			catch(Exception ex)
			{

			}
		}
		return stanbyExists;
	}


	/**
	 * Used to get All Mediation Names
	 * @return array liust of all mediation names
	 */
	public ArrayList getAllMediationNames()
	{
		String query="select "+RelationalUtil.getAlias("name")+" from EMHMediation ";
		ArrayList data=new ArrayList();
		ResultSet rs=null;
		Statement stmt=null;
		try{
			stmt=NmsUtil.relapi.query(query, true);
			rs=stmt.getResultSet();
			while (rs.next())
			{
				data.add(rs.getString(1));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				if(rs!=null) rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			try{if(stmt!=null) stmt.close();}catch(Exception e){}
		}
		return data;
	}

	/**
	 * Method to get the Active Cage Names.
	 * @return vector containing the active cage names.
	 */
	public Vector getAllActiveCageNames()
	{
		Vector cageList = new Vector();
		ResultSet rs = null;
		String sqlStr = "select NAME from Cage";
		try
		{
			rs = ConnectionPool.getInstance().executeQueryStmt(sqlStr);

			while (rs.next())
			{
				String cageName  = rs.getString(1);
				if(!CoreUtil.getInstance().isLossOfComAlarmExist(cageName))
				{
					cageList.addElement(cageName);
				}
			}
		}
		catch (Exception exp)
		{
			System.err.println("Error in getting Cage Name : "+exp);
		}
		finally
		{
			try{
				if(rs!=null)rs.close();
			}catch(Exception ex){
				System.err.println("CoreDBUtil:getAllActiveCageNames:Error closing result set "+ex);
			}
		}
		return cageList;
	}
	/**
	 * Method to get the Object Names in the given Table. Expects the name to be avialable in the Table.
	 * @return vector containing the list of objects in it.
	 */
	public Vector getAllObjectNames(String tableName)
	{
		Vector objList = new Vector();
		ResultSet rs = null;
		String sqlStr = "select NAME from " +tableName;
		try
		{
			rs = ConnectionPool.getInstance().executeQueryStmt(sqlStr);

			while (rs.next())
			{
				String obj  = rs.getString(1);
				objList.addElement(obj);
			}
		}
		catch (Exception exp)
		{
			System.err.println("Error in getting Object Name : "+exp);
		}
		finally
		{
			try{
				if(rs!=null)rs.close();
			}catch(Exception ex){
				System.err.println("CoreDBUtil:getAllObjectNames:Error closing result set "+ex);
			}
		}
		return objList;
	}

	/**
	 * This method used to fetch the NetworkTPs corresponding to the given SSC.
	 * @param name SSC Object name
	 * @return ArrayList The names of the NetworkTP objects corresponding to the given SSC.
	 */
	public ArrayList getNetworkTPForSSC(String name)
	{
		ArrayList objectNames = new ArrayList();
		String query = "select RelationObject." + RelationalUtil.getAlias("source") + " from RelationObject, ManagedObject where RelationObject." + RelationalUtil.getAlias("target") + " = ManagedObject." + RelationalUtil.getAlias("name") + " and ManagedObject." + RelationalUtil.getAlias("parentKey") + " ='" + name + "' and ManagedObject." + RelationalUtil.getAlias("type") + " ='" + DeviceTypeConstants.networkep + "' and RelationObject." + RelationalUtil.getAlias("relationship") + " ='"+DeviceTypeConstants.dependency+"'";
		ResultSet rs = null;

		try
		{
			rs = ConnectionPool.getInstance().executeQueryStmt(query);
			while(rs.next())
			{
				objectNames.add( rs.getString(1));
			}
		}
		catch(Exception sqle)
		{
			EMHUtil.printErr("Exception while fetching the NetworkTPs corresponding to " + name, sqle , Log.SUMMARY);
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}

			}catch(SQLException sqle)
			{
			}
		}
		return objectNames;

	}

	/*
	 * method to get the SPANIFTYPE value stored in the table MCCDOCard for the given lmccdoNEName
	 */
	public String getSpanIfType(String lmccdoNEName) throws EMHException {
		ResultSet rs = null;
		Statement st = null;
		String spanIfType = null;
		String query = "SELECT DISTINCT(SPANIFTYPE) FROM LmccdoNeData WHERE NAME LIKE '" + lmccdoNEName + "%'";
		try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if (rs.next()){
				spanIfType = rs.getString("SPANIFTYPE");
			}
		}catch(Exception e){
			throw new EMHException("Unable to execute the Query : " + query, e);
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
			}
		}
		return spanIfType;
	}

	/**
	 * This method used to fetch the name, mapname and the parentname of the MapSymbols
	 * present in the MAPSYMBOL table whose name contains the given name
	 *
	 * @param name partial name of the map symbol
	 *
	 * @return ArrayList contains ArrayList of Properties. Properties object will contain
	 * the name, mapname and the parentname of the MapSymbol that matches the criteria
	 */
	public ArrayList getMapSymbolsForName(String searchName) throws EMHException {
		ResultSet rs = null;
		Statement st = null;
		//String spanIfType = null;
		ArrayList resultAl = new ArrayList();
		String query = "SELECT NAME, MAPNAME, PARENTNAME FROM MapSymbol WHERE NAME LIKE '%" + searchName + "%'";
		try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			while (rs.next()){
				String symbolName = rs.getString(1);
				String mapName = rs.getString(2);
				String parentName = rs.getString(3);
				Properties props = new Properties();
				props.put("SYMBOL_NAME", symbolName);
				props.put("MAP_NAME", mapName);
				props.put("PARENT_NAME", parentName);
				resultAl.add(props);
			}
		} catch(Exception e){
			throw new EMHException("Unable to execute the Query : " + query, e);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
			}
		}
		return resultAl;
	}
    public int getMCCDOPositionInMap(String parentMap, int nodeid)
    {
    	int toRet = -1;
        String query = "Select count(MCCDOCard.NAME) from MCCDOCard,MapDB,ManagedObject " +
        		" where MCCDOCard.NAME = ManagedObject.NAME " +
        		" AND ManagedObject.DISPLAYNAME = MapDB.LABEL " +
        		" AND MCCDOCard.MCCDO1NODEID  < "+nodeid +
        		" AND MapDB.PARENT='"+parentMap+"'";
		ResultSet rs = null;
		Statement st = null;
        try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if(rs.next()){
				toRet = rs.getInt(1);
			}
		} catch (Exception e) {
			EMHUtil.printErr("Error while getting the position of MCCDO Card.Query :"+query, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) { //No use
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) { // No Use
			}
		}
		return toRet;
    }

	/*
	 * method to get the the number of userobjects that has the name, attribName and attribValue parameters
	 * as passed in the arguments
	 */
	public int getFeatureCountForUserObject(String name, String attribName, String attribValue) throws EMHException {
		ResultSet rs = null;
		Statement st = null;
		int count = -1;
		String query = "SELECT count(name) FROM AttribTable WHERE NAME = '" + name + "' and " +
				" ATTRIBNAME = '" + attribName + "' and ATTRIBVALUE = '" + attribValue + "'";
		try {
			st = relapi.query(query,true);
			rs = st.getResultSet();
			if (rs.next()){
				count = rs.getInt(1);
				//System.out.println(query + " -- result -- : " +count);
			}
		}catch(Exception e){
			throw new EMHException("Unable to execute the Query : " + query, e);
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {
			}

			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
			}
		}
		return count;
	}


	/**
	 * Used to get StatusPollEnabled Object Names.
	 * @return array list of status polled enabled mo names
	 */
	public ArrayList getStatusPollEnabledCages()
	{
		ArrayList polledCagesList=new ArrayList();

		PreparedStatementWrapper psw = relapi.fetchPreparedStatement(PS_FOR_GET_POLL_ENABLED_CAGES_ID);
		PreparedStatement ps = psw.getPreparedStatement();
		ResultSet rs = null;
		try
		{
			rs = ps.executeQuery();
			while(rs.next())
			{
			    Properties cageProps = new Properties();
			    cageProps.put("name", rs.getString(1));
			    cageProps.put("ipAddress", rs.getString(2));
			    polledCagesList.add(cageProps);
			}
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}catch(SQLException sqle){}
			relapi.returnPreparedStatement(psw);
		}
		return polledCagesList;
	}




}

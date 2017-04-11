package com.motorola.atcas.ems.cneomitemplates;

//Java imports
import java.util.Properties;

// Adventnet base packages for Inventory Handlers
import com.adventnet.management.config.InventoryHandler;
import com.adventnet.management.config.xml.InventoryInput;

// EMH sequence numbers
import com.motorola.emh.core.util.CoreUtil;

// To do logging
import com.adventnet.nms.util.NmsLogMgr;
import com.adventnet.nms.util.NmsUtil;

// To get SNMP data
import com.motorola.emh.common.util.EMHUtil;
import com.motorola.emh.common.util.ObjectNamingUtility;
import com.motorola.emh.core.util.CageUtil;

import com.motorola.emh.core.util.DeviceTypeConstants;
import com.motorola.emh.common.util.EMHCommonIfc;


/**
	This class implements getting the inventory input of the sequenceTag from EMH.  This is a mechanism
        of getting data into a template in WebNMS/provisioningtemplates/* from the server.
**/
public class InventoryInputProcessor implements InventoryHandler
{
	/**
		The value attribute of InventoryInput tag is assigend with a datum 
		obtained from a file. First the MOName and MOField attribute of the
		InventoryInput tag is fetched. If there is any valid entry corresponding 
		to this MOName and MOField attributes, then that entry is set to the 
		value attribute.
		
	**/

	public InventoryInput getInventory(InventoryInput i)
	{
            String name=i.getMOName();
            String field=i.getMOField();

            int sequenceTag;
            int correlationTag;
		
            try {
                // For the GET we are trying to get the value of sequenceTag 
                if( name.compareTo("GET") == 0 )
                {
                    
                    if( field.compareTo("sequenceTag") == 0)
                    {
                        String corTag = String.valueOf(CoreUtil.getInstance().getEMHCorrelationTag(-1));
                        i.setAttribute("value",corTag);
                        logMessage("sequenceTag = " + corTag);
                    }
                    else if( field.compareTo("correlationTag") == 0)
                    {
                        String corTag = String.valueOf(CoreUtil.getInstance().getEMHCorrelationTag(-1));
                        i.setAttribute("value",corTag);
                        logMessage("correlationTag = " + corTag);
                    }
                    else
                    {
                        // Just return garbage
                        i.setAttribute("value","ERROR:");
                        logMessage("ERROR: Got unknown type = " + field);
                    }
                }
                else if( name.compareTo("CONVERTNAME") == 0 )
                {
                        String sbneRegex = String.valueOf("/SBNE-\\d");
                        String empty = String.valueOf("");

                        String tmp = field.replaceAll(sbneRegex, empty);
                        i.setAttribute("value",tmp);
                }
                else if( name.compareTo("GETSNMPPORTNUMBER") == 0 )
                {
                        Properties snmpProps = EMHUtil.getLbscdoSnmpDetails();
                        String portnum = snmpProps.getProperty("port");
                        i.setAttribute("value", portnum);
                }
                else if( name.compareTo("GETSNMPIPADDRESS") == 0 )
                {
                        EMHCommonIfc commonIfc = EMHUtil.getInstance().getEMHCommonIfc();
                        Properties fieldProps = commonIfc.getMOProperties(field);
                        String neName = fieldProps.getProperty("neName");
                        Properties cageProps = commonIfc.getMOProperties(neName+"/"+DeviceTypeConstants.cage+"-1");

                        i.setAttribute("value",cageProps.getProperty("ipAddress"));
                }
                else if( name.compareTo("GETSNMPCOMMUNITYNAME") == 0 )
                {
                        Properties snmpProps = EMHUtil.getLbscdoSnmpDetails();
                        String communityName = snmpProps.getProperty("community");
                        i.setAttribute("value",communityName);
                }
                else if( name.compareTo("GETCNEOMICLASS") == 0 )
                {
                        Properties snmpProps = ObjectNamingUtility.getObjProps(field);
                        String cneomiClass = snmpProps.getProperty("cneomiClass");
                        i.setAttribute("value",cneomiClass);
                }

            } catch (Exception e) {
                logMessage("Error preparing statement: " + e);
            }

            return i;
	}



    public void logMessage( String userString ){

            NmsLogMgr.MISCERR.fail(NmsUtil.GetString("CneomiInventoryInputProcessor:" + getMethod(4) + ":line " + getLineNum(4) + ":" + userString), null);
    }

    public String getMethod(int index)
    {
        return (Thread.currentThread().getStackTrace()[index].getMethodName());
    }

    public int getLineNum(int index)
    {
        return (Thread.currentThread().getStackTrace()[index].getLineNumber());
    }



}

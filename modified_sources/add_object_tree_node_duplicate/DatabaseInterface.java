
/*
 * This class uses EMHCoreTopoMgr APIs which are protected or private.
 * To get the full functionality the class has to be in the same package.
 * The rest of the code and the actual classes using this object are under
 * package com.motorola.atcas.ems;  
 */

package com.motorola.emh.core.inventory;

import java.util.*;
import java.util.zip.DataFormatException;
//import com.adventnet.nms.severity.SeverityInfo;
import com.motorola.emh.common.exception.EMHInventoryException;
import com.motorola.emh.core.constants.CoreConstants;
import com.motorola.emh.core.util.CoreUtil;
import com.motorola.atcas.ems.commissioning.ConfContainer;
import com.motorola.atcas.ems.commissioning.DebugFramework;
import com.motorola.atcas.ems.commissioning.EmsNameValuePair;
import com.motorola.atcas.ems.commissioning.EmsObject;
import com.motorola.atcas.ems.commissioning.EmsObjectRelation;
import com.adventnet.nms.topodb.ManagedObject;
import com.adventnet.nms.topodb.TopoAPI;


public class DatabaseInterface /* extends EMHCoreTopoMgr */
{
    public DebugFramework Debug = DebugFramework.getInstance();
    
    /*
     * APIs between EMH and Commissioning
     * 
     * EMHCoreTopoMgr.addAnyObject( Properties objProps, Properties otherProps )
     * EMHCoreTopoMgr.addRelationShip( Relationship string, MO-name, targetMO-name )
     * 
     */
    public DatabaseInterface()
    {
        /* 
         * instantiate whatever is necessary from the EMH classes
         *  
         */
    }
    
    
    public void beginTransaction(int i) throws Exception
    {
        try 
        {
            EMHCoreTopoMgr.getInstance().begin(i);
        } 
        catch (Exception e)
        {
            Debug.println3( "Can't begin transactions: " + e );
            e.printStackTrace();
            throw new Exception("Can't begin transactions");

        }
    }

    public void rollbackTransaction() throws Exception
    {
        try 
        {
            EMHCoreTopoMgr.getInstance().rollback();
        } 
        catch (Exception e)
        {
            Debug.println3( "Can't rollback transactions: " + e );
            e.printStackTrace();
            throw new Exception("Can't rollback transactions");
        }

    }

    public void commitTransaction()  throws Exception 
    {
        try 
        {
            EMHCoreTopoMgr.getInstance().commit();
        } 
        catch (Exception e)
        {
            Debug.println3( "Can't commit transactions: " + e );
            e.printStackTrace();
            throw new Exception("Can't commit transactions");
        }
        
    }

    private Properties buildPropertiesObj( EmsObject tmpEmsObj ) throws DataFormatException
    {
        ListIterator iter = null;
        Properties tmpProp = new Properties();
        EmsNameValuePair tmpNameValuePair = null;
        int count=0;
        DataFormatException errorNoCluster =  new DataFormatException("The EmsObject doesn't have an owner - i.e. Cluster");
        //        int unknownSeverity = SeverityInfo.getInstance().getValue("Unknown");

        // ownerName is not written to the database, AdventNet is not supporting this Key field
        // taking it and prepending it to the ID/name to differentiate between identical elements 
        // on the network, which belog to different clusters (N.B. Sept, 2007)
        if( tmpEmsObj.cluster == null )
            throw errorNoCluster;
//else
        tmpProp.setProperty( "NENAME", tmpEmsObj.cluster );
        //tmpProp.setProperty( "moid", tmpEmsObj.num );
                
        tmpProp.setProperty( "name", tmpEmsObj.id );
        tmpProp.setProperty( "type", tmpEmsObj.javaClass );
        //        tmpProp.setProperty( "status", "" + unknownSeverity );
        if( tmpEmsObj.getParent() != null )
            tmpProp.setProperty( "parentKey", tmpEmsObj.getParent() );
               
        if( tmpEmsObj.nameValuePairVector != null )
        {
            iter = tmpEmsObj.nameValuePairVector.listIterator();
            while( iter.hasNext() ) 
            {
                tmpNameValuePair = (EmsNameValuePair)iter.next();
                tmpProp.put( tmpNameValuePair.name, tmpNameValuePair.value );
                count++;
            }
        }

        if( Debug.getDebugLevel() >= 3 )
            tmpProp.list( System.out );
        
        return( tmpProp );
    }
    
    /*
     * Wrapper function around  EMHCoreTopoMgr.addAnyObject()
     * It internally populated the Properties objects which are used by the call.
     * The name-value pairs are put together from the EmsObject vector
     * which holds the elements of the parsed XML file with the cluster information 
     */
    public boolean addAnyObject( ConfContainer clusterInfo, boolean modify ) throws Exception, DataFormatException
    {
        Properties tmpProp = new Properties();
        Properties otherProps = new Properties();                 
        
        EmsObject tmpEmsObj = null;
        ListIterator iter = null;
        
        if(modify) 
        {
        	iter = ((Vector<EmsObject>)clusterInfo.emsObjectModifications).listIterator();
        }
        else
        {
        	iter = ((Vector<EmsObject>)clusterInfo.emsObjects).listIterator();
        }

   

        while( iter.hasNext() ) 
        {
            tmpEmsObj = (EmsObject)iter.next();
            Debug.println3( "Building Properties for Obj=" + tmpEmsObj.num );

            try
            {
                tmpProp = buildPropertiesObj( tmpEmsObj );
                
                // Set the neName for this object
                otherProps.put("neName",tmpEmsObj.cluster + "/cnpCage-1");
                
                Debug.println3( "EMHCoreTopoMgr.addAnyObject( tmpProp, otherProps )\n-------------------\n" );
                /*
                 * This is the EMH API which creates the objects in the database
                 */                
                if(modify)
                {
                		EMHCoreTopoMgr.getInstance().modifyAnyObject( tmpProp.getProperty( "name"), tmpProp.getProperty( "type"), tmpProp );
                }
                else
                {
                	try 
                	{
				/******** add object debug - Venkat - Friday-13th-October*/
				TopoAPI tapi = (TopoAPI) com.adventnet.nms.util.NmsUtil.getAPI("TopoAPI");
				String moName = tmpProp.getProperty("name"); 
				if(!tapi.isManagedObjectPresent(moName)){
					EMHCoreTopoMgr.getInstance().addAnyObject( tmpProp, otherProps );
				} else {
					System.out.println( "Ignoring Double Create During Upgrade. "+moName+" object already Exists in the database");
				}
				/******** add object debug - Venkat - Friday-13th-October - Ends*/
                	} 
                	catch (EMHInventoryException e) 
                	{
                		// Allow double creates...
                		String resultString = e.getMessage();
                		if(clusterInfo.isUpgrade())
                		{
                			if(!((resultString != null) &&
                					(resultString.trim().indexOf("Already Exists in the database") != -1)))
                			{
                				System.out.println( "Error During Upgrade: "+e.toString() );
                				throw e;
                			}
                			else
                			{
                				System.out.println( "Ignoring Double Create During Upgrade: "+e.toString() );
                			}
                		}
                		else
                		{
                			System.out.println( "Error During Install: "+e.toString() );
                			throw e;
                		}
                	}
                }
            }
            catch( DataFormatException  e )
            {
                System.out.println( "Data format Exception Happened in Commissioning: "+e.toString() );
                e.printStackTrace( System.out );
                throw e;
            }
            catch( Exception e )
            {
                System.out.println( "Exception Happened in Commissioning Objects: "+e.toString() );
                e.printStackTrace( System.out );
                throw e;
            }
            
        }
        
        return( true );   
    }

    /*
     * Wrapper function around  EMHCoreTopoMgr.deleteAnyObject()
     * It internally populated the Properties objects which are used by the call.
     * The name-value pairs are put together from the EmsObject vector
     * which holds the elements of the parsed XML file with the cluster information 
     */
    public boolean deleteAnyObject( ConfContainer clusterInfo ) throws Exception, DataFormatException
    {
        Properties tmpProp = new Properties();
        Properties otherProps = new Properties();
                                  
        EmsObject tmpEmsObj = null;
        ListIterator iter = null;
        
        iter = ((Vector<EmsObject>)clusterInfo.emsObjectDeletions).listIterator();
   
        while( iter.hasNext() ) 
        {
            tmpEmsObj = (EmsObject)iter.next();
            Debug.println3( "Building Properties for Obj=" + tmpEmsObj.num );

            try
            {
                tmpProp = buildPropertiesObj( tmpEmsObj );

                Debug.println3( "EMHCoreTopoMgr.deleteAnyObject( tmpProp, otherProps )\n-------------------\n" );
                /*
                 * This is the EMH API which creates the objects in the database
                 */                
              	EMHCoreTopoMgr.getInstance().deleteAnyObject( tmpProp.getProperty( "name"), tmpProp.getProperty( "type") );
            }
            catch( DataFormatException  e )
            {
                System.out.println( "Data format Exception Happened in Commissioning: "+e.toString() );
                e.printStackTrace( System.out );
                throw e;
            }
            catch( Exception e )
            {
                System.out.println( "Exception Happened in Commissioning Objects: "+e.toString() );
                e.printStackTrace( System.out );
                throw e;
            }
            
        }
        
        return( true );   
    }

    
    /*
     * Wrapper function around  EMHCoreTopoMgr.addRelationShip()
     * It creats the strings from its internal <EmsObjectRelation> vector elements
     * which holds the elements of the parsed XML file with the cluster information 
     */
    public boolean addRelationships( ConfContainer clusterInfo ) throws Exception
    {
        EmsObjectRelation tmpEmsRel;
        String relationship;
        String srcMO;
        String targetMO;
        String cageName;
        int    index;
        
        ListIterator iter = ((Vector<EmsObjectRelation>)clusterInfo.emsObjectRelations).listIterator();

        while( iter.hasNext() ) 
        {
            tmpEmsRel = (EmsObjectRelation)iter.next();

            relationship = tmpEmsRel.type;
            srcMO = ((EmsObject)tmpEmsRel.srcObject).id;
            targetMO = ((EmsObject)tmpEmsRel.targetObject).id;
            index =  ((EmsObject)tmpEmsRel.targetObject).num;
            cageName = "";
            
            Debug.println3( "Num=" + index + " --- Src=" + srcMO + " --- Target=" + targetMO + " --- cageName=" + cageName + "\n" );
            Debug.println3( "EMHCoreTopoMgr.addRelationShip( _relationship, _srcMO, _targetMO, _cageName )" );
            try 
            {
                EMHCoreTopoMgr.getInstance().addRelationShip( relationship, srcMO, targetMO, cageName );
            }
            catch( Exception e )
            {
                System.out.println( "Exception Happened in Commissioning Relationships: "+e.toString() );
                e.printStackTrace( System.out );
                throw e;
            }
        }
        
        return( true );   
    }

}

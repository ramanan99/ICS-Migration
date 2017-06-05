/*Usage - java -Dwebnms.rootdir=$NMS_HOME test.InsertQueries*/
package test;

import com.adventnet.nms.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;
public class InsertQueries {

	public static void main(String args[]){

		String rootDir = System.getProperty("webnms.rootdir");
		if(rootDir == null){
			System.err.println("Usage : java -Dwebnms.rootdir=$NMS_HOME test.InsertQueries ");
			System.exit(1);
		}

		String tables[] = {"EsmeLink", "InterMateLink", "LnpServerLink", "SmscLink", "TapClientLink", "XmlClientLink"}; //You can add your tables in this array
		String superTable = "Interface" ; //You can change the super table name
		String columns_of_superTable = " (MOID, ADMINSTATE, OPSTATE, USAGESTATE, CONTROLSTATUS, AVAILABILITYSTATUS, REASONCODE, USERNAME) values (";
		String values = ",1,1,1, 255,255,0, 'NULL')";
		int howManyTables = tables.length;
		String[] queries = new String[howManyTables];
		DBParamsParser dbParser = null ;
		Connection con =null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;

		for(int k=0;k<howManyTables;k++){
			queries[k]= "select MOID from "+tables[k]+"  where MOID NOT in (select MOID from "+superTable+")";	
			System.err.println("query "+k+": "+queries[k]);
		}

		try{
			File file = new File (rootDir+"/classes/hbnlib/hibernate.cfg.xml");
			dbParser = DBParamsParser.getInstance(file);
		}
		catch (Exception exp){
			System.err.println("Exception in getting DB instance from hibernate.cfg.xml");
			exp.printStackTrace();
		}
		String userName = dbParser.getUserName();
		String password = dbParser.getPassword();
		String url = dbParser.getURL();
		String driver = dbParser.getDriverName();
		try
		{
			Class.forName(driver);
		} catch (Exception e)
		{
			System.err.println("Exception in initialising the driver : "+driver);
			e.printStackTrace();
		}
		try
		{
			con =DriverManager.getConnection(url,userName,password);
		} catch (Exception exp){
			System.err.println("Exception in getting the connection from the database with the following details: userName: "+userName+", password: "+password+", URL:"+url);
			exp.printStackTrace();
		}
		try{
			stmt = con.createStatement();       
		} catch (Exception exp){
			exp.printStackTrace();
		}
		for (int j=0;j<queries.length;j++){
			try{
				Vector moid_set = new Vector();
				rs = stmt.executeQuery(queries[j]);
				stmt1 = con.createStatement();
				while (rs.next()){
					int id = rs.getInt("MOID");
					String query = "insert into "+superTable+columns_of_superTable+id+values;
					System.err.println(query);
					stmt1.addBatch(query);
				}
				stmt1.executeBatch();
				stmt1.clearBatch();
				//con.commit();
			} catch (Exception exp){
				exp.printStackTrace();
			}
		}
		try{
			stmt.close();
			stmt = null;
			stmt1.close();
			stmt1=null;
			rs.close();
			rs=null;
			con.close();
			con=null;
		} catch (Exception exp){
			exp.printStackTrace();
		}

	}

}

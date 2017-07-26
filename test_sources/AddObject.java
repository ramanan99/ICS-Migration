package test;

import com.adventnet.nms.util.*;
import java.util.*;
import java.rmi.Naming;
import com.adventnet.nms.topodb.*;
import java.io.*;
import com.adventnet.management.log.SystemUtil;
public class AddObject {
	public static void main(String args[]) {
		TopoAPI topo = null;
		try{
			topo = (TopoAPI)Naming.lookup("//localhost:1099/TopoAPI");
			System.err.println("Got TopoAPI handle"); //Will log them in stderr
			ManagedObject mobj = new ManagedObject();
			Properties prop = new Properties();
			prop.setProperty("pollInterval", "1800");
			prop.setProperty("neName", "/SBNE-1");
			prop.setProperty("statusChangeTime", "1501023211678");
			prop.setProperty("periodicity", "0");
			prop.setProperty("uClass", "null");
			prop.setProperty("type", "blackBoxDataConfig");
			prop.setProperty("statusUpdateTime", "1501023211678");
			prop.setProperty("statusPollEnabled", "false");
			prop.setProperty("displayName", "blackBoxDataConfig-1");
			prop.setProperty("active", "1");
			prop.setProperty("isContainer", "true");
			prop.setProperty("stopXmit", "NULL");
			prop.setProperty("isGroup", "false");
			prop.setProperty("childrenKeys", "");
			prop.setProperty("stringstatus", "Unknown");
			prop.setProperty("filterConfig", "NULL");
			prop.setProperty("failureCount", "0");
			prop.setProperty("startXmit", "NULL");
			prop.setProperty("entityType", "262167");
			prop.setProperty("status", "7");
			prop.setProperty("failureThreshold", "1");
			prop.setProperty("parentKey", "/SBNE-1/cnpCage-1/cnpClusterManager-5/blackBoxLogStream-1");
			prop.setProperty("controlConfig", "NULL");
			prop.setProperty("tester", "ping");
			prop.setProperty("managed", "false");
			prop.setProperty("classname", "BlackBoxDataConfig");
			mobj.setName("/SBNE-1/cnpCage-1/cnpClusterManager-5/blackBoxLogStream-1/blackBoxDataConfig-1");
			mobj.setProperties(prop);
			System.err.println("addObject result "+topo.addObject(mobj,true,false));
		}
		catch(Exception e){e.printStackTrace();}
		System.exit(0);
	}
}

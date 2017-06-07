package test;

import com.adventnet.nms.util.*;
import java.util.*;
import java.rmi.Naming;
import com.adventnet.nms.topodb.*;
public class GetByName 
{

	public static void main(String args[])
	{
		TopoAPI topo = null;
		try{
			topo = (TopoAPI)Naming.lookup("//localhost:1099/TopoAPI");
			System.err.println("Got TopoAPI handle"); //Will log them in stderr
			ManagedObject mobj;
			String str = args[0];
			mobj = topo.getByName(str);
			if (mobj != null){
				System.err.println("Object: "+mobj.getProperties());
			}
			else{
				System.err.println("Object is null ");
			}
			System.err.println("mobj.getParentKey() for "+str+" : "+mobj.getParentKey());
		}
		catch(Exception e){e.printStackTrace();}
		System.exit(0);
	}
}

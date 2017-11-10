package test;

import com.adventnet.nms.util.*;
import java.util.*;
import java.rmi.Naming;
import com.adventnet.nms.topodb.*;
public class NodeList implements RunProcessInterface
{

	public static void main(String args[])
	{
		TopoAPI topo = null;
		try{
			topo = (TopoAPI)Naming.lookup("//localhost:1099/TopoAPI");
			System.err.println("Got TopoAPI handle"); //Will log them in stderr
			ManagedObject mobj;
			String str = args[0];
			/*The below list got from the query - "select NAME from ManagedObject"*/
			/*String errors = "";
			for (int k=0;k<mos.length; k++){
				try{
					ManagedObject mobj1 = topo.getByName(mos[k]);
				} catch (Exception exp){
					errors = errors+"\n"+ mos[k];
				}
			}
			System.err.println("error MOs :"+errors);*/
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

	public void callMain(String args[])
	{
		TopoAPI topo = null;
		try{
			while (topo == null)
			{
				try{
					topo = (TopoAPI)NmsUtil.getAPI("TopoAPI");
					Thread.sleep(2000);
				}
				catch(Exception e){}
			}
			System.err.println("Got TopoAPI handle"); //Will log them in stderr
			ManagedObject mobj;
			String str = args[0];
			System.err.println("Going to getByName for: "+str); //Will log them in stderr
			/*The below list got from the query - "select NAME from ManagedObject"*/
			/*String errors = "";
			for (int k=0;k<mos.length; k++){
				try{
					ManagedObject mobj1 = topo.getByName(mos[k]);
				} catch (Exception exp){
					errors = errors+"\n"+ mos[k];
				}
			}
			System.err.println("error MOs :"+errors);*/
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
	}
	/*public void callMain(String args[])
	{
		TopoAPI topo = null;
		while (topo == null)
		{
			try{
				topo = (TopoAPI)NmsUtil.getAPI("TopoAPI");
				Thread.sleep(2000);
			}
			catch(Exception e){}
		}
		System.err.println("Got TopoAPI handle"); //Will log them in stderr
		int count=0;
		while (count < 10) {
			GetByName(topo,count);
			count++;
		}

	}*/
	public boolean isInitialized()
	{
		return true;
	}
	public void shutDown()
	{

	}
	/*public void GetByName(TopoAPI topo, int count){
		try {
			Vector vect = topo.getCompleteList();
			System.err.println("Attempt No.: "+count+":size of Vector : " +vect.size());
			Enumeration enumm = vect.elements();
			long starttime = System.currentTimeMillis();
			long ttime_start = System.currentTimeMillis();
			long ttime_end = System.currentTimeMillis();
			int moSize = vect.size();
			ManagedObject mobj;
			for(int i=0; i<moSize; i++){
				String str = vect.elementAt(i).toString();
				mobj = topo.getByName(str);
				if (i%1000 == 0){
					ttime_end = System.currentTimeMillis();
					long difference = ttime_end - ttime_start;
					System.err.println("Objects completed : "+i+". Time to do getByName of 1000 objects : "+difference);
					ttime_start = System.currentTimeMillis();
				}
			}
			long endTime = System.currentTimeMillis();
			long difference = endTime - starttime;
			float average = difference/moSize;
			System.err.println("Attempt No. : "+count+": All Objects completed. Time to do getByName of "+moSize +" objects in ms : "+difference+". Average time for one getByName operation in ms: "+average);
			Thread.sleep(3000);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
}

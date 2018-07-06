package test;

import com.adventnet.nms.util.*;
import java.util.*;
import java.rmi.Naming;
import com.adventnet.nms.topodb.*;
public class GetByNameForAllObjects
{

	public static void main(String args[])
	{
		TopoAPI topo = null;
		try{
			topo = (TopoAPI)Naming.lookup("//localhost:1099/TopoAPI");
			System.err.println("Got TopoAPI handle"); //Will log them in stderr
		try {
			Vector vect = topo.getCompleteList();
			int count = vect.size();
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
			System.err.println("Attempt No.: "+count+":size of Vector : " +vect.size());
			long endTime = System.currentTimeMillis();
			long difference = endTime - starttime;
			float average = difference/moSize;
			System.err.println("Attempt No. : "+count+": All Objects completed. Time to do getByName of "+moSize +" objects in ms : "+difference+". Average time for one getByName operation in ms: "+average);
			//Thread.sleep(3000);
		}catch(Exception e) {
			e.printStackTrace();
		}
		}
		catch(Exception e){e.printStackTrace();}
		System.exit(0);
	}
}

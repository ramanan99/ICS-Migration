// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LogStreamDataConfig.java

package com.motorola.atcas.ems.metadata.cneomi.streams;

import com.adventnet.nms.topodb.ManagedObject;
import java.math.BigInteger;
import java.util.Properties;

public class LogStreamDataConfig extends ManagedObject
{

    public LogStreamDataConfig()
    {
        setClassname("LogStreamDataConfig");
    }

    public void setStartXmit(String startXmit)
    {
        this.startXmit = startXmit;
    }

    public void setStopXmit(String stopXmit)
    {
        this.stopXmit = stopXmit;
    }

    public void setPeriodicity(long periodicity)
    {
        this.periodicity = periodicity;
    }

    public void setFilterConfig(String filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    public void setControlConfig(String controlConfig)
    {
        this.controlConfig = controlConfig;
    }

    /*public void setActive(String active)
    {
        this.active = active;
    }*/

    public void setActive(boolean active)
    {
        this.active = active;
    }
    public String getStartXmit()
    {
        return startXmit;
    }

    public String getStopXmit()
    {
        return stopXmit;
    }

    public long getPeriodicity()
    {
        return periodicity;
    }

    public String getFilterConfig()
    {
        return filterConfig;
    }

    public String getControlConfig()
    {
        return controlConfig;
    }

    /*public String getActive()
    {
        return active;
    }*/
	public boolean getActive()
	{
		return active;
	} 

    public void setProperties(Properties props)
    {
        String startXmit_value = (String)props.remove("startXmit");
        if(startXmit_value != null)
            startXmit = startXmit_value;
        String stopXmit_value = (String)props.remove("stopXmit");
        if(stopXmit_value != null)
            stopXmit = stopXmit_value;
        long periodicity_value = (long)props.remove("periodicity");
        //if(periodicity_value != null)
            periodicity = periodicity_value;
        String filterConfig_value = (String)props.remove("filterConfig");
        if(filterConfig_value != null)
            filterConfig = filterConfig_value;
        String controlConfig_value = (String)props.remove("controlConfig");
        if(controlConfig_value != null)
            controlConfig = controlConfig_value;
        //String active_value = (String)props.remove("active");
        //if(active_value != null)
         		props.setProperty("active", "true");
			props.setProperty("periodicity", "0");
        super.setProperties(props);
    }

    public Properties getProperties()
    {
        Properties props = super.getProperties();
        if(getStartXmit() != null)
            props.put("startXmit", getStartXmit());
        if(getStopXmit() != null)
            props.put("stopXmit", getStopXmit());
        //if(getPeriodicity() != null)
          //  props.put("periodicity", getPeriodicity().toString());
            props.put("periodicity", "0");
        if(getFilterConfig() != null)
            props.put("filterConfig", getFilterConfig());
        if(getControlConfig() != null)
            props.put("controlConfig", getControlConfig());
        //if(getActive() != null)
           // props.put("active", getActive().toString());
		   props.put("active", "true");
			props.setProperty("periodicity", "0");
        return props;
    }

    private String startXmit;
    private String stopXmit;
    private long periodicity;
    private String filterConfig;
    private String controlConfig;
    private boolean active;
}

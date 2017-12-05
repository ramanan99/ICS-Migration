// $Id: GetChallengeServlet.java,v 1.3 2006/10/07 08:06:52 javeed Exp $
package com.adventnet.nms.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import com.adventnet.nms.util.*;
import com.adventnet.security.AuthUtil;
import com.adventnet.security.audit.AuditAPI;

import com.adventnet.security.authentication.AuthenticationException;

public class GetChallengeServlet extends HttpServlet{

       
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
    {
	//res.getWriter().println("success");
    }

    String userName = null;

    public void  service(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException 
    {
		userName = null;
                String challenge = null;
                String challengeAndSessionID = null;
		HttpSession session = request.getSession(false);
		PrintWriter out = null;
	
                    if(session != null)
                    {
                        userName = (String) session.getAttribute("userName");
                    }
                    
                    if((userName = request.getHeader("userName"))== null )
                    {
                        userName = request.getParameter("userName");
                    }
		    /*if(NmsUtil.isPasswordOnewayEncrypted()){
			    String hash = request.getHeader("hash");//No I18N
			    if(hash == null || hash.trim().equals("")){//No I18N
				    //auditForWrongPassword(userName);
				    out = response.getWriter();
				    out.println("No such user");//No I18N
			    }
			    Hashtable temp = new Hashtable();
			    String password = "";//No I18N
			    try{
				    temp = com.adventnet.security.authentication.ExtendedAuthenticationImpl.getCredentialsForUser(userName);
				    if(temp!=null){
					    password = (String)temp.get(userName);
				    }
			    } catch(Exception ex){ex.printStackTrace();}
			    if (!hash.equals(password)){
				    out = response.getWriter();
				    out.println("No such user");//No I18N
			    }
		    }*/
                    
		    String hostAddress = request.getParameter("hostaddress");//No I18N
		    String hostPort = request.getParameter("hostPort");//No I18N
		    Properties hostProperties = new Properties();
		    // as per the security team requirement hostAddress is sent as hostname
		    if(hostAddress != null)
		    {
			    hostProperties.put("hostname",hostAddress);
		    }
		    if(hostPort != null)
		    {

			    hostProperties.put("hostport",hostPort);
		    }
		    hostProperties.put("webserveraddress",InetAddress.getLocalHost().getHostName());
		    hostProperties.put("servertype",NmsUtil.getServerType());

		    if(userName != null )
                    {
                        try{
                            challenge = PureServerUtilsFE.getChallenge(userName,hostProperties);
                            challenge = URLEncoder.encode(challenge);
                        }
                        catch(AuthenticationException ae){
                            out = response.getWriter();
                            out.println("No such user");
                        }
                        if(challenge != null)
                        {

                            session =request.getSession(true);
                            session.setAttribute("userName",userName);//No Internationalization
                            out = response.getWriter();
                            challengeAndSessionID = "SessionId="+session.getId() + ";Challenge="+challenge ;
                            out.println(challengeAndSessionID);
                        }
                    }
                    
                    
                    if( out != null ){
                        out.flush();
                        out.close();
                    }
                    
    }
    /*private void auditForWrongPassword (String userName){ 
	    try{
		    AuditAPI authAudit =(AuditAPI)NmsUtil.getAPI("AuditAPI");//No I18N
		    synchronized(authAudit){
			    Properties auditProp = new Properties();
			    auditProp.put("userName",userName);//No I18N
			    auditProp.put("operation","Authentication");//No I18N
			    String time = new java.sql.Timestamp(System.currentTimeMillis()).toString();
			    auditProp.put("auditTime",time);//No I18N
			    auditProp.put("status","FAILURE");//No I18N
			    auditProp.put("category","Authentication");//No I18N
			    authAudit.audit(userName, auditProp);
		    }
	    }
	    catch (Exception exp){
		    exp.printStackTrace();
	    }
    }*/
}
    
    
    

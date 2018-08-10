package com.edt.b4t.projects;

import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import com.edt.b4t.B4TProjects;
import com.edt.b4t.B4TUsers;
import com.edt.b4t.util.*;
import com.edt.email.Outlook365Email;

public class B4TProjectsEmail
{
	public static void main(String [] args)
			throws IOException, AddressException, MessagingException
	{
		System.out.println("Today " + new Date());
		
	    B4TProjectsEmail email = new B4TProjectsEmail();
	    
		email.send();
	}
	
	public void send()
	{
	    String props = "props/EDTB4TEmails";
	    
	    try
	    {
        	Outlook365Email email = new Outlook365Email(props);
        	
			String body = this.getBody(),
					date = TimeDate.getDateString("Tomorrow");
            
            if (null != body)
            	email.send(body);
            else
            	System.out.println("NO email to send. Setting the date to " + date);
            
            AppProperties.setFname(props);
    		AppProperties.update("b4t.last.email", date);
        }
        catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private String getBody()
	{
		String date = System.getProperty("b4t.last.email"),
				params = "?$filter=createdDate ge '" + date + "'",
				B4T_PROJECT_URL = System.getProperty("b4t.project.url");
		
		System.out.println("Looking for projects created on " + date);
		
		B4TProjects projects = new B4TProjects(Str.convertToURL(params));
		
		boolean send = (0 < projects.getIds().size());
		
		B4TUsers user = new B4TUsers("");
		
//		String image = System.getProperty("image.header"),
		String content = "<html><body>" +

//		        content = "<head><style>" +
//				"h2 {font-size: 15px;}" +
//				"</style></head>" +
				"<center>" +
				
//				"<div id=\"imagesize\"></div>" +
//
//                "<script>" +
//                    "var txt = \"<img src=\"" + image + 
//                              " width=\"50%\" height=\"25%\">" +
//                
//                    "if (700 < screen.height)" +
//                        "txt = \"<img src=\"" + image + " width=\"50%\" height=\"50%\">" +
//                        
//                    "document.getElementById(\"imagesize\").innerHTML = txt;" +
//                "</script>" +
				
//				"<img src=" + System.getProperty("image.header") + ">" +

				"<table border=\"1\" cellpadding=\"5\">" +
				"<thead>" +
				"<tr align=\"center\">" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Project</font></b></th>" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Custom ID</font></b></th>" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Client</font></b></th>" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Project Type</font></b></th>" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Assignment</font></b></th>" +
				"<th bgcolor=#636285><font color=\"white\" size=\"4\"><b>Created</font></b></th>" +
				"</tr>" +
				"</thead>";

		String body = "";
		
		if (send)
		{
			for (int element = 0; element < projects.getIds().size(); element++)
			{
				date = projects.getCreatedDates().get(element);
				
				String id = projects.getAssignedTos().get(element);
				
				date = (String)date.subSequence(0, date.indexOf('T'));
				
				body += "<tr align=\"center\">" + 
						"<td><a href=\"" + B4T_PROJECT_URL + Integer.valueOf(projects.getIds().get(element)) + "\" target=\"_blank\"><b>" 
						+ projects.getProjectNames().get(element) + "</a></b></td>" +
						"<td>" + projects.getCustomIds().get(element) + "</td>" +
						"<td>" + projects.getClientNames().get(element) + "</td>" +
						"<td>" + projects.getProjectTypes().get(element) + "</td>" +
						"<td><a href=\"mailto://" + this.getEmail(user.getIds(), id, user.getEmails()) + "\">"
						+ this.getFname(user.getIds(), id, user.getFnames(), user.getLnames()) + "</a></td>" +
						"<td>" + date + "</td>" +
						"</tr>";
			}
			
			body += "</table></center></body></html>";
			
// 			DemoFile table = new DemoFile("projects.html");
//			table.writeLine(content + body);
//			table.close();
		}
		
		return (!send) ? null : content + body;
	}
	
	private String getFname(ArrayList<String> ids, String id, ArrayList<String> firstNames, ArrayList<String> lastNames)
	{
		String name = "";
		
		for (int count = 0; (0 == name.length()) && (count < firstNames.size()); count++)
		{
//			System.out.println(ids.get(count) + " - " + id + "; " + firstNames.get(count) + " " + lastNames.get(count));
			
			if (id.equals(ids.get(count)))
				name = firstNames.get(count) + " " + lastNames.get(count);
		}
		
		return name;
	}

	private String getEmail(ArrayList<String> ids, String id, ArrayList<String> emails)
	{
		String email = "";
		
		for (int count = 0; (0 == email.length()) && (count < ids.size()); count++)
		{
			if (id.equals(ids.get(count)))
				email = emails.get(count);
		}
		
		return email;
	}
}
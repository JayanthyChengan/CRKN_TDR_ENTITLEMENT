package com.marklogic.semantics.xsl;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;



public class XslExternalProc {

	public static URI uri;
	public static ContentSource cs;
	public static Session session;
	public static Request request;
	public static ResultSequence rs;


	public static int rand(int len)
	{
		Random rand = new Random( len );
		return (rand.nextInt(len));
	}

	public static String encodePath(String issn , String startdate, String enddate, String type ) {

		String originalInput = issn ; //issn + "_" + startdate.replace("-", "") + "_" + enddate.replace("-", "") + "_" + type ;

		if (startdate.length() > 2)
		{
			originalInput =   originalInput + "_s" + startdate.replace("-", "");
		}

		if (enddate.length() > 2)
		{
			originalInput = originalInput + "_e" + enddate.replace("-", "");
		}

		originalInput =   originalInput + "_" + type ;

		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

		encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

		return originalInput;

	}

	public static String encodePerpetualPath(String issn , String startdate, String type ) {

		String originalInput = issn.toLowerCase() ; //issn + "_" + startdate.replace("-", "") + "_" + enddate.replace("-", "") + "_" + type ;

		if (startdate.length() > 2)
		{
			originalInput =   originalInput + "_y" + startdate.replace("-", "");
		}


		originalInput =   originalInput + "_" + type ;

		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

		encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

		return originalInput;

	}



	public static String encodePath(String issn , String startdate  ) {

		String originalInput = issn.replace("-", "") + "_y" +  (startdate).substring(0,4)   + "_p"  ;

		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		return originalInput;

	}

	public static String getISSNDetails(String issn , String type)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();


		String query = ""; 
		if(type.equals("eissn"))
		{
			query = " doc('/"  +issn + "/toc.xml')//*:issn[@pub-type = 'epub']/text() ";
		}
		else
		{
			query = " doc('/"  +issn + "/toc.xml')//*:journalID-sp/text() ";
		}

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		return rs.asString();

	}
	//<toc:journalID-sp>sp010001</toc:journalID-sp>


	 

}



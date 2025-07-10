/*
 * Copyright 2016-2017 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.jena.spRDF;
 
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 

import javax.xml.xpath.XPathExpressionException;
 
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.*;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.ejournals.client.DocumentRead; 

public class Utils {


 
 
	
	public static DatabaseClient load_Mlsand_Entitlements_Props_orig() {

		String host = "mlsand.scholarsportal.info";
		int port = Integer.parseInt("8051");
		String user = "loader";
		String pass = "Loader123";

		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user,pass, Authentication.DIGEST);

		return client;
 
	}
	
	  
	public static DatabaseClient load_mlenodetest1_Entitlements_Props_orig() {

		String host = "mlenodetest1.scholarsportal.info";
		int port = Integer.parseInt("8003");
		String user = "loader";
		String pass = "Loader123";

		DatabaseClient client = DatabaseClientFactory.newClient(host, port, user,pass, Authentication.DIGEST);

		return client;
 
	} 

	public static Document getDocument(DatabaseClient client_Ejournals, String a_uri) throws XPathExpressionException, IOException {
		DocumentRead documentRead = new DocumentRead();
		Document source = documentRead.documentRead(client_Ejournals , a_uri);
		return source;
	}
	
	public static void addDocument(DatabaseClient client_Ejournals , String xmlFileName) throws IOException
	{
		DocumentRead documentRead = new DocumentRead();
		documentRead.documentWrite(client_Ejournals , xmlFileName);  
		System.out.println("Loaded " + xmlFileName + "  xml.");
	}

	public static DocumentMetadataHandle getMetadata(DatabaseClient client_Ejournals, String a_uri) throws XPathExpressionException, IOException 
	{
		// create a manager for XML documents
		XMLDocumentManager docMgr = client_Ejournals.newXMLDocumentManager();				

		// retrieve the document - set up the handles
		// 1. metadata handle
		DocumentMetadataHandle mdhRead = new DocumentMetadataHandle(); 
		ReaderHandle rh = new ReaderHandle(); 
		docMgr.read(a_uri, mdhRead, rh);
		return mdhRead ; 
	}
	
	public static String hashCode(String input) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(input.toString().getBytes());

		byte byteData[] = md.digest();

		//convert the byte to hex format 
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		System.out.println("Digest(in hex format):: " + sb.toString());
		return sb.toString();
	}

 


}
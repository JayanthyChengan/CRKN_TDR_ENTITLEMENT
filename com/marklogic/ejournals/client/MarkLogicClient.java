package com.marklogic.ejournals.client; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.XMLDocumentManager.DocumentRepair;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.eval.ServerEvaluationCall;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.extensions.ResourceServices;

import java.io.FileInputStream;

public class MarkLogicClient {

	private final int MIN_BATCH_SIZE = 1;
	private final int MAX_BATCH_SIZE = 1000;
	private int batchSize = MAX_BATCH_SIZE;

	private DocumentWriteSet writeSet = null;
	private XMLDocumentManager xmlDocMgr = null;
	private DatabaseClient databaseClient = null;
	
	int numSize=0;
	ArrayList<DOMHandle> dhs=new ArrayList<DOMHandle>();
	
	public MarkLogicClient(String marklogicHost, String marklogicDatabase, String marklogicUser, String password, int marklogicPort) {
		//System.out.println("==> "+marklogicHost+" "+marklogicPort+" "+marklogicUser+" "+password);
		databaseClient = DatabaseClientFactory.newClient(marklogicHost, marklogicPort, marklogicDatabase, marklogicUser, password,
					Authentication.DIGEST);
		xmlDocMgr = databaseClient.newXMLDocumentManager();
		writeSet = xmlDocMgr.newWriteSet();
	}
	
	public MarkLogicClient(String marklogicHost, String marklogicUser, String password, int marklogicPort) {
		this(marklogicHost, null, marklogicUser, password, marklogicPort);
	}

	public MarkLogicClient(String marklogicHost, String marklogicDatabase, String marklogicUser, String password, int marklogicPort,
			int batchSize) {
		this(marklogicHost, marklogicDatabase, marklogicUser, password, marklogicPort);
		if (batchSize < MIN_BATCH_SIZE || batchSize > MAX_BATCH_SIZE) {
			this.batchSize = MAX_BATCH_SIZE;
		} else {
			this.batchSize = batchSize;
		}		
	}
	
	public MarkLogicClient(String marklogicHost, String marklogicUser, String password, int marklogicPort,
			int batchSize) {
		this(marklogicHost,  null, marklogicUser, password, marklogicPort, batchSize);
	}

	public MarkLogicClient() {
		// TODO Auto-generated constructor stub
	}

	public Document read(String documentUri) {
		DOMHandle domHandle = new DOMHandle();
		domHandle = xmlDocMgr.read(documentUri, domHandle);
		Document document = domHandle.get();
		return document;
	}

	@Deprecated
	public void addToBatch(Document document, String documentUri, ArrayList<String> collections,
			Map<String, String> properties) {
		if (writeSet.size() >= batchSize) {
			uploadBatch();
		}
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll(collections);
		addDocumentMetadataProperties(metadataHandle, properties);
		writeSet.add(documentUri, metadataHandle, new DOMHandle(document));
	}
	
	@Deprecated
	public void uploadBatch() {
		if (!writeSet.isEmpty()) {
			xmlDocMgr.write(writeSet);
			writeSet.clear();
		}
	}

	public void release() {
		databaseClient.release();
	}

	private void addDocumentMetadataProperties(DocumentMetadataHandle metadataHandle, Map<String, String> properties) {
		if (properties != null && !properties.isEmpty()) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				metadataHandle.withProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/*run a query against a database by indicating the filename of the query*/	
	public String runXQUERY(File xqueryFile,Map<String,String> properties){
		
		ServerEvaluationCall theCall = databaseClient.newServerEval();
		
		//supply input variables to query if any
		if (properties != null && !properties.isEmpty()) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				theCall.addVariable(entry.getKey(), entry.getValue());
			}
		}
		
		String response;
		
		FileInputStream fis = null;
		InputStreamHandle s = null;
		try {
			
			fis = new FileInputStream(xqueryFile);
			s = new InputStreamHandle(fis);
			
			//associate the adhoc query with your call object. You can specify query using a string or textwritehandle
			//FileHandle temp =new FileHandle(xqueryFile);
			
			//FileHandle temp =new FileHandle().with(xqueryFile);
			theCall.xquery(s);
			response = theCall.evalAs(String.class);	
			
		}catch(Exception e){
			System.out.println("the call failed");
			response="failed";
		
		}
		
		//cleanup
		s.close();
		
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("the response:"+response);
					
		return response;
		
	}
	
	/*run a query by indicating a raw query*/
	public String runXQUERY(String xquery,Map<String,String> properties){
		
		ServerEvaluationCall theCall = databaseClient.newServerEval();
		
		//supply input variables to query if any
		if (properties != null && !properties.isEmpty()) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				theCall.addVariable(entry.getKey(), entry.getValue());
			}
		}
		//associate the adhoc query with your call object. You can specify query using a string or textwritehandle
		
		String response = theCall.evalAs(String.class);				   
		return response;
		
	}
	
	public List<String> getURISforCollection(String collection){
		
		List<String> uris = new ArrayList<String>();
		
		String collectionQuery ="declare namespace xlink='"+"http://www.w3.org/1999/xlink"+"';"+
								"declare variable $collectionName as xs:string external;  " +
								"for $x in collection($collectionName) "+ 
								"order by document-uri($x) "+
								"return (concat(fn:document-uri($x),'@',$x/book/book-meta/self-uri[@content-type='pdf']/@xlink:href))";
		
		ServerEvaluationCall call = databaseClient.newServerEval().xquery(collectionQuery).addVariable("collectionName",collection);
		for ( EvalResult result : call.eval() ) {
		      String the_uri = result.getString();
		      uris.add(the_uri);
		}
		return uris;

		

				
		/*Another option is using QueryManager but it doesn't seem to like java 6 xml api
		 *List<String> uris = new ArrayList<String>();
		  QueryManager queryMgr = databaseClient.newQueryManager();
		  StringQueryDefinition qd = queryMgr.newStringDefinition();
		  //set collection to collection
		  qd.setCollections(collection);
		  // empty search defaults to returning all results
		  qd.setCriteria("");
					
		  SearchHandle resultsHandle = queryMgr.search(qd, new SearchHandle());
		  MatchDocumentSummary[] summaries = resultsHandle.getMatchResults();
	      for (MatchDocumentSummary summary: summaries) {
	    	uris.add(summary.getUri());
	       }
	        return uris;
		*/
		
	}
	
	 
		
	 
	
  
	public Document documentRepair(File file, String documentUri) {		
		xmlDocMgr.setDocumentRepair(DocumentRepair.FULL);
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		xmlDocMgr.write(documentUri, handle);
		return read(documentUri);
	}
	
	public void write(File file, String documentUri) {
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		xmlDocMgr.write(documentUri, handle);
	}
	
	public void deleteDocument(String documentUri) {
		xmlDocMgr.delete(documentUri);
	}
	
	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public XMLDocumentManager getXmlDocMgr() {
		return xmlDocMgr;
	}

	public List<String> runXQUERY(DatabaseClient db_client, String query, HashMap<String, String> properties) {
		// TODO Auto-generated method stub
		ServerEvaluationCall theCall = db_client.newServerEval();
		List<String> uris = new ArrayList<String>();
		
		
		//ServerEvaluationCall call = databaseClient.newServerEval().xquery(collectionQuery).addVariable("collectionName",collection);
		
		
		
		System.out.println("Xquery is " + query);
		//supply input variables to query if any
		if (properties != null && !properties.isEmpty()) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				theCall.addVariable(entry.getKey(), entry.getValue());
			}
		} 
		
		theCall.xquery(query);
		
		for ( EvalResult result : theCall.eval() ) {
		      String the_uri = result.getString();
		      uris.add(the_uri);
		}
		return uris;
		
		
	}
}

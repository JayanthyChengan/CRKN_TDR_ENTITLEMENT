package com.marklogic.semantics.xsl;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.jena.spRDF.Get_from_Excel;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

public class LoadALMATriples {

	static String host = "mlenodetest1.scholarsportal.info";
	static int port =  Integer.parseInt("8003");  //"Entitlement_Semantics"
	static String user = "loader";
	static String pass = "Loader123";

	static DatabaseClient client = DatabaseClientFactory.newClient(
			host, port, "Entitlement_Semantics",
			new DatabaseClientFactory.DigestAuthContext(
					user,pass));

	static private GraphManager graphMgr = client.newGraphManager();

	static private MarkLogicDatasetGraph dsg;



	public static void main_bkup(String[] args) { 	        

		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(client);

		// Create RDF model using Apache Jena
		Model model = ModelFactory.createDefaultModel();

		String  hasKBtitle = "http://scholarsportal.info/ontologies/core/kbtitle"; 

		Get_from_Excel t = new Get_from_Excel();
		// Excel sheet is generated from https://gitlab.scholarsportal.info/ejournals/Loader/-/tree/master/info/scholarsportal/referencedocs
		HashMap<String, String> map_collection_alma = t.get_values(0, "data/input/mappingdetails" + File.separator + "collection_AlmaName.xls", 0, 1, 0);

		for (Map.Entry<String, String> entry : map_collection_alma.entrySet()) {

			String collectionStringResource = getCollectionResource(entry.getKey());

			if(collectionStringResource.length() > 0 )
			{
				System.out.println("====Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				addTriple(model, collectionStringResource,hasKBtitle,entry.getValue());     
			}
			else
			{
				System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				System.out.println("no resource");
			}
		}

		// Output the RDF model in RDF/XML format
		StringWriter writer = new StringWriter();
		model.write(writer, "RDF/XML");
		String rdfXml = writer.toString();
		//System.out.println(rdfXml); // For debugging purposes

		// Save RDF/XML to file
		saveRdfToFile(rdfXml, "/Users/chenganj/git/entitlement_loader/data/output/ALMApredicatemapping/outputALMApredicatemapping.rdf");

		// Load triples into MarkLogic
		loadRdfIntoMarkLogic(rdfXml);

		// Close the client
		client.release();
		System.out.println("RDF data loaded successfully!");
	}
	
	public static void main(String[] args) { 	        

		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(client);

		// Create RDF model using Apache Jena
		Model model = ModelFactory.createDefaultModel();

		String  hasKBtitle = "http://scholarsportal.info/ontologies/core/kbtitle"; 

		Get_from_Excel t = new Get_from_Excel();
		// Excel sheet is generated from https://gitlab.scholarsportal.info/ejournals/Loader/-/tree/master/info/scholarsportal/referencedocs
		HashMap<String, String> map_collection_alma = t.get_values(0, "data/input/mappingdetails" + File.separator + "collection_AlmaName.xls", 0, 1, 0);

		for (Map.Entry<String, String> entry : map_collection_alma.entrySet()) {

			String collectionStringResource = getCollectionResource(entry.getKey());

			if(collectionStringResource.length() > 0 )
			{
				System.out.println("====Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				addTriple(model, collectionStringResource,hasKBtitle,entry.getValue());     
			}
			else
			{
				System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				System.out.println("no resource");
			}
		}

		// Output the RDF model in RDF/XML format
		StringWriter writer = new StringWriter();
		model.write(writer, "RDF/XML");
		String rdfXml = writer.toString(); 
		
		// Save RDF/XML to file
		saveRdfToFile(rdfXml, "/Users/chenganj/git/entitlement_loader/data/output/ALMApredicatemapping/outputALMApredicatemapping.rdf");


		String  hasKBfilename = "http://scholarsportal.info/ontologies/core/kbfilename"; 
		
		// Excel sheet is generated from https://gitlab.scholarsportal.info/ejournals/Loader/-/tree/master/info/scholarsportal/referencedocs
		map_collection_alma = t.get_values(0, "data/input/mappingdetails" + File.separator + "collection_AlmaName.xls", 0, 2, 0);

		for (Map.Entry<String, String> entry : map_collection_alma.entrySet()) {

			String collectionStringResource = getCollectionResource(entry.getKey());

			if(collectionStringResource.length() > 0 )
			{
				System.out.println("====Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				addTriple(model, collectionStringResource,hasKBfilename,entry.getValue());     
			}
			else
			{
				System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue() +  ", collectionresource: " + collectionStringResource);
				System.out.println("no resource");
			}
		}

		// Output the RDF model in RDF/XML format
		writer = new StringWriter();
		model.write(writer, "RDF/XML");
		rdfXml = writer.toString(); 

		// Save RDF/XML to file
		saveRdfToFile(rdfXml, "/Users/chenganj/git/entitlement_loader/data/output/ALMApredicatemapping/outputALMAFileNamepredicatemapping.rdf");
		
		// Load triples into MarkLogic
		loadRdfIntoMarkLogic(rdfXml);

		// Close the client
		client.release();
		System.out.println("RDF data loaded successfully!");
	}

	private static String getCollectionResource(String name) {
		String query = "PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
				"   select ?a   where {   " + 
				" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Collection> . "+
				" ?a sp:id '" + name + "' }" ;

		//System.out.println(query);
		QueryExecution execution = QueryExecutionFactory.create(query, dsg.toDataset()); 

		int n = 1;
		String output = "";
		for (ResultSet results = execution.execSelect(); results.hasNext();  n++) {
			QuerySolution solution = results.next();
			//System.out.println("Solution #" + n + ": "  + solution.get("a").asResource()  );
			output = solution.get("a").asResource().toString();
		} 

		return output;

	}

	// Function to save RDF/XML to file
	public static void saveRdfToFile(String rdfXml, String fileName) {
		try (FileWriter fileWriter = new FileWriter(fileName)) {
			fileWriter.write(rdfXml);
			System.out.println("RDF/XML saved to file: " + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Function to load RDF triples into MarkLogic
	public static void loadRdfIntoMarkLogic(String rdfXml) {
		String graphURI = ""; 

		graphURI = "http://scholarsportal.info/graphs/Collection"; 
		File directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/ALMApredicatemapping");   		
		File filesList[]  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 
		client.release();

	}

	private static void loadFiles(File[] filesList, String graphURI) {

		for(File file : filesList) {

			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", ""); 
			try {
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println(" RDF load for " + tripleFilename + " - completed ");
		} 

		System.out.println(" RDF load for " + graphURI + " - completed ");

	}

	public static void loadGraph(String filename, String graphURI, String format) {

		FileHandle tripleHandle =  new FileHandle(new File(filename)).withMimetype(format);
		//graphMgr.write(graphURI, tripleHandle);  writes new graph always
		graphMgr.merge(graphURI, tripleHandle);  // merge to the existing graph
	}

	private static void addTriple(Model model, String subject, String predicate, String object) {
		Resource subj = model.createResource(subject);
		Property pred = model.createProperty(predicate);
		Resource obj = model.createResource(object);
		Statement stmt = model.createStatement(subj, pred, object);
		model.add(stmt);
	}
} 
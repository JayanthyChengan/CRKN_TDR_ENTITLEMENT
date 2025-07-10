package com.marklogic.semantics.xsl;

//Resource :  https://docs.marklogic.com/guide/java/semantics#id_88689  
import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory; 
import com.marklogic.client.io.FileHandle; 
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes; 

public class loadGraph {


	static String host = "mlenodetest1.scholarsportal.info";
	static int port =  Integer.parseInt("8003");  //"Entitlement_Semantics"
	//Integer.parseInt("8075"); ; //eresources_linkeddata
	static String user = "loader";
	static String pass = "Loader123";

	static DatabaseClient client = DatabaseClientFactory.newClient(
			host, port, "Entitlement_Semantics",
			new DatabaseClientFactory.DigestAuthContext(
					user,pass));


	static private GraphManager graphMgr = client.newGraphManager();


	// Load managed triples from a file into a graph in MarkLogic
	public static void loadGraph(String filename, String graphURI, String format) {

		FileHandle tripleHandle =  new FileHandle(new File(filename)).withMimetype(format);
		//graphMgr.write(graphURI, tripleHandle);  writes new graph always
		graphMgr.merge(graphURI, tripleHandle);  // merge to the existing graph
	}

	// Delete a graph. Unmmanaged triples are unaffected.
	public static void deleteGraph(String graphURI) {
		System.out.println("Deleting graph " + graphURI);
		graphMgr.delete(graphURI);
	}


	public static void main(String[] args) {

		//http://mlenodetest1.scholarsportal.info:8003/v1/graphs/things?iri=http%3a//scholarsportal.info/ontologies/core/Collection

		//deleteGraph("http://scholarsportal.info/graphs/Journal");


		/*
		http://scholarsportal.info/graphs/Collection
		http://scholarsportal.info/graphs/Coverage
		http://scholarsportal.info/graphs/Event
		http://scholarsportal.info/graphs/Journal
		http://scholarsportal.info/graphs/License
		http://scholarsportal.info/graphs/Network
		http://scholarsportal.info/graphs/Organization
		http://scholarsportal.info/graphs/Portfolio
		http://scholarsportal.info/graphs/ROR
		 */
		
		
		//deleteALLGraphs();
		
		
		//System.exit(0);;


		/*

		String graphURItest = ""; 

		graphURItest = "http://scholarsportal.info/graphs/Journal"; 
		File directoryPathtest = new File("/Users/chenganj/git/entitlement_loader/data/output/journal");  
		File filesListtest[]  = directoryPathtest.listFiles(); 
		loadFiles (filesListtest , graphURItest) ; 

		System.exit(0);;
		 */

		String graphURI = ""; 

	 

		graphURI = "http://scholarsportal.info/graphs/Collection" ; 
		File directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/collection");   
		File filesList[] = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/Coverage";
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/coverage");   
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/Event"; 
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/event");   
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/License";
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/license");   
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/Portfolio";
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/portfolio");   
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/Journal"; 
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/journal");   
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		graphURI = "http://scholarsportal.info/graphs/Organization"; 
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/perpetual/sage");   		
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 
		 	
		graphURI = "http://scholarsportal.info/graphs/Organization"; 
		directoryPath = new File("/Users/chenganj/git/entitlement_loader/data/output/perpetual/acs");   		
		filesList  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 

		System.out.println(" Completed  "  );

		client.release();


		/*

		for(File file : filesList) {

			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{
				try {
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}


		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}



		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{		
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}



		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{		
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}




		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}


		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(tripleFilename.contains("temp_"))
			{
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}


		for(File file : filesList) {
			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");

			{
				System.out.println(" RDF load for " + tripleFilename );
				loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
				System.out.print( " - completed ");
			}
		}

		 */


	}

	private static void deleteALLGraphs() {

		String graphURI = "";
 
		graphURI = "http://scholarsportal.info/graphs/Organization" ;   
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/Collection" ;   
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/Coverage";
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/Event"; 
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/License";
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/Portfolio";
		deleteGraph(graphURI);

		graphURI = "http://scholarsportal.info/graphs/Journal"; 	
		deleteGraph(graphURI);

		deleteGraph("http://scholarsportal.info/graphs/Network");

		deleteGraph("http://scholarsportal.info/graphs/ROR");


	}

	public static void loadFiles(File[] filesList, String graphURI) {

		for(File file : filesList) {

			String tripleFilename = file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			if(!tripleFilename.contains(".DS_Store"))
			{
				try {
					if(tripleFilename.contains("pacoverage"))
					{
						loadGraph(tripleFilename, "http://scholarsportal.info/graphs/Portfolio", RDFMimeTypes.RDFXML);
					}
					else 
					{
						loadGraph(tripleFilename, graphURI, RDFMimeTypes.RDFXML);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out.println(" RDF load for " + tripleFilename + " - completed ");
			}
		}

		System.out.println(" RDF load for " + graphURI + " - completed ");

	}
}

/*
 * 

http://scholarsportal.info/graphs/Collection
http://scholarsportal.info/graphs/Coverage
http://scholarsportal.info/graphs/Event
http://scholarsportal.info/graphs/Journal
http://scholarsportal.info/graphs/License
http://scholarsportal.info/graphs/Portfolio 




 */




/*  JC Commented
 * 
	//static private String tripleFilename = "data/scripts/works3.rdf" ; //"data/temp_acm_jats.rdf";
    //static private String tripleFilename =  "data/stylesheet/output/temp_jats_to_rdf.rdf" ;  // works
    //static private String tripleFilename =  "data/stylesheet/output/temp_collection_to_rdf.rdf";
    //static private String unmanagedTripleDocURI = "mothergoose.json";
    // Insert a document that includes an unmanaged triple.
    public static void addUnmanagedTriple() {
        System.out.println("Inserting doc containing an unmanaged triple...");
        StringHandle contentHandle = new StringHandle(
            "{ \"name\": \"Mother Goose\"," +
                "\"triple\" : {" +
                    "\"subject\" : \"http://example.org/marklogic/person/Mother_Goose\"," +
                    "\"predicate\" : \"http://example.org/marklogic/predicate/livesIn\"," +
                    "\"object\" : {" +
                      "\"value\" : \"London\"," +
                      "\"datatype\" : \"http://www.w3.org/2001/XMLSchema#string\"" +
            "} }  }").withFormat(Format.JSON);
        JSONDocumentManager jdm = client.newJSONDocumentManager();
        jdm.write(unmanagedTripleDocURI, contentHandle);
    }

    public static void deleteUnmanagedTriple() {
        System.out.println("Removing doc containing unmanaged triple...");
        JSONDocumentManager jdm = client.newJSONDocumentManager();
        jdm.delete(unmanagedTripleDocURI);
    }

    public static void readGraph(String graphURI, String format) {
        System.out.println("Reading graph " + graphURI);
        StringHandle triples = 
            graphMgr.read(graphURI, new StringHandle().withMimetype(format));
        System.out.println(triples);
    }

    // Delete a graph. Unmmanaged triples are unaffected.
    public static void deleteGraph(String graphURI) {
        System.out.println("Deleting graph " + graphURI);
        graphMgr.delete(graphURI);
    }

    // Evaluate a SPARQL query.
    public static void sparqlQuery() {
        SPARQLQueryManager qm = client.newSPARQLQueryManager();
        SPARQLQueryDefinition query = qm.newQueryDefinition(
            "SELECT ?person " +
            "WHERE { ?person <http://example.org/marklogic/predicate/livesIn> \"London\" }"
        );

        JsonNode results = 
            qm.executeSelect(query, new JacksonHandle()).get();
        JsonNode matches = results.path("results").path("bindings");
        System.out.println("SPARQL: Persons who live in London:");
        for (int i = 0; i < matches.size(); i++) {
            String subject = 
                matches.get(i).path("person").path("value").asText();
            System.out.println("  " + subject);
        }
    }

    public static void opticQuery() {
        RowManager rowMgr = client.newRowManager();
        PlanBuilder pb = rowMgr.newPlanBuilder();
        PlanPrefixer predPrefixer = 
            pb.prefixer("http://example.org/marklogic/predicate/");
        Plan plan = pb.fromTriples(
            pb.pattern(
                pb.col("person"), 
                predPrefixer.iri("livesIn"), 
                pb.xs.string("London")));

        RowSet<RowRecord> results = rowMgr.resultRows(plan);
        System.out.println("OPTIC: Persons who live in London:");
        for (RowRecord row: results) {
            System.out.println("  " + row.getString("person"));
        }
    }



readGraph(graphURI, RDFMimeTypes.RDFXML);

System.out.println("Creating graph 3 "  );



 * // Query the graph for persons who live in London. // Should find 2 matches.
 * sparqlQuery();
 * 
 * // Add a document containing an unmanaged triple. Query again. // Should find
 * 3 matches. addUnmanagedTriple(); sparqlQuery();
 * 
 * // Perform the same query using the Optic API opticQuery();
 * 
 * // Delete the created graph. Unmanaged triple remains. // Query should find 1
 * match. deleteGraph(graphURI); sparqlQuery();
 * 
 * // Remove the document containing the unmanaged triple. // Query should find
 * no matches. deleteUnmanagedTriple(); sparqlQuery();
 */






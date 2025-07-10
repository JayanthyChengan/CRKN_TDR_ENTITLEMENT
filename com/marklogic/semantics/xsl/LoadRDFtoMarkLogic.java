package com.marklogic.semantics.xsl;

 
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;

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

import javax.xml.xpath.XPathExpressionException;

public class LoadRDFtoMarkLogic {
		
	static String host = "mlenodetest1.scholarsportal.info";
	static int port =  Integer.parseInt("8003");  //"Entitlement_Semantics"
	static String user = "loader";
	static String pass = "Loader123";

	static DatabaseClient client = DatabaseClientFactory.newClient(
			host, port, "Entitlement_Semantics",
			new DatabaseClientFactory.DigestAuthContext(
					user,pass));
	
	static private GraphManager graphMgr = client.newGraphManager();
	
	
    public static void main(String[] args) {
        // Create MarkLogic client    	    	        

        // Define namespaces
        String orgPrefix = "http://scholarsportal.info/things/organization/";
        String rorLink = "http://scholarsportal.info/relatedrorlink";

        // Create RDF model using Apache Jena
        Model model = ModelFactory.createDefaultModel();
        
        String rorexternalid = "http://scholarsportal.info/ontologies/core/hasExternalIds";
        String  hasROR = "http://scholarsportal.info/ontologies/core/hasROR";
        String id = "http://scholarsportal.info/ontologies/core/id" ;
        
        
    	String[] univ = { "acadiauniv.", 
				 "algoma",  
				 "athabascauniv.",  
				 "bishop'suniv.",  		 "brandonuniv.", 
				 "brock", 
				 "capebretonuniv.",  
				 "capilanouniv.",  
				 "carleton",  		 "concordiauniv.", 
				 "concordiauniv.ofedmonton",  		 "dalhousie", 
				 "guelph",  
				 "hecmontréal",  
				 "institutnationaldelarecherchescientifique",  		 "kwantlenpolytechnicuniv.",  
				 "lakehead", 
				 "laurentian",  
				 "laurier",  
				 "macewanuniv.",  
				 "mcgilluniv.",  
				 "mcmaster",  
				 "memorialuniv.ofnewfoundland",  
				 "mountallisonuniv.",  		 "mountroyaluniv.",  
				 "mountsaintvincentuniv.",  		 "nipissing",  
				 "nscaduniv.",  
				 "ocad",  
				 "ottawa",  		 "polytechniquemontréal",  
				 "queens",  
				 "rmc",  		 "royalmilitarycollege",  
				 "royalroadsuniv.",  		 "ryerson",  		 "saintmary'suniv.",  
				 "simonfraseruniv.",  
				 "st.francisxavieruniv.", 
				 "theking'suniv.", 		 "thompsonriversuniv.",  		 "toronto",  
				 "trent",  
				 "trinitywesternuniv.",  		 "univ.demoncton", 
				 "univ.demontréal",  
				 "univ.desherbrooke", 
				 "univ.duquébecenabitibi-témiscamingue",  
				 "univ.duquébecenoutaouais",  
				 "univ.duquébecàchicoutimi",  		 "univ.duquébecàmontréal", 
				 "univ.duquébecàrimouski",  
				 "univ.duquébecàtrois-rivières",  
				 "univ.laval",  
				 "univ.ofalberta",  
				 "univ.ofbritishcolumbia", 
				 "univ.ofcalgary", 		 "univ.oflethbridge",  		 "univ.ofmanitoba",  
				 "univ.ofnewbrunswick",  
				 "univ.ofnorthernbritishcolumbia",  
				 "univ.ofprinceedwardisland", 
				 "univ.ofregina", 
				 "univ.ofsaskatchewan",  
				 "univ.ofthefraservalley",  
				 "univ.ofvictoria",  
				 "univ.ofwinnipeg",  		 "univ.sainte-anne",  
				 "univ.téluq",  
				 "uoit", 
				 "vancouverislanduniv.",  
				 "waterloo",  
				 "western",  
				 "windsor",  		 "york",  
				 "écoledetechnologiesupérieure",  
				 "écolenationaled'administrationpublique"};
		// Specify the URI of the document you want to read
		String[] docUri = {"/ror.org/00839we02",
				"/ror.org/0131d6623",
				"/ror.org/01y3xgc52",
				"/ror.org/051prj435",
				"/ror.org/02qp25a50",
				"/ror.org/056am2717",
				"/ror.org/052y05165",
				"/ror.org/046nfbs12",
				"/ror.org/02qtvee93",
				"/ror.org/04013rx15",
				"/ror.org/04013rx15",
				"/ror.org/01e6qks80",
				"/ror.org/01r7awg59",
				"/ror.org/05ww3wq27",
				"/ror.org/04td37d32",
				"/ror.org/04raxj885",
				"/ror.org/023p7mg82",
				"/ror.org/03rcwtr18",
				"/ror.org/00fn7gb05",
				"/ror.org/003s89n44",
				"/ror.org/01pxwe438",
				"/ror.org/02fa3aq29",
				"/ror.org/04haebc03",
				"/ror.org/03grc6f14",
				"/ror.org/04evsam41",
				"/ror.org/03g3p3b82",
				"/ror.org/05k14ba46",
				"/ror.org/03sscxa41",
				"/ror.org/059ncap70",
				"/ror.org/03c4mmv16",
				"/ror.org/05f8d4e86",
				"/ror.org/02y72wh86",
				"/ror.org/04yr71909",
				"/ror.org/04yr71909",
				"/ror.org/05w4ste42",
				"/ror.org/05g13zd79",
				"/ror.org/010zh7098",
				"/ror.org/0213rcc28",
				"/ror.org/01wcaxs37",
				"/ror.org/02p5gkq58",
				"/ror.org/01v9wj339",
				"/ror.org/03dbr7087",
				"/ror.org/03ygmq230",
				"/ror.org/01j2kd606",
				"/ror.org/029tnqt29",
				"/ror.org/0161xgx34",
				"/ror.org/00kybxq39",
				"/ror.org/02mqrrm75",
				"/ror.org/011pqxa69",
				"/ror.org/00y3hzd62",
				"/ror.org/002rjbv21",
				"/ror.org/049jtt335",
				"/ror.org/02xrw9r68",
				"/ror.org/04sjchr03",
				"/ror.org/0160cpw27",
				"/ror.org/03rmrcq20",
				"/ror.org/03yjb2x39",
				"/ror.org/044j76961",
				"/ror.org/02gfys938",
				"/ror.org/05nkf0n29",
				"/ror.org/025wzwv46",
				"/ror.org/02xh9x144",
				"/ror.org/03dzc0485",
				"/ror.org/010x8gc63",
				"/ror.org/04h6w7946",
				"/ror.org/04s5mat29",
				"/ror.org/02gdzyx04",
				"/ror.org/034msqq35",
				"/ror.org/007y6q934",
				"/ror.org/016zre027",
				"/ror.org/033wcvv61",
				"/ror.org/01aff2v68",
				"/ror.org/02grkyz14",
				"/ror.org/01gw3d370",
				"/ror.org/05fq50484",
				"/ror.org/0020snb74",
		"/ror.org/05wwfbb42"};

		for (int i = 0 ; i < univ.length ; i++)
		{
			addTriple(model, orgPrefix + univ[i], rorexternalid, orgPrefix + univ[i]+"/externalIds");
	        addTriple(model, orgPrefix + univ[i]+"/externalIds",hasROR,orgPrefix + univ[i]+"/externalIds/ROR");
	        addTriple(model, orgPrefix + univ[i]+"/externalIds/ROR",id,"https:/"+docUri[i]);
	        
	         
		}
		
	 
		 // Output the RDF model in RDF/XML format
        StringWriter writer = new StringWriter();
        model.write(writer, "RDF/XML");
        String rdfXml = writer.toString();
        System.out.println(rdfXml); // For debugging purposes
        
        // Save RDF/XML to file
        saveRdfToFile(rdfXml, "data/output/RORpredicatemapping/outputpredicatemapping.rdf");

        // Load triples into MarkLogic
        loadRdfIntoMarkLogic(rdfXml);
        
        
        /*
        // Convert the model to an InputStream
        InputStream rdfInputStream = new ByteArrayInputStream(model.toString().getBytes());

        // Load RDF data into MarkLogic
        InputStreamHandle handle = new InputStreamHandle(rdfInputStream);
        handle.setMimetype(RDFMimeTypes.RDFXML);

        // Replace or merge graph as needed
        graphManager.write("/university-data", handle);
        */

        // Close the client
        client.release();
        System.out.println("RDF data loaded successfully!");
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
		
		graphURI = "http://scholarsportal.info/graphs/ROR"; 
		File directoryPath = new File("data/output/RORpredicatemapping");   		
		File filesList[]  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 
		client.release();
		
    }
    
    private static void loadFiles(File[] filesList, String graphURI) {

		for(File file : filesList) {

			String tripleFilename = file.getPath(); // file.getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
			//if(tripleFilename.contains("collection_acs_to_rdf"))
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
        Statement stmt = model.createStatement(subj, pred, obj);
        model.add(stmt);
    }
}

/*
https://api.ror.org/v2/organizations?query.advanced=%22 acadia university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 algoma %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 athabasca university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 bishop's university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 brandon university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 brock %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 cape breton university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 capilano university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 carleton %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 concordia university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 concordia university of edmonton %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 dalhousie %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 guelph %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 hec montréal %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 hospitals %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 institut national de la recherche scientifique %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 kwantlen polytechnic university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 lakehead %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 laurentian %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 laurier %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 macewan university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 mcgill university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 mcmaster %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 memorial university of newfoundland %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 mount allison university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 mount royal university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 mount saint vincent university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 nipissing %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 nscad university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 ocad %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 ontario tech university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 ottawa %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 polytechnique montréal %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22queen's university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 rmc %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 royal military college %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 royal roads university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 ryerson %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 saint mary's university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 simon fraser university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 st francis xavier university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 the king's university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 thompson rivers university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 toronto %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 trent %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 trinity western university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university de moncton %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university de montréal %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university de sherbrooke %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec en abitibi-témiscamingue %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec en outaouais %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec à chicoutimi %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec à montréal %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec à rimouski %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university du québec à trois-rivières %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university laval %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of alberta %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of british columbia %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of calgary %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of lethbridge %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of manitoba %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of new brunswick %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of northern british columbia %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of prince edward island %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of regina %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of saskatchewan %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of the fraser valley %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of victoria %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university of winnipeg %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university sainte-anne %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 university téluq %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 uoit %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 vancouver island university%22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 waterloo %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 western %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 windsor %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 york %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 école de technologie supérieure %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 école nationale d'administration publique %22&filter=country.country_code:CA,types:education
https://api.ror.org/v2/organizations?query.advanced=%22 University of Toronto %22&filter=country.country_code:CA,types:education
*/
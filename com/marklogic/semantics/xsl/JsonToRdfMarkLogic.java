package com.marklogic.semantics.xsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.apache.jena.rdf.model.Property;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.xpath.XPathExpressionException;

public class JsonToRdfMarkLogic {



	static String host = "mlenodetest1.scholarsportal.info";
	static int port =  Integer.parseInt("8003");  //"Entitlement_Semantics"
	static int port2 = Integer.parseInt("8075"); ; //eresources_linkeddata
	static String user = "loader";
	static String pass = "Loader123";

	static DatabaseClient client = DatabaseClientFactory.newClient( host, port, "Entitlement_Semantics", new DatabaseClientFactory.DigestAuthContext(user,pass));

	static DatabaseClient client2 = DatabaseClientFactory.newClient( host, port2, "eresources_linkeddata", new DatabaseClientFactory.DigestAuthContext( user,pass));


	static private GraphManager graphMgr = client.newGraphManager();

	public static void main(String[] args) throws XPathExpressionException, IOException {

		// ror json is loaded to eresources_linkeddata
		// cd /data/MarkLogic/Modules/loader/dev/jayanthy/GitLab/eresources_linkeddata_loader

		// sudo ./loadRORJSON.sh > output_loadROR.out &
		
		// Read the json from the eresources_linkeddata database
		// create triples and load into Entitlement_Semantics database
 

		try {
			// Create an XMLDocumentManager instance
			XMLDocumentManager xmlDocManager = client2.newXMLDocumentManager();

			 
			// Specify the URI of the document you want to read
			String[] docUri = {"/ror.org/00839we02.json",
					"/ror.org/0131d6623.json",
					"/ror.org/01y3xgc52.json",
					"/ror.org/051prj435.json",
					"/ror.org/02qp25a50.json",
					"/ror.org/056am2717.json",
					"/ror.org/052y05165.json",
					"/ror.org/046nfbs12.json",
					"/ror.org/02qtvee93.json",
					"/ror.org/04013rx15.json",
					"/ror.org/04013rx15.json",
					"/ror.org/01e6qks80.json",
					"/ror.org/01r7awg59.json",
					"/ror.org/05ww3wq27.json",
					"/ror.org/04td37d32.json",
					"/ror.org/04raxj885.json",
					"/ror.org/023p7mg82.json",
					"/ror.org/03rcwtr18.json",
					"/ror.org/00fn7gb05.json",
					"/ror.org/003s89n44.json",
					"/ror.org/01pxwe438.json",
					"/ror.org/02fa3aq29.json",
					"/ror.org/04haebc03.json",
					"/ror.org/03grc6f14.json",
					"/ror.org/04evsam41.json",
					"/ror.org/03g3p3b82.json",
					"/ror.org/05k14ba46.json",
					"/ror.org/03sscxa41.json",
					"/ror.org/059ncap70.json",
					"/ror.org/03c4mmv16.json",
					"/ror.org/05f8d4e86.json",
					"/ror.org/02y72wh86.json",
					"/ror.org/04yr71909.json",
					"/ror.org/04yr71909.json",
					"/ror.org/05w4ste42.json",
					"/ror.org/05g13zd79.json",
					"/ror.org/010zh7098.json",
					"/ror.org/0213rcc28.json",
					"/ror.org/01wcaxs37.json",
					"/ror.org/02p5gkq58.json",
					"/ror.org/01v9wj339.json",
					"/ror.org/03dbr7087.json",
					"/ror.org/03ygmq230.json",
					"/ror.org/01j2kd606.json",
					"/ror.org/029tnqt29.json",
					"/ror.org/0161xgx34.json",
					"/ror.org/00kybxq39.json",
					"/ror.org/02mqrrm75.json",
					"/ror.org/011pqxa69.json",
					"/ror.org/00y3hzd62.json",
					"/ror.org/002rjbv21.json",
					"/ror.org/049jtt335.json",
					"/ror.org/02xrw9r68.json",
					"/ror.org/04sjchr03.json",
					"/ror.org/0160cpw27.json",
					"/ror.org/03rmrcq20.json",
					"/ror.org/03yjb2x39.json",
					"/ror.org/044j76961.json",
					"/ror.org/02gfys938.json",
					"/ror.org/05nkf0n29.json",
					"/ror.org/025wzwv46.json",
					"/ror.org/02xh9x144.json",
					"/ror.org/03dzc0485.json",
					"/ror.org/010x8gc63.json",
					"/ror.org/04h6w7946.json",
					"/ror.org/04s5mat29.json",
					"/ror.org/02gdzyx04.json",
					"/ror.org/034msqq35.json",
					"/ror.org/007y6q934.json",
					"/ror.org/016zre027.json",
					"/ror.org/033wcvv61.json",
					"/ror.org/01aff2v68.json",
					"/ror.org/02grkyz14.json",
					"/ror.org/01gw3d370.json",
					"/ror.org/05fq50484.json",
					"/ror.org/0020snb74.json",
					"/ror.org/05wwfbb42.json"};

			for (int i = 0 ; i < docUri.length ; i++)
			{
				// Use StringHandle to read the document content as a String
				StringHandle readHandle = new StringHandle();
				xmlDocManager.read(docUri[i], readHandle);

				// Get the document content
				String documentContent = readHandle.get();

				// Print the content of the XML document
				//System.out.println("Document Content: ");
				//System.out.println(documentContent);
				
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(documentContent);

				// Create an empty RDF model
				Model model = ModelFactory.createDefaultModel();

				// Define namespace for j.0
				String ns = "http://scholarsportal.info/ontologies/core/";
				model.setNsPrefix("sp", ns);

				// Extract triples and add them to the RDF model (using the root subject)
				String rootSubjectUri = rootNode.get("id").asText();
				Resource rootSubject = model.createResource(rootSubjectUri);
				extractTriples(rootNode, rootSubject, model, ns);

				// Output the RDF model in RDF/XML format
				StringWriter writer = new StringWriter();
				model.write(writer, "RDF/XML");
				String rdfXml = writer.toString();
				//System.out.println(rdfXml); // For debugging purposes

				// Save RDF/XML to file
				saveRdfToFile(rdfXml, "data/output/ROR/output.rdf");

				// Load triples into MarkLogic
				loadRdfIntoMarkLogic(rdfXml);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Release the client
			client2.release();
			client.release();
		}

/*
		String json = "";//source.getTextContent().toString()  ; 
		//"{ \"id\":\"https://ror.org/03dbr7087\", \"name\":\"University of Toronto\", \"email_address\":\"\", \"ip_addresses\":[], \"established\":1827, \"types\":[\"Education\"], \"relationships\":[{\"label\":\"Baycrest Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/03gp5b411\"}, {\"label\":\"Campbellford Memorial Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/022ave037\"}, {\"label\":\"Centre for Addiction and Mental Health\", \"type\":\"Related\", \"id\":\"https://ror.org/03e71c577\"}, {\"label\":\"Creative Destruction Lab\", \"type\":\"Related\", \"id\":\"https://ror.org/02p6a3b37\"}, {\"label\":\"Holland Bloorview Kids Rehabilitation Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/03qea8398\"}, {\"label\":\"Hospital for Sick Children\", \"type\":\"Related\", \"id\":\"https://ror.org/057q4rt57\"}, {\"label\":\"Humber River Regional Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/02gj19t78\"}, {\"label\":\"Institute for Circumpolar Health Research\", \"type\":\"Related\", \"id\":\"https://ror.org/0390kp681\"}, {\"label\":\"Lunenfeld-Tanenbaum Research Institute\", \"type\":\"Related\", \"id\":\"https://ror.org/01s5axj25\"}, {\"label\":\"Mount Sinai Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/05deks119\"}, {\"label\":\"North York General Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/05b3hqn14\"}, {\"label\":\"St. Michael's Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/04skqfp25\"}, {\"label\":\"Sunnybrook Health Science Centre\", \"type\":\"Related\", \"id\":\"https://ror.org/03wefcv03\"}, {\"label\":\"Sunnybrook Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/008kn1a71\"}, {\"label\":\"Surrey Place Centre\", \"type\":\"Related\", \"id\":\"https://ror.org/01tw7ew41\"}, {\"label\":\"The Scarborough Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/04pzgb662\"}, {\"label\":\"Toronto East General Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/03sm16s30\"}, {\"label\":\"Trillium Health Centre\", \"type\":\"Related\", \"id\":\"https://ror.org/03v6a2j28\"}, {\"label\":\"University Health Network\", \"type\":\"Related\", \"id\":\"https://ror.org/042xt5161\"}, {\"label\":\"Waypoint Centre for Mental Health Care\", \"type\":\"Related\", \"id\":\"https://ror.org/0548x8e24\"}, {\"label\":\"West Park Healthcare Centre\", \"type\":\"Related\", \"id\":\"https://ror.org/037y13578\"}, {\"label\":\"Women's College Hospital\", \"type\":\"Related\", \"id\":\"https://ror.org/03cw63y62\"}, {\"label\":\"Canadian Institute for Theoretical Astrophysics\", \"type\":\"Child\", \"id\":\"https://ror.org/0265wc016\"}, {\"label\":\"Fields Institute for Research in Mathematical Sciences\", \"type\":\"Child\", \"id\":\"https://ror.org/03zzj3f20\"}, {\"label\":\"Ted Rogers Centre for Heart Research\", \"type\":\"Child\", \"id\":\"https://ror.org/00cgnj660\"}, {\"label\":\"Beaufort Lagoon Ecosystems Long Term Ecological Research Network\", \"type\":\"Related\", \"id\":\"https://ror.org/055a54548\"}], \"addresses\":[{\"lat\":43.70011, \"lng\":-79.4163, \"state\":null, \"state_code\":null, \"city\":\"Toronto\", \"geonames_city\":{\"id\":6167865, \"city\":\"Toronto\", \"geonames_admin1\":{\"name\":\"Ontario\", \"id\":6093943, \"ascii_name\":\"Ontario\", \"code\":\"CA.08\"}, \"geonames_admin2\":{\"name\":null, \"id\":null, \"ascii_name\":null, \"code\":null}, \"license\":{\"attribution\":\"Data from geonames.org under a CC-BY 3.0 license\", \"license\":\"http://creativecommons.org/licenses/by/3.0/\"}, \"nuts_level1\":{\"name\":null, \"code\":null}, \"nuts_level2\":{\"name\":null, \"code\":null}, \"nuts_level3\":{\"name\":null, \"code\":null}}, \"postcode\":null, \"primary\":false, \"line\":null, \"country_geonames_id\":6251999}], \"links\":[\"http://www.utoronto.ca/\"], \"aliases\":[], \"acronyms\":[], \"status\":\"active\", \"wikipedia_url\":\"https://en.wikipedia.org/wiki/University_of_Toronto\", \"labels\":[{\"label\":\"Université de Toronto\", \"iso639\":\"fr\"}], \"country\":{\"country_name\":\"Canada\", \"country_code\":\"CA\"}, \"external_ids\":{\"ISNI\":{\"preferred\":null, \"all\":[\"0000 0001 2157 2938\"]}, \"FundRef\":{\"preferred\":\"501100003579\", \"all\":[\"501100003579\", \"501100000154\", \"501100000086\", \"501100000064\", \"100008386\", \"501100007921\", \"501100008097\", \"501100007224\", \"100009036\", \"501100000181\", \"501100002323\", \"501100002827\", \"501100003474\", \"501100004730\", \"501100006151\"]}, \"OrgRef\":{\"preferred\":\"7955325\", \"all\":[\"7955325\", \"358828\", \"221783\", \"32774\", \"333619\", \"5303285\", \"251833\", \"599409\", \"1724690\"]}, \"Wikidata\":{\"preferred\":\"Q180865\", \"all\":[\"Q180865\", \"Q6120110\", \"Q3551675\"]}, \"GRID\":{\"preferred\":\"grid.17063.33\", \"all\":\"grid.17063.33\"}}}";

		//System.out.println(json);

		System.exit(0);

		try {
			// Parse JSON string
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(json);

			// Create an empty RDF model
			Model model = ModelFactory.createDefaultModel();

			// Define namespace for j.0
			String ns = "http://scholarsportal.info/ontologies/core/";
			model.setNsPrefix("sp", ns);

			// Extract triples and add them to the RDF model (using the root subject)
			String rootSubjectUri = rootNode.get("id").asText();
			Resource rootSubject = model.createResource(rootSubjectUri);
			extractTriples(rootNode, rootSubject, model, ns);

			// Output the RDF model in RDF/XML format
			StringWriter writer = new StringWriter();
			model.write(writer, "RDF/XML");
			String rdfXml = writer.toString();
			System.out.println(rdfXml); // For debugging purposes

			// Save RDF/XML to file
			saveRdfToFile(rdfXml, "/Users/chenganj/git/entitlement_loader/data/stylesheet/output/ROR/output.rdf");

			// Load triples into MarkLogic
			loadRdfIntoMarkLogic(rdfXml);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		*/
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


	// Function to extract triples from a JSON object
	public static void extractTriples(JsonNode node, Resource subject, Model model, String ns) {
		Iterator<String> fieldNames = node.fieldNames();

		while (fieldNames.hasNext()) {
			String predicate = fieldNames.next();
			JsonNode valueNode = node.get(predicate);

			if (valueNode.isObject()) {
				// For any nested object, create a URI and relate it to the root subject
				String nestedObjectUri = subject.getURI() + "/" + predicate;
				Resource nestedResource = model.createResource(nestedObjectUri);
				extractTriples(valueNode, nestedResource, model, ns); // Recursively extract nested triples

				// Add a property linking the root subject to the nested object
				Property prop = model.createProperty(ns + "has_" + predicate);
				subject.addProperty(prop, nestedResource);
			} else if (valueNode.isArray()) {
				// Handle arrays by iterating over elements
				for (JsonNode arrayElement : valueNode) {
					if (arrayElement.isObject()) {
						// For objects inside arrays, create URIs and recursively extract triples
						Resource arrayResource = model.createResource(subject.getURI() + "/" + predicate + "_item");
						extractTriples(arrayElement, arrayResource, model, ns);
						Property prop = model.createProperty(ns + "has_" + predicate);
						subject.addProperty(prop, arrayResource);
					} else {
						// Add simple values directly as properties
						Property prop = model.createProperty(ns + predicate);
						subject.addProperty(prop, arrayElement.asText());
					}
				}
			} else if (!valueNode.asText().isEmpty()) {
				// Add simple property-value pairs to the root subject
				Property prop = model.createProperty(ns + predicate);
				subject.addProperty(prop, valueNode.asText());
			}
		}
	}

	// Function to load RDF triples into MarkLogic
	public static void loadRdfIntoMarkLogic(String rdfXml) {

		/*
        // Connect to the MarkLogic server
        DatabaseClient client = DatabaseClientFactory.newClient("mlenodetest1.scholarsportal.info", 8010,
                new DatabaseClientFactory.DigestAuthContext("loader", "Loader123"));

        // Use GraphManager to manage triples
        GraphManager graphManager = client.newGraphManager();

        // Insert the RDF/XML data into the triple store
        StringHandle handle = new StringHandle(rdfXml);
        handle.setFormat(Format.XML);

        // Write the RDF/XML data to the default graph
        graphManager.write("/example/graph", handle);

        // Close the database client connection
        client.release();

		 */
		String graphURI = ""; 

		graphURI = "http://scholarsportal.info/graphs/ROR"; 
		File directoryPath = new File("data/output/ROR");   		
		File filesList[]  = directoryPath.listFiles(); 
		loadFiles (filesList , graphURI) ; 
		

	}

	private static void loadFiles(File[] filesList, String graphURI) {

		for(File file : filesList) {

			String tripleFilename = file.getPath(); // .getAbsolutePath().replace("/Users/chenganj/git/entitlement_loader/", "");
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




}

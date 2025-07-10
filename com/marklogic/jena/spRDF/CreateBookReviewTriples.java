package com.marklogic.jena.spRDF;

import com.marklogic.client.DatabaseClient;
import com.marklogic.ejournals.client.*; 
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections; 
import com.marklogic.ejournals.client.DocumentRead;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory; 


import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.graph.GraphFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class CreateBookReviewTriples {

	private DatabaseClient db_client;
	private MarkLogicDatasetGraph dsg;
	//private DatabaseClient client_Ejournals;
	Semantics_Utilities semantics_utilities;

	DocumentRead documentRead = new DocumentRead(); 

	// 
	String ns 		=	"http://scholarsportal.info/";
	String dc 		= 	"http://purl.org/dc/elements/1.1/";
	String dcterms 	=	"http://purl.org/dc/terms/";
	String dcmitype = 	"http://purl.org/dc/dcmitype/";
	String fabio	=	"http://purl.org/spar/fabio/";
	String prism	=	"http://prismstandard.org/namespaces/basic/2.0/" ; 
	String sp 		=	"http://scholarsportal.info/ontologies/core/";
	String rdf 		= 	"http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String bibo 	= 	"http://purl.org/ontology/bibo/";
	String prov		=	"http://www.w3.org/ns/prov#";
	String owl		=	"http://www.w3.org/2002/07/owl#";
	String foaf		=	"http://xmlns.com/foaf/0.1/" ;

	String turtle = 
			"@prefix ns: <http://scholarsportal.info/> . "
					+ "@prefix dc: <http://purl.org/dc/elements/1.1/> . "
					+ "@prefix dcterms: <http://purl.org/dc/terms/> . "
					+ "@prefix dcmitype: <http://purl.org/dc/dcmitype/> . "
					+ "@prefix fabio: <http://purl.org/spar/fabio/> . "
					+ "@prefix prism: <http://prismstandard.org/namespaces/basic/2.0/> . " 
					+ "@prefix sp: <http://scholarsportal.info/ontologies/core/> . "
					+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix bibo: <http://purl.org/ontology/bibo/> . "
					+ "@prefix prov: <http://www.w3.org/ns/prov#> . "
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix foaf: <http://xmlns.com/foaf/0.1/> . ";


	String articleGraphModelName = "http://scholarsportal.info/graphs/Articles";
	String bookGraphmodelName = "http://scholarsportal.info/graphs/Book"; 

	Resource articleCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Article");
	Resource bookCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Book"); 

	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath(); 
	XPathFactory factory = XPathFactory.newInstance();
	MyNamespaceContext myC = new MyNamespaceContext();

	Model articleModel = ModelFactory.createDefaultModel(); 
	Model bookModel = ModelFactory.createDefaultModel(); 

	Dataset dataset = null;


	public CreateBookReviewTriples() {
		
		// JC restapi connection pending
				//
		//db_client =  Utils.loadBookProps(); //rest api connection to the database
		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(db_client);

		semantics_utilities = new Semantics_Utilities();



		myC.setNamespace("xml","http://www.w3.org/XML/1998/namespace");
		myC.setNamespace("custom","http://scholarsportal.info/metadata");
		myC.setNamespace("sfx","http://scholarsportal.info/sfx-headings");

		xpath = factory.newXPath();
		xpath.setNamespaceContext(myC);

	}

	public static void main(String[] args) throws NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException, Exception {
		// TODO Auto-generated method stub
		CreateBookReviewTriples ct = new CreateBookReviewTriples();
		ct.run();  
	}


	public void run() throws ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, Exception {
		// TODO Auto-generated method stub

		dsg.clear();
		System.out.print("\n TESTing\n");

		//System.exit(0);;
		dataset = dsg.toDataset(); 
		
		//String a_uri = "/00223913/v76i0002/176_ttbioaeatmol.xml";

		BufferedReader in = new BufferedReader(new FileReader("tmpFiles/bookreview.txt"));
		String strLine = "";

		while ((strLine = in.readLine()) != null)
		{ 
			System.out.print("\n strLine: " + strLine+"\n");
 
			try{

				System.out.println("Make a model and load the turtle into it (client-side)"); 

				RDFDataMgr.read(bookModel,  new StringReader(turtle), "", Lang.TURTLE);
				RDFDataMgr.read(articleModel,  new StringReader(turtle), "", Lang.TURTLE); 


				System.out.println("Store the model in MarkLogic.");


				addNamedModel(articleGraphModelName, articleModel);
				addNamedModel(bookGraphmodelName, bookModel); 
				
				//System.exit(0);;

				int length = strLine.split(" ").length;
				String a_uri = strLine.split(" ")[length-1];

				System.out.println("Make a triple by hand."); 

				String article_graphid = hashCode(a_uri);

				System.out.println("article_graphid....."+ article_graphid);

				Resource articleResource = ResourceFactory.createResource("http://scholarsportal.info/things/article/"+article_graphid);

				//sp:id
				semantics_utilities.addIdStatement(a_uri, articleResource, articleModel);

				//spgraphId
				semantics_utilities.addgraphIdStatement(article_graphid, articleResource, articleModel);

				//rdf:type 
				semantics_utilities.addtypeStatement(articleCoreObjectResource , articleResource, articleModel);

				
				 //bibo:reviewOf   
				ArrayList<String> bookList = addBook(strLine.split(" ")[0]);
				for (int s = 0 ; s < bookList.size() ; s ++)
				{
					Property property = ResourceFactory.createProperty(bibo + "reviewOf");
					Resource contribResource = ResourceFactory.createResource(bookList.get(s));
					Statement statement = createStatement(articleResource,property, contribResource);
					articleModel.add( statement );
				}


				System.out.println("Combine models and save"); 
				//dataset.addNamedModel(articleGraphModelName, articleModel);
				dataset.getNamedModel(articleGraphModelName).add(articleModel);

				System.out.println("DONE");

				//System.exit(0);
			}


			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}

		// for first time
		//dataset.addNamedModel(articleGraphModelName, articleModel);

	}

	private ArrayList<String> addBook(String booksource) throws Exception {
		// TODO Auto-generated method stub

		ArrayList<String> book_iri = new ArrayList<String>();

		//Make a triple by hand. 
		Model bookTriples = ModelFactory.createDefaultModel();



		String bookStringResource = getBookResource(booksource);

		if(bookStringResource.length() == 0 )
		{

			String book_graphid = hashCode(booksource);

			String s = "http://scholarsportal.info/things/book/"+book_graphid;

			book_iri.add(s);

			Resource bookResource = ResourceFactory.createResource(s);

			//sp:id
			semantics_utilities.addIdStatement(booksource, bookResource, bookTriples);

			//spgraphId
			semantics_utilities.addgraphIdStatement(book_graphid, bookResource, bookTriples);

			//rdf:type 
			semantics_utilities.addtypeStatement(bookCoreObjectResource , bookResource, bookTriples);





		}
		else
		{
			book_iri.add(bookStringResource);
		}


		bookModel.add(bookTriples);
		dataset.getNamedModel(bookGraphmodelName).add(bookModel);

		//dataset.addNamedModel(bookGraphmodelName, bookModel);
		//
		return book_iri;
	}




	private void addNamedModel(String graphModelName, Model model) {

		if (dataset.getNamedModel(graphModelName) == null)
		{
			dataset.addNamedModel(graphModelName, model);
		}

	}


	private String getBookResource(String bookcommonid)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Book> . "+
						" ?a sp:id '" + bookcommonid + "' }", dsg.toDataset()); 

		int n = 1;
		String output = "";
		for (ResultSet results = execution.execSelect();
				results.hasNext();
				n++) {
			QuerySolution solution = results.next();
			//System.out.println("Solution #" + n + ": "  + solution.get("a").asResource()  );
			output = solution.get("a").asResource().toString();
		} 
		return output;
	}


	private String hashCode(String input) throws NoSuchAlgorithmException
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

	private Statement createStatement(Resource subjectResource, Property rdfTypeProperty, Literal createStringLiteral) {
		// TODO Auto-generated method stub
		Statement statement = ResourceFactory.createStatement(subjectResource,rdfTypeProperty,createStringLiteral );
		return statement;
	}

	private Statement createStatement(Resource subjectResource, Property rdfTypeProperty, Resource objectResource) {
		// TODO Auto-generated method stub
		Statement statement = ResourceFactory.createStatement(subjectResource,rdfTypeProperty,objectResource );
		return statement;
	}



}

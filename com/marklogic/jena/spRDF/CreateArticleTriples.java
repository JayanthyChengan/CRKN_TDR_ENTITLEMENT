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

public class CreateArticleTriples { 
	

	private DatabaseClient db_client;
	private MarkLogicDatasetGraph dsg;
	//private DatabaseClient client_Ejournals;
	Semantics_Utilities semantics_utilities;

	DocumentRead documentRead = new DocumentRead();
	private MarkLogicClient ml_client;

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
	String subjectGraphmodelName = "http://scholarsportal.info/graphs/Subject";
	String contribGraphmodelName = "http://scholarsportal.info/graphs/Contribution";

	Resource articleCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Article");
	Resource contributionCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Contribution");
	Resource subjectCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Subject");

	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath(); 
	XPathFactory factory = XPathFactory.newInstance();
	MyNamespaceContext myC = new MyNamespaceContext();

	Model articleModel = ModelFactory.createDefaultModel(); 
	Model contribModel = ModelFactory.createDefaultModel();
	Model subjectModel = ModelFactory.createDefaultModel();

	Dataset dataset = null;


	public CreateArticleTriples() {
		
		// JC restapi connection pending
		//db_client =  Utils.loadPropsAndInit();//rest api connection to the database
		//client_Ejournals =  Utils.loadEjournalsProps();
		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(db_client);

		semantics_utilities = new Semantics_Utilities();

		ml_client = new MarkLogicClient( );


		myC.setNamespace("xml","http://www.w3.org/XML/1998/namespace");
		myC.setNamespace("custom","http://scholarsportal.info/metadata");
		myC.setNamespace("sfx","http://scholarsportal.info/sfx-headings");

		xpath = factory.newXPath();
		xpath.setNamespaceContext(myC);

	}

	public static void main(String[] args) throws NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException, Exception {
		// TODO Auto-generated method stub
		CreateArticleTriples ct = new CreateArticleTriples();
		ct.run();  
	}


	public void run() throws ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, Exception {
		// TODO Auto-generated method stub

		dataset = dsg.toDataset(); 

		//String a_uri = "/00223913/v76i0002/176_ttbioaeatmol.xml";

		BufferedReader in = new BufferedReader(new FileReader("tmpFiles/toc.txt"));
		String issn = "";

		while ((issn = in.readLine()) != null)
		{
			//issn ="/21512205/";//"/00972088/";
			System.out.print("\nISSN: " + issn+"\n");

			try{
				String query = "cts:uri-match('"+issn +"*/*.xml')"; 

				HashMap<String,String> params = new HashMap<String, String>() {};

				List<String> uris = ml_client.runXQUERY(db_client , query, params);


				if (uris.size() == 0) {
					System.out.println("No articles"); 
				} else {

					System.out.println("Number of articles for ISSN:" + issn + "   " +  uris.size());

					for (String a_uri : uris) {
						System.out.println(a_uri); 



						Document source = Utils.getDocument(db_client , a_uri); 

						DocumentMetadataHandle metadata = Utils.getMetadata(db_client , a_uri); 


						System.out.println("Make a model and load the turtle into it (client-side)"); 

						RDFDataMgr.read(contribModel,  new StringReader(turtle), "", Lang.TURTLE);
						RDFDataMgr.read(articleModel,  new StringReader(turtle), "", Lang.TURTLE);
						RDFDataMgr.read(subjectModel,  new StringReader(turtle), "", Lang.TURTLE);


						System.out.println("Store the model in MarkLogic.");


						addNamedModel(articleGraphModelName, articleModel);
						addNamedModel(contribGraphmodelName, contribModel);
						addNamedModel(subjectGraphmodelName, subjectModel);



						System.out.println("Make a triple by hand.");
						//Model articleModel = ModelFactory.createDefaultModel();

						String article_graphid = hashCode(a_uri);

						System.out.println("article_graphid....."+ article_graphid);

						Resource articleResource = ResourceFactory.createResource("http://scholarsportal.info/things/article/"+article_graphid);

						//sp:id
						semantics_utilities.addIdStatement(a_uri, articleResource, articleModel);

						//spgraphId
						semantics_utilities.addgraphIdStatement(article_graphid, articleResource, articleModel);

						//rdf:type 
						semantics_utilities.addtypeStatement(articleCoreObjectResource , articleResource, articleModel);

						//issn
						String issnPrint =   xpath.evaluate("/article/front/journal-meta/issn", source ).replace("-", "");
						Property property = ResourceFactory.createProperty(sp + "issnPrint"); 
						Literal createStringLiteral = ResourceFactory.createStringLiteral(issnPrint) ;
						Statement statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//volume 
						String volume =   xpath.evaluate("/article/front/article-meta/volume", source );
						property = ResourceFactory.createProperty(sp + "volume"); 
						createStringLiteral = ResourceFactory.createStringLiteral(volume) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//issue
						String issue =   xpath.evaluate("/article/front/article-meta/issue", source );
						property = ResourceFactory.createProperty(sp + "issue"); 
						createStringLiteral = ResourceFactory.createStringLiteral(issue) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//pagestart
						String fpage =   xpath.evaluate("/article/front/article-meta/fpage", source );
						property = ResourceFactory.createProperty(sp + "pageStart"); 
						createStringLiteral = ResourceFactory.createStringLiteral(fpage) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//doi
						String doi =   xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", source );
						property = ResourceFactory.createProperty(sp + "doi"); 
						createStringLiteral = ResourceFactory.createStringLiteral(doi) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//doiLink
						property = ResourceFactory.createProperty(sp + "doiLink"); 
						createStringLiteral = ResourceFactory.createStringLiteral("http://dx.doi.org/"+doi) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//spgraphId
						property = ResourceFactory.createProperty(sp + "spgraphId"); 
						createStringLiteral = ResourceFactory.createStringLiteral(article_graphid) ;
						statement = createStatement(articleResource,property,createStringLiteral);
						articleModel.add( statement );

						//hasJournal
						String journalStringResource = getJournalResource(issnPrint); 
						property = ResourceFactory.createProperty(sp + "hasJournal");
						Resource journalResource = ResourceFactory.createResource(journalStringResource);
						statement = createStatement(articleResource,property,journalResource);
						articleModel.add( statement );

						//hasSubject
						ArrayList<String> subjectList = addSubject( issnPrint);

						for (int s = 0 ; s < subjectList.size() ; s ++)
						{
							property = ResourceFactory.createProperty(sp + "hasSubject");
							Resource subjectResource = ResourceFactory.createResource(subjectList.get(s));
							statement = createStatement(journalResource,property,subjectResource);
							articleModel.add( statement );
						}

						//hasContribution

						ArrayList<String> contribList = addContribution(source);
						for (int s = 0 ; s < contribList.size() ; s ++)
						{
							property = ResourceFactory.createProperty(sp + "hasContribution");
							Resource contribResource = ResourceFactory.createResource(contribList.get(s));
							statement = createStatement(articleResource,property, contribResource);
							articleModel.add( statement );
						}

						//getCollectionResource
						//		https://github.com/MarkLogicUniversity/top-songs/blob/master/java/Unit12/src/com/marklogic/training/LoadDocumentWithOptions.java

						DocumentCollections collections = metadata.getCollections();
						System.out.println(" ----------------- " + collections.size());
						System.out.println(" ----------------- " + collections.toString());

						Object[] c_array = collections.toArray();
						for (int c = 0 ; c < c_array.length ; c++)
						{
							String collection_name = c_array[c].toString();
							System.out.println("collection name **********************" + collection_name);
							String collectionStringResource = getCollectionResource(collection_name);
							if(collectionStringResource.length() > 0 )
							{
								property = ResourceFactory.createProperty(sp + "hasCollection");
								Resource collectionResource = ResourceFactory.createResource(collectionStringResource);
								statement = createStatement(articleResource,property,collectionResource);
								articleModel.add( statement );
							}
						}
						System.out.println("Combine models and save"); 
						//dataset.addNamedModel(articleGraphModelName, articleModel);
						dataset.getNamedModel(articleGraphModelName).add(articleModel);

						System.out.println("DONE");

						//System.exit(0);
					}
				}

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

	private ArrayList<String> addContribution(Document source) throws Exception {
		// TODO Auto-generated method stub

		ArrayList<String> contrib_iri = new ArrayList<String>();

		//Store the model in MarkLogic.


		//Make a triple by hand. 
		Model contribTriples = ModelFactory.createDefaultModel();


		NodeList c_list = (NodeList) xpath.evaluate("//contrib", source, XPathConstants.NODESET);
		if (c_list != null){

			for(int i =0; i < c_list.getLength(); i++)
			{
				String surname = xpath.evaluate("name/surname", c_list.item(i));  
				String givenname = xpath.evaluate("name/given-names", c_list.item(i));
				//String c_id = c_name.replace("/collection/", "collection/" ).replace("/" , "_");


				String contribStringResource = getContribResource(surname, givenname);

				System.out.println("contribStringResource name ================ "  +  contribStringResource + "  ================ "   + surname + "  ================ "  +  givenname );
				if(contribStringResource.length() == 0 )
				{

					String contrib_graphid = hashCode(givenname+"-"+surname);

					String s = "http://scholarsportal.info/things/contributions/"+contrib_graphid;

					contrib_iri.add(s);

					Resource contribResource = ResourceFactory.createResource(s);

					//sp:id
					semantics_utilities.addIdStatement(givenname+"-"+surname, contribResource, contribTriples);

					//spgraphId
					semantics_utilities.addgraphIdStatement(contrib_graphid, contribResource, contribTriples);

					//rdf:type 
					semantics_utilities.addtypeStatement(contributionCoreObjectResource , contribResource, contribTriples);

					//sp:role 
					Property property = ResourceFactory.createProperty(sp + "role");
					Literal createStringLiteral = ResourceFactory.createStringLiteral("author") ;
					Statement statement = createStatement(contribResource, property,	createStringLiteral);				
					contribTriples.add( statement );

					//sp:publishedFamilyName
					property = ResourceFactory.createProperty(sp + "publishedFamilyName");
					createStringLiteral = ResourceFactory.createStringLiteral(surname) ;
					statement = createStatement(contribResource, property,	createStringLiteral);				
					contribTriples.add( statement );

					//sp:publishedGivenName
					property = ResourceFactory.createProperty(sp + "publishedGivenName");
					createStringLiteral = ResourceFactory.createStringLiteral(givenname) ;
					statement = createStatement(contribResource, property,	createStringLiteral);				
					contribTriples.add( statement );

					//hasAffiliation
					//TODO



				}
				else
				{
					contrib_iri.add(contribStringResource);
				}

			}
		}

		contribModel.add(contribTriples);
		dataset.getNamedModel(contribGraphmodelName).add(contribModel);

		//	dataset.addNamedModel(contribGraphmodelName, contribModel);
		//
		return contrib_iri;
	}

	private String getContribResource(String surname, String givenname) {
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Contribution> . "+
						" ?a sp:publishedGivenName '" + givenname + "' ." +
						" ?a sp:publishedFamilyName '" + surname + "' }", dsg.toDataset()); 

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

	private ArrayList<String> addSubject(  String issn) throws XPathExpressionException, IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		ArrayList<String> subject_iri = new ArrayList<String>(); 
		System.out.println("===========" + "/"+issn+"/journal-info.xml" );
		Document source = documentRead.documentRead(db_client  , "/"+issn+"/journal-info.xml");




		System.out.println("Make a triple by hand.");
		Model subjectTriples = ModelFactory.createDefaultModel();

		/*
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath(); 

		MyNamespaceContext myC = new MyNamespaceContext();
		myC.setNamespace("xml","http://www.w3.org/XML/1998/namespace");
		myC.setNamespace("custom","http://scholarsportal.info/metadata");
		myC.setNamespace("sfx","http://scholarsportal.info/sfx-headings");


		XPathFactory factory = XPathFactory.newInstance();

		xpath = factory.newXPath();
		xpath.setNamespaceContext(myC);
		 */

		NodeList s_list = (NodeList) xpath.evaluate("//sfx:subject", source, XPathConstants.NODESET);
		if (s_list != null){

			for(int i =0; i < s_list.getLength(); i++)
			{
				String subject_name =   s_list.item(i).getTextContent(); 
				//String c_id = c_name.replace("/collection/", "collection/" ).replace("/" , "_");


				String subjectStringResource = getSubjectResource(subject_name);

				System.out.println("Subject name ================ " + subject_name + "  =================== " + subjectStringResource);
				if(subjectStringResource.length() == 0 )
				{

					String subject_graphid = hashCode(subject_name);

					String s = "http://scholarsportal.info/things/subject/"+subject_graphid;

					subject_iri.add(s);

					Resource subjectResource = ResourceFactory.createResource(s);

					//sp:id
					semantics_utilities.addIdStatement(subject_name, subjectResource, subjectTriples);

					//spgraphId
					semantics_utilities.addgraphIdStatement(subject_graphid, subjectResource, subjectTriples);

					//rdf:type
					semantics_utilities.addtypeStatement(subjectCoreObjectResource , subjectResource, subjectTriples);

					//dcterms:title
					Property property = ResourceFactory.createProperty(dcterms + "title");
					Literal createStringLiteral = ResourceFactory.createStringLiteral(subject_name) ;
					Statement statement = createStatement(subjectResource, property,	createStringLiteral);				
					subjectTriples.add( statement );


				}
				else
				{
					subject_iri.add(subjectStringResource);
				}

			}
		}

		subjectModel.add(subjectTriples);
		dataset.getNamedModel(subjectGraphmodelName).add(subjectModel);
		//dataset.addNamedModel(subjectGraphmodelName, subjectModel);

		return subject_iri;
	}

	/*private void loadArticles() {
		// TODO Auto-generated method stub


		DocumentRead documentRead = new DocumentRead();
		documentRead.documentWrite(client_Ejournals , "sp-article-semantics/src/main/java/data/10826467/v09i0002/183_mjdw1.xml");
		documentRead.documentWrite(client_Ejournals , "client.xml");
		//documentRead.runShortcut(client,"");
		System.out.println("Loaded collection xml.");
	}*/

	private void addNamedModel(String graphModelName, Model model) {

		if (dataset.getNamedModel(graphModelName) == null)
		{
			dataset.addNamedModel(graphModelName, model);
		}

	}

	private String getJournalResource(String issn)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX prism: <http://prismstandard.org/namespaces/basic/2.0/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Journal> . "+
						" ?a prism:issn '" + issn + "' }", dsg.toDataset()); 

		int n = 1;

		/*for (ResultSet results = execution.execSelect();
                results.hasNext();
                n++) {
            QuerySolution solution = results.next();
            System.out.println("Solution #" + n + ": "  + solution.get("a").asResource()  );
        }*/
		//System.exit(0);
		return execution.execSelect().next().get("a").asResource().toString();
	}

	private String getCollectionResource(String collection)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Collection> . "+
						" ?a sp:id '" + collection + "' }", dsg.toDataset()); 

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

	private String getSubjectResource(String subject)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Subject> . "+
						" ?a sp:id '" + subject + "' }", dsg.toDataset()); 

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

	private String createJournalResource(String issn, Model eJorunalsTriples) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub



		//System.exit(0);

		String journal_graphid = hashCode(issn);

		Resource journalResource = ResourceFactory.createResource("http://scholarsportal.info/things/journals/"+journal_graphid);

		//rdf:type
		Property property = ResourceFactory.createProperty(rdf + "type");
		Resource objectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Journal");
		Statement statement = createStatement(journalResource,property,objectResource);
		eJorunalsTriples.add( statement );

		//issn

		property = ResourceFactory.createProperty(sp + "issn"); 
		Literal createStringLiteral = ResourceFactory.createStringLiteral(issn) ;
		statement = createStatement(journalResource,property,createStringLiteral);
		eJorunalsTriples.add( statement );

		//spgraphId
		property = ResourceFactory.createProperty(sp + "spgraphId"); 
		createStringLiteral = ResourceFactory.createStringLiteral(journal_graphid) ;
		statement = createStatement(journalResource,property,createStringLiteral);
		eJorunalsTriples.add( statement );

		//collection

		//title

		return "http://scholarsportal.info/things/journals/"+journal_graphid;

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


	public void run2() throws ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, XPathExpressionException {

		//createStringLiteral = ResourceFactory.createStringLiteral(article_graphid+"test") ;

		//dsg.clear();
		Dataset dataset = dsg.toDataset(); 


		String a_uri = "/00223913/v76i0002/176_ttbioaeatmol.xml";
		Document source = Utils.getDocument(db_client  , a_uri);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath(); 

		System.out.println("Make a model and load the turtle into it (client-side)");
		Model articleModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(articleModel,  new StringReader(turtle), "", Lang.TURTLE);

		System.out.println("Store the model in MarkLogic.");
		dataset.addNamedModel(articleGraphModelName, articleModel);

		System.out.println("Make a triple by hand.");
		Model localTriples = ModelFactory.createDefaultModel();

		String article_graphid = hashCode(a_uri);
		Resource articleResource = ResourceFactory.createResource("http://scholarsportal.info/things/article/"+article_graphid);

		//spgraphId
		Property property = ResourceFactory.createProperty(sp + "spgraphId"); 
		Literal createStringLiteral = ResourceFactory.createStringLiteral(article_graphid+"test") ;
		Statement statement = createStatement(articleResource,property,createStringLiteral);
		//localTriples.remove( statement );


		System.out.println("Combine models and save");
		articleModel.remove(statement);
		dataset.addNamedModel(articleGraphModelName, articleModel);

	}

}

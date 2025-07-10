
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

import com.marklogic.client.DatabaseClient;
import com.marklogic.ejournals.client.DocumentRead;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory;
import com.marklogic.semantics.jena.client.JenaDatabaseClient;
import com.marklogic.semantics.xsl.XslExternalProc;

import ch.qos.logback.classic.Level;

import org.apache.jena.datatypes.xsd.XSDDatatype;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class CreateCollectionTriples {
	//http://mlenodetest1.scholarsportal.info:8003/v1/graphs/things 
	// http://mlenodetest1.scholarsportal.info:8003/v1/graphs

	private DatabaseClient client; 
	private MarkLogicDatasetGraph dsg;
	Semantics_Utilities semantics_utilities;

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
	String geo 		= "http://www.geonames.org/ontology/";

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
					+ "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
					+ "@prefix geo: <http://www.geonames.org/ontology/> . ";


	String collectionGraphModelName = "http://scholarsportal.info/graphs/Collection";
	String licenseGraphModelName = "http://scholarsportal.info/graphs/License";
	String coverageGraphModelName = "http://scholarsportal.info/graphs/Coverage";
	String portfolioGraphModelName = "http://scholarsportal.info/graphs/Portfolio";
	String journalGraphModelName = "http://scholarsportal.info/graphs/Journal";
	String eventGraphModelName = "http://scholarsportal.info/graphs/Event";
	String organizationGraphModelName = "http://scholarsportal.info/graphs/Organization";

	Resource collectionCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Collection");
	Resource licenseCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/License");
	Resource coverageCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Coverage");
	Resource portfolioCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Portfolio");
	Resource journalCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Journal");
	Resource eventCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Event");
	Resource organizationCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Organization");

	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath(); 
	XPathFactory factory = XPathFactory.newInstance();
	MyNamespaceContext myC = new MyNamespaceContext();

	Model collectionModel = ModelFactory.createDefaultModel(); 
	Model licenseModel = ModelFactory.createDefaultModel();
	Model coverageModel = ModelFactory.createDefaultModel();
	Model portfolioModel = ModelFactory.createDefaultModel();
	Model journalModel = ModelFactory.createDefaultModel();
	Model eventModel = ModelFactory.createDefaultModel();
	Model organizationModel = ModelFactory.createDefaultModel(); 

	Dataset dataset = null;

	// 

	public CreateCollectionTriples() throws Exception {
		client = Utils.load_mlenodetest1_Entitlements_Props_orig();
		//client_Ejournals =  Utils.loadEjournalsProps();
		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(client);
		semantics_utilities = new Semantics_Utilities();

		//dsg.clear();
		/*
		//Step 1:
		System.out.println("Load /LOGS ");
		load();

		//System.exit(0);
		System.out.println("\t Completed Successfully ");
		System.out.println("Load collections ");
		run("//collection");
		run("//sub-collection");
		//prepetualrun();
		 * 
		 * 
		 */

		// comment jc on 12 Jan 2024 
		 prepetualrun_new();

		 //SpringerGuelphPA - this method written for Wei to find max/min date from excel - this is temporary task
		//readentitlementexcel();
	}


	public void load() throws Exception {
		//Step 1:
		Utils.addDocument(client , "collection-groups.xml" );
		Utils.addDocument(client , "client.xml" );
	}

	public void run(String xpathcondition) throws Exception {


		//Step 2:
		// Make some triples
		//dsg.clear();
		//System.exit(0);;
		dataset = dsg.toDataset();

		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,"data"+File.separator+"collection_licensetype.xls",0,1,0);	
		HashMap<String, String> map_licensetypes = t.get_values(0, "data"+File.separator+"collection_licensetype.xls",0,2,0);

		Document source = Utils.getDocument(client ,  "/LOGS/collection-groups.xml"); 

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath(); 

		RDFDataMgr.read(collectionModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(licenseModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(coverageModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(portfolioModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(journalModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(eventModel,  new StringReader(turtle), "", Lang.TURTLE);

		System.out.println("Store the model in MarkLogic.");

		addNamedModel(collectionGraphModelName , collectionModel);
		addNamedModel(licenseGraphModelName , licenseModel);
		addNamedModel(coverageGraphModelName , coverageModel);
		addNamedModel(portfolioGraphModelName , portfolioModel);
		addNamedModel(journalGraphModelName , journalModel);
		addNamedModel(eventGraphModelName , eventModel);

		//System.exit(0);;

		System.out.println("Make a triple by hand.");


		NodeList c_list = (NodeList) xpath.evaluate(xpathcondition, source, XPathConstants.NODESET);

		if (c_list != null){

			for(int i =0; i < c_list.getLength(); i++)//c_list.getLength()
			{

				String c_name = xpath.evaluate("@name", c_list.item(i));  
				System.out.println(" \t Load  " +c_name);

				/*
				if( 
						c_name.equalsIgnoreCase("/collection/tandf/tandfwestern") || 
						c_name.equalsIgnoreCase("/collection/springer") || 
						c_name.equalsIgnoreCase("/collection/jstor") )
				{
					System.out.println("  Skipped Collection for now " + c_name);
				}
				else
				 */
				{
					Literal collectionType = ResourceFactory.createStringLiteral(map_collectiontypes.get(c_name)+"") ;
					c_name = c_name.replace("collection", "SPcollection");
					String licenseType = map_licensetypes.get(c_name);

					String collectionStringResource = getCollectionResource(c_name);
					System.out.println("Collection resource found " + collectionStringResource);
					//System.exit(0);

					if(collectionStringResource.length() == 0 )
					{
						//String collection_graphid = hashCode(c_name);
						Resource collectionResource = ResourceFactory.createResource("http://scholarsportal.info/things/collection"+c_name);

						//sp:id				
						semantics_utilities.addIdStatement(c_name, collectionResource, collectionModel);

						//spgraphId
						//semantics_utilities.addgraphIdStatement(collection_graphid, collectionResource, collectionModel);

						//rdf:type				
						semantics_utilities.addtypeStatement(collectionCoreObjectResource , collectionResource, collectionModel);

						//dcterms:title
						Property property = ResourceFactory.createProperty(dcterms + "title");
						Literal createStringLiteral = ResourceFactory.createStringLiteral(c_name) ;
						Statement statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
						collectionModel.add( statement );

						//dc:publisher
						property = ResourceFactory.createProperty(dc + "publisher");
						createStringLiteral = ResourceFactory.createStringLiteral(c_name.split("/")[2]) ;
						statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
						collectionModel.add( statement );

						//sp:collectionType
						property = ResourceFactory.createProperty(sp + "collectionType");

						//map_collectiontypes.get(c_list.item(i));

						/*Iterator<String> itr_db = map_uris.keySet().iterator();

					RemoveDocuments removeDocument = new RemoveDocuments(delete_eventdetail);

					while(itr_db.hasNext())
					{
						uri = itr_db.next();
						if(uri.length() > 0 )
						{ 
							System.out.print("URI ----- " + uri );

							targetUri = map_uris.get(uri);
							System.out.println( " ----- " + targetUri );
						 */


						//createStringLiteral = ResourceFactory.createStringLiteral("perpetual");
						statement = semantics_utilities.createStatement(collectionResource, property,	collectionType);				
						collectionModel.add( statement );

						/*
							System.out.println("==========================");
							if(dataset == null)
							{
								System.out.println("dataset is null ==============================" );
							}
							else
							{
								System.out.println("dataset is NOT null ****************" );
							}
							System.exit(0);;

						 */
						//dcterms:license
						String l_name = xpath.evaluate("@license_tag", c_list.item(i)); 
						if(l_name.length() > 0 )
						{  					
							ArrayList<String> licenseList = addLicense( l_name , licenseType);

							for (int s = 0 ; s < licenseList.size() ; s ++)
							{
								property = ResourceFactory.createProperty(dcterms + "license");
								Resource licenseResource = ResourceFactory.createResource(licenseList.get(s));
								statement = createStatement(collectionResource,property,licenseResource);
								collectionModel.add( statement );
							}
						}

						Property p_temporal = ResourceFactory.createProperty(dcterms + "temporal");
						Property haspart_property = ResourceFactory.createProperty(dcterms + "hasPart");
						Property title_property = ResourceFactory.createProperty(dcterms + "title");


						//collection start date and end date
						String s_date = xpath.evaluate("@start-date", c_list.item(i)); 
						String e_date = xpath.evaluate("@end-date", c_list.item(i)); 

						if(s_date.length() > 0)
						{

						}
						else
						{
							s_date = "1200-01-01";
						}

						if(e_date.length() > 0)
						{

						}
						else
						{
							e_date = "2100-01-01";
						}

						ArrayList<String> coverageList = addCoverage( s_date , e_date , c_name);

						for (int s = 0 ; s < coverageList.size() ; s ++)
						{			
							Resource coverageResource = ResourceFactory.createResource(coverageList.get(s));
							statement = semantics_utilities.createStatement(collectionResource, p_temporal, coverageResource );
							collectionModel.add( statement );
						}


						if(xpathcondition.contains("//collection"))
						{

							NodeList i_list = (NodeList) xpath.evaluate("issn", c_list.item(i), XPathConstants.NODESET);

							if (i_list != null){

								for(int j =0; j < i_list.getLength(); j++)//i_list.getLength()
								{

									long start = System.currentTimeMillis();


									System.out.print ( "ISSN at " + j + " -- ");
									String i_id = i_list.item(j).getTextContent() ; 
									String issn_s_date = xpath.evaluate("@start-date", i_list.item(j)); 
									String issn_e_date = xpath.evaluate("@end-date", i_list.item(j)); 

									//System.out.println("========= ISSN =================");

									ArrayList<String> portfolioList = addportfolio(c_name , i_id, issn_s_date , issn_e_date );

									for (int s = 0 ; s < portfolioList.size() ; s ++)
									{			
										Resource portfolioResource = ResourceFactory.createResource(portfolioList.get(s));
										statement = semantics_utilities.createStatement(collectionResource, haspart_property, portfolioResource );
										collectionModel.add( statement );
									}

									// ...
									long finish = System.currentTimeMillis();
									long timeElapsed = finish - start;
									System.out.println(timeElapsed  );
								}
							}//end of issn loop

						}
						else if(xpathcondition.contains("//sub-collection"))
						{

							NodeList i_list = (NodeList) xpath.evaluate("parent::collection/issn", c_list.item(i), XPathConstants.NODESET);

							if (i_list != null){

								for(int j =0; j < i_list.getLength(); j++)//i_list.getLength()
								{
									String i_id = i_list.item(j).getTextContent() ; 
									String issn_s_date = xpath.evaluate("@start-date", i_list.item(j)); 
									String issn_e_date = xpath.evaluate("@end-date", i_list.item(j)); 

									ArrayList<String> portfolioList = addportfolio(c_name , i_id, issn_s_date , issn_e_date );

									for (int s = 0 ; s < portfolioList.size() ; s ++)
									{			
										Resource portfolioResource = ResourceFactory.createResource(portfolioList.get(s));
										statement = semantics_utilities.createStatement(collectionResource, haspart_property, portfolioResource );
										collectionModel.add( statement );
									}
								}
							}//end of issn loop

						}

						else if(xpathcondition.contains("premetual"))
						{

							BufferedReader reader = new BufferedReader(new FileReader("")); 
							String strLine;

							//Read File Line By Line
							while ((strLine = reader.readLine()) != null)   {
								String i_id = strLine ;  

								ArrayList<String> portfolioList = addportfolio(c_name , i_id, "" , "" );

								for (int s = 0 ; s < portfolioList.size() ; s ++)
								{			
									Resource portfolioResource = ResourceFactory.createResource(portfolioList.get(s));
									statement = semantics_utilities.createStatement(collectionResource, haspart_property, portfolioResource );
									collectionModel.add( statement );
								}
							}

						}

						Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event"+c_name+"/id:1" );
						Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
						statement = semantics_utilities.createStatement(collectionResource, sp_hasevent_property, eventResource );
						collectionModel.add( statement );

						addEvent(eventResource, "Collection");

						System.out.println("Combine models and save");
						dataset.getNamedModel(collectionGraphModelName).add(collectionModel);

						System.out.print("================== DONE ========================= "); 
						//System.exit(0);;
					}

					else
					{
						System.out.print("================== collection already added  ========================= ");
					}

					System.out.println(" \t loaded successfully ");
				}
			}
		}

		System.out.print("DONE");
	}


	private void addEvent(Resource eventResource ,String eventdetail) {
		//rdf:type
		semantics_utilities.addtypeStatement(eventCoreObjectResource , eventResource, eventModel);

		//sp:eventType
		Literal createStringLiteral = ResourceFactory.createStringLiteral("created") ;
		Property sp_haseventType_property = ResourceFactory.createProperty(sp + "eventType");
		Statement statement = semantics_utilities.createStatement(eventResource, sp_haseventType_property, createStringLiteral); 
		eventModel.add( statement );

		//sp:eventDetail
		createStringLiteral = ResourceFactory.createStringLiteral(eventdetail) ;
		Property sp_haseventDetail_property = ResourceFactory.createProperty(sp + "eventDetail");
		statement = semantics_utilities.createStatement(eventResource, sp_haseventDetail_property, createStringLiteral); 
		eventModel.add( statement );

		//dcterms:date

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(); 

		//createStringLiteral = ResourceFactory.createStringLiteral(dateFormat.format(date)) ;
		createStringLiteral = ResourceFactory.createTypedLiteral(dateFormat.format(date), XSDDatatype.XSDdate);
		Property dcterms_date = ResourceFactory.createProperty(dcterms + "date");
		statement = semantics_utilities.createStatement(eventResource, dcterms_date, createStringLiteral); 
		eventModel.add( statement );

		dataset.getNamedModel(eventGraphModelName).add(eventModel);

	}



	private ArrayList<String> addportfolio(String c_name, String i_id, String issn_s_date, String issn_e_date) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		//System.out.println("Start date  add portfolio  1 " );

		String ci_Id = c_name+ "/" +i_id;

		//System.out.println("Start date  add portfolio  ============ " + ci_Id);

		//System.out.println("========================== " + ci_Id + " ======================= ");

		ArrayList<String> portfolio_iri = new ArrayList<String>();
		//Model portfolioModel = ModelFactory.createDefaultModel(); 

		Property issn_property = ResourceFactory.createProperty(prism + "issn");
		Property p_temporal = ResourceFactory.createProperty(dcterms + "temporal");
		//System.out.println("Start date  add portfolio  ============ 0  "  );
		String collectionissnStringResource = "";//getcollectionissnResource(c_name , i_id);

		//System.out.println("Start date  add portfolio  ============ before if  "  );
		if(collectionissnStringResource.length() == 0 )
		{			
			//String portfolio_graphid = hashCode( ci_Id );
			//System.out.println("Start date  add portfolio  ============ in if  "  );
			String id = "http://scholarsportal.info/things/portfolio"+ci_Id;

			//System.out.println("Start date  add portfolio  ============ 1  "  );


			portfolio_iri.add(id);
			//System.out.println("Start date  add portfolio  ============ 2  "  );
			Resource portfolioResource = ResourceFactory.createResource(id);

			//sp:id
			semantics_utilities.addIdStatement(ci_Id, portfolioResource, portfolioModel);

			//spgraphId
			//semantics_utilities.addgraphIdStatement(portfolio_graphid, portfolioResource, portfolioModel);

			//rdf:type 
			semantics_utilities.addtypeStatement(portfolioCoreObjectResource , portfolioResource, portfolioModel);
			//System.out.println("Start date  add portfolio  ============ 3  "  );
			String journalStringResource = getJournalResource(i_id);

			//System.out.println("Start date  add portfolio  ============ journal resource before if  "  );
			if(journalStringResource.length() == 0 )
			{
				//String issn_graphid = hashCode( i_id);

				id = "http://scholarsportal.info/things/journal/"+i_id;

				Resource journalResource = ResourceFactory.createResource(id);

				//sp:id
				semantics_utilities.addIdStatement(i_id, journalResource, journalModel);

				//spgraphId
				//semantics_utilities.addgraphIdStatement(issn_graphid, journalResource, journalModel);

				//rdf:type 
				semantics_utilities.addtypeStatement(journalCoreObjectResource , journalResource, journalModel);

				//prism:issn
				Literal createStringLiteral = ResourceFactory.createStringLiteral(i_id) ;
				Statement statement = semantics_utilities.createStatement(journalResource, issn_property, createStringLiteral); 
				journalModel.add( statement ); 

				dataset.getNamedModel(journalGraphModelName).add(journalModel);

				Property sp_hasjournal_property = ResourceFactory.createProperty(sp + "hasJournal");		 
				statement = semantics_utilities.createStatement(portfolioResource, sp_hasjournal_property, journalResource );
				portfolioModel.add( statement );
			}
			else
			{			 	
				Property sp_hasjournal_property = ResourceFactory.createProperty(sp + "hasJournal");				
				Resource journalResource = ResourceFactory.createResource(journalStringResource);
				Statement statement = semantics_utilities.createStatement(portfolioResource, sp_hasjournal_property, journalResource );
				portfolioModel.add( statement );
			}

			if(issn_s_date.length() > 0)
			{

			}
			else
			{
				issn_s_date = "1200-01-01";
			}

			if(issn_e_date.length() > 0)
			{

			}
			else
			{
				issn_e_date = "2100-01-01";
			}

			//System.out.println("Start date  add portfolio  1 " );

			ArrayList<String> coverageList = addCoverage( issn_s_date , issn_e_date , ci_Id);

			//System.out.println("Start date  add portfolio  2 " );

			//System.out.println("SStart date  add portfolio  2  7 " );

			for (int s = 0 ; s < coverageList.size() ; s ++)
			{			
				//System.out.println("Start date  add portfolio  2  8 " );
				Resource coverageResource = ResourceFactory.createResource(coverageList.get(s));
				//System.out.println("Start date  9 " );
				Statement statement = semantics_utilities.createStatement(portfolioResource, p_temporal, coverageResource );
				//System.out.println("Start date  10 " );
				portfolioModel.add( statement );
				//System.out.println("Start date  11 " );
			}

			//System.out.println("Start date  12 " );
			dataset.getNamedModel(portfolioGraphModelName).add(portfolioModel);
			//System.out.println("Start date  13 " );

			Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event"+ci_Id+"/id:1" );
			Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
			Statement statement = semantics_utilities.createStatement(portfolioResource, sp_hasevent_property, eventResource );
			portfolioModel.add( statement );

			addEvent(eventResource , "Portfolio");

		}
		else
		{
			portfolio_iri.add(collectionissnStringResource);
		}

		return portfolio_iri;

	}


	private ArrayList<String> addCoverage(String s_date, String e_date , String id) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		ArrayList<String> coverage_iri = new ArrayList<String>();
		Model coverageTriples = ModelFactory.createDefaultModel(); 

		Property sp_dateStart = coverageTriples.createProperty(sp+"dateStart") ;
		Property sp_dateEnd = coverageTriples.createProperty(sp+"dateEnd") ;

		String coverageStringResource = getCoverageResourse(s_date , e_date);


		if(coverageStringResource.length() == 0 )
		{

			//String coverage_graphid = hashCode(s_date+"to"+e_date);

			String s = "http://scholarsportal.info/things/coverage"+id;

			coverage_iri.add(s);

			Resource coverageResource = ResourceFactory.createResource(s);


			//sp:id
			semantics_utilities.addIdStatement(id, coverageResource, coverageTriples);

			//spgraphId
			//semantics_utilities.addgraphIdStatement(coverage_graphid, coverageResource, coverageTriples);

			//rdf:type 
			semantics_utilities.addtypeStatement(coverageCoreObjectResource , coverageResource, coverageTriples);

			//sp:dateStart
			//System.out.println("Start date  2 " );
			//Literal createStringLiteral = ResourceFactory.createStringLiteral(s_date) ;
			Literal createStringLiteral = ResourceFactory.createTypedLiteral(s_date.trim(), XSDDatatype.XSDdate);
			Statement statement = semantics_utilities.createStatement(coverageResource, sp_dateStart, createStringLiteral); 
			coverageTriples.add( statement );

			//sp:dateEnd
			//System.out.println("Start date  3 " );
			//createStringLiteral = ResourceFactory.createStringLiteral(e_date) ;
			createStringLiteral = ResourceFactory.createTypedLiteral(e_date.trim(), XSDDatatype.XSDdate);			
			statement = semantics_utilities.createStatement(coverageResource, sp_dateEnd, createStringLiteral); 
			coverageTriples.add( statement );
			//System.out.println("Start date  4 " );

			coverageModel.add(coverageTriples);
			//System.out.println("Start date  5 " );
			dataset.getNamedModel(coverageGraphModelName).add(coverageModel);
			//System.out.println("Start date  6 " );
		}
		else
		{
			coverage_iri.add(coverageStringResource);
		}



		return coverage_iri;
	}


	private ArrayList<String> addLicense(String l_name, String licensetype) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		ArrayList<String> license_iri = new ArrayList<String>();
		Model licenseTriples = ModelFactory.createDefaultModel(); 

		String licenseStringResource = getLicenseResourse(l_name);

		//System.out.println("License name ================ " + l_name + "  =================== " + licenseStringResource);
		if(licenseStringResource.length() == 0 )
		{

			//String license_graphid = hashCode(l_name);

			String s = "http://scholarsportal.info/things/license/"+l_name;

			license_iri.add(s);

			Resource licenseResource = ResourceFactory.createResource(s);


			//sp:id
			semantics_utilities.addIdStatement(l_name, licenseResource, licenseTriples);

			//spgraphId
			//semantics_utilities.addgraphIdStatement(license_graphid, licenseResource, licenseTriples);

			//rdf:type 
			semantics_utilities.addtypeStatement(licenseCoreObjectResource , licenseResource, licenseTriples);

			//dcterms:title
			Property property = ResourceFactory.createProperty(dcterms + "title");
			Literal createStringLiteral = ResourceFactory.createStringLiteral(l_name) ;
			Statement statement = semantics_utilities.createStatement(licenseResource,property,createStringLiteral);
			licenseTriples.add( statement );

			Property type_property = ResourceFactory.createProperty(sp + "licenseType");
			createStringLiteral = ResourceFactory.createStringLiteral(licensetype+"") ;
			//createStringLiteral = ResourceFactory.createStringLiteral("perpetual");
			statement = semantics_utilities.createStatement(licenseResource, type_property,	createStringLiteral);				
			licenseTriples.add( statement );

			licenseModel.add(licenseTriples);

			//System.out.println("==========================");
			if(dataset == null)
			{
				//System.out.println("dataset is null ==============================" );
			}

			if ( dataset.getNamedModel(licenseGraphModelName) == null )
			{
				//System.out.println("==========================");
				dataset.getNamedModel(licenseGraphModelName).add(licenseModel);
			}
			else
			{
				dataset.getNamedModel(licenseGraphModelName).add(licenseModel);
			}

		}
		else
		{
			license_iri.add(licenseStringResource);
		}


		return license_iri;
	}

	private String getLicenseResourse(String l_name) {
		// TODO Auto-generated method stub

		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/License> . "+
						" ?a sp:id '" + l_name + "' }", dsg.toDataset()); 

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


	private String getCollectionResource(String name) {
		// TODO Auto-generated method stub
		//System.out.println("Find Collection Resource " );
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


	private String getCoverageResourse(String s_date, String e_date) {
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Coverage> . "+
						" ?a sp:id '" + s_date+"to"+e_date + "' }", dsg.toDataset()); 

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

	private String getJournalResource(String issn)
	{
		String s = "PREFIX prism: <http://prismstandard.org/namespaces/basic/2.0/> "+
				"   select ?a   where {   " + 
				" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Journal> . "+
				" ?a prism:issn '" + issn + "' }" ;

		//System.out.println(s);
		QueryExecution execution = QueryExecutionFactory.create(s, dsg.toDataset()); 
		//System.out.println("s.1");

		int n = 1;

		/*for (ResultSet results = execution.execSelect();
                results.hasNext();
                n++) {
            QuerySolution solution = results.next();
            System.out.println("Solution #" + n + ": "  + solution.get("a").asResource()  );
        }*/
		//System.exit(0); 
		String output = "";

		//ResultSet results1 = execution.execSelect();


		for (ResultSet results = execution.execSelect();  results.hasNext();   n++) {

			QuerySolution solution = results.next();

			output = solution.get("a").asResource().toString();
		} 


		return output;
	}

	private String getcollectionissnResource(String c_name, String i_id) {

		String s = "PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
				" select ?a   where {   " + 
				" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/portfolio> . "+
				" ?a sp:id '" + c_name+ "/" +i_id + "' }" ;


		QueryExecution execution = QueryExecutionFactory.create(
				s, dsg.toDataset()); 

		int n = 1;
		String output = "";
		for (ResultSet results = execution.execSelect();
				results.hasNext();
				n++) {
			QuerySolution solution = results.next();

			output = solution.get("a").asResource().toString();
		} 

		return output;
	}

	private Statement createStatement(Resource subjectResource, Property rdfTypeProperty, Resource objectResource) {
		// TODO Auto-generated method stub
		Statement statement = ResourceFactory.createStatement(subjectResource,rdfTypeProperty,objectResource );
		return statement;
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


		return sb.toString();
	}

	private void addNamedModel(String graphModelName, Model model) {

		if (dataset.getNamedModel(graphModelName) == null)
		{
			dataset.addNamedModel(graphModelName, model);
		}

	}

	public void prepetualrun( ) throws Exception {
		// Make some triples
		//dsg.clear();
		dataset = dsg.toDataset();

		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_types = t.get_values(1,"sp-entitlement-semantics/src/main/java/data/2015-2017ElseviersubscribedlistRev20180824.xls",0,1,0);
		HashMap<String, String> map_issns = t.get_values(0,"sp-entitlement-semantics/src/main/java/data/2015-2017ElseviersubscribedlistRev20180824.xls",0,1,13);

		RDFDataMgr.read(collectionModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(licenseModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(coverageModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(portfolioModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(journalModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(eventModel,  new StringReader(turtle), "", Lang.TURTLE);



		addNamedModel(collectionGraphModelName , collectionModel);
		addNamedModel(licenseGraphModelName , licenseModel);
		addNamedModel(coverageGraphModelName , coverageModel);
		addNamedModel(portfolioGraphModelName , portfolioModel);
		addNamedModel(journalGraphModelName , journalModel);
		addNamedModel(eventGraphModelName , eventModel); 

		String c_name = map_types.get("title") ; //  /SPcollection/perpetual/brock/elsevier/2017";
		c_name = "/SPcollection/" + c_name ; 
		String collectionStringResource = getCollectionResource(c_name);

		if(collectionStringResource.length() == 0 )
		{
			//String collection_graphid = hashCode(c_name);

			Resource collectionResource = ResourceFactory.createResource("http://scholarsportal.info/things/collection"+c_name);

			//sp:id				
			semantics_utilities.addIdStatement(c_name, collectionResource, collectionModel);

			//spgraphId
			//semantics_utilities.addgraphIdStatement(collection_graphid, collectionResource, collectionModel);

			//rdf:type				
			semantics_utilities.addtypeStatement(collectionCoreObjectResource , collectionResource, collectionModel);

			//dcterms:title
			Property property = ResourceFactory.createProperty(dcterms + "title");
			Literal createStringLiteral = ResourceFactory.createStringLiteral(c_name) ;
			Statement statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
			collectionModel.add( statement );

			//dc:publisher
			property = ResourceFactory.createProperty(dc + "publisher");
			createStringLiteral = ResourceFactory.createStringLiteral(map_types.get("publisher")) ;
			statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
			collectionModel.add( statement );

			//sp:collectionType
			property = ResourceFactory.createProperty(sp + "collectionType"); 
			createStringLiteral = ResourceFactory.createStringLiteral(map_types.get("hasCollectionType"));
			statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
			collectionModel.add( statement );


			Property p_temporal = ResourceFactory.createProperty(dcterms + "temporal");
			Property haspart_property = ResourceFactory.createProperty(dcterms + "hasPart"); 

			//collection start date and end date
			String s_date = map_types.get("dateStart").toString().toLowerCase(); // "1995-01-01" ; //
			String e_date = map_types.get("dateEnd").toString().toLowerCase(); //"2017-12-31" ; //

			//System.out.println("Start date " + s_date);
			//System.out.println("End date " + e_date);

			if(s_date.length() > 0)
			{

			}
			else
			{
				s_date = "1200-01-01";
			}

			if(e_date.length() > 0)
			{

			}
			else
			{
				e_date = "2100-01-01";
			}


			ArrayList<String> coverageList = addCoverage( s_date , e_date , c_name);


			for (int s = 0 ; s < coverageList.size() ; s ++)
			{			

				Resource coverageResource = ResourceFactory.createResource(coverageList.get(s));

				statement = semantics_utilities.createStatement(collectionResource, p_temporal, coverageResource );

				collectionModel.add( statement );

			}



			List mapKeys = new ArrayList(map_issns.keySet());
			Iterator keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String i_id = key.toString();


				if(i_id.length() > 4)
				{
					ArrayList<String> portfolioList = addportfolio(c_name , i_id.replace("-", ""), s_date , e_date );


					for (int s = 0 ; s < portfolioList.size() ; s ++)
					{			
						Resource portfolioResource = ResourceFactory.createResource(portfolioList.get(s));
						statement = semantics_utilities.createStatement(collectionResource, haspart_property, portfolioResource );
						collectionModel.add( statement );
					}
				}
			}

			Resource organizationResource = ResourceFactory.createResource("http://scholarsportal.info/things/organization/"+map_types.get("subscribeTo"));
			Property sp_SubscribeTo_property = ResourceFactory.createProperty(sp + "subscribeTo");
			statement = createStatement(organizationResource,sp_SubscribeTo_property,collectionResource);
			organizationModel.add( statement );

			Model model = dataset.getNamedModel(organizationGraphModelName);
			model.add(organizationModel); 

			Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event"+c_name+"/id:1" );
			Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
			statement = semantics_utilities.createStatement(collectionResource, sp_hasevent_property, eventResource );
			collectionModel.add( statement );

			addEvent(eventResource , map_types.get("eventDetail")); //   "list from CRKN" );// TO DO :


			dataset.getNamedModel(collectionGraphModelName).add(collectionModel);

			System.out.print("================== DONE ========================= "); 
			//System.exit(0);;
		}
		else
		{
			System.out.print("==================prepetual collection already added  ========================= ");
		}
		System.out.print("DONE");
	}

	public void prepetualrun_new( ) throws Exception {
		// Make some triples
		//dsg.clear();
		dataset = dsg.toDataset();

		RDFDataMgr.read(collectionModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(licenseModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(coverageModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(portfolioModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(journalModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(eventModel,  new StringReader(turtle), "", Lang.TURTLE);



		addNamedModel(collectionGraphModelName , collectionModel);
		addNamedModel(licenseGraphModelName , licenseModel);
		addNamedModel(coverageGraphModelName , coverageModel);
		addNamedModel(portfolioGraphModelName , portfolioModel);
		addNamedModel(journalGraphModelName , journalModel);
		addNamedModel(eventGraphModelName , eventModel); 

		Get_from_Excel t = new Get_from_Excel();
		System.out.println("excel sheet astart");
		Map<String, List<String>> issnYearMap = t.readFromExcel("data/CRKN_PARightsTracking_SAGE.xls", (short)2, (short)7);
		Map<String, List<String>> issnYearSchoolMap = t.readFromExcel2("data/CRKN_PARightsTracking_SAGE.xls", (short)2, (short)7);
		System.out.println("excel sheet ");

		Iterator<String> x = issnYearMap.keySet().iterator();
		while (x.hasNext()) {
			String issn = x.next();
			//String value = (String) videoIDMap.get(key).get(0);
			System.out.println();


			List<String> new_year_ids = issnYearMap.get(issn);

			Iterator<String> sub_iter=new_year_ids.iterator();

			while(sub_iter.hasNext())
			{ 
				String year=sub_iter.next();
				System.out.println("Issn:"+ issn + "  mapped to: "  );
				System.out.println("year:"+year + "  mapped to: "  );

				List<String> new_school_ids = issnYearSchoolMap.get(issn+":"+year);

				Iterator<String> sub_iter2=new_school_ids.iterator();


				while(sub_iter2.hasNext())
				{ 
					String school=sub_iter2.next();
					System.out.println("\t" + school );

					String p_id = XslExternalProc.encodePath(issn, year);

					String id = "http://scholarsportal.info/things/portfolio/"+ p_id;

					System.out.println(id);
					Resource portfolioResource = ResourceFactory.createResource(id);


					//sp:id
					semantics_utilities.addIdStatement(p_id, portfolioResource, portfolioModel);

					//spgraphId
					//semantics_utilities.addgraphIdStatement(portfolio_graphid, portfolioResource, portfolioModel);

					//rdf:type 
					semantics_utilities.addtypeStatement(portfolioCoreObjectResource , portfolioResource, portfolioModel);



					Literal createStringLiteral = ResourceFactory.createStringLiteral((year).substring(0,4)) ;
					Property dcterms_date = ResourceFactory.createProperty(dcterms + "date");
					Statement statement = semantics_utilities.createStatement(portfolioResource, dcterms_date,	createStringLiteral);	
					portfolioModel.add(statement);


					Resource journalResource = ResourceFactory.createResource("http://scholarsportal.info/things/journal/"+issn.replace("-", ""));
					Property sp_hasjournal_property = ResourceFactory.createProperty(sp + "hasJournal");		 
					statement = semantics_utilities.createStatement(portfolioResource, sp_hasjournal_property, journalResource );
					portfolioModel.add( statement );


					Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event/"+p_id+"/id:1" );
					Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
					statement = semantics_utilities.createStatement(portfolioResource, sp_hasevent_property, eventResource );
					portfolioModel.add( statement );


					Resource organizationResource = ResourceFactory.createResource("http://scholarsportal.info/things/organization/"+school.toLowerCase().replace(" ", ""));
					Property sp_SubscribeTo_property = ResourceFactory.createProperty(sp + "hasPerpetualAccess");
					statement = createStatement(organizationResource,sp_SubscribeTo_property,portfolioResource);
					organizationModel.add( statement );

					//sp:id				
					semantics_utilities.addIdStatement(school, organizationResource, organizationModel);

					semantics_utilities.addtypeStatement(organizationCoreObjectResource , organizationResource, organizationModel);

					//sp:id
					Property property = ResourceFactory.createProperty(geo + "name");
					createStringLiteral = ResourceFactory.createStringLiteral(school) ;
					statement = ResourceFactory.createStatement(organizationResource,property,createStringLiteral ); 
					organizationModel.add( statement );


					dataset.getNamedModel(portfolioGraphModelName).add(portfolioModel);

					Model model = dataset.getNamedModel(organizationGraphModelName);
					model.add(organizationModel); 
				}



				/*

<http://scholarsportal.info/things/portfolio/MDAwMS02OTkzMjAwNi4w> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Portfolio> .
<http://scholarsportal.info/things/portfolio/MDAwMS02OTkzMjAwNi4w> <http://scholarsportal.info/ontologies/core/id> "MDAwMS02OTkzMjAwNi4w" .

<http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Portfolio> .
<http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> <http://purl.org/dc/terms/date> "2015" .
<http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> <http://scholarsportal.info/ontologies/core/hasEvent> <http://scholarsportal.info/things/event/MjA0Nzc0NzNjdXJyZW50/id:1> .
<http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> <http://scholarsportal.info/ontologies/core/hasJournal> <http://scholarsportal.info/things/journal/20477473> .
<http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> <http://scholarsportal.info/ontologies/core/id> "MjA0Nzc0NzNjdXJyZW50" .



<http://scholarsportal.info/things/organization/brock> <http://scholarsportal.info/ontologies/core/hasPerpetualAccess> <http://scholarsportal.info/things/portfolio/MjA0Nzc0NzNjdXJyZW50> .

				 */

			}
		}




		System.exit(0);

		System.out.print("DONE");
	}

	private ArrayList<String> addportfolio_new(String c_name, String i_id, String issn_s_date, String issn_e_date) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		//System.out.println("Start date  add portfolio  1 " );

		String ci_Id = c_name+ "/" +i_id;

		//System.out.println("Start date  add portfolio  ============ " + ci_Id);

		//System.out.println("========================== " + ci_Id + " ======================= ");

		ArrayList<String> portfolio_iri = new ArrayList<String>();
		//Model portfolioModel = ModelFactory.createDefaultModel(); 

		Property issn_property = ResourceFactory.createProperty(prism + "issn");
		Property p_temporal = ResourceFactory.createProperty(dcterms + "temporal");
		//System.out.println("Start date  add portfolio  ============ 0  "  );
		String collectionissnStringResource = "";//getcollectionissnResource(c_name , i_id);

		//System.out.println("Start date  add portfolio  ============ before if  "  );
		if(collectionissnStringResource.length() == 0 )
		{			
			//String portfolio_graphid = hashCode( ci_Id );
			//System.out.println("Start date  add portfolio  ============ in if  "  );
			String id = "http://scholarsportal.info/things/portfolio"+ci_Id;

			//System.out.println("Start date  add portfolio  ============ 1  "  );


			portfolio_iri.add(id);
			//System.out.println("Start date  add portfolio  ============ 2  "  );
			Resource portfolioResource = ResourceFactory.createResource(id);

			//sp:id
			semantics_utilities.addIdStatement(ci_Id, portfolioResource, portfolioModel);

			//spgraphId
			//semantics_utilities.addgraphIdStatement(portfolio_graphid, portfolioResource, portfolioModel);

			//rdf:type 
			semantics_utilities.addtypeStatement(portfolioCoreObjectResource , portfolioResource, portfolioModel);
			//System.out.println("Start date  add portfolio  ============ 3  "  );
			String journalStringResource = getJournalResource(i_id);

			//System.out.println("Start date  add portfolio  ============ journal resource before if  "  );
			if(journalStringResource.length() == 0 )
			{
				//String issn_graphid = hashCode( i_id);

				id = "http://scholarsportal.info/things/journal/"+i_id;

				Resource journalResource = ResourceFactory.createResource(id);

				//sp:id
				semantics_utilities.addIdStatement(i_id, journalResource, journalModel);

				//spgraphId
				//semantics_utilities.addgraphIdStatement(issn_graphid, journalResource, journalModel);

				//rdf:type 
				semantics_utilities.addtypeStatement(journalCoreObjectResource , journalResource, journalModel);

				//prism:issn
				Literal createStringLiteral = ResourceFactory.createStringLiteral(i_id) ;
				Statement statement = semantics_utilities.createStatement(journalResource, issn_property, createStringLiteral); 
				journalModel.add( statement ); 

				dataset.getNamedModel(journalGraphModelName).add(journalModel);

				Property sp_hasjournal_property = ResourceFactory.createProperty(sp + "hasJournal");		 
				statement = semantics_utilities.createStatement(portfolioResource, sp_hasjournal_property, journalResource );
				portfolioModel.add( statement );
			}
			else
			{			 	
				Property sp_hasjournal_property = ResourceFactory.createProperty(sp + "hasJournal");				
				Resource journalResource = ResourceFactory.createResource(journalStringResource);
				Statement statement = semantics_utilities.createStatement(portfolioResource, sp_hasjournal_property, journalResource );
				portfolioModel.add( statement );
			}

			if(issn_s_date.length() > 0)
			{

			}
			else
			{
				issn_s_date = "1200-01-01";
			}

			if(issn_e_date.length() > 0)
			{

			}
			else
			{
				issn_e_date = "2100-01-01";
			}

			//System.out.println("Start date  add portfolio  1 " );

			ArrayList<String> coverageList = addCoverage( issn_s_date , issn_e_date , ci_Id);

			//System.out.println("Start date  add portfolio  2 " );

			//System.out.println("SStart date  add portfolio  2  7 " );

			for (int s = 0 ; s < coverageList.size() ; s ++)
			{			
				//System.out.println("Start date  add portfolio  2  8 " );
				Resource coverageResource = ResourceFactory.createResource(coverageList.get(s));
				//System.out.println("Start date  9 " );
				Statement statement = semantics_utilities.createStatement(portfolioResource, p_temporal, coverageResource );
				//System.out.println("Start date  10 " );
				portfolioModel.add( statement );
				//System.out.println("Start date  11 " );
			}

			//System.out.println("Start date  12 " );
			dataset.getNamedModel(portfolioGraphModelName).add(portfolioModel);
			//System.out.println("Start date  13 " );

			Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event"+ci_Id+"/id:1" );
			Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
			Statement statement = semantics_utilities.createStatement(portfolioResource, sp_hasevent_property, eventResource );
			portfolioModel.add( statement );

			addEvent(eventResource , "Portfolio");

		}
		else
		{
			portfolio_iri.add(collectionissnStringResource);
		}

		return portfolio_iri;

	}



	public static void main(String... args) throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.ERROR);
		CreateCollectionTriples createcollectiontriples = new CreateCollectionTriples();
		//createcollectiontriples.run(); 
	}


	public void readentitlementexcel( ) throws Exception {


		Get_from_Excel t = new Get_from_Excel();
		System.out.println("excel sheet astart");
		Map<String, List<Integer>> issnYearMap = t.readFromExcelPAtest("data/SpringerGuelphPAtest.xls", (short)1, (short)3); 
		System.out.println("excel sheet ");

		Iterator<String> x = issnYearMap.keySet().iterator();
		while (x.hasNext()) {
			String issn = x.next();
			//String value = (String) videoIDMap.get(key).get(0);
			System.out.println();



			int minValue = Integer.MAX_VALUE;
			int maxValue = Integer.MIN_VALUE;

			List<Integer> list = issnYearMap.get(issn);
			for (int value : list) {
				if (value < minValue) {
					minValue = value; 
				}
				if (value > maxValue) {
					maxValue = value; 
				}
			}

			System.out.println(  issn.trim()  +" : " + minValue  +" : " + maxValue  );

		}



		System.exit(0);

		System.out.print("DONE");
	}


}


/*
ISSN at 0 -- 1673
ISSN at 1 -- 1491
ISSN at 2 -- 1362
ISSN at 3 -- 2002
ISSN at 4 -- 1299
ISSN at 5 -- 1493
ISSN at 6 -- 1287
ISSN at 7 -- 1345
ISSN at 8 -- 1292
ISSN at 9 -- 1691
ISSN at 10 -- 1776
ISSN at 11 -- 1355
ISSN at 12 -- 1465
ISSN at 13 -- 1276
ISSN at 14 -- 1739
ISSN at 15 -- 1603
ISSN at 16 -- 1270
ISSN at 17 -- 1434
ISSN at 18 -- 1469
ISSN at 19 -- 1297
ISSN at 20 -- 1689
ISSN at 21 -- 1478
ISSN at 22 -- 1821
ISSN at 23 -- 2101
ISSN at 24 -- 1712
ISSN at 25 -- 1362
ISSN at 26 -- 1864
ISSN at 27 -- 1716
ISSN at 28 -- 1417
ISSN at 29 -- 1850
ISSN at 30 -- 1696
ISSN at 31 -- 1687
ISSN at 32 -- 1624
ISSN at 33 -- 1812
ISSN at 34 -- 1334
ISSN at 35 -- 1403
ISSN at 36 -- 1691
ISSN at 37 -- 1358
ISSN at 38 -- 1334
ISSN at 39 -- 2176
ISSN at 40 -- 1660
ISSN at 41 -- 1472
ISSN at 42 -- 2315
ISSN at 43 -- 1381
ISSN at 44 -- 1531
ISSN at 45 -- 1753
ISSN at 46 -- 1889
ISSN at 47 -- 2046
ISSN at 48 -- 
 */
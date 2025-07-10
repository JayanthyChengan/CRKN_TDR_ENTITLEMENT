/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.jena.spRDF ;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.marklogic.client.DatabaseClient;
import com.marklogic.ejournals.client.DocumentRead;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory;
import com.marklogic.jena.spRDF.*;
/** Tutorial 1 creating a simple model
 */

public class CreateClientTriples extends Object {
	// some definitions 

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

	String organizationGraphModelName = "http://scholarsportal.info/graphs/Organization";
	String networkGraphModelName = "http://scholarsportal.info/graphs/Network";
	String eventGraphModelName = "http://scholarsportal.info/graphs/Event";

	Resource organizationCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Organization");
	Resource networkCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Network");
	Resource eventCoreObjectResource = ResourceFactory.createResource("http://scholarsportal.info/ontologies/core/Event");

	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath(); 
	XPathFactory factory = XPathFactory.newInstance();
	MyNamespaceContext myC = new MyNamespaceContext();

	Model organizationModel = ModelFactory.createDefaultModel(); 
	Model networkModel = ModelFactory.createDefaultModel();
	Model eventModel = ModelFactory.createDefaultModel();

	Dataset dataset = null;

	public CreateClientTriples() {
		
		client = Utils.load_mlenodetest1_Entitlements_Props_orig(); 
		dsg = MarkLogicDatasetGraphFactory.createDatasetGraph(client); 
		semantics_utilities = new Semantics_Utilities(); 
	}



	public void run() throws Exception {

	 
		
		dataset = dsg.toDataset();

		 

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath(); 


		RDFDataMgr.read(organizationModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(networkModel,  new StringReader(turtle), "", Lang.TURTLE);
		RDFDataMgr.read(eventModel,  new StringReader(turtle), "", Lang.TURTLE);

		addNamedModel(organizationGraphModelName , organizationModel);
		addNamedModel(networkGraphModelName , networkModel);
		addNamedModel(eventGraphModelName , eventModel);

		Document source = Utils.getDocument(client ,  "/LOGS/client.xml");
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath(); 

		Property sp_SubscribeTo_property = ResourceFactory.createProperty(sp + "subscribeTo");
		
		NodeList l_list = (NodeList) xpath.evaluate("//library", source, XPathConstants.NODESET);
		if (l_list != null){

			for(int i =0; i < l_list.getLength(); i++)//l_list.getLength()
			{
				String g_name = xpath.evaluate("@name", l_list.item(i));   
				
				System.out.println(" Client name : " + g_name);
				
				
				
				/*
				//String l_id = "l_"+i;
				String g_name = "brock" ; // 
				//
				System.out.println(g_name);

				if(g_name.equals("brock"))
				{
					String organizationStringResource = getOrganizationResource(g_name);
					
					String collectionStringResource = getCollectionResource("/collection/perpetual/brock/elsevier/2017");

					if(collectionStringResource.length() == 0 )
					{
						System.out.println("Collection resource missing " );
					} 
					else
					{							
						Resource organizationResource = ResourceFactory.createResource(organizationStringResource);
						Resource collectionResource = ResourceFactory.createResource(collectionStringResource);
						Statement statement = createStatement(organizationResource,sp_SubscribeTo_property,collectionResource);
						organizationModel.add( statement );
					}
				}
				else if(g_name.equals("skip"))
				*/
				{
					//String client_graphid = Utils.hashCode(g_name);

					Resource organizationResource = ResourceFactory.createResource("http://scholarsportal.info/things/organization/"+g_name);

					//sp:id				
					semantics_utilities.addIdStatement(g_name, organizationResource, organizationModel);

					//spgraphId
					//semantics_utilities.addgraphIdStatement(client_graphid, organizationResource, organizationModel);

					//rdf:type
					semantics_utilities.addtypeStatement(organizationCoreObjectResource , organizationResource, organizationModel);


					//sp:id
					Property property = ResourceFactory.createProperty(geo + "name");
					Literal createStringLiteral = ResourceFactory.createStringLiteral(g_name) ;
					Statement statement = createStatement(organizationResource,property,createStringLiteral);
					organizationModel.add( statement );

					


					NodeList i_list = (NodeList) xpath.evaluate("entitlements", l_list.item(i), XPathConstants.NODESET);
					if (i_list != null){

						for(int j =0; j < i_list.getLength(); j++)//i_list.getLength()
						{
							String e_name = xpath.evaluate("@name", i_list.item(j));   

							String collectionStringResource = getCollectionResource(e_name);

							if(collectionStringResource.length() == 0 )
							{
								System.out.println("Collection resource missing : " + e_name);
							} 
							else
							{							
								System.out.println("Collection resource add : " + e_name);
								Resource collectionResource = ResourceFactory.createResource(collectionStringResource);
								statement = createStatement(organizationResource,sp_SubscribeTo_property,collectionResource);
								organizationModel.add( statement );
							}
						}
					} // end of <entitlements>

					Property sp_hasnetwork_property = ResourceFactory.createProperty(sp + "hasNetwork");
					ArrayList<String> networkList = addNetwork( l_list.item(i) , g_name);

					for (int s = 0 ; s < networkList.size() ; s ++)
					{			
						Resource networkResource = ResourceFactory.createResource(networkList.get(s));
						statement = semantics_utilities.createStatement(organizationResource, sp_hasnetwork_property, networkResource );
						organizationModel.add( statement );
					}
					
					

					Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event/"+g_name +"/id:1" );
					Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
					statement = createStatement(organizationResource, sp_hasevent_property, eventResource );
					organizationModel.add( statement );

					addEvent(eventResource , "Organization");
				}
				
				if (dataset.getNamedModel(organizationGraphModelName) == null)
				{
					dataset.addNamedModel(organizationGraphModelName, organizationModel);
				}

				Model model = dataset.getNamedModel(organizationGraphModelName);
				model.add(organizationModel); 
			}
		}

	
		System.out.println("Done");
	}

	private ArrayList<String> addNetwork(Node l_list, String g_name) throws XPathExpressionException {
		
		ArrayList<String> network_iri = new ArrayList<String>();
		Model networkTriples = ModelFactory.createDefaultModel(); 


		Property sp_baseopenurl_property = ResourceFactory.createProperty(sp + "baseOpenURL");
		Property sp_iprange_property = ResourceFactory.createProperty(sp + "ipRange");
		
		NodeList i_list = (NodeList) xpath.evaluate("ipaddress/value", l_list, XPathConstants.NODESET);
		if (i_list != null){
			
			String s = "http://scholarsportal.info/things/network/"+g_name;

			network_iri.add(s);

			
			Resource networkResource = ResourceFactory.createResource(s);

			//sp:id				
			semantics_utilities.addIdStatement(g_name, networkResource, networkModel);

			//rdf:type
			semantics_utilities.addtypeStatement(networkCoreObjectResource , networkResource, networkModel);

			
			for(int j =0; j < i_list.getLength(); j++)//i_list.getLength()
			{
				String e_name = i_list.item(j).getTextContent();   
				Literal createStringLiteral = ResourceFactory.createStringLiteral(e_name) ;
				Statement statement = createStatement(networkResource,sp_iprange_property,createStringLiteral);
				networkTriples.add( statement );
				
				
			}
			
			
			i_list = (NodeList) xpath.evaluate("open-url", l_list, XPathConstants.NODESET);
			if (i_list != null){
				
				
				for(int j =0; j < i_list.getLength(); j++)//i_list.getLength()
				{
					String e_name = i_list.item(j).getTextContent();   
					Literal createStringLiteral = ResourceFactory.createStringLiteral(e_name) ;
					Statement statement = createStatement(networkResource,sp_baseopenurl_property,createStringLiteral);
					networkTriples.add( statement );
					
					
				}
				
				
			} // end of <open-url>
			

			
			networkModel.add(networkTriples);
			dataset.getNamedModel(networkGraphModelName).add(networkModel);
			
		} // end of <ipaddress>
		
		
		
		

		return network_iri;
	}



	private void addNamedModel(String graphModelName, Model model) {

		if (dataset.getNamedModel(graphModelName) == null)
		{
			dataset.addNamedModel(graphModelName, model);
		}

	}



	private void addEvent(Resource eventResource , String type) {
		//rdf:type
		semantics_utilities.addtypeStatement(eventCoreObjectResource , eventResource, eventModel);

		//sp:eventType
		Literal createStringLiteral = ResourceFactory.createStringLiteral("Add "+type) ;
		Property sp_haseventType_property = ResourceFactory.createProperty(sp + "eventType");
		Statement statement = semantics_utilities.createStatement(eventResource, sp_haseventType_property, createStringLiteral); 
		eventModel.add( statement );

		//sp:eventDetail
		createStringLiteral = ResourceFactory.createStringLiteral("New "+type+" Added") ;
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

	private String getCollectionResource(String collectionname)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://scholarsportal.info/ontologies/core/Collection> . "+
						" ?a sp:id '" + collectionname.replace("collection", "SPcollection") + "' }", dsg.toDataset()); 

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

	private String getOrganizationResource(String name)
	{
		QueryExecution execution = QueryExecutionFactory.create(
				"PREFIX sp: <http://scholarsportal.info/ontologies/core/> "+
						"   select ?a   where {   " + 
						" ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> sp:Organization . "+
						" ?a sp:id '" +  name + "' }", dsg.toDataset()); 

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

	public static void main(String... args) throws Exception {
		CreateClientTriples example = new CreateClientTriples();
		example.run(); 

	}
}

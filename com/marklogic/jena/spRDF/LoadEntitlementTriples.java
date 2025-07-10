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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import ch.qos.logback.classic.Level;

public class LoadEntitlementTriples {

	public LoadEntitlementTriples() throws Exception {
		
		System.out.println("test 111");
		CreateCollectionTriples ct = new CreateCollectionTriples();
		  
		 
		//CreateClientTriples cct = new CreateClientTriples();
		//cct.run(); 		 
	}	 

	public static void main(String... args) throws Exception {
		
		System.out.println("Main 1 " );
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.ERROR);
	    
	    System.out.println("Main 2 " );
	    
		LoadEntitlementTriples load = new LoadEntitlementTriples();
		
	}

}

/*

public void prepetualrun( ) throws Exception {
	// Make some triples
	//dsg.clear();
	dataset = dsg.toDataset();

	//System.exit(0);;



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

	String c_name = "/SPcollection/perpetual/brock/elsevier/2017";
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
		createStringLiteral = ResourceFactory.createStringLiteral("elsevier") ;
		statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
		collectionModel.add( statement );

		//sp:collectionType
		property = ResourceFactory.createProperty(sp + "collectionType"); 
		createStringLiteral = ResourceFactory.createStringLiteral("perpetual");
		statement = semantics_utilities.createStatement(collectionResource, property,	createStringLiteral);				
		collectionModel.add( statement );


		Property p_temporal = ResourceFactory.createProperty(dcterms + "temporal");
		Property haspart_property = ResourceFactory.createProperty(dcterms + "hasPart"); 

		//collection start date and end date
		String s_date = ""; 
		String e_date = ""; 

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



		BufferedReader reader = new BufferedReader(new FileReader("/home/chenganj/Documents/13. Linked Data/sp-entitlement/sp-entitlement-semantics/src/main/java/data/prepetual.txt")); 
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

		Resource eventResource = ResourceFactory.createResource("http://scholarsportal.info/things/event"+c_name+"/id:1" );
		Property sp_hasevent_property = ResourceFactory.createProperty(sp + "hasEvent");
		statement = semantics_utilities.createStatement(collectionResource, sp_hasevent_property, eventResource );
		collectionModel.add( statement );

		addEvent(eventResource , "Collection");

		System.out.println("Combine models and save");
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

*/
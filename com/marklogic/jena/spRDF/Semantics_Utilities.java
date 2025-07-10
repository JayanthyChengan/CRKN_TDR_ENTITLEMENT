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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.*;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory; 

public class Semantics_Utilities {


	String ns = "http://scholarsportal.info/";
	String dc = "http://purl.org/dc/elements/1.1/";
	String dcterms ="http://purl.org/dc/terms/";
	String dcmitype = "http://purl.org/dc/dcmitype/";
	String fabio	=	"http://purl.org/spar/fabio/";
	String prism	=	"http://prismstandard.org/namespaces/basic/2.0/" ; 
	String sp 		=	"http://scholarsportal.info/ontologies/core/";
	String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String bibo = "http://purl.org/ontology/bibo/";


   
    
    public void addtypeStatement(String type, Resource resource, Model collectionTriples) {
		// TODO Auto-generated method stub
		Property property = ResourceFactory.createProperty(rdf + "type");
		Resource objectResource = ResourceFactory.createResource(type);
		Statement statement = createStatement(resource,property,objectResource);
		collectionTriples.add( statement );
	}

    public void addtypeStatement(Resource objectResource, Resource resource, Model collectionTriples) {
		// TODO Auto-generated method stub
		Property property = ResourceFactory.createProperty(rdf + "type"); 
		Statement statement = createStatement(resource,property,objectResource);
		collectionTriples.add( statement );
	}


	public void addIdStatement(String c_name, Resource resource, Model collectionTriples) {
		// TODO Auto-generated method stub
		Property property = ResourceFactory.createProperty(sp + "id");
		Literal createStringLiteral = ResourceFactory.createStringLiteral(c_name) ;
		Statement statement = createStatement(resource,property,createStringLiteral);
		collectionTriples.add( statement );
	}

	public void addgraphIdStatement(String collection_graphid, Resource resource, Model collectionTriples) {
		// TODO Auto-generated method stub
		Property property = ResourceFactory.createProperty(sp + "spgraphId"); 
		Literal createStringLiteral = ResourceFactory.createStringLiteral(collection_graphid) ;
		Statement statement = createStatement(resource,property,createStringLiteral);
		collectionTriples.add( statement );
	}
	
	public Statement createStatement(Resource subjectResource, Property rdfTypeProperty, Literal createStringLiteral) {
		// TODO Auto-generated method stub
		Statement statement = ResourceFactory.createStatement(subjectResource,rdfTypeProperty,createStringLiteral );
		return statement;
	}

	public Statement createStatement(Resource subjectResource, Property rdfTypeProperty, Resource objectResource) {
		// TODO Auto-generated method stub
		Statement statement = ResourceFactory.createStatement(subjectResource,rdfTypeProperty,objectResource );
		return statement;
	}
     
}

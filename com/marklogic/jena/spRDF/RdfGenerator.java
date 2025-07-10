package com.marklogic.jena.spRDF;

 

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;


public class RdfGenerator {

    private static final String BASE = "http://scholarsportal.info/";
    private static final String CORE = BASE + "ontologies/core/";
    private static final String THINGS = BASE + "things/";

    public static void main(String[] args) {
        String csvFile = "data/input/data.csv";
        String line;
        String csvSplit = ",";

        // Static metadata
        
        /*
         * 
         * To find the maximum collection ID from your RDF data (where collection IRIs look like http://scholarsportal.info/things/collection/collection:ID), 
         * you can extract the numeric part of the ID using SPARQL string functions and then compute the maximum.
        
PREFIX sp: <http://scholarsportal.info/ontologies/core/>
PREFIX fn: <http://www.w3.org/2005/xpath-functions#>

SELECT (MAX(xsd:integer(REPLACE(STR(?collection), "^.*collection:([0-9]+)$", "$1"))) AS ?maxCollectionID)
WHERE {
  ?collection a sp:Collection .
}


         */
        String collectionId = "543";
        String collectionPath = "/collection/emerald/emerald-2003";
        String publisher = "Emerald";
        String eventDate = LocalDate.now().toString(); 

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("core", CORE);

        Property coreId = model.createProperty(CORE + "id");
        Property coreTitle = model.createProperty(CORE + "title");
        Property corePublisher = model.createProperty(CORE + "publisher");
        Property coreHasPart = model.createProperty(CORE + "hasPart");
        Property coreHasEvent = model.createProperty(CORE + "hasEvent");
        Property coreHasJournal = model.createProperty(CORE + "hasJournal");
        Property coreTemporal = model.createProperty(CORE + "temporal");
        Property coreDateStart = model.createProperty(CORE + "dateStart");
        Property coreDateEnd = model.createProperty(CORE + "dateEnd");
        Property coreEventType = model.createProperty(CORE + "eventType");
        Property coreEventDetail = model.createProperty(CORE + "eventDetail");
        Property coreDate = model.createProperty(CORE + "date");
        Property coreIssn = model.createProperty(CORE + "issn");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // skip header

            // Create Collection
            Resource collection = model.createResource(THINGS + "collection/collection:" + collectionId)
                    .addProperty(coreId, collectionPath)
                    .addProperty(coreTitle, collectionPath)
                    .addProperty(corePublisher, publisher);

            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(csvSplit, -1);
                if (fields.length < 4) continue;

                String title = fields[0].trim();
                String issn = fields[1].trim();
                String start = fields[2].trim();
                String end = fields[3].trim();
                
                String id = issn;
                if (!start.isEmpty()) id += "_s" + start.replace("-", "");
                if (!end.isEmpty()) id += "_e" + end.replace("-", "");
                id += "_c";
                
                
                 
                String coverageURI = THINGS + "coverage/" + id;
                String portfolioURI = THINGS + "portfolio/" + id;
                String journalURI = THINGS + "journal/" + issn;
                String eventURI = THINGS + "event/" + id + "/id:" + collectionId;

                // Portfolio
                Resource portfolio = model.createResource(portfolioURI)
                        .addProperty(RDF.type, model.createResource(CORE + "Portfolio"))
                        .addProperty(coreId, id)
                        .addProperty(coreTemporal, model.createResource(coverageURI))
                        .addProperty(coreHasEvent, model.createResource(eventURI))
                        .addProperty(coreHasJournal, model.createResource(journalURI));

                collection.addProperty(coreHasPart, portfolio);

                // Coverage
                Resource coverage = model.createResource(coverageURI)
                        .addProperty(RDF.type, model.createResource(CORE + "Coverage"));

                if (!start.isEmpty()) {
                    coverage.addProperty(coreDateStart, model.createTypedLiteral(start, "xs:date"));
                }
                if (!end.isEmpty()) {
                    coverage.addProperty(coreDateEnd, model.createTypedLiteral(end, "xs:date"));
                }

                // Event
                model.createResource(eventURI)
                        .addProperty(RDF.type, model.createResource(CORE + "Event"))
                        .addProperty(coreEventType, "Add")
                        .addProperty(coreEventDetail, "Portfolio added to the collection : " + collectionPath)
                        .addProperty(coreId, model.createTypedLiteral(collectionId, "xs:integer"))
                        .addProperty(coreDate, model.createTypedLiteral(eventDate, "xs:date"));

                // Journal
                model.createResource(journalURI)
                        .addProperty(RDF.type, model.createResource(CORE + "Journal"))
                        .addProperty(coreId, issn)
                        .addProperty(coreIssn, issn)
                        .addProperty(coreTitle, title);

                count++;
            }

            // Output RDF (Turtle)
            model.write(System.out, "TURTLE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.marklogic.semantics.xsl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.marklogic.jena.spRDF.Get_from_Excel;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;

public class collectionxslTransform {

	public static URI uri;
	public static ContentSource cs;
	public static Session session;
	public static Request request;
	public static ResultSequence rs;
	public static String c_name = "";
	public static String l_name = "";

	public static void main(String[] args) throws Exception {

		 
		for (int collectionCount = 1; collectionCount <= 350; collectionCount++) {
			String createinputXML = "data/stylesheet/input/temp_collectionNode.xml";
			getCollectionNode(createinputXML , collectionCount);

			c_name = getCollectionName(collectionCount);
			l_name = getLicenseName(collectionCount);

			
			
			

			if (c_name.length() > 5) {
				
				String stylesheet = "data/stylesheet/input/collectionrdfTransformer.xsl";  
				String destfile = "data/stylesheet/output/collection/temp" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				XSLtransform(c_name,createinputXML,stylesheet,destfile);
				
				stylesheet = "data/stylesheet/input/coveragerdfTransformer.xsl";  
				destfile = "data/stylesheet/output/coverage/temp" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				XSLtransform(c_name,createinputXML,stylesheet,destfile);
				
				
				stylesheet = "data/stylesheet/input/eventrdfTransformer.xsl";  
				destfile = "data/stylesheet/output/event/temp" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				XSLtransform(c_name,createinputXML,stylesheet,destfile);
				
				
				stylesheet = "data/stylesheet/input/licenserdfTransformer.xsl";  
				destfile = "data/stylesheet/output/license/temp" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				XSLtransform(c_name,createinputXML,stylesheet,destfile);
				
				
				stylesheet = "data/stylesheet/input/portfoliordfTransformer.xsl";  
				destfile = "data/stylesheet/output/portfolio/temp" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				XSLtransform(c_name,createinputXML,stylesheet,destfile);
				
				System.out.print("RDF Generation " + c_name);
			}

			System.out.println("   - completed   ");

		}
		
		String stylesheet = "data/stylesheet/input/journalrdfTransformer.xsl"; // change here
		String destfile = "data/stylesheet/output/journal/temp_issn_to_rdf.rdf"; // change
		 
		getIssnNode(  );
		XSLISSNtransform(c_name,   stylesheet ,   destfile);
		
	}

	static TransformerFactory tFactory;

	public static void getCollectionNode(String inputXML , int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return <collection-groups>{$doc//collection["
				+ collectionCount + "]}</collection-groups> ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(inputXML));
		writer.write(str);

		writer.close();

	}

	//

	public static void getIssnNode( )
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')    let $issn := fn:distinct-values ($doc//issn ) return <collection-groups><collection> {for $i in $issn return <issn>{$i}</issn>  }</collection></collection-groups>  ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/stylesheet/input/temp_issnNode.xml"));
		writer.write(str);

		writer.close();

	}

	public static String getCollectionName(int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return  $doc//collection[" + collectionCount
				+ "]/@name  ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();

		return str;

	}

	public static String getLicenseName(int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return  $doc//collection[" + collectionCount
				+ "]/@license_tag  ";

		 
		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();

		return str;

	}

	public static void XSLtransform(String c_name , String createinputXML , String stylesheet , String destfile ) throws Exception {

		//String stylesheet = "data/stylesheet/input/portfoliordfTransformer.xsl"; // change here
		//String destfile = "data/stylesheet/output/portfolio/temp" + c_name.replace("/", "_") + "_to_rdf.rdf"; // change
																												// here
		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,
				"data" + File.separator + "collection_licensetype.xls", 0, 1, 0);
		HashMap<String, String> map_licensetypes = t.get_values(0,
				"data" + File.separator + "collection_licensetype.xls", 0, 2, 0);

		Source xslfile = new StreamSource(stylesheet);

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xslfile);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();

		// output file after transforming
		FileOutputStream fileOutputStream = new FileOutputStream(destfile);
		StreamResult streamResult = new StreamResult(fileOutputStream);

		 

		InputSource inputSource = new InputSource(createinputXML);

		String collectionType = (map_collectiontypes.get(c_name) + "");
		String licenseType = map_licensetypes.get(c_name);

		transformer.setParameter("collectionType", collectionType);
		transformer.setParameter("publisher", c_name.split("/")[2]);
		transformer.setParameter("licenseName", l_name);
		transformer.setParameter("licenseType", licenseType);
		transformer.transform(new SAXSource(xmlReader, inputSource), streamResult);

		inputSource = null;
		fileOutputStream.close();
		fileOutputStream.flush();

	}
	
	public static void XSLISSNtransform( String c_name , String stylesheet , String destfile) throws Exception {

		
																												// here
		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,
				"data" + File.separator + "collection_licensetype.xls", 0, 1, 0);
		HashMap<String, String> map_licensetypes = t.get_values(0,
				"data" + File.separator + "collection_licensetype.xls", 0, 2, 0);

		Source xslfile = new StreamSource(stylesheet);

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xslfile);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();

		// output file after transforming
		FileOutputStream fileOutputStream = new FileOutputStream(destfile);
		StreamResult streamResult = new StreamResult(fileOutputStream);

		String tempfile = "data/stylesheet/input/temp_issnNode.xml";

		InputSource inputSource = new InputSource(tempfile);

		String collectionType = (map_collectiontypes.get(c_name) + "");
		String licenseType = map_licensetypes.get(c_name);

		transformer.setParameter("collectionType", collectionType);
		transformer.setParameter("publisher", c_name );
		transformer.setParameter("licenseName", l_name);
		transformer.setParameter("licenseType", licenseType);
		transformer.transform(new SAXSource(xmlReader, inputSource), streamResult);

		inputSource = null;
		fileOutputStream.close();
		fileOutputStream.flush();

	}
	
	
	
}

/*
 * <for-each select="issn"> <variable name="issnString" select="."/>
 * <terms:hasPart rdf:resource=
 * "http://scholarsportal.info/things/portfolio{$local_collection_name}/{$issnString}"
 * /> <terms:subject rdf:parseType="Resource"> <rdf:value> <value-of
 * select="."/> </rdf:value> </terms:subject> </for-each>
 */

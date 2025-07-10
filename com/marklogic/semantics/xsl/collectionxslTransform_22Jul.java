package com.marklogic.semantics.xsl;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.marklogic.jena.spRDF.Get_from_Excel;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;
public class collectionxslTransform_22Jul {

	public static URI uri;
	public static ContentSource cs;
	public static Session session;
	public static Request request;
	public static ResultSequence rs;
	public static String c_name = "";
	public static String l_name = "";

	public static void main(String[] args) throws Exception {


		/*
		
		
		int issncount = getDistinctIssnCount();
		int start = 1;
		int end = issncount/ 1000 + 1;

		for (int issnid = 1; issnid <= end ; issnid++) {  
			String createinputXML = "data/temp/temp_issndetails_"+ issnid+ ".xml";
			String destfile = "data/output/journal/temp_issndetails_" + issnid +  "_to_rdf.rdf"; 
			System.out.println(  " RDF Generation for journal from  " + createinputXML +" "+ start +" "+ issnid*1000);
			getIssnDetails(createinputXML , start , issnid*1000);
			
			createJournalTripes(createinputXML , destfile); 
			start = start + 1000 ; 
		}

		 


		for (int collectionId = 1; collectionId <= 400; collectionId++) { //300
			String createinputXML = "data/temp/temp_collectionNode.xml";


			c_name = getCollectionName(collectionId);
			l_name = getLicenseName(collectionId);
			if (c_name.length() > 5) {
				getCollectionNode(createinputXML , collectionId);
				System.out.print(collectionId + " RDF Generation for collection  " + c_name);
				createTriples(c_name , collectionId, createinputXML);
			}
		}


		for (int collectionId = 1; collectionId <= 400; collectionId++) { //300
			String createinputXML = "data/temp/temp_subcollectionNode.xml";


			c_name = getSubCollectionName(collectionId);
			l_name = getSubLicenseName(collectionId);
			if (c_name.length() > 5) {
				getSubCollectionNode(createinputXML , collectionId);
				System.out.print(collectionId + " RDF Generation for subcollection  " + c_name);
				createTriples(c_name , 450 + collectionId, createinputXML);
			}
		}

		*/

		String PARightsClient_stylesheet = "data/stylesheet/PARightsClient_rdfTransformer.xsl";  
		String PARightsPortfolio_stylesheet = "data/stylesheet/PARightsPortfolio_rdfTransformer.xsl";
		XSLISSNtransform_PARights( "acs" ,   PARightsClient_stylesheet, PARightsPortfolio_stylesheet );

	}

	public static String getSubCollectionName(int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return  $doc//collection[" + collectionCount
				+ "]/sub-collection/@name  ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();

		return str;

	}

	public static String getSubLicenseName(int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return  $doc//collection[" + collectionCount
				+ "]/sub-collection/@license_tag  ";


		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();

		return str;

	}

	public static void createTriples(String c_name, int collectionId , String createinputXML) throws Exception
	{
		
		createDirectory("data/output/collection/");
		createDirectory("data/output/coverage/");
		createDirectory("data/output/event/");
		createDirectory("data/output/license/");
		createDirectory("data/output/portfolio/");
		
		String stylesheet = "data/stylesheet/collectionrdfTransformer.xsl";  
		String destfile = "data/output/collection/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
		XSLtransform_collection(collectionId, c_name,createinputXML,stylesheet,destfile); 

		stylesheet = "data/stylesheet/coveragerdfTransformer.xsl";  
		destfile = "data/output/coverage/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
		XSLtransform_collection(collectionId, c_name,createinputXML,stylesheet,destfile);

		stylesheet = "data/stylesheet/eventrdfTransformer.xsl";  
		destfile = "data/output/event/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
		XSLtransform_collection(collectionId, c_name,createinputXML,stylesheet,destfile);

		stylesheet = "data/stylesheet/licenserdfTransformer.xsl";  
		destfile = "data/output/license/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
		XSLtransform(c_name,createinputXML,stylesheet,destfile);

		stylesheet = "data/stylesheet/portfoliordfTransformer.xsl";  
		destfile = "data/output/portfolio/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
		XSLtransform_collection(collectionId, c_name,createinputXML,stylesheet,destfile);



		System.out.println("   - completed   ");
	}

	static TransformerFactory tFactory;

	public static void getCollectionNode(String inputXML , int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')   return <collection-groups>{$doc//collection["
				+ collectionCount + "]}</collection-groups> ";

		String filePath = "data/xqueryscripts/collectiontransform.txt";
		String content = ""; 
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("collectionCount", collectionCount+"");
			//System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}



		request = session.newAdhocQuery(content);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(inputXML));
		writer.write(str);

		writer.close();

	}

	public static void getSubCollectionNode(String inputXML , int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();



		String filePath = "data/xqueryscripts/subcollectiontransform.txt";
		String content = ""; 
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("collectionCount", collectionCount+"");
			//System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}



		request = session.newAdhocQuery(content);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(inputXML));
		writer.write(str);

		writer.close();

	}

	public static void getIssnNode(String inputXML , int collectionCount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();



		String filePath = "data/xqueryscripts/issnbycollectiontransform.txt";
		String content = ""; 
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("collectionCount", collectionCount+"");
			//System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}



		request = session.newAdhocQuery(content);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(inputXML));
		writer.write(str);

		writer.close();

	}

	public static void getIssnNode( )
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')    let $issn := fn:distinct-values ($doc//issn ) return <collection-groups><collection> {for $i in $issn return <issn>{$i}</issn>  }</collection></collection-groups>  ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/temp/temp_issnNode.xml"));
		writer.write(str);

		writer.close();

	}

	public static void getIssnDetails( String inputXML , int startcount , int endcount)
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String filePath = "data/xqueryscripts/issnbycount.txt";
		String content = ""; 
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("start", startcount+"");
			content = content.replace("end", endcount+"");
			//System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}


		request = session.newAdhocQuery(content);
		rs = session.submitRequest(request);

		String str = rs.asString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(inputXML));
		writer.write(str);
		writer.close();

	}

	public static Integer getDistinctIssnCount( )
			throws URISyntaxException, XccConfigException, RequestException, IOException {

		URI uri = new URI("xcc://loader:Loader123@mle1.scholarsportal.info:9006/");
		cs = ContentSourceFactory.newContentSource(uri);
		session = cs.newSession();

		String query = "let $doc := doc('/LOGS/collection-groups.xml')    return fn:count( fn:distinct-values ($doc//issn ) )  ";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		int count = Integer.parseInt( rs.asString());
		return count;

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


		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 1, 0);
		HashMap<String, String> map_licensetypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 2, 0);

		Source xslfile = new StreamSource(stylesheet);

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

	public static SAXSource convertXMLToSource(String xml){

		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();

			reader.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.endsWith(".dtd")) {
						StringReader stringInput = new StringReader(" ");
						return new InputSource(stringInput);
					}else {
						return null; // use default behavior
					}
				}
			});
			//try DOMSource
			return new SAXSource(reader, new InputSource(new StringReader(xml)));

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static String transformToXML(Source xmlSource, Transformer transformer) throws TransformerException{

		StringWriter xml_transfer = new StringWriter();

		transformer.transform(xmlSource, new StreamResult(xml_transfer));
		//System.out.println(xml_transfer.toString());	
		return xml_transfer.toString();		
	}

	public static void XSLtransform_collection(int collectionId, String c_name , String createinputXML , String stylesheet , String destfile ) throws Exception {


		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 1, 0);
		HashMap<String, String> map_licensetypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 2, 0);

		Source xslfile = new StreamSource(stylesheet);

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
		transformer.setParameter("collectionId", collectionId);
		transformer.setParameter("publisher", c_name.split("/")[2]);
		transformer.setParameter("licenseName", l_name);
		transformer.setParameter("licenseType", licenseType);
		transformer.transform(new SAXSource(xmlReader, inputSource), streamResult);

		inputSource = null;
		fileOutputStream.close();
		fileOutputStream.flush();

	}



	public static void createJournalTripes(   String createinputXML, String destfile) throws Exception {

		String stylesheet = "data/stylesheet/journalrdfTransformer.xsl"; 
		Source xslfile = new StreamSource(stylesheet);

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

		
		transformer.transform(new SAXSource(xmlReader, inputSource), streamResult);

		inputSource = null;
		fileOutputStream.close();
		fileOutputStream.flush();

	}

	public static void XSLISSNtransform_PARights(   String publisher , String  PARightsClient_stylesheet, String PARightsPortfolio_stylesheet   ) throws Exception {


		Source PARightsClient_xslfile = new StreamSource(PARightsClient_stylesheet);
		Source PARightsPortfolio_xslfile = new StreamSource(PARightsPortfolio_stylesheet);

		tFactory = TransformerFactory.newInstance();
		Transformer PARightsClient_transformer = tFactory.newTransformer(PARightsClient_xslfile);

		Transformer PARightsPortfolio_transformer = tFactory.newTransformer(PARightsPortfolio_xslfile);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();


		String directoryPath = "data/input/perpetual/" + publisher;

		// Get all files in the directory
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		createDirectory("data/output/perpetual/"+ publisher +"/");

		// Check if the directory is valid and contains files
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				// Ensure the file is an XML file
				if (file.isFile() && file.getName().endsWith(".xml")) {

					String destfile = "data/output/perpetual/"+ publisher +"/"+removeAccent(file.getName()).replace(".", "").replace("xml", ".rdf");
					System.out.println("\n\nProcessing file: " + file.getCanonicalPath());
					System.out.println("Destfile " + destfile);

					try
					{
						// output file after transforming
						FileOutputStream fileOutputStream = new FileOutputStream(destfile);
						StreamResult streamResult = new StreamResult(fileOutputStream);

						String tempfile = "data/input/perpetual/"+ publisher +"/"+file.getName() ; // "data/stylesheet/input/perpetual/Algoma.xml";

						InputSource inputSource = new InputSource(tempfile);


						//String fileName = "Café.xml";

						// Create a File object with the file name
						File file2 = new File(tempfile);

						// Convert the file path to URI and then to string to handle special characters
						String filePath = file2.toURI().toString();

						// Create an InputStream for the file
						InputStream inputStream = new FileInputStream(file2);

						// Create an InputSource from the InputStream
						InputSource inputSource2 = new InputSource(inputStream);

						// Optionally set the encoding if necessary
						inputSource2.setEncoding(StandardCharsets.UTF_8.name());

						if (file.isFile() && file.getName().endsWith("pacoverage.xml")) {
							PARightsPortfolio_transformer.transform(new SAXSource(xmlReader, inputSource2), streamResult);
						}
						else
						{
							PARightsClient_transformer.transform(new SAXSource(xmlReader, inputSource2), streamResult);
						}

						inputSource = null;
						fileOutputStream.close();
						fileOutputStream.flush();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

				}

			}
		}
	}

	public static String removeAccent(String input) {
		// Normalize the input string to decompose characters with diacritics into base characters and combining characters
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

		// Remove the accent characters using a regular expression that matches all non-ASCII characters
		String accentRemoved = normalized.replaceAll("\\p{M}", "");

		// Return the result
		return accentRemoved;
	}

	private static boolean createDirectory(String path){

		File latestCrossref = new File(path);
		if(!latestCrossref.exists()){
			if(!latestCrossref.mkdirs()){
				System.out.println("Can't use mkdirs to create sub directory. " + path);
				return false;
			}
		}
		return true;
	}



}

/*
 * 
 * 
 * 

 public static void XSLISSNtransformtemp( String c_name , String stylesheet , String destfile) throws Exception {

		Get_from_Excel t = new Get_from_Excel();
		HashMap<String, String> map_collectiontypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 1, 0);
		HashMap<String, String> map_licensetypes = t.get_values(0,
				"data/input/mappingdetails" + File.separator + "collection_licensetype.xls", 0, 2, 0);

		Source xslfile = new StreamSource(stylesheet);

		tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xslfile);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();

		// output file after transforming
		FileOutputStream fileOutputStream = new FileOutputStream(destfile);
		StreamResult streamResult = new StreamResult(fileOutputStream);

		String tempfile = "data/temp/temp_issnNode.xml";

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
	
	 
		
		instead of handling by collection, rewrite to handle distinct issns

		for (int collectionId = 1; collectionId <= 400; collectionId++) { 
			try {
				String createinputXML = "data/temp/temp_issnNode.xml";

				c_name = getCollectionName(collectionId);
				l_name = getLicenseName(collectionId);
				createDirectory("data/output/journal/");
				String destfile = "data/output/journal/" + c_name.replace("/", "_") + "_to_rdf.rdf";  
				if (c_name.length() > 5) {
					getIssnNode(createinputXML , collectionId);	
					System.out.println(collectionId + " RDF Generation for journal from  " + c_name);
					createJournalTripes(    createinputXML , destfile);

				}
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		 

 * 
 * 
 * <for-each select="issn"> <variable name="issnString" select="."/>
 * <terms:hasPart rdf:resource=
 * "http://scholarsportal.info/things/portfolio{$local_collection_name}/{$issnString}"
 * /> <terms:subject rdf:parseType="Resource"> <rdf:value> <value-of
 * select="."/> </rdf:value> </terms:subject> </for-each>
 */

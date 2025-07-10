package com.marklogic.semantics.xsl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class xslTransform {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		XSLtransform("data/stylesheet/input/rdfTransformer.xsl" , "data/stylesheet/input/183_mjdw1.xml", "data/stylesheet/output/temp_jats_to_rdf.rdf" ,  false);
	}
	static TransformerFactory  tFactory;
	
	 public static void XSLtransform(String stylesheet , String source, String destfile ,   Boolean subarticle_flag) throws Exception {
	    	 
	    	Source xslfile = new  StreamSource(stylesheet);
			 
			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			
			tFactory = TransformerFactory.newInstance();
			Transformer transformer =  tFactory.newTransformer(xslfile);
			
			SAXParserFactory spf = SAXParserFactory.newInstance();         
			spf.setFeature("http://apache.org/xml/features/validation/schema", false);   
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);       
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();         
		
			
			//output file after transforming
			FileOutputStream fileOutputStream = new FileOutputStream(destfile);
			StreamResult streamResult = new StreamResult(fileOutputStream);

			//input file to be transformed
			File file = new File(source).getAbsoluteFile(); 
				
			InputStream is = new FileInputStream(source);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			
			byte[] data = bos.toByteArray();
			String dataString = new String(data, "UTF-8");
			
		 
					 
			String tempfile = "data/stylesheet/input/temp_sourcefile.xml" ; 
			File out1 = new File(tempfile);
			FileOutputStream fo = new FileOutputStream(out1, false);
			fo.write(dataString.getBytes("UTF-8"));
			fo.flush();
			fo.close();
			
			InputSource inputSource = new InputSource(tempfile); 
			
			transformer.setParameter("source", source);
			transformer.setParameter("homepage", "homepage transfer from java to xsl " ); 
			transformer.transform(new SAXSource(xmlReader,inputSource), streamResult);

			inputSource = null;
			fileOutputStream.close();
			fileOutputStream.flush();
						
		}

}

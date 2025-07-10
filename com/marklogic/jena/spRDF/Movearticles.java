package com.marklogic.jena.spRDF;




import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.marklogic.client.DatabaseClient;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
 


public class Movearticles {

	private DatabaseClient db_mlsand_client;
	private DatabaseClient db_mlenodetest1_client;

	 
	public static URI uri;
	public static ContentSource cs;
	public static Session session;
	public static Request request;
	public static ResultSequence rs;

	public static URI uri_testDB;
	public static ContentSource cs_testDB;
	public static Session session_testDB;
	public static Request request_testDB;
	public static ResultSequence rs_testDB;
	
	
	/*
	 *initially mltestdnode1 xcc connection was not working 
	 *-to resolve 
	 *changed authentication to 9001 to digestbasic
	 *added new xcc.jar file from ml8 
	 
	 */
	 
	
			
			
	public Movearticles(String[] args) throws Exception
	{
		System.out.println("START       " ); 
		uri = new URI("xcc://loader:Loader123@marklogic11.scholarsportal.info:9006/ejournals");
		cs = ContentSourceFactory.newContentSource(uri);
		session =cs.newSession();

		uri_testDB = new URI("xcc://loader:Loader123@mldnodetest1.scholarsportal.info:9001/ejournals");
		cs_testDB = ContentSourceFactory.newContentSource(uri_testDB);
		session_testDB =cs_testDB.newSession();


		String query = "cts:uris('*',('any'),cts:directory-query('/','1'))";

		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);

		if(rs.asString().length() > 0)
		{
			String issn[] =  rs.asString().split("\n");

			for(int in=0; in < issn.length; in++) //
			{
				try{
					query = "cts:uri-match('"+issn[in] +"journal-info.xml')";
					//query = "cts:uri-match('/LOGS/client.xml')";  
					//query = "cts:uri-match('/LOGS/collection-groups.xml')";  
					request = session.newAdhocQuery(query);

					rs = session.submitRequest(request);
					if(rs.asString().length() > 0)
					{
						String[] uri_list = rs.asString().split("\n");

						String q = "";
						for (int i = 0; i < uri_list.length; i++)
						{
							try
							{
								System.out.println(in + " -- " + uri_list[i]); 
								query="xdmp:quote(fn:doc('"+ uri_list[i] +"'))";								 
								
								request = session.newAdhocQuery(query);
								rs = session.submitRequest(request);
								if(rs.asString().length() > 0)
								{
									String sourceDocument =rs.asString();
									sourceDocument = sourceDocument.replace("&", "&amp;").replace("'", "&rsquo;");
									//q = "xdmp:document-insert('"+ uri_list[i]+"', xdmp:unquote('" + sourceDocument +"'))";
									
									q = "import module namespace alert = 'http://marklogic.com/xdmp/alert'  at '/MarkLogic/alert.xqy';  "+
												"  let $uri :=( '/23248440/v2016inone/1_kp_ieee.xml')  "+
												"  return alert:invoke-matching-actions('/alert', fn:doc($uri), <options/>) ";
									request_testDB = session_testDB.newAdhocQuery(q); 
									rs_testDB = session_testDB.submitRequest(request_testDB);
									System.exit(0);
								}	 
							}catch( Exception e )
							{
								System.out.println("Rejected e " + e);
								e.printStackTrace();
								System.out.println(q);
								System.exit(0);
							}
						}
					}
					else
					{
						System.out.println("No article exists : " +issn[in]  );
					}
				}catch( Exception e )
				{
					System.out.println("Exception e " + e);
					e.printStackTrace();
				}
			}
		}
	}

	public void textmining_rights() throws Exception
	{
		// in admin interface - loader role added to "Execute Privilege: xdmp:http-get" 
		
		uri = new URI("xcc://loader:Loader123@marklogic11.scholarsportal.info:9006/ejournals");
		cs = ContentSourceFactory.newContentSource(uri);
		session =cs.newSession();
		
		String query = "let $rights  :=   let $url := fn:concat('https://ocul.scholarsportal.info/licenses/api/?tag=',$license_tag)  "+
		"let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  return ($probe//license//text_mining//usage/text())  ";
		
		/*
	
		for $i in doc('/LOGS/collection-groups.xml')//sub-collection 
		let $name := $i/@name
		let $licensetag := $i/@license_tag 
		return 
		if($licensetag) 
		then   
		 fn:concat($name  , ' ? ' , $licensetag  , '  ?' ,
		 let $url := fn:concat('https://toronto.scholarsportal.info/licenses/api/?tag=',$licensetag)  
		 let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  
		 return ($probe//license//text_mining//usage/text()) 
		, ' ? ' ,
		 let $url := fn:concat('https://ottawa.scholarsportal.info/licenses/api/?tag=',$licensetag)  
		 let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  
		 return ($probe//license//text_mining//usage/text())
		, ' ? ' ,
		 let $url := fn:concat('https://ocul.scholarsportal.info/licenses/api/?tag=',$licensetag)  
		 let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  
		 return ($probe//license//text_mining//usage/text())
		)
		else (
		fn:concat($name)
		) 
		
		
		
		
		
		let $doc := doc('/LOGS/collection-groups.xml')
		for $i in $doc//collection 
		let $name := $i/@name
		let $licensetag := $i/@license_tag 
		let $textmining := $i/@textmining
		let $ocul_textmining := 
		let $url := fn:concat('https://ocul.scholarsportal.info/licenses/api/?tag=',$licensetag)  
		 let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  
		 return ($probe//license//text_mining//usage/text())
		let $ottawa_textmining := 
		 let $url := fn:concat('https://ottawa.scholarsportal.info/licenses/api/?tag=',$licensetag)  
		 let $probe := xdmp:http-get($url,<options xmlns='xdmp:http'></options>)[2]  
		 return ($probe//license//text_mining//usage/text())
		 
		 (:
		 xdmp:node-insert-child(doc('"+result[0]+"')/article, attribute article-type {'"+article_type+"'})
		 :)
		 
		return 
		if($licensetag) 
		then   
		 if ($ocul_textmining) then if ($textmining) then "ocul tag exists" else xdmp:node-insert-child($doc//collection[@name = $name] , attribute textmining {$ocul_textmining})
		 else if ($ottawa_textmining) then  if ($textmining) then " ottawa tag exists" else  xdmp:node-insert-child($doc//collection[@name = $name] , attribute textmining {$ottawa_textmining})
		 else ('1')
		else (
		 '2'
		) 



 
		 */
		request = session.newAdhocQuery(query);
		rs = session.submitRequest(request);
		
		System.out.println(rs.asString());
	}

	public static void main(String[] args) 
	{
		try
		{
			//Movearticles e= new Movearticles(args);
			
			System.out.println(  "START "   );
			
			 
			
			
		}
		catch(Exception e1)
		{
			System.out.println("Exception in main method");
			e1.printStackTrace();
		}
	}



} 

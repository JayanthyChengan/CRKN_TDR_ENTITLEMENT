package com.marklogic.jena.spRDF;
 
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class MyNamespaceContext implements NamespaceContext {
   private Map<String,String> map;

   public MyNamespaceContext() {
       map = new HashMap<String, String>();
   }

   public void setNamespace(String prefix, String namespaceURI) {
       map.put(prefix, namespaceURI);
   }

   public String getNamespaceURI(String prefix) {
       return (String) map.get(prefix);
   }

   public String getPrefix(String namespaceURI) {
       Set keys = map.keySet();
       for (Iterator iterator = keys.iterator(); iterator.hasNext();)
       {
           String prefix = (String) iterator.next();
           String uri = (String) map.get(prefix);
           if (uri.equals(namespaceURI)) return prefix;
       }
       return null;
   }

   public Iterator getPrefixes(String namespaceURI) {
       List<String> prefixes = new ArrayList<String>();
       Set keys = map.keySet();
       for (Iterator iterator = keys.iterator(); iterator.hasNext();)
       {
           String prefix = (String) iterator.next();
           String uri = (String) map.get(prefix);
           if (uri.equals(namespaceURI)) prefixes.add(prefix);
       }
       return prefixes.iterator();
   }

} 

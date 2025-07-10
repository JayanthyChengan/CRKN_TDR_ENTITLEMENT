package com.marklogic.PARights;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.marklogic.semantics.xsl.collectionxslTransform_22Jul;
import com.marklogic.semantics.xsl.loadGraph;
import org.apache.poi.util.IOUtils;
import java.io.*;
import java.util.*;

public class PARights {

	private static String FILE_PATH = "data/input/perpetual/";

	public static void main(String[] args) throws Exception {

		collectionxslTransform_22Jul c = new collectionxslTransform_22Jul(); 
		loadGraph l = new loadGraph();
		String[] publisherList = {   "wiley"}; // {"sage", "acs", "iop","rsc","tandf",};

		for (int i = 0; i < publisherList.length; i++)
		{
			FILE_PATH = "data/input/perpetual/";
			String publisher = publisherList[i];
			FILE_PATH= FILE_PATH + publisher + "/CRKN_PARightsTracking.xlsx" ;
			
			System.out.println(FILE_PATH);
			
			
			Map<String, LinkedHashMap<String, JournalInfo>> data = loadDataFromExcel(FILE_PATH, publisher);

			

			for (String university : data.keySet()) {
				generateXMLForUniversity(university, publisher, data.get(university));
			}

			generateXMLForPublisher(  publisher, data.get(publisher));
			
			String PARightsClient_stylesheet = "data/stylesheet/PARightsClient_rdfTransformer.xsl";  
			String PARightsPortfolio_stylesheet = "data/stylesheet/PARightsPortfolio_rdfTransformer.xsl";
			c.XSLISSNtransform_PARights( publisher ,   PARightsClient_stylesheet, PARightsPortfolio_stylesheet );
			
			String graphURI = "http://scholarsportal.info/graphs/Organization"; 
			File directoryPath = new File("data/output/perpetual/"+publisher);   		
			File[] filesList  = directoryPath.listFiles(); 
			l.loadFiles (filesList , graphURI) ; 
		}
		
	}

	private static void generateXMLForPublisher(String publisher, LinkedHashMap<String, JournalInfo> titles) throws IOException {
		StringBuilder xmlContent = new StringBuilder();

		xmlContent.append("        <PARights name=\"")
		.append(publisher) 
		.append("\">\n"); 

		for (Map.Entry<String, JournalInfo> entry : titles.entrySet()) {
			String title = processTitle(entry.getKey());
			JournalInfo journalInfo = entry.getValue();

			xmlContent.append("        <issn print=\"")
			.append(journalInfo.getPrintISSN())
			.append("\" online=\"")
			.append(journalInfo.getOnlineISSN())
			.append("\">\n");

			xmlContent.append("            <title>").append(title).append("</title>\n");

			for (String year : journalInfo.getYears()) {
				xmlContent.append("            <year>").append(year).append("</year>\n");
			}

			xmlContent.append("        </issn>\n");
		}


		xmlContent.append("</PARights>");

		// Ensure the output directory exists
		File outputDir = new File("data/input/perpetual/" + publisher);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		// Write the XML content to a file in the specified directory
		File file = new File(outputDir, publisher + "_pacoverage.xml");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(xmlContent.toString());
		}

	}

	private static Map<String, LinkedHashMap<String, JournalInfo>> loadDataFromExcel(String filePath, String publisher) throws IOException {
		Map<String, LinkedHashMap<String, JournalInfo>> data = new HashMap<>();

		IOUtils.setByteArrayMaxOverride(150_000_000);
		
		FileInputStream file = new FileInputStream(new File(filePath));
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheet("PA-Rights");

		// Get the header row for university names
		Row headerRow = sheet.getRow(2); // Assuming university names are on the 3rd row (index 2)

		for (int i = 10; i < headerRow.getLastCellNum(); i++) { // Start from column 10 for university names
			String university = headerRow.getCell(i).getStringCellValue();
			data.put(university, new LinkedHashMap<>()); // Use LinkedHashMap to maintain order
		}

		data.put(publisher, new LinkedHashMap<>()); // Use LinkedHashMap to maintain order

		for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Data starts from row 4
			Row row = sheet.getRow(rowIndex);
			if (row == null) continue;

			String title = row.getCell(0).getStringCellValue().trim();
			String printISSN = row.getCell(2).getStringCellValue().toLowerCase().trim();
			String onlineISSN = row.getCell(3).getStringCellValue().toLowerCase().trim();
			
			System.out.println("title:    "+ title);
			String year = ""; 
			try
			{
				 year = String.valueOf((int) row.getCell(7).getNumericCellValue()).trim();
			}
			catch (Exception  e)
			{
				 year = row.getCell(7).getStringCellValue().toLowerCase().trim();
			}
			
			

			for (int colIndex = 10; colIndex < headerRow.getLastCellNum(); colIndex++) {
				Cell cell = row.getCell(colIndex);
				if (cell != null && cell.getStringCellValue().startsWith("Y")) {
					String university = headerRow.getCell(colIndex).getStringCellValue();
					data.get(university).computeIfAbsent(title, k -> new JournalInfo(printISSN, onlineISSN)).addYear(year);
				}
				data.get(publisher).computeIfAbsent(title, k -> new JournalInfo(printISSN, onlineISSN)).addYear(year);
			}
		}

		workbook.close();
		file.close();

		return data;
	}

	private static void generateXMLForUniversity(String university, String publisher,  LinkedHashMap<String, JournalInfo> titles) throws IOException {
		StringBuilder xmlContent = new StringBuilder();

		xmlContent.append("<client-groups>\n"); 
		xmlContent.append("    <client name=\"").append(university).append("\">\n");

		for (Map.Entry<String, JournalInfo> entry : titles.entrySet()) {
			String title = processTitle(entry.getKey());
			JournalInfo journalInfo = entry.getValue();

			xmlContent.append("        <issn print=\"")
			.append(journalInfo.getPrintISSN())
			.append("\" online=\"")
			.append(journalInfo.getOnlineISSN())
			.append("\">\n");

			xmlContent.append("            <title>").append(title).append("</title>\n");

			for (String year : journalInfo.getYears()) {
				xmlContent.append("            <year>").append(year).append("</year>\n");
			}

			xmlContent.append("        </issn>\n");
		}

		xmlContent.append("    </client>\n");
		xmlContent.append("</client-groups>");

		// Ensure the output directory exists
		File outputDir = new File("data/input/perpetual/" + publisher);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		// Write the XML content to a file in the specified directory
		File file = new File(outputDir, university.replace(" ", "_") + ".xml");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(xmlContent.toString());
		}
	}

	private static String processTitle(String title) {
		// Case 1: Remove "<=>"
		title = title.replace(" <=>", "");

		// Case 2: Append "amp;" after "&"
		title = title.replace("&", "&amp;");

		return title;
	}

	// Helper class to store journal information
	static class JournalInfo {
		private final String printISSN;
		private final String onlineISSN;
		private final Set<String> years;

		public JournalInfo(String printISSN, String onlineISSN) {
			this.printISSN = printISSN;
			this.onlineISSN = onlineISSN;
			this.years = new LinkedHashSet<>(); // Use LinkedHashSet to maintain order of years
		}

		public String getPrintISSN() {
			return printISSN;
		}

		public String getOnlineISSN() {
			return onlineISSN;
		}

		public Set<String> getYears() {
			return years;
		}

		public void addYear(String year) {
			years.add(year);
		}
	}
}

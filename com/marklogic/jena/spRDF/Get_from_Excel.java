package com.marklogic.jena.spRDF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.poi.hssf.usermodel.*;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Get_from_Excel {

	public HashMap<String, String> get_values(int sheet_no,String filename,int c1, int c2 , int startrow)
	{
		HashMap<String, String> input_values = new HashMap();
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filename)); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheet_no);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if(row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}
			
			String key = "";
			String value = "";
			for(int r = startrow; r < rows; r++) {
				row = sheet.getRow(r);
				if(row != null) {
					for(int c = 0; c < cols; c++) {
						cell = row.getCell((short)c);
						if(cell != null) {
							// Your code here
							if(c == c1)
							{	
								key = cell.toString().trim(); 
							}
							else if(c == c2)
							{
								value = cell.toString().trim(); 
							}
						}
					}
					input_values.put(key, value);
				}
			}
		} catch(Exception ioe) {
			ioe.printStackTrace();
			System.exit(0);;
		}


		return input_values;

	}
	
	

	public Map<String, List<String>> get_values_cols(String sheet_loc, int col1 , int col2)
	{
		//HashMap<String, String> input_values = new HashMap();
		Map<String, List<String>> input_values= new HashMap<String, List<String>>();
		try {
			FileInputStream fis = new FileInputStream(sheet_loc);
			POIFSFileSystem fs = new POIFSFileSystem(fis); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if(row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}
			

			//System.out.println("nu of rows : " + rows);
			List<String> l = null;
			for(int r = 0; r < rows; r++) {
				String key = "";
				String value = "";
				l=new ArrayList<String>();
				boolean status = false;
				row = sheet.getRow(r);
				if(row != null) {
					for(int c = 0; c < cols; c++) {
						cell = row.getCell((short)c);
						
						// Your code here
						if(c ==col1)
						{
							//System.out.println("row is : "+r );
							if(cell  != null)
							{
							key = cell.toString().trim();			
							//System.out.println("Key is : "+key );
							}
						}
						else if(c ==col2)
						{
							if(cell != null) {
								value = cell.toString().trim();	
							//	System.out.println("value is " +value);
							//	System.out.println("checking for key :" + key);
								l = input_values.get(key);
								
								if(l == null)
								{
									l=new ArrayList<String>();
								}
								else
								{
								//	System.out.println("Size is "+l.size() ) ;
									for (int k =0 ; k < l.size(); k ++)
									{
										if(l.get(k).equalsIgnoreCase(value))
										{
											status = true;
										}											
									}
								}
								if (!status){
									if(value.length() > 0)
									{
										l.add(value);
									//	System.out.println("value added ");
									}
								}
							}
							else
							{
								//System.out.println("Column is null ");
							}
						}
					}
					input_values.put(key, l);
				}
			}
			fis.close();			
		
		} catch(Exception ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}


		return input_values;

	}
	

	public Map<String, List<String>> get_values_cols(String sheet_loc, int sheet_no ,int col1 , int col2)
	{
		//HashMap<String, String> input_values = new HashMap();
		Map<String, List<String>> input_values= new HashMap<String, List<String>>();
		try {
			FileInputStream fis = new FileInputStream(sheet_loc);
			POIFSFileSystem fs = new POIFSFileSystem(fis); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheet_no);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if(row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}
			

			//System.out.println("nu of rows : " + rows);
			List<String> l = null;
			for(int r = 0; r < rows; r++) {
				String key = "";
				String value = "";
				l=new ArrayList<String>();
				boolean status = false;
				row = sheet.getRow(r);
				if(row != null) {
					for(int c = 0; c < cols; c++) {
						cell = row.getCell((short)c);
						
						// Your code here
						if(c ==col1)
						{
							//System.out.println("row is : "+r );
							if(cell  != null)
							{
							key = cell.toString().trim();			
							//System.out.println("Key is : "+key );
							}
						}
						else if(c ==col2)
						{
							if(cell != null) {
								value = cell.toString().trim();	
							//	System.out.println("value is " +value);
							//	System.out.println("checking for key :" + key);
								l = input_values.get(key);
								
								if(l == null)
								{
									l=new ArrayList<String>();
								}
								else
								{
								//	System.out.println("Size is "+l.size() ) ;
									for (int k =0 ; k < l.size(); k ++)
									{
										if(l.get(k).equalsIgnoreCase(value))
										{
											status = true;
										}											
									}
								}
								if (!status){
									if(value.length() > 0)
									{
										l.add(value);
									//	System.out.println("value added ");
									}
								}
							}
							else
							{
								//System.out.println("Column is null ");
							}
						}
					}
					input_values.put(key, l);
				}
			}
			fis.close();			
		
		} catch(Exception ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}


		return input_values;

	}
	
/*	public static void main(String args[])
	{
		Get_from_Excel t = new Get_from_Excel();
	
		HashMap<String, String> getValues = t.get_values(0,"");
	
		Iterator<String> itr1 = getValues.keySet().iterator();
		
		while(itr1.hasNext())
		{
			String temp = itr1.next();
			System.out.println(temp);
		}
	}
*/
	
	public   Map<String, List<String>> readFromExcel(String fileName, short keyColumn, short valueColumn){
		try {

            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
            HSSFSheet firstSheet = workbook.getSheetAt(0);
            
            Map<String, List<String>> fileNameCollections = new HashMap<String, List<String>>();
            HSSFRow currentRow = null;
            HSSFCell cellKey = null;
            HSSFCell cellValue = null;
            
           //  for(int i = 3; i < 100; i++) { //firstSheet.getPhysicalNumberOfRows()
            for(int i = 3; i < 2000; i++) { //firstSheet.getPhysicalNumberOfRows()

            	currentRow =  firstSheet.getRow(i);
                
            	if(currentRow != null){
                	cellKey = currentRow.getCell(keyColumn);
                	cellValue = currentRow.getCell(valueColumn);
                    if(cellKey != null && !cellKey.getRichStringCellValue().toString().isEmpty() && cellValue != null){
                    	addToMap(fileNameCollections, cellKey+"", cellValue+"");
                    }
                }else{
                	//skip empty row
                	//System.out.println(" current Row is null");
                }
            }
            return fileNameCollections;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public   Map<String, List<Integer>> readFromExcelPAtest(String fileName, short keyColumn, short valueColumn){
		try {

            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
            HSSFSheet firstSheet = workbook.getSheetAt(0);
            
            Map<String, List<Integer>> fileNameCollections = new HashMap<String, List<Integer>>();
            HSSFRow currentRow = null;
            HSSFCell cellKey = null;
            HSSFCell cellValue = null;
            
           //  for(int i = 3; i < 100; i++) { //firstSheet.getPhysicalNumberOfRows()
            for(int i = 1; i < firstSheet.getPhysicalNumberOfRows(); i++) { //

            	currentRow =  firstSheet.getRow(i);
                
            	if(currentRow != null){
                	cellKey = currentRow.getCell(keyColumn);
                	cellValue = currentRow.getCell(valueColumn);
                    if(cellKey != null && !cellKey.getRichStringCellValue().toString().isEmpty() && cellValue != null){
                    	
                    	 // Parse the string to a floating-point number
                        double doubleValue = Double.parseDouble(cellValue+"");
                        
                        // Cast the floating-point number to an integer
                        int intValue = (int) doubleValue;
                        
                        
                    	addToMap(fileNameCollections, cellKey+"", intValue);
                    }
                }else{
                	//skip empty row
                	//System.out.println(" current Row is null");
                }
            }
            return fileNameCollections;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	private void addToMap(Map<String, List<Integer>> map, String key, Integer value) {
		// TODO Auto-generated method stub
		List<Integer>values = map.get(key);
		if(values == null){
			 values = new ArrayList<Integer>();
		}
		putList(values, value);
		
		map.put(key, values);
	}

	private   void putList(List<Integer> sourceISSNs, Integer issn){
		
		if(issn != null  ){
			sourceISSNs.add(issn);
		}
	}


	public   Map<String, List<String>> readFromExcel2(String fileName, short keyColumn, short valueColumn){
		try {

            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
            HSSFSheet firstSheet = workbook.getSheetAt(0);
            
            Map<String, List<String>> fileNameCollections = new HashMap<String, List<String>>();
            HSSFRow currentRow = null;
            HSSFCell cellKey = null;
            HSSFCell cellValue = null;
            
            for(int i = 3; i < 2000; i++) {//firstSheet.getPhysicalNumberOfRows()
            	//System.out.println("1");
            	currentRow =  firstSheet.getRow(i);
            	HSSFRow firstRow  =  firstSheet.getRow(2);
                
            	if(currentRow != null){
            		//System.out.println("2");
                	cellKey = currentRow.getCell(keyColumn);
                	cellValue = currentRow.getCell(valueColumn);
                	//System.out.println("cellKey:"+cellKey);
                	//System.out.println("cellValue:"+cellValue);
                    if(cellKey != null && !cellKey.getRichStringCellValue().toString().isEmpty() && cellValue != null){
                    	//System.out.println("3");
                    	for ( int s = 10 ; s <=85 ; s++)
                    	{
                    		//System.out.println("3 : " + currentRow.getCell(s));
                    		if (currentRow.getCell(s).toString().equals("Y"))
                    		{
                    			//System.out.println("3 : key " + cellKey+":"+cellValue);
                    			//System.out.println("3 : value " + firstRow.getCell(s));
                    			addToMap(fileNameCollections, cellKey+":"+cellValue, firstRow.getCell(s)+"");
                    		}
                    	}
                    }
                }else{
                	//skip empty row
                	//System.out.println(" current Row is null");
                }
            }
            return fileNameCollections;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public   void addToMap(Map<String, List<String>> map, String key, String value){
		
		List<String>values = map.get(key);
		if(values == null){
			 values = new ArrayList<String>();
		}
		putList(values, value);
		
		map.put(key, values);
	}
	
	private   void putList(List<String> sourceISSNs, String issn){
		
		if(issn != null && !issn.isEmpty()){
			sourceISSNs.add(issn);
		}
	}

}
package resultinExcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CheckingForLoop {

	public static void main(String[] args) throws IOException {
		ArrayList<String> storingPdfTitle = new ArrayList<>();
		ArrayList<String> StoringMonth = new ArrayList<>();
		int rowNum =0;
		for (int i = 2023; i  >= 2016; i--) 
		{
			for(int j = 1; j <= 12; j++) 
			{
				for(int k = 100; k <= 110; k++) 
				{	String demo ="hello";
					storingPdfTitle.add(k+"");
					StoringMonth.add(j+"");
					
				}
				//storingPdfTitle.add("Month Completed");
			}
			
			//storingPdfTitle.add("Year Completed");
		}
		 
		
		for(String title : storingPdfTitle ) 
		{
			System.out.println(title);
		}
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		  XSSFSheet testing = workbook.createSheet("Sheet");
		 
		  System.out.println(storingPdfTitle.size());
		  
		  System.out.println(StoringMonth.size());
			/*
			 * Row row = null; for(String months :StoringMonth ) { //Create row row
			 * =testing.createRow(rowNum++);
			 * 
			 * // Create cell Cell cell = row.createCell(0); // Enter value inside the cell
			 * cell.setCellValue(months); System.out.println(months+"aaaaa");
			 * 
			 * }
			 * 
			 * 
			 * rowNum =0;
			 * 
			 * for(String title :storingPdfTitle) { //Create row //row
			 * =testing.createRow(rowNum++);
			 * 
			 * // Create cell Cell cell = row.createCell(1,rowNum); // Enter value inside
			 * the cell cell.setCellValue(title);
			 * 
			 * }
			 */
		  
		  for(int m =0;m<storingPdfTitle.size();m++) 
		  {
			  Row row =testing.createRow(m);
			  Cell cell1 = row.createCell(0);
			  cell1.setCellValue(storingPdfTitle.get(m));
			  
			  Cell cell2 = row.createCell(1);
			  cell2.setCellValue(StoringMonth.get(m));
			  
		  }
		  
		  
		 
		  // Creating File for sheet 
		  File f = new File(
		  "D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\testdata5.xls"
		  );
		  
		  FileOutputStream fos = new FileOutputStream(f); 
		  workbook.write(fos);
		  fos.close();
		  workbook.close();
		}

	}



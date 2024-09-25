package Final;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestingExcel {

	public static void main(String[] args) throws IOException  {
		
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		
		XSSFSheet sheet1 = workbook.createSheet("Sheet");
		
		
		Row r0 =sheet1.createRow(0);
		Cell c0 =r0.createCell(0);
		c0.setCellValue("ramesh");
		

		Row r1 =sheet1.createRow(1);
		Cell c2 =r1.createCell(0);
		c2.setCellValue("suresh");
		
		Row r2 =sheet1.createRow(2);
		Cell c3 =r2.createCell(0);
		c3.setCellValue("mahesh");
		
		XSSFSheet sheet2 = workbook.createSheet("Sheet 2");
		
		
		File f = new File ("D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\testdata.xls");
		
		FileOutputStream fos = new  FileOutputStream(f);
		workbook.write(fos);
		fos.close();
		workbook.close();
		
		System.out.println("file run successfully ");
		
		
	}

}

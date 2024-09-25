package resultinExcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestingExcel {

	public static void main(String[] args) throws IOException {
		String title = "asycvshvcuhasdcajsdd";
		titlePrint(title);
	}

	public static void titlePrint(String title) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet3 = workbook.createSheet("Sheet");

		int numberOfRows = 10;

		// Create rows and cells
		for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
			Row row = sheet3.createRow(rowIndex);
			
				Cell cell = row.createCell(0);
				 cell.setCellValue(title);
			}
			
			//cell.setCellValue(title);

			XSSFSheet sheet2 = workbook.createSheet("Sheet 2");

			File f = new File("D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\testdata1.xls");

			FileOutputStream fos = new FileOutputStream(f);
			workbook.write(fos);
			fos.close();
			workbook.close();

			System.out.println("file run successfully ");

		}

	
}

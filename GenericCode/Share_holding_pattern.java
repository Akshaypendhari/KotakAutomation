package GenericCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Share_holding_pattern {

	static WebDriver driver = null;
	static JavascriptExecutor js;

	// XPATH
	static String URL = "https://www.kotak.com/en/investor-relations/investor-information/share-holding-pattern.html";
	static String URL2 = "https://www.kotak.com/en/investor-relations/investor-information.html";
	static String arrow = "//div[@class=\"owl-next\"]";
	static String dropdown = "//button[@class=\"btn dropdown-toggle btn-default\"]";
	static String quaterList = "//ul[@class=\"dropdown-menu inner\"]/li";
	static String tabledata = "(//table[@class=\"table table-striped\"])/tbody/tr/td[3]";

	
	static ArrayList<String> storingPdfTitle = new ArrayList<>();
	static ArrayList<String> StoringQuarter = new ArrayList<>();
	static ArrayList<String> StoringYear = new ArrayList<>();
	
	static ArrayList<String> issuePdf = new ArrayList<>();
	static ArrayList<String> quarternameforPDFissue = new ArrayList<>();
	static ArrayList<String> yearForPDFissue = new ArrayList<>();
	
	static XSSFWorkbook workbook2 = new XSSFWorkbook();
	static XSSFSheet WorkingPDF = workbook2.createSheet("WorkingPDF");
	static XSSFSheet pdferror = workbook2.createSheet("Error PDf ");
	
	
	@BeforeTest
	public void launchbrowser() {

		// Set up WebDriver
		System.setProperty("webdriver.chrome.driver", "./Driver/chromedriver.exe");

		ChromeOptions opt = new ChromeOptions();
		opt.addArguments("--remote-allow-origins=*");
		opt.addArguments("--no-sandbox");
		opt.addArguments("--disable-dev-shm-usage");
		opt.addArguments("--disable-notifications");
		opt.addArguments("disable-infobars");

		driver = new ChromeDriver(opt);

		driver.manage().window().maximize();
		driver.get(URL);
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, 400)");
	}

	@Test(description = "This is my custom test name" ,priority =0)
	public void yearSelection() {

		for (int i = 1; i <= 15; i++) {
			try {
				WebElement year = driver
						.findElement(By.xpath("(//span[@class=\"financial-selected-year\"])[" + i + "]"));

				if (year.isDisplayed()) {

					String nameofyear = year.getText();

					Actions actions = new Actions(driver);

					// js.executeScript("arguments[0].click();", year);
					actions.click(year).build().perform();

					// year.click();
					System.out.println("CURRENTLY WE ARE ON " + nameofyear + "");

					quaterdropdown(nameofyear);
					
					

				} else {
					driver.findElement(By.xpath(arrow)).click();
					Thread.sleep(2000);
					i--;

				}

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
			}

		}
		
		
	}

	public void quaterdropdown(String nameofyear) throws InterruptedException {

		System.out.println("we click on dropdown ");

		List<WebElement> quarters = driver.findElements(By.xpath(quaterList));
		for (int j = 1; j <= quarters.size(); j++) {

			driver.findElement(By.xpath(dropdown)).click();

			WebElement quarter = driver.findElement(By.xpath("//ul[@class=\"dropdown-menu inner\"]/li[" + j + "]"));
			String quarterName = quarter.getText();
			quarter.click();
			System.out.println("we are clicking on " + quarterName);
			
			

			// js.executeScript("arguments[0].click();", quarter);
			Thread.sleep(2000);

			List<WebElement> allDataFromTable = driver.findElements(By.xpath(tabledata));
			int rowcount = allDataFromTable.size();
			
			System.out.println(rowcount);
			
			APIstatus(nameofyear,quarterName,j);
			
			pdfChecker(rowcount, nameofyear, quarterName);

			// APIstatus(nameofyear, quarterName);

		}

	}

	public static void pdfChecker(int rowcount, String nameofyear ,String quarterName) {
		for (int k = 1; k <= rowcount; k++) {

			try {
				WebElement pdflink = driver.findElement(By.xpath(
						"(//table[@class=\"table table-striped\"])/tbody/tr["+k +"]/td[3]/a"));
				String href = pdflink.getAttribute("href");
			

				WebElement title = driver.findElement(By
						.xpath("(//table[@class=\"table table-striped\"])/tbody/tr["+k+"]/td[1]"));
				String pdftitle = title.getText();

				try {

					URL url = new URL(href);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setRequestMethod("GET");

					int responseCode = connection.getResponseCode();

					// System.out.println("Below URLS are not loading properly ");

					if (responseCode == 200)

					{

						System.out.println("" + k + " PDF is loading properly " + responseCode + "");
						// Create row
						
						  storingPdfTitle.add(pdftitle);
						  StoringQuarter.add(quarterName);
						  StoringYear.add(nameofyear);
						 

					}

					else

					{

						System.out.println("Not working " + url + ": " + responseCode + "");
						// If we are getting 500 or 403 error for any pdf then this error will store
						
						
						  issuePdf.add(pdftitle);
						  quarternameforPDFissue.add(quarterName);
						  yearForPDFissue.add(nameofyear);
						 
						 

					}

				} catch (IOException e) {

					e.printStackTrace();
					System.out.println("PDF Not authored  ");

					/*
					 * authoring.add(pdftitle); month.add(monthName); year.add(nameofyear);
					 */

					// issuePdf.add(pdftitle);
				}
			}

			catch (Exception e) {
				System.out.println(nameofyear + quarterName + k + " : " + "PDF not found ");
				/*
				 * authoring.add("Row " + k + " Dont have any PDF"); month.add(monthName);
				 * year.add(nameofyear);
				 */

			}

		}
	
	}

	public static void APIstatus(String nameofyear, String quarterName, int j) {
		
		//int number = Integer.parseInt(str.trim());

	

		String apiUrl = "https://www.kotak.com/content/kotakcl/en/investor-relations/financial-results/jcr:content/icontent_par/tab/financialresults1498543599966/financialresults_cop.getresults."+nameofyear+".Q"+j+".esc.json?_=1725276806408";

		try {

			URL url = new URL(apiUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == 200)

			{
				// output = ("API for the " + monthname + " month are working fine");
				System.out.println("API for the " + quarterName + " are working fine");
			}

			else

			{
				String APIerror = nameofyear + "API not working for" + quarterName + "we are getting" + responseCode + "Error";
				System.out.println(nameofyear + ":" + " API not working for " + quarterName + " we are getting  " + responseCode
						+ " Error");

				/*
				 * monthAPIissue.add(APIerror); issuemonth.add(monthname); issueyear.add(year);
				 */

			}

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println("Having issue in " + quarterName + " API");

		}

	}
	
	@Test(priority =1)
	public static void storingDataintoExcel() throws IOException {
		// Creating headers 
		Row row0 = WorkingPDF.createRow(0);
		
		Cell cell1 = row0.createCell(0);
		cell1.setCellValue("YEAR");

		Cell cell2 = row0.createCell(1);
		cell2.setCellValue("QUARTER");

		Cell cell3 = row0.createCell(2);
		cell3.setCellValue("PDF TITLE");
		
		// Storing data
		
		for (int m = 1; m < storingPdfTitle.size(); m++) {
			Row row = WorkingPDF.createRow(m);
			Cell cell4 = row.createCell(0);
			cell4.setCellValue(StoringYear.get(m));

			Cell cell5 = row.createCell(1);
			cell5.setCellValue(StoringQuarter.get(m));

			Cell cell6 = row.createCell(2);
			cell6.setCellValue(storingPdfTitle.get(m));

		}
		
		// Creating headers 
				Row pdferrorrow = pdferror.createRow(0);
				
				Cell cell7 = pdferrorrow.createCell(0);
				cell7.setCellValue("YEAR");

				Cell cell8 = pdferrorrow.createCell(1);
				cell8.setCellValue("QUARTER");

				Cell cell9 = pdferrorrow.createCell(2);
				cell9.setCellValue("PDF TITLE");
		
		for (int n = 1; n < issuePdf.size(); n++) {
			Row row = pdferror.createRow(n);
			Cell cell4 = row.createCell(0);
			cell4.setCellValue(yearForPDFissue.get(n));

			Cell cell5 = row.createCell(1);
			cell5.setCellValue(quarternameforPDFissue.get(n));

			Cell cell6 = row.createCell(2);
			cell6.setCellValue(issuePdf.get(n));
		}
		
		File f = new File("D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\Share_Holding_pattern.xls");

		FileOutputStream fos = new FileOutputStream(f);
		workbook2.write(fos);
		fos.close();
		workbook2.close();
		System.out.println("Data stored properly ");
	}
  
  
  @AfterTest 
  public static  void closeBrowser() {
	  driver.quit(); }
  


	
}

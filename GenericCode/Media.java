package GenericCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import io.github.bonigarcia.wdm.WebDriverManager;
import io.opentelemetry.exporter.logging.SystemOutLogExporter;
import resultinExcel.ApiStatusCode;

public class Media {
	static WebDriver driver = null;
	static JavascriptExecutor js;

	// XPATH
	static String URL = "https://www.kotak.com/en/about-us/media.html";
	static String dropdown = "//div[@class='col-md-3 col-sm-3 col-xs-12 pull-left']";
	static String Yearlist = "(//div[@class=\"btn-group bootstrap-select media-filter\"])/div/ul/li";

	static ArrayList<String> storingPdfTitle = new ArrayList<>();
	
	static ArrayList<String> StoringYear = new ArrayList<>();

	static ArrayList<String> issuePdf = new ArrayList<>();

	static ArrayList<String> yearForPDFissue = new ArrayList<>();

	static ArrayList<String> monthAPIissue = new ArrayList<>();
	
	static ArrayList<String> issueyear = new ArrayList<>();

	static ArrayList<String> authoring = new ArrayList<>();

	static ArrayList<String> year = new ArrayList<>();

	static Map<Integer, String> pdfTitles = new HashMap<>();

	static XSSFWorkbook workbook = new XSSFWorkbook();
	static XSSFSheet WorkingPDF = workbook.createSheet("Working PDF");
	static XSSFSheet pdferror = workbook.createSheet("Error PDF ");
	static XSSFSheet monthapiissue = workbook.createSheet("MonthAPIissues ");
	static XSSFSheet authoringissue = workbook.createSheet("Authoringissue");

	public static void main(String[] args) throws IOException {

		// Set up WebDriver
		System.setProperty("webdriver.chrome.driver", "D:\\backup\\Projects\\Intranet\\Driver\\chromedriver.exe");

		ChromeOptions opt = new ChromeOptions();
		opt.addArguments("--remote-allow-origins=*");
		opt.addArguments("--no-sandbox");
		opt.addArguments("--disable-dev-shm-usage");
		opt.addArguments("--disable-notifications");
		opt.addArguments("disable-infobars");

		driver = new ChromeDriver(opt);

		driver.manage().window().maximize();
		// Navigate to the website
		driver.get(URL);

		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

		// Scroll down
		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, 400)");

		// sortingcheck();
		// yearSelection();
		IterationOfyear();
		// storingDataintoExcel();
	}

	public static void IterationOfyear() {
		try {
			// Click on year dropdown

			driver.findElement(By.xpath(dropdown)).click();
			Thread.sleep(2000);

			System.out.println("we click on year dropdown ");

			// Storing all month details
			List<WebElement> years = driver.findElements(By.xpath("(//ul[@class=\"dropdown-menu inner\"])[1]/li"));
			System.out.println(years.size());
			for (int j = 1; j <= years.size(); j++) {

				try {

					WebElement year = driver
							.findElement(By.xpath("(//ul[@class=\"dropdown-menu inner\"])[1]/li[" + j + "]/a/span[1]"));

					String YearName = year.getText();

					System.out.println("we are clicking on " + YearName + " year");

					year.click();

					// month.click();

					Thread.sleep(2000);

					// Fetching data from table

					List<WebElement> allDataFromTable = driver
							.findElements(By.xpath("(//div[@class=\"stretch-row dynamic-cards-media\"])/div"));
					int rowcount = allDataFromTable.size();
					System.out.println("row count is " + rowcount + "");

					// storing each href

					pdfChecker(rowcount, YearName);

					driver.findElement(By.xpath(dropdown)).click();

				} catch (Exception e) {
					System.out.println("An error occurred while processing month " + j + ": " + e.getMessage());

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void pdfChecker(int rowcount, String yearName) {
		// TODO Auto-generated method stub

		for (int k = 1; k <= rowcount; k++) {

			try {
				WebElement pdflink = driver.findElement(
						By.xpath("(//div[@class=\"stretch-row dynamic-cards-media\"])/div[" + k + "]/div/a"));
				String href = pdflink.getAttribute("href");

				WebElement title = driver.findElement(
						By.xpath("(//div[@class=\"stretch-row dynamic-cards-media\"])/div[" + k + "]/div/div"));
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
						StoringYear.add(yearName);

					}

					else

					{

						System.out.println("Not working " + url + ": " + responseCode + "");
						// If we are getting 500 or 403 error for any pdf then this error will store

						issuePdf.add(pdftitle);
						
						yearForPDFissue.add(yearName);

					}

				} catch (IOException e) {

					e.printStackTrace();
					System.out.println("PDF Not authored  ");

					authoring.add(pdftitle);
					
					year.add(yearName);

					issuePdf.add(pdftitle);
				}
			}

			catch (Exception e) {
				System.out.println(yearName + k + " : " + "PDF not found ");

				authoring.add("Row " + k + " Dont have any PDF");
		
				year.add(yearName);

			}

		}
	}
	
	public static void storingDataintoExcel() throws IOException {
		for (int m = 1; m < storingPdfTitle.size(); m++) {
			Row row = WorkingPDF.createRow(m);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(StoringYear.get(m));

			Cell cell2 = row.createCell(1);
			
			cell2.setCellValue(storingPdfTitle.get(m));

		}
		for (int n = 1; n < issuePdf.size(); n++) {
			Row row = pdferror.createRow(n);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(issueyear.get(n));

			Cell cell2 = row.createCell(1);
			
			cell2.setCellValue(issuePdf.get(n));
		}

		for (int p = 1; p < monthAPIissue.size(); p++) {
			Row row = monthapiissue.createRow(p);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(issueyear.get(p));

			Cell cell2 = row.createCell(1);
			
			cell2.setCellValue(monthAPIissue.get(p));
		}

		for (int q = 1; q < authoring.size(); q++) {
			Row row = authoringissue.createRow(q);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(year.get(q));

			Cell cell2 = row.createCell(1);
			
			cell2.setCellValue(authoring.get(q));
		}

		File f = new File("D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\Media.xls");

		FileOutputStream fos = new FileOutputStream(f);
		workbook.write(fos);
		fos.close();
		workbook.close();
		System.out.println("Data stored properly ");

	}


	private static void waitForElement(WebDriver driver, By locator, Duration timeout) {
		new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
				.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private static String cleanText(String text) {
		return text.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
	}

}

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

public class sebi_listing_disclosures {
	static WebDriver driver = null;
	static JavascriptExecutor js;

	// XPATH
	static String URL = "https://www.kotak.com/en/investor-relations/governance/sebi-listing-disclosures.html";
	static String arrow = "//*[@id=\"sebi-listing\"]/div[1]/div/div/div[2]/div[2]";
	static String dropdown = "//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button";
	static String monthlist = "//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li/a/span[1]";
	static String tabledata = "//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]";

	static ArrayList<String> storingPdfTitle = new ArrayList<>();
	static ArrayList<String> StoringMonth = new ArrayList<>();
	static ArrayList<String> StoringYear = new ArrayList<>();

	static ArrayList<String> issuePdf = new ArrayList<>();
	static ArrayList<String> monthforPDFissue = new ArrayList<>();
	static ArrayList<String> yearForPDFissue = new ArrayList<>();

	static ArrayList<String> monthAPIissue = new ArrayList<>();
	static ArrayList<String> issuemonth = new ArrayList<>();
	static ArrayList<String> issueyear = new ArrayList<>();

	static ArrayList<String> authoring = new ArrayList<>();
	static ArrayList<String> month = new ArrayList<>();
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
		
		//sortingcheck();
		yearSelection();

		storingDataintoExcel();
	}

	public static void yearSelection() {
		for (int i = 1; i <= 10; i++) {

			try {

				WebElement year = driver.findElement(
						By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div[" + i + "]/div/div"));
				if (year.isDisplayed()) {

					String nameofyear = year.getText();
					year.click();

					System.out.println();
					System.out.println("CURRENTLY WE ARE ON " + nameofyear + "");

					IterationOfMonth(nameofyear);

					System.out.println();

					Actions actions = new Actions(driver);
					actions.click().build().perform();

					js.executeScript("window.scrollBy(0,-400)");

				} else {
					driver.findElement(By.xpath(arrow)).click();
					Thread.sleep(2000);
					i--;
				}
			} catch (Exception e) {
				System.out.println("An error occurred while processing year " + i + ": " + e.getMessage());
				e.printStackTrace();

			}
		}

	}

	public static void IterationOfMonth(String nameofyear) {
		try {
			// Click on month dropdown
			driver.findElement(By.xpath(dropdown)).click();
			Thread.sleep(2000);

			System.out.println("we click on dropdown ");
			// Storing all month details
			List<WebElement> months = driver.findElements(By.xpath(monthlist));

			for (int j = 1; j <= months.size(); j++) {

				try {

					WebElement month = driver.findElement(
							By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li[" + j
									+ "]/a/span[1]"));

					String monthName = month.getText();

					System.out.println("we are clicking on " + monthName + " month");

					js.executeScript("arguments[0].click();", month);

					// month.click();

					Thread.sleep(2000);

					// Fetching data from table

					List<WebElement> allDataFromTable = driver.findElements(By.xpath(tabledata));
					int rowcount = allDataFromTable.size();
					// System.out.println("row count is "+rowcount+"");
					System.out.println("We are selecting " + monthName + " from " + nameofyear + "");

					// Call the API status for month code example
					APIstatus(nameofyear, monthName, j);

					// String monthoutput = ApiStatusCode.APIstatus(nameofyear, monthName, j);

					// storing each href

					pdfChecker(rowcount, nameofyear, monthName);

					driver.findElement(By.xpath(dropdown)).click();

				} catch (Exception e) {
					System.out.println("An error occurred while processing month " + j + ": " + e.getMessage());

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void pdfChecker(int rowcount, String nameofyear, String monthName) {
		for (int k = 1; k <= rowcount; k++) {

			try {
				WebElement pdflink = driver.findElement(By.xpath(
						"//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr[" + k + "]/td[3]/a"));
				String href = pdflink.getAttribute("href");

				WebElement title = driver.findElement(By
						.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr[" + k + "]/td[2]"));
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
						StoringMonth.add(monthName);
						StoringYear.add(nameofyear);

					}

					else

					{

						System.out.println("Not working " + url + ": " + responseCode + "");
						// If we are getting 500 or 403 error for any pdf then this error will store
						issuePdf.add(pdftitle);
						monthforPDFissue.add(monthName);
						yearForPDFissue.add(nameofyear);

					}

				} catch (IOException e) {

					e.printStackTrace();
					System.out.println("PDF Not authored  ");

					authoring.add(pdftitle);
					month.add(monthName);
					year.add(nameofyear);

					// issuePdf.add(pdftitle);
				}
			}

			catch (Exception e) {
				System.out.println(nameofyear + monthName + k + " : " + "PDF not found ");
				authoring.add("Row " + k + " Dont have any PDF");
				month.add(monthName);
				year.add(nameofyear);

			}

		}
	}

	public static void APIstatus(String year, String monthname, int month) {
		String str = year;
		int number = Integer.parseInt(str.trim());

		// driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

		String apiUrl = "https://www.kotak.com/content/kotakcl/en/investor-relations/governance/_jcr_content/icontent_par/tab_157670678_copy/sebilistingdisclosures1498038929815/disclosure.disclosurefilter."
				+ number + "." + month + ".desc.esc.json";

		try {

			URL url = new URL(apiUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == 200)

			{
				// output = ("API for the " + monthname + " month are working fine");
				System.out.println("API for the " + monthname + " month are working fine");
			}

			else

			{
				String APIerror = year + "API not working for" + monthname + "we are getting" + responseCode + "Error";
				System.out.println(year + ":" + " API not working for " + monthname + " we are getting  " + responseCode
						+ " Error");

				monthAPIissue.add(APIerror);
				issuemonth.add(monthname);
				issueyear.add(year);

			}

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println("Having issue in " + monthname + " API");

		}

	}

	public static void storingDataintoExcel() throws IOException {
		for (int m = 1; m < storingPdfTitle.size(); m++) {
			Row row = WorkingPDF.createRow(m);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(StoringYear.get(m));

			Cell cell2 = row.createCell(1);
			cell2.setCellValue(StoringMonth.get(m));

			Cell cell3 = row.createCell(2);
			cell3.setCellValue(storingPdfTitle.get(m));

		}
		for (int n = 1; n < issuePdf.size(); n++) {
			Row row = pdferror.createRow(n);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(issueyear.get(n));

			Cell cell2 = row.createCell(1);
			cell2.setCellValue(monthforPDFissue.get(n));

			Cell cell3 = row.createCell(2);
			cell3.setCellValue(issuePdf.get(n));
		}

		for (int p = 1; p < monthAPIissue.size(); p++) {
			Row row = monthapiissue.createRow(p);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(issueyear.get(p));

			Cell cell2 = row.createCell(1);
			cell2.setCellValue(issuemonth.get(p));

			Cell cell3 = row.createCell(2);
			cell3.setCellValue(monthAPIissue.get(p));
		}

		for (int q = 1; q < authoring.size(); q++) {
			Row row = authoringissue.createRow(q);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(year.get(q));

			Cell cell2 = row.createCell(1);
			cell2.setCellValue(month.get(q));

			Cell cell3 = row.createCell(2);
			cell3.setCellValue(authoring.get(q));
		}

		File f = new File("D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\sebi-listing-disclosures.xls");

		FileOutputStream fos = new FileOutputStream(f);
		workbook.write(fos);
		fos.close();
		workbook.close();
		System.out.println("Data stored properly ");

	}

	public static void sortingcheck() {

		try {
			driver.get(URL);
			int yearnumber = 5;
			// Click on the year dropdown
			WebElement yearDropdown = driver.findElement(By.xpath(
					"//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div[" + yearnumber + "]/div/div/span[1]"));
			yearDropdown.click();

			// Wait for the table to be visible
			waitForElement(driver,
					By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"),
					Duration.ofSeconds(3));

			// Store the table data
			Map<Integer, String> pdfTitles = new HashMap<>();
			List<WebElement> rows = driver.findElements(
					By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr"));
			for (int i = 0; i < rows.size(); i++) {
				WebElement cell = rows.get(i).findElement(By.xpath("./td[2]"));
				pdfTitles.put(i + 1, cell.getText());
			}

			// Scroll down and click on sort dropdown >> Oldest first
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0, 400)");

			WebElement sortDropdown = driver.findElement(
					By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[2]/div/div/button/span[2]/span"));
			sortDropdown.click();

			// Wait and select "Oldest First"
			waitForElement(driver,
					By.xpath("(//div[@class=\"dropdown-menu open\"])[2]/ul/li/a/span[text()='Oldest First']"),
					Duration.ofSeconds(3));
			WebElement oldestFirstOption = driver.findElement(
					By.xpath("(//div[@class=\"dropdown-menu open\"])[2]/ul/li/a/span[text()='Oldest First']"));
			oldestFirstOption.click();

			// Wait for the sorting to be applied
			waitForElement(driver,
					By.xpath(
							"//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr[1]/td[2]"),
					Duration.ofSeconds(5));

			// Get the first and last PDF titles
			String firstPDFTitle = cleanText(driver
					.findElement(By.xpath(
							"//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr[1]/td[2]"))
					.getText());
			String lastPDFTitle = cleanText(pdfTitles.get(pdfTitles.size()));

			// Verify sorting
			if (firstPDFTitle.equalsIgnoreCase(lastPDFTitle)) {
				System.out.println(firstPDFTitle);
				System.out.println(lastPDFTitle);
				System.out.println("Sorting pdf working properly");
			} else {
				System.out.println("Sorting pdf is not working");
			}
		} finally {
			// Quit the WebDriver
			driver.quit();
		}
	}

	private static void waitForElement(WebDriver driver, By locator, Duration timeout) {
		new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
				.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private static String cleanText(String text) {
		return text.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
	}

}

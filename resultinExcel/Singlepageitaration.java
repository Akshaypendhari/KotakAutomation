package resultinExcel;

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

import Final.ApiStatusCode;

public class Singlepageitaration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebDriver driver = null;
		int rowNum=0;
		
		

		try {
			// Set up WebDriver
			System.setProperty("webdriver.chrome.driver", "./Driver/chromedriver.exe");

			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("--remote-allow-origins=*");
			opt.addArguments("--no-sandbox");
			opt.addArguments("--disable-dev-shm-usage");
			opt.addArguments("--disable-notifications");
			opt.addArguments("disable-infobars");

			driver = new ChromeDriver(opt);

			// Navigate to the website
			driver.get("https://www.kotak.com/en/investor-relations/governance/sebi-listing-disclosures.html");
			driver.manage().window().maximize();

			// Scroll down
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0, 400)");

			Thread.sleep(10);

			try {
				WebElement year = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div[4]/div/div"));
				if (year.isDisplayed()) {
					int count = 0;
					String nameofyear = year.getText();
					year.click();

					System.out.println();
					System.out.println("Currently we are clicking on " + nameofyear + "");

					driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

					// Click on month dropdown
					driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button")).click();

					// Storing all month details
					List<WebElement> months = driver.findElements(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li/a/span[1]"));

					driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

					ArrayList<String> storingPdfTitle = new ArrayList<>();
					ArrayList<String> storingIssuePDF= new ArrayList<>();

					// Iteration for the months
					for (int j = 1; j <= months.size(); j++) {
						try {
							int counter = 1;
							WebElement month = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li[" + j+ "]/a/span[1]"));

							String monthName = month.getText();

							month.click();
							storingPdfTitle.add(" ");
							storingPdfTitle.add(monthName);

							// Fetching data from table

							List<WebElement> allDataFromTable = driver.findElements(By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"));
							int rowcount = allDataFromTable.size();
							System.out.println("row count is " + rowcount + "");

							// Call the API status for month code example
							ApiStatusCode.APIstatus(nameofyear, monthName, j);

							// storing each href
							for (int k = 1; k <= rowcount; k++) {
								WebElement pdflink = driver.findElement(
										By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr["
												+ k + "]/td[3]/a"));
								String href = pdflink.getAttribute("href");

								WebElement title = driver.findElement(
										By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr["
												+ k + "]/td[2]"));
								String pdftitle = title.getText();

								// Storing value inside the excel

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
									
										

										

									}

									else

									{

										System.out.println("Not working " + url + ": " + responseCode + "");
										
										
										

									}
									
									
									
									 

								} catch (IOException e) {

									e.printStackTrace();

								}
								
							}
								
								System.out.println();
								
								// Clicking on month dropdown 
								
								  driver.findElement(By.xpath(
								  "//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button"))
								  .click();
								 

							
						} catch (Exception e) {
							System.out.println("An error occurred while processing month " + j + ": " + e.getMessage());
							String errormesaage = e.getMessage() ;
							storingPdfTitle.add(errormesaage);
							
						}
					}
					
					
					  XSSFWorkbook workbook = new XSSFWorkbook();
					  XSSFSheet testingsinglepage = workbook.createSheet(nameofyear);
					  
					  
					  for(String title :storingPdfTitle)
					  { //Create row 
						  Row row =testingsinglepage.createRow(rowNum++);
						  
							System.out.println(rowNum);
					  // Create cell 
					  Cell cell = row.createCell(0); // Enter value inside the cell 
					  cell.setCellValue(nameofyear+":"+title); } 
					  // Creating File for sheet 
					  File f = new File(
					  "D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\testdata2.xls"
					  );
					  
					  FileOutputStream fos = new FileOutputStream(f); 
					  workbook.write(fos);
					  fos.close();
					  workbook.close();
					 

					System.out.println();

					Actions actions = new Actions(driver);
					actions.click().build().perform();

					js.executeScript("window.scrollBy(0,-400)");

					driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

				} else {
					
					/*
					 * driver.findElement(By.xpath(
					 * "//*[@id=\"sebi-listing\"]/div[1]/div/div/div[2]/div[2]")).click();
					 * driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000)); i--;
					 */
				}
			} catch (Exception e) {
				System.out.println("An error occurred while processing year : " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("An unexpected error occurred: " + e.getMessage());
		} finally {
			// Clean up and close the browser
			if (driver != null) {
				driver.quit();
			}
		}

	}}





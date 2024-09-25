package resultinExcel;

import java.io.File; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.opentelemetry.exporter.logging.SystemOutLogExporter;



public class TestingJS{
	
	  static int rowNum=0;

	public static void main(String[] args) {
		WebDriver driver = null;
		
		

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
			ArrayList<String> storingPdfTitle = new ArrayList<>();
			ArrayList<String> StoringMonth = new ArrayList<>();
			ArrayList<String> StoringYear = new ArrayList<>();
			
			ArrayList<String> issuePdf = new ArrayList<>();
			ArrayList<String> monthforPDFissue = new ArrayList<>();
			ArrayList<String>  yearForPDFissue= new ArrayList<>();
			
			ArrayList<String> monthAPIissue = new ArrayList<>();
			ArrayList<Integer> issuemonth = new ArrayList<>();
			ArrayList<String> issueyear = new ArrayList<>();
			
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet loadPDF = workbook.createSheet("Sheet");
			XSSFSheet	pdferror = workbook.createSheet("Error PDf ");
			XSSFSheet	monthapiissue =	workbook.createSheet("MonthAPIissues ");
			

			for (int i = 1; i <=10; i++) {
				
				try {
					
					
					WebElement year = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div[" + i + "]/div/div"));
					if (year.isDisplayed()) {
						int count = 0;
						String nameofyear = year.getText();
						year.click();
						
						

						System.out.println();
						System.out.println("CURRENTLY WE ARE ON " + nameofyear + "");

						driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

						// Click on month dropdown
						driver.findElement(
								By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button"))
								.click();

						// Storing all month details
						List<WebElement> months = driver.findElements(By.xpath(
								"//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li/a/span[1]"));

						driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

						//storingPdfTitle.add(nameofyear);
						
						// Iteration for the months
						for (int j = 1; j <= months.size(); j++) {
							
								
							
							try {
								WebElement month = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li["
												+ j + "]/a/span[1]"));
								
							

								String monthName	 = month.getText();
								
								js.executeScript("arguments[0].click();", month);
								
								
								//storingPdfTitle.add(" ");


								// Fetching data from table

								List<WebElement> allDataFromTable = driver.findElements(By.xpath(
										"//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"));
								int rowcount = allDataFromTable.size();
								// System.out.println("row count is "+rowcount+"");
								System.out.println("We are selecting " + monthName + " from " + nameofyear + "");

								// Call the API status for month code example
								
								
								 ApiStatusCode.APIstatus(nameofyear, monthName, j);
							//	monthAPIissue.add(monthstatus);
								
								
								//storingPdfTitle.add(monthName);
								
								// storing each href
								for (int k = 1; k <= rowcount; k++) {
									WebElement pdflink = driver.findElement(By
											.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr["
													+ k + "]/td[3]/a"));
									String href = pdflink.getAttribute("href");

									WebElement title = driver.findElement(By
											.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr["
													+ k + "]/td[2]"));
									String pdftitle = title.getText();
									
									try {

										URL url = new URL(href);

										HttpURLConnection connection = (HttpURLConnection) url.openConnection();

										connection.setRequestMethod("GET");

										int responseCode = connection.getResponseCode();

										// System.out.println("Below URLS are not loading properly ");

										if (responseCode == 200)

										{

											//System.out.println("" + k + " PDF is loading properly " + responseCode + "");
											// Create row
											storingPdfTitle.add(pdftitle);
											StoringMonth.add(monthName);
											StoringYear.add(nameofyear);
											
	
											

										}

										else

										{

											System.out.println("Not working " + url + ": " + responseCode + "");
											issuePdf.add(pdftitle);
											monthforPDFissue.add(monthName);
											yearForPDFissue.add(nameofyear);
											

										}
										
										
										
										 

									} catch (IOException e) {

										e.printStackTrace();

									}

									

								}
								System.out.println();
								System.out.println();
								

								driver.findElement(By.xpath(
										"//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button"))
										.click();

							} catch (Exception e) {
								System.out.println("An error occurred while processing month " + j + ": " + e.getMessage());
								monthAPIissue.add("Element not found ");
								issuemonth.add(j);
								issueyear.add(nameofyear);
								
															
							}
							
						}
						
						
						

						System.out.println();

						Actions actions = new Actions(driver);
						actions.click().build().perform();

						js.executeScript("window.scrollBy(0,-400)");

						driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));
						
						
						
						

					} else {
						driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[2]/div[2]")).click();
						driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));
						i--;
					}
					
					
				} catch (Exception e) {
					System.out.println("An error occurred while processing year " + i + ": " + e.getMessage());
					
					
					
				}
				
			}
			
			
			
			  
			 
			  for (int m =1 ;m< storingPdfTitle.size() ;m++) 
			  {
				  Row row =loadPDF.createRow(m);
				  Cell cell1 = row.createCell(0);
				  cell1.setCellValue(StoringYear.get(m));
				  
				  Cell cell2 = row.createCell(1);
				  cell2.setCellValue(StoringMonth.get(m));
				  
				  Cell cell3 = row.createCell(2);
				  cell3.setCellValue(storingPdfTitle.get(m));
				  
				 
			  }
			  
			  
			  for (int o =1 ;o <issuePdf.size() ;o++) 
			  {
				  Row row =pdferror.createRow(o);
				  Cell cell1 = row.createCell(0);
				  cell1.setCellValue(issueyear.get(o));
				  
				  Cell cell2 = row.createCell(1);
				  cell2.setCellValue(monthforPDFissue.get(o));
				  
				  Cell cell3 = row.createCell(2);
				  cell3.setCellValue(issuePdf.get(o));
				  
				 
			  }
			
//			  for(String errorPdfTitle: issuePdf) 
//			  {
//				  Row row =pdferror.createRow(rowNum++);
//				  Cell cell0 = row.createCell(2);
//				  cell0.setCellValue(errorPdfTitle);
//			  }
//			  
//			  rowNum=0;
			  
				/*
				 * for(String monthAPI : monthAPIissue) { System.out.println(monthAPI);
				 * 
				 * Row row =monthapiissue.createRow(rowNum++); Cell cell = row.createCell(1);
				 * cell.setCellValue(monthAPI);
				 * 
				 * }
				 */
			  
			  for (int n =1 ;n< monthAPIissue.size() ;n++) 
			  {
				  Row row =monthapiissue.createRow(n);
				  Cell cell1 = row.createCell(0);
				  cell1.setCellValue(issueyear.get(n));
				  
				  Cell cell2 = row.createCell(1);
				  cell2.setCellValue(issuemonth.get(n));
				  
				  Cell cell3 = row.createCell(2);
				  cell3.setCellValue(monthAPIissue.get(n));
				  
				 
			  }
			  
			  
			 
			  
			  // Creating File for sheet 
			  File f = new File(
			  "D:\\backup\\Projects\\Intranet\\src\\test\\java\\resultinExcel\\StoringPdfData2.xls"
			  );
			  
			  FileOutputStream fos = new FileOutputStream(f); 
			  workbook.write(fos);
			  fos.close();
			  workbook.close();
			
			
			 
		} catch (Exception e) {
			System.out.println("An unexpected error occurred: " + e.getMessage());
		} finally {
			// Clean up and close the browser
			if (driver != null) {
				driver.quit();
			}
		}
	}

	
}

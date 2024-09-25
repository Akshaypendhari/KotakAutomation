package Final;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DemoFinal {

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

            for (int i = 9; i <= 10; i++) {
                try {
                    WebElement year = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div[" + i + "]/div/div"));
                    if (year.isDisplayed()) {
                        int count = 0;
                        String nameofyear = year.getText();
                        year.click();
                        
                        System.out.println();
                        System.out.println("Currently we are clicking on " + nameofyear + "");
                        

                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

                        // Click on month dropdown
                        driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button")).click();
                        
                        

                        // Storing all month details
                        List<WebElement> months = driver.findElements(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li/a/span[1]"));

                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

                        // Iteration for the months
                        for (int j = 1; j <= months.size(); j++) {
                            try {
                                WebElement month = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/div/ul/li[" + j + "]/a/span[1]"));

                                String monthName = month.getText();
                                js.executeScript("arguments[0].click();", month);
                                //month.click();
                                
                                // Fetching data from table 


                              Thread.sleep(2000);
                              
                                
                                List<WebElement> allDataFromTable = driver.findElements(
                        				By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"));
                        		int rowcount = allDataFromTable.size();
                        		System.out.println("row count is "+rowcount+"");
                        		
                        		                        		
                        		
                        		// Call the API status for month code example
                        		ApiStatusCode.APIstatus(nameofyear, monthName, j);
                        	
                        		
                        		// storing each href
                        		for (int k = 1; k <= rowcount; k++) {
                        			try {
                        				WebElement pdflink = driver.findElement(
                            					By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr[" + k + "]/td[3]/a"));
                            			String href = pdflink.getAttribute("href");
                            			
                            			 status(href ,k);
    									
    								} catch (Exception e) {
    									// TODO: handle exception
    									System.out.println("Pdf not found");
    								}
                            		
                        			
                        			 
                        		}
                        		System.out.println();
                   			    System.out.println();
                                
                               

                                driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[1]/div/div/button")).click();

                            } catch (Exception e) {
                                System.out.println("An error occurred while processing month " + j + ": " + e.getMessage());
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
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            // Clean up and close the browser
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    public static void status (String hrefURL,int rowno) throws InterruptedException {
		String href =hrefURL;
        int  row = rowno;
			try {

				URL url = new URL(href);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				
				//System.out.println("Below URLS are not loading properly ");

				if (responseCode == 200)

				{

					System.out.println(""+row+" PDF is loading properly " + responseCode + "");

				}

				else

				{

					System.out.println("Not working "+url+": "+ responseCode + "");

				}

				
			} catch (IOException e) {

				e.printStackTrace();

			}
		}

	}


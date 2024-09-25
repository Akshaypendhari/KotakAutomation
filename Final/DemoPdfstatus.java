package Final;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DemoPdfstatus {
	public static void status1 () throws InterruptedException {
		// TODO Auto-generated method stub
		

		WebDriver driver = new ChromeDriver();

		

		//opt.setExperimentalOption("excludeSwitches", new String[] { "disable-popup-blocking" });

		
		


		// Store complete table data
		List<WebElement> allDataFromTable = driver.findElements(
				By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"));
		int rowcount = allDataFromTable.size();
		System.out.println("row count is "+rowcount+"");

		// storing each href
		for (int i = 1; i <= rowcount; i++) {
			WebElement pdflink = driver.findElement(
					By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[2]/table/tbody/tr[" + i + "]/td[3]/a"));
			String href = pdflink.getAttribute("href");
			//System.out.println(href);
			
			

			try {

				URL url = new URL(href);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				
				//System.out.println("Below URLS are not loading properly ");

				if (responseCode == 200)

				{

					System.out.println(""+i+" PDF is loading properly " + responseCode + "");

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

}

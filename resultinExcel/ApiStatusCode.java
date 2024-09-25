package resultinExcel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;

import java.net.HttpURLConnection;

import java.net.URL;

public class ApiStatusCode{

	public static void APIstatus(String year, String monthname,int month) {
		String str = year;
		int number = Integer.parseInt(str.trim());
		
		
		
              
			// driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

			
			

				String apiUrl = "https://www.kotak.com/content/kotakcl/en/investor-relations/governance/_jcr_content/icontent_par/tab_157670678_copy/sebilistingdisclosures1498038929815/disclosure.disclosurefilter."
						+ number+ "." + month + ".desc.esc.json";
				
				

				try {

					URL url = new URL(apiUrl);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setRequestMethod("GET");

					int responseCode = connection.getResponseCode();

					if (responseCode == 200)

					{
						//output = ("API for the " + monthname + " month are working fine");
						System.out.println("API for the " + monthname + " month are working fine");
					}

					else

					{
							 
						System.out.println(year +":"+" API not working for " + monthname + " we are getting  " + responseCode + " Error");
				
					}

					

				} catch (IOException e) {

					e.printStackTrace();
					System.out.println("Having issue in " + monthname +" API");


				}
				
			}
		}

	



package Final;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.opentelemetry.exporter.logging.SystemOutLogExporter;

public class SortingDropdownFinal {

    public static void main(String[] args) {
        // Set up WebDriver
        System.setProperty("webdriver.chrome.driver", "./Driver/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("disable-infobars");
        options.setExperimentalOption("excludeSwitches", new String[] { "disable-popup-blocking" });

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.kotak.com/en/investor-relations/governance/sebi-listing-disclosures.html");
            
            int year = 5;
            // Click on the year dropdown
            WebElement yearDropdown = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[1]/div/div/div[1]/div/div["+year+"]/div/div/span[1]"));
            yearDropdown.click();

            // Wait for the table to be visible
            waitForElement(driver, By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr/td[2]"), Duration.ofSeconds(3));

            // Store the table data
            Map<Integer, String> pdfTitles = new HashMap<>();
            List<WebElement> rows = driver.findElements(By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr"));
            for (int i = 0; i < rows.size(); i++) {
                WebElement cell = rows.get(i).findElement(By.xpath("./td[2]"));
                pdfTitles.put(i + 1, cell.getText());
            }

            // Scroll down and click on sort dropdown >> Oldest first
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 400)");

            WebElement sortDropdown = driver.findElement(By.xpath("//*[@id=\"sebi-listing\"]/div[2]/div/div/div/div[1]/div[2]/div/div/button/span[2]/span"));
            sortDropdown.click();

            // Wait and select "Oldest First"
            waitForElement(driver, By.xpath("(//div[@class=\"dropdown-menu open\"])[2]/ul/li/a/span[text()='Oldest First']"), Duration.ofSeconds(3));
            WebElement oldestFirstOption = driver.findElement(By.xpath("(//div[@class=\"dropdown-menu open\"])[2]/ul/li/a/span[text()='Oldest First']"));
            oldestFirstOption.click();

            // Wait for the sorting to be applied
            waitForElement(driver, By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr[1]/td[2]"), Duration.ofSeconds(5));

            // Get the first and last PDF titles
            String firstPDFTitle = cleanText(driver.findElement(By.xpath("//div[@class=\"table-data table-more document-data-disclosure\"]/table/tbody/tr[1]/td[2]")).getText());
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

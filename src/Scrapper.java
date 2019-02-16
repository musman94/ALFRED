import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Scrapper{
    // Properties
    ArrayList<Clue> clues;
    ArrayList<String> trace;
    Main main;

    // Constructor
    public Scrapper(ArrayList<Clue> clues, Main main)
    {
        this.clues = clues;
        this.main = main;
        trace = new ArrayList<>();
        //scrap();
    }

    // Methods
    // After running this method, every hint will contain the text of top 5 google hits
    @SuppressWarnings("all")
    public void scrap() {
        System.out.println("Lets Google The Hints!");
//        main.setTextForTrace("Lets Google The Hints!");
        File chrome = new File("src/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", chrome.getAbsolutePath());
        WebDriver driver = new ChromeDriver();		// Using Firefox
        WebDriver driver2 = new ChromeDriver();




        // Process each hint
        for(int i = 0; i < clues.size(); i++)
        {
            ArrayList<String> googlePages  = new ArrayList<String>();
            System.out.println("Googling Hint Number " + (i+1));
          //  main.setTextForTrace("Googling Hint Number " + (i+1));
          //  trace.add("Googling Hint Number " + (i+1));
            String[] googleResult = new String[3];	// Change this size to change the number of hits to be visited for each hint
            driver.get("http://www.google.com");
            WebElement element = driver.findElement(By.name("q"));
            CharSequence searchQuery = clues.get(i).getText() + " crossword clue\n";
            element.sendKeys(searchQuery);
            //element.submit();
            // wait until the google page shows the result
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));
            List<WebElement> searchResults = driver.findElements(By.xpath("//*[@id='rso']//h3/a"));

            // these are all the links you want to visit
            for (WebElement webElement : searchResults)
            {
                googlePages.add(webElement.getAttribute("href"));
            }
            // visit those links
            for (int j = 0; j < googleResult.length; j++)
            {
                // Might not have sufficient hits or unusable hits
                try
                {
                    driver2.get(googlePages.get(j));
                    (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                    WebElement text = driver2.findElement(By.tagName("body"));
                    if( text.getText().equals("Please Sign in")) {
                        driver2.findElement(By.xpath("/html/body/a")).click();
                        driver2.navigate().back();
                        text = driver2.findElement(By.tagName("body"));
                    }
                    googleResult[j] = text.getText();
                    System.out.println("Data from " + " Google Hit number " + (j+1) + " Retrieved!");
                   // main.setTextForTrace("Data from " + " Google Hit number " + (j+1) + " Retrieved!");
                   //trace.add("Data from " + " Google Hit number " + (j+1) + " Retrieved!");
                }
                catch(RuntimeException ex)
                {
                    System.out.println("Unable to retrieve data from Google Hit number " + (j+1));
                    //trace.add("Unable to retrieve data from Google Hit number " + (j+1));
                }
            }
            clues.get(i).setGoogleResult(googleResult);
            clues.get(i).cleanResults(i);
        }
    }
    public ArrayList<String> getTrace() {
        return trace;
    }
}

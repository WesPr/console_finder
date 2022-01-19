
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

public class PS5Driver {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver","/.../chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.mediamarkt.nl/nl/product/" +
                    "_sony-playstation-5-digital-extra-" +
                    "controller-accessoiresset-1705904.html");

            //accept cookies
            waitForElementTobeClickable("/html/body/div[9]/div/div[1]/div[2]/button", driver);

            Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                    .withTimeout(Duration.ofSeconds(60))
                    .pollingEvery(Duration.ofSeconds(5))
                    .ignoring(NoSuchElementException.class);

            WebElement clickStoreButton = wait.until(new Function<>() {
                public WebElement apply(WebDriver driver) {
                    return driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[3]/div[1]/div[3]/ul/li[2]/a"));
                }
            });
            clickStoreButton.click();

            for (int i = 5; i <= 8; i++) {
                int finalI = i;
                WebElement Location = wait.until(new Function<>() {
                    public WebElement apply(WebDriver driver) {
                        return driver.findElement(By.xpath("/html/body/" +
                                "div[13]/div[1]/div[2]/div[2]/div[2]/a[" + finalI + "]/h3"));
                    }
                });
                WebElement Stock = wait.until(new Function<>() {
                    public WebElement apply(WebDriver driver) {
                        return driver.findElement(By.xpath("/html/body/div[13]/div[1]/" +
                                "div[2]/div[2]/div[2]/a[" + finalI + "]/div[1]/span"));
                    }
                });
                System.out.println(Location.getText());
                if (!Objects.equals(Stock.getText(), "Niet op voorraad")) {
                    System.out.println("Stock found at: " + Location.getText());
                    sendMessage(Location.getText());
                    driver.close();
                } else {
                    System.out.println(Stock.getText());
                }
            }
            driver.navigate().refresh();
            Thread.sleep(60000*5);

            //execute until stock is found
            boolean stockNotFound = true;
            while(stockNotFound){

                WebElement clickStoreButton1 = wait.until(new Function<>() {
                    public WebElement apply(WebDriver driver) {
                        return driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[3]/div[1]/div[3]/ul/li[2]/a"));
                    }
                });
                clickStoreButton1.click();

                //loop through stores
                for (int i = 5; i <= 8; i++) {
                    int finalI = i;
                    WebElement Location = wait.until(new Function<>() {
                        public WebElement apply(WebDriver driver) {
                            return driver.findElement(By.xpath("/html/body/" +
                                    "div[17]/div[1]/div[2]/div[2]/div[2]/a[" + finalI + "]/h3"));
                        }
                    });
                    WebElement Stock = wait.until(new Function<>() {
                        public WebElement apply(WebDriver driver) {
                            return driver.findElement(By.xpath("/html/body/div[17]/div[1]/" +
                                    "div[2]/div[2]/div[2]/a[" + finalI + "]/div[1]/span"));
                        }
                    });
                    System.out.println(Location.getText());
                    if (!Objects.equals(Stock.getText(), "Niet op voorraad")) {
                        System.out.println("Stock found at: " + Location.getText());
                        sendMessage(Location.getText());
                        stockNotFound = false;
                    } else {
                        System.out.println(Stock.getText());
                    }
                }
                driver.navigate().refresh();
                Thread.sleep(60000*5);
            }
        }
        catch(Exception e){
            System.out.print("Took too much time");
            driver.close();
        }
    }

    public static void waitForElementTobeClickable(String s, WebDriver driver){
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath(s)));
        element.click();
    }
    public static void sendMessage(String locationStock) {
        Twilio.init("...", "...");
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("..."),
                        new com.twilio.type.PhoneNumber("..."),
                        "PS5 Stock found at: "+ locationStock)
                .create();
    }
}

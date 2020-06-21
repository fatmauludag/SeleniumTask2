package com.testinium.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

public class PageTester {

    private static final String baseUrl = "https://www.trendyol.com";
    private static final String driverPath = "C:\\chromedriver.exe";
    private static final String testUser = "task.mailim.2020@gmail.com";
    private static final String testPassword = "tasksifre2020";
    private static final String mainPageTitle = "En Trend Ürünler Türkiye'nin Online Alışveriş Sitesi Trendyol'da";
    private static final String loginAfterPageTitle = "Kadın, Moda, Giyim, Stil, Giyim Markaları | Trendyol";
    private static final String searchResultTitle = "Tüm Ürünler - Trendyol";
    private static final String cartTitle = "Sepetim - Trendyol";
    private static final String searchKeyword = "bilgisayar";
    private static final ExpectedCondition<Boolean> documentReady = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    private static WebDriverWait wait;
    private static WebDriver driver;

    static {
        Setup();
    }

    @Before
    public static void Setup() {
        System.setProperty("webdriver.chrome.driver", driverPath);

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, 15);
    }

    @Test
    public static void ExecuteAll() throws InterruptedException {
        CheckMainPage();

        DoLogin();

        CheckSearching(searchKeyword);

        CheckPaging();

        // Generate a random in between 0-28
        int randomProductIndex = new Random().nextInt(20);

        String detailPagePrice = ChooseProduct(randomProductIndex);
        String cartPrice = AddAndNavigateToCart();

        // All prices should be the same
        Assert.assertEquals(detailPagePrice, cartPrice);

        CheckCartQuantity();

        RemoveFromCart();

        CheckIfCartEmpty();

    }

    public static void CheckMainPage() throws InterruptedException {
        // Navigate to main page
        driver.get(baseUrl);
        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);
        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), mainPageTitle);

        // Checks and closes all open popups
        CloseAllPopups();
    }

    public static void DoLogin() throws InterruptedException {
        // click to login
        driver.findElement(By.id("accountBtn")).click();
        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Fill credential fields
        driver.findElement(By.id("email")).sendKeys(testUser);
        Thread.sleep(100);
        driver.findElement(By.id("password")).sendKeys(testPassword);
        Thread.sleep(100);

        driver.findElement(By.id("loginSubmit")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);
        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), loginAfterPageTitle);

        // Checks and closes all open popups
        CloseAllPopups();
    }

    public static void CheckSearching(String keyword) throws InterruptedException {
        // Fill search field with search keyword
        driver.findElement(By.cssSelector(".search-box-container input.search-box")).sendKeys(keyword);

        Thread.sleep(1000);

        // Checks and closes all open popups
        CloseAllPopups();
        // Click search button
        driver.findElement(By.cssSelector(".search-box-container .search-icon")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), searchResultTitle);

        // Checks and closes all open popups
        CloseAllPopups();
    }

    public static void CheckPaging() throws InterruptedException {
        //scroll page
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);
    }

    private static String ChooseProduct(int randomProductIndex) throws InterruptedException {
        WebElement productElement = driver.findElement(By.cssSelector("#search-app div.srch-prdcts-cntnr div.p-card-wrppr:nth-child(" + randomProductIndex + ")"));
        // Get listing price before navigate to details
        String preSelectionPrice = productElement.findElement(By.cssSelector(".prc-cntnr .prc-box-sllng")).getText();
        // Navigate to details
        productElement.findElement(By.cssSelector(".p-card-chldrn-cntnr a")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Checks and closes all open popups
        CloseAllPopups();

        // Get price on details page
        String preCartPrice = driver.findElement(By.cssSelector("#product-detail-app div.pr-cn div.pr-cn-in span.prc-slg")).getText();

        // All prices should be the same
        Assert.assertEquals(preSelectionPrice, preCartPrice);

        return preCartPrice;
    }

    public static String AddAndNavigateToCart() throws InterruptedException {
        CloseAllPopups();
        // Click to "add to cart"
        driver.findElement(By.cssSelector("#product-detail-app button.add-to-bs")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Checks and closes all open popups
        CloseAllPopups();

        // Navigate to cart
        driver.findElement(By.cssSelector("#myBasketListItem a")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), cartTitle);

        // Checks and closes all open popups
        CloseAllPopups();

        // Get price on cart page
        return driver.findElement(By.cssSelector("#basketAside .shoppingReview .total-price")).getText();
    }

    public static void CheckCartQuantity() throws InterruptedException {
        Thread.sleep(3000);

        String productQuantity;
        try {
            // Increment the quantity of the product.
            driver.findElement(By.cssSelector("#partial-basket .ty-numeric-counter-button:last-of-type")).click();

            // Wait until page load
            Thread.sleep(4000);

            // Get new quantity of the product.
             productQuantity = driver.findElement(By.cssSelector("#partial-basket input.counter-content")).getAttribute("value");
        }
        catch(Exception e) {
            WebElement selectElement = driver.findElement(By.cssSelector("#basketContent select.basketItemQuantity"));
            Select selectObject = new Select(selectElement);
            selectObject.selectByVisibleText("2");

            // Wait until page load
            Thread.sleep(4000);

            // Get new quantity of the product.
            productQuantity= selectObject.getFirstSelectedOption().getAttribute("value");
        }

        // Check if quantities matching
        Assert.assertEquals(productQuantity, "2");
    }

    public static void RemoveFromCart() throws InterruptedException {
        Thread.sleep(3000);
        // Click on clear cart button
        driver.findElement(By.cssSelector("#partial-basket div.pb-basket-item div.pb-basket-item-actions > button")).click();
        Thread.sleep(3000);

        // Click confirm button on modal
        driver.findElement(By.cssSelector("#ngdialog1 > div.ngdialog-content button.btn-remove")).click();
        Thread.sleep(3000);
    }

    public static void CheckIfCartEmpty() {
        try {
            // Get empty cart element if exists
            String emptyCartText = driver.findElement(By.cssSelector("#basketNoProductPage span")).getText();
            // Check if cart is empty or not
            Assert.assertNotNull(emptyCartText);
        } catch (NoSuchElementException e) {
            // Alternative to login problem, when anonymous cart is buggy
            String cartTotal = driver.findElement(By.cssSelector("div.checkoutContainer div.total > span.price")).getText();
            Assert.assertEquals("0,00 TL", cartTotal);
        }
    }

    public static void CloseAllPopups() throws InterruptedException {
        CloseDiscountPopup();
        CloseAdPopups();
        // CloseCookiePopup();
    }

    // Check if advertisements modals shows up
    public static void CloseAdPopups() throws InterruptedException {
        try {
            By selector = By.cssSelector(".fancybox-wrap a[title=\"Close\"]");
            WebElement adModalEl = driver.findElement(selector);
            adModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    }

    // Check if discount modal shows up
    public static void CloseDiscountPopup() throws InterruptedException {
        try {
            By selector = By.cssSelector(".modal-close");
            WebElement adModalEl = driver.findElement(selector);
            adModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    }

    // Check if KVKK Modal shows up
    /* public static void CloseKVKKModal() throws InterruptedException {
        try {
            By selector = By.cssSelector("#userKvkkModal > div > div.btnHolder > span");
            WebElement kvkkModalEl = driver.findElement(selector);
            kvkkModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    } */

    // Check if cookie popup shows up
    /* public static void CloseCookiePopup() throws InterruptedException {
        try {
            By selector = By.cssSelector("#cookieUsagePopIn > span");
            WebElement cookieModalEl = driver.findElement(selector);
            cookieModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    } */

    @After
    public static void Cancel() {
        driver.close();
        // driver.quit();
    }
}
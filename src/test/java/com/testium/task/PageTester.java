package com.testium.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

public class PageTester {

    private static final String baseUrl = "https://www.n11.com";
    private static final String driverPath = "C:\\chromedriver.exe";
    private static final String testUser = "task.mailim.2020@gmail.com";
    private static final String testPassword = "tasksifre2020";
    private static final String loginUrl = "/giris-yap";
    private static final String searchKeyword = "bilgisayar";
    private static final String mainPageTitle = "n11.com - Alışverişin Uğurlu Adresi";
    private static final String loginPageTitle = "Giriş Yap - n11.com";
    private static final String searchResultTitle = "Bilgisayar - n11.com";
    private static final String searchResultTitle2 = "Bilgisayar - n11.com - 2/50";
    private static final String cartTitle = "Sepetim - n11.com";
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

        CheckPaging(2);

        // Generate a random in between 0-28
        int randomProductIndex = new Random().nextInt(28);

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
        // Navigate to login
        driver.get(baseUrl + loginUrl);
        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);
        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), loginPageTitle);

        // Fill credential fields
        driver.findElement(By.id("email")).sendKeys(testUser);
        Thread.sleep(100);
        driver.findElement(By.id("password")).sendKeys(testPassword);
        Thread.sleep(100);

        // Login is disabled due to some kind of security problems.Instead we navigate to main page.
        // Checks and closes all open popups
        // CloseAllPopups();
        // driver.findElement(By.id("loginButton")).click();

        driver.get(baseUrl);
        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);
        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), mainPageTitle);

    }

    public static void CheckSearching(String keyword) throws InterruptedException {
        // Fill search field with search keyword
        driver.findElement(By.id("searchData")).sendKeys(keyword);

        Thread.sleep(1000);

        // Checks and closes all open popups
        CloseAllPopups();
        // Click search button
        driver.findElement(By.cssSelector("div.searchBox.withLocalization > a")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), searchResultTitle);

        // Checks and closes all open popups
        CloseAllPopups();
    }

    public static void CheckPaging(int pageIndex) throws InterruptedException {
        // Selector for second page button
        By pageSelector = By.cssSelector("div.pagination > a:nth-child(" + pageIndex + ")");
        // Wait until button clickable.
        wait.until(ExpectedConditions.elementToBeClickable(pageSelector));
        // Click pagination button
        driver.findElement(pageSelector).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), searchResultTitle2);

        // Checks and closes all open popups
        CloseAllPopups();
    }

    private static String ChooseProduct(int randomProductIndex) throws InterruptedException {
        WebElement productElement = driver.findElement(By.cssSelector("#view > ul > li:nth-child(" + randomProductIndex + ") > div.columnContent"));
        // Get listing price before navigate to details
        String preSelectionPrice = productElement.findElement(By.cssSelector("div.proDetail > a:last-of-type > ins")).getText();
        // Navigate to details
        productElement.findElement(By.cssSelector("div.pro > a")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Checks and closes all open popups
        CloseAllPopups();

        // Get price on details page
        String preCartPrice = driver.findElement(By.cssSelector("div.priceDetail > div > ins")).getText();

        // All prices should be the same
        Assert.assertEquals(preSelectionPrice, preCartPrice);

        return preCartPrice;
    }

    public static String AddAndNavigateToCart() throws InterruptedException {
        CloseAllPopups();
        // Click to "add to cart"
        driver.findElement(By.cssSelector(".unf-p-detail a[title=\"Sepete Ekle\"],.paymentDetail a[title=\"Sepete Ekle\"]")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Checks and closes all open popups
        CloseAllPopups();

        // Navigate to cart
        driver.findElement(By.cssSelector("div.myBasketHolder > a")).click();

        // Wait until page load
        Thread.sleep(4000);
        wait.until(documentReady);

        // Check if titles are matching
        Assert.assertEquals(driver.getTitle(), cartTitle);

        // Checks and closes all open popups
        CloseAllPopups();

        // Get price on cart page
        return driver.findElement(By.cssSelector("div.priceTag span:last-of-type")).getText();
    }

    public static void CheckCartQuantity() throws InterruptedException {
        Thread.sleep(3000);
        // Increment the quantity of the product.
        driver.findElement(By.cssSelector("div.spinnerField span.spinnerUp.spinnerArrow")).click();

        // Wait until page load
        Thread.sleep(4000);

        // Get new quantity of the product.
        String productQuantity = driver.findElement(By.cssSelector("div.spinnerField > input")).getAttribute("value");

        // Check if quantities matching
        Assert.assertEquals(productQuantity, "2");

    }

    public static void RemoveFromCart() throws InterruptedException {
        Thread.sleep(3000);
        // Click on clear cart button
        driver.findElement(By.cssSelector("div.prodAction span.removeProd")).click();

        Thread.sleep(3000);
    }

    public static void CheckIfCartEmpty() {
        try {
            // Get empty cart element if exists
            String emptyCartText = driver.findElement(By.cssSelector("div.checkoutContainer.emptyCartContainer div.cartEmptyText > h2")).getText();
            // Check if cart is empty or not
            Assert.assertNotNull(emptyCartText);
        } catch (NoSuchElementException e) {
            // Alternative to login problem, when anonymous cart is buggy
            String cartTotal = driver.findElement(By.cssSelector("div.checkoutContainer div.total > span.price")).getText();
            Assert.assertEquals("0,00 TL", cartTotal);
        }
    }

    public static void CloseAllPopups() throws InterruptedException {
        CloseKVKKModal();
        CloseAdPopups();
        CloseCookiePopup();
    }

    // Check if advertisements modals shows up
    public static void CloseAdPopups() throws InterruptedException {
        try {
            By selector = By.cssSelector(".seg-popup-close");
            WebElement adModalEl = driver.findElement(selector);
            adModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    }

    // Check if KVKK Modal shows up
    public static void CloseKVKKModal() throws InterruptedException {
        try {
            By selector = By.cssSelector("#userKvkkModal > div > div.btnHolder > span");
            WebElement kvkkModalEl = driver.findElement(selector);
            kvkkModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    }

    // Check if cookie popup shows up
    public static void CloseCookiePopup() throws InterruptedException {
        try {
            By selector = By.cssSelector("#cookieUsagePopIn > span");
            WebElement cookieModalEl = driver.findElement(selector);
            cookieModalEl.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {
        }
    }

    @After
    public static void Cancel() {
        driver.close();
        // driver.quit();
    }
}
package QKART_TESTNG.testcases;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes.Name;
import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.Message;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;
    

     @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    //"testUser", "abc@123"
         
         @Test(groups={"Sanity_test"},description = "Verify registration happens correctly", priority = 1)
         @Parameters({"Username","Password"})
         public void TestCase01(String Username, String Password) throws InterruptedException {
        Boolean status;
        //  logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
         //takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, Password);
         //logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        //  logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
        //  ? "PASS" : "FAIL");
        //  takeScreenshot(driver, "EndTestCase", "TestCase1");
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    @Test(groups={"Sanity_test"},description = "Verify re-registering an already registered user fails", priority = 2)
    @Parameters({"Username","Password"})
    public void TestCase02(String Username, String Password) throws InterruptedException {
        Boolean status;
        // logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username,Password, true);
        Assert.assertTrue("Unable to register", status);
        // logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        
        // if (!status) {
        //     logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "PASS" : "FAIL");
        //     return false;

        // }

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, Password,false);
        Assert.assertFalse("Re-registration successful",status);
        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "FAIL" : "PASS");
        //return !status;
    }

    /*
     * Verify the functinality of the search text box
     */
    //"YONEX"  "Gesundheit"
    @Test(groups={"Sanity_test"},description = "Verify the functionality of search text box", priority = 3)
    @Parameters({"product1","product2"})
    public void TestCase03(String product1,String product2) throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(product1);
        SoftAssert sa=new SoftAssert();
        sa.assertTrue(status, "Unable to search for given product");
        // if (!status) {
        //     logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");
        //     return false;
        // }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        // if (searchResults.size() == 0) {
        //     logStatus("TestCase 3", "Test Case Failure. There were no results for the given search string", "FAIL");
        //     return false;
        // }
        sa.assertTrue(searchResults.size() > 0, "No results found for the given search string");

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            // if (!elementText.toUpperCase().contains("YONEX")) {
            //     logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected values: " + elementText,
            //             "FAIL");
            //     return false;
            // }
            sa.assertTrue(elementText.toUpperCase().contains(product1),
            "Test Results contain unexpected value: " + elementText);
        }

        // logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct(product2);
        // if (status) {
        //     logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        //     return false;
        // }
        sa.assertFalse(status, "Invalid keyword returned results");

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        // // if (searchResults.size() == 0) {
        // //     if (homePage.isNoResultFound()) {
        // //         logStatus("Step Success", "Successfully validated that no products found message is displayed", "PASS");
        // //     }
        // //     logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for the given text",
        // //             "PASS");
        // } else {
        //     logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual: Results were available", "FAIL");
        //     return false;
        // }
        sa.assertEquals(searchResults.size(), 0, "Expected: no results, Actual: Results were available");
        sa.assertAll();
        //return true;
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as
     * expected
     */
    //"Running Shoes"
    @Test(groups={"Regression_Test"},description = "Verify the existence of size chart for certain items and validate contents of size chart", priority = 4)
    @Parameters("shoes")
    public void TestCase04(String shoes) throws InterruptedException {
        //takeScreenshot(driver, "StartTestCase", "TestCase04");
        // logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // SLEEP_STMT_03 : Wait for page to load
        //Thread.sleep(5000);

        // Search for product and get card content element of search results
        status = homePage.searchForProduct(shoes);
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        SoftAssert sa=new SoftAssert();
        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            // if (result.verifySizeChartExists()) {
            //     logStatus("Step Success", "Successfully validated presence of Size Chart Link", "PASS");
           
            sa.assertTrue(result.verifySizeChartExists(), "size chart link validation failded");
                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                //logStatus("Step Success", "Validated presence of drop down", status ? "PASS" : "FAIL");
                sa.assertTrue(status, "drop down validation failed");
                // Open the size chart
                //if (result.openSizechart()) {
                Assert.assertTrue("Unable to open size chart",result.openSizechart());
                    // Verify if the size chart contents matches the expected values
                    // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver)) {
                    //     logStatus("Step Success", "Successfully validated contents of Size Chart Link", "PASS");
                    // } else {
                    //     logStatus("Step Failure", "Failure while validating contents of Size Chart Link", "FAIL");
                    //     takeScreenshot(driver, "Failed", "TestCase04");
                    // }
                    sa.assertTrue(result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver),
                  "Validation of size chart contents failed");


                    // Close the size chart modal
                    status = result.closeSizeChart(driver);
                    sa.assertTrue(status, "Failure to close Size Chart");


                // } else {
                //     logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart", "FAIL");
                //     takeScreenshot(driver, "Failed", "TestCase04");
                //     return false;
                // }

            // } else {
            //     logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist", "FAIL");
            //     takeScreenshot(driver, "Failed", "TestCase04");
            //     return false;
            // }
            
        }
        sa.assertAll();
        
        //logStatus("TestCase 4", "Test Case PASS. Validated Size Chart Details", "PASS");
        //takeScreenshot(driver, "EndTestCase", "TestCase04");
        //return status;
    }
    //YONEX--"YONEX Smash Badminton Racquet"
    //Tan--"Tan Leatherette Weekender Duffle"
    //Address--"Addr line 1 addr Line 2 addr line 3"
    @Test(groups={"Sanity_test"},description = "Verify that a new user can add multiple products in to the cart and Checkout", priority = 5)
    @Parameters({"Username","Password","YONEX","Tan","Address"})
    public void TestCase05(String Username, String Password,String YONEX,String Tan,String Address) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser(Username,Password , true);
        Assert.assertTrue("Unable to register", status);
        // if (!status) {
        //     logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName,Password);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" : "FAIL");
        // }
        Assert.assertTrue("User Perform Login Failed", status);

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart(YONEX);
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart(Tan);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Address);
        checkoutPage.selectAddress(Address);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue("redirecting to thanks page Validation failed", status);
        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");
        //return status;
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(groups={"Regression_Test"},description = "Verify that the contents of the cart can be edited", priority = 6)
    @Parameters({"Username","Password","Xtend","Yarine","Address"})
    public void TestCase06(String Username, String Password,String Xtend, String Yarine,String Address) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        Assert.assertTrue("Unable to register", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Register Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 6:  Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        //     return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, Password);
        Assert.assertTrue("User Perform Login Failed", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 6:  Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        //     return false;
        // }
        SoftAssert sa=new SoftAssert();
        homePage.navigateToHome();
        status = homePage.searchForProduct(Xtend);
        sa.assertTrue(status, "Xtend product search unsuccessful");
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct(Yarine);
        sa.assertTrue(status, "Yarine product search unsuccessful");
        homePage.addProductToCart(Yarine);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(Xtend, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(Yarine, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(Xtend , 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Address);
        checkoutPage.selectAddress(Address);

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
           // return false;
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue("redirecting to thanks page Validation failed", status);

        homePage.navigateToHome();
        homePage.PerformLogout();
        sa.assertAll();
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        //return status;
    }
  //Stylecon--"Stylecon 9 Seater RHS Sofa Set "
  //Qty--10
  @Test(groups={"Sanity_test"},description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 7)
    @Parameters({"Username","Password","Stylecon","Qty","Address"})
    public void TestCase07(String Username, String Password,String Stylecon,int Qty,String Address) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase",
        //         "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
        //         "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        Assert.assertTrue("Unable to register", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase",
        //             "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //             status ? "PASS" : "FAIL");
        //     return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, Password);
        Assert.assertTrue("User Perform Login Failed", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase",
        //             "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //             status ? "PASS" : "FAIL");
        //     return false;
        // }
        SoftAssert sa=new SoftAssert();
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        sa.assertTrue(status, "Stylecon search unsuccessful");
        homePage.addProductToCart(Stylecon);

        homePage.changeProductQuantityinCart(Stylecon, Qty);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Address);
        checkoutPage.selectAddress(Address);

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        sa.assertTrue(status, "Insufficient balance validation failded");

        // logStatus("End TestCase",
        //         "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //         status ? "PASS" : "FAIL");
        sa.assertAll();
        //return status;
    }
    @Test(groups={"Regression_Test"},description = "Verify that a product added to a cart is available when a new tab is added", priority = 8)
    @Parameters({"Username","Password","YONEX"})
    public void TestCase08(String Username, String Password, String YONEX) throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        //         "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        //         "DONE");
        //takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        Assert.assertTrue("Unable to register", status);
        // if (!status) {
        //     logStatus("TestCase 8",
        //             "Test Case Failure. Verify that product added to cart is available when a new tab is opened",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, Password);
        Assert.assertTrue("User Perform Login Failed", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 8:   Verify that product added to cart is available when a new tab is opened",
        //             status ? "PASS" : "FAIL");
        // }
        SoftAssert sa=new SoftAssert();
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        sa.assertTrue(status,"Unable to search YONEX");
        homePage.addProductToCart(YONEX);

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList(YONEX);
        status = homePage.verifyCartContents(expectedResult);
        sa.assertTrue(status, "Unable to verify cart contents");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        //takeScreenshot(driver, "EndTestCase", "TestCase08");
        sa.assertAll();
        //return status;
    }
    @Test(groups={"Regression_Test"},description = "Verify that privacy policy and about us links are working fine", priority = 9)
    @Parameters({"Username","Password"})
    public void TestCase09(String Username,String Password) throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        //         "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
        //         "DONE");
        //takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        Assert.assertTrue("Unable to Register", status);
        
        // if (!status) {
        //     logStatus("TestCase 09",
        //             "Test Case Failure.  Verify that the Privacy Policy, About Us are displayed correctly ",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, Password);
        Assert.assertTrue("User Perform Login Failed", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        //     logStatus("End TestCase",
        //             "Test Case 9:    Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }
        SoftAssert sa=new SoftAssert();
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        sa.assertTrue(status, "Verifying parent page url didn't change on privacy policy link click failed");

        // if (!status) {
        //     logStatus("Step Failure", "Verifying parent page url didn't change on privacy policy link click failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        // if (!status) {
        //     logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }
        sa.assertTrue(status,"Verifying new tab opened has Privacy Policy page heading failed");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        sa.assertTrue(status,"Verifying new tab opened has Terms Of Service page heading failed");
        // if (!status) {
        //     logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "PASS");
        //takeScreenshot(driver, "EndTestCase", "TestCase9");
        sa.assertAll();
        //return status;
    }
    //Name--"crio user"    Email--"criouser@gmail.com"  Message--"Testing the contact us page"

    @Test(groups={"Regression_Test"},description = "Verify that the contact us dialog works fine", priority = 10)
    @Parameters({"Name","Email","Message"})
    public void TestCase10(String Name,String Email,String Message) throws InterruptedException {
        // logStatus("Start TestCase",
        //         "Test Case 10: Verify that contact us option is working correctly ",
        //         "DONE");
        //takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(Name);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(Email);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(Message);

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        //         "Test Case 10: Verify that contact us option is working correctly ",
        //         "PASS");

        //takeScreenshot(driver, "EndTestCase", "TestCase10");

        //return true;
        Assert.assertTrue(true);
    }
    @Test(groups={"Sanity_test","Regression_Test"},description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 11)
    @Parameters({"Username","Password","YONEX","Address"})
    public void TestCase11(String Username,String Password,String YONEX, String Address) throws InterruptedException {
        Boolean status = false;
        // logStatus("Start TestCase",
        //         "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
        //         "DONE");
        //takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        Assert.assertTrue("Unable to register", status);
        // if (!status) {
        //     logStatus("TestCase 11",
        //             "Test Case Failure. Ensure that the links on the QKART advertisement are clickable",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase11");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, Password);
        Assert.assertTrue("User Perform Login Failed", status);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase 11");
        //     logStatus("End TestCase",
        //             "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
        //             status ? "PASS" : "FAIL");
        // }
     
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(YONEX);
        Assert.assertTrue("Unable to search YONEX Smash Badminton Racquet", status);
        homePage.addProductToCart(YONEX);
        homePage.changeProductQuantityinCart(YONEX, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Address);
        checkoutPage.selectAddress(Address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        Assert.assertTrue("3 Advirtisments are not available", status);
        //logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" : "FAIL");
        SoftAssert sa=new SoftAssert();
        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL); 
        sa.assertTrue(status, "Advertisement 1 is not clickable");
        //logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" : "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        sa.assertTrue(status,"Advertisement 2 is not clickable");
        //logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" : "FAIL");

        // logStatus("End TestCase",
        //         "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
        //         status ? "PASS" : "FAIL");
        //return status;
        sa.assertAll();
    }

    @AfterSuite(alwaysRun = true)
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public static void takeScreenshot(String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
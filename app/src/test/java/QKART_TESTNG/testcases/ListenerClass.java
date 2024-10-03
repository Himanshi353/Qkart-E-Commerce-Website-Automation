package QKART_TESTNG.testcases;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener {
   
    public void onTestStart(ITestResult result) {
        System.out.println("onTestStart >>" +result.getName()+" >> taking screenshot...");
        QKART_Tests.takeScreenshot("START","TestCase started");
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("onTestFailure >>" +result.getName()+" >> taking screenshot...");
        QKART_Tests.takeScreenshot("FAILURE", "TestCaseFailed");
    }
    
    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess >>" +result.getName()+" >> taking screenshot...");
        QKART_Tests.takeScreenshot("SUCCESS", "TestCaseSuccess");
    }

    


}
package webappautomation;

import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class GenerateReports 
{
	 public static WebDriver driver;
	 public static  ExtentHtmlReporter htmlReporter;
	 public static ExtentReports extent;
	 public static ExtentTest test;

	 @BeforeSuite
	 public void setExtent() {
	  // specify location of the report
	  htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/myReport.html");

	  htmlReporter.config().setDocumentTitle("Automation Report"); // Title of report
	  htmlReporter.config().setReportName("Test Summary Report"); // Name of the report
	  htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
	  htmlReporter.config().setTheme(Theme.DARK); //Theme of the report
	  
	  extent = new ExtentReports();
	  extent.attachReporter(htmlReporter);
	  
	  // Passing General information
	  extent.setSystemInfo("Java Version", "8 Update 221");
	  extent.setSystemInfo("Selenium Version", "3.141.59");
	  extent.setSystemInfo("TestNG Vesion", "6.9.10");
	  extent.setSystemInfo("Chrome Driver Version", "77.0");
	  extent.setSystemInfo("Automation Tester", "Vasant Verma");
	 }
	 
     @BeforeMethod()
	 public void setup(Method testMethod) 
	 {
	  test=extent.createTest(testMethod.getName());
	 }
    
	 @AfterMethod
	 public void tearDown(ITestResult result) 
	 {
		 if(result.getStatus() == ITestResult.FAILURE)
	        {
	            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" Test case FAILED due to below issues:", ExtentColor.RED));
	            test.fail(result.getThrowable());
	        }
	        else if(result.getStatus() == ITestResult.SUCCESS)
	        {
	            test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
	        }
	        else
	        {
	            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" Test Case SKIPPED", ExtentColor.ORANGE));
	            test.skip(result.getThrowable());
	        }
	  
	 }
	 
	 @AfterSuite
	 public void endReport() {
	  extent.flush();
	 }

}

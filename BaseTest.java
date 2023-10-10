package com.slb.framework;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.slb.driver.TestConfig;
import com.slb.driver.TestDriver;
import com.slb.utils.ExcelObject;
import com.slb.utils.UIUtils;

public class BaseTest {
	public static LogMe LOGGER;
	public static WebDriver driver;
	public static ExtentTest extentTest;
	public static String PAGENAME;
	public static String OBJECTNAME;
	public static SoftAssert sAssert;
	public static AssertManager assertManager = AssertManager.getInstance();
	public static String recoringFlag = "";
	public static String PARAENT_REPORT_FOLDER_PATH;
	public static String REPORT_FOLDER_PATH;
	public static String JSON_FOLDER_PATH;
	public static String SCREENSHOT_FOLDER_PATH;
	public static String VIDEO_FOLDER_PATH;
	private File videoDirectory = null;
	private File parentDirectory = null;
	private File reportDirectory = null;
	private File screenshotDirectory = null;
	

	@BeforeSuite
	public void suiteSetup() {
		try {
			TestConfig.getInstance().suiteSetup();
			String currDate = new SimpleDateFormat("yyyy-MMM-dd").format(new Date());
			PARAENT_REPORT_FOLDER_PATH =System.getProperty("user.dir") + File.separator+ "Framework/Test_Reports/"+TestConfig.getModuleName()+"_UI/"+TestConfig.getModuleName()+"_UI_"+currDate;
			parentDirectory = new File(PARAENT_REPORT_FOLDER_PATH+"/temp.txt");
			String currDateTimestamp = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date()).replace(" ", "-").replaceAll(":", "-");
			REPORT_FOLDER_PATH = PARAENT_REPORT_FOLDER_PATH+"/"+currDateTimestamp;
			reportDirectory = new File(REPORT_FOLDER_PATH+"/temp.txt");
			JSON_FOLDER_PATH = System.getProperty("user.dir")+ File.separator+"JSONData"+File.separator+TestConfig.getModuleName()+ File.separator;
			SCREENSHOT_FOLDER_PATH = REPORT_FOLDER_PATH+"/Screenshots";
			screenshotDirectory = new File(SCREENSHOT_FOLDER_PATH+"/temp.txt");
			VIDEO_FOLDER_PATH = REPORT_FOLDER_PATH+"/Videos";
			
			videoDirectory = new File(System.getProperty("user.dir") + File.separator+"Framework/Test_Reports/Videos/temp.txt");
						
			if (! videoDirectory.getParentFile().exists()){
				videoDirectory.getParentFile().mkdirs();
				videoDirectory.createNewFile();
		    }	
			if (! parentDirectory.getParentFile().exists()){
				parentDirectory.getParentFile().mkdirs();
				parentDirectory.createNewFile();
		    }
			
			if (! reportDirectory.getParentFile().exists()){
				reportDirectory.getParentFile().mkdirs();
				reportDirectory.createNewFile();
		    }
			
			if (! screenshotDirectory.getParentFile().exists()){
				screenshotDirectory.getParentFile().mkdirs();
				screenshotDirectory.createNewFile();
		    }
			
			//ExtentManager.createInstance("Framework/Test_Reports/AUTOMATION_Test-Reports"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replace(" ", "-").replaceAll(":", "-")+".html");
			ExtentManager.createInstance(REPORT_FOLDER_PATH+"/AUTOMATION_Test-Reports"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replace(" ", "-").replaceAll(":", "-")+".html");
			LOGGER = new LogMe(BaseTest.class);
			LOGGER.logInfo("*********EXECUTION STARTED**********\n\n");
		} catch (Exception e) {
			LOGGER.logError("Exception " + e.getClass().getName() + " caught from suite setup method");
		}
}
	
	@BeforeMethod
	public void startReporting(Method method) throws Exception {
		System.out.println("Started @BeforeMethod");
		driver = TestDriver.driverInstantiation(TestConfig.getConfig().getPropertyValue("Browser").toUpperCase());
//		extentTest = LOGGER.logBeginTestCase(method.getName());
		LogMe.test_name=method.getName();
		sAssert = new SoftAssert();	
		extentTest = LOGGER.logBeginTestCase("TC_" + ExcelObject.testCase_Id.get(method.getName()) +"_" + LogMe.test_name);
		
		recoringFlag = TestConfig.getConfig().getPropertyValue("recoringFlag").toUpperCase();
		if(recoringFlag.equalsIgnoreCase("true")) {
			try {
				MyScreenRecorder.startRecording(method.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		LogMe.getLogger().info("Base URL is " + TestConfig.getInstance().getAppBaseURL());
		driver.get(TestConfig.getInstance().getAppBaseURL());
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(120));
		UIUtils.waitForPageLoad(driver);
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_0);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e) {

		}
		if (!TestConfig.getInstance().getDeviceResolution().equals("Full Screen")) {
			String[] dim = TestConfig.getInstance().getDeviceResolution().split(",");
			int dimX = Integer.parseInt(dim[0]);
			int dimY = Integer.parseInt(dim[1]);
			Dimension d = new Dimension(dimX, dimY);
			driver.manage().window().setSize(d);
		} else {
			driver.manage().window().maximize();
		}
		 	
	}
	
	@AfterMethod
	public void testResult(Method method, ITestResult result) throws IOException {
		switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			LOGGER.logWithScreenshot("PASS", "Test Case " + method.getName() + " is passed", driver);
			break;
		case ITestResult.FAILURE:
			LOGGER.logTestStep(extentTest, "FAIL", "Test Case " + method.getName() + " failed");
			break;
		case ITestResult.SKIP:
			LOGGER.logWithScreenshot("skip", "Test Case " + method.getName() + "  skiped", driver);
			break;
		default:
			break;
		}
		LOGGER.logEndTestCase(method.getName(), extentTest);
		
		TestCaseUpdate testCaseUpdate=new TestCaseUpdate();
		int testCaseID = Integer.parseInt(ExcelObject.testCase_Id.get(method.getName()));
		int testSuiteID = Integer.parseInt(ExcelObject.testSuite_Id.get(method.getName()));
		testCaseUpdate.azureTestcaseUpdate(testCaseID,testSuiteID,(result.getStatus()==1?true:false));
		if(recoringFlag.equalsIgnoreCase("true")) {
			try {
				MyScreenRecorder.stopRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		driver.quit();
	}

	@AfterSuite
	public void generateResult() {
		try {
			ExtentManager.getInstance().flush();
			if(videoDirectory.exists()) {
				videoDirectory.delete();
			}
			if(parentDirectory.exists()) {
				parentDirectory.delete();
			}
			if(reportDirectory.exists()) {
				reportDirectory.delete();
			}
			if(screenshotDirectory.exists()) {
				screenshotDirectory.delete();
			}
		} finally {
			ExtentManager.getInstance().flush();
		}
	}
}

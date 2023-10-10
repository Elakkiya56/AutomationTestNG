package com.slb.framework;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.slb.utils.AbstractPage;
import com.slb.utils.Config;
import com.slb.utils.Constants;
import com.slb.utils.ExcelObject;


public class LogMe {
	private static Logger LOGGER;
	private ExtentTest extentTest;
	public static String test_name=null;

	static Config config;
	static {
		try {
			config = new Config(Constants.ConfigPath);
		} catch (IOException e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}}
	public ExtentTest getExtentTest() {
		return extentTest;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public LogMe(String loggerClass) {
		LOGGER = Logger.getLogger(loggerClass);
	}

	@SuppressWarnings("rawtypes")
	public LogMe(Class loggerClass) {
		LOGGER = Logger.getLogger(loggerClass);
	}

	public void logInfo(ExtentTest extentTest, String message) {
		LOGGER.info("---INFO--- " + message);

		if (extentTest != null) {
			extentTest.log(Status.INFO, "---INFO--- " + message);
		}
	}
	
	public void logInfo(String message) {
		LOGGER.info("---INFO--- " + message);
	}

	public void logInfo(ExtentTest extentTest, String message, Throwable t) {
		LOGGER.info("---INFO--- " + message, t);

		if (extentTest != null) {
			extentTest.log(Status.INFO, "---INFO--- " + message + ". Exception message is " + t.getMessage());
		}
	}

	public void logWarn(ExtentTest extentTest, String message) {
		LOGGER.warn("---WARN--- " + message);

		if (extentTest != null) {
			extentTest.log(Status.WARNING, "---WARN--- " + message);
		}
	}

	public void logWarn(ExtentTest extentTest, String message, String screenshotPath) {
		LOGGER.warn("---WARN--- " + message);

		if (extentTest != null) {
			//extentTest.log(Status.WARNING, "---WARN--- " + message + extentTest.addScreenCaptureFromPath(screenshotPath));
			logWithScreenshot("warning", message, BaseTest.driver);
			
		}
	}

	public void logWarn(ExtentTest extentTest, String message, Throwable t, String... screenshotPath) {
		LOGGER.warn("---WARN--- " + message, t);

		if (extentTest != null) {
			if (screenshotPath == null) {
				extentTest.log(Status.WARNING, "---WARN--- " + message + ". Exception message is " + t.getMessage());
			} else {
				//extentTest.log(Status.WARNING, "---WARN--- " + message + ". Exception message is " + t.getMessage()
				//		+ extentTest.addScreenCaptureFromPath(screenshotPath[0]));
				logWithScreenshot("warning", message, BaseTest.driver);
			}
		}
	}

	public void logError(ExtentTest extentTest, String message) {
		LOGGER.error("---ERROR--- " + message);

		if (extentTest != null) {
			logWithScreenshot("fail", "---ERROR--- " + message, BaseTest.driver);
		}
	}
	
	public void logError(String message) {
		LOGGER.error("---ERROR--- " + message);
	}

	public void logError(ExtentTest extentTest, String message, String screenshotPath) {
		LOGGER.error("---ERROR--- " + message);

		if (extentTest != null) {
			//extentTest.log(Status.FAIL, "---ERROR--- " + message + extentTest.addScreenCaptureFromPath(screenshotPath));
			logWithScreenshot("fail", message, BaseTest.driver);
		}
	}
	
	@SuppressWarnings("unused")
	public void logError(ExtentTest extentTest, String message, String screenshotPath, String oldImage) {
		try {
			LOGGER.error("---ERROR--- " + message);
			/*BufferedImage img1 = ImageIO.read(new File(TestConfig.getInstance().getReportPath() + "//" +screenshotPath));
	        BufferedImage img2=ImageIO.read(new File(oldImage));
	        BufferedImage joinedImg = JoinImage.joinBufferedImage(img1,img2);
	        String joined = TestConfig.getInstance().getReportPath() + "//"+"joined.png";
            boolean success = ImageIO.write(joinedImg, "png", new File(joined));*/
			if (extentTest != null) {
				//extentTest.log(Status.FAIL, "---ERROR--- " + message + extentTest.addScreenCaptureFromPath("joined.png"));
				logWithScreenshot("fail", message, BaseTest.driver);
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void logError(ExtentTest extentTest, String message, Throwable t, String... screenshotPath) {
		LOGGER.error("---ERROR--- Exception " + t.getClass().getSimpleName() + " encountered");
		LOGGER.error("---ERROR--- " + message, t);

		if (extentTest != null) {
			if (screenshotPath == null) {
				extentTest.log(Status.FAIL, "---ERROR--- " + message + ". Exception message is " + t.getMessage());
				logWithScreenshot("fail", "---ERROR--- " + message + ". Exception message is " + t.getMessage(), BaseTest.driver);
			} else {
				//extentTest.log(Status.FAIL, "---ERROR--- " + message + ". Exception message is " + t.getMessage()
				//		+ extentTest.addScreenCaptureFromPath(screenshotPath[0]));
				logWithScreenshot("fail", message, BaseTest.driver);
				
			}
		}
	}	

	public ExtentTest logBeginTestCase(String tsName) {
		LOGGER.info("---INFO---Strating test case " + tsName);
		return ExtentManager.getInstance().createTest(tsName);
	}

	public void logBeginTestCase(ExtentTest extentTest, String tsName, String desc) {
		LOGGER.info("---INFO---Strating Test Case : " + tsName);
		LOGGER.info("---INFO---Test Case Description : " + desc);
		extentTest = ExtentManager.getInstance().createTest(tsName, desc);
	}

	
	public void logEndTestCase(String tsName, ExtentTest extentTest) {
		LOGGER.info("---INFO---Test Case : " + tsName + " finished");
		ExtentManager.getInstance().flush();
	}

	public void logTestStep(ExtentTest extentTest, String status, String message, String filePath, Throwable... throwables) {
		Status stepStatus = Status.valueOf(status.toUpperCase());

//		String split[] = StringUtils.split(filePath, "/");
//		filePath = split[split.length - 1];
//		String relativeFilePath =filePath.replace(System.getProperty("user.dir") +"\\"+ TestConfig.getInstance().getReportPath(), "").substring(1);
		//String relativeFilePath =filePath.split("Test_Reports")[1].substring(1);



		switch (stepStatus) {
		case PASS:
			if (throwables.length != 0) {
				LOGGER.info(
						"---PASS---" + message + " Exception " + throwables[0].getClass().getSimpleName() + " occured");
				//extentTest.log(Status.PASS,
				//		"---PASS---" + message + " Exception " + throwables[0].getClass().getSimpleName() + " occured"
				//				+ extentTest.addScreenCaptureFromPath(relativeFilePath));
				logWithScreenshot("pass", message + " Exception " + throwables[0].getClass().getSimpleName() + " occured", BaseTest.driver);
			} else {
				LOGGER.info("---PASS---" + message);
				//extentTest.log(Status.PASS, "---PASS---" + message + extentTest.addScreenCaptureFromPath(relativeFilePath));
				logWithScreenshot("pass", message, BaseTest.driver);
			}
			break;
		case FAIL:
			if (throwables.length != 0) {
				logError(extentTest, "---FAIL---" + message, throwables[0], filePath);
			} else {
				logError(extentTest, "---FAIL---" + message, filePath);
			}
			break;
		case SKIP:
		case WARNING:
			if (throwables.length != 0) {
				logWarn(extentTest, "---WARNING---" + message, throwables[0], filePath);
			} else {
				logWarn(extentTest, "---WARNING---" + message, filePath);
			}
			break;
		default:
			break;
		}
	}
	
	public void logTestStep(ExtentTest extentTest, String status, String message, Throwable... throwables) throws IOException {
		Status stepStatus = Status.valueOf(status.toUpperCase());
		String testcaseName = test_name;
        boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);
        
		switch (stepStatus) {
		case PASS:
			if(config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y")&& testScreenshotFlag) {
				//String filePath = AbstractPage.takesScreenshotOnflag(status,config.getPropertyValue("ScreenShotOnPass"));
				//String relativeFilePath =filePath.split("Test_Reports")[1].substring(1);
				
			if (throwables.length != 0) {
				
				LOGGER.info(
						"---PASS---" + message + " Exception " + throwables[0].getClass().getSimpleName() + " occured");
				logWithScreenshot("pass", "---PASS---" + message + " Exception " + throwables[0].getClass().getSimpleName() + " occured", BaseTest.driver);
			} else {
				LOGGER.info("---PASS---" + message);
				logWithScreenshot("pass", message, BaseTest.driver);
			}
			}else {
				if (throwables.length != 0) {
					LOGGER.info(
							"---Exception---" + message + " Exception " + throwables[0].getClass().getSimpleName() + " occured");
					logWithScreenshot("FAIL","---Exception---" + message + " Exception: " +throwables[0].getClass().getSimpleName() + " occured", BaseTest.driver);
				} else {
					LOGGER.info("---PASS---" + message);
					extentTest.log(Status.PASS, "---PASS---" + message);
				}
				
			}
			break;
		case FAIL:
			if (throwables.length != 0) {
				logError(extentTest, "---FAIL---" + message, throwables[0], AbstractPage.takesScreenshot());
			} else {
				logError(extentTest, "---FAIL---" + message, AbstractPage.takesScreenshot());
			}
			break;
		case SKIP:
		case WARNING:
			if (throwables.length != 0) {
				logWarn(extentTest, "---WARNING---" + message, throwables[0], AbstractPage.takesScreenshot());
			} else {
				logWarn(extentTest, "---WARNING---" + message, AbstractPage.takesScreenshot());
			}
			break;
		default:
			break;
		}
	}
		
	public void logWithScreenshot(String status, String msg, WebDriver driver) {
		try {
			if(status.equalsIgnoreCase("warning")) {
				BaseTest.extentTest.log(Status.WARNING, msg,
						MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen()).build());
				}
			if(status.equalsIgnoreCase("info")) {
				BaseTest.extentTest.log(Status.INFO, msg,
						MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen()).build());
				}
			if(status.equalsIgnoreCase("pass")) {
				BaseTest.extentTest.log(Status.PASS, msg,
						MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen()).build());
				}
			if(status.equalsIgnoreCase("fail")) {
				BaseTest.extentTest.log(Status.FAIL, msg,
						MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen()).build());
				}
			if(status.equalsIgnoreCase("skip")) {
				BaseTest.extentTest.log(Status.SKIP, msg,
						MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen()).build());
				}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String captureScreen() {
		String filePath = null;
		try {
			filePath = "Screenshots/"+ LogMe.test_name + "-" +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replace(" ", "-").replaceAll(":", "-")+ ".jpg";
			//File dest = new File(System.getProperty("user.dir") + "/Framework/Test_Reports/" + filePath);
			File dest = new File(BaseTest.REPORT_FOLDER_PATH+"/" +filePath);
		
			if (!dest.exists()) {
				dest.getParentFile().mkdirs();
				dest.createNewFile();
			}
			//			Screenshot Page Scrolling Code
			// ImageIO.write(new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(BaseTest.driver).getImage(), "JPG", dest);
			File screenshot = ((TakesScreenshot) BaseTest.driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, dest);
		} catch (IOException e) {
			e.printStackTrace();
			filePath = null;
		}
		return filePath;
	}

}
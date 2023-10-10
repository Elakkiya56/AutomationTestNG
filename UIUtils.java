package com.slb.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.json.Json;
import javax.json.stream.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.json.stream.JsonParser.Event;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import com.slb.driver.TestConfig;
import com.slb.framework.BaseTest;
import com.slb.framework.LogMe;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import junit.framework.Assert;

//import groovy.json.StreamingJsonBuilder.StreamingJsonDelegate;

public class UIUtils {
	static Logger LOGGER = Logger.getLogger(UIUtils.class);
	static LogMe logMeLOGGER = null;

	private static Config config;

	public static ExpectedCondition<Boolean> waitForPageLoad;
	public static Map<String, String> data;

	private static final String JQUERY_ACTIVE_CONNECTIONS_QUERY = "return $.active == 0;";

	static {
		try {
			config = new Config("Framework/Test_Config/config.properties");
			logMeLOGGER = new LogMe(UIUtils.class);
		} catch (IOException e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}

		waitForPageLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					return executeScript(driver, "return document.readyState").equals("complete");
				} catch (Exception e) {
					return false;
				}
			}
		};
	}

	public static void clearText(WebDriver driver, By by, String... strings) {
		WebElement element;
		try {
			element = findElement(driver, by);
			element.clear();
			Thread.sleep(500);
		} catch (Exception e1) {
			e1.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clearing text of element:" + strings[0] + ".", BaseTest.LOGGER.captureScreen(),
						e1);
			} else {
				BaseTest.LOGGER
						.logTestStep(
								BaseTest.extentTest, "FAIL", "Failed while clearing text of element:"
										+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
								BaseTest.LOGGER.captureScreen(), e1);
			}

			Assert.fail("Clearing text of element failed");
		}

	}

	public static By locate(String pageName, String objectName) {
		BaseTest.PAGENAME = pageName;
		BaseTest.OBJECTNAME = objectName;
		return TestConfig.getInstance().getObjRep().getLocator(pageName, objectName);
	}

	public static By locate(String pageName, String objectName, String data) {
		BaseTest.PAGENAME = pageName;
		BaseTest.OBJECTNAME = objectName;
		return TestConfig.getInstance().getObjRep().getLocator(pageName, objectName, data);
	}

	public static final ExpectedCondition<Boolean> EXPECT_DOC_READY_STATE = new ExpectedCondition<Boolean>() {
		String script = "if (typeof window != 'undefined') { return document.readyState;} else { return 'notready';}";

		@Override
		public Boolean apply(WebDriver driver) {
			try {
				String result = String.valueOf(executeScript(driver, script));
				return (result.equals("complete") || result.equals("interactive"));
			} catch (Exception e) {
				LOGGER.error(e.getClass().getSimpleName(), e);
				return false;
			}
		}
	};

	public static final ExpectedCondition<Boolean> EXPECT_NO_SPINNERS = new ExpectedCondition<Boolean>() {
		@Override
		public Boolean apply(WebDriver driver) {
			Boolean loaded = true;
			try {
				List<WebElement> spinners = driver.findElements(Constants.BYSPINNER);

				for (WebElement spinner : spinners) {
					if (spinner.isDisplayed()) {
						loaded = false;
						break;
					}
				}
			} catch (Exception e) {
				LOGGER.error(e.getClass().getSimpleName(), e);
				return false;
			}
			return loaded;
		}
	};

	public static Config getConfig() {
		return config;
	}

	private static DesiredCapabilities getBrowserCapabilities(String browserType, String driverPath) throws Exception {
		DesiredCapabilities dc = new DesiredCapabilities();

		switch (browserType.toUpperCase()) {
		case "FIREFOX":
			if (StringUtils.isNotBlank(driverPath)) {
				System.setProperty("webdriver.gecko.driver", driverPath);
			} else {
				System.setProperty("webdriver.gecko.driver", config.getPropertyValue("GeckoDriverPath"));
			}
//			dc = DesiredCapabilities.firefox();
//			dc.setBrowserName(BrowserType.FIREFOX);
//			dc.setCapability("browserName", "firefox");

			FirefoxOptions fOptions = new FirefoxOptions();
			fOptions.setCapability("browserName", "firefox");
			fOptions.setBrowserVersion("");
			fOptions.setPlatformName("");

			break;
		case "CHROME":
			if (StringUtils.isNotBlank(driverPath)) {
				System.setProperty("webdriver.chrome.driver", driverPath);
			} else {
				System.setProperty("webdriver.chrome.driver", config.getPropertyValue("ChromeDriverPath"));
			}
//			dc = DesiredCapabilities.
//			dc.setBrowserName(BrowserType.CHROME);
//			dc.setCapability(ChromeOptions.CAPABILITY, options);

			ChromeOptions cOptions = new ChromeOptions();
			cOptions.addArguments("test-type");
			cOptions.setCapability("browserName", "chrome");
			cOptions.setBrowserVersion("");
			cOptions.setPlatformName("");

			break;
		case "IE":
			if (StringUtils.isNotBlank(driverPath)) {
				System.setProperty("webdriver.ie.driver", driverPath);
			} else {
				System.setProperty("webdriver.ie.driver", config.getPropertyValue("InternetExplorerDriverPath"));
			}
//			dc = DesiredCapabilities.internetExplorer();
//			dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//			dc.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
//			dc.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
//			dc.setCapability("ignoreProtectedModeSettings", true);
//			dc.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//			dc.setBrowserName(BrowserType.IE);

			InternetExplorerOptions ieOptions = new InternetExplorerOptions();
			ieOptions.setCapability("browserName", "ie");
			ieOptions.setBrowserVersion("");
			ieOptions.setPlatformName("");

			break;
		default:
			break;
		}
		return dc;
	}

	public static WebDriver createDriverInstance(String browserType, String driverPath) throws Exception {
		WebDriver driver = null;

		switch (browserType) {
		case "FIREFOX":
			if (StringUtils.isNotBlank(driverPath)) {
				System.setProperty("webdriver.gecko.driver", driverPath);
			} else {
				System.setProperty("webdriver.gecko.driver", config.getPropertyValue("GeckoDriverPath"));
			}
			driver = new FirefoxDriver();
			break;

//	Below Code is for Linux Headless
		case "LINUXCHROME":
			System.out.println("Execution Starts with Linux-Chrome");
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			options.addArguments("--window-size=1440x900");
			options.addArguments("--disable-dev-shm-usage");
//			if (StringUtils.isNotBlank(driverPath)) {
//				System.setProperty("webdriver.chrome.driver", driverPath);
//			} else {
//				System.setProperty("webdriver.chrome.driver", config.getPropertyValue("LinuxChromeDriverPath"));
//
//			}
			driver = new ChromeDriver(options);
			break;

//	Below Code supports Windows Chrome Execution
		case "WINDOWSCHROME":
			System.out.println("Execution Starts with Windows-Chrome");
			WebDriverManager.chromedriver().setup();
//			if (StringUtils.isNotBlank(driverPath)) {
//				System.setProperty("webdriver.chrome.driver", driverPath);
//			} else {
//				System.setProperty("webdriver.chrome.driver", config.getPropertyValue("ChromeDriverPath"));
//
//			}
			driver = new ChromeDriver();
			break;

		case "IE":
			DesiredCapabilities dc = getBrowserCapabilities(browserType, driverPath);
			InternetExplorerOptions ieOptions = new InternetExplorerOptions(dc);
			driver = new InternetExplorerDriver(ieOptions);
			break;
		default:
			break;
		}

//		driver.manage().timeouts().setScriptTimeout(Long.parseLong(config.getPropertyValue("ScriptTimeoutSeconds")),
//				TimeUnit.SECONDS);
//		driver.manage().timeouts().pageLoadTimeout(Long.parseLong(config.getPropertyValue("AVGWAITTIME")),
//				TimeUnit.SECONDS);
//		driver.manage().timeouts().implicitlyWait(Long.parseLong(config.getPropertyValue("AVGWAITTIME")),
//				TimeUnit.SECONDS);

		driver.manage().timeouts()
				.implicitlyWait(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("AVGWAITTIME"))));
		driver.manage().timeouts()
				.scriptTimeout(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("ScriptTimeoutSeconds"))));
		driver.manage().timeouts()
				.pageLoadTimeout(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("AVGWAITTIME"))));

		return driver;
	}

	public static WebDriver createDriverInstance(String browserType, String driverPath, String gridURL)
			throws Exception {
		WebDriver driver = new RemoteWebDriver(new URL(gridURL), getBrowserCapabilities(browserType, driverPath));
//		driver.manage().timeouts().setScriptTimeout(Long.parseLong(config.getPropertyValue("ScriptTimeoutSeconds")),
//				TimeUnit.SECONDS);
//		driver.manage().timeouts().pageLoadTimeout(Long.parseLong(config.getPropertyValue("AVGWAITTIME")),
//				TimeUnit.SECONDS);
//		driver.manage().timeouts().implicitlyWait(Long.parseLong(config.getPropertyValue("AVGWAITTIME")),
//				TimeUnit.SECONDS);

		driver.manage().timeouts()
				.implicitlyWait(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("AVGWAITTIME"))));
		driver.manage().timeouts()
				.scriptTimeout(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("ScriptTimeoutSeconds"))));
		driver.manage().timeouts()
				.pageLoadTimeout(Duration.ofSeconds(Long.parseLong(config.getPropertyValue("AVGWAITTIME"))));
		return driver;
	}

	public static Object executeScript(WebDriver driver, String script, Object... args) {
		return ((JavascriptExecutor) (driver)).executeScript(script, args);
	}

	public static Object executeAsyncScript(WebDriver driver, String script, Object... args) {
		return ((JavascriptExecutor) (driver)).executeAsyncScript(script, args);
	}

	public static boolean isObjectExist(WebDriver driver, By by) {
		return (driver.findElements(by).size() > 0);
	}

	public static void Compare(ExtentTest extent, String optionText, String optionText2) {
		optionText = optionText.trim();

		if (optionText.contains(optionText2)) {
			System.out.println(optionText + " is validated with " + optionText2);
			extent.log(Status.PASS, optionText + "is validated with " + optionText2);

		} else {
			System.out.println(optionText + " is not  validated with " + optionText2);
			extent.log(Status.FAIL, optionText + "is  not validated with " + optionText2);
		}
	}

	public static By getLocatorObject(String locatorType, String locatorValue) {
		By by = null;

		switch (locatorType.toUpperCase()) {
		case "XPATH":
			by = By.xpath(locatorValue);
			break;
		case "ID":
			by = By.id(locatorValue);
			break;
		case "NAME":
			by = By.name(locatorValue);
			break;
		case "TAGNAME":
		case "TAG":
			by = By.tagName(locatorValue);
			break;
		case "CLASSNAME":
		case "CLASS":
			by = By.className(locatorValue);
			break;
		case "CSSSELECTOR":
		case "CSS":
			by = By.cssSelector(locatorValue);
			break;
		case "LINKTEXT":
		case "LINK":
			by = By.linkText(locatorValue);
			break;
		case "PARTIALLINKTEXT":
			by = By.partialLinkText(locatorValue);
			break;
		default:
			break;
		}

		return by;
	}

	public static By getLocatorObject(String locator) {
		return getLocatorObject(locator.split(config.getPropertyValue("LocatorValueSeparator"))[0],
				locator.split(config.getPropertyValue("LocatorValueSeparator"))[1]);
	}

	public static WebElement findElement(WebDriver driver, By by) throws Exception {
		// AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
		return findElement(driver, by, Integer.valueOf(config.getPropertyValue("AVGWAITTIME")));
	}

	public static WebElement findElement(WebDriver driver, By by, int waitTime) {
		try {
			AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WebDriverWait wWait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
		return wWait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public static WebElement findElements(WebDriver driver, By by) {
		try {
			AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<WebElement> webElements = driver.findElements(by);
		for (WebElement webElement : webElements) {
			if (webElement.isDisplayed()) {
				return webElement;
			}
		}
		return null;
	}

	public static void highLightElement(WebDriver driver, WebElement element) {
		executeScript(driver, "arguments[0].setAttribute('style', 'border: 2px solid blue;');", element);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		executeScript(driver, "arguments[0].setAttribute('style', 'border: 3px solid blue;');", element);
	}

	public static void highLightElement(WebDriver driver, By by) {
		WebElement element = driver.findElement(by);
		executeScript(driver, "arguments[0].setAttribute('style', 'border: 2px solid blue;');", element);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		executeScript(driver, "arguments[0].setAttribute('style', 'border: 3px solid blue;');", element);
	}

	public static List<WebElement> retrieveChildElements(WebDriver driver, By by) {
		WebElement element = driver.findElement(by);
		List<WebElement> childs = element.findElements(By.xpath(".//*"));
		return childs;
	}

	public static void DoubleclickElement(WebDriver driver, By by) throws Exception {
		WebElement element = findElement(driver, by);
		new Actions(driver).moveToElement(element).doubleClick().build().perform();
	}

	public static void hoverElement(WebDriver driver, WebElement element) {
		Actions action = new Actions(driver);
		// action.moveToElement(element).build().perform();
		action.moveToElement(element).perform();
	}

	public static boolean isAlertPresent(WebDriver driver) {
		boolean result = false;

		try {
			driver.switchTo().alert();
			result = true;
			driver.switchTo().defaultContent();
		} catch (NoAlertPresentException e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}

		return result;
	}

	public static Alert getAlert(WebDriver driver) {
		return getAlert(driver, Integer.valueOf(config.getPropertyValue("AVGWAITTIME")));
	}

	public static void handleAlert() {
		try {
			Robot robot = new Robot();

			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(200);
			robot.keyRelease(KeyEvent.VK_TAB);
			Thread.sleep(200);

			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(200);
			robot.keyRelease(KeyEvent.VK_TAB);
			Thread.sleep(200);

			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(200);
			robot.keyRelease(KeyEvent.VK_ENTER);

			Thread.sleep(5000);
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
	}

	public static Alert getAlert(WebDriver driver, int waitTime) {
		return new WebDriverWait(driver, Duration.ofSeconds(waitTime)).until(ExpectedConditions.alertIsPresent());
	}

	public static void alertAccept(WebDriver driver) throws Exception {
		getAlert(driver).accept();
		driver.switchTo().defaultContent();
	}

	public static void alertDismiss(WebDriver driver) throws Exception {
		getAlert(driver).dismiss();
		driver.switchTo().defaultContent();
	}

	public static void alertAccept(WebDriver driver, int waitTime) {
		getAlert(driver, waitTime).accept();
		driver.switchTo().defaultContent();
	}

	public static void alertDismiss(WebDriver driver, int waitTime) {
		getAlert(driver, waitTime).dismiss();
		driver.switchTo().defaultContent();
	}

	public static String getToolTip(WebDriver driver, String screenName, String element) throws Exception {
		WebElement webElement = UIUtils.findElement(driver,
				TestConfig.getInstance().getObjRep().getLocator(screenName, element));
		// UIUtils.hoverElement(driver, webElement);
		return webElement.getAttribute("Title");

	}

	public static void takeScreenshot(WebDriver driver, String filePath) throws IOException {
		File file = new File(filePath);

		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileUtils.copyFile(((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE), file);
	}

	public static void takeScreenshot(WebDriver driver, WebElement element, String filePath) throws IOException {
		UIUtils.scrollIntoView(driver, element);

		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File file = new File(filePath);

		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileUtils.copyFile(screenshot, file);
	}

	public static void closeAllPopUps(WebDriver driver) {
		String mainWinHanlde = driver.getWindowHandle();

		// Closing all but the main window
		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
			if (!winHandle.equalsIgnoreCase(mainWinHanlde)) {
				driver.close();
			}
		}

		// Focusing back to main Window
		driver.switchTo().window(mainWinHanlde);
	}

	public static void selectValue(WebDriver driver, By by, String optionText) {

		optionText = optionText.trim();

		if (optionText.toLowerCase().startsWith("index=")) {
			selectValue(driver, by, "index", optionText.replaceAll("index=", ""));
		} else if (optionText.toLowerCase().startsWith("text=")) {
			selectValue(driver, by, "text", optionText.replaceAll("text=", ""));
		} else if (optionText.toLowerCase().startsWith("containstext=")) {
			selectValue(driver, by, "containstext", optionText.replaceAll("containstext=", ""));
		} else if (optionText.startsWith("value=")) {
			selectValue(driver, by, "value", optionText.replaceAll("value=", ""));
		} else {
			new Select(driver.findElement(by)).selectByVisibleText(optionText);
		}
	}

	public static void selectValue(WebDriver driver, By by, String selectBy, String option, String... strings) {
		try {
			Select select = new Select(driver.findElement(by));

			switch (selectBy.toLowerCase()) {
			case "index":
				select.selectByIndex(Integer.valueOf(option));
				break;
			case "text":
				select.selectByVisibleText(option);
				break;
			case "value":
				select.selectByValue(option);
				break;
			case "containstext":
				int indexNum = 1;
				for (WebElement element : select.getOptions()) {
					if (element.getText().toLowerCase().contains(option.toLowerCase())) {
						select.selectByIndex(indexNum);
						break;
					}
					indexNum++;
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while selecting the value:" + option + " in element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e);
			} else {
				BaseTest.LOGGER.logTestStep(
						BaseTest.extentTest, "FAIL", "Failed while selecting the value:" + option + " in element:"
								+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
						BaseTest.LOGGER.captureScreen(), e);
			}

			Assert.fail("Selecting the value in element failed");
		}
	}

	public static void verticalScrollWindow(WebDriver driver, String direction) {
		if (direction.equalsIgnoreCase("Up")) {
			executeScript(driver, "scroll(250, 0)");
		} else {
			executeScript(driver, "scroll(0, 250)");
		}
	}

	public static void scrollIntoView(WebDriver driver, WebElement element) {
		executeScript(driver, "arguments[0].scrollIntoView(true);", element);
	}

	public static void scrolldown(WebDriver driver) {
		executeScript(driver, "window.scrollBy(0,1000)");
	}

	public static void scrollIntoView(WebDriver driver, By by) {
		WebElement element = driver.findElement(by);
		executeScript(driver, "arguments[0].scrollIntoView(true);", element);
	}

	public static void scrollIntoView(WebDriver driver, Integer height) throws InterruptedException {
		JavascriptExecutor js = ((JavascriptExecutor) driver);
		if (null == height) {
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		} else {
			js.executeScript("window.scrollTo(0, " + height + ")");
		}
		Thread.sleep(5000);
	}

	public static FluentWait<WebDriver> getFluentWait(WebDriver driver, Integer... waitTimes) {
		if (waitTimes != null) {
		} else {
		}

		if (waitTimes.length > 1) {
		} else {
		}

		return new FluentWait<>(driver).ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	public static boolean waitForPageLoad(WebDriver driver, int waitTime) {
		return new WebDriverWait(driver, Duration.ofSeconds(waitTime)).until(waitForPageLoad);
	}

	public static boolean waitForPageLoad(WebDriver driver) throws Exception {
		return waitForPageLoad(driver, Integer.valueOf(config.getPropertyValue("AVGWAITTIME")));
	}

	@SafeVarargs
	public static boolean waitForPageLoad(WebDriver driver, int waitTime, ExpectedCondition<Boolean>... conditions) {
		boolean isLoaded = false;

		try {
			waitUntilAjaxRequestCompletes(driver);
			Wait<WebDriver> wWait = getFluentWait(driver);

			for (ExpectedCondition<Boolean> condition : conditions) {
				isLoaded = wWait.until(condition);
				if (!isLoaded) {
					// Stop checking on first condition returning false
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return isLoaded;
	}

	public static WebElement waitUntilElementExists(WebDriver driver, final By by, Integer... waitTimes) {
		final Wait<WebDriver> wWait = getFluentWait(driver, waitTimes);
		WebElement element = null;
		try {
			wWait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
			element = wWait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return element;
	}

	public static boolean waitUntilElementNotExists(WebDriver driver, By by, Integer... waitTimes) {
		boolean result = false;
		try {
			result = getFluentWait(driver, waitTimes).until(
					ExpectedConditions.or(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(by)),
							ExpectedConditions.invisibilityOfElementLocated(by)));
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return result;
	}

	private static void waitUntilAjaxRequestCompletes(WebDriver driver, Integer... waitTimes) {
		final Wait<WebDriver> wWait = getFluentWait(driver, waitTimes);

		wWait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return (Boolean) executeScript(driver, JQUERY_ACTIVE_CONNECTIONS_QUERY);
			}
		});
	}

	public static boolean dynamicWait(WebDriver driver, By by, int waitTime) {
		for (int i = 1; i < waitTime; i++) {
			try {
				return driver.findElement(by).isDisplayed() == true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static void clickElement(WebDriver driver, By by, String... strings) throws Exception {
		try {
			WebElement element = findElement(driver, by);
			new Actions(driver).moveToElement(element).click().build().perform();
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on
					// element:"+strings[0]+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO", "Clicked on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Clicked on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Clicked on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on
					// element:"+BaseTest.OBJECTNAME+ " in page "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO,
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo(
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
				}
			}
		} catch (Exception e) {
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clicking on element:" + strings[0] + ".");
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "Failed while clicking on element:"
						+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
			}

			Assert.fail("Clicking on object failed");
		}

	}

	public static void clickElement(WebDriver driver, WebElement element, String... strings) throws Exception {
		try {
//			WebElement element = findElement(driver, by);
			new Actions(driver).moveToElement(element).click().build().perform();
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on
					// element:"+strings[0]+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO", "Clicked on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Clicked on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Clicked on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on
					// element:"+BaseTest.OBJECTNAME+ " in page "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO,
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo(
							"Clicked on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
				}
			}
		} catch (Exception e) {
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clicking on element:" + strings[0] + ".");
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "Failed while clicking on element:"
						+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
			}

			Assert.fail("Clicking on object failed");
		}

	}
	
	public static void clickEnter(WebDriver driver, By by, String... strings) throws Exception {
		try {
			WebElement element = findElement(driver, by);
			new Actions(driver).moveToElement(element).build().perform();
			element.sendKeys(Keys.ENTER);

			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked Enter on
					// element:"+strings[0]+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Clicked Enter on element:" + strings[0] + ".", BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Clicked Enter on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Clicked Enter on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked Enter on
					// element:"+BaseTest.OBJECTNAME+ " in page "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Clicked Enter on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO,
							"Clicked Enter on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo(
							"Clicked Enter on element:" + BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clicking ENTER on element:" + strings[0] + ".");
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "Failed while clicking ENTER on element:"
						+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
			}

			Assert.fail("Inputting text on object failed");
		}
	}

	public static void clickElementJScript(WebDriver driver, By by, String... strings) throws Exception {

		WebElement element = null;
		try {
			element = findElement(driver, by);
			clickElementJScript(driver, element);
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on element:"+strings[0]+"
					// using JavaScript.", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Clicked on element:" + strings[0] + " using JavaScript.", BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Clicked on element:" + strings[0] + " using JavaScript.");
					BaseTest.LOGGER.logInfo("Clicked on element:" + strings[0] + " using JavaScript.");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Clicked on
					// element:"+BaseTest.OBJECTNAME+ " using JavaScript in page
					// "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(
							BaseTest.extentTest, "INFO", "Clicked on element:" + BaseTest.OBJECTNAME
									+ " using JavaScript in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Clicked on element:" + BaseTest.OBJECTNAME
							+ " using JavaScript in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo("Clicked on element:" + BaseTest.OBJECTNAME + " using JavaScript in page "
							+ BaseTest.PAGENAME + ".");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clicking element using JavaScript on element:" + strings[0] + ".");
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while clicking element using JavaScript on element:" + BaseTest.OBJECTNAME + " in page "
								+ BaseTest.PAGENAME + ".");
			}

			Assert.fail("Inputting text on object failed");
		}
	}

	public static void clickElementJScript(WebDriver driver, WebElement element) {
		// executeScript(driver, "arguments[0].scrollIntoView(true);", element);
		// executeScript(driver, "arguments[0].click();", element);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

	}

	public static void inputValue(WebElement element, String data, String... strings) {
		try {
			element.clear();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			for (char chr : data.toCharArray()) {
				element.sendKeys(new StringBuilder(chr));
			}
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered value:"+data+" on
					// element:"+strings[0]+".", BaseTest.driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Entered value:" + data + " on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Entered value:" + data + " on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Entered value:" + data + " on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered data as "+data, null);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO", "Entered data as " + data,
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Entered data as " + data);
					BaseTest.LOGGER.logInfo("Entered data as " + data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while inputting value:" + data + " on element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e);
			} else {
				BaseTest.LOGGER.logTestStep(
						BaseTest.extentTest, "FAIL", "Failed while inputting value:" + data + " on element:"
								+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
						BaseTest.LOGGER.captureScreen(), e);
			}

			Assert.fail("Inputting value on object failed");

		}

	}

	public static void inputText(WebDriver driver, By by, String data, String... strings) {
		WebElement element;
		try {
			element = findElement(driver, by);
			element.clear();
			Thread.sleep(500);
			element.sendKeys(data);

			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered text as "+data+" on
					// element:"+strings[0]+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Entered text as " + data + " on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Entered text as " + data + " on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Entered text as " + data + " on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered text as "+data+" on
					// element:"+BaseTest.OBJECTNAME+ " in page "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(
							BaseTest.extentTest, "INFO", "Entered text as " + data + " on element:"
									+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Entered text as " + data + " on element:"
							+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo("Entered text as " + data + " on element:" + BaseTest.OBJECTNAME
							+ " in page " + BaseTest.PAGENAME + ".");
				}

			}

		} catch (Exception e1) {
			e1.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while inputting text:" + data + " on element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e1);
			} else {
				BaseTest.LOGGER.logTestStep(
						BaseTest.extentTest, "FAIL", "Failed while inputting text:" + data + " on element:"
								+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
						BaseTest.LOGGER.captureScreen(), e1);
			}

			Assert.fail("Inputting text on object failed");
		}
	}

	public static void inputText(WebDriver driver, By by, String data, int time, String... strings) {
		WebElement element;
		try {
			element = findElement(driver, by);
			element.clear();
			Thread.sleep(time);
			element.clear();
			performKeyBoardOperation(KeyEvent.VK_BACK_SPACE);
			element.sendKeys(data);
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Enetered text as "+data+" on
					// element:"+strings[0]+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Enetered text as " + data + " on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO,
							"Enetered text as " + data + " on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Enetered text as " + data + " on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Enetered text as "+data+" on
					// element:"+BaseTest.OBJECTNAME+ " in page "+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(
							BaseTest.extentTest, "INFO", "Enetered text as " + data + " on element:"
									+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Enetered text as " + data + " on element:"
							+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
					BaseTest.LOGGER.logInfo("Enetered text as " + data + " on element:" + BaseTest.OBJECTNAME
							+ " in page " + BaseTest.PAGENAME + ".");
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while entering text:" + data + " on element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e1);
			} else {
				BaseTest.LOGGER.logTestStep(
						BaseTest.extentTest, "FAIL", "Failed while entering text:" + data + " on element:"
								+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
						BaseTest.LOGGER.captureScreen(), e1);
			}

			Assert.fail("Inputting text on object failed");
		}
	}

	public static void inputText(WebElement element, String data, String... strings) {
		try {
			element.clear();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			element.sendKeys(data);
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Enetred text as "+data+" on
					// element:"+strings[0]+".", BaseTest.driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Enetred text as " + data + " on element:" + strings[0] + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Enetred text as " + data + " on element:" + strings[0] + ".");
					BaseTest.LOGGER.logInfo("Enetred text as " + data + " on element:" + strings[0] + ".");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered text as "+data,
					// BaseTest.driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO", "Entered text as " + data,
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Entered text as " + data);
					BaseTest.LOGGER.logInfo("Entered text as " + data);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while entering text:" + data + " on element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e);
			} else {
				BaseTest.LOGGER.logTestStep(
						BaseTest.extentTest, "FAIL", "Failed while entering text:" + data + " on element:"
								+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
						BaseTest.LOGGER.captureScreen(), e);
			}

			Assert.fail("Inputting text on object failed");

		}

	}

	public static void inputTextJScript(WebDriver driver, WebElement element, String data, String... strings) {
		try {
			element.clear();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			executeScript(driver, "arguments[0].scrollIntoView(true);", element);
			executeScript(driver, "arguments[0].setAttribute('value', '" + data + "');", element);
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Entered text as "+data+" on
					// element:"+strings[0]+" using JavaScript.", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Entered text as " + data + " on element:" + strings[0] + " using JavaScript.",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO,
							"Entered text as " + data + " on element:" + strings[0] + " using JavaScript.");
					BaseTest.LOGGER
							.logInfo("Entered text as " + data + " on element:" + strings[0] + " using JavaScript.");
				}
			} else {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "Enetred data as "+data+" using
					// JavaScript.", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"Enetred data as " + data + " using JavaScript.", BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.extentTest.log(Status.INFO, "Enetred data as " + data + " using JavaScript.");
					BaseTest.LOGGER.logInfo("Enetred data as " + data + " using JavaScript.");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while entering text:" + data + " using JavaScript on element:" + strings[0] + ".",
						BaseTest.LOGGER.captureScreen(), e);
			} else {
				BaseTest.LOGGER
						.logTestStep(BaseTest.extentTest, "FAIL",
								"Failed while entering text:" + data + " using JavaScript on element:"
										+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".",
								BaseTest.LOGGER.captureScreen(), e);
			}

			Assert.fail("Inputting text on object failed");

		}
	}

	public static String getText(WebElement element) {
		String text = element.getText();

		if (StringUtils.isBlank(text)) {
			text = element.getAttribute("value");
		}

		return text;
	}

	public static String getChildText(WebElement element) {
		String elementText = getText(element);
		StringBuilder sb;

		if (StringUtils.isNotBlank(elementText)) {
			sb = new StringBuilder(elementText);
		} else {
			sb = new StringBuilder();
		}

		List<WebElement> childrens = element.findElements(By.xpath(".//*"));
		for (WebElement child : childrens) {
			try {
				if (child.isDisplayed() && StringUtils.isNotBlank(child.getText())
						&& !(elementText.equalsIgnoreCase(child.getText()))) {
					sb.append(child.getText());
				}
			} catch (Exception e) {
			}
		}
		return sb.toString();
	}

	public static void moveTo(WebDriver driver, By by, String... strings) throws Exception {
		try {
			WebElement element = findElement(driver, by);
			new Actions(driver).moveToElement(element).build().perform();
			String testcaseName = LogMe.test_name;
			boolean testScreenshotFlag = ExcelObject.testCase_screenshot.get(testcaseName);

			if (strings.length > 0) {
				if (config.getPropertyValue("ScreenShotOnPass").equalsIgnoreCase("Y") && testScreenshotFlag) {
					// BaseTest.LOGGER.logWithScreenshot("INFO", "MoveToElement on
					// element:"+BaseTest.OBJECTNAME+" and page:"+BaseTest.PAGENAME+".", driver);
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"MoveToElement on element:" + BaseTest.OBJECTNAME + " and page:" + BaseTest.PAGENAME + ".",
							BaseTest.LOGGER.captureScreen());
				} else {
					BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "INFO",
							"MoveToElement on element:" + BaseTest.OBJECTNAME + " and page:" + BaseTest.PAGENAME + ".");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (strings.length > 0) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						"Failed while moving to element on element:" + strings[0] + ".");
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "Failed while moving to element on element:"
						+ BaseTest.OBJECTNAME + " in page " + BaseTest.PAGENAME + ".");
			}

			Assert.fail("Inputting text on object failed");
		}
	}

	public static void moveAndClick(WebDriver driver, List<By> byList) throws Exception {
		Actions actions = new Actions(driver);
		WebElement element;

		for (int i = 0; i < byList.size() - 1; i++) {
			element = findElement(driver, byList.get(i));
			actions.moveToElement(element).perform();
			Thread.sleep(3000);
		}

		element = findElement(driver, byList.get(byList.size() - 1));
		actions.moveToElement(element).click().build().perform();
	}

	public static String generateAbsoluteXPath(WebElement childElement, String current) {
		String childTag = childElement.getTagName();

		if ("html".equals(childTag)) {
			return "/html" + current;
		}

		WebElement parentElement = childElement.findElement(By.xpath(".."));
		List<WebElement> childElements = parentElement.findElements(By.xpath("*"));

		int count = 0;

		for (WebElement webElement : childElements) {
			if (webElement.getTagName().equals(childTag)) {
				count++;
			}

			if (childElement.equals(webElement)) {
				return generateAbsoluteXPath(parentElement, "/" + childTag + "[" + count + "]" + current);
			}
		}
		return null;
	}

	public static WebElement getGridParentRowElement(WebElement element) {
		WebElement parentRow = null;
		int rowCounter = 0;

		if (element != null) {
			parentRow = element;

			do {
				parentRow = parentRow.findElement(By.xpath(".."));
				rowCounter++;
			} while (parentRow.getTagName().toLowerCase().compareTo("tr") != 0 && rowCounter < 10);

			if (rowCounter >= 10) {
				return null;
			}
		}
		return parentRow;
	}

	public static Map<Integer, List<WebElement>> getGridCells(WebDriver driver, String tableXPath) throws Exception {
		Map<Integer, List<WebElement>> gridElements = new HashMap<Integer, List<WebElement>>();
		String rowsXPath = tableXPath + "//tbody/tr";
		List<WebElement> rowElements = driver.findElements(By.xpath(rowsXPath));

		int rowIndex = 1;
		for (WebElement rowElement : rowElements) {
			List<WebElement> gridCells = rowElement.findElements(By.xpath(".//td"));

			gridElements.put(rowIndex, gridCells);
			rowIndex++;
		}

		return gridElements;
	}

	public static Map<Integer, List<String>> getGridCellValues(WebDriver driver, String tableXPath) throws Exception {
		Map<Integer, List<String>> values = new HashMap<Integer, List<String>>();
		Map<Integer, List<WebElement>> gridElements = getGridCells(driver, tableXPath);

		for (int rowIndex = 1; rowIndex <= gridElements.size(); rowIndex++) {
			List<WebElement> gridCells = gridElements.get(rowIndex);
			List<String> rowValues = new ArrayList<String>();

			for (WebElement gridCell : gridCells) {
				rowValues.add(UIUtils.getChildText(gridCell));
			}

			values.put(rowIndex, rowValues);
		}
		return values;
	}

	public static List<WebElement> getGridRowElements(WebDriver driver, String tableXPath, String itemValue)
			throws Exception {
		String rowXPath = tableXPath + "//*[text()='" + itemValue + "']//ancestor::tr[1]";
		WebElement rowElement = UIUtils.findElement(driver, By.xpath(rowXPath));

		return rowElement.findElements(By.xpath(".//td"));
	}

	public static void getGridValues(WebDriver driver, By by, String Value) throws Exception {
		try {

			WebElement rowElement = UIUtils.findElement(driver, by);

			String getRowData = rowElement.getText();

//		if (getRowData.equalsIgnoreCase(Value)) {
			if (getRowData.contains(Value)) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS",
						Value + "- is Expected Value Matched with Actual Value - " + getRowData);
//			AssertManager.getInstance().sAssertEquals(getRowData, Value, "Actual Matched with Expected Value", true, false);
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
						Value + " is Expected Value but NOT Matched with Actual Value =" + getRowData);
				Assert.fail("Steps Failed because Expected Value Not Matched with Actual Value");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed because Value not matched on Page");
		}
	}

	public static List<String> getGridRowValues(WebDriver driver, String tableXPath, String itemValue)
			throws Exception {
		List<WebElement> gridRowElements = getGridRowElements(driver, tableXPath, itemValue);
		List<String> rowValues = new ArrayList<String>();

		for (WebElement gridCell : gridRowElements) {
			rowValues.add(UIUtils.getChildText(gridCell));
		}

		return rowValues;
	}

	public static int getGridColNumber(WebDriver driver, String tableXPath, String tableHeader) {
		String colXPath = tableXPath + "//th";
		List<WebElement> colHeaders = driver.findElements(By.xpath(colXPath));
		int colNum = 1;

		for (WebElement colHeader : colHeaders) {
			WebElement title;

			try {
				title = colHeader.findElement(By.xpath(".//*[text() = '" + tableHeader + "']"));
				if (title.isDisplayed()) {
					return colNum;
				}
			} catch (Exception e) {
			}
			colNum++;
		}

		return -1;
	}

	public static String getGridColValueForItem(WebDriver driver, String tableXPath, String itemValue, int colNum)
			throws Exception {
		String rowXPath = tableXPath + "//*[text()='" + itemValue + "']//ancestor::tr[1]";
		WebElement rowElement = UIUtils.findElement(driver, By.xpath(rowXPath));
		String colValue = getChildText(rowElement.findElement(By.xpath(".//td[" + colNum + "]")));
		return colValue;
	}

	public static Map<String, List<WebElement>> getGridCellsColWise(WebDriver driver, String tableXPath)
			throws Exception {
		Map<String, List<WebElement>> gridElements = new HashMap<String, List<WebElement>>();

		String colXPath = tableXPath + "//th";
		List<WebElement> colElements = driver.findElements(By.xpath(colXPath));

		int colNum = 1;

		for (WebElement colHeader : colElements) {
			String rowXPath = tableXPath + "//tbody/tr[" + colNum + "]";
			List<WebElement> gridCells = driver.findElement(By.xpath(rowXPath)).findElements(By.xpath(".//td"));

			gridElements.put(getChildText(colHeader), gridCells);
		}
		return gridElements;
	}

	public static Map<String, List<String>> getGridCellValuesColWise(WebDriver driver, String tableXPath)
			throws Exception {
		Map<String, List<WebElement>> gridElements = getGridCellsColWise(driver, tableXPath);
		Map<String, List<String>> gridCellValues = new HashMap<String, List<String>>();

		Set<String> headers = gridElements.keySet();

		for (String header : headers) {
			List<WebElement> colElements = gridElements.get(header);
			List<String> colValues = new ArrayList<String>();

			for (WebElement cell : colElements) {
				colValues.add(getChildText(cell));
			}
			gridCellValues.put(header, colValues);
		}

		return gridCellValues;
	}

	public static void fileUpload(String filePath) throws AWTException, InterruptedException {
		StringSelection selection = new StringSelection(filePath);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);

		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);

		Thread.sleep(6000);

		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	public static String getDate(String format) {
		// DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		SimpleDateFormat dtf = new SimpleDateFormat(format);
		LocalDateTime now = LocalDateTime.now();

		return dtf.format(now);
	}

	public static void performKeyBoardOperation(int keyEvent) throws AWTException, InterruptedException {
		Robot robot = new Robot();
		robot.keyPress(keyEvent);
		robot.keyRelease(keyEvent);
		Thread.sleep(15000);
	}

	public static String getText(WebDriver driver, By by) throws Exception {
		WebElement element = findElement(driver, by);
		highLightElement(driver, by);
		String text = element.getText();
		if (StringUtils.isBlank(text)) {
			text = element.getAttribute("value");
		}
		return text;
	}

	public static void clickTab(WebDriver driver, By by) throws Exception {
		WebElement element = findElement(driver, by);
		new Actions(driver).moveToElement(element).build().perform();
		element.sendKeys(Keys.TAB);
	}

	public static String add_Minus_Date(int i, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		return dateFormat.format(cal.getTime());
	}

	public static void writeToCell(String fileName, String sheetName, int rowNum, int colNum, String Value)
			throws IOException {

		try {
			String loc = System.getProperty("user.dir") + "/Framework/Test_Data/" + fileName;
			FileInputStream fis = new FileInputStream(loc);

			try (XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
				XSSFSheet sheet = workbook.getSheet(sheetName);

				XSSFRow row = sheet.getRow(rowNum);

				Cell cell = row.getCell(colNum);
				if (cell == null) {
					cell = row.createCell(colNum);
				}
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					cell.setCellValue(Value);
				}
				fis.close();
				FileOutputStream out = new FileOutputStream(loc);
				workbook.write(out);
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dates  String array of dates in given format
	 * @param format e.g MM/dd/yyyy hh:mm:ss
	 * @return latest Date from array
	 * @throws ParseException
	 */
	public static String latestDate(String dates[], String format) throws ParseException {
		Date latestDate = null;
		for (String date : dates) {
			if (null != date) {
				Date dateVar = new SimpleDateFormat(format).parse(date);
				if (null == latestDate) {
					latestDate = dateVar;
				} else if (latestDate.before(dateVar)) {
					latestDate = dateVar;
				}
			}
		}
		DateFormat dateFormat = new SimpleDateFormat(format);
		String latestDateStr = dateFormat.format(latestDate);
		return latestDateStr;
	}

	public static void main(String args[]) throws ParseException {
		String[] dates = new String[3];
		dates[0] = "02/02/2021 16:33:46";
		dates[1] = "02/02/2021 16:33:44";
		dates[2] = "02/01/2021 16:33:44";
		System.out.println(latestDate(dates, "MM/dd/yyyy HH:mm:ss"));
	}

	public static void textClear(WebDriver driver, By by) {
		WebElement element;
		try {
			element = findElement(driver, by);
			Thread.sleep(5000);
			element.sendKeys(Keys.CONTROL, Keys.chord("a"));
			element.sendKeys(Keys.BACK_SPACE);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void mouseOverElement(WebDriver driver, By by) {

		WebElement element;
		try {
			element = findElement(driver, by);
			Thread.sleep(10000);
			Actions action = new Actions(driver);
			action.moveToElement(element).build().perform();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean switchToFrame(WebDriver driver, By by) {
		boolean result = false;
		WebElement element;
		try {
			try {
				element = findElement(driver, by);
				driver.switchTo().frame(element);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String postAPICall(String token, String requestUrl, String casePayload) throws Exception {

		String caseNumber = "";

		try {
			String url = requestUrl;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", "Bearer " + token);
			con.setRequestProperty("Content-Type", "application/json");
			String postPayload = casePayload;
			byte[] outputBytes = postPayload.getBytes("UTF-8");
			OutputStream os = con.getOutputStream();
			os.write(outputBytes);
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			// get response code
			int responseCode = con.getResponseCode();
			System.out.println("Response code is =" + responseCode);
			con.disconnect();
			String response = sb.toString();
			System.out.println("Response body is =" + response);
			caseNumber = response.split("\"CaseNumber\":")[1].split(",")[0];
			System.out.println("Case number is =" + caseNumber);
			if (caseNumber.contains("\"")) {
				caseNumber = caseNumber.replaceAll("\"", "").trim();
			}
			System.out.println("Case number is =" + caseNumber);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return caseNumber;
	}

	public static class Tokenclass {
		private static String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (first)
					first = false;
				else
					result.append("&");
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			return result.toString();
		}

		private static HashMap<String, String> getHashMapData() {
			HashMap<String, String> mapData = new HashMap<String, String>();
			mapData.put("client_id", "5c95c003-b26a-4f6f-8afa-26e953ce822b");
			mapData.put("client_secret", "eO47Q~sfdbBuTNxl804Uic3-rBks~gDztrz1m");
			mapData.put("grant_type", "client_credentials");
			mapData.put("resource", "https://ctsgtolling-test.crm.dynamics.com/");
			return mapData;
		}
	}

	public static String postAccessToken(String requestUrl) {
		String token = "";
		try {
			String urlParameters = Tokenclass.getDataString(Tokenclass.getHashMapData());
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			String request = requestUrl;
			URL url = new URL(request);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("charset", "utf-8");
			con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			con.setUseCaches(false);
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.write(postData);
				wr.flush();
				wr.close();
			}
			BufferedReader br = null;
			String strCurrentLine;
			StringBuilder fullResponseBody = new StringBuilder();
			if (con.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((strCurrentLine = br.readLine()) != null) {
					fullResponseBody = fullResponseBody.append(strCurrentLine);
				}
			} else {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((strCurrentLine = br.readLine()) != null) {
					fullResponseBody = fullResponseBody.append(strCurrentLine);
				}
			}

			JsonParser parser = Json.createParser(new StringReader(fullResponseBody.toString()));
			while (parser.hasNext()) {
				Event event = parser.next();
				if (event == Event.KEY_NAME) {
					switch (parser.getString()) {
					case "access_token":
						parser.next();
						token = parser.getString();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.getLocalizedMessage();
		}
		return token;
	}

	public static String EmailNotificationRead(String userName, String password) throws Exception {

		String Body = null;

		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imap");
		props.setProperty("mail.imap.ssl.enable", "true");
		props.setProperty("mail.imaps.partialfetch", "false");
		props.put("mail.mime.base64.ignoreerrors", "true");

		Session mailSession = Session.getInstance(props);
		mailSession.setDebug(true);
		Store store = mailSession.getStore("imap");
		store.connect("outlook.office365.com", userName, password);

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		// search for all "unseen" messages
		Flags seen = new Flags(Flags.Flag.SEEN);
		FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		Message messages[] = folder.search(unseenFlagTerm);

		if (messages.length == 0)
			System.out.println("No messages found.");

		for (int i = 0; i < messages.length; i++) {
			// stop after listing ten messages
			if (i > 10) {
				System.exit(0);
				folder.close(true);
				store.close();
			}
			System.out.println("Message " + (i + 1));
			System.out.println("From : " + messages[i].getFrom()[0]);
			System.out.println("Subject : " + messages[i].getSubject());
			System.out.println("Sent Date : " + messages[i].getSentDate());
			System.out.println();
			System.out.println("Body: \n" + getTextFromMessage(messages[i]));
			Body = getTextFromMessage(messages[i]);
			System.out.println();
		}
		return Body;
	}

	public static String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	public static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break;
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}

	public static int postAPICallReceiveResponseCode(String token, String requestUrl, String casePayload)
			throws Exception {
		int responseCode = 0;

		try {
			String url = requestUrl;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", "Bearer " + token);
			con.setRequestProperty("Content-Type", "application/json");
			String postPayload = casePayload;
			System.out.println("Payload :" + casePayload);

			byte[] outputBytes = postPayload.getBytes("UTF-8");
			OutputStream os = con.getOutputStream();
			os.write(outputBytes);
			// get response code
			responseCode = con.getResponseCode();
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			con.disconnect();
			String response = sb.toString();
			System.out.println("Response code is =" + response);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return responseCode;
	}

	public static boolean isElementPresent(WebDriver driver, By by) {

		try {
			if (driver.findElements(by).size() != 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return true;
	}

	// New Added wait Hitendra
	public static WebElement waitUntilElementIsClickable(WebDriver driver, final By by, Integer... waitTimes) {
		final Wait<WebDriver> wWait = getFluentWait(driver, waitTimes);
		WebElement element = null;
		try {
			wWait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
			element = wWait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return element;
	}

	// New Added wait Hitendra
	public static WebElement waitUntilElementIsVisible(WebDriver driver, final By by, Integer... waitTimes) {
		final Wait<WebDriver> wWait = getFluentWait(driver, waitTimes);
		WebElement element = null;
		try {
			wWait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
			element = wWait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return element;
	}

	// New Added wait Hitendra
	public static WebElement waitUntilFrameisAvailableAndSwitchToit(WebDriver driver, final By by,
			Integer... waitTimes) {
		final Wait<WebDriver> wWait = getFluentWait(driver, waitTimes);
		WebElement element = null;
		try {
			wWait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
			wWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
		} catch (Exception e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		return element;
	}

	public static boolean verifyPDFContent(String strURL, String text) {

		String output = "";
		boolean flag = false;
		try {
			URL url = new URL(strURL);
			BufferedInputStream file = new BufferedInputStream(url.openStream());
			PDDocument document = null;
			try {
				document = PDDocument.load(file);
				output = new PDFTextStripper().getText(document);
				System.out.println(text);
			} finally {
				if (document != null) {
					document.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (output.contains(text)) {
			flag = true;
		}
		return flag;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static List<WebElement> findMultipleElements(WebDriver driver, By by) {
		List<WebElement> elements = null;
		try {
			AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
			elements = driver.findElements(by);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return elements;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static List<WebElement> findMultipleElements(WebDriver driver, String xpath) {
		List<WebElement> elements = null;
		try {
//									AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
			elements = driver.findElements(By.xpath(xpath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return elements;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static WebElement findElement(WebDriver driver, String xpath) {
		WebElement element = null;
		try {
//									AbstractPage.objWait(by, config.getPropertyValue("OBJWAITTIME"));
			element = driver.findElement(By.xpath(xpath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return element;
	}
	
	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static List<String> getGridHeaders(WebDriver driver, String tableXpath) {
		List<String> actHeaders = new ArrayList<String>();
//		String headerXpath = tableXpath + "\thead\tr\th";
		try {
			List<WebElement> elements = findMultipleElements(driver, tableXpath);
			for (int i = 0; i < elements.size(); i++) {
				if(!elements.get(i).getText().equals("")) {
					actHeaders.add(elements.get(i).getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actHeaders;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static Map<Integer, Map<String, String>> getGridCellValues(WebDriver driver, String headerXpath, int noOfRows) {
		Map<Integer, Map<String, String>> actDataMap = new LinkedHashMap<>();
//								String headerXpath = getLocatorXpath(screenName, objName) + "\\tr\\th";
		Map<String, String> data2 = new LinkedHashMap<String, String>();
		try {
			List<String> actGridHeaders = getGridHeaders(driver, headerXpath);
			for (int i = 0; i < noOfRows; i++) {
				int index = i + 1;
				String dataXpath = "//table/tbody/tr[" + index + "]/td";
				System.out.println(dataXpath);
				List<WebElement> elements = findMultipleElements(driver, dataXpath);
				for (int j = 1; j < elements.size(); j++) {
					String cellValue = elements.get(j).getText();
					String header = actGridHeaders.get(j-1);
					data2.put(header+String.valueOf(index), cellValue);
				}
				actDataMap.put(index, data2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actDataMap;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static String getLocatorXpath(String screenName, String objName) {
		return TestConfig.getInstance().getObjRep().getObject(screenName, objName)[1];
	}
	
	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static String getLocatorXpath(String screenName, String objName, String elementName) {
		return TestConfig.getInstance().getObjRep().getObject(screenName, objName)[1].replace("^^^", elementName);
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static void assertThat(boolean status, String passMsg, String failMsg) {
		try {
			if (status == false) {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", failMsg, BaseTest.LOGGER.captureScreen());
				Assert.fail(failMsg);
			} else {
				BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", passMsg, BaseTest.LOGGER.captureScreen());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(failMsg);
		}
	}

	/***
	 * @author Manikandan
	 * @throws Exception 
	 * @date: 12/9/2023
	 */
	public static String getAttribute(WebDriver driver, String screenName, String objName, String eleName, String attrName) throws Exception {
		return findElement(driver, UIUtils.locate(screenName, objName, eleName)).getAttribute(attrName);
	}

	/***
	 * @author Manikandan
	 * @throws Exception 
	 * @date: 12/9/2023
	 */
	public static void switchWindow(WebDriver driver, String url) {
		try {
			Set<String> allWindowId = driver.getWindowHandles();
			if(allWindowId.size() > 1) {
				for (String windowId : allWindowId) {
					driver.switchTo().window(windowId);
					String currentUrl = driver.getCurrentUrl();
					if(currentUrl.contains(url)) {
						break;
					}
				}
			}else {
				System.out.println("no new window");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static int getGridHeaderIndex(WebDriver driver, By by, String header) {
		int index = 0;
		try {
			List<WebElement> elements = UIUtils.findMultipleElements(driver, by);
			for(int i=0; i<elements.size(); i++) {
				System.out.println(elements.get(i).getText());
				if(elements.get(i).getText().equals(header)) {
					index = i+1;
					break;
				}
			}
			return index;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static Map<String, Integer> getGridAllHeaderIndex(WebDriver driver, By by) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		try {
			List<WebElement> elements = UIUtils.findMultipleElements(driver, by);
			for(int i=0; i<elements.size(); i++) {
				String head = elements.get(i).getText();
				if(!head.equals("")) {
					map.put(head, i+1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/***
	 * @author Manikandan
	 * @return 
	 * @date: 11/8/2023
	 */
	@SuppressWarnings("null")
	public static List<String> getGridDataByCol(WebDriver driver, int index, int count) {
		List<String> data = new ArrayList<String>();
		try {
			for(int i=1; i<=count; i++) {
				String xpath = "//table/tbody/tr["+i+"]/td["+index+"]";
				data.add(UIUtils.findElement(driver, xpath).getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/***
	 * @author Manikandan
	 * @return 
	 * @date: 11/8/2023
	 */
	@SuppressWarnings("null")
	public static List<String> getGridDataByCol(WebDriver driver, String tableXpath, int index, int count) {
		List<String> data = new ArrayList<String>();
		try {
			for(int i=1; i<=count; i++) {
				String xpath = "//table/tbody/tr["+i+"]/td["+index+"]";
				data.add(UIUtils.findElement(driver, xpath).getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/***
	 * @author Manikandan
	 * @return 
	 * @date: 11/8/2023
	 */
	@SuppressWarnings("null")
	public static List<String> getGridDataColor(WebDriver driver, String tableXpath, int index, int count) {
		List<String> data = new ArrayList<String>();
		try {
			for(int i=1; i<=count; i++) {
				String xpath = tableXpath + "/tbody/tr["+i+"]/td["+index+"]/span";
				String rgba = UIUtils.findElement(driver, xpath).getCssValue("background-color");
				String values = rgba.substring(rgba.charAt('(')+1,rgba.charAt(')'));
				String[] split = values.split(",");
				int red = Integer.parseInt(split[0].trim());
				int green = Integer.parseInt(split[1].trim());
				int blue = Integer.parseInt(split[2].trim());
				int alpha = Integer.parseInt(split[3].trim());
				data.add(rgbaToHex(red, green, blue, alpha));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void holdElement(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.clickAndHold(element).build().perform();
	}
	
	public static boolean checkVisiblityOfElement(WebDriver driver, String xpath, int waitTime) {
		boolean status = false;
		try {
			WebDriverWait wWait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
//			wWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			status = driver.findElement(By.xpath(xpath)).isDisplayed();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return status;
	}
	
	public static boolean checkDisplayed(WebDriver driver, String xpath, int waitTime) {
		boolean status = false;
		try {
			WebDriverWait wWait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
//			wWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			status = driver.findElement(By.xpath(xpath)).isDisplayed();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return status;
	}
	
	public static String getCurrentURL(WebDriver driver) {
		return driver.getCurrentUrl();
	}
	
	public static String rgbaToHex(int red, int green, int blue, int alpha) {
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));
        alpha = Math.min(255, Math.max(0, alpha));

        String redHex = Integer.toHexString(red);
        String greenHex = Integer.toHexString(green);
        String blueHex = Integer.toHexString(blue);
        String alphaHex = Integer.toHexString(alpha);

        redHex = (redHex.length() == 1) ? "0" + redHex : redHex;
        greenHex = (greenHex.length() == 1) ? "0" + greenHex : greenHex;
        blueHex = (blueHex.length() == 1) ? "0" + blueHex : blueHex;
        alphaHex = (alphaHex.length() == 1) ? "0" + alphaHex : alphaHex;

        String hexColor = "#" + redHex + greenHex + blueHex + alphaHex;

        return hexColor.toUpperCase(); 
    }
	
}


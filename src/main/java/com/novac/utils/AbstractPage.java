package com.novac.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.novac.driver.TestConfig;
import com.novac.framework.BaseTest;

public abstract class AbstractPage implements BasePage {
	public static WebDriver driver;
	static Logger LOGGER = Logger.getLogger(AbstractPage.class);

	public AbstractPage(WebDriver driver) {
		this.driver = driver;
	}

	public boolean isElementExists(String screenName, String elementKey) {
		LOGGER.info("verifing the presence of " + elementKey + " in " + screenName + " page..");
		return UIUtils.isObjectExist(driver, TestConfig.getInstance().getObjRep().getLocator(screenName, elementKey));
	}

	public boolean isElementEnabled(String screenName, String elementKey) {
		WebElement element = null;

		try {
			element = UIUtils.findElement(driver,
					TestConfig.getInstance().getObjRep().getLocator(screenName, elementKey));
		} catch (Exception e) {
		}

		return element.isEnabled();
	}

	public boolean isClickable(WebElement el) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
			wait.until(ExpectedConditions.elementToBeClickable(el));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String takesScreenshot() throws IOException {
		String imagePath = BaseTest.LOGGER.captureScreen();
		return imagePath;
	}

	public static String takesScreenshotOnflag(String Status, String flag) throws IOException {
		String imagePath = "No//Path";
		if (Status.equalsIgnoreCase("Pass") && flag.equalsIgnoreCase("Y")) {
			imagePath = BaseTest.LOGGER.captureScreen();
			return imagePath;
		} else if (Status.contains("Pass") && flag.contentEquals("N")) {
			return "No//Path";
		} else if (Status.equalsIgnoreCase("FAIL")) {
			imagePath = BaseTest.LOGGER.captureScreen();
			return imagePath;
		}
		return imagePath;
	}

	public String takesScreenshotAt(String screenName, String elementKey) throws IOException {
		WebElement element = null;
		String imagePath = null;

		try {
			element = UIUtils.findElement(driver,
					TestConfig.getInstance().getObjRep().getLocator(screenName, elementKey));
		} catch (Exception e) {
		}
		UIUtils.scrollIntoView(driver, element);
		imagePath = BaseTest.LOGGER.captureScreen();
		return imagePath;
	}

	public void navigateToMenu(String menuList) throws Exception {
		String menus[] = menuList.split(">");
		int index = 1;
		for (String menu : menus) {
			if (index == menus.length) {
				UIUtils.clickElement(driver,
						TestConfig.getInstance().getObjRep().getLocator("Home", "MenuGenericLink", menu));
			} else {
				if (index == 1) {
					if (!UIUtils.isObjectExist(driver,
							TestConfig.getInstance().getObjRep().getLocator("Home", "MenuGeneric", menu))) {
						UIUtils.clickElement(driver,
								TestConfig.getInstance().getObjRep().getLocator("Home", "NavigatorIcon"));
					}
				}
				UIUtils.clickElement(driver,
						TestConfig.getInstance().getObjRep().getLocator("Home", "MenuGeneric", menu));
			}
			index++;
			Thread.sleep(5);
		}
		Thread.sleep(10 * 1000);
	}

	public void navigateToPage(String breadCrum) throws Exception {
		driver.switchTo().defaultContent();
		UIUtils.clickElement(driver, TestConfig.getInstance().getObjRep().getLocator("Home", "NavigatorIcon"));
		navigateToMenu(breadCrum);
	}

	public void goToHome() throws Exception {
		driver.switchTo().defaultContent();
		driver.switchTo().frame("e1menuAppIframe");
		while (UIUtils.isObjectExist(driver,
				TestConfig.getInstance().getObjRep().getLocator("AddStandardReceiptEntry", "selectCancel"))
				|| UIUtils.isObjectExist(driver, TestConfig.getInstance().getObjRep().getLocator("Order", "Cancel"))) {
			if (UIUtils.isObjectExist(driver,
					TestConfig.getInstance().getObjRep().getLocator("AddStandardReceiptEntry", "selectCancel"))) {
				UIUtils.clickElement(driver,
						TestConfig.getInstance().getObjRep().getLocator("AddStandardReceiptEntry", "selectCancel"));
			} else if (UIUtils.isObjectExist(driver,
					TestConfig.getInstance().getObjRep().getLocator("Order", "Cancel"))) {
				UIUtils.clickElement(driver, TestConfig.getInstance().getObjRep().getLocator("Order", "Cancel"));
			}
			Thread.sleep(5000);
			driver.switchTo().defaultContent();
			driver.switchTo().frame("e1menuAppIframe");
		}
		driver.switchTo().defaultContent();
	}

	public static void objWait(By by, String maxDurationInSec) throws IOException {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofSeconds(Integer.parseInt(maxDurationInSec)))
					.pollingEvery(Duration.ofMillis(100)).ignoring(NoSuchElementException.class);

			WebElement el = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
			boolean el2 = wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					boolean isTrue = (boolean) js.executeScript("return window.location.host !== ''");
					return isTrue;
				}
			});

			wait.until((ExpectedConditions.presenceOfElementLocated(by)));
			Thread.sleep(100);
		} catch (Exception e) {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL",
					"Exception occured due to locator issue.Exception:" + e.getMessage());

		}
	}

	public static String addLinebreaks(String input, int maxLineLength) {
		StringBuilder output = new StringBuilder(input.length());
		String[] msgArr = input.split("(?<=\\G.{" + maxLineLength + "})");

		for (int i = 0; i < msgArr.length; i++) {
			output.append(msgArr[i] + "<br>");
		}
		return output.toString();
	}

	
	
}
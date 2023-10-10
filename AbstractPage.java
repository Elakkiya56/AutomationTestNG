package com.slb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.slb.driver.TestConfig;
import com.slb.framework.BaseTest;

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

	public static void selectMenu(String pageName, String objName, String menuName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, menuName));
	}

	/***
	 * @author Manikandan
	 * @date: 10/8/2023
	 */
	public static void select_DateRange_From_Calender(String startDate, String endDate) throws Exception {
		String[] startDateSplit = startDate.split("/");// 15 , MAY , 2023
		String expStartDay = startDateSplit[0], expStartMon = startDateSplit[1], expStartYear = startDateSplit[2];
		String[] endDateSplit = endDate.split("/");
		String expEndDay = endDateSplit[0], expEndMon = endDateSplit[1], expEndYear = endDateSplit[2];
		String actMonYear = UIUtils.getText(driver, UIUtils.locate("DynacardPage", "MonYearDD"));
		if (!(expStartMon.trim() + " " + expStartYear.trim()).equals(actMonYear)) {
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectYearDD"));
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectYear", expStartYear));
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectMon", expStartMon));
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectDate", expStartDay));
		}
		actMonYear = UIUtils.getText(driver, UIUtils.locate("DynacardPage", "MonYearDD"));
		if (!(expEndMon.trim() + " " + expEndYear.trim()).equals(actMonYear)) {
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectYearDD"));
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectYear", expEndYear));
			UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectMon", expEndMon));
		}
		UIUtils.clickElement(driver, UIUtils.locate("DynacardPage", "SelectDate", expEndDay));
	}

	/***
	 * @author Manikandan
	 * @date: 10/8/2023
	 */
	public static void select_DefaultDate(String pageName, String locName, String dateRange) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, locName, dateRange));

	}

	/***
	 * @author Manikandan
	 * @date: 10/8/2023
	 */
	public static void click_Reset_InCalender_Popup(String pageName, String locName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, locName));
	}

	/***
	 * @author Manikandan
	 * @date: 10/8/2023
	 */
	public static void click_Apply_InCalender_Popup(String pageName, String locName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, locName));
	}

	/***
	 * @author Manikandan
	 * @date: 11/8/2023
	 */
	public static boolean verify_Grid_Data(Map<String, String> dataMap, Map<Integer, Map<String, String>> expDataMap,
			String headerXpath, int noOfRows) throws Exception {
		boolean status = false;
		try {
//			Map<Integer, Map<String, String>> expDataMap = new HashMap<>();
			Map<Integer, Map<String, String>> actGridCellValuesMap = UIUtils.getGridCellValues(driver, headerXpath,
					noOfRows);//"WellListPage", "WellListTableHeaders"
			for (int i = 0; i < expDataMap.size(); i++) {
				Map<String, String> actDataMap = actGridCellValuesMap.get(i);
				for (Map.Entry<String, String> e : actDataMap.entrySet()) {
					String actCol = e.getKey();
					String actData = e.getValue();
					if (expDataMap.get(i + 1).get(actCol).equals(actData)) {
						status = true;
					} else {
						status = false;
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	public static Map<Integer, Map<String, String>> getGridData(String headerXpath, int noOfRows) throws Exception {
		select_Pagecount(String.valueOf(noOfRows));
		return UIUtils.getGridCellValues(driver, headerXpath,
				noOfRows);
	}

	/***
	 * @author Manikandan
	 * @date: 18/8/2023
	 */
	public static void verifyPagination(String option) {
		try {
			boolean status = false;
			UIUtils.selectValue(driver, UIUtils.locate("", ""), option);
			String expNoOfPages = "0-" + option.split("=")[1] + "of 100";
			String actNoOfPages = UIUtils.getText(driver, UIUtils.locate("", ""));
			UIUtils.assertThat(expNoOfPages.equals(actNoOfPages), "pagination verified matched with expected",
					"pagination didn't matched with expected " + actNoOfPages);
			int actPageNo = Integer.parseInt(UIUtils.getText(driver, UIUtils.locate("", "")));
			// prev first
			UIUtils.clickElement(driver, UIUtils.locate("", ""));
			actPageNo = Integer.parseInt(UIUtils.getText(driver, UIUtils.locate("", "")));
			UIUtils.assertThat(actPageNo == 1, "page navigated to first page",
					"page not navigated to first page " + actPageNo);
			// next last
			UIUtils.clickElement(driver, UIUtils.locate("", ""));
			actPageNo = Integer.parseInt(UIUtils.getText(driver, UIUtils.locate("", "")));
			UIUtils.assertThat(actPageNo == Integer.parseInt(option.split("=")[1]), "page navigated to last page",
					"page not navigated to last page " + actPageNo);
			for (int i = 1; i <= Integer.parseInt(option.split("=")[1]); i++) {
				actPageNo = Integer.parseInt(UIUtils.getText(driver, UIUtils.locate("", "")));
				if (actPageNo != i) {
					status = false;
					break;
				}
				// next
				UIUtils.clickElement(driver, UIUtils.locate("", ""));
			}
			UIUtils.assertThat(status, "page navigation verified", "page navigation not verified");
			for (int i = Integer.parseInt(option.split("=")[1]); i >= 1; i--) {
				actPageNo = Integer.parseInt(UIUtils.getText(driver, UIUtils.locate("", "")));
				if (actPageNo != i) {
					status = false;
					break;
				}
				// next
				UIUtils.clickElement(driver, UIUtils.locate("", ""));
			}
			UIUtils.assertThat(status, "page navigation verified", "page navigation not verified");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void selectMenu(String expMenuName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate("HomePage", "SelectMenu", expMenuName));
//		String actMenuName = UIUtils.getText(driver, UIUtils.locate("", ""));
//		UIUtils.assertThat(actMenuName.equals(expMenuName), "User navigated to " + actMenuName,
//				"User not navigated to " + actMenuName);
	}

	public static void clickLink(String pageName, String objName, String expName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, expName));
//		String actName = UIUtils.getText(driver, UIUtils.locate("", ""));
//		UIUtils.assertThat(actName.equals(expName), "User selected particular link " + actName,
//				"User not selected particular link " + actName);
	}

	public static void sortByWellName(String pageName, String objName, String fieldName, String wellName)
			throws Exception {
		verifyArrowIcon(fieldName);
		Thread.sleep(2000);
		UIUtils.clickElementJScript(driver, UIUtils.locate("WellListPage", "SortByWellNameDD"));
		if (wellName.contains("|")) {
			String[] splitWellName = wellName.split("\\|");
			for (int i = 0; i < splitWellName.length; i++) {
				selectWell(pageName, objName, splitWellName[i]);
			}
		}else {
			selectWell(pageName, objName, wellName);
		}
	}
	
	public static void selectWell(String pageName, String objName, String wellName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, wellName));
		String attribute = UIUtils.getAttribute(driver, pageName, objName, wellName, "aria-selected");
		if(attribute.equals("true")) {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "well selected " + wellName, BaseTest.LOGGER.captureScreen());
		}else {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "well not selected " + wellName, BaseTest.LOGGER.captureScreen());
		}
	}

	public static void clickApplyFilter(String pageName, String objName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate(pageName, objName));
	}

	public static void selectFilter(String pageName, String objName, String option, String fieldName) throws Exception {
		if (option.contains("|")) {
			String[] split = option.split("\\|");
			for (int i = 0; i < split.length; i++) {
				selectFilterOptions(pageName, objName, split[i], fieldName);
			}
		} else {
			selectFilterOptions(pageName, objName, option, fieldName);
		}
	}

	public static void select_Filter(Map<String, String> dataMap) throws Exception {
		Thread.sleep(3000);
		if (!dataMap.get("Comms Status").trim().equals("")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Comms Status"));
			selectFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Comms Status"), "Comms Status");
			checkFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Comms Status"));
		}
		if (!dataMap.get("Controller Status").trim().equals(" ")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Controller Status"));
			selectFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Controller Status"), "Controller Status");
			checkFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Controller Status"));
		}
		if (!dataMap.get("Pumping Type").trim().equals("")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Pumping Type"));
			selectFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Pumping Type"), "Pumping Type");
			checkFilter("WellListPage", "FilterOptionCheckbox", dataMap.get("Pumping Type"));
		}
		if (!dataMap.get("SPM").trim().equals("")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "SPM"));
			moveSlider("spmSlider", dataMap.get("SPM"), "SPM");
		}
		if (!dataMap.get("Pump Fillage").trim().equals("")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Pump Fillage"));
			moveSlider("pumpFillage", dataMap.get("Pump Fillage"), "Pump Fillage");
		}
		if (!dataMap.get("Inferred Production (bpd)").trim().equals("")) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Inferred Production (bpd)"));
			moveSlider("inferredProduction", dataMap.get("Inferred Production (bpd)"), "Inferred Production (bpd)");
		}
	}
	
	public static void checkFilter(String pageName, String objName, String elementName) throws Exception {
		String status = UIUtils.getAttribute(driver, pageName, objName, elementName, "ng-reflect-checked");
		if(status.equals("true")) {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "filter option selected " + elementName, BaseTest.LOGGER.captureScreen());
		}else {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "filter option not selected " + elementName, BaseTest.LOGGER.captureScreen());
		}
	}

	public static void moveSlider(String sliderName, String sliderValue, String fieldName)
			throws NumberFormatException, Exception {
		verifyArrowIcon(fieldName);
		int expStart = 0;
		int expEnd = 0;
		if (sliderValue.contains("|")) {
			String[] split = sliderValue.split("\\|");
			expStart = Integer.parseInt(split[0].split("=")[1]);
			expEnd = Integer.parseInt(split[1].split("=")[1]);
		} else {
			expStart = Integer.parseInt(sliderValue.split("=")[1]);
			expEnd = Integer.parseInt(sliderValue.split("=")[1]);
		}
		WebElement startSlider = UIUtils.findElement(driver,
				UIUtils.getLocatorXpath("WellListPage", "Slider", sliderName+"Start"));
		WebElement endSlider = UIUtils.findElement(driver,
				UIUtils.getLocatorXpath("WellListPage", "Slider", sliderName+"End"));
		int i = 0;
		while (i == 0) {
			int actStart = Integer.parseInt(
					UIUtils.getText(driver, UIUtils.locate("WellListPage", "SliderStartValue", fieldName)).trim().replaceAll("[^0-9]", ""));
			if (actStart <= expStart) {
				if (actStart == expStart) {
					break;
				}
				startSlider.sendKeys(Keys.ARROW_RIGHT);
			} else {
				if (actStart == expStart) {
					break;
				}
				startSlider.sendKeys(Keys.ARROW_LEFT);
			}
		}
		while (i == 0) {
			int actEnd = Integer.parseInt(
					UIUtils.getText(driver, UIUtils.locate("WellListPage", "SliderEndValue", fieldName)).trim().replaceAll("[^0-9]", ""));
			if (actEnd >= expEnd) {
				if (actEnd == expEnd) {
					break;
				}
				endSlider.sendKeys(Keys.ARROW_LEFT);
			} else {
				if (actEnd == expEnd) {
					break;
				}
				endSlider.sendKeys(Keys.ARROW_RIGHT);
			}
		}
	}

	public static void verifyArrowIcon(String fieldName) throws Exception {
		String attribute = UIUtils.getAttribute(driver, "WellListPage", "ExpandArrow", fieldName, "aria-expanded");
		if (attribute.equals("false")) {
			UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ExpandArrow", fieldName));
		}
	}

	public static void selectFilterOptions(String pageName, String objName, String option, String fieldName)
			throws Exception {
		verifyArrowIcon(fieldName);
		if (option.contains("|")) {
			String[] split = option.split("\\|");
			for (int i = 0; i < split.length; i++) {
				UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, split[i]));
			}
		}else {
			UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, option));
		}
	}

	public static void verifyLegends(String legends) throws Exception {
		String[] legendSplit = legends.split("\\|");
		Map<String, Integer> count = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < legendSplit.length; i++) {
			String actText = UIUtils.getText(driver, UIUtils.locate("WellListPage", "Legend", legendSplit[i]));
			int actCount = Integer.parseInt(actText.replaceAll("[^0-9]", ""));
			count.put(legendSplit[i], actCount);
		}
		UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "FilterTab"));
		for (int i = 0; i < legendSplit.length; i++) {
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ExpandArrow", "Pumping Type"));
			verifyArrowIcon("Pumping Type");
			UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "PumpingType", legendSplit[i].split("=")[0]));
			UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ApplyFilterBtn"));
			Thread.sleep(2000);
			UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ItemsPerPageDD"));
			UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ItemsPerPageDD"));
			UIUtils.clickElement(driver,
					UIUtils.locate("WellListPage", "ItemsCountInPage", String.valueOf(count.get(legendSplit[i]))));
			int expCount = UIUtils.findMultipleElements(driver, UIUtils.locate("WellListPage", "TableRowCount")).size();
			UIUtils.assertThat(expCount == count.get(legendSplit[i]), "Actual count matched with expected count",
					"Actual count not matched with expected count");
		}
	}

	public static void clickOnFilter() throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "FilterTab"));
	}

	/***
	 * @author Manikandan
	 * @date: 15/9/2023
	 */
	public static void verifyDate(String expStartDate, String expEndDate, String date) {
		String[] split = date.split("TO");
		String start = split[0].trim();
		start = start.substring(0, start.lastIndexOf(':'));
		String end = split[1].trim();
		end = end.substring(0, end.lastIndexOf(':'));
		String startDate = "";
		String endDate = "";
		if (expStartDate.contains("/")) {
			startDate = DateTimeUtils.convertDateInNumFormat(expStartDate);
			endDate = DateTimeUtils.convertDateInNumFormat(expEndDate);
		} else {
			startDate = expStartDate;
			endDate = expEndDate;
		}
		String actDate = start + end;
		String expDate = startDate + endDate;
		UIUtils.assertThat(actDate.equals(expDate), "Actual date "+actDate+" range matched with expected date "+expDate+" range",
				"Actual date "+actDate+" range not matched with expected date "+expDate+" range");
	}

	/***
	 * @author Manikandan
	 * @date: 15/9/2023
	 */
	public void checkDefaultDateRange(String defaultDate) throws Exception {
		select_DefaultDate("DynacardPage", "DefaultDateBtn", defaultDate);
		String actDate = UIUtils.getText(driver, UIUtils.locate("DynacardPage", "DateRange"));
		String startDate = null;
		String endDate = DateTimeUtils.getDate("today=").toString().replace("T", " ");
		endDate = endDate.substring(0, endDate.lastIndexOf(':'));
		String dateRange = defaultDate;
		if (dateRange.equals("3m")) {
			String d = DateTimeUtils.getDate("minus=90").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("1m")) {
			String d = DateTimeUtils.getDate("minus=30").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("1w")) {
			String d = DateTimeUtils.getDate("minus=7").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("1d")) {
			String d = DateTimeUtils.getDate("minus=1").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("12h")) {
			String d = DateTimeUtils.getDate("minushours=12").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("4h")) {
			String d = DateTimeUtils.getDate("minushours=4").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		} else if (dateRange.equals("1h")) {
			String d = DateTimeUtils.getDate("minushours=1").toString().replace("T", " ");
			startDate = d.substring(0, d.lastIndexOf(':'));
		}
		verifyDate(startDate, endDate, actDate);
	}

	public static void select_Pagecount(String count) throws Exception {
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "ItemsPerPageDD"));
		UIUtils.clickElementJScript(driver, UIUtils.locate("WellListPage", "ItemsPerPageDD"));
		UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ItemsCountInPage", count));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "selected page count as " + count, BaseTest.LOGGER.captureScreen());
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "WellListTableHeaders"));
	}

	public static void search_Wellname(String wellName) {
		UIUtils.inputText(driver, UIUtils.locate("WellListPage", "SearchWellInput"), wellName);
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "data entered as " + wellName,
				BaseTest.LOGGER.captureScreen());
	}

	public static boolean verifySearch(int index, int count, String wellName) {
		List<String> data = UIUtils.getGridDataByCol(driver, index, count);
		boolean status = true;
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).contains(wellName)) {
				status = false;
				break;
			}
		}
		return status;
	}

	public static int getCountOfData() throws Exception {
		String text = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split = text.split(" ");
		return Integer.parseInt(split[split.length - 1]);
	}

	public static void editColumn(String colName) throws Exception {
		UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "EditColIcon"));
		boolean status = false;
		if (colName.contains("|")) {
			String[] splitCol = colName.split("\\|");
			for (int i = 0; i < splitCol.length; i++) {
				UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ColCheckBox", splitCol[i]));
				String attribute = UIUtils.getAttribute(driver, "WellListPage", "ColCheckBox", splitCol[i], "class");
				if(attribute.contains("checkbox-checked")) {
					status = true;
				}else {
					status = false;
					break;
				}
			}
		} else {
			UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "ColCheckBox", colName));
			String attribute = UIUtils.getAttribute(driver, "WellListPage", "ColCheckBox", colName, "class");
			status = attribute.contains("checkbox-checked");
		}
		if(status) {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "all columns selected",
					BaseTest.LOGGER.captureScreen());
		}else {
			BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "FAIL", "all columns are not selected",
					BaseTest.LOGGER.captureScreen());
		}
		UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "EditColCloseIcon"));
	}

	public static boolean verifyColumn(String pageName, String objName, String colName) {
		List<String> actHeaders = UIUtils.getGridHeaders(driver, UIUtils.getLocatorXpath(pageName, objName));
		boolean status = true;
		if(colName.contains("|")) {
			String[] split = colName.split("\\|");
			for (int i = 0; i < split.length; i++) {
				if (!actHeaders.contains(split[i])) {
					status = false;
					break;
				}
			}
		}else {
			if (!actHeaders.contains(colName)) {
				status = false;
			}
		}
		return status;
	}
	
	public static void selectAllColumn() {
		
	}
	
	public static void clickPageNavIcon(String nav) throws Exception {
		UIUtils.clickElementJScript(driver, UIUtils.locate("WellListPage", "PageNavIcon", nav));
	} 

	public static void verifyNextPageNavigation() throws Exception {
//		select_Pagecount("10");
		String text1 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split1 = text1.trim().split(" ");
		int initCount = Integer.parseInt(split1[0]);
		clickPageNavIcon("Next page");
		String text2 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split2 = text2.split(" ");
		int afterCount = Integer.parseInt(split2[0]);
		UIUtils.assertThat(afterCount==initCount+10, "Navigated to next page " + afterCount, "not navigated to next page " + afterCount);
		Thread.sleep(2000);
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "WellListTableHeaders"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "data showed", BaseTest.LOGGER.captureScreen());
	}
	
	public static void verifyPrevPageNavigation() throws Exception {
		String text1 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split1 = text1.trim().split(" ");
		int initCount = Integer.parseInt(split1[0]);
		clickPageNavIcon("Previous page");
		String text2 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split2 = text2.split(" ");
		int afterCount = Integer.parseInt(split2[0]);
		UIUtils.assertThat(afterCount==initCount-10, "Navigated to prev page " +afterCount, "not navigated to prev page " +afterCount);
		Thread.sleep(2000);
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "WellListTableHeaders"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "data showed", BaseTest.LOGGER.captureScreen());
	}
	
	public static void verifyFirstPageNavigation() throws Exception {
//		select_Pagecount("10");
//		clickPageNavIcon("Last page");
		String text1 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "initial page count", BaseTest.LOGGER.captureScreen());
		String[] split1 = text1.trim().split(" ");
		int initCount = Integer.parseInt(split1[0]);
		int totalCount = getCountOfData();
		int afterCount = 0;
		if(initCount==totalCount) {
			clickPageNavIcon("First page");
			String text2 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
			String[] split2 = text2.split(" ");
			afterCount = Integer.parseInt(split2[0]);
		}
		UIUtils.assertThat(afterCount==1, "Navigated to first page", "not navigated to first page");
		Thread.sleep(2000);
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "WellListTableHeaders"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "data showed", BaseTest.LOGGER.captureScreen());
	}
	
	public static void verifyLastPageNavigation() throws Exception {
//		select_Pagecount("10");
		String text1 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "initial page count", BaseTest.LOGGER.captureScreen());
		String[] split1 = text1.trim().split(" ");
		int initCount = Integer.parseInt(split1[0]);
		int totalCount = getCountOfData();
		int afterCount = 0;
		if(initCount==1) {
			clickPageNavIcon("Last page");
			String text2 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
			String[] split2 = text2.split(" ");
			afterCount = Integer.parseInt(split2[0]);
		}
		UIUtils.assertThat(afterCount==totalCount, "Navigated to last page", "not navigated to last page");
		Thread.sleep(2000);
		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "WellListTableHeaders"));
		BaseTest.LOGGER.logTestStep(BaseTest.extentTest, "PASS", "data showed", BaseTest.LOGGER.captureScreen());
	}
	
	public static void navigateToParticularPage(String page) throws Exception {
		String text1 = UIUtils.getText(driver, UIUtils.locate("WellListPage", "ItemsCount"));
		String[] split1 = text1.trim().split(" ");
		int initCount = Integer.parseInt(split1[0]);
		int expCount = Integer.parseInt(page);
		while(initCount > expCount) {
			
		}
	}
	
	public static Map<String, String> verifyWellinfoTooltip(String toolTipInfo) throws Exception {
		Map<String, String> tooltipInfoMap = new LinkedHashMap<>();
		String[] expInfo = null;
		boolean status = false;
		if(toolTipInfo.contains("|")) {
			expInfo = toolTipInfo.split("\\|");
		}
		List<WebElement> elements = UIUtils.findMultipleElements(driver, UIUtils.locate("WellListPage", "ToolTipIcon"));
		UIUtils.scrollIntoView(driver, elements.get(6));
		for(int i=0; i<elements.size(); i++) {
			UIUtils.holdElement(driver, elements.get(i));
			String actInfo = UIUtils.getText(driver, UIUtils.locate("WellListPage", "TooltipInfo"));
			if(expInfo[i].equals(actInfo)) {
				status = true;
				String[] info = actInfo.split("=");
				tooltipInfoMap.put(info[0], info[1]);
			}else {
				status = false;
				break;
			}
		}
		UIUtils.assertThat(status, "Actual tooltip info matched with expected info", "Actual tooltip info not matched with expected info");
		return tooltipInfoMap;
	}
	
	public static void getWellTooltipInfo() {
		
	}
	
	public static String getCountOfRowsSelected() throws Exception {
		return UIUtils.getText(driver, UIUtils.locate("WellListPage", "SelectedCount"));
	}
	
	public static void deleteFilter(String field) throws Exception {
//		UIUtils.scrollIntoView(driver, UIUtils.locate("WellListPage", "DeleteFilterIcon", field));
		UIUtils.clickElementJScript(driver, UIUtils.locate("WellListPage", "DeleteFilterIcon", field));
	}
	
	public void verifyGridDataBasedOnColumn(String colName, String expData) throws Exception {
		Map<Integer, Map<String, String>> gridData = getGridData(
				UIUtils.getLocatorXpath("WellListPage", "WellListTableHeaders"), 5);
		boolean status = false;
		String failedData = "";
		for (Entry<Integer, Map<String, String>> entry : gridData.entrySet()){
			Integer row = entry.getKey();
			Map<String, String> gridata = entry.getValue();
			for (Entry<String, String> entry2 : gridata.entrySet()) {
				String actColName = entry2.getKey();				
				if(actColName.contains(colName)) {
					String data = entry2.getValue();
					if(expData.contains(data)) {
						status = true;
					}else {
						status = false;
						failedData = data;
						break;
					}
				}
			}	
		}
		UIUtils.assertThat(status, "Grid data matched", "Grid data not matched " + failedData);
	}
	
	public static boolean verifyMenu(String menuName, String expURL) {
		boolean status = driver.getCurrentUrl().equals(expURL);
		UIUtils.assertThat(status, "User navigated to "+menuName+ " menu", "User not navigated to "+menuName+ " menu");
		return status;
	}
	
	public static void filterWellList(String objName, String fields) throws Exception {
		//alpha|lambda|padpi=apache1,apache2|padsig=apache3
		//zeta|lambda|padpi=apache1,apache2|padsig=apache3
		String[] fieldsArray = null;
		if(fields.contains("|")) {
			fieldsArray = fields.split("\\|");
			verifyExpandArrowIcon("WellListPage", "WellListFilterHeadExpand", fieldsArray[0]);//alpha
			verifyExpandArrowIcon("WellListPage", "WellListFilterHeadExpand", fieldsArray[1]);//lambda
			for(int i=2; i<fieldsArray.length; i++) {
				if(fieldsArray[i].contains("=")) {
					String[] pads = fieldsArray[i].split("=");//padpi=apache1,apache2
					verifyExpandArrowIcon("WellListPage", objName, pads[0]);//padpi
					if(pads[1].contains(",")) {
						String[] wells = pads[1].split(",");//apache1,apache2
						for(int j=0; j<wells.length; j++) {
							UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "WellCheckBox", wells[i]));
						}
					}else {
						UIUtils.clickElement(driver, UIUtils.locate("WellListPage", "WellCheckBox", pads[1]));	
					}
				}
			}
		}	
	}
	
	public static void verifyExpandArrowIcon(String pageName, String objName, String element) throws Exception {
		String text = UIUtils.getText(driver, UIUtils.locate(pageName, objName, element)).trim();
		if(text.equals("chevron_right")) {
			UIUtils.clickElement(driver, UIUtils.locate(pageName, objName, element));
		}
	}
	
//	public static void validateDataa(String fileName) {
//		String expJsonData = getExpDataFromJsonFile(APIBaseTest.JSON_FOLDER_PATH+fileName).toString();
//		String actJsonData = response.getBody().toString();
//		Gson gson = new Gson();
//	    Type alertListType = new TypeToken<List<AlertsPage>>() {}.getType();
//	    List<AlertsPage> expAlertsDataList = gson.fromJson(expJsonData, alertListType);
//	    List<AlertsPage> actAlertsDataList = gson.fromJson(actJsonData, alertListType);
//	    for(int i=0; i<expAlertsDataList.size(); i++) {
//	    	System.out.println("ActAlertId: " + actAlertsDataList.get(i).getAlertId()+" = expctAlertId: " + expAlertsDataList.get(i).getAlertId());
//	    }
//	}
	
	public static boolean verifyFileDownload(String fileName) {
		boolean status = false;
		System.out.println(fileName);
		File f = new File(System.getProperty("user.home")+ File.separator+"Downloads");
		File[] listFiles = f.listFiles();
		for (File file : listFiles) {
			if(file.getName().equals(fileName)) {
				status = true;
			}
		}
		return status;
	}
	
	public static void verifyDataInDownloadedFile(String fileName, int count) throws Exception {
		Map<Integer, Map<String, String>> excelData = new LinkedHashMap<>();
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File(System.getProperty("user.home")+ File.separator+"Downloads"+ File.separator+fileName)));
		XSSFSheet sheet = wb.getSheet("Sheet1");
		for(int i=1; i<sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			Map<String, String> data = getData(sheet, row);
			excelData.put(i, data);
		}
		Map<Integer, Map<String, String>> gridData = getGridData(
				UIUtils.getLocatorXpath("WellListPage", "WellListTableHeaders"), count);
		boolean status = false;
		for(int i=0; i<gridData.size(); i++) {
			status = gridData.get(i).get("Well Name").equals(excelData.get(i).get("Well Name"));
			status = gridData.get(i).get("Time & Date").equals(excelData.get(i).get("Time & Date"));
			status = gridData.get(i).get("Comms Status").equals(excelData.get(i).get("Comms Status"));
			status = gridData.get(i).get("Controller Status").equals(excelData.get(i).get("Controller Status"));
			status = gridData.get(i).get("Avg SPM").equals(excelData.get(i).get("Avg SPM"));
			status = gridData.get(i).get("Avg Pump Fillage").equals(excelData.get(i).get("Avg Pump Fillage"));
			status = gridData.get(i).get("Avg Inferred Production(bpd)").equals(excelData.get(i).get("Avg Inferred Production(bpd)"));
			status = gridData.get(i).get("No.Of Alerts").equals(excelData.get(i).get("No.Of Alerts"));
			status = gridData.get(i).get("Effective Runtime(%)").equals(excelData.get(i).get("Effective Runtime(%)"));
			status = gridData.get(i).get("Cycles Today").equals(excelData.get(i).get("Cycles Today"));
			status = gridData.get(i).get("Structural Load(%)").equals(excelData.get(i).get("Structural Load(%)"));
			status = gridData.get(i).get("MinMax Load(%)").equals(excelData.get(i).get("MinMax Load(%)"));
			status = gridData.get(i).get("Gearbox Load(%)").equals(excelData.get(i).get("Gearbox Load(%)"));
			status = gridData.get(i).get("Rod Stress(%)").equals(excelData.get(i).get("Rod Stress(%)"));
		}
		if(status) {
			System.out.println("data verified");
		}
	}
	
//	public static void iterateMap(Map<Integer, Map<String, String>> actData, Map<Integer, Map<String, String>> expData) {
//		for (Entry<Integer, Map<String, String>> entry : gridData.entrySet()) {
//			int key = entry.getKey();
//			Map<String, String> val = entry.getValue();
//			for (Entry<String, String> entry2 : val.entrySet()) {
//				String expColName = entry2.getKey();
////				String expData = entry2.getValue();
//				
//			}
//		}
//	}
	
	public static Map<String, String> getData(XSSFSheet sheet, XSSFRow row) {
		Map<String, String> daMap = new LinkedHashMap<>();
		for(int j=1; j<row.getLastCellNum(); j++) {
			String data = "";
			String colName = sheet.getRow(0).getCell(j).getStringCellValue();				
			CellType cellType = row.getCell(j).getCellType();
			if(cellType.toString().equals("STRING")) {
				data = row.getCell(j).getStringCellValue();
			}else {
				if(colName.equals("Time & Date")) {
					data = String.valueOf(row.getCell(j).getDateCellValue());
					String[] split = data.split(" ");
					String monInNum = DateTimeUtils.getMonInNum(split[1].toUpperCase());
					data = split[5] + "-" + monInNum + "-" + split[2] + " " + split[3];
					System.out.println(data);
				}else {
					data = String.valueOf(row.getCell(j).getNumericCellValue());
				}
			}
			daMap.put(colName, data);
			System.out.println(colName + " = " + data);
		}
		return daMap;
	}
	
	public Map<String, String> getColorOfData(List<String> actData) throws Exception {
		Map<String, String> colorOfWell = new LinkedHashMap<>();
		List<String> color = getColorOfElement("AlertsPage", "AlertsTable");
		for(int i=0; i<actData.size(); i++) {
			colorOfWell.put(actData.get(i), color.get(i));
		}
		return colorOfWell;
	}
	
	public static List<String> getColorOfElement(String pageName, String objName) throws Exception {
		int countOfData = getCountOfData();
		return UIUtils.getGridDataColor(driver, UIUtils.getLocatorXpath(pageName, objName), 1, countOfData);
	}
	
}
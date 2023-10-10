package com.novac.driver;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.novac.framework.ObjectRepository;
import com.novac.utils.Config;
import com.novac.utils.Constants;
import com.novac.utils.ExcelObject;

public class TestConfig {
	static Logger LOGGER = Logger.getLogger(TestConfig.class);

	private static TestConfig testConfig;

	private static Config config;
	private static Config dataConfig;
	private static String configWorkbook;

	private ObjectRepository objRep;
	private String testModulesPath;

	private String reportPath;
	private String screenShotPath;

	private boolean remoteExecution = false;
	private String gridURL;

	private String execEnvironment;

	private String deviceResolution;
	private int FailureRetryCount;

	private String testModuleName;

	private TestConfig() {
	}

	static {
		try {
			config = new Config(Constants.ConfigPath);
			dataConfig = new Config(Constants.DataPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// System.setProperty("log4j.configurationFile", "log4j.properties");
		PropertyConfigurator.configure("log4j.properties");

		try {
			configWorkbook = new File(Constants.ConfigXls).getCanonicalPath();
		} catch (IOException e) {
			LOGGER.error("Unable to find Config workbook", e);
		}
	}

	public static TestConfig getInstance() {
		if (testConfig == null) {
			testConfig = new TestConfig();
		}
		return testConfig;
	}

	public static Config getConfig() {
		return config;
	}

	public static Config getDataConfig() {
		return dataConfig;
	}

	public String getDeviceResolution() {
		return deviceResolution;
	}

	public static String getConfigWorkbook() {
		return configWorkbook;
	}

	public ObjectRepository getObjRep() {
		return objRep;
	}

	public String getReportPath() {
		return reportPath;
	}

	public String getScreenShotPath() {
		return screenShotPath;
	}

	public String getTestModulesPath() {
		return testModulesPath;
	}

	public boolean isRemoteExecution() {
		return remoteExecution;
	}

	public String getGridURL() {
		return gridURL;
	}

	public String getExecEnvironment() {
		return execEnvironment;
	}

	public int getFailureRetryCount() {
		FailureRetryCount = Integer.parseInt(config.getPropertyValue("FailureRetryCount"));
		return FailureRetryCount;
	}

	public static String getModuleName() throws IOException {
		if (System.getProperty("moduleName") == null) {
//			System.out.println("Module Name is--" +config.getPropertyValue("moduleName"));
			return config.getPropertyValue("moduleName");

		} else {

//		System.out.println("Module Name is--" + System.getProperty("moduleName")); 
			return System.getProperty("moduleName");

		}
	}

	public String getTestModuleName() {
		return config.getPropertyValue("TestModuleName");
	}

	// Framework Initialization
	public void suiteSetup(String database) {
		try {
			frameworkSetup();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getClass().getName() + " caught from suite setup method", e);
		}
	}

	// Framework Initialization
	public void suiteSetup() {
		try {
			frameworkSetup();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getClass().getName() + " caught from suite setup method", e);
		}
	}

	private void frameworkSetup() throws IOException {
		ExcelObject tcExcel = new ExcelObject(configWorkbook, "Config");

		if(TestConfig.getInstance().getTestModuleName().equals("UI")) {
			objRep = new ObjectRepository("Framework/ObjectRepository/" + getModuleName() + "/ObjectRepository.xlsx");
		}
//		objRep = new ObjectRepository("Framework\\ObjectRepository\\ObjectRepository.xlsx");
		reportPath = String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=ReportsPath")).trim();
		screenShotPath = String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=ScreenshotPath")).trim();
		testModulesPath = new File("Framework/Test_Scripts").getCanonicalPath();
		deviceResolution = String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=Resolution")).trim();
		setTestModuleName(String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=Resolution")).trim());

		if ("Yes".equalsIgnoreCase(
				String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=RemoteExecution")).trim())) {
			remoteExecution = true;
		}

		gridURL = String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=GridURL")).trim();

		execEnvironment = String.valueOf(tcExcel.getCellValue("Config", "Value", "Key=Environment")).trim();

		tcExcel.closeWorkbook();
	}

	public void setTestModuleName(String testModuleName) {
		this.testModuleName = testModuleName;
	}

	
}
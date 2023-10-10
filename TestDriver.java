package com.slb.driver;

import org.openqa.selenium.WebDriver;

import com.slb.utils.UIUtils;

public class TestDriver {
	public static WebDriver driverInstantiation(String browserName) throws Exception {
		WebDriver driver;

		if (TestConfig.getInstance().isRemoteExecution()) {
			driver = UIUtils.createDriverInstance(browserName, "", TestConfig.getInstance().getGridURL());
		} else {
			driver = UIUtils.createDriverInstance(browserName, "");
		}

		return driver;
	}
}
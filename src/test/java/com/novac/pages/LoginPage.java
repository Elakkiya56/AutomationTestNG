package com.novac.pages;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.novac.utils.UIUtils;

public class LoginPage {
	
	WebDriver driver;
	Map<String, String> dataMap;
	
	public LoginPage(WebDriver driver, Map<String, String> dataMap) {
		this.driver = driver;
		this.dataMap = dataMap;
	}
	
	public void login() throws Exception {
		UIUtils.inputText(driver, UIUtils.locate("", ""), dataMap.get(""));
		UIUtils.inputText(driver, UIUtils.locate("", ""), dataMap.get(""));
		UIUtils.clickElement(driver, UIUtils.locate("", ""));
	}

}

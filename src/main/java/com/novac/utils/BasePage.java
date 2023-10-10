package com.novac.utils;

public interface BasePage {
	public boolean isPageOpen();

	public boolean isElementExists(String screenName, String elementKey);

	public boolean isElementEnabled(String screenName, String elementKey);

}
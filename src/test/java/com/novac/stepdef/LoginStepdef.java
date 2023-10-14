package com.novac.stepdef;

import java.util.Map;

import com.novac.driver.TestConfig;
import com.novac.framework.BaseTest;
import com.novac.pages.LoginPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginStepdef extends BaseTest{
	
	Map<String, String> dataMap;
	
	LoginPage loginPage = new LoginPage(driver, dataMap);
	
	
	@Given("^launch the URL$")
	public void launch_the_url() throws Exception {
		loginPage.login();
	}
	
	@When("login with valid credentials")
	public void login_with_valid_credentials() {
	    
	}
	
	@When("click on login btn")
	public void click_on_login_btn() {
	    
	}
	
	@Then("validate the login")
	public void validate_the_login() {
	    
	}
	
}

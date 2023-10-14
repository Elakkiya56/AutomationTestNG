package com.novac.runner;

import org.junit.runner.RunWith;

import com.novac.framework.BaseTest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "C:\\Users\\raja\\eclipse-workspace\\JAVA-OCT\\TestAutomation\\src\\test\\resources\\LoginFeature\\login.feature", monochrome = true
, glue = {"com.novac.stepdef.LoginStepdef"}, dryRun = true)
public class TestRunner {
	
	

}

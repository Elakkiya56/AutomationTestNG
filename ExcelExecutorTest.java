package com.slb.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.slb.driver.TestConfig;
import com.slb.framework.Myretry;

public class ExcelExecutorTest implements IAnnotationTransformer {

	private Map<String, Integer> activatedTest;
	private static Logger LOGGER = Logger.getLogger(UIUtils.class);
    Config config= new Config(Constants.ConfigPath);
	
	public ExcelExecutorTest() throws IOException {
//		activatedTest = new ArrayList<>();
		if(TestConfig.getInstance().getTestModuleName().equals("UI")) {
			ExcelObject obj = new ExcelObject("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestCases.xlsx",TestConfig.getModuleName());
			activatedTest =obj.excelTestNameRead("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestCases.xlsx",TestConfig.getModuleName());
		}else if(TestConfig.getInstance().getTestModuleName().equals("API")) {
			ExcelObject excelobj = new ExcelObject("Framework/Test_Data/"+TestConfig.getModuleName()+"/APITestCases.xlsx",TestConfig.getModuleName());
			activatedTest =excelobj.excelTestNameRead("Framework/Test_Data/"+TestConfig.getModuleName()+"/APITestCases.xlsx",TestConfig.getModuleName());
			System.out.println(activatedTest.keySet());
		}
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		String CmdTestValue = System.getProperty("cmdtestname")!=null?System.getProperty("cmdtestname"):"";

		if(CmdTestValue != "") {
			ArrayList<String> list = new ArrayList<String>();
			CollectionUtils.addAll(list, CmdTestValue.split(","));
			if(list.contains(testMethod.getName())) {
				annotation.setPriority(activatedTest.get(testMethod.getName()));
				annotation.setEnabled(true);
			}else {
				annotation.setEnabled(false);
			}
		}
		else if (activatedTest.containsKey(testMethod.getName())) {
				annotation.setPriority(activatedTest.get(testMethod.getName()));
				annotation.setEnabled(true);
			} else {
				annotation.setEnabled(false);
			}
		
		annotation.setRetryAnalyzer(Myretry.class);
		
	}
}
package com.slb.testdataproviders;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import com.slb.driver.TestConfig;
import com.slb.utils.Config;
import com.slb.utils.Constants;
import com.slb.utils.DataLoaders;
import com.slb.utils.ExcelObject;
import com.slb.utils.UIUtils;

public class TestDataProviders {
	private static Config config;
	private static Logger LOGGER = Logger.getLogger(UIUtils.class);
	static {
		try {
			config = new Config(Constants.ConfigPath);
			
		} catch (IOException e) {
			LOGGER.error(e.getClass().getSimpleName(), e);
		}
		}
	
	
	@DataProvider(name="TestData")
	public Object[][] getTestData(Method name) {
		try {
			//Object[][] testData = DataLoaders.excelDataLoader("Framework/Test_Data/TestData.xlsx", name.getName());
			Object[][] testData = DataLoaders.excelDataLoader("Framework/Test_Data/TestData.xlsx", "Sheet1");
			return testData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@DataProvider(name="TestDataInMap")
	public Object[][] getTestDataInMap(Method method) throws IOException {
		Object[][] obj = new Object[1][1];
		ExcelObject excelobj = new ExcelObject("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestCases.xlsx",TestConfig.getModuleName());
		try {
			Map<String, Map<String, String>> testData = DataLoaders.excelDataLoaderInMap("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestData.xlsx",excelobj.excelSheetName(method) , method);
			Set<String> keys = testData.keySet();
			for(String key:keys) {
				if(key.equalsIgnoreCase(excelobj.excelSheetName(method))) {
					obj[0][0] = testData.get(key);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@DataProvider(name="MultiTestData")
    public Object[] getMultiTestData(Method method) throws IOException {
		ExcelObject excelobj = new ExcelObject("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestCases.xlsx",TestConfig.getModuleName());
		try {
			Map<String, Map<String, String>> testData = DataLoaders.excelDataLoaderMulti("Framework/Test_Data/"+TestConfig.getModuleName()+"/TestData.xlsx",excelobj.excelSheetName(method) , method.getName());
            
                 Set<String> keys = testData.keySet();
                 int rows = keys.size();
                 int column = 0;
                 Object[] retObjArray = new Object[rows];
                 int index = 0;
                 for(String key:keys) {
                     retObjArray[index] = testData.get(key);
                     index++;
                 }
                 return retObjArray;
          } catch (IOException e) {
                 e.printStackTrace();
          }
          return null;
    }

}
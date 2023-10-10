package com.novac.driver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.novac.utils.ExcelObject;

public class TestData {

	//Common method for parametrization.
	public static Map<String, String> getData(String s) throws IOException {
		ExcelObject excelObject = new ExcelObject(new File("Framework/Test_Data/TestData.xlsx").getCanonicalPath());
		List<List<Object>> queryResult = excelObject.getExcelData(s);
		excelObject.closeWorkbook();
		Map<String, String> testData = new HashMap<String, String>();
		for (int row = 1; row < queryResult.size(); row++) {
			List<Object> objInfo = queryResult.get(row);
			for (int column = 0; column < objInfo.size(); column++) {
				String value = "";
				if(objInfo.get(column).toString().contains(".0")) {
					value = objInfo.get(column).toString().replace(".0", "");
				} else {
					value = objInfo.get(column).toString();
				}
				testData.put(queryResult.get(0).get(column).toString(), value);
			}
		}
		return testData;
	}
	

}

package com.novac.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoaders {
	public static Object[][] excelDataLoader(String excelPath) throws IOException {
		ExcelObject testDataObj = new ExcelObject(excelPath);
		return excelDataLoader(excelPath, testDataObj.getActiveSheet().getSheetName());
	}

	public static Object[][] excelDataLoader(String excelPath, String sheetName) throws IOException {

		ExcelObject testDataObj = new ExcelObject(excelPath);
		List<List<Object>> data = testDataObj.getExcelData(sheetName);
		testDataObj.closeWorkbook();
		Object[][] objects = new Object[data.size() - 1][];
		int i = 0;
		for (int ctr = 1; ctr < data.size(); ctr++) {
			List<Object> dataList = data.get(ctr);
			objects[i++] = dataList.toArray(new Object[dataList.size()]);
		}
		return objects;
	}

	public static Object[][] excelDataLoader(String excelPath, String sheetName, Method name) throws IOException {

		ExcelObject testDataObj = new ExcelObject(excelPath);
		List<List<Object>> data = testDataObj.getExcelData(sheetName);
		testDataObj.closeWorkbook();
		Object[][] objects = new Object[data.size() - 1][];
		int i = 0;
		for (int ctr = 1; ctr < data.size(); ctr++) {
			if (data.get(ctr).contains(name.getName())) {
				List<Object> dataList = data.get(ctr);
				objects[i++] = dataList.toArray(new Object[dataList.size()]);
			}
		}
		return objects;
	}

	public static Map<String, Map<String, String>> excelDataLoaderInMap(String excelPath, String sheetName)
			throws IOException {
		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
		ExcelObject testDataObj = new ExcelObject(excelPath);
		List<List<Object>> rows = testDataObj.getExcelData(sheetName);
		testDataObj.closeWorkbook();
		Map<Integer, String> headerMap = new HashMap<Integer, String>();
		List<Object> headers = rows.get(0);
		int index = 1;
		for (Object header : headers) {
			headerMap.put(index, header.toString());
			index++;
		}
		for (List<Object> row : rows) {
			index = 1;

			Map<String, String> rowMap = new HashMap<String, String>();
			for (Object cell : row) {
				rowMap.put(headerMap.get(index), cell.toString());
				index++;
			}
			// getCommonData(excelPath, rowMap);
			dataMap.put(sheetName, rowMap);
		}
		return dataMap;
	}

	public static Map<String, Map<String, String>> excelDataLoaderInMap(String excelPath, String sheetName, Method name)
			throws IOException {
		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
		ExcelObject testDataObj = new ExcelObject(excelPath);
		List<List<Object>> rows = testDataObj.getExcelData(sheetName);
		testDataObj.closeWorkbook();
		Map<Integer, String> headerMap = new HashMap<Integer, String>();
		List<Object> headers = rows.get(0);
		int index = 1;
		for (Object header : headers) {
			headerMap.put(index, header.toString());
			index++;
		}
		for (List<Object> row : rows) {
			index = 1;
			if (row.contains(name.getName())) {
				Map<String, String> rowMap = new HashMap<String, String>();
				for (Object cell : row) {
					rowMap.put(headerMap.get(index), cell.toString());
					index++;
				}
				// getCommonData(excelPath, rowMap);
				dataMap.put(sheetName, rowMap);
			}
		}
		return dataMap;
	}


	public static Map<String, Map<String, String>> excelDataLoaderMulti(String excelPath, String sheetName,
			String methodName) throws IOException {

		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
		ExcelObject testDataObj = new ExcelObject(excelPath);
		List<List<Object>> rows = testDataObj.getExcelDataNew(sheetName);
		testDataObj.closeWorkbook();
		Map<Integer, String> headerMap = new HashMap<Integer, String>();
		List<Object> headers = rows.get(0);
		int index = 1;
		for (Object header : headers) {
			headerMap.put(index, header.toString());
			index++;
		}
		int dataMap_index = 1;

		for (List<Object> row : rows) {
			index = 1;

			if (row.contains(methodName)) {
				Map<String, String> rowMap = new HashMap<String, String>();
				for (Object cell : row) {
					rowMap.put(headerMap.get(index), cell.toString());
					index++;
				}
				dataMap.put(sheetName + '_' + String.valueOf(dataMap_index), rowMap);
				dataMap_index++;
			}
		}
		return dataMap;
	}

}
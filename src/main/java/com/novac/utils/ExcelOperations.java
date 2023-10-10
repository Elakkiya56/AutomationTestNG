package com.novac.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import io.restassured.RestAssured;
import io.restassured.response.Response;

class ReportRow {
	private String TCName;
	private String Method;
	private String EndPoints;
	private String Params;
	private String Authorization;
	private String Header;
	private String JSONBody;

	public String getTCName() {
		return this.TCName;
	}

	public void setTCName(String TCName) {
		this.TCName = TCName;
	}

	public String getMethod() {
		return this.Method;
	}

	public void setMethod(String Method) {
		this.Method = Method;
	}

	public String getEndPoints() {
		return this.EndPoints;
	}

	public void setEndPoints(String EndPoints) {
		this.EndPoints = EndPoints;
	}

	public String getParams() {
		return this.Params;
	}

	public void setParams(String Params) {
		this.Params = Params;
	}

	public String getAuthorization() {
		return this.Authorization;
	}

	public void setAuthorization(String Authorization) {
		this.Authorization = Authorization;
	}

	public String getHeader() {
		return this.Header;
	}

	public void setHeader(String Header) {
		this.Header = Header;
	}

	public String getJSONBody() {
		return this.JSONBody;
	}

	public void setJSONBody(String JSONBody) {
		this.JSONBody = JSONBody;
	}
}

public class ExcelOperations {
	static FileInputStream file;
	static XSSFWorkbook workbook;
	static XSSFSheet sheet;
	static Map<String, Integer> map = new LinkedHashMap<String, Integer>();
	static String userDir = System.getProperty("user.dir");
	static File apiTestCasesFile = new File(userDir + "\\APITests\\GLO_API.xlsx");
	static String apiResponsesOutputFile = userDir + "\\APITests\\";
	public static HashMap<String, Response> responsesMap = new HashMap<String, Response>();
	public static String JSON_FILE_LOCATION;

	public static Map<String, Integer> getColumnNameIndex() throws IOException {
		// Create map

		file = new FileInputStream(apiTestCasesFile);
		workbook = new XSSFWorkbook(file);
		sheet = workbook.getSheetAt(0);

		XSSFRow row = sheet.getRow(0); // Get first row
		// following is boilerplate from the java doc
		short minColIx = row.getFirstCellNum(); // get the first column index for a row
		short maxColIx = row.getLastCellNum(); // get the last column index for a row
		for (short colIx = minColIx; colIx < maxColIx; colIx++) { // loop from first to last index
			XSSFCell cell = row.getCell(colIx); // get the cell
			map.put(cell.getStringCellValue(), cell.getColumnIndex()); // add the cell contents (name of column) and
																		// cell index to the map
		}
		System.out.println(map);
		return map;
	}




}

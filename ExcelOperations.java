package com.slb.utils;

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

	public static void readExcelData(Map<String, Integer> map) throws FileNotFoundException {
		List<ReportRow> listOfDataFromReport = new ArrayList<ReportRow>();

		sheet = workbook.getSheetAt(0);
		int totalRows = sheet.getPhysicalNumberOfRows();

		for (int x = 1; x < totalRows; x++) {
			ReportRow rr = new ReportRow(); // Data structure to hold the data from the xls file.
			XSSFRow dataRow = sheet.getRow(x); // get row 1 to row n (rows containing data)

			int idxForTCName = map.get("TC_No");
			int idxForMethod = map.get("Method"); // get the column index for the column with header name = "Column1"
			int idxForEndPoints = map.get("EndPoints"); // get the column index for the column with header name =
														// "Column2"
			int idxForParams = map.get("Params"); // get the column index for the column with header name = "Column3"
			int idxForAuthorization = map.get("Authorization");
			int idxForHeader = map.get("Header");
			int idxForJSONBody = map.get("JSONBody");

			XSSFCell cellTCName = dataRow.getCell(idxForTCName);
			XSSFCell cellMethod = dataRow.getCell(idxForMethod); // Get the cells for each of the indexes
			XSSFCell cellEndPoints = dataRow.getCell(idxForEndPoints);
			XSSFCell cellParams = dataRow.getCell(idxForParams);
			XSSFCell cellAuthorization = dataRow.getCell(idxForAuthorization); // Get the cells for each of the indexes
			XSSFCell cellHeader = dataRow.getCell(idxForHeader);
			XSSFCell cellJSONBody = dataRow.getCell(idxForJSONBody);

			// NOTE THAT YOU HAVE TO KNOW THE DATA TYPES OF THE DATA YOU'RE EXTRACTING.
			// FOR EXAMPLE I DON'T THINK YOU CAN USE cell.getStringCellValue IF YOU'RE
			// TRYING TO GET A NUMBER
			rr.setTCName(cellTCName.getStringCellValue());
			rr.setMethod(cellMethod.getStringCellValue()); // Get the values out of those cells and put them into the
															// report row object
			rr.setEndPoints(cellEndPoints.getStringCellValue());
			rr.setParams(cellParams.getStringCellValue());
			rr.setAuthorization(cellAuthorization.getStringCellValue()); // Get the values out of those cells and put
																			// them into the report row object
			rr.setHeader(cellHeader.getStringCellValue());
			rr.setJSONBody(cellJSONBody.getStringCellValue());
			listOfDataFromReport.add(rr);

		}

		// Now you have a list of report rows
		for (int j = 0; j < listOfDataFromReport.size(); j++) {
			System.out.println("----------------------Row number =" + j);
			System.out.println("Method Value: " + listOfDataFromReport.get(j).getMethod());
			System.out.println("EndPoints Value: " + listOfDataFromReport.get(j).getEndPoints());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getParams());

			System.out.println("Header Value: " + listOfDataFromReport.get(j).getAuthorization());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getHeader());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getJSONBody());

			createARequest(listOfDataFromReport, j);
		}

		writeOutputFileResponses(responsesMap);
	}

	// Use this method for individual test case.
	public static Response readAPIForTestCase(String testcasename) throws IOException {
		Map<String, Integer> map = getColumnNameIndex();
		List<ReportRow> listOfDataFromReport = new ArrayList<ReportRow>();

		sheet = workbook.getSheetAt(0);
		int totalRows = sheet.getPhysicalNumberOfRows();

		for (int x = 1; x < totalRows; x++) {
			ReportRow rr = new ReportRow(); // Data structure to hold the data from the xls file.
			XSSFRow dataRow = sheet.getRow(x); // get row 1 to row n (rows containing data)

			int idxForTCName = map.get("TC_No");
			int idxForMethod = map.get("Method"); // get the column index for the column with header name = "Column1"
			int idxForEndPoints = map.get("EndPoints"); // get the column index for the column with header name =
														// "Column2"
			int idxForParams = map.get("Params"); // get the column index for the column with header name = "Column3"
			int idxForAuthorization = map.get("Authorization");
			int idxForHeader = map.get("Header");
			int idxForJSONBody = map.get("JSONBody");

			XSSFCell cellTCName = dataRow.getCell(idxForTCName);
			if (cellTCName.getStringCellValue().toLowerCase().equals(testcasename.toLowerCase())) {
				XSSFCell cellMethod = dataRow.getCell(idxForMethod); // Get the cells for each of the indexes
				XSSFCell cellEndPoints = dataRow.getCell(idxForEndPoints);
				XSSFCell cellParams = dataRow.getCell(idxForParams);
				XSSFCell cellAuthorization = dataRow.getCell(idxForAuthorization); // Get the cells for each of the
																					// indexes
				XSSFCell cellHeader = dataRow.getCell(idxForHeader);
				XSSFCell cellJSONBody = dataRow.getCell(idxForJSONBody);

				// NOTE THAT YOU HAVE TO KNOW THE DATA TYPES OF THE DATA YOU'RE EXTRACTING.
				// FOR EXAMPLE I DON'T THINK YOU CAN USE cell.getStringCellValue IF YOU'RE
				// TRYING TO GET A NUMBER
				rr.setTCName(cellTCName.getStringCellValue());
				rr.setMethod(cellMethod.getStringCellValue()); // Get the values out of those cells and put them into
																// the report row object
				rr.setEndPoints(cellEndPoints.getStringCellValue());
				rr.setParams(cellParams.getStringCellValue());
				rr.setAuthorization(cellAuthorization.getStringCellValue()); // Get the values out of those cells and
																				// put them into the report row object
				rr.setHeader(cellHeader.getStringCellValue());
				rr.setJSONBody(cellJSONBody.getStringCellValue());
				listOfDataFromReport.add(rr);
				break;
			}
		}

		Response resp = null;
		// Now you have a list of report rows
		for (int j = 0; j < listOfDataFromReport.size(); j++) {
			System.out.println("----------------------Row number =" + j);
			System.out.println("Method Value: " + listOfDataFromReport.get(j).getMethod());
			System.out.println("EndPoints Value: " + listOfDataFromReport.get(j).getEndPoints());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getParams());

			System.out.println("Header Value: " + listOfDataFromReport.get(j).getAuthorization());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getHeader());
			System.out.println("Header Value: " + listOfDataFromReport.get(j).getJSONBody());

			resp = createARequest(listOfDataFromReport, j);
		}
		return resp;
	}

	public static Response createARequest(List<ReportRow> listOfDataFromReport, int rowNo) {
		Response resp = null;
		JSON_FILE_LOCATION = System.getProperty("user.dir");
		JSON_FILE_LOCATION = JSON_FILE_LOCATION + "/JSONData/";

		if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("get")) {
			resp = APIUtils.getRequest(listOfDataFromReport.get(rowNo).getEndPoints(),
					listOfDataFromReport.get(rowNo).getParams(), listOfDataFromReport.get(rowNo).getHeader());
			// responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
		} else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("post")) {
			String jsonFileName = listOfDataFromReport.get(rowNo).getJSONBody();
			JSON_FILE_LOCATION = JSON_FILE_LOCATION + jsonFileName;

			// String jsonBody = convertJsonObjectToString(jsonFileName);
			resp = APIUtils.postRequest(listOfDataFromReport.get(rowNo).getEndPoints(),
					listOfDataFromReport.get(rowNo).getParams(), listOfDataFromReport.get(rowNo).getHeader(),
					JSON_FILE_LOCATION);
			// responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
		} else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("put")) {
			String jsonFileName = listOfDataFromReport.get(rowNo).getJSONBody();
			JSON_FILE_LOCATION = JSON_FILE_LOCATION + jsonFileName;

			// String jsonBody = convertJsonObjectToString(jsonFileName);
			resp = APIUtils.putRequest(listOfDataFromReport.get(rowNo).getEndPoints(),
					listOfDataFromReport.get(rowNo).getParams(), listOfDataFromReport.get(rowNo).getHeader(),
					JSON_FILE_LOCATION);
			// responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
		} else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("delete")) {
			resp = APIUtils.deleteRequest(listOfDataFromReport.get(rowNo).getEndPoints(),
					listOfDataFromReport.get(rowNo).getParams(), listOfDataFromReport.get(rowNo).getHeader());
			// responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
		}
		return resp;
	}

	public static void writeOutputFileResponses(HashMap<String, Response> respMap) throws FileNotFoundException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("sheet1");
		int rowno = 0;
		// -----------------Serialize the object
		for (HashMap.Entry entry : respMap.entrySet()) {
			XSSFRow row = sheet.createRow(rowno++);
			row.createCell(0).setCellValue((String) entry.getKey());
			Response res = (Response) entry.getValue();
			row.createCell(1).setCellValue(res.print());
			System.out.println("writing values to excel" + entry.getKey() + "" + entry.getValue());
		}

		FileOutputStream file = new FileOutputStream(apiResponsesOutputFile + "Out_GLO_API.xlsx");
		try {
			workbook.write(file);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Data Copied to Excel");

	}

	public static void main(String[] args) throws IOException {

//		Response res1 = given().when().get("https://evd.srp.cloud.slb-ds.com/api/Alerts/GetAllAlertList");
//		System.out.println(res1);
		Map<String, Integer> map = getColumnNameIndex();
		Response res = readAPIForTestCase("TC_003");
		System.out.println(res.body());
		// res.getStatusCode()
//		System.out.println(res.body());
//		System.out.println(res.contentType());
//		readExcelData(map);
//		int idx = map.get("Params");
//		System.out.println(idx);

	}
	
	public static Response readAPIForTestCase(Map<String, String> dataMap) {
		return createARequest(dataMap);
	}

	public static Response createARequest(Map<String, String> dataMap) {
		Response resp = null;
		JSON_FILE_LOCATION = System.getProperty("user.dir");
		JSON_FILE_LOCATION = JSON_FILE_LOCATION + "/JSONData/";

		if (dataMap.get("Method").equals("GET")) {
			resp = APIUtils.getRequest(dataMap.get("EndPoints"), dataMap.get("Params"), dataMap.get("Header"));
			// responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
//	   } else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("post")) {
//		   String jsonFileName = listOfDataFromReport.get(rowNo).getJSONBody();
//		   JSON_FILE_LOCATION = JSON_FILE_LOCATION+jsonFileName;
//		   
//		   //String jsonBody = convertJsonObjectToString(jsonFileName);
//		    resp = APIUtils.postRequest(listOfDataFromReport.get(rowNo).getEndPoints(),listOfDataFromReport.get(rowNo).getParams(),listOfDataFromReport.get(rowNo).getHeader(), JSON_FILE_LOCATION);
//		   //responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
//	   } else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("put")) {
//		   String jsonFileName = listOfDataFromReport.get(rowNo).getJSONBody();
//		   JSON_FILE_LOCATION = JSON_FILE_LOCATION+jsonFileName;
//		   
//		   //String jsonBody = convertJsonObjectToString(jsonFileName); 
//		   resp = APIUtils.putRequest(listOfDataFromReport.get(rowNo).getEndPoints(), listOfDataFromReport.get(rowNo).getParams(),listOfDataFromReport.get(rowNo).getHeader(), JSON_FILE_LOCATION);
//		   //responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
//	   } else if (listOfDataFromReport.get(rowNo).getMethod().toLowerCase().equals("delete")) {
//		    resp = APIUtils.deleteRequest(listOfDataFromReport.get(rowNo).getEndPoints(), listOfDataFromReport.get(rowNo).getParams(),listOfDataFromReport.get(rowNo).getHeader());
//		   //responsesMap.put(listOfDataFromReport.get(rowNo).getTCName(), resp);
//	   }
		}

		return resp;
	}
}

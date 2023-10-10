package com.slb.framework;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slb.utils.Config;
import com.slb.utils.Constants;
import com.slb.utils.UIUtils;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCaseUpdate {
	String userCredentials = "harsh.agnihotri@cnd.com:xeqwjuqdvacy6apireqembnhjybqlrsfhiqsq3fuyqrognjlb5gq";
	String basicAuth = "Basic " + new String(Base64.getEncoder().encodeToString(userCredentials.getBytes()));

/*
	public int[] getTestIdSuiteIdForTestCase(String methodName) {
		System.out.println("Inside getTestIdSuiteIdForTestCase------------for method "+methodName);		
		int[] resArray=new int[2];
		int testCaseId = -1,suiteId=-1;
		for (TestCaseMetadata test : UIUtils.testCaseMetadataList) {
			System.out.println("--------->>>>>"+test);
			if (test.getTestCaseName().equals(methodName)) {
				testCaseId = Integer.parseInt(test.getTestCaseID());
				suiteId = Integer.parseInt(test.getTestSuiteID());				
			}
		}
		resArray[0]=testCaseId;
		resArray[1]=suiteId;	
		return resArray;
		
	}
	
	*/
	
	public boolean azureTestcaseUpdate(int testCaseId, int suiteId, boolean isPassed) {
		Config config = null;
		try {
			config = new Config(Constants.ConfigPath);

		} catch (IOException e) {
			System.out.println("Exception in getting config at testCaseAzureFlag()");
		}
		String tcConfigValue = config.getPropertyValue("TestCaseUpdate");
		
		if (tcConfigValue.equals("N")) {
			return false;
		}
		System.out.println("Azure API Update is enabled!!");
		
		boolean isSuccess = false;
		try {
			int firstResponseId = getFirstApiData(testCaseId, suiteId);
			try {
				int secondResponseId = makePatchRequest(firstResponseId, suiteId, isPassed);
				isSuccess = true;
			} catch (Exception e) {
				System.out.println("Exception in First Response ID");
			}			
		} catch (Exception e) {
			System.out.println("Exception in Test Case Updation");
		}
		return isSuccess;
	}

	public int getFirstApiData(int id, int inputsuiteId) throws Exception {

		String url = "https://dev.azure.com/TSG-Tolling-Modernization/Platform-Modernization/_apis/test/points?api-version=5.1-preview.2";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization",
				"Basic aGFyc2guYWduaWhvdHJpQGNvbmR1ZW50LmNvbTp4ZXF3anVxZHZhY3k2YXBpcmVxZW1ibmhqeWJxbHJzZmhpcXNxM2Z1eXFyb2duamxiNWdx");

		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Cookie",
				"VstsSession=%7B%22PersistentSessionId%22%3A%223f9de546-c980-48ab-9ed2-275a3258ca10%22%2C%22PendingAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22CurrentAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22SignInState%22%3A%7B%7D%7D");

		String postJsonData = "{\"PointsFilter\": {\"TestcaseIds\": [\"" + id + "\"]}}";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(postJsonData);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		JsonParser parser = new JsonParser();
		JsonObject resjson = parser.parse(response.toString()).getAsJsonObject();
		JsonArray ar = resjson.getAsJsonArray("points");
		int resId = -1;
		for (int i = 0; i < ar.size(); i++) {
			JsonObject jo = (JsonObject) ar.get(i);

			if (jo.get("suite") != null) {
				int suiteId = ((JsonObject) jo.get("suite")).get("id").getAsInt();
				if (suiteId == inputsuiteId) {
					resId = jo.get("id").getAsInt();
					break;
				}

			}

		}
		return resId;
	}

	public int makePatchRequest(int responseId, int suiteId, boolean isPassed) throws Exception {

		String url = "https://dev.azure.com/TSG-Tolling-Modernization/Platform-Modernization/_apis/testplan/Plans/12269/Suites/"
				+ suiteId + "/TestPoint?includePointDetails=true&returnIdentityRef=true&api-version=5.1-preview.2";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
		con.setRequestMethod("POST");
		// con.setRequestMethod("PATCH");;
		con.setRequestProperty("Authorization",
				"Basic aGFyc2guYWduaWhvdHJpQGNvbmR1ZW50LmNvbTp4ZXF3anVxZHZhY3k2YXBpcmVxZW1ibmhqeWJxbHJzZmhpcXNxM2Z1eXFyb2duamxiNWdx");

		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Cookie",
				"VstsSession=%7B%22PersistentSessionId%22%3A%223f9de546-c980-48ab-9ed2-275a3258ca10%22%2C%22PendingAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22CurrentAuthenticationSessionId%22%3A%2200000000-0000-0000-0000-000000000000%22%2C%22SignInState%22%3A%7B%7D%7D");

		String postJsonData = "[{\"id\": " + responseId + ", \"results\": {\"outcome\": " + (isPassed?2:3) + "}}]";

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(postJsonData);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();

		JsonParser parser = new JsonParser();
		JsonObject resjson = parser.parse(response.toString()).getAsJsonObject();
		JsonArray ar = resjson.getAsJsonArray("value");

		JsonObject jo = (JsonObject) ar.get(0); //

		return jo.get("id").getAsInt();

	}

}

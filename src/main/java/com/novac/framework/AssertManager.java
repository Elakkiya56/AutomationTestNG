package com.novac.framework;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.novac.utils.AbstractPage;

import junit.framework.Assert;

public class AssertManager {
	private static AssertManager assertManager = new AssertManager();
	private AssertManager() {		
	};
	
	public static AssertManager getInstance() {
		return assertManager;
	}
	public void sAssertEquals(String actual, String expected, String message, boolean screenshot, Boolean...hardAssert) {
		if (expected.equals(actual)) {
			BaseTest.extentTest.log(Status.PASS, message + " - Successful.Expected match with Actual value:"+expected);
		}else if(screenshot) {
			log(message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
			BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");	
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}else {
			BaseTest.extentTest.log(Status.FAIL,message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual);
			BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}		
			
	}
	public void sAssertContains(String actual, String expected, String message, boolean screenshot, Boolean...hardAssert) {
		if(actual!=null) {
			if (actual.contains(expected)) {
				BaseTest.extentTest.log(Status.PASS, message + " - Successful.Expected contains matched with Expected:"+expected+" and Actual value:"+actual);
			}else if(screenshot) {
				log(message + "-Failed for contains assertion.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
				BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
				if(hardAssert.length > 0) {
					if(hardAssert[0].equals(true)) {
						Assert.fail();
					}
				}
			}else {
				BaseTest.extentTest.log(Status.FAIL,message + "-Failed for contains assertion.<br>Expected:" + expected + " <br>Actual  :" + actual);
				BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
				if(hardAssert.length > 0) {
					if(hardAssert[0].equals(true)) {
						Assert.fail();
					}
				}
			}
		}else {
			if(screenshot) {
				log(message + "-Failed for contains assertion.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
				BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
				if(hardAssert.length > 0) {
					if(hardAssert[0].equals(true)) {
						Assert.fail();
					}
				}
			}else {
				BaseTest.extentTest.log(Status.FAIL,message + "-Failed for contains assertion.<br>Expected:" + expected + " <br>Actual  :" + actual);
				BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
				if(hardAssert.length > 0) {
					if(hardAssert[0].equals(true)) {
						Assert.fail();
					}
				}
			}
		}
				
	}

	public void sAssertEquals(int actual, int expected, String message, Boolean...hardAssert) {
		if (expected == actual) {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful.Expected match with Actual value:"+expected);
		}else
			log(message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
			BaseTest.sAssert.assertEquals(actual, expected, message + "- Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
				Assert.fail();
			}
		}
	}

	public void sAssertEquals(boolean actual, boolean expected, String message, boolean screenshot, Boolean...hardAssert) {
		if (expected == actual) {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful. Expected match with Actual value:"+expected);
		}else if(screenshot) {
			log(message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
			BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}else {
			BaseTest.extentTest.log(Status.FAIL,message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual);
			BaseTest.sAssert.assertEquals(actual, expected, message + " - Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}
	}

	public void sAssertEquals(double actual, double expected, String message, Boolean...hardAssert) {
		if (expected == actual) {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful. Expected match with Actual value:"+expected);
		}else {
			log(message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
			BaseTest.sAssert.assertEquals(actual, expected, message + "- Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}
	}

	public void sAssertEquals(float actual, float expected, String message, Boolean...hardAssert) {
		if (expected == actual) {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful. Expected match with Actual value:"+expected);
		}else {
			log(message + "-Failed.<br>Expected:" + expected + " <br>Actual  :" + actual, BaseTest.driver);
			BaseTest.sAssert.assertEquals(actual, expected, message + "- Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}
	}

	public void sAssertTrue(Boolean condition, String message,boolean screenshot, Boolean...hardAssert) {
		if (condition) {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful");
		}else if(screenshot) {
			log(message + "-Failed.", BaseTest.driver);
			BaseTest.sAssert.assertTrue(condition, message + " - Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}else {
			BaseTest.extentTest.log(Status.FAIL,message + "-Failed.");
			BaseTest.sAssert.assertTrue(condition, message + " - Failed");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}		
	}

	public void sAssertFalse(Boolean condition, String message, Boolean...hardAssert) {
		if (condition) {
			log(message + "- Failed .", BaseTest.driver);
		}else {
			BaseTest.extentTest.log(Status.PASS, message + "- Successful.");
			BaseTest.sAssert.assertFalse(condition, message + "- Successful");
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}
	}	
	public void sAssertException(String message, boolean screenshot, Boolean...hardAssert) {
		if(screenshot) {
			log("Exception occured while validating "+message, BaseTest.driver);	
			BaseTest.extentTest.log(Status.FAIL,"Exception occured while validating. Please investigate!!! "+message);
			BaseTest.sAssert.assertEquals(false, true, "Exception occured in while validating "+message);
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}else {
			BaseTest.extentTest.log(Status.FAIL,"Exception occured while validating "+message);
			BaseTest.sAssert.assertEquals(false, true, "Exception occured in while validating "+message);
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		}
	}
	public void log(Status status, String msg, Boolean...hardAssert) {
		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msg= AbstractPage.addLinebreaks(msg, 100);
		if (status == Status.PASS) {
			BaseTest.extentTest.log(Status.PASS, MarkupHelper.createLabel(msg, ExtentColor.GREEN));
			System.out.println(msg);
		} else if (status == Status.SKIP) {
			BaseTest.extentTest.log(Status.SKIP, MarkupHelper.createLabel(msg, ExtentColor.ORANGE));
			System.out.println("SKIPPED - " + msg);
		} else if (status == Status.FAIL) {
			BaseTest.extentTest.log(status, MarkupHelper.createLabel(msg, ExtentColor.RED));
			System.err.println(msg);
			if(hardAssert.length > 0) {
				if(hardAssert[0].equals(true)) {
					Assert.fail();
				}
			}
		} else if (status == Status.INFO) {
			BaseTest.extentTest.log(Status.INFO, MarkupHelper.createLabel(msg, ExtentColor.WHITE));
			System.out.println(msg);
		}
		
	}
	
	public void logWithNoLineBreak(Status status, String msg) {
		/*try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		if (status == Status.PASS) {
			BaseTest.extentTest.log(Status.PASS, MarkupHelper.createLabel(msg, ExtentColor.GREEN));
			System.out.println(msg);
		} else if (status == Status.SKIP) {
			BaseTest.extentTest.log(Status.SKIP, MarkupHelper.createLabel(msg, ExtentColor.ORANGE));
			System.out.println("SKIPPED - " + msg);
		} else if (status == Status.FAIL) {
			BaseTest.extentTest.log(status, MarkupHelper.createLabel(msg, ExtentColor.RED));
			System.err.println(msg);
		} else if (status == Status.INFO) {
			BaseTest.extentTest.log(Status.INFO, MarkupHelper.createLabel(msg, ExtentColor.WHITE));
			System.out.println(msg);
		}
	}
	public static void customlog(Status status, String msg) {
		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msg= AbstractPage.addLinebreaks(msg, 100);
		if (status == Status.PASS) {
			BaseTest.extentTest.log(Status.PASS, msg);
			System.out.println(msg);
		} else if (status == Status.SKIP) {
			BaseTest.extentTest.log(Status.SKIP, msg);
			System.out.println("SKIPPED - " + msg);
		} else if (status == Status.FAIL) {
			BaseTest.extentTest.log(status, msg);
			System.err.println(msg);
		} else if (status == Status.INFO) {
			BaseTest.extentTest.log(Status.INFO, msg);
			System.out.println(msg);
		}
	}
	public void log(String msg, WebDriver driver) {
		BaseTest.extentTest.log(Status.FAIL, msg,MediaEntityBuilder.createScreenCaptureFromPath(BaseTest.LOGGER.captureScreen()).build());
		System.err.println(msg);
	}


}

package com.slb.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.helpers.DateTimeDateFormat;

public class DateTimeUtils {
//	public static String getTime() {
//		Calendar cal = new GregorianCalendar();
//		return Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(cal.get(Calendar.MINUTE)) + ":"
//				+ Integer.toString(cal.get(Calendar.SECOND));
//	}

	public static String getDateInTextFormat(String data, String... dateFormat) {
		Calendar cal = Calendar.getInstance();
		String format;

		if (dateFormat == null) {
			format = "dd/MM/yyyy";
		} else {
			format = dateFormat[0];
		}

		DateFormat dtFormat = new SimpleDateFormat(format);

		if ("Tomorrow".equalsIgnoreCase(data)) {
			cal.add(Calendar.DATE, 1);
		} else if ("Yesterday".equalsIgnoreCase(data)) {
			cal.add(Calendar.DATE, -1);
		} else if ("Today".equalsIgnoreCase(data)) {
			
		} else if ("DateAfterMonth".equalsIgnoreCase(data)) {
			cal.add(Calendar.DATE, +31);
		} else if (data.toLowerCase().startsWith("today+")) {
			cal.add(Calendar.DATE, Integer.valueOf(data.toLowerCase().replaceAll("today+", "")));
		} else if (data.toLowerCase().startsWith("today-")) {
			cal.add(Calendar.DATE, -Integer.valueOf(data.toLowerCase().replaceAll("today-", "")));
		}
		return dtFormat.format(cal.getTime());
	}

	public static String getTodaysDate() {
		return DateFormat.getDateTimeInstance().format(new Date()).toString().replaceAll(":", "_")
				.replaceAll("/s+", "_").replaceAll(",", "");
	}

	public static Map<String, String> getMonInNum() {
		Map<String, String> mon = new HashMap<String, String>();
		mon.put("JAN", "01");
		mon.put("FEB", "02");
		mon.put("MAR", "03");
		mon.put("APR", "04");
		mon.put("MAY", "05");
		mon.put("JUN", "06");
		mon.put("JUL", "07");
		mon.put("AUG", "08");
		mon.put("SEP", "09");
		mon.put("OCT", "10");
		mon.put("NOV", "11");
		mon.put("DEC", "12");
		return mon;
	}
	
	public static String getMonInNum(String monName) {
		Map<String, String> mon = new HashMap<String, String>();
		mon.put("JAN", "01");
		mon.put("FEB", "02");
		mon.put("MAR", "03");
		mon.put("APR", "04");
		mon.put("MAY", "05");
		mon.put("JUN", "06");
		mon.put("JUL", "07");
		mon.put("AUG", "08");
		mon.put("SEP", "09");
		mon.put("OCT", "10");
		mon.put("NOV", "11");
		mon.put("DEC", "12");
		return mon.get(monName);
	}

	public static String convertDateInNumFormat(String date) {
		Map<String, String> monInNum = getMonInNum();
		String[] split = date.split("/");
		return split[2].trim() + "-" + monInNum.get(split[1].trim()) + "-" + split[0].trim();
	}
	
	public static LocalDateTime getDate(String date) {
		LocalDateTime locDate = LocalDateTime.now();
		String[] dat = date.split("=");
		if(dat[0].equals("minus")) {
			locDate = locDate.minusDays(Integer.parseInt(dat[1]));
		}else if(dat[0].equals("plus")){
			locDate = locDate.plusDays(Integer.parseInt(dat[1]));
		}else if(dat[0].equals("minushours")) {
			locDate = locDate.minusHours(Integer.parseInt(dat[1]));
		}
		else {
			return locDate;
		}
		
		return locDate;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static boolean checkDateFormat(String expFormat, String actDate) throws ParseException {
		boolean status = false;
		try {
			DateTimeFormatter formater = DateTimeFormatter.ofPattern(expFormat);
			formater.parse(actDate);
			status = true;
		} catch (Exception e) {
			status = false;
		}
		return status;
	}
	
	public static String getTodaysDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

}
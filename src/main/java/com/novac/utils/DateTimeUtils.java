package com.novac.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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



}
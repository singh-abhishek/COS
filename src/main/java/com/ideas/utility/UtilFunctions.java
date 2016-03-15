package com.ideas.utility;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class UtilFunctions {
	
	public static String generateFileName() {
		String filename = "Employee_Schedule_";
		Calendar cal = Calendar.getInstance();
		if(cal.get(cal.HOUR_OF_DAY) < 16 ){
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy");
			String date = sdf.format(cal.getTime());
			filename+=date+"_Evening";
		}
		else{
			cal.set(cal.DAY_OF_MONTH, cal.get(cal.DAY_OF_MONTH) + 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy");
			String date = sdf.format(cal.getTime());
			filename+=date+"_Day";
		}
		return filename;
	}

	public static String[] getEmailRecipients() throws IOException {
		Properties config = new Properties();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		config.load(inputStream);
		return config.getProperty("emailRecipients").toString().split("\\|");
	}
}

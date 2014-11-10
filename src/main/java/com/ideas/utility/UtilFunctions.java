package com.ideas.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
public class UtilFunctions {
	public String generateFileName() {
		String filename = "Employee_Schedule_";
		Calendar cal = Calendar.getInstance();
		if(cal.get(cal.HOUR_OF_DAY) < 12 ){
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy");
			String date = sdf.format(cal.getTime());
			filename+=date+"_Evening";
		}
		else{
			cal.set(cal.DAY_OF_MONTH, cal.get(cal.DAY_OF_MONTH)+1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy");
			String date = sdf.format(cal.getTime());
			filename+=date+"_Day";
		}
		return filename;
	}
}

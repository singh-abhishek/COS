package vendorreport;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class EmailScheduler {

	public void callScheduler(Timer timer) throws Exception {

		System.out.println("Scheduler Starterd...");
		
		long millisInADay = 24 * 60 * 60 * 1000;
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date am11 = new Date(cal.getTimeInMillis());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date pm4 = new Date(cal.getTimeInMillis());
//		System.out.println("Will send morning roster i: "
//				+ (millisInADay + am11.getTime() - date.getTime())
//				% millisInADay / 60000 + "mins");
//		System.out.println("Will send evening roster i: "
//				+ (millisInADay + pm4.getTime() - date.getTime())
//				% millisInADay / 60000 + "mins");
		timer.scheduleAtFixedRate(
				new EmailDispatcher(),
				(millisInADay + am11.getTime() - date.getTime()) % millisInADay,
				millisInADay);
		timer.scheduleAtFixedRate(new EmailDispatcher(),
				(millisInADay + pm4.getTime() - date.getTime()) % millisInADay,
				millisInADay);
	
	}

//	public static void main(String a[]) throws Exception {
//		
//	}

}

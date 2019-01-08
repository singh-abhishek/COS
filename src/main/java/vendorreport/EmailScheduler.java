package vendorreport;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailScheduler {

	private final static Logger LOGGER = LogManager.getLogger(EmailScheduler.class.getName()); 
	public void callScheduler(Timer timer) throws Exception {

		LOGGER.info("Scheduler Starterd...");
		
		long millisInADay = 24 * 60 * 60 * 1000;
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 05);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date am1105 = new Date(cal.getTimeInMillis());
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 10);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date am1110 = new Date(cal.getTimeInMillis());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 10);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date pm4 = new Date(cal.getTimeInMillis());
		LOGGER.info("Will send morning roster in: "
				+ (millisInADay + am1105.getTime() - date.getTime())
				% millisInADay / 60000 + "mins");
		LOGGER.info("Will send morning roster in: "
				+ (millisInADay + am1110.getTime() - date.getTime())
				% millisInADay / 60000 + "mins");
		LOGGER.info("Will send evening roster in: "
				+ (millisInADay + pm4.getTime() - date.getTime())
				% millisInADay / 60000 + "mins");

		timer.scheduleAtFixedRate(
				new EmailDispatcher(),
				(millisInADay + am1105.getTime() - date.getTime()) % millisInADay,
				millisInADay);

		timer.scheduleAtFixedRate(
				new EmailDispatcher(),
				(millisInADay + am1110.getTime() - date.getTime()) % millisInADay,
				millisInADay);
		
		timer.scheduleAtFixedRate(new EmailDispatcher(),
				(millisInADay + pm4.getTime() - date.getTime()) % millisInADay,
				millisInADay);
	
	}

}

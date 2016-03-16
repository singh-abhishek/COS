package vendorreport;

import com.ideas.utility.UtilFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.TimerTask;

public class EmailDispatcher extends TimerTask {

	private static final Logger LOGGER = LogManager.getLogger(EmailDispatcher.class);
	public void run() {

		String[] toEmail = new String[0];
		try {
			toEmail = UtilFunctions.getEmailRecipients();
		} catch (IOException e) {
			LOGGER.error("Error while retrieving email recipients", e);
		}
		String subject = "IDeaS Cab Schedule";
		String body = "IDeaS Cab Schedule";

		try {
			LOGGER.info("Creating report and sending...");
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/COS/routeOptimize").openConnection();
			con.setRequestMethod("GET");
			con.connect();
			con.getResponseCode();
			Properties smtpHostProperties = new Properties();
			String smtpHost = "mailhost.fyi.sas.com";
			smtpHostProperties.put("mail.smtp.host", smtpHost);
			Email email = new Email(smtpHostProperties);
			email.sendEmail(subject, body, toEmail);
		} catch (Exception e) {
			LOGGER.error("Error in sending report", e);
		}
	}
	
	public static void generateReportAndSend() {
		new EmailDispatcher().run();
	}
	
}

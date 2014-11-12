package vendorreport;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;


public class EmailDispatcher extends TimerTask {

	public void run() {

		String from = "khoorafati@gmail.com";
		String pass = "ideas@1234";
		String[] to = { "mittal.prachi22@gmail.com" }; // list of recipient
														// email addresses
		String subject = "Java send mail example";
		String body = "Welcome to JavaMail!";

		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/COS/routeOptimize").openConnection();
			con.setRequestMethod("GET");
			con.connect();
			con.getResponseCode();
			new Email().sendEmail(from, pass, to, subject, body);
			System.out.println("Email Sent Succesfully...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

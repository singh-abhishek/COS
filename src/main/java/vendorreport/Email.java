package vendorreport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.ideas.utility.UtilFunctions;

public class Email {

	public void sendEmail(String from, String pass, String[] to,
			String subject, String body) {
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		ArrayList<String> filePath = new ArrayList<String>();
		addFilePath(filePath);

		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Here's the file");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			addAttachment(multipart, filePath);
			message.setContent(multipart);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (AddressException ae) {
		} catch (MessagingException me) {
		}
	}

	private void addFilePath(ArrayList<String> filePath) {
		filePath.add("C:/" + new UtilFunctions().generateFileName() + ".xlsx");
		filePath.add("C:/" + new UtilFunctions().generateFileName() + ".pdf");
	}

	private void addAttachment(Multipart multipart, ArrayList<String> filePath)
			throws MessagingException {
		for (int i = 0; i < filePath.size(); i++) {
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath.get(i));
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(new File(filePath.get(i)).getName());
			multipart.addBodyPart(messageBodyPart);
		}
	}
}
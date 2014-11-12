package vendorreport;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Simple use case for the javax.mail API.
 */
public final class Emailer {
	private int propertyID;
	private String smtpHost;
	private Properties smtpHostProperties = new Properties();

	

	public Emailer(Properties smtpHostProperties) {
		this.smtpHostProperties = smtpHostProperties;
	}

	public void sendEmail(String aSubject, String aBody, String aToEmailAddr,
			String aToCCEmailAddr, String aToBccEmailAddr,
			String[] fileNamesForAttachmentWithAbsPath,
			String reportListingForMailBody) {

		// Here, no Authenticator argument is used (it is null).
		// Authenticators are used to prompt the user for user name and
		// password.
		Session session = Session.getDefaultInstance(smtpHostProperties, null);
		MimeMessage message = new MimeMessage(session);
		String aFromEmailAddr = smtpHostProperties.getProperty("mail.from");
		// String aToEmailAddr = fMailServerConfig.getProperty("mail.to");
		// String aToCCEmailAddr = fMailServerConfig.getProperty("mail.to.cc");
		// String aToBccEmailAddr =
		// fMailServerConfig.getProperty("mail.to.bcc");
		// String message1 = fMailServerConfig.getProperty("mail.message");
		// if(message1!=null && !message1.equals(""))
		// aBody = message1;
		// String subject1 = fMailServerConfig.getProperty("mail.subject");
		// if(subject1!=null && !subject1.equals(""))
		// aSubject = subject1;

		try {
			InternetAddress mailFromAddress = new InternetAddress();
			mailFromAddress
					.setAddress("IDeaS-Scheduled-Reports@aspmail.ideas.com");
			String bToEmailAddress = aToEmailAddr.lastIndexOf(";") == aToEmailAddr
					.length() - 1 ? aToEmailAddr.substring(0,
					aToEmailAddr.length() - 1) : aToEmailAddr;
			message.setFrom(mailFromAddress); // single address

			// message.addRecipient(Message.RecipientType.TO, new
			// InternetAddress(aToEmailAddr));
			// if(bToEmailAddress!=null && bToEmailAddress.)
			message.addRecipients(Message.RecipientType.TO,
					bToEmailAddress.replaceAll(";", ",")/* "Prasad.Kunte@ideas.com" */);
			if (aToCCEmailAddr != null && aToCCEmailAddr != "") {
				String bToCCEmailAddr = aToCCEmailAddr.lastIndexOf(";") == aToCCEmailAddr
						.length() - 1 ? aToCCEmailAddr.substring(0,
						aToCCEmailAddr.length()) : aToCCEmailAddr;
				message.addRecipients(Message.RecipientType.CC, bToCCEmailAddr);
			}
			if (aToBccEmailAddr != null && aToBccEmailAddr != "") {
				String bToBccEmailAddr = aToBccEmailAddr.lastIndexOf(";") == aToBccEmailAddr
						.length() - 1 ? aToBccEmailAddr.substring(0,
						aToBccEmailAddr.length()) : aToBccEmailAddr;
				message.addRecipients(Message.RecipientType.BCC,
						bToBccEmailAddr);
			}
			message.setSubject(aSubject);

			// Prafulla - REPLY TO
			message.setReplyTo(message.getFrom()); // single address
			// Prafulla - REPLY TO

			Multipart mp = new MimeMultipart();
			// ADD BODY
			/*
			 * Changed by Anushka for MR-9518
			 */
			// BodyPart mimeBodyPart=new MimeBodyPart();
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			// mimeBodyPart.setText(aBody + reportListingForMailBody);
			mimeBodyPart.setText(aBody + reportListingForMailBody, "utf-8");
			/*
			 * Changed by Anushka for MR-9518
			 */
			mp.addBodyPart(mimeBodyPart);

			// Attach FILE
			if (fileNamesForAttachmentWithAbsPath != null
					&& fileNamesForAttachmentWithAbsPath.length > 0) {
				for (int i = 0; i < fileNamesForAttachmentWithAbsPath.length; i++) {
					File tempFile = new File(
							fileNamesForAttachmentWithAbsPath[i]);
					if (tempFile.exists()) {
						BodyPart mimeFileAttachBodyPart = new MimeBodyPart();
						FileDataSource fds = new FileDataSource(
								fileNamesForAttachmentWithAbsPath[i]);
						mimeFileAttachBodyPart.setDataHandler(new DataHandler(
								fds));
						mimeFileAttachBodyPart.setFileName(fds.getName());

						mp.addBodyPart(mimeFileAttachBodyPart);
						message.setContent(mp);
					}
				}
			}
			// aalhat MR 15226
//			ReportZipper.mailDeliveryFlag = true;
			Transport.send(message);
		} catch (MessagingException ex) {
//			loge("\nCannot send email. CHECK if SMTP Host is up and Running:\n"
//					+ ex);
			System.err
					.println("Cannot send email. CHECK if SMTP Host is up and Running:"
							+ ex);
			// aalhat MR 15226
//			ReportService.MAIL_SERVER_DOWN = true;
//			ReportZipper.mailDeliveryFlag = false;
			ex.printStackTrace();
		}
	}

	/**
	 * This method sends changed password to the user's configured email
	 * address.
	 * 
	 * @param aSubject
	 *            Subject of e-mail
	 * @param aBody
	 *            Contents of e-mail
	 * @param aToEmailAddr
	 *            To
	 */
	public void sendEmailChangePassword(String aSubject, String aBody,
			String aToEmailAddr) throws MessagingException {

		// Session session = Session.getDefaultInstance(smtpHostProperties,
		// null);
		Session session = Session.getInstance(smtpHostProperties);
		MimeMessage message = new MimeMessage(session);

		try {
			InternetAddress mailFromAddress = new InternetAddress();
//			java.util.ResourceBundle appEnvBundle = java.util.ResourceBundle
//					.getBundle("Environment");
			String fromInMail = "IDeaSTranportService@something";
			mailFromAddress.setAddress(fromInMail);

			String bToEmailAddress = aToEmailAddr.lastIndexOf(";") == aToEmailAddr
					.length() - 1 ? aToEmailAddr.substring(0,
					aToEmailAddr.length() - 1) : aToEmailAddr;
			message.setFrom(mailFromAddress); // single address
			message.addRecipients(Message.RecipientType.TO,
					bToEmailAddress.replaceAll(";", ",")/* "Prasad.Kunte@ideas.com" */);

			// message.setSubject(aSubject);
			message.setSubject(aSubject, "utf-8");

			// Prafulla - REPLY TO
			message.setReplyTo(message.getFrom()); // single address
			// Prafulla - REPLY TO

			Multipart mp = new MimeMultipart();
			// ADD BODY
			/*
			 * Changed by Anushka for MR-9518
			 */
			// BodyPart mimeBodyPart=new MimeBodyPart();
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			// mimeBodyPart.setText(aBody + reportListingForMailBody);
			mimeBodyPart.setText(aBody, "utf-8");
			/*
			 * Changed by Anushka for MR-9518
			 */
			mp.addBodyPart(mimeBodyPart);
			message.setContent(mp);

//			ReportZipper.mailDeliveryFlag = true;
			Transport.send(message);
		} catch (MessagingException ex) {
//			loge("\nCannot send email. CHECK if SMTP Host is up and Running:\n"
//					+ ex);
//			System.err
//					.println("Cannot send email. CHECK if SMTP Host is up and Running:"
//							+ ex);
//			// aalhat MR 15226
//			logger.error("Exception occured in add method:", ex);
			throw new MessagingException("User.ForgotPassword.Email.Failed");
		} catch (Exception ae) {
//			logger.error("Exception occured in add method:", ae);
			throw new MessagingException("User.ForgotPassword.Email.Failed");
		}
	}

	public void sendEmailForDisabledReports(String aSubject, String aBody,
			String aToEmailAddr) {

		Session session = Session.getDefaultInstance(smtpHostProperties, null);
		MimeMessage message = new MimeMessage(session);

		try {
			InternetAddress mailFromAddress = new InternetAddress();
			mailFromAddress
					.setAddress("IDeaS-Scheduled-Reports@aspmail.ideas.com");
			String bToEmailAddress = aToEmailAddr.lastIndexOf(";") == aToEmailAddr
					.length() - 1 ? aToEmailAddr.substring(0,
					aToEmailAddr.length() - 1) : aToEmailAddr;
			message.setFrom(mailFromAddress);
			message.addRecipients(Message.RecipientType.TO,
					bToEmailAddress.replaceAll(";", ",")/* "Prasad.Kunte@ideas.com" */);
			message.setSubject(aSubject);
			message.setReplyTo(message.getFrom());
			Multipart mp = new MimeMultipart();
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setText(aBody, "utf-8");
			mp.addBodyPart(mimeBodyPart);
			message.setContent(mp);
			Transport.send(message);
		} catch (MessagingException ex) {
			/*loge("\n Cannot send email for Disabled reports. CHECK if SMTP Host is up and Running:\n"
					+ ex);*/
			System.err
					.println("Cannot send email for Disabled reports. CHECK if SMTP Host is up and Running:"
							+ ex);
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Properties smtpHostProperties = new Properties();
		String smtpHost = "mailhost.fyi.sas.com";
		smtpHostProperties.put("mail.smtp.host", smtpHost);
		Emailer email = new Emailer(smtpHostProperties);
		/*
		 * email.sendEmailChangePassword("Password Has been changed",
		 * "User ideas_adm,your password is ideas_adm", "amundaye@ideas.com");
		 */
		// try {
		// email.sendEmailChangePassword("Password Has been changed",
		// "User ideas_adm,your password is ideas_adm",
		// "abhilash.veeragouni@ideas.com");
		// } catch (MessagingException e) {
		// e.printStackTrace();
		// }
		String emailBody = "Dear ideas_adm, <br/><br/> "
				+ "54321"
				+ " is your One-Time Password(OTP) to log in to Ideas G2 <br/>"
				+ "Application. It is valid for your current login session only. <br/>"
				+ "Requested on " + new Date() + ". <br/>" + "Regards, <br/>"
				+ "Ideas Care.<br/>";
		String aToEmailAddr = "singhkmabhishek@gmail.com";

		try {
		email.sendEmailChangePassword("OTP for Ideas G2 Login",
				emailBody,
				aToEmailAddr);
	} catch (MessagingException e) {
		e.printStackTrace();
	}
	}
}


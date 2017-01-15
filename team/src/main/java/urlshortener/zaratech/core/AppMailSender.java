package urlshortener.zaratech.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class AppMailSender {

	private static final Logger logger = LoggerFactory.getLogger(AppMailSender.class);
	
	@Value("${gmail.user}")
	private static String from;

	@Value("${gmail.pass}")
	private static String pass;

	public static void sendMail(String to, BufferedImage image) {

		String text = "There is your QR code.";
		String subject = "QR code";

		try {
			
			// set properties
			Properties prop = System.getProperties();
			prop.put("mail.transport.protocol", "smtp");
			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.starttls.enable", "true");
			prop.put("mail.smtp.trust", "smtp.gmail.com");
			prop.put("mail.smtp.host", "smtp.gmail.com");
			prop.put("mail.smtp.port", "587");

			Session mailSession = Session.getInstance(prop);
			Transport transport = mailSession.getTransport();

			// build message
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			MimeMultipart content = new MimeMultipart();
			BodyPart bodyPart = new MimeBodyPart();
			MimeBodyPart imagePart = new MimeBodyPart();
			
			// body
			bodyPart.setContent(text, "text/html");
			content.addBodyPart(bodyPart);
			
			// image
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			byte[] imageArray = baos.toByteArray();
			DataSource ds = new ByteArrayDataSource(imageArray, "image/png");
			DataHandler handler = new DataHandler(ds);
			imagePart.setDataHandler(handler);
			imagePart.setFileName("QR.png");
			
			content.addBodyPart(imagePart);

			message.setContent(content);

			// send email
			transport.connect(from, pass);
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
			logger.info("Mail sent."); 
			
		} catch (MessagingException e) {

			logger.info("Error sendig mail."); 
			e.printStackTrace();
		} catch (IOException e) {

			logger.info("Mail can't be sended."); 
			e.printStackTrace();
		}
	}
}

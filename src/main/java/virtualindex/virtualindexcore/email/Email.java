package virtualindex.virtualindexcore.email;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.configuration.ConfigSetup;

 
public class Email 
{
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	
	public  void send (String subjectStr, String messageStr) throws IOException, JSONException
	{
		String mailPrefix = ConfigSetup.getValue("version");
		// Recipient's email ID needs to be mentioned.
	      String to = "philippe.aron@gmail.com";//change accordingly

	      // Sender's email ID needs to be mentioned
	      String from = "virtualindexmail@gmail.com";//change accordingly
	      final String username = "virtualindexmail@gmail.com";//change accordingly
	      final String password = "athena77";//change accordingly

	      String host = "smtp.gmail.com";

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	      new javax.mail.Authenticator() {
	         protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	         }
	      });

	      try {
	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	         InternetAddress.parse(to));

	         // Set Subject: header field
	         message.setSubject("["+mailPrefix+"] - "+subjectStr);

	         // Now set the actual message
	         message.setText(messageStr);

	         // Send message
	         Transport.send(message);

	         logger.info("Email sent successfully");

	      } catch (MessagingException e) {
	            throw new RuntimeException(e);
	      }
	   }
	}

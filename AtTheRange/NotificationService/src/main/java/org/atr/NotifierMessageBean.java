package org.atr;

import java.util.Properties;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Message-Driven Bean implementation class for: NotifierBean
 *
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty (propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty (propertyName = "destination", propertyValue = "java:/queue/notification")
		}, 
		mappedName = "jms.queue.notificationQueue")
public class NotifierMessageBean implements MessageListener {
	
	private static Session session;
	
	{
		final String username = "jmsimpson68@gmail.com";
		final String password = "1luvg0lf";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

	}

    /**
     * Default constructor. 
     */
    public NotifierMessageBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        System.out.println("Got the message: "+message.toString());
        
        MapMessage msg = (MapMessage) message;
        
        try {
			String email = msg.getString("email");
	        String title = msg.getString("title");
	        String emailMesage = msg.getString("message");
	        
	        sendEmail(email, title, emailMesage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
	public void sendEmail(String emailAddress, String subject, String messageText) {

 
		try {
 
			javax.mail.Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("rso-onduty@gmail.com"));
			message.setRecipients(javax.mail.Message.RecipientType.TO,
				InternetAddress.parse(emailAddress));
			message.setSubject(subject);
			message.setText(messageText);
 
			Transport.send(message);
 
			System.out.println("Done sending email");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}

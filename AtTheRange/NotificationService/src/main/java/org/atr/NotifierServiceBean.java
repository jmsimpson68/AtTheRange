package org.atr;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

/**
 * Session Bean implementation class NotifierServiceBean
 */
@Stateless
public class NotifierServiceBean {
	@Resource(name="java:/ConnectionFactory")
	ConnectionFactory connFactory;

	@Resource(name="java:/queue/notification")
	Queue notificationQueue;
	
    /**
     * Default constructor. 
     */
    public NotifierServiceBean() {
        // TODO Auto-generated constructor stub
    }
    
	public void notifiy(String rsoEmailAddress, String title, String message) {
		
		try {
			System.out.println("Sending message on Queue...");
			javax.jms.QueueConnection connection = (QueueConnection) connFactory.createConnection();
			connection.start();
			
			QueueSession session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(notificationQueue);
						
			MapMessage queueMsg = session.createMapMessage();
			
			queueMsg.setString("email", rsoEmailAddress);
			queueMsg.setString("title", title);
			queueMsg.setString("message", message);
			
			sender.send(queueMsg);
			System.out.println("Queue Message Sent: "+queueMsg.toString()+" input msg="+message);
			sender.close();
			session.commit();
			session.close();
			connection.stop();
			connection.close();
			
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	/*		
		try {
			System.out.println("Sending message on Topic...");
			javax.jms.TopicConnection connection = (TopicConnection) connFactory.createConnection();
			connection.start();
			
			TopicSession session = connection.createTopicSession(true, Session.AUTO_ACKNOWLEDGE);
			TopicPublisher sender = session.createPublisher(notificationTopic);
			
			TextMessage txtMsg = session.createTextMessage(message);
			
			sender.send(txtMsg);
			System.out.println("Topic Message Sent: "+txtMsg.toString()+" input msg="+message);
			sender.close();
			session.commit();
			session.close();
			connection.stop();
			connection.close();
			
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 */
		
		
		
	}

}

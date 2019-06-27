package com.solace.samples.tracer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.Destination;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.MapMessage;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.SDTException;
import com.solacesystems.jcsmp.SDTMap;
import com.solacesystems.jcsmp.StreamMessage;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.XMLMessageProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleProducer {
    private  String authHeader = "x-auth";
    private  String prefix = "services/";

    private  String host = "solacea";
    private  String username = "default";
    private  String vpnname = "default";
    private  String password = "default";

    // private  JCSMPSession session;
    private  CoolSession session;
    private  XMLMessageProducer prod;

    public void initializeSession() throws JCSMPException {
        log.info("initializeSession...");

        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, host); // host:port
        properties.setProperty(JCSMPProperties.USERNAME, username);
        properties.setProperty(JCSMPProperties.VPN_NAME, vpnname);
        properties.setProperty(JCSMPProperties.PASSWORD, password); 

        //session = JCSMPFactory.onlyInstance().createSession(properties);
        session = CoolFactory.onlyInstance().createSession(properties);
        
        session.connect();

    }

    public  void initializeQProducer() throws JCSMPException {

        prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            @Override
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);

            }
            @Override
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });

    }

    public  void sendMessage(String topicName,  String text) throws JCSMPException {
        final Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText(text);
        prod.send(msg,topic);
    }


    public  void doIt() throws JCSMPException, InterruptedException {

        initializeSession();
        initializeQProducer();
        sendMessage("payment/charge", "Credit Card Tx" );
        
        Thread.sleep(100000);
        System.out.println("Exiting...");
        session.closeSession();
    }
}

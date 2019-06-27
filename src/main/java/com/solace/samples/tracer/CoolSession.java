package com.solace.samples.tracer;

import com.google.common.collect.ImmutableMap;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.XMLMessageProducer;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoolSession {
    private JCSMPSession session = null;
    private static CoolFactory instance = null;
    private Tracer tracer;


    public CoolSession(Tracer aTracer, JCSMPSession session) {
        this.session = session;
        this.tracer = aTracer;
    }

    public void connect() throws JCSMPException {
        sayHello("session","created");
        sayHello("session","connect");

        session.connect();
    }
    public XMLMessageProducer getMessageProducer(JCSMPStreamingPublishEventHandler callback) throws JCSMPException {
        sayHello("session","getMessageProducer");

        return(session.getMessageProducer(callback));
        
    }
    public void closeSession(){
        sayHello("session","close");

        session.closeSession();
    }

    private void sayHello(String spanLabel, String message) {
        Span span = tracer.buildSpan(spanLabel).start();
        span.setTag("hello-to", message);

        // String helloStr = String.format("Hello, %s!", helloTo);
        span.log(ImmutableMap.of("event", "string-format", "value", message));

        // System.out.println(helloStr);
        span.log(ImmutableMap.of("event", "println"));

        span.finish();
    }


}    

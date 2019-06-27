package com.solace.samples.tracer;

import com.google.common.collect.ImmutableMap;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoolFactory {
    private static JCSMPFactory parentFactory = null;
    private static CoolFactory instance = null;
    private static Tracer tracer;

    private CoolFactory() {
        log.info("CoolFactory");
        parentFactory = JCSMPFactory.onlyInstance();
    }

    public static CoolFactory onlyInstance() {
        log.info("onlyInstance");

        if (instance == null) {
            instance = new CoolFactory();
            tracer = initTracer("solace-tracer");

        }
        return (instance);
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


    public CoolSession createSession(JCSMPProperties properties) throws InvalidPropertiesException {
        log.info("createSession");
        sayHello("session","created");
        JCSMPSession session = parentFactory.createSession(properties);
        CoolSession coolSession = new CoolSession(tracer, session);
        return coolSession;
    }

    public static JaegerTracer initTracer(String service) {
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration(service).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

}    

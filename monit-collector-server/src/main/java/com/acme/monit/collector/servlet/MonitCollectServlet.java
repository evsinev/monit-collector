package com.acme.monit.collector.servlet;

import com.acme.monit.collector.service.collect.IMonitCollectService;
import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;
import com.acme.monit.collector.service.collect.messages.MonitCollectResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payneteasy.jetty.util.SafeHttpServlet;
import com.payneteasy.jetty.util.SafeServletRequest;
import com.payneteasy.jetty.util.SafeServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

public class MonitCollectServlet extends SafeHttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger( MonitCollectServlet.class );

    private final ObjectMapper mapper = new XmlMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final IMonitCollectService service;
    private final File eventsDir;

    public MonitCollectServlet(IMonitCollectService service, File eventsDir) {
        this.service   = service;
        this.eventsDir = eventsDir;
    }

    @Override
    protected void doSafePost(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        String requestBody = aRequest.getRequestBody();


         LOG.trace("Received {}", requestBody);
        try {
            MonitCollectRequest monitCollectRequest = mapper.readValue(requestBody, MonitCollectRequest.class);
            writeBody(monitCollectRequest, requestBody);

            MonitCollectResponse monitCollectResponse = service.receiveMonitCollect(monitCollectRequest);
            aResponse.setContentType("application/xml");
            aResponse.write(mapper.writeValueAsString(monitCollectResponse));
        } catch (JsonProcessingException e) {
            LOG.error("Cannot parse message", e);
            throw new IllegalStateException("Cannot parse message", e);
        }

    }

    private void writeBody(MonitCollectRequest aRequest, String aBody) {
        File file = new File(eventsDir, aRequest.getServer().getLocalHostname() + ".xml");
        try(FileWriter out = new FileWriter(file)) {
            JsonNode tree = mapper.readTree(aBody);
            mapper.writeValue(out, tree);
        } catch (Exception e) {
            LOG.error("Cannot write last status", e);
        }

    }
}

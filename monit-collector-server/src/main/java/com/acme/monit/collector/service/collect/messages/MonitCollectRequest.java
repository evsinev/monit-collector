package com.acme.monit.collector.service.collect.messages;

import com.acme.monit.collector.service.collect.model.MonitEvent;
import com.acme.monit.collector.service.collect.model.MonitPlatform;
import com.acme.monit.collector.service.collect.model.MonitServer;
import com.acme.monit.collector.service.collect.model.MonitService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitCollectRequest {

    @JacksonXmlProperty(isAttribute = true)
    String id;

    MonitServer server;

    MonitEvent  event;

    MonitPlatform platform;

    List<MonitService> services;
}

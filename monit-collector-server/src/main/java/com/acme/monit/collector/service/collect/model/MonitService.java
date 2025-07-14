package com.acme.monit.collector.service.collect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitService {
    @JacksonXmlProperty(isAttribute = true)
    String name;

    int type;
    int status;

    MonitServiceBlock block;
}

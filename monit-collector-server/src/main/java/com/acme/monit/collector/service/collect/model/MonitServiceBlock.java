package com.acme.monit.collector.service.collect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class MonitServiceBlock {
    double percent;
    double usage;
    double total;
}

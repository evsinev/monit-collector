package com.acme.monit.collector.service.host.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class HostListItem {
    String hostname;
    long   lastSeenMs;
    String lastSeenFormatted;
    String lastSeenAgeFormatted;
    int    cpuCount;
    String memorySizeFormatted;
    String swapSizeFormatted;
    String rootFsSizeFormatted;
    String rootFsPercentUsedFormatted;
}

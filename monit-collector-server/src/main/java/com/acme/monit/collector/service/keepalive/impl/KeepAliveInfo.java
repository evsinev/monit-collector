package com.acme.monit.collector.service.keepalive.impl;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder(toBuilder = true)
public class KeepAliveInfo {
    String hostname;
    long   lastSeen;
    long   lastFired;

}

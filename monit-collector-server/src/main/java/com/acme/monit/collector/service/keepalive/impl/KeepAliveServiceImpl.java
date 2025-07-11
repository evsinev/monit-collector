package com.acme.monit.collector.service.keepalive.impl;

import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;
import com.acme.monit.collector.service.keepalive.IKeepAliveService;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static com.payneteasy.jetty.util.Strings.isEmpty;

public class KeepAliveServiceImpl implements IKeepAliveService {

    private final ConcurrentMap<String, KeepAliveInfo> map;

    public KeepAliveServiceImpl(ConcurrentMap<String, KeepAliveInfo> map) {
        this.map = map;
    }

    @Override
    public void addMessage(MonitCollectRequest aRequest) {
        Optional<String> hostnameOpt = extractHostname(aRequest);
        if (!hostnameOpt.isPresent()) {
            return;
        }

        String hostname = hostnameOpt.get();

        map.compute(hostname, (mapHostname, mapKeepAlive) -> {
            if (mapKeepAlive == null) {
                return KeepAliveInfo.builder()
                        .hostname(hostname)
                        .lastSeen(System.currentTimeMillis())
                        .lastFired(0)
                        .build();
            }

            return mapKeepAlive.toBuilder()
                    .lastSeen(System.currentTimeMillis())
                    .lastFired(0)
                    .build();
        });

    }

    private Optional<String> extractHostname(MonitCollectRequest aRequest) {
        if (aRequest == null) {
            return Optional.empty();
        }

        if (aRequest.getServer() == null) {
            return Optional.empty();
        }

        if (isEmpty(aRequest.getServer().getLocalHostname())) {
            return Optional.empty();
        }

        return Optional.of(aRequest.getServer().getLocalHostname());
    }
}

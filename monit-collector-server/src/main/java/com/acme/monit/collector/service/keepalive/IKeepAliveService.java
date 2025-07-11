package com.acme.monit.collector.service.keepalive;

import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;

public interface IKeepAliveService {

    void addMessage(MonitCollectRequest aRequest);

}

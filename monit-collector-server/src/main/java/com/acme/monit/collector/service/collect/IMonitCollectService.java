package com.acme.monit.collector.service.collect;

import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;
import com.acme.monit.collector.service.collect.messages.MonitCollectResponse;

public interface IMonitCollectService {

    MonitCollectResponse receiveMonitCollect(MonitCollectRequest aRequest);

}

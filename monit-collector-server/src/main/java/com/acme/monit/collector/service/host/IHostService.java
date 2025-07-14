package com.acme.monit.collector.service.host;

import com.acme.monit.collector.service.host.messages.HostListRequest;
import com.acme.monit.collector.service.host.messages.HostListResponse;

public interface IHostService {

    HostListResponse listAllHosts(HostListRequest aRequest);
}

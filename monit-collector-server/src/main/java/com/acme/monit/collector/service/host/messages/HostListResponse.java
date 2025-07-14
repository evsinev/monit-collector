package com.acme.monit.collector.service.host.messages;

import com.acme.monit.collector.service.host.model.HostListItem;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class HostListResponse {
    List<HostListItem> hosts;
}

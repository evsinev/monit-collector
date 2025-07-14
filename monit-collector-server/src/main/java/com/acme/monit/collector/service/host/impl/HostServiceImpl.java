package com.acme.monit.collector.service.host.impl;

import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;
import com.acme.monit.collector.service.collect.model.MonitPlatform;
import com.acme.monit.collector.service.collect.model.MonitService;
import com.acme.monit.collector.service.collect.model.MonitServiceBlock;
import com.acme.monit.collector.service.host.IHostService;
import com.acme.monit.collector.service.host.messages.HostListRequest;
import com.acme.monit.collector.service.host.messages.HostListResponse;
import com.acme.monit.collector.service.host.model.HostListItem;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveInfo;
import com.acme.monit.collector.util.DateFormats;
import com.acme.monit.collector.util.Dates;
import com.acme.monit.collector.util.SizeFormatter;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class HostServiceImpl implements IHostService {

    private static final Logger LOG = LoggerFactory.getLogger(HostServiceImpl.class );

    private static final DateTimeFormatter   FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss Z")
            .withZone(ZoneId.of("Europe/Moscow"));

    private static final MonitCollectRequest EMPTY = MonitCollectRequest.builder().build();
    private static final Gson                GSON  = new Gson();
    public static final MonitService EMPTY_SERVICE = MonitService.builder().build();

    private final ConcurrentMap<String, KeepAliveInfo> map;
    private final File                                 dir;

    public HostServiceImpl(ConcurrentMap<String, KeepAliveInfo> map, File dir) {
        this.map = map;
        this.dir = dir;
    }

    @Override
    public HostListResponse listAllHosts(HostListRequest aRequest) {
        long now = Instant.now().toEpochMilli();


        List<HostListItem> hosts = map.values()
                .stream()
                .sorted(Comparator.comparing(KeepAliveInfo::getHostname))
                .map(it -> toItem(it, now))
                .toList();

        return HostListResponse.builder()
                .hosts(hosts)
                .build();
    }

    private HostListItem toItem(KeepAliveInfo it, long now) {
        MonitCollectRequest request       = loadRequest(it.getHostname());
        MonitPlatform       platform      = request.getPlatform() != null ? request.getPlatform() : MonitPlatform.builder().build();
        MonitService        rootFsService = findRootFs(request);
        MonitServiceBlock   block         = rootFsService.getBlock() != null ? rootFsService.getBlock() : MonitServiceBlock.builder().build();

        return HostListItem.builder()
                .hostname             ( it.getHostname())
                .lastSeenFormatted    ( Dates.formatDateTime(new Date(it.getLastSeen()), LocalDateTime.now(ZoneId.systemDefault())))
                .lastSeenMs           ( it.getLastSeen())
                .lastSeenAgeFormatted ( DateFormats.formatAgo(now, it.getLastSeen()))
                .cpuCount             ( platform.getCpu())
                .memorySizeFormatted  ( SizeFormatter.formatSize(platform.getMemory()))
                .swapSizeFormatted    ( SizeFormatter.formatSize(platform.getSwap()))
                .rootFsSizeFormatted  ( SizeFormatter.formatSize((long)block.getTotal() * 1024 * 1024))
                .rootFsPercentUsedFormatted  ( block.getPercent() + "%")
                .build();
    }

    private MonitService findRootFs(MonitCollectRequest request) {
        if (request.getServices() == null) {
            return EMPTY_SERVICE;
        }

        for (MonitService service : request.getServices()) {
            if ("rootfs".equals(service.getName())) {
                return service;
            }
        }

        return EMPTY_SERVICE;
    }

    private MonitCollectRequest loadRequest(String hostname) {
        File file = new File(dir, hostname + ".json");
        if (!file.exists()) {
            LOG.warn("File {} does not exist", file);
            return EMPTY;
        }

        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, MonitCollectRequest.class);
        } catch (Exception e) {
            LOG.error("Cannot load file {}", file, e);
            return EMPTY;
        }
    }
}

package com.acme.monit.collector.service.keepalive.impl;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KeepAliveMapCreator {

    public static ConcurrentMap<String, KeepAliveInfo> createKeepAliveMap(File aDir) {
        ConcurrentMap<String, KeepAliveInfo> map   = new ConcurrentHashMap<>();
        File[]                               files = aDir.listFiles(pathname -> pathname.getName().endsWith(".xml"));
        if (files == null) {
            return map;
        }

        for (File file : files) {
            String hostname = file.getName().replace(".xml", "");
            map.put(hostname, KeepAliveInfo.builder()
                    .hostname(hostname)
                    .lastSeen(System.currentTimeMillis())
                    .build());
        }

        return map;
    }
}

package com.acme.monit.collector.service.keepalive.impl;

import com.payneteasy.telegram.bot.client.ITelegramService;
import com.payneteasy.telegram.bot.client.messages.TelegramMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class CheckKeepLiveTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CheckKeepLiveTask.class);

    private final ConcurrentMap<String, KeepAliveInfo> map;
    private final ITelegramService                     telegramService;
    private final long                                 chatId;
    private final long                                 thresholdMs;

    public CheckKeepLiveTask(ConcurrentMap<String, KeepAliveInfo> map, ITelegramService telegramService, long chatId, long thresholdMs) {
        this.map             = map;
        this.telegramService = telegramService;
        this.chatId          = chatId;
        this.thresholdMs     = thresholdMs;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                checkKeepAlive();
            } catch (Exception e) {
                LOG.error("Cannot check keep alive", e);
            }

            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                LOG.warn("Interrupted", e);
                return;
            }
        }
    }

    private void checkKeepAlive() {

        Set<String> hostnames = map.keySet();
        for (String hostname : hostnames) {
            KeepAliveInfo value = map.get(hostname);

            if (value == null) {
                continue;
            }

            LOG.debug("info is {}", value);

            boolean isOld        = value.getLastSeen() < System.currentTimeMillis() - thresholdMs;
            boolean isSentTooOld = value.getLastFired() == 0 || value.getLastFired() < System.currentTimeMillis() - thresholdMs * 30;

            if (isOld && isSentTooOld) {

                map.put(hostname, value.toBuilder()
                        .lastFired(System.currentTimeMillis())
                        .build());

                String message = "Server "
                        + value.getHostname()
                        + " did not send update for > "
                        + (thresholdMs / 1000)
                        + " seconds. Last seen " + new Date(value.getLastSeen())
                ;
                LOG.warn("Fire {}", message);

                telegramService.sendMessage(TelegramMessageRequest.builder()
                        .chatId(chatId)
                        .text(message)
                        .build());
            }
        }


    }
}

package com.acme.monit.collector;

import com.acme.monit.collector.service.collect.impl.MonitCollectServiceImpl;
import com.acme.monit.collector.service.host.IHostService;
import com.acme.monit.collector.service.host.impl.HostServiceImpl;
import com.acme.monit.collector.service.keepalive.IKeepAliveService;
import com.acme.monit.collector.service.keepalive.impl.CheckKeepLiveTask;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveInfo;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveMapCreator;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveServiceImpl;
import com.acme.monit.collector.service.telegram.ITelegramCheckService;
import com.acme.monit.collector.service.telegram.TelegramCheckServiceImpl;
import com.payneteasy.mini.core.context.IServiceContext;
import com.payneteasy.mini.core.context.IServiceCreator;
import com.payneteasy.mini.core.context.ServiceContextImpl;
import com.payneteasy.telegram.bot.client.ITelegramService;
import com.payneteasy.telegram.bot.client.http.TelegramHttpClientImpl;
import com.payneteasy.telegram.bot.client.impl.TelegramServiceImpl;

import java.util.concurrent.ConcurrentMap;

public class MonitCollectorFactory {

    private final IStartupConfig                       config;
    private final ConcurrentMap<String, KeepAliveInfo> map;
    private final IServiceContext                      context = new ServiceContextImpl();

    public MonitCollectorFactory(IStartupConfig aConfig) {
        config = aConfig;
        map    = KeepAliveMapCreator.createKeepAliveMap(aConfig.getLastXmlDir());
    }

    IKeepAliveService keepAliveService() {
        return singleton(IKeepAliveService.class, () -> new KeepAliveServiceImpl(map));
    }

    CheckKeepLiveTask checkKeepLiveTask() {
        return singleton(CheckKeepLiveTask.class, () -> new CheckKeepLiveTask(map, telegramService(), config.getTelegramTechnicalChatId(), config.getThresholdMs()));
    }

    TelegramHttpClientImpl telegramHttpClient() {
        return singleton(TelegramHttpClientImpl.class, () -> new TelegramHttpClientImpl(config.getTelegramToken()));
    }

    public ITelegramService telegramService() {
        return singleton(ITelegramService.class, () -> new TelegramServiceImpl(telegramHttpClient()));
    }

    ITelegramCheckService telegramCheckService() {
        return singleton(ITelegramCheckService.class, () -> new TelegramCheckServiceImpl(telegramService()));
    }


    MonitCollectServiceImpl monitCollectService() {
        return singleton(MonitCollectServiceImpl.class, () -> new MonitCollectServiceImpl(
                  telegramService()
                , config.getTelegramChatId()
                , config.getTelegramTechnicalChatId()
                , config.getLastJsonDir()
                , keepAliveService()
        ));
    }

    public IHostService hostService() {
        return new HostServiceImpl(map, config.getLastJsonDir());
    }

    private <T> T singleton(Class<? super T> aClass, IServiceCreator<T> aCreator) {
        return context.singleton(aClass, aCreator);
    }

}

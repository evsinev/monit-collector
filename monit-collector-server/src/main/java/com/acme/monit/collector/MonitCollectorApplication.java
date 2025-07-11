package com.acme.monit.collector;

import com.acme.monit.collector.service.collect.impl.MonitCollectServiceImpl;
import com.acme.monit.collector.service.keepalive.IKeepAliveService;
import com.acme.monit.collector.service.keepalive.impl.CheckKeepLiveTask;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveInfo;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveMapCreator;
import com.acme.monit.collector.service.keepalive.impl.KeepAliveServiceImpl;
import com.acme.monit.collector.service.telegram.ITelegramCheckService;
import com.acme.monit.collector.service.telegram.TelegramCheckServiceImpl;
import com.acme.monit.collector.servlet.MonitCollectServlet;
import com.payneteasy.jetty.util.*;
import com.payneteasy.mini.core.app.AppContext;
import com.payneteasy.telegram.bot.client.ITelegramService;
import com.payneteasy.telegram.bot.client.http.TelegramHttpClientImpl;
import com.payneteasy.telegram.bot.client.impl.TelegramServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

import static com.payneteasy.mini.core.app.AppRunner.runApp;
import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MonitCollectorApplication {

    private static final Logger LOG = LoggerFactory.getLogger( MonitCollectorApplication.class );

    public static void main(String[] args) {
        runApp(args, MonitCollectorApplication::run);
    }

    private static void run(AppContext appContext) {
        IStartupConfig config = getStartupParameters(IStartupConfig.class);

        config.getLastJsonDir().mkdirs();
        config.getLastXmlDir().mkdirs();

        TelegramHttpClientImpl               httpClient        = new TelegramHttpClientImpl(config.getTelegramToken());
        ITelegramService                     telegramService   = new TelegramServiceImpl(httpClient);
        ITelegramCheckService                checkService      = new TelegramCheckServiceImpl(telegramService);
        ConcurrentMap<String, KeepAliveInfo> map               = KeepAliveMapCreator.createKeepAliveMap(config.getLastXmlDir());
        IKeepAliveService                    absentService     = new KeepAliveServiceImpl(map);
        CheckKeepLiveTask                    checkKeepLiveTask = new CheckKeepLiveTask(map, telegramService, config.getTelegramTechnicalChatId(), config.getThresholdMs());

        MonitCollectServiceImpl collectService  = new MonitCollectServiceImpl(
                telegramService
                , config.getTelegramChatId()
                , config.getTelegramTechnicalChatId()
                , config.getLastJsonDir()
                , absentService
        );

        checkService.checkChatAccess(config.getTelegramTechnicalChatId());

        Thread checkThread = new Thread(checkKeepLiveTask, "check-keep-alive");
        checkThread.start();

        JettyServer jetty = new JettyServerBuilder()
                .startupParameters(config)
                .contextOption(JettyContextOption.NO_SESSIONS)

                .filter("/*", new PreventStackTraceFilter())
                .servlet("/health", new HealthServlet())
                .servlet("/collect", new MonitCollectServlet(collectService, config.getLastXmlDir()))

                .shutdownListener(checkThread::interrupt)

                .build();


        jetty.startJetty();

    }

}

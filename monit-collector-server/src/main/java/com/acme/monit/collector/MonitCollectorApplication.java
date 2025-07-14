package com.acme.monit.collector;

import com.acme.monit.collector.service.host.IHostService;
import com.acme.monit.collector.service.host.messages.HostListRequest;
import com.acme.monit.collector.servlet.MonitCollectServlet;
import com.acme.monit.collector.servlet.PageReactServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.apiservlet.GsonJettyContextHandler;
import com.payneteasy.jetty.util.*;
import com.payneteasy.mini.core.app.AppContext;
import com.payneteasy.mini.core.error.handler.ApiExceptionHandler;
import com.payneteasy.mini.core.error.handler.ApiRequestValidator;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;

import static com.payneteasy.mini.core.app.AppRunner.runApp;
import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MonitCollectorApplication {

    public static void main(String[] args) {
        runApp(args, MonitCollectorApplication::run);
    }

    private static void run(AppContext appContext) {
        IStartupConfig config = getStartupParameters(IStartupConfig.class);

        config.getLastJsonDir().mkdirs();
        config.getLastXmlDir().mkdirs();

        MonitCollectorFactory factory = new MonitCollectorFactory(config);

        factory.telegramCheckService().checkChatAccess(config.getTelegramTechnicalChatId());

        Thread checkThread = new Thread(factory.checkKeepLiveTask(), "check-keep-alive");
        checkThread.start();

        JettyServer jetty = new JettyServerBuilder()
                .startupParameters(config)
                .contextOption(JettyContextOption.NO_SESSIONS)

                .filter("/*", new PreventStackTraceFilter())
                .servlet("/health", new HealthServlet())
                .servlet("/collect", new MonitCollectServlet(factory.monitCollectService(), config.getLastXmlDir()))
                .servlet("/ui/*", new PageReactServlet(config.assetsIndexJsUri(), config.assetsIndexCssUri()))
                .contextListener(context -> onContext(context, factory))
                .shutdownListener(checkThread::interrupt)

                .build();

        jetty.startJetty();

    }

    private static void onContext(ServletContextHandler context, MonitCollectorFactory factory) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        GsonJettyContextHandler gsonHandler = new GsonJettyContextHandler(
                context
                , gson
                , new ApiExceptionHandler()
                , new ApiRequestValidator()
        );

        IHostService hostService = factory.hostService();
        gsonHandler.addApi("/api/host/list/*", hostService::listAllHosts, HostListRequest.class);

    }

}

package com.acme.monit.collector;

import com.payneteasy.jetty.util.IJettyStartupParameters;
import com.payneteasy.startup.parameters.AStartupParameter;

import java.io.File;

public interface IStartupConfig extends IJettyStartupParameters {

    @AStartupParameter(name = "JETTY_CONTEXT", value = "/monit-collector")
    String getJettyContext();

    @AStartupParameter(name = "JETTY_PORT", value = "48080")
    int getJettyPort();

    @AStartupParameter(name = "TELEGRAM_CHAT_ID", value = "-1")
    long getTelegramChatId();

    @AStartupParameter(name = "TELEGRAM_TECHNICAL_CHAT_ID", value = "-1")
    long getTelegramTechnicalChatId();

    @AStartupParameter(name = "TELEGRAM_TOKEN", value = "no-token")
    String getTelegramToken();

    @AStartupParameter(name = "LAST_JSON_EVENT_DIR", value = "./target/last-json")
    File getLastJsonDir();

    @AStartupParameter(name = "LAST_EVENT_DIR", value = "./target/last-xml")
    File getLastXmlDir();

    @AStartupParameter(name = "THRESHOLD_MS", value = "120000")
    long getThresholdMs();

    @AStartupParameter(name = "ASSETS_INDEX_JS_URI", value = "/monit-collector-assets/index.js")
    String assetsIndexJsUri();

    @AStartupParameter(name = "ASSETS_INDEX_CSS_URI", value = "/monit-collector-assets/index.css")
    String assetsIndexCssUri();

}

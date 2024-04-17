package com.acme.monit.collector.service.telegram;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.telegram.bot.client.ITelegramService;
import com.payneteasy.telegram.bot.client.messages.TelegramGetUpdatesRequest;
import com.payneteasy.telegram.bot.client.messages.TelegramGetUpdatesResponse;
import com.payneteasy.telegram.bot.client.messages.TelegramMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramCheckServiceImpl implements ITelegramCheckService {

    private static final Logger LOG = LoggerFactory.getLogger( TelegramCheckServiceImpl.class );

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final ITelegramService telegramService;

    public TelegramCheckServiceImpl(ITelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void checkChatAccess(long aChatId) {
        LOG.debug("Checking telegram token ...");
        telegramService.getMe();
        LOG.debug("Token is OK");

        LOG.debug("Checking telegram chat id {} ...", aChatId);
        try {
            telegramService.sendMessage(TelegramMessageRequest.builder()
                    .text("Starting monit collector...")
                    .chatId(aChatId)
                    .build());
            LOG.debug("Chat id is OK");
        } catch (Exception e) {
            LOG.error("Chat id is bad", e);
            processWaitngEventFromChats();
        }
    }

    private void processWaitngEventFromChats() {
        LOG.debug("Waiting events from telegram ...");
        TelegramGetUpdatesResponse updates = telegramService.getUpdates(TelegramGetUpdatesRequest.builder()
                .build());

        LOG.debug("Received {}", gson.toJson(updates));


    }
}

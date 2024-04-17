package com.acme.monit.collector.service.collect.impl;

import com.acme.monit.collector.service.collect.IMonitCollectService;
import com.acme.monit.collector.service.collect.messages.MonitCollectRequest;
import com.acme.monit.collector.service.collect.messages.MonitCollectResponse;
import com.acme.monit.collector.service.collect.model.MonitEvent;
import com.acme.monit.collector.service.collect.model.MonitEventActionType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.telegram.bot.client.ITelegramService;
import com.payneteasy.telegram.bot.client.messages.TelegramMessageRequest;
import com.payneteasy.telegram.bot.client.model.ParseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

import static com.acme.monit.collector.service.collect.impl.TelegramEscaper.escapeTelegram;

public class MonitCollectServiceImpl implements IMonitCollectService {


    private static final Logger LOG = LoggerFactory.getLogger(MonitCollectServiceImpl.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final ITelegramService telegram;
    private final long             chatId;
    private final long             technicalChatId;
    private final File             lastStatusDir;

    public MonitCollectServiceImpl(ITelegramService telegram, long aChatId, long aTechnicalChatId, File aLastStatusDir) {
        this.telegram   = telegram;
        chatId          = aChatId;
        technicalChatId = aTechnicalChatId;
        lastStatusDir   = aLastStatusDir;
    }

    public MonitCollectResponse receiveMonitCollect(MonitCollectRequest aRequest) {
        LOG.debug("Request is {}", gson.toJson(aRequest));

        if (aRequest == null) {
            return MonitCollectResponse.builder()
                    .id("no request")
                    .build();
        }

        saveEvent(aRequest);
        processEvent(aRequest);

        return MonitCollectResponse.builder()
                .id(aRequest.getId())
                .build();
    }

    private void saveEvent(MonitCollectRequest aRequest) {
        File file = new File(lastStatusDir, aRequest.getServer().getLocalHostname() + ".json");
        try (FileWriter out = new FileWriter(file)) {
            gson.toJson(aRequest, out);
        } catch (Exception e) {
            LOG.error("Cannot write last status", e);
        }
    }

    private void processEvent(MonitCollectRequest aRequest) {
        if (aRequest == null || aRequest.getEvent() == null) {
            return;
        }

        MonitEvent event = aRequest.getEvent();

        String message = String.format(
                  "%s state: %s\n"
                + "%s type: %s\n"
                + "%s action: %s\n"
                + "```json\n"
                + "%s\n"
                + "```"
                , event.getStateType().getEmoji()  , event.getStateType()
                , event.getType().getEmoji()       , event.getType()
                , event.getActionType().getEmoji() , event.getActionType()
                , escapeTelegram(gson.toJson(aRequest))
        );

        telegram.sendMessage(TelegramMessageRequest.builder()
                .parseMode ( ParseMode.MarkdownV2 )
                .chatId    ( technicalChatId      )
                .text      ( message              )
                .build());

        if (event.getActionType() == MonitEventActionType.ALERT) {
            telegram.sendMessage(TelegramMessageRequest.builder()
                    .chatId(chatId)
                    .text(
                            String.format(
                                    "%s %s %s %s\n"
                                  + "%s"
                            , event.getStateType().getEmoji()
                            , event.getType().getEmoji()
                            , aRequest.getServer().getLocalHostname()
                            , event.getService()
                            ,
                                trim(
                                    event.getMessage()
                                )
                            )
                    )
                    .build());
        }

    }

    private String trim(String message) {
        return message != null ? message.trim() : "";
    }
}

package com.acme.monit.collector.service.collect.model;

public enum MonitEventActionType {
    IGNORE      ("\uD83D\uDE48"), // 🙈
    ALERT       ("\uD83D\uDD14"), // 🔔
    RESTART     ("\uD83D\uDD04"), // 🔄
    STOP        ("⏹\uFE0F"), // ⏹️
    EXEC        ("\uD83D\uDD74"), // 🕴
    UNMONITOR   ("\uD83D\uDCF5"), // 📵
    START       ("▶\uFE0F"), // ▶️
    MONITOR     ("\uD83D\uDCDF"), // 📟
    UNKNOWN     ("❓"), // ❓
    ;

    private final String emoji;

    MonitEventActionType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}

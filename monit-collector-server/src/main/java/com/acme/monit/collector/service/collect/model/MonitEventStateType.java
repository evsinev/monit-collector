package com.acme.monit.collector.service.collect.model;

public enum MonitEventStateType {
    SUCCEEDED   (0x00, "✅"          ), // ✅
    FAILED      (0x01, "\uD83D\uDE21"), // 😡
    CHANGED     (0x02, "\uD83D\uDD00"), // 🔀️
    CHANGED_NOT (0x04, "\uD83D\uDE45"), // 🙅
    INIT        (0x08, "\uD83C\uDF0C"), // 🌌
    NONE        (0x08, "\uD83E\uDD37"), // 🤷
    UNKNOWN     (  -1, "\uD83E\uDD14"), // 🤔
    ;

    private final int code;
    private final String emoji;

    MonitEventStateType(int code, String emoji) {
        this.code  = code;
        this.emoji = emoji;
    }

    public int getCode() {
        return code;
    }

    public String getEmoji() {
        return emoji;
    }
}

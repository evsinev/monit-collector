package com.acme.monit.collector.service.collect.model;

public enum MonitEventServiceType {
    FILESYSTEM ( 0  , "\uD83D\uDCBE"), // 💾
    DIRECTORY  ( 1  , "\uD83D\uDCC1"), // 📁
    FILE       ( 2  , "\uD83D\uDCC3"), // 📃
    PROCESS    ( 3  , "\uD83D\uDDF3"), // 🗳
    HOST       ( 4  , "\uD83D\uDDA5"), // 🖥
    SYSTEM     ( 5  , "\uD83D\uDCBB"), // 💻
    FIFO       ( 6  , "\uD83C\uDFA2"), // 🎢
    PROGRAM    ( 7  , "\uD83D\uDCFA"), // 📺
    NET        ( 8  , "\uD83C\uDF10"), // 🌐
    UNKNOWN    ( -1 , "❓"          ), // ❓
    ;

    private final int    code;
    private final String emoji;

    MonitEventServiceType(int aCode, String aEmoji) {
        code = aCode;
        emoji = aEmoji;
    }

    public int getCode() {
        return code;
    }

    public String getEmoji() {
        return emoji;
    }
}

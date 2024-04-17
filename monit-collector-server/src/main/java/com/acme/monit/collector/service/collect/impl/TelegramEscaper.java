package com.acme.monit.collector.service.collect.impl;

import java.util.*;

public class TelegramEscaper {

    private static final Set<Character> CHARS = new HashSet<>(Arrays.asList('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!'));

    public static String escapeTelegram(String aMessage) {
        StringBuilder sb = new StringBuilder(aMessage.length());
        for (char c : aMessage.toCharArray()) {
            if(CHARS.contains(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}

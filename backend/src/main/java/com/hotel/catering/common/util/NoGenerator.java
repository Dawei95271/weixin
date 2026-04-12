package com.hotel.catering.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class NoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private NoGenerator() {
    }

    public static String next(String prefix) {
        int suffix = COUNTER.updateAndGet(current -> current >= 999 ? 1 : current + 1);
        return prefix + LocalDateTime.now().format(FORMATTER) + String.format("%03d", suffix);
    }
}

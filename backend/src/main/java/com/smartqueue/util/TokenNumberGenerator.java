package com.smartqueue.util;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TokenNumberGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);

    public String generate(String queuePrefix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int seq = counter.incrementAndGet() % 9999;
        return String.format("%s-%s-%04d", queuePrefix.substring(0, Math.min(3, queuePrefix.length())).toUpperCase(), date, seq);
    }
}

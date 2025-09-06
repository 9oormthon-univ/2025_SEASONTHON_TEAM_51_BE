package com.mockit.support.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class MarketCalendar {

    private static final LocalTime OPEN  = LocalTime.of(9, 0);
    private static final LocalTime CLOSE = LocalTime.of(15, 30);

    public boolean isOpen(LocalDateTime nowKst) {
        var t = nowKst.toLocalTime();
        return !t.isBefore(OPEN) && !t.isAfter(CLOSE);
    }

    public LocalDateTime nextOpen(LocalDateTime nowKst) {
        var date = nowKst.toLocalDate();
        var t = nowKst.toLocalTime();
        if (t.isAfter(CLOSE)) date = date.plusDays(1);
        // 주말 보정
        switch (date.getDayOfWeek()) {
            case SATURDAY -> date = date.plusDays(2);
            case SUNDAY   -> date = date.plusDays(1);
            default -> {}
        }
        return date.atTime(OPEN);
    }
}

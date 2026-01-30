package com.apitest.ddulo.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    // Util: 날짜를 요일로(ex: 2026-01-28 -> WED) 변환하는 메서드
    public static String convertDateToDayKey(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return date.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase();
    }

    // Util: 시간을 10분 간격으로 내림 처리하는 메서드(ex: 14:37:21 -> "1430")
    public static String getCurrentTimeKey() {
        LocalTime now = LocalTime.now();

        // 10분 단위로 내림 (Floor) 계산
        // ex: 47분 -> 40분, 03분 -> 00분
        int minute = now.getMinute();
        int roundedMinute = (minute / 10) * 10;

        // 시간을 다시 설정 (초는 00으로)
        LocalTime targetTime = now.withMinute(roundedMinute).withSecond(0).withNano(0);

        // Redis 키 형식(HHmm)으로 변환
        // 예: 14:30:00 -> "1430"
        return targetTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }
}

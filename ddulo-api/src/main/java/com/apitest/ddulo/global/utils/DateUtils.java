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

    // Util: 요일을 키값으로(ex: WED -> 1) 변환하는 메서드
    // 평일:1, 토요일:2, 휴일/일요일:3
    public static String convertDayKeyToWeekTag(String dayKey) {
        if(dayKey.equals("SUN")) return "3";
        if(dayKey.equals("SAT")) return "2";
        return "1";
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

    // Util: 시간을 10분 간격으로 올림 처리하는 메서드(ex: 14:37:21 -> "1440")
    public static String getNextTimeKey() {
        LocalTime now = LocalTime.now();

        // 현재 기준 '내림' 분 계산 (이미 있는 로직)
        int minute = now.getMinute();
        int roundedMinute = (minute / 10) * 10;

        // 현재 구간의 시작 시간으로 설정 (ex: 14:32 -> 14:30:00)
        LocalTime currentBucketTime = now.withMinute(roundedMinute).withSecond(0).withNano(0);

        // 10분을 더함 (LocalTime이 알아서 59분 넘어가면 시단위 올려줌)
        LocalTime nextBucketTime = currentBucketTime.plusMinutes(10);

        return nextBucketTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }
}

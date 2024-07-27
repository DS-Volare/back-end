package com.example.volare.global.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final long SECOND = 1000; // MilliSecond
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;

    public static String convert(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        // 현재 시간
        long curTime = now.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        // 등록 시간
        long regTime = dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        // 지난 시간
        long diffTime = Math.abs(curTime - regTime);

        String msg;

        if (diffTime / SECOND < 60) {
            msg = diffTime / SECOND + "초 전";
        } else if (diffTime / MINUTE < 60) {
            msg = diffTime / MINUTE + "분 전";
        } else if (diffTime / HOUR < 24) {
            msg = DateTimeFormatter.ofPattern("HH:mm").format(dateTime);
        } else if (dateTime.getYear() == now.getYear()) {
            msg = DateTimeFormatter.ofPattern("MM/dd HH:mm").format(dateTime);
        } else {
            msg = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm").format(dateTime);
        }
        return msg;
    }

}

package hpPrice.common.dateTime;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter DC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter NAVER_DATE_FORMATTER_OLD = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
    private static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
    
    public static final DateTimeFormatter MY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"); // TODO 필요없으면 삭제

    public static LocalDateTime parseDcDateTime(String crawledDateTime) {
        return LocalDateTime.parse(crawledDateTime, DC_DATE_FORMATTER);
    }

    public static LocalDateTime parseNaverDateTime(String crawledDateTime) {
        return LocalDateTime.parse(crawledDateTime, NAVER_DATE_FORMATTER); // 게시글 시간 yyyy.MM.dd. HH:mm 형태

//        if (crawledDateTime.contains(":")) { // 오늘 올라온 글 (HH:mm)
//            return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.parse(crawledDateTime));
//        } else { // 어제 ~ (yyyy.MM.dd.)
//            return LocalDate.parse(crawledDateTime, NAVER_DATE_FORMATTER_OLD).atStartOfDay();
//        }

    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DC_DATE_FORMATTER);
    }
}

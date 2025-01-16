package hpPrice.common.dateTime;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeUtils {

    public static final DateTimeFormatter DC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");

    public static LocalDateTime parseDcDateTime(String crawledDateTime) {
        return LocalDateTime.parse(crawledDateTime, DC_DATE_FORMATTER);
    }

    public static LocalDateTime parseNaverDateTime(String crawledDateTime) {
        if (crawledDateTime.contains(":")) { // 오늘 올라온 글 (HH:mm)
            return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.parse(crawledDateTime));
        } else { // 어제 ~ (yyyy.MM.dd.)
            return LocalDate.parse(crawledDateTime, NAVER_DATE_FORMATTER).atStartOfDay();
        }

    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DC_DATE_FORMATTER);
    }


}

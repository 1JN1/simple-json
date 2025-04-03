package com.study.json.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class DateUtils {

    private static final Set<Class<?>> DATE_TYPES = new HashSet<>(Arrays.asList(
            Date.class,
            Calendar.class,
            LocalDate.class,
            LocalDateTime.class,
            ZonedDateTime.class,
            Instant.class,
            java.sql.Date.class,
            java.sql.Timestamp.class
    ));


    /**
     * 判断是否是日期类型
     *
     * @param obj
     * @return
     */
    public static Class<?> getDateType(Class<?> obj) {

        return DATE_TYPES.stream()
                .filter(clazz -> clazz.isAssignableFrom(obj))
                .findFirst()
                .orElse(null);
    }

}

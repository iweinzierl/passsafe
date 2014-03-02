package de.iweinzierl.passsafe.shared.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATABASE_DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static final FastDateFormat DATABASE_DATEFORMAT = FastDateFormat.getInstance(DATABASE_DATE_PATTERN);

    public static Date parseDatabaseDate(final String dbDate) {
        try {
            return new SimpleDateFormat(DATABASE_DATE_PATTERN).parse(dbDate);
        } catch (ParseException e) {
            LOGGER.error("Parsing database formatted date failed: {}", dbDate, e);
            return new Date();
        }
    }

    public static String formatDatabaseDate(final Date date) {
        return DATABASE_DATEFORMAT.format(date);
    }
}

package de.iweinzierl.passsafe.shared.utils;

import java.util.Date;

import org.joda.time.DateTime;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void testParseFormattedDate() throws Exception {

        DateTime currentTime = new DateTime(2014, 3, 3, 20, 8, 0);
        Date now = currentTime.toDate();

        String formatted = DateUtils.formatDatabaseDate(now);
        Date parsed = DateUtils.parseDatabaseDate(formatted);

        Assert.assertEquals(now.getTime(), parsed.getTime());
    }
}

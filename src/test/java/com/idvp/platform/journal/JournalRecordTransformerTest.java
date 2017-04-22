package com.idvp.platform.journal;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertNotNull;

public class JournalRecordTransformerTest {
    @Test
    public void testDateTimeParse() throws Exception {
        String line = "2017-04-17T15:51:32.548+03:00";
        LocalDateTime time = LocalDateTime.parse(line, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        assertNotNull(time);
    }

}
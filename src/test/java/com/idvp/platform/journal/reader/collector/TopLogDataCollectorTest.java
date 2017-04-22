package com.idvp.platform.journal.reader.collector;

import com.idvp.platform.journal.reader.model.LogDataBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TopLogDataCollectorTest {

    @Test
    public void testTopLogRecords() throws Exception {
        TopLogDataCollector topLogDataCollector = new TopLogDataCollector(3);

        LocalDateTime dateTime = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            dateTime = dateTime.plus(10, ChronoUnit.SECONDS);
            topLogDataCollector.add(new LogDataBuilder()
                    .withDate(dateTime)
                    .withId(i)
                    .build());
        }

        Assert.assertEquals(3, topLogDataCollector.getLogData().length);
        Assert.assertEquals(2, topLogDataCollector.getLogData()[0].getId());

        topLogDataCollector.add(new LogDataBuilder()
                .withDate(dateTime.plus(Duration.ofSeconds(100)))
                .withId(3)
                .build());

        Assert.assertEquals(3, topLogDataCollector.getLogData().length);
        Assert.assertEquals(3, topLogDataCollector.getLogData()[0].getId());
    }
}
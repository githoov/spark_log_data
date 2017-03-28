package com.looker.logDataWebinar.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.looker.logDataWebinar.LogLine;
import com.looker.logDataWebinar.LogParser;

/**
 * @author kaidul
 *
 */
public class LogParserTest {

    private static String log = "2.174.143.4 - - [09/May/2016:05:56:03 +0000]  \"GET  /department HTTP/1.1\" 200 1226  \"-\" \"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6.3; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; InfoPath.2)\" \"USER=0;NUM=9\"";
    private static Logger logger = Logger.getLogger(LogParserTest.class);

    @Before
    public void setup() {
        BasicConfigurator.configure();
    }

    @Test
    public void testEventLogParser() {
        LogLine logLine = LogParser.parseLog(log);
        System.out.println(logLine);
    }

}

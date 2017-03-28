package com.looker.logDataWebinar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kaidul
 *
 */
public class LogParser {

    private static Pattern logPattern = Pattern.compile(
            "^([d\\+.]+)\\s+(\\S+)\\s+(\\S+)\\s+(?:\\[)(.*)(?:\\])\\s+(?:\\p{Punct})([A-Z]+)\\s+(\\S+)\\s+(\\S+)(?:\\p{Punct})\\s+(\\S+)\\s+(\\S+)\\s+(?:\\p{Punct})(\\S+)(?:\\p{Punct})\\s+(?:\\p{Punct})([^\"]*)(?:\")\\s+(.*)$");
    
    public LogLine parseLog(String logtxt) {
        Matcher logMatcher = logPattern.matcher(logtxt);
        if (!logMatcher.matches()) {
            System.out.println("Not matched");
            return null;
        }
        System.out.println(logMatcher.groupCount());
        for(int i = 1; i <= logMatcher.groupCount(); i++) {
            System.out.println(logMatcher.group(i));
        }
        return null;
    }
}

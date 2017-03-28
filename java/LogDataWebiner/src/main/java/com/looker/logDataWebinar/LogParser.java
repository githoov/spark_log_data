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
            "^([\\d+.]+)\\s+(\\S+)\\s+(\\S+)\\s+(?:\\[)(.*)(?:\\])\\s+(?:\\p{Punct})([A-Z]+)\\s+(\\S+)\\s+(\\S+)(?:\\p{Punct})\\s+(\\S+)\\s+(\\S+)\\s+(?:\\p{Punct})(\\S+)(?:\\p{Punct})\\s+(?:\\p{Punct})([^\"]*)(?:\")\\s+(.*)$");
    
    public static LogLine parseLog(String logtxt) {
    	Matcher logMatcher = logPattern.matcher(logtxt);
		if(!logMatcher.matches()) {
			return null;
		}
		String ipAddress = logMatcher.group(1);
		String identifier = logMatcher.group(2);
	    String userId = logMatcher.group(3);
	    String creationTime = logMatcher.group(4);
	    String method = logMatcher.group(5);
	    String uri = logMatcher.group(6);
	    String protocol = logMatcher.group(7);
	    String status = logMatcher.group(8);
	    String size = logMatcher.group(9);
	    String referer = logMatcher.group(10);
	    String agent = logMatcher.group(11);
	    String userMetaInfo = logMatcher.group(12);
	    
		return new LogLine(ipAddress, identifier, userId, creationTime, method, uri, protocol, status, size, referer, agent, userMetaInfo);
    }
}

package com.looker.logDataWebinar;

import java.io.Serializable;

/**
 * @author kaidul
 *
 */
public class LogLine implements Serializable {

    private static final long serialVersionUID = 6972814572157146958L;
    
    private String ipAddress;
    private String identifier;
    private String userId;
    private String creationTime;
    private String method;
    private String uri;
    private String protocol;
    private String status;
    private String size;
    private String referer;
    private String agent;
    private String userMetaInfo;
    
    public LogLine(String ipAddress, String identifier, String userId, String creationTime, String method, String uri,
            String protocol, String status, String size, String referer, String agent, String userMetaInfo) {
        this.ipAddress = ipAddress;
        this.identifier = identifier;
        this.userId = userId;
        this.creationTime = creationTime;
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.status = status;
        this.size = size;
        this.referer = referer;
        this.agent = agent;
        this.userMetaInfo = userMetaInfo;
    }
    
    @Override
    public String toString() {
        return Utilities.toString(this);
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getReferer() {
        return referer;
    }
    public void setReferer(String referer) {
        this.referer = referer;
    }
    public String getAgent() {
        return agent;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }
    public String getUserMetaInfo() {
        return userMetaInfo;
    }
    public void setUserMetaInfo(String userMetaInfo) {
        this.userMetaInfo = userMetaInfo;
    }

}
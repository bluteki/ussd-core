package com.bluteki.ussd.core.dto.response;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ussd")
public class TruRouteUssdResponse {

    private String msisdn;
    private String sessionId;
    private String type;
    private String msg;

    public TruRouteUssdResponse() {

    }

    public TruRouteUssdResponse(String msisdn, String sessionId, String type, String msg) {
        this.msisdn = msisdn;
        this.sessionId = sessionId;
        this.type = type;
        this.msg = msg;
    }

    @XmlElement(name = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @XmlElement(name = "sessionid")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "msg")
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
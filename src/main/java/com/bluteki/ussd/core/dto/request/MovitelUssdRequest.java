package com.bluteki.ussd.core.dto.request;

import java.io.StringReader;
import java.util.Objects;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovitelUssdRequest {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private Body body;

    // Add the fromXml method back
    public static MovitelUssdRequest fromXml(String xml) throws JAXBException {
        String normalizedXml = xml
                .replace("xmlns:xsd=\"http://viettel.com/xsd\"", "xmlns:urn=\"http://viettel.com/xsd\"")
                .replace(" xsi:type=\"xsd:string\"", "");

        JAXBContext jaxbContext = JAXBContext.newInstance(MovitelUssdRequest.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (MovitelUssdRequest) unmarshaller.unmarshal(new StringReader(normalizedXml));
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        MovitelUssdRequest that = (MovitelUssdRequest) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(body);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "start", namespace = "http://viettel.com/xsd")
        private Start start;

        public Start getStart() {
            return start;
        }

        public void setStart(Start start) {
            this.start = start;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            Body body = (Body) o;
            return Objects.equals(start, body.start);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(start);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Start {
        @XmlElement(name = "user")
        private String user;

        @XmlElement(name = "pass")
        private String password;

        @XmlElement(name = "msisdn")
        private String msisdn;

        @XmlElement(name = "msg")
        private String message;

        @XmlElement(name = "session_id")
        private String sessionId;

        @XmlElement(name = "transactionid")
        private String transactionId;

        @XmlElement(name = "requestType")
        private String requestType;

        @XmlElement(name = "ussdgw_id")
        private String ussdGwId;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            Start start = (Start) o;
            return Objects.equals(user, start.user) && Objects.equals(password, start.password)
                    && Objects.equals(msisdn, start.msisdn) && Objects.equals(message, start.message)
                    && Objects.equals(sessionId, start.sessionId) && Objects.equals(transactionId, start.transactionId)
                    && Objects.equals(requestType, start.requestType) && Objects.equals(ussdGwId, start.ussdGwId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, password, msisdn, message, sessionId, transactionId, requestType, ussdGwId);
        }
    }

    public String getUser() {
        return this.body != null && this.body.start != null ? this.body.start.user : null;
    }

    public void setUser(String user) {
        this.body.start.user = user;
    }

    public String getPassword() {
        return this.body != null && this.body.start != null ? this.body.start.password : null;
    }

    public void setPassword(String password) {
        this.body.start.password = password;
    }

    public String getMsisdn() {
        return this.body != null && this.body.start != null ? this.body.start.msisdn : null;
    }

    public void setMsisdn(String msisdn) {
        this.body.start.msisdn = msisdn;
    }

    public String getMessage() {
        return this.body != null && this.body.start != null ? this.body.start.message : null;
    }

    public void setMessage(String message) {
        this.body.start.message = message;
    }

    public String getSessionId() {
        return this.body != null && this.body.start != null ? this.body.start.sessionId : null;
    }

    public void setSessionId(String sessionId) {
        this.body.start.sessionId = sessionId;
    }

    public String getTransactionId() {
        return this.body != null && this.body.start != null ? this.body.start.transactionId : null;
    }

    public void setTransactionId(String transactionId) {
        this.body.start.transactionId = transactionId;
    }

    public String getRequestType() {
        return this.body != null && this.body.start != null ? this.body.start.requestType : null;
    }

    public void setRequestType(String requestType) {
        this.body.start.requestType = requestType;
    }

    public String getUssdGwId() {
        return this.body != null && this.body.start != null ? this.body.start.ussdGwId : null;
    }

    public void setUssdGwId(String ussdGwId) {
        this.body.start.ussdGwId = ussdGwId;
    }

}
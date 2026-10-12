package com.bluteki.ussd.core.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovitelUssdResponse {
    @XmlElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private Body body = new Body(); // Initialize here to prevent NPE

    // Constructor
    public MovitelUssdResponse() {
        this.body = new Body();
        this.body.response = new Response();
    }

    // Constructor with parameters
    public MovitelUssdResponse(String errorCode, String message) {
        this();
        setErrorCode(errorCode);
        setMessage(message);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "ussdresponse")
        private Response response = new Response();

        public Response getResponse() {
            return response;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Response {
        @XmlElement
        private String errorCode;
        @XmlElement
        private String message;

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }
    }
    
    public void setErrorCode(String errorCode) {
        this.body.response.errorCode = errorCode;
    }
    
    public void setMessage(String message) {
        this.body.response.message = message;
    }
    
    public String getErrorCode() {
        return this.body.response.errorCode;
    }

    public String getMessage() {
        return this.body.response.message;
    }
}
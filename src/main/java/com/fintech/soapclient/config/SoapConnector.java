package com.fintech.soapclient.config;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class SoapConnector extends WebServiceGatewaySupport {
    public Object callWebService(Object request) {
        return getWebServiceTemplate().marshalSendAndReceive(request);
    }
}

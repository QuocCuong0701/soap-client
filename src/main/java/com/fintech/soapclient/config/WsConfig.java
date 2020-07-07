package com.fintech.soapclient.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;

import java.io.IOException;
import java.util.List;

@Configuration
public class WsConfig extends WsConfigurerAdapter {
    private final String clientKsAlias = "mycert";
    private final String clientKsPwd = "123456";

    private final Resource clientKs = new ClassPathResource("keystore/server.keystore");
    private Resource requestSchema = new ClassPathResource("task.xsd");

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("com.fintech.soapclient.bindings");
        return jaxb2Marshaller;
    }

    @Bean
    PayloadLoggingInterceptor payloadLoggingInterceptor() {
        return new PayloadLoggingInterceptor();
    }

    @Bean
    PayloadValidatingInterceptor payloadValidatingInterceptor() {
        PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.setSchema(requestSchema);
        return validatingInterceptor;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        //interceptors.add(payloadLoggingInterceptor());
        interceptors.add(payloadValidatingInterceptor());
    }

    @Bean
    public SoapConnector soapConnector(Jaxb2Marshaller marshaller) throws Exception {
        SoapConnector connector = new SoapConnector();
        connector.setDefaultUri("http://localhost:1998/ws");
        connector.setMarshaller(marshaller);
        connector.setUnmarshaller(marshaller);
        //connector.setInterceptors(new ClientInterceptor[]{securityInterceptor()});
        connector.setInterceptors(new ClientInterceptor[]{wss4jSecurityInterceptor()});
        return connector;
    }

    /*@Autowired
    UserDetailsServiceImpl userDetailsService;
    @Bean
    SpringSecurityPasswordValidationCallbackHandler callbackHandler() {
        SpringSecurityPasswordValidationCallbackHandler callbackHandler = new SpringSecurityPasswordValidationCallbackHandler();
        callbackHandler.setUserDetailsService(userDetailsService);
        return callbackHandler;
    }*/
    @Bean
    public Wss4jSecurityInterceptor wss4jSecurityInterceptor() throws Exception {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();

        // set security actions: Timestamp Signature SAMLTokenSigned SAMLTokenUnsigned
        securityInterceptor.setSecurementActions("Timestamp Signature");

        // sign the request
        securityInterceptor.setSecurementUsername(clientKsAlias);
        securityInterceptor.setSecurementPassword(clientKsPwd);

        securityInterceptor.setSecurementSignatureCrypto(getCryptoFactoryBean().getObject());
        securityInterceptor.setSecurementSignatureParts(
                "{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;" +
                        "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body"
        );
        return securityInterceptor;
    }

    @Bean
    public CryptoFactoryBean getCryptoFactoryBean() throws IOException {
        CryptoFactoryBean cryptoFactoryBean = new CryptoFactoryBean();
        cryptoFactoryBean.setKeyStorePassword(clientKsPwd);
        cryptoFactoryBean.setKeyStoreLocation(clientKs);
        return cryptoFactoryBean;
    }

}

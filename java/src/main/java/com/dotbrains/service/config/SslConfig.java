package com.dotbrains.service.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SslConfig {

    private static final Logger logger = LoggerFactory.getLogger(SslConfig.class);

    @Value("${server.ssl.trust-store}")
    private Resource trustStoreResource;

    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${server.ssl.key-store}")
    private Resource keyStoreResource;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Bean
    public void setUpTrustStoreForApplication() throws IOException {
        String trustStoreFilePath = trustStoreResource.getFile().getAbsolutePath();

        logger.info("TrustStore location is {}", trustStoreFilePath);

        System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    @Bean
    public void setUpKeyStoreForApplication() throws IOException {
        String keyStoreFilePath = keyStoreResource.getFile().getAbsolutePath();

        logger.info("KeyStore location is {}", keyStoreFilePath);

		System.setProperty("javax.net.ssl.keyStore", keyStoreFilePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }
}

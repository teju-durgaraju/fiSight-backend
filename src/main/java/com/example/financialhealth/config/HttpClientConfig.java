package com.example.financialhealth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
// import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Example: Setting timeouts using HttpComponentsClientHttpRequestFactory
        // This requires Apache HttpClient library (org.apache.httpcomponents.client5:httpclient5)
        // If you want to enable this, ensure the dependency is in pom.xml.
        //
        // HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // requestFactory.setConnectTimeout(5000); // 5000 milliseconds = 5 seconds
        // requestFactory.setConnectionRequestTimeout(5000); // Timeout for requesting a connection from the connection manager
        // // requestFactory.setReadTimeout(5000); // Deprecated in newer versions, use response timeout on request config
        // // For more fine-grained control including read timeout with Apache HttpClient 5:
        // // org.apache.hc.client5.http.config.RequestConfig requestConfig = org.apache.hc.client5.http.config.RequestConfig.custom()
        // //         .setConnectionKeepAlive(org.apache.hc.core5.util.TimeValue.ofSeconds(10))
        // //         .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(5))
        // //         .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(5))
        // //         .build();
        // // org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.custom()
        // //         .setDefaultRequestConfig(requestConfig)
        // //         .build();
        // // requestFactory.setHttpClient(httpClient);
        //
        // restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }
}

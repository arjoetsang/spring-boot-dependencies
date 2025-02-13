package joe.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

@Configuration
@Slf4j
public class RestTemplateConfig {
    /**
     * Helper method to create a RestTemplate that trusts all SSL certificates.
     *
     * @return a RestTemplate instance configured to trust all SSL certificates.
     * @throws GeneralSecurityException if there is a problem with the SSL context configuration.
     */
    @Bean(name = "restTemplateWithTrustAllCerts")
    public RestTemplate createRestTemplateWithTrustAllCerts() throws GeneralSecurityException {
        // Create a trust strategy that accepts all certificates.
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] cert, String authType) -> true;

        // Build an SSL context using the trust strategy.
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        // Create an SSL connection socket factory with the SSL context and no hostname verifier.
        DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE);

        // Create a registry of connection socket factories for HTTP and HTTPS.
//        Registry<TlsSocketStrategy> tlsStrategyRegistry = RegistryBuilder.<TlsSocketStrategy>create()
//                .register("https", tlsStrategy)
//                .register("http", DefaultClientTlsStrategy.createDefault())
//                .build();

        // Create a connection manager using the socket factory registry.
//        BasicHttpClientConnectionManager connectionManager = BasicHttpClientConnectionManager.create(tlsStrategyRegistry);

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .build();

        // Create an HttpClient with the connection manager.
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .addRequestInterceptorFirst((request, entity, context) -> {
                    if (context.getAttribute("http.cookie-origin") != null) {
                        log.info("3rd Party API Request: {} {}", request.getMethod(), request.getPath());
                    }
                })
                .addResponseInterceptorLast((response, entity, context) -> {
                    if (response.getCode() != 301) {
                        log.info("3rd Party API Response: {} {}", response.getCode(), response.getReasonPhrase());
                    }
                })
                .build();

        // Create a request factory to use the HttpClient with the RestTemplate.
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Return a new RestTemplate instance using the request factory.
        return new RestTemplate(requestFactory);
    }
}


package joe.core;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class RestHelper {
    private final RestTemplate restTemplate;
    private final RestTemplate restTemplateWithTrustAllCerts;

    public RestHelper(RestTemplate restTemplate, @Qualifier("restTemplateWithTrustAllCerts") RestTemplate restTemplateWithTrustAllCerts) {
        this.restTemplate = restTemplate;
        this.restTemplateWithTrustAllCerts = restTemplateWithTrustAllCerts;
    }


    public <T> T doGet(String url, Class<T> responseClass, Map<String, String> headersMap) {
        return doGet(url, responseClass, headersMap, false);
    }


    public <T> T doGet(String url, Class<T> responseClass, Map<String, String> headersMap, boolean ignoreSSL) {
        ResponseEntity<T> responseEntity = doGetForEntity(url, responseClass, ignoreSSL, headersMap);
        return responseEntity != null ? responseEntity.getBody() : null;
    }


    public <T> T doGet(String url, TypeReference<T> typeRef, Map<String, String> headersMap, boolean ignoreSSL) throws Exception {
        ResponseEntity<String> responseEntity = doGetForEntity(url, String.class, ignoreSSL, headersMap);
        if (responseEntity != null && responseEntity.getBody() != null) {
            return Converter.jsonToObject(responseEntity.getBody(), typeRef);
        }
        return null;
    }


    public <T> ResponseEntity<T> doGetForEntity(String url, Class<T> responseClass, Map<String, String> headersMap) {
        return doGetForEntity(url, responseClass, false, headersMap);
    }


    public <T> ResponseEntity<T> doGetForEntity(String url, Class<T> responseClass, boolean ignoreSSL, Map<String, String> headersMap) {
        if (url == null || responseClass == null) {
            log.error("URL or ResponseClass provided is null");
            return null;
        }
        // Create and populate HttpHeaders
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headersMap != null) {
                headersMap.forEach(httpHeaders::set);
            }
            RestTemplate client = ignoreSSL ? restTemplateWithTrustAllCerts : restTemplate;
            return client.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseClass);
        } catch (Exception e) {
            logException(e);
        }
        return null;
    }


    public <T> ResponseEntity<T> doGetForEntity(String url, TypeReference<T> typeRef, boolean ignoreSSL, Map<String, String> headersMap) {
        if (url == null || typeRef == null) {
            log.error("URL or TypeReference provided is null");
            return null;
        }

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headersMap != null) {
                headersMap.forEach(httpHeaders::set);
            }
            RestTemplate client = ignoreSSL ? restTemplateWithTrustAllCerts : restTemplate;
            ResponseEntity<String> responseEntity = client.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

            if (responseEntity.getBody() != null) {
                T body = Converter.jsonToObject(responseEntity.getBody(), typeRef);
                return new ResponseEntity<>(body, responseEntity.getHeaders(), responseEntity.getStatusCode());
            }

        } catch (Exception e) {
            logException(e);
        }
        return null;
    }

    private void logException(Exception e) {
        log.error("Error during REST operation", e);
    }
}

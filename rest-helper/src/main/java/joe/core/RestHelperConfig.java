package joe.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestHelperConfig {

    @Bean
    public RestHelper restHelper() {
        return new RestHelper(new RestTemplate(), new RestTemplate());
    }
}

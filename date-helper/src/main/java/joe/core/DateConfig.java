package joe.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
@EnableConfigurationProperties(DateProperties.class)
@ConditionalOnProperty(name = "joe.date.format.enabled", havingValue = "true", matchIfMissing = true)
public class DateConfig {

    @Bean
    public DateTimeFormatter dateTimeFormatter(DateProperties dateProperties) {
        return DateTimeFormatter.ofPattern(dateProperties.getPattern());
    }

    @Bean
    public DateHelper dateHelper(DateTimeFormatter dateTimeFormatter){
        return new DateHelper(dateTimeFormatter);
    }
}
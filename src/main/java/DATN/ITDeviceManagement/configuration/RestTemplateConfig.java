package DATN.ITDeviceManagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        String bearerToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MDA3ODMiLCJ1c2VySWQiOiI0MDA3ODMiLCJ1c2VyRW1haWwiOiJIb2FuZy5WYW4uVGllbkB1c29sLXYuY29tLnZuIiwib2lkIjoiMzY2Y2I3M2YtZTFjMS00YmNjLTliOGYtODVkM2RkNmE2ZTVjIiwiZXhwIjoxNzM5MzI5MTk2fQ.B2c2rAP4etsPRp2qsQZRARp0hN3KKI7lgG3Gn4EX4QM"; // Thay thế bằng token thực tế

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + bearerToken);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
    }
}

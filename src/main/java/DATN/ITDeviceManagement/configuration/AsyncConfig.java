package DATN.ITDeviceManagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Số luồng cơ bản
        executor.setMaxPoolSize(20); // Số luồng tối đa
        executor.setQueueCapacity(100); // Hàng đợi chờ
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }
}
package DATN.ITDeviceManagement.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Slf4j
@Configuration
public class AppConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

   @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); // Giữ nếu cần test local
        config.addAllowedOrigin("http://10.128.58.11:5173");
        config.addAllowedOrigin("http://10.128.58.5:5173");
        config.addAllowedOrigin("http://10.128.58.6:5173");
        config.addAllowedOrigin("http://10.128.58.12:5173");
        config.addAllowedOrigin("https://qlvtui-production.up.railway.app"); // Sửa domain cho đúng
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
    @PostConstruct
    public void init() {
        try {
            if (!isProcedureExists("list_device")) {
                executeSqlFile("classpath:procedure.sql");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isProcedureExists(String procedureName) {
        String sql = "SELECT COUNT(*) FROM information_schema.ROUTINES WHERE ROUTINE_TYPE='PROCEDURE' AND ROUTINE_SCHEMA=DATABASE() AND ROUTINE_NAME=?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{procedureName}, Integer.class);
        return count != null && count > 0;
    }

    private void executeSqlFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource(filePath);
        System.out.println("Loading SQL file from: " + resource.getURL());
        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            jdbcTemplate.execute(sql.toString());
        }
    }

}

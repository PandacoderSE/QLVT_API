package DATN.ITDeviceManagement.configuration;

import DATN.ITDeviceManagement.entity.Role;
import DATN.ITDeviceManagement.entity.User;
import DATN.ITDeviceManagement.repository.RoleRepository;
import DATN.ITDeviceManagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private RoleRepository roleRepository ;

    // Kiểm tra tài khoản admin chưa tồn tại thì phải tạo 1 cái duy nhất
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if (userRepository.findByUsername("admin") == null) {
                Role adminRole = roleRepository.findByName("ADMIN");
                List<Role> roless = new ArrayList<>() ;
                roless.add(adminRole) ;
                User user = User.builder()
                        .id("1234567")
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .status(1)
                        .roles(roless)
                        .phone("0123456789")
                        .email("admin@gmail.com")
                        .firstname("admin")
                        .lastname("mr")
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password, admin please change it");
            }
        };
    }
}

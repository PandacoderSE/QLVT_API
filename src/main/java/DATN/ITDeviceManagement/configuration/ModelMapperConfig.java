package DATN.ITDeviceManagement.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper(); // Cấu hình Matching Strategy
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // Cấu hình để bỏ qua các thuộc tính null
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }
}

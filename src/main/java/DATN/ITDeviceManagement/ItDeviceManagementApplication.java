package DATN.ITDeviceManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ItDeviceManagementApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
		SpringApplication.run(ItDeviceManagementApplication.class, args);
	}

}

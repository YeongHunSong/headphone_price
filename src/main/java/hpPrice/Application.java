package hpPrice;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
//@MapperScan("hpPrice.repository.mybatis")
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
				.headless(false) // Clipboard 를 사용하려면 headless 모드를 해제해야 함.
				.run(args);

//		SpringApplication.run(Application.class, args);
	}
}

package ch.zumbo.jooqdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JooqDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JooqDemoApplication.class, args);
		System.out.println("Demo-Frontend erreichbar unter: http://localhost:8080/demo/execute");
		System.out.println("H2 Console erreichbar unter: http://localhost:8080/h2/");
	}

}

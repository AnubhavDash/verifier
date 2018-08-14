package ch.post.it.evoting.verifier.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan("ch.post.it.evoting.verifier")
@PropertySource("classpath:verifier.properties")
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}

package ch.post.it.evoting.verifier.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ch.post.it.evoting.verifier")
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}

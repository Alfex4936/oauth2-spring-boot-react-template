package csw.lms.namsan.nodes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableMongoAuditing
@SpringBootApplication
public class NamsanNodesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NamsanNodesApplication.class, args);
    }

}

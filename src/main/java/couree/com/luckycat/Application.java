package couree.com.luckycat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = {
        // By excluding this class, SpringBoot will not automatically load data source. Instead,
        // developers can load the data source on their own. In this project, we use Hibernate to do the job
        DataSourceAutoConfiguration.class,

        // Excluding this class to enable "com.couree.backend.exception.md.resolver.HandlerExceptionResolver",
        // which is a custom exception.md resolver
        WebMvcAutoConfiguration.class,
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

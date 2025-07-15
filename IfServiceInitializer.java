import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EntityScan(basePackages = {"com.tag.biometric.*"})
@ComponentScan(basePackages = {"com.tag.biometric.*"})
@EnableR2dbcRepositories
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
public class IfServiceInitializer {

    public static void main(String[] args) {
        SpringApplication.run(IfServiceInitializer.class, args);
    }

}

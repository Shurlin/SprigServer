package xyz.shurlin.sprigserver;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("xyz.shurlin.sprigserver.mapper")
public class SprigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprigServerApplication.class, args);
    }

}

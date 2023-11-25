package kopo.poly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스프링 스케줄러 활용을 위한 어노테이션 추가
@SpringBootApplication
public class AbsSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbsSchedulerApplication.class, args);
    }

}

package ua.project.FinalProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ua.project.FinalProject.controller.RegController;
import ua.project.FinalProject.scheduler.SubscriptionScheduler;

@SpringBootApplication
@EnableScheduling
public class FinalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectApplication.class, args);
    }

}

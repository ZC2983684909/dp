package com.wxmblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@ServletComponentScan
@EnableRetry
@EnableAsync
public class MsfastYanJianApplication {

    public static void main(String[] args) {
        configureSentinelLogDirectory();
        SpringApplication.run(MsfastYanJianApplication.class, args);
    }

    /**
     * Sentinel uses a JVM system property for its file logger. In the local
     * Windows environment user.home can resolve to a non-writable root, so
     * default it to a project-local directory unless the launcher provided a
     * custom location.
     */
    private static void configureSentinelLogDirectory() {
        String configured = System.getProperty("csp.sentinel.log.dir");
        if (configured != null && !configured.trim().isEmpty()) {
            return;
        }
        Path logDirectory = Paths.get(
                System.getProperty("user.dir", "."),
                "logs",
                "csp"
        ).toAbsolutePath().normalize();
        try {
            Files.createDirectories(logDirectory);
        } catch (Exception e) {
            System.err.println("Unable to create Sentinel log directory: " + logDirectory);
        }
        System.setProperty("csp.sentinel.log.dir", logDirectory.toString());
    }

}

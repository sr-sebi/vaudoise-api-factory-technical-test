package com.vaudoise.vaudoiseback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.net.InetAddress;
import java.util.Optional;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
@EnableMethodSecurity
@Slf4j
public class VaudoiseApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication app = new SpringApplication(VaudoiseApplication.class);

        Environment env = app.run(args).getEnvironment();

        try {
            String appName = Optional.ofNullable(env.getProperty("spring.application.name"))
                    .orElse("Vaudoise API");
            String serverPort = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "/");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            log.info("------------------------------------------------------------");
            log.info("🟢 {} started successfully!", appName);
            log.info("🌍 Access URLs:");
            log.info("   ➤ Local:      http://localhost:{}{}", serverPort, contextPath);
            log.info("   ➤ External:   http://{}:{}{}", hostAddress, serverPort, contextPath);
            log.info("------------------------------------------------------------");
            log.info("📖 Swagger / OpenAPI URLs:");
            log.info("   ➤ OpenAPI JSON:      http://localhost:{}{}/v3/api-docs", serverPort, contextPath);
            log.info("   ➤ Swagger UI:        http://localhost:{}{}/swagger-ui/index.html", serverPort, contextPath);
            log.info("------------------------------------------------------------");
            log.info("🕒 Active profiles: {}", (Object) env.getActiveProfiles());
            log.info("------------------------------------------------------------");

        } catch (Exception e) {
            log.error("❌ Error while starting VaudoiseApplication", e);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("✅ Startup completed in {} ms", elapsed);
    }
}

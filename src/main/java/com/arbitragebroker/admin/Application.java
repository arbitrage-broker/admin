package com.arbitragebroker.admin;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Slf4j
@AllArgsConstructor
@EnableScheduling
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class Application {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Profile("dev")
    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    private void postConstruct() {
        String protocol = "https";
        if (!environment.containsProperty("server.ssl.key-store")) {
            protocol = "http";
        }
        String port = environment.getProperty("server.port","2024");
        String appName = environment.getProperty("spring.application.name","Admin");
        String localUrl = "%s://localhost:%s/swagger-ui.html".formatted(protocol, port);
        String externalUrl = "%s://%s:%s/swagger-ui.html".formatted(protocol,InetAddress.getLocalHost().getHostAddress(), port);

        log.info("""
                ----------------------------------------------------------
                \tApplication '{}' is running! Access URLs:
                \tLocal: \t\t{}
                \tExternal: \t{}
                \tProfile(s): {}
                ----------------------------------------------------------
                """,  appName, localUrl, externalUrl, environment.getActiveProfiles());


        /*if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(new String[]{"bash", "-c", "google-chrome ".concat(url)});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}

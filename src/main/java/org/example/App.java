package org.example;

import org.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        try{
            AppConfig config = new AppConfig();
            logger.info("Configurations loaded: port={}, message={}", config.getServer().port(), config.getServer().message());
            logger.info(new App().getGreeting());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}

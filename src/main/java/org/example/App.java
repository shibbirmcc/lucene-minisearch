package org.example;

import org.example.config.AppConfig;
import org.example.config.Lucene;
import org.example.config.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        try{
            logger.info(new App().getGreeting());
            AppConfig config = new AppConfig();
            Server server = config.getServer();
            logger.info("Configurations loaded: port={}, message={}", server.getPort(), server.getMessage());
            Lucene lucene = config.getLucene();
            logger.info("Configurations loaded: lucene-data-store={}", lucene.getDataStore());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}

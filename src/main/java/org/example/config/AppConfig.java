package org.example.config;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class AppConfig {

    private final Root root;

    public AppConfig() {
        this("application.yaml");
    }

    public AppConfig(String resourceName) {
        Yaml yaml = new Yaml(new Constructor(Root.class, new LoaderOptions()));
        try (var in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException(resourceName + " not found in classpath");
            }
            this.root = yaml.load(in);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load " + resourceName, e);
        }
    }


    public Server getServer() {
        return root.server();
    }
}

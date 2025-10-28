package org.example.config;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;

public class AppConfig {

    private final Root root;

    public AppConfig() throws IOException, YAMLException {
        this("application.yaml");
    }

    public AppConfig(String resourceName) throws IOException, YAMLException {
        Yaml yaml = new Yaml(new Constructor(Root.class, new LoaderOptions()));
        try (var in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException(resourceName + " not found in classpath");
            }
            this.root = yaml.load(in);
        }
    }


    public Server getServer() {
        return root == null ? null : root.getServer();
    }
}

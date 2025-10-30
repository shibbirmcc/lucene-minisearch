package org.example.config;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;

/**
 * Configuration loader for application settings from YAML files.
 */
public class AppConfig {

    public static final String DEFAULT_RESOURCE = "application.yaml";

    private final Root root;

    /**
     * Constructs AppConfig using the default resource.
     *
     * @throws IOException if the resource cannot be read
     * @throws YAMLException if the YAML content is invalid
     */
    public AppConfig() throws IOException, YAMLException {
        this(DEFAULT_RESOURCE);
    }

    /**
     * Constructs AppConfig from a specified YAML resource file.
     *
     * @param resourceName the name of the YAML resource file
     * @throws IOException if the resource cannot be read
     * @throws YAMLException if the YAML content is invalid
     */
    public AppConfig(String resourceName) throws IOException, YAMLException {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(Root.class, loaderOptions);
        constructor.setPropertyUtils(new CaseInsensitivePropertyUtils());

        Yaml yaml = new Yaml(constructor);

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

    public Lucene getLucene() {
        return root == null ? null : root.getLucene();
    }
}

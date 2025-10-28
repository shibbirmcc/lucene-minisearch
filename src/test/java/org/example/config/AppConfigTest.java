package org.example.config;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class AppConfigTest {

    @Test
    void loadsYamlIntoPojo() throws IOException, YAMLException {
        AppConfig cfg = new AppConfig();
        assertThat(cfg.getServer()).isNotNull();
        assertThat(cfg.getServer().getPort()).isEqualTo(9090);
        assertThat(cfg.getServer().getMessage()).isEqualTo("Hello from tests");
    }

    @Test
    void throwsIfResourceMissing() {
        assertThatThrownBy(() -> new AppConfig("does-not-exist.yaml"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does-not-exist.yaml");
    }

    @Test
    void malformedYamlSurfacesParseError() {
        assertThatThrownBy(() -> new AppConfig("bad-application.yaml"))
                .isInstanceOf(YAMLException.class)
                .hasRootCauseInstanceOf(NumberFormatException.class);
    }
}

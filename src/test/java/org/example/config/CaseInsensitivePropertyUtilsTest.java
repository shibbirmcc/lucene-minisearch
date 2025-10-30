package org.example.config;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.introspector.Property;

import static org.assertj.core.api.Assertions.*;

class CaseInsensitivePropertyUtilsTest {

    private final CaseInsensitivePropertyUtils propertyUtils = new CaseInsensitivePropertyUtils();

    @Test
    void shouldReturnProperty_whenExactNameMatches() {
        Property property = propertyUtils.getProperty(Server.class, "appPort");
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("appPort");
    }

    @Test
    void shouldConvertKebabCaseToCamelCase_whenGettingProperty() {
        Property property = propertyUtils.getProperty(Server.class, "app-port");
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("appPort");
    }

    @Test
    void shouldConvertSnakeCaseToCamelCase_whenGettingProperty() {
        Property property = propertyUtils.getProperty(Server.class, "app_port");
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("appPort");
    }

    @Test
    void shouldConvertMultipleKebabSegments_whenGettingProperty() {
        Property property = propertyUtils.getProperty(Server.class, "metric-port");
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("metricPort");
    }

    @Test
    void shouldThrowException_whenPropertyDoesNotExist() {
        assertThatThrownBy(() -> propertyUtils.getProperty(Server.class, "nonExistentProperty"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldThrowException_whenKebabCasePropertyDoesNotExist() {
        assertThatThrownBy(() -> propertyUtils.getProperty(Server.class, "non-existent-property"))
                .isInstanceOf(RuntimeException.class);
    }
}

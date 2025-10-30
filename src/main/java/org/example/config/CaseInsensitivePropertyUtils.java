package org.example.config;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * Custom PropertyUtils that enables case-insensitive and kebab-case to camelCase property mapping for YAML parsing.
 */
public class CaseInsensitivePropertyUtils extends PropertyUtils {
    @Override
    public Property getProperty(Class<?> type, String name) {
        try {
            return super.getProperty(type, name);
        } catch (Exception e) {
            String camelCase = toCamelCase(name);
            try {
                return super.getProperty(type, camelCase);
            } catch (Exception ex) {
                throw e;
            }
        }
    }

    private String toCamelCase(String kebabCase) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : kebabCase.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }
}

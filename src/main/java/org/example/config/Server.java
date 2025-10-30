package org.example.config;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Configuration properties for server settings.
 */
@NoArgsConstructor
@Getter
@Setter
public class Server {
    @NonNull
    private Integer appPort;
    @NonNull
    private Integer metricPort;
}

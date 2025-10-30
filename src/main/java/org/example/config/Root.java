package org.example.config;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Root configuration container that holds multiple settings.
 */
@NoArgsConstructor
@Getter
@Setter
public class Root {
    @NonNull
    private Server server;
    @NonNull
    private Lucene lucene;
}

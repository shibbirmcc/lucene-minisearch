package org.example.config;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Configuration properties for Lucene indexing and searching.
 */
@NoArgsConstructor
@Getter
@Setter
public class Lucene {
    @NonNull
    private String dataStore;
}

package org.example.config;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Server {
    @NonNull
    private Integer port;
    @NonNull
    private String message;
}

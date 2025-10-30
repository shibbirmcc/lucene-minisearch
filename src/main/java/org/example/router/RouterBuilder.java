package org.example.router;

import io.netty.handler.codec.http.HttpMethod;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fluent builder for constructing {@link Router} instances.
 *
 * <p>Example:
 * <pre>{@code
 * Router router = new RouterBuilder()
 *     .get("/health", ctx -> ctx.ok())
 *     .post("/echo", ctx -> ctx.text(OK, "Echo"))
 *     .build();
 * }</pre>
 */
public final class RouterBuilder {
    private final Map<String, RouteHandler> routes = new ConcurrentHashMap<>();

    /** Adds a GET route to the router. */
    public RouterBuilder get(String path, RouteHandler handler) {
        routes.put(key(HttpMethod.GET, path), handler);
        return this;
    }

    /** Adds a POST route to the router. */
    public RouterBuilder post(String path, RouteHandler handler) {
        routes.put(key(HttpMethod.POST, path), handler);
        return this;
    }

    /** Adds a PUT route to the router. */
    public RouterBuilder put(String path, RouteHandler handler) {
        routes.put(key(HttpMethod.PUT, path), handler);
        return this;
    }

    /** Adds a DELETE route to the router. */
    public RouterBuilder delete(String path, RouteHandler handler) {
        routes.put(key(HttpMethod.DELETE, path), handler);
        return this;
    }

    /**
     * Builds and returns an immutable {@link Router}.
     *
     * @return a Router ready to attach to a Netty pipeline
     */
    public Router build() {
        var table = Map.copyOf(routes);
        return () -> new SimpleRouteHandler(table);
    }

    private static String key(HttpMethod method, String path) {
        return method.name() + " " + path;
    }
}

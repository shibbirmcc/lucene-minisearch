package org.example.router;

import org.example.http.RequestContext;

/**
 * Functional interface representing a single HTTP route handler.
 *
 * <p>Each handler receives a {@link RequestContext} object that wraps
 * both the Netty request and response context.
 */
@FunctionalInterface
public interface RouteHandler {
    /**
     * Handles an HTTP request asynchronously.
     *
     * @param context the request context
     * @throws Exception if any exception occurs during handling
     */
    void handle(RequestContext context) throws Exception;
}



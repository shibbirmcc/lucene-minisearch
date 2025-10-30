package org.example.router;

import io.netty.channel.ChannelHandler;

/**
 * Represents a lightweight routing abstraction for Netty HTTP requests.
 *
 * <p>Routers map HTTP method + path combinations to {@link RouteHandler} implementations.
 * They are immutable and thread-safe.
 */

public interface Router {
    /**
     * Returns the Netty {@link ChannelHandler} that performs route dispatch.
     *
     * @return the route handler to attach to a Netty pipeline
     */
    ChannelHandler handler();
}

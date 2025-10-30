package org.example.routes;

import io.netty.channel.ChannelHandler;
import org.example.router.Router;
import org.example.router.RouterBuilder;

/**
 * Defines the primary application routes.
 *
 * <p>This router can be extended to expose REST endpoints for
 * the business logic of the service.
 */
public class AppRouter implements Router {
    private final Router delegate;

    public AppRouter() {
        delegate = new RouterBuilder()
                .get("/health", ctx -> ctx.ok())
                .build();
    }

    @Override
    public ChannelHandler handler() {
        return delegate.handler();
    }
}

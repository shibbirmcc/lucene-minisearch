package org.example.routes;

import io.netty.channel.ChannelHandler;
import org.example.router.Router;
import org.example.router.RouterBuilder;

public class MetricsRouter implements Router {
    private final Router delegate;

    public MetricsRouter() {
        delegate = new RouterBuilder()
                .get("/health", ctx -> ctx.ok())
                .build();
    }

    @Override
    public ChannelHandler handler() {
        return delegate.handler();
    }
}

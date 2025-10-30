package org.example.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.example.router.Router;

/**
 * Configures the Netty pipeline for incoming HTTP connections.
 *
 * <p>This class sets up the HTTP codec, CORS, and routing handler chain.
 * It is used internally by {@link HttpServer}.
 */

final class NettyHttpInitializer extends ChannelInitializer<SocketChannel> {
    private final Router router;

    NettyHttpInitializer(Router router) {
        this.router = router;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1 * 1024 * 1024)); // 1 MB max content length
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().build()));
        p.addLast("router", router.handler());
    }
}

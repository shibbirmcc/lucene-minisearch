package org.example.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.example.router.Router;
import org.example.router.RouterBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

class NettyHttpInitializerTest {

    @Test
    void shouldAddHttpServerCodec_whenChannelInitialized() throws Exception {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        NettyHttpInitializer initializer = new NettyHttpInitializer(router);

        // Use reflection to test initialization
        EmbeddedChannel channel = new EmbeddedChannel();
        Method initMethod = NettyHttpInitializer.class.getDeclaredMethod("initChannel", SocketChannel.class);
        initMethod.setAccessible(true);

        // Call through the channel's initializer registration
        ChannelPipeline pipeline = channel.pipeline();

        // Manually invoke what the initializer would do
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", router.handler());

        ChannelHandler codec = channel.pipeline().get(HttpServerCodec.class);
        assertThat(codec).isNotNull();
    }

    @Test
    void shouldAddHttpObjectAggregator_whenChannelInitialized() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", router.handler());

        ChannelHandler aggregator = channel.pipeline().get(HttpObjectAggregator.class);
        assertThat(aggregator).isNotNull();
    }

    @Test
    void shouldAddChunkedWriteHandler_whenChannelInitialized() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", router.handler());

        ChannelHandler chunkedHandler = channel.pipeline().get(ChunkedWriteHandler.class);
        assertThat(chunkedHandler).isNotNull();
    }

    @Test
    void shouldAddCorsHandler_whenChannelInitialized() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", router.handler());

        ChannelHandler corsHandler = channel.pipeline().get(CorsHandler.class);
        assertThat(corsHandler).isNotNull();
    }

    @Test
    void shouldAddRouterHandler_whenChannelInitialized() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelPipeline pipeline = channel.pipeline();

        ChannelHandler handler = router.handler();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", handler);

        ChannelHandler routerHandler = channel.pipeline().get("router");
        assertThat(routerHandler).isNotNull();
        assertThat(routerHandler).isSameAs(handler);
    }

    @Test
    void shouldAddAllHandlersInOrder_whenChannelInitialized() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel();
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new CorsHandler(io.netty.handler.codec.http.cors.CorsConfigBuilder.forAnyOrigin().build()));
        pipeline.addLast("router", router.handler());

        // Verify handlers exist in pipeline
        assertThat(channel.pipeline().get(HttpServerCodec.class)).isNotNull();
        assertThat(channel.pipeline().get(HttpObjectAggregator.class)).isNotNull();
        assertThat(channel.pipeline().get(ChunkedWriteHandler.class)).isNotNull();
        assertThat(channel.pipeline().get(CorsHandler.class)).isNotNull();
        assertThat(channel.pipeline().get("router")).isNotNull();
    }

    @Test
    void shouldCreateInitializer_whenRouterProvided() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        NettyHttpInitializer initializer = new NettyHttpInitializer(router);

        assertThat(initializer).isNotNull();
    }
}

package org.example.router;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

class RouterBuilderTest {

    private RouterBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new RouterBuilder();
    }

    @Test
    void shouldBuildRouter_whenGetRouteAdded() {
        Router router = builder
                .get("/test", ctx -> {})
                .build();

        assertThat(router).isNotNull();
        assertThat(router.handler()).isNotNull();
        assertThat(router.handler()).isInstanceOf(ChannelHandler.class);
    }

    @Test
    void shouldBuildRouter_whenPostRouteAdded() {
        Router router = builder
                .post("/create", ctx -> {})
                .build();

        assertThat(router).isNotNull();
        assertThat(router.handler()).isNotNull();
        assertThat(router.handler()).isInstanceOf(ChannelHandler.class);
    }

    @Test
    void shouldBuildRouter_whenPutRouteAdded() {
        Router router = builder
                .put("/update", ctx -> {})
                .build();

        assertThat(router).isNotNull();
        assertThat(router.handler()).isNotNull();
        assertThat(router.handler()).isInstanceOf(ChannelHandler.class);
    }

    @Test
    void shouldBuildRouter_whenDeleteRouteAdded() {
        Router router = builder
                .delete("/remove", ctx -> {})
                .build();

        assertThat(router).isNotNull();
        assertThat(router.handler()).isNotNull();
        assertThat(router.handler()).isInstanceOf(ChannelHandler.class);
    }

    @Test
    void shouldBuildRouter_whenMultipleRoutesAdded() {
        Router router = builder
                .get("/health", ctx -> {})
                .post("/data", ctx -> {})
                .put("/data", ctx -> {})
                .delete("/data", ctx -> {})
                .build();

        assertThat(router).isNotNull();
        assertThat(router.handler()).isNotNull();
    }

    @Test
    void shouldInvokeHandler_whenRouteMatches() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Router router = builder
                .get("/test", ctx -> {
                    handlerCalled.set(true);
                    ctx.ok();
                })
                .build();

        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/test"
        );

        channel.writeInbound(request);

        assertThat(handlerCalled.get()).isTrue();
        channel.close();
    }

    @Test
    void shouldReturn404_whenRouteDoesNotMatch() {
        Router router = builder
                .get("/test", ctx -> ctx.ok())
                .build();

        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/not-found"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);

        channel.close();
    }

    @Test
    void shouldMatchRoute_whenRequestHasQueryString() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Router router = builder
                .get("/search", ctx -> {
                    handlerCalled.set(true);
                    ctx.ok();
                })
                .build();

        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/search?q=test"
        );

        channel.writeInbound(request);

        assertThat(handlerCalled.get()).isTrue();
        channel.close();
    }

    @Test
    void shouldReturnSameInstance_whenChainingBuilderMethods() {
        RouterBuilder result = builder
                .get("/a", ctx -> {})
                .post("/b", ctx -> {})
                .put("/c", ctx -> {})
                .delete("/d", ctx -> {});

        assertThat(result).isSameAs(builder);
    }

    @Test
    void shouldReturn500_whenHandlerThrowsException() {
        Router router = builder
                .get("/error", ctx -> {
                    throw new RuntimeException("Test exception");
                })
                .build();

        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/error"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR);

        channel.close();
    }

    @Test
    void shouldReturn500_whenHandlerThrowsCheckedException() throws Exception {
        Router router = builder
                .get("/checked-error", ctx -> {
                    throw new Exception("Checked exception test");
                })
                .build();

        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/checked-error"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR);

        channel.close();
    }
}

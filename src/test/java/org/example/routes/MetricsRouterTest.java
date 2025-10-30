package org.example.routes;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MetricsRouterTest {

    private MetricsRouter router;

    @BeforeEach
    void setUp() {
        router = new MetricsRouter();
    }

    @Test
    void shouldReturnHandler_whenHandlerCalled() {
        assertThat(router.handler()).isNotNull();
    }

    @Test
    void shouldReturn200_whenGetHealthEndpointRequested() {
        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/health"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.OK);

        channel.close();
    }

    @Test
    void shouldReturn404_whenHealthRequestedWithoutLeadingSlash() {
        // Invalid URI path without leading slash should not match
        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "health"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);

        channel.close();
    }

    @Test
    void shouldReturn404_whenUnknownEndpointRequested() {
        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/unknown"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);

        channel.close();
    }

    @Test
    void shouldReturn404_whenPostToHealthEndpoint() {
        EmbeddedChannel channel = new EmbeddedChannel(router.handler());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                "health"
        );

        channel.writeInbound(request);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);

        channel.close();
    }
}

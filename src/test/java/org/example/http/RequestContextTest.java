package org.example.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.example.http.RequestContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

class RequestContextTest {

    @Test
    void shouldSend200Response_whenOkCalled() {
        AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<>();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctxRef.set(ctx);
            }
        });

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/"
        );
        RequestContext context = new RequestContext(ctxRef.get(), request);

        context.ok();

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE))
                .isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    void shouldSendCustomStatusAndMessage_whenTextCalled() {
        AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<>();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctxRef.set(ctx);
            }
        });

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/"
        );
        RequestContext context = new RequestContext(ctxRef.get(), request);

        context.text(HttpResponseStatus.CREATED, "Resource created");

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.status()).isEqualTo(HttpResponseStatus.CREATED);
        assertThat(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE))
                .isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    void shouldSetContentLength_whenTextCalled() {
        AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<>();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctxRef.set(ctx);
            }
        });

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/"
        );
        RequestContext context = new RequestContext(ctxRef.get(), request);

        String body = "Test message";
        context.text(HttpResponseStatus.OK, body);

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.headers().getInt(HttpHeaderNames.CONTENT_LENGTH))
                .isEqualTo(body.getBytes().length);
    }

    @Test
    void shouldSetKeepAliveHeader_whenRequestHasKeepAlive() {
        AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<>();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctxRef.set(ctx);
            }
        });

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/"
        );
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        RequestContext context = new RequestContext(ctxRef.get(), request);

        context.text(HttpResponseStatus.OK, "OK");

        Object response = channel.readOutbound();
        assertThat(response).isInstanceOf(HttpResponse.class);
        HttpResponse httpResponse = (HttpResponse) response;
        assertThat(httpResponse.headers().get(HttpHeaderNames.CONNECTION))
                .isEqualTo(HttpHeaderValues.KEEP_ALIVE.toString());
    }

    @Test
    void shouldReturnHttpRequest_whenGetRequestCalled() {
        AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<>();
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                "/test"
        );
        RequestContext context = new RequestContext(ctxRef.get(), request);

        assertThat(context.getRequest()).isSameAs(request);
        assertThat(context.getRequest().method()).isEqualTo(HttpMethod.POST);
        assertThat(context.getRequest().uri()).isEqualTo("/test");
    }
}

package org.example.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.AccessLevel;
import lombok.Getter;
import java.nio.charset.StandardCharsets;

/**
 * Encapsulates the request and response context for a single HTTP call.
 *
 * <p>This utility class provides convenient methods for writing responses
 * without directly manipulating Netty's low-level APIs.
 */

@Getter
public class RequestContext {
    @Getter(AccessLevel.NONE)
    private final ChannelHandlerContext context;
    private final FullHttpRequest request;

    public RequestContext(ChannelHandlerContext context, FullHttpRequest request) {
        this.context = context;
        this.request = request;
    }

    /**
     * Writes a plain-text response with the specified status and body.
     *
     * @param status HTTP status code
     * @param body   response content
     */
    public void text(HttpResponseStatus status, String body) {
        var content  = Unpooled.copiedBuffer(body, StandardCharsets.UTF_8);
        var response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if(keepAlive){
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        var f = context.writeAndFlush(response);
        if(!keepAlive){
            f.addListener(ch -> context.close());
        }
    }

    /**
     * Sends a simple "200 OK" response with body "OK".
     * */
    public void ok(){
        text(HttpResponseStatus.OK, "OK");
    }
}

package org.example.router;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.example.http.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A simple Netty handler that dispatches HTTP requests
 * to registered {@link RouteHandler} instances based on path and method.
 */

final class SimpleRouteHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRouteHandler.class);

    private final Map<String, RouteHandler> routes;

    SimpleRouteHandler(Map<String, RouteHandler> routes) {
        this.routes = routes;
    }

    /**
     * Handles an incoming {@link FullHttpRequest} message.
     *
     * <p>This method dispatches the request to a matching {@link RouteHandler}
     * based on the HTTP method and request path. If no route is found, a
     * {@code 404 Not Found} response is returned. Any unexpected error during
     * handling results in a {@code 500 Internal Server Error} response.
     *
     * @param channelHandlerContext the Netty {@link ChannelHandlerContext} for this channel
     * @param fullHttpRequest the full HTTP request received from the client
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // TODO: Implement route lookup and dispatching logic seperately with proper error handling
        var key = fullHttpRequest.method().name() + " " + fullHttpRequest.uri().split("\\?")[0];
        var handler = routes.get(key);
        var requestContext = new RequestContext(channelHandlerContext, fullHttpRequest);
        if (handler == null){
            requestContext.text(HttpResponseStatus.NOT_FOUND, "Not Found");
            return;
        }
        try{
            handler.handle(requestContext);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            requestContext.text(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}

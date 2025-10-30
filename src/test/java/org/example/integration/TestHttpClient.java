package org.example.integration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * HTTP client for integration testing.
 *
 * <p>This client provides a simple interface for making HTTP requests
 * to the test servers. It handles connection management and response
 * processing asynchronously.
 *
 * <p>Usage:
 * <pre>{@code
 * TestHttpClient client = new TestHttpClient();
 * HttpResponse response = client.get("http://localhost:8080/health");
 * assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
 * client.close();
 * }</pre>
 */
public class TestHttpClient {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    /**
     * Creates a new test HTTP client.
     */
    public TestHttpClient() {
        this.eventLoopGroup = new NioEventLoopGroup(1);
        this.bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class);
    }

    /**
     * Performs a GET request to the specified URL.
     *
     * @param url the full URL to request (e.g., "http://localhost:8080/health")
     * @return the HTTP response
     * @throws Exception if the request fails
     */
    public HttpResponse get(String url) throws Exception {
        return request(HttpMethod.GET, url);
    }

    /**
     * Performs a POST request to the specified URL.
     *
     * @param url the full URL to request
     * @return the HTTP response
     * @throws Exception if the request fails
     */
    public HttpResponse post(String url) throws Exception {
        return request(HttpMethod.POST, url);
    }

    /**
     * Performs a PUT request to the specified URL.
     *
     * @param url the full URL to request
     * @return the HTTP response
     * @throws Exception if the request fails
     */
    public HttpResponse put(String url) throws Exception {
        return request(HttpMethod.PUT, url);
    }

    /**
     * Performs a DELETE request to the specified URL.
     *
     * @param url the full URL to request
     * @return the HTTP response
     * @throws Exception if the request fails
     */
    public HttpResponse delete(String url) throws Exception {
        return request(HttpMethod.DELETE, url);
    }

    /**
     * Performs an HTTP request with the specified method and URL.
     *
     * @param method the HTTP method
     * @param url    the full URL to request
     * @return the HTTP response
     * @throws Exception if the request fails
     */
    private HttpResponse request(HttpMethod method, String url) throws Exception {
        // Parse URL
        String[] parts = parseUrl(url);
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        String path = parts[2];

        CompletableFuture<HttpResponse> responseFuture = new CompletableFuture<>();

        Bootstrap clientBootstrap = bootstrap.clone()
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx, FullHttpResponse msg) {
                                // Create a copy of the response before the channel closes
                                DefaultHttpResponse response = new DefaultHttpResponse(
                                        msg.protocolVersion(),
                                        msg.status(),
                                        msg.headers().copy()
                                );
                                responseFuture.complete(response);
                            }

                            @Override
                            public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
                                responseFuture.completeExceptionally(cause);
                                ctx.close();
                            }
                        });
                    }
                });

        Channel channel = clientBootstrap.connect(host, port).sync().channel();

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                method,
                path
        );
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        channel.writeAndFlush(request);

        // Wait for response with timeout
        HttpResponse response = responseFuture.get(5, TimeUnit.SECONDS);
        channel.close().sync();

        return response;
    }

    /**
     * Parses a URL into host, port, and path components.
     *
     * @param url the URL to parse
     * @return array containing [host, port, path]
     */
    private String[] parseUrl(String url) {
        // Remove protocol
        String withoutProtocol = url.replaceFirst("^https?://", "");

        // Split host:port from path
        String[] hostAndPath = withoutProtocol.split("/", 2);
        String hostPort = hostAndPath[0];
        String path = hostAndPath.length > 1 ? "/" + hostAndPath[1] : "/";

        // Split host and port
        String[] hostPortParts = hostPort.split(":");
        String host = hostPortParts[0];
        String port = hostPortParts.length > 1 ? hostPortParts[1] : "80";

        return new String[]{host, port, path};
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}

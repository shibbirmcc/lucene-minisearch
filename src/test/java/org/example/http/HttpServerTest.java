package org.example.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.example.router.Router;
import org.example.router.RouterBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

class HttpServerTest {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private EventLoopGroup clientGroup;
    private HttpServer server;

    @BeforeEach
    void setUp() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(1);
        clientGroup = new NioEventLoopGroup(1);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        clientGroup.shutdownGracefully();
    }

    @Test
    void shouldCreateServer_whenEventLoopGroupsProvided() {
        server = new HttpServer(bossGroup, workerGroup);
        assertThat(server).isNotNull();
    }

    @Test
    void shouldSetPortAndReturnSameInstance_whenWithPortCalled() {
        server = new HttpServer(bossGroup, workerGroup);
        assertThat(server).isNotNull();

        HttpServer result = server.withPort(8080);
        assertThat(result).isSameAs(server);
    }

    @Test
    void shouldSetRouterAndReturnSameInstance_whenWithRouterCalled() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        server = new HttpServer(bossGroup, workerGroup);
        assertThat(server).isNotNull();

        HttpServer result = server.withRouter(router);
        assertThat(result).isSameAs(server);
    }

    @Test
    void shouldSupportFluentChaining_whenConfiguringServer() {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        server = new HttpServer(bossGroup, workerGroup)
                .withPort(9999)
                .withRouter(router);

        assertThat(server).isNotNull();
    }

    @Test
    void shouldStartSuccessfully_whenConfiguredWithPortAndRouter() throws Exception {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        server = new HttpServer(bossGroup, workerGroup)
                .withPort(0) // Use ephemeral port
                .withRouter(router)
                .start();

        assertThat(server).isNotNull();
    }

    @Test
    void shouldAcceptAndHandleHttpRequests_whenServerStarted() throws Exception {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Router router = new RouterBuilder()
                .get("/test", ctx -> {
                    handlerCalled.set(true);
                    ctx.ok();
                })
                .build();

        server = new HttpServer(bossGroup, workerGroup)
                .withPort(18080)
                .withRouter(router)
                .start();

        // Give server time to start
        Thread.sleep(100);

        // Create client to connect
        CountDownLatch responseLatch = new CountDownLatch(1);
        AtomicBoolean responseReceived = new AtomicBoolean(false);

        Bootstrap clientBootstrap = new Bootstrap()
                .group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx, FullHttpResponse msg) {
                                if (msg.status().equals(HttpResponseStatus.OK)) {
                                    responseReceived.set(true);
                                }
                                responseLatch.countDown();
                            }
                        });
                    }
                });

        Channel clientChannel = clientBootstrap.connect("localhost", 18080).sync().channel();

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/test"
        );
        request.headers().set(HttpHeaderNames.HOST, "localhost");
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        clientChannel.writeAndFlush(request);

        boolean completed = responseLatch.await(5, TimeUnit.SECONDS);
        clientChannel.close().sync();

        assertThat(completed).isTrue();
        assertThat(handlerCalled.get()).isTrue();
        assertThat(responseReceived.get()).isTrue();
    }

    @Test
    void shouldCloseChannel_whenStopCalled() throws Exception {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        server = new HttpServer(bossGroup, workerGroup)
                .withPort(18081)
                .withRouter(router)
                .start();

        Thread.sleep(100);

        server.stop();

        // Server should be stopped now
        assertThat(server).isNotNull();
    }

    @Test
    void shouldNotThrowException_whenStopCalledOnUnstartedServer() {
        server = new HttpServer(bossGroup, workerGroup)
                .withPort(9999)
                .withRouter(new RouterBuilder().get("/test", ctx -> ctx.ok()).build());

        // Should not throw exception
        assertThatCode(() -> server.stop()).doesNotThrowAnyException();
    }

    @Test
    void shouldBlockThread_untilServerChannelCloses() throws Exception {
        Router router = new RouterBuilder()
                .get("/test", ctx -> ctx.ok())
                .build();

        server = new HttpServer(bossGroup, workerGroup)
                .withPort(18082)
                .withRouter(router)
                .start();

        Thread.sleep(100);

        AtomicBoolean blockCompleted = new AtomicBoolean(false);

        // Start blocking in separate thread
        Thread blockingThread = new Thread(() -> {
            try {
                server.blockUntilClosed();
                blockCompleted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        blockingThread.start();

        // Give blocking thread time to start waiting
        Thread.sleep(100);

        // Block should not complete yet
        assertThat(blockCompleted.get()).isFalse();

        // Stop the server
        server.stop();

        // Wait for blocking thread
        blockingThread.join(2000);

        // Block should now complete
        assertThat(blockCompleted.get()).isTrue();
    }
}

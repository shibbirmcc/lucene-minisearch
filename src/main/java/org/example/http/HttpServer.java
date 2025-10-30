package org.example.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.router.Router;

/**
 * A lightweight wrapper around a Netty HTTP server bootstrap.
 *
 * <p>This class provides a minimal abstraction to start and stop
 * an HTTP server with a configurable port and {@link Router} for handling requests.
 */

public final class HttpServer {
    private final EventLoopGroup boss;
    private final EventLoopGroup worker;
    private int port;
    private Channel serverChannel;
    private ChannelFuture closeFuture;
    private Router router;

    /**
     * Creates a new HTTP server with shared event loop groups.
     *
     * @param boss   the boss group for accepting new connections
     * @param worker the worker group for handling I/O
     */
    public HttpServer(EventLoopGroup boss, EventLoopGroup worker) {
        this.boss = boss;
        this.worker = worker;
    }

    /**
     * Sets the port for this server to listen on.
     *
     * @param port the TCP port number
     * @return this server instance
     */
    public HttpServer withPort(int port){
        this.port = port;
        return this;
    }

    /**
     * Sets the router that defines how HTTP requests are handled.
     *
     * @param router the router implementation
     * @return this server instance
     */
    public HttpServer withRouter(Router router){
        this.router = router;
        return this;
    }

    /**
     * Starts the Netty HTTP server and binds to the configured port.
     *
     * @return this server instance
     * @throws InterruptedException if binding is interrupted
     */
    public HttpServer start() throws InterruptedException {
        var b = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new NettyHttpInitializer(router));

        serverChannel = b.bind(port).sync().channel();
        closeFuture = serverChannel.closeFuture();
        return this;
    }

    /**
     * Stops the server by closing the channel if it is running.
     * **/
    public void stop(){
        if(serverChannel != null){
            serverChannel.close();
        }
    }

    /**
     * Blocks the calling thread until the server channel is closed.
     *
     * @throws InterruptedException if waiting is interrupted
     */
    public void blockUntilClosed() throws InterruptedException {
        if(closeFuture != null){
            closeFuture.sync();
        }
    }
}

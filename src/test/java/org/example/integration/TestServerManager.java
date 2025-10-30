package org.example.integration;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.example.http.HttpServer;
import org.example.routes.AppRouter;
import org.example.routes.MetricsRouter;

import java.io.IOException;

/**
 * Singleton manager for test servers.
 *
 * <p>This class ensures that only one instance of the test servers is created
 * and shared across all integration tests. This prevents port binding conflicts
 * when multiple test classes try to start servers on the same ports.
 *
 * <p>The servers are started lazily on first access and shut down via JVM
 * shutdown hook.
 */
public final class TestServerManager {

    private static final int APP_PORT = 28080;
    private static final int METRICS_PORT = 29090;

    private static volatile TestServerManager instance;
    private static final Object lock = new Object();

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final HttpServer appServer;
    private final HttpServer metricsServer;
    private final TestHttpClient httpClient;

    private TestServerManager() throws InterruptedException, IOException {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(2);

        // Start app server
        this.appServer = new HttpServer(bossGroup, workerGroup)
                .withPort(APP_PORT)
                .withRouter(new AppRouter())
                .start();

        // Start metrics server
        this.metricsServer = new HttpServer(workerGroup, bossGroup)
                .withPort(METRICS_PORT)
                .withRouter(new MetricsRouter())
                .start();

        // Initialize HTTP client
        this.httpClient = new TestHttpClient();

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Give servers time to fully start
        Thread.sleep(200);
    }

    /**
     * Gets the singleton instance of the test server manager.
     * Starts the servers if they haven't been started yet.
     *
     * @return the server manager instance
     * @throws RuntimeException if server startup fails
     */
    public static TestServerManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    try {
                        instance = new TestServerManager();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to start test servers", e);
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Gets the HTTP client for making test requests.
     *
     * @return the test HTTP client
     */
    public TestHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Gets the application server port.
     *
     * @return the app server port
     */
    public int getAppPort() {
        return APP_PORT;
    }

    /**
     * Gets the metrics server port.
     *
     * @return the metrics server port
     */
    public int getMetricsPort() {
        return METRICS_PORT;
    }

    /**
     * Gets the application server base URL.
     *
     * @return the app server base URL
     */
    public String getAppBaseUrl() {
        return "http://localhost:" + APP_PORT;
    }

    /**
     * Gets the metrics server base URL.
     *
     * @return the metrics server base URL
     */
    public String getMetricsBaseUrl() {
        return "http://localhost:" + METRICS_PORT;
    }

    /**
     * Shuts down the test servers and releases resources.
     * This is called automatically via shutdown hook.
     */
    private void shutdown() {
        if (httpClient != null) {
            httpClient.close();
        }

        if (appServer != null) {
            appServer.stop();
        }

        if (metricsServer != null) {
            metricsServer.stop();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}

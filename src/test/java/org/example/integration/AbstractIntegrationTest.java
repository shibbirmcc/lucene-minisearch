package org.example.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

/**
 * Abstract base class for all integration tests.
 *
 * <p>This class provides common utilities for integration testing by delegating
 * to the {@link TestServerManager} singleton. All integration tests should extend
 * this class to ensure consistent server access and configuration.
 *
 * <p>The servers are managed by a singleton and shared across all test classes.
 * This prevents port binding conflicts and improves test performance by avoiding
 * repeated server startup/shutdown.
 *
 * <p>Usage:
 * <pre>{@code
 * @Tag("integration")
 * class MyFeatureIT extends AbstractIntegrationTest {
 *     @Test
 *     void shouldDoSomething_whenCondition() {
 *         // Test implementation using getAppBaseUrl() or getMetricsBaseUrl()
 *         HttpResponse response = getHttpClient().get(getAppBaseUrl() + "/my-endpoint");
 *         assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
 *     }
 * }
 * }</pre>
 */
@Tag("integration")
public abstract class AbstractIntegrationTest {

    private static TestServerManager serverManager;

    /**
     * Initializes the server manager before any tests run.
     * This ensures servers are started before test execution.
     */
    @BeforeAll
    static void initializeServerManager() {
        serverManager = TestServerManager.getInstance();
    }

    /**
     * Returns the base URL for the application server.
     *
     * @return the app server base URL (e.g., "http://localhost:28080")
     */
    protected static String getAppBaseUrl() {
        return serverManager.getAppBaseUrl();
    }

    /**
     * Returns the base URL for the metrics server.
     *
     * @return the metrics server base URL (e.g., "http://localhost:29090")
     */
    protected static String getMetricsBaseUrl() {
        return serverManager.getMetricsBaseUrl();
    }

    /**
     * Returns the shared HTTP client for making test requests.
     *
     * @return the test HTTP client instance
     */
    protected static TestHttpClient getHttpClient() {
        return serverManager.getHttpClient();
    }

    /**
     * Returns the application server port.
     *
     * @return the app server port number
     */
    protected static int getAppPort() {
        return serverManager.getAppPort();
    }

    /**
     * Returns the metrics server port.
     *
     * @return the metrics server port number
     */
    protected static int getMetricsPort() {
        return serverManager.getMetricsPort();
    }
}

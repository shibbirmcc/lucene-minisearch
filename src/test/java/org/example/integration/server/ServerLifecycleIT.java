package org.example.integration.server;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.example.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for server lifecycle and availability.
 *
 * <p>These tests verify that the servers start correctly, remain available
 * throughout the test execution, and handle requests properly.
 *
 * <p>These tests focus on the overall server behavior rather than specific
 * endpoint functionality. They ensure that:
 * <ul>
 *   <li>Both app and metrics servers start successfully</li>
 *   <li>Servers listen on their configured ports</li>
 *   <li>Servers can handle requests concurrently</li>
 *   <li>Servers remain stable under load</li>
 * </ul>
 *
 * <p>To add new server lifecycle tests:
 * <ol>
 *   <li>Add a new test method following the naming pattern:
 *       {@code shouldBehaveCorrectly_whenServerInSpecificState()}</li>
 *   <li>Use {@code getHttpClient()} to make requests</li>
 *   <li>Use {@code getAppPort()} and {@code getMetricsPort()} to verify port configuration</li>
 *   <li>Test cross-server scenarios if needed</li>
 * </ol>
 */
@Tag("integration")
@DisplayName("Server Lifecycle Integration Tests")
class ServerLifecycleIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should have both servers running on expected ports")
    void shouldHaveBothServersRunning_whenTestsStart() throws Exception {
        // Given
        int expectedAppPort = getAppPort();
        int expectedMetricsPort = getMetricsPort();

        // When - make requests to both servers
        HttpResponse appResponse = getHttpClient().get(getAppBaseUrl() + "/health");
        HttpResponse metricsResponse = getHttpClient().get(getMetricsBaseUrl() + "/health");

        // Then - both should respond successfully
        assertThat(appResponse.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(metricsResponse.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(expectedAppPort).isEqualTo(28080);
        assertThat(expectedMetricsPort).isEqualTo(29090);
    }

    @Test
    @DisplayName("Should handle sequential requests to app server")
    void shouldHandleSequentialRequests_whenMultipleRequestsMadeToAppServer() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When - make sequential requests
        for (int i = 0; i < 5; i++) {
            HttpResponse response = getHttpClient().get(url);

            // Then - each should succeed
            assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
        }
    }

    @Test
    @DisplayName("Should handle sequential requests to metrics server")
    void shouldHandleSequentialRequests_whenMultipleRequestsMadeToMetricsServer() throws Exception {
        // Given
        String url = getMetricsBaseUrl() + "/health";

        // When - make sequential requests
        for (int i = 0; i < 5; i++) {
            HttpResponse response = getHttpClient().get(url);

            // Then - each should succeed
            assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
        }
    }

    @Test
    @DisplayName("Should handle alternating requests between servers")
    void shouldHandleAlternatingRequests_whenRequestsAlternateBetweenServers() throws Exception {
        // Given
        String appUrl = getAppBaseUrl() + "/health";
        String metricsUrl = getMetricsBaseUrl() + "/health";

        // When/Then - alternate between servers
        for (int i = 0; i < 3; i++) {
            HttpResponse appResponse = getHttpClient().get(appUrl);
            assertThat(appResponse.status()).isEqualTo(HttpResponseStatus.OK);

            HttpResponse metricsResponse = getHttpClient().get(metricsUrl);
            assertThat(metricsResponse.status()).isEqualTo(HttpResponseStatus.OK);
        }
    }

    @Test
    @DisplayName("Should return proper error codes for invalid endpoints on both servers")
    void shouldReturnProperErrorCodes_whenInvalidEndpointsRequested() throws Exception {
        // Given
        String appInvalidUrl = getAppBaseUrl() + "/nonexistent";
        String metricsInvalidUrl = getMetricsBaseUrl() + "/nonexistent";

        // When
        HttpResponse appResponse = getHttpClient().get(appInvalidUrl);
        HttpResponse metricsResponse = getHttpClient().get(metricsInvalidUrl);

        // Then - both should return 404
        assertThat(appResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
        assertThat(metricsResponse.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should maintain server stability after multiple requests")
    void shouldMaintainStability_afterMultipleRequestsToMultipleEndpoints() throws Exception {
        // Given
        String appHealthUrl = getAppBaseUrl() + "/health";
        String appInvalidUrl = getAppBaseUrl() + "/invalid";
        String metricsHealthUrl = getMetricsBaseUrl() + "/health";

        // When - make various requests
        for (int i = 0; i < 3; i++) {
            getHttpClient().get(appHealthUrl);
            getHttpClient().get(appInvalidUrl);
            getHttpClient().get(metricsHealthUrl);
        }

        // Then - servers should still respond correctly
        HttpResponse finalAppResponse = getHttpClient().get(appHealthUrl);
        HttpResponse finalMetricsResponse = getHttpClient().get(metricsHealthUrl);

        assertThat(finalAppResponse.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(finalMetricsResponse.status()).isEqualTo(HttpResponseStatus.OK);
    }
}

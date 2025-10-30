package org.example.integration.api;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.example.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the metrics health endpoint.
 *
 * <p>These tests verify the health check functionality of the metrics
 * server by making real HTTP requests and validating responses.
 *
 * <p>The metrics server runs on a separate port from the main application
 * and provides monitoring/observability endpoints.
 *
 * <p>To add new metrics-related integration tests:
 * <ol>
 *   <li>Add a new test method following the naming pattern:
 *       {@code shouldReturnExpectedResult_whenCondition()}</li>
 *   <li>Use {@code getHttpClient()} to make requests</li>
 *   <li>Use {@code getMetricsBaseUrl()} for the metrics server URL</li>
 *   <li>Add appropriate assertions using AssertJ</li>
 * </ol>
 */
@Tag("integration")
@DisplayName("Metrics Endpoint Integration Tests")
class MetricsEndpointIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should return 200 OK when GET /health is requested on metrics server")
    void shouldReturn200OK_whenMetricsHealthEndpointRequested() throws Exception {
        // Given
        String url = getMetricsBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().get(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response.headers().get("content-type"))
                .isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    @DisplayName("Should return 404 when POST is used on metrics /health endpoint")
    void shouldReturn404_whenPostUsedOnMetricsHealthEndpoint() throws Exception {
        // Given
        String url = getMetricsBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().post(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when unknown endpoint is requested on metrics server")
    void shouldReturn404_whenUnknownMetricsEndpointRequested() throws Exception {
        // Given
        String url = getMetricsBaseUrl() + "/unknown";

        // When
        HttpResponse response = getHttpClient().get(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 200 on metrics server independently of app server")
    void shouldReturn200OnMetricsServer_independentlyOfAppServer() throws Exception {
        // Given
        String metricsUrl = getMetricsBaseUrl() + "/health";
        String appUrl = getAppBaseUrl() + "/health";

        // When - both servers should respond independently
        HttpResponse metricsResponse = getHttpClient().get(metricsUrl);
        HttpResponse appResponse = getHttpClient().get(appUrl);

        // Then - both should return 200 OK
        assertThat(metricsResponse.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(appResponse.status()).isEqualTo(HttpResponseStatus.OK);
    }

    @Test
    @DisplayName("Should handle concurrent requests to metrics endpoint")
    void shouldHandleConcurrentRequests_whenMultipleMetricsRequestsMade() throws Exception {
        // Given
        String url = getMetricsBaseUrl() + "/health";

        // When - make multiple concurrent requests
        HttpResponse response1 = getHttpClient().get(url);
        HttpResponse response2 = getHttpClient().get(url);
        HttpResponse response3 = getHttpClient().get(url);

        // Then - all should succeed
        assertThat(response1.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response2.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response3.status()).isEqualTo(HttpResponseStatus.OK);
    }
}

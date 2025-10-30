package org.example.integration.api;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.example.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the application health endpoint.
 *
 * <p>These tests verify the health check functionality of the main
 * application server by making real HTTP requests and validating responses.
 *
 * <p>To add new health-related integration tests:
 * <ol>
 *   <li>Add a new test method following the naming pattern:
 *       {@code shouldReturnExpectedResult_whenCondition()}</li>
 *   <li>Use {@code getHttpClient()} to make requests</li>
 *   <li>Use {@code getAppBaseUrl()} for the application server URL</li>
 *   <li>Add appropriate assertions using AssertJ</li>
 * </ol>
 */
@Tag("integration")
@DisplayName("Health Endpoint Integration Tests")
class HealthEndpointIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should return 200 OK when GET /health is requested")
    void shouldReturn200OK_whenHealthEndpointRequested() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().get(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response.headers().get("content-type"))
                .isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    @DisplayName("Should return 404 when POST is used on /health endpoint")
    void shouldReturn404_whenPostUsedOnHealthEndpoint() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().post(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when PUT is used on /health endpoint")
    void shouldReturn404_whenPutUsedOnHealthEndpoint() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().put(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when DELETE is used on /health endpoint")
    void shouldReturn404_whenDeleteUsedOnHealthEndpoint() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When
        HttpResponse response = getHttpClient().delete(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when unknown endpoint is requested")
    void shouldReturn404_whenUnknownEndpointRequested() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/unknown";

        // When
        HttpResponse response = getHttpClient().get(url);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return consistent response when health endpoint called multiple times")
    void shouldReturnConsistentResponse_whenHealthCalledMultipleTimes() throws Exception {
        // Given
        String url = getAppBaseUrl() + "/health";

        // When - make multiple requests
        HttpResponse response1 = getHttpClient().get(url);
        HttpResponse response2 = getHttpClient().get(url);
        HttpResponse response3 = getHttpClient().get(url);

        // Then - all should return 200 OK
        assertThat(response1.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response2.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(response3.status()).isEqualTo(HttpResponseStatus.OK);
    }
}

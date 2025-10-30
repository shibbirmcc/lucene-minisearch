# Integration Testing Framework

## Overview

This integration testing framework provides a robust, scalable structure for testing the entire application stack end-to-end. It follows industry best practices for Java integration testing and makes it easy to add new tests as features are developed.

## Architecture

```
src/test/java/org/example/integration/
├── AbstractIntegrationTest.java    # Base class for all integration tests
├── TestServerManager.java          # Singleton server lifecycle manager
├── TestHttpClient.java             # HTTP client for test requests
├── api/                            # API endpoint integration tests
│   ├── HealthEndpointIT.java
│   └── MetricsEndpointIT.java
└── server/                         # Server lifecycle integration tests
    └── ServerLifecycleIT.java
```

## Design Principles

### 1. Singleton Server Management
- **TestServerManager**: Ensures only one instance of test servers runs across all test classes
- Prevents port binding conflicts
- Improves test performance by avoiding repeated server startup/shutdown
- Servers shut down automatically via JVM shutdown hook

### 2. Base Test Class Pattern
- **AbstractIntegrationTest**: Provides common utilities and server access
- All integration tests extend this base class
- Ensures consistent configuration across all test classes

### 3. Test Organization by Feature
- Tests are organized in packages by feature area (`api/`, `server/`)
- Each feature gets its own test class with `IT` suffix
- Easy to locate and extend tests for specific features

### 4. Naming Conventions

#### Package Structure
```
org.example.integration           # Base integration tests
org.example.integration.api       # API/endpoint tests
org.example.integration.server    # Server infrastructure tests
```

#### Class Names
- Use `IT` suffix for integration test classes (e.g., `HealthEndpointIT`)
- Descriptive names that indicate the feature under test
- Compatible with Maven Failsafe plugin convention

#### Test Method Names
Follow the pattern: `should<ExpectedBehavior>_when<Condition>()`

Examples:
- `shouldReturn200OK_whenHealthEndpointRequested()`
- `shouldReturn404_whenUnknownEndpointRequested()`
- `shouldHandleConcurrentRequests_whenMultipleRequestsMade()`

### 5. JUnit 5 Features
- `@Tag("integration")` for test categorization
- `@DisplayName` for readable test reports
- `@BeforeAll` for server initialization
- AssertJ for fluent assertions

## Adding New Integration Tests

### Step 1: Create Test Class

Choose the appropriate package based on what you're testing:
- `api/` - For API endpoints and request/response testing
- `server/` - For server infrastructure and lifecycle testing

Create a new class extending `AbstractIntegrationTest`:

```java
package org.example.integration.api;

import org.example.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
@DisplayName("My New Feature Integration Tests")
class MyNewFeatureIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should return expected result when feature is used")
    void shouldReturnExpectedResult_whenFeatureUsed() throws Exception {
        // Test implementation
    }
}
```

### Step 2: Write Test Methods

Use the helper methods provided by `AbstractIntegrationTest`:

```java
@Test
void shouldProcessRequest_whenValidDataProvided() throws Exception {
    // Given - prepare test data
    String url = getAppBaseUrl() + "/my-endpoint";

    // When - execute the request
    HttpResponse response = getHttpClient().get(url);

    // Then - verify the results
    assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
    assertThat(response.headers().get("content-type"))
        .contains("application/json");
}
```

### Step 3: Run Tests

Run all tests:
```bash
./gradlew test
```

Run only integration tests:
```bash
./gradlew test --tests "*IT"
```

Run tests for a specific class:
```bash
./gradlew test --tests "HealthEndpointIT"
```

## Available Helper Methods

### From AbstractIntegrationTest

| Method | Description | Example |
|--------|-------------|---------|
| `getAppBaseUrl()` | Get app server base URL | `"http://localhost:28080"` |
| `getMetricsBaseUrl()` | Get metrics server base URL | `"http://localhost:29090"` |
| `getHttpClient()` | Get HTTP client instance | Used for making requests |
| `getAppPort()` | Get app server port | `28080` |
| `getMetricsPort()` | Get metrics server port | `29090` |

### From TestHttpClient

| Method | Description |
|--------|-------------|
| `get(String url)` | Perform GET request |
| `post(String url)` | Perform POST request |
| `put(String url)` | Perform PUT request |
| `delete(String url)` | Perform DELETE request |

## Example Test Patterns

### Testing Successful Response
```java
@Test
void shouldReturnSuccess_whenValidRequest() throws Exception {
    HttpResponse response = getHttpClient().get(getAppBaseUrl() + "/endpoint");

    assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
}
```

### Testing Error Handling
```java
@Test
void shouldReturn404_whenResourceNotFound() throws Exception {
    HttpResponse response = getHttpClient().get(getAppBaseUrl() + "/nonexistent");

    assertThat(response.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
}
```

### Testing Multiple Requests
```java
@Test
void shouldHandleMultipleRequests_whenCalledConcurrently() throws Exception {
    String url = getAppBaseUrl() + "/endpoint";

    for (int i = 0; i < 10; i++) {
        HttpResponse response = getHttpClient().get(url);
        assertThat(response.status()).isEqualTo(HttpResponseStatus.OK);
    }
}
```

### Testing Cross-Server Scenarios
```java
@Test
void shouldRespondIndependently_whenBothServersQueried() throws Exception {
    HttpResponse appResponse = getHttpClient().get(getAppBaseUrl() + "/health");
    HttpResponse metricsResponse = getHttpClient().get(getMetricsBaseUrl() + "/health");

    assertThat(appResponse.status()).isEqualTo(HttpResponseStatus.OK);
    assertThat(metricsResponse.status()).isEqualTo(HttpResponseStatus.OK);
}
```

## Best Practices

1. **One Feature Per Test Class**: Keep test classes focused on a single feature or endpoint
2. **Descriptive Test Names**: Use the `should/when` pattern for clarity
3. **Given-When-Then**: Structure test logic clearly with comments
4. **Independent Tests**: Each test should be independent and not rely on execution order
5. **Use DisplayName**: Add `@DisplayName` for readable test reports
6. **Tag Tests**: Use `@Tag("integration")` for test categorization
7. **Clean Assertions**: Use AssertJ's fluent API for readable assertions

## Test Categories

Tests are tagged with `@Tag("integration")` to differentiate them from unit tests. This allows selective test execution:

```bash
# Run only integration tests
./gradlew test --tests "*IT"

# Run only unit tests (exclude integration)
./gradlew test --exclude-tag integration
```

## Performance Considerations

- **Server Lifecycle**: Servers start once and are shared across all tests
- **Connection Pooling**: HTTP client reuses connections where possible
- **Parallel Execution**: Tests within a class run sequentially, but classes can run in parallel
- **Fast Feedback**: Tests typically complete in under 5 seconds total

## Troubleshooting

### Port Already in Use
If you see port binding errors:
```bash
# Check what's using the ports
lsof -ti:28080,29090

# Kill processes on those ports
lsof -ti:28080,29090 | xargs kill -9
```

### Tests Hanging
- Check that servers are starting correctly
- Verify no firewall blocking localhost connections
- Ensure sufficient system resources (file descriptors, memory)

### Connection Refused
- Verify servers have sufficient time to start (200ms sleep in TestServerManager)
- Check server logs for startup errors
- Confirm ports are not blocked by security software


# Lucene MiniSearch

[![CI/CD Pipeline](https://github.com/shibbirmcc/lucene-minisearch/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/shibbirmcc/lucene-minisearch/actions/workflows/ci-cd.yml)
[![codecov](https://codecov.io/gh/shibbirmcc/lucene-minisearch/branch/master/graph/badge.svg)](https://codecov.io/gh/shibbirmcc/lucene-minisearch)
[![Docker Image](https://img.shields.io/docker/v/shibbirmcc/lucene-minisearch?label=docker&logo=docker)](https://hub.docker.com/r/shibbirmcc/lucene-minisearch)
[![Docker Pulls](https://img.shields.io/docker/pulls/shibbirmcc/lucene-minisearch)](https://hub.docker.com/r/shibbirmcc/lucene-minisearch)
[![Java Version](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)

A high-performance, lightweight HTTP server built on Netty for Lucene-based Index and Search functionality.

## Overview

Lucene MiniSearch is a minimal, production-ready search service that provides:
- Fast HTTP API for search operations
- Separate metrics/monitoring endpoint
- Lucene full-text search capabilities
- Zero-downtime health checks
- YAML-based configuration

## Architecture

The application consists of two HTTP servers:
- **Application Server** (port 8080): Main search API endpoints
- **Metrics Server** (port 9090): Health checks and monitoring

Both servers run on Netty's non-blocking event loop architecture for high throughput and low latency.

## Tech Stack

- **Runtime**: Java 25
- **HTTP Server**: Netty 4.x
- **Search Engine**: Apache Lucene 9.x
- **Configuration**: SnakeYAML
- **Testing**: JUnit 5, AssertJ
- **Build**: Gradle 9.x

## Quick Start

### Prerequisites
- Java 25 or higher
- Gradle 9.x (or use included wrapper)

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew run
```

The servers will start on:
- Application: http://localhost:8080
- Metrics: http://localhost:9090

### Health Check

```bash
# Application server
curl http://localhost:8080/health

# Metrics server
curl http://localhost:9090/health
```

### Docker

#### Pull and Run

```bash
# Pull the latest image
docker pull shibbirmcc/lucene-minisearch:latest

# Run the container
docker run -d \
  -p 8080:8080 \
  -p 9090:9090 \
  --name lucene-minisearch \
  shibbirmcc/lucene-minisearch:latest
```

#### Build Locally

```bash
# Build the Docker image
docker build -t lucene-minisearch .

# Run the container
docker run -d \
  -p 8080:8080 \
  -p 9090:9090 \
  --name lucene-minisearch \
  lucene-minisearch
```

#### Docker Compose

```yaml
version: '3.8'
services:
  lucene-minisearch:
    image: shibbirmcc/lucene-minisearch:latest
    ports:
      - "8080:8080"
      - "9090:9090"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9090/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
```

## Configuration

Configuration is managed via `src/main/resources/application.yaml`:

```yaml
server:
  app-port: 8080
  metric-port: 9090
lucene:
  data-store: "/lucene-data"
```

## Development

### Running Tests

```bash
# Run all tests (unit + integration)
./gradlew test

# Run only unit tests
./gradlew test --exclude-tag integration

# Run only integration tests
./gradlew test --tests "*IT"
```

### Code Quality

The project follows Java best practices:
- Clean architecture with separation of concerns
- Comprehensive unit and integration testing
- Test naming: `should_expectedBehavior_when_condition()`
- JavaDoc documentation for public APIs

## Testing

For detailed testing documentation, see **[integration-tests.md](integration-tests.md)**

## API Documentation

### Health Endpoint

**GET** `/health`

Returns the health status of the service.

**Response:**
- Status: `200 OK`
- Content-Type: `text/plain; charset=UTF-8`
- Body: `OK`

**Example:**
```bash
curl http://localhost:8080/health
# Response: OK
```

## Performance

- **Non-blocking I/O**: Netty's event loop handles thousands of concurrent connections
- **Zero-copy**: Direct buffer usage for minimal memory overhead
- **Lightweight**: Small memory footprint suitable for containerized deployments
- **Fast startup**: Server initialization in under 500ms

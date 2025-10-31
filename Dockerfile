FROM eclipse-temurin:25-jre

WORKDIR /app

RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY build/libs/*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080 9090

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:9090/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

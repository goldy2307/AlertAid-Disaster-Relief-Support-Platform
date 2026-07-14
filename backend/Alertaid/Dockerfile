# Build stage
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .

# Detect whether we're building from the module directory or the repo root,
# then compile and stage the jar in a stable location.
RUN set -eux; \
    MODULE_PATH="."; \
    if [ ! -f "pom.xml" ]; then \
        if [ -f "backend/Alertaid/pom.xml" ]; then \
            MODULE_PATH="backend/Alertaid"; \
        else \
            echo "Unable to locate backend pom.xml" >&2; \
            exit 1; \
        fi; \
    fi; \
    cd "$MODULE_PATH"; \
    mvn -q -DskipTests package; \
    JAR_PATH="$(ls target/*.jar | head -n 1)"; \
    cp "$JAR_PATH" /workspace/app.jar

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar
# Frontend dir (built React app + legacy pages) — must exist at build
# context root as ./frontend, dist already built (npm run build) before
# `docker build` runs. This is the ONLY copy of the frontend Spring
# serves in prod — no duplicate baked into the jar's static resources.
COPY frontend /app/frontend
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV FRONTEND_DIR=/app/frontend
# Note: DATABASE_URL will be provided by Render automatically
ENTRYPOINT ["java","-jar","/app/app.jar"]

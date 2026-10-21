# syntax=docker/dockerfile:1.7

# ---------- Stage 1: build ----------
# Build context is the ecommerce/ multi-module root:
#   docker build -f stats-writer/Dockerfile -t springboot-ecommerce/stats-writer:latest .
# NOTE: the build stage must be glibc-based (Debian/Ubuntu), NOT Alpine.
# The protobuf-maven-plugin downloads glibc-linked native protoc +
# protoc-gen-grpc-java binaries that cannot execute on musl libc.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Pre-fetch dependencies for the target module + its reactor deps (caches ~/.m2).
# Every module pom must be present because the root pom declares the full reactor.
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./
COPY common/pom.xml common/
COPY role/pom.xml role/
COPY user/pom.xml user/
COPY merchant/pom.xml merchant/
COPY merchant_award/pom.xml merchant_award/
COPY merchant_business/pom.xml merchant_business/
COPY merchant_detail/pom.xml merchant_detail/
COPY merchant_policy/pom.xml merchant_policy/
COPY merchant_social_link/pom.xml merchant_social_link/
COPY category/pom.xml category/
COPY product/pom.xml product/
COPY banner/pom.xml banner/
COPY slider/pom.xml slider/
COPY cart/pom.xml cart/
COPY order/pom.xml order/
COPY order_item/pom.xml order_item/
COPY review/pom.xml review/
COPY review_detail/pom.xml review_detail/
COPY shipping_address/pom.xml shipping_address/
COPY transaction/pom.xml transaction/
COPY auth/pom.xml auth/
COPY gateway/pom.xml gateway/
COPY email-service/pom.xml email-service/
COPY db-migration/pom.xml db-migration/
COPY stats-reader/pom.xml stats-reader/
COPY stats-writer/pom.xml stats-writer/
COPY stats-backfill/pom.xml stats-backfill/
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -pl stats-writer -am dependency:go-offline

# Copy sources and package the fat jar.
COPY common/src common/src
COPY stats-writer/src stats-writer/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -pl stats-writer -am -DskipTests package

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Non-root runtime user (UID 1001, matches the k8s securityContext).
RUN addgroup -g 1001 -S app \
    && adduser -u 1001 -S app -G app \
    && mkdir -p /var/log/app \
    && chown -R 1001:1001 /app /var/log/app

COPY --from=build --chown=1001:1001 \
    /workspace/stats-writer/target/stats-writer-0.0.1-SNAPSHOT.jar /app/app.jar

USER 1001:1001

# Actuator management port (management.server.port). No gRPC/HTTP server surface;
# this is a Kafka consumer that runs continuously.
EXPOSE 9395

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

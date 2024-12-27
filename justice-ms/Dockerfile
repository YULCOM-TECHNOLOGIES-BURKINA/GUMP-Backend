# Start with eclipse-temurin as base
FROM eclipse-temurin:17-jre-alpine

# Install required packages
RUN apk add --no-cache \
    openconnect \
    bash \
    curl \
    openssl \
    iptables \
    ip6tables \
    iproute2 \
    procps

# Set environment variables
ENV deploy_env=local

# Copy your application
COPY target/*.jar app.jar

# Create a startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Set the entrypoint to your startup script
ENTRYPOINT ["/start.sh"]

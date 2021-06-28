FROM open-liberty:full-java11-openj9

# Add server configuration
COPY --chown=1001:0  src/main/liberty/config/server.xml /config/

# Add the application
COPY --chown=1001:0  target/*.war /config/dropins/
# This script will add the requested server configurations, apply any interim fixes and populate caches to optimize runtime.
RUN configure.sh
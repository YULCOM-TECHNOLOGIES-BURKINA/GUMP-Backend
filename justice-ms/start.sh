#!/bin/bash

# Start VPN connection in the background
ORIGINAL_GATEWAY=$(ip route | grep default | awk '{print $3}')

echo $VPN_PASSWORD | openconnect -u $VPN_USERNAME --passwd-on-stdin --protocol=fortinet $VPN_SERVER:10443 --servercert $SERVER_CERT --script-tun --script "vpn-slice justice-database eureka-server" \
 --background


# Wait for VPN connection to establish
sleep 5

DOCKER_GATEWAY=$(ip route | grep 'default via' | grep -v 'tun' | awk '{print $3}')

# Add routes for Docker services
ip route add default via $ORIGINAL_GATEWAY


# Start your Java application
exec java -Djava.awt.headless=true -jar app.jar

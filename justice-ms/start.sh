#!/bin/bash

# Start VPN connection in the background

echo $VPN_PASSWORD | openconnect -u $VPN_USERNAME --passwd-on-stdin --protocol=fortinet $VPN_SERVER:10443 --servercert $SERVER_CERT --background


# Wait for VPN connection to establish
sleep 5

# Start your Java application
exec java -Djava.awt.headless=true -jar app.jar

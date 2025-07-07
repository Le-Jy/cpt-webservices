#include "wifi.h"
#include "config.h"
#include <ESP8266WiFi.h>

void Wifi::wifi_setup() {
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      debug.print(".");
    }

    debug.println();
    debug.print("Connected, IP address: ");
    debug.println(WiFi.localIP());
}

String Wifi::get_ip()
{
    return WiFi.localIP().toString();
}

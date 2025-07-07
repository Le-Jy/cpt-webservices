#include "NodeMCU_client.h"

#include <ESP8266HTTPClient.h>

#include "config.h"

void NodeMCU_Client::send_get(String url) {
    HTTPClient http;
    WiFiClient client;

    if (http.begin(client, url)) {
        int httpCode = http.GET();

        if (httpCode > 0) {
            debug.printf("[HTTP] GET... code: %d\n", httpCode);

            if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
                String payload = http.getString();
                debug.println(payload);
            }
        }
        else {
            debug.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
        }

        http.end();
    }
    else {
        debug.printf("[HTTP} Unable to connect\n");
    }
}

void NodeMCU_Client::send_post(String url, String body) {
    HTTPClient http;
    WiFiClient client;
    debug.printf("Post to %s, body: %s\n", url, body);

    if (http.begin(client, url)) {
        http.addHeader("Content-Type", "application/json");
        int httpCode = http.POST(body);

        if (httpCode > 0) {
            debug.printf("[HTTP] POST... code: %d\n", httpCode);

            if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
                String payload = http.getString();
                debug.println(payload);
            }
        }
        else {
            debug.printf("[HTTP] POST... failed, error: %s\n", http.errorToString(httpCode).c_str());
        }

        http.end();
    }
    else {
        debug.printf("[HTTP} Unable to connect\n");
    }

}

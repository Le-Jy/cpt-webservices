#include "NodeMCU_server.h"

#include <ESP8266WebServer.h>
#include <uri/UriBraces.h>

#include "config.h"
#include "led.h"
#include "lcd.h"
#include "hum_temp_sensor.h"

// Changer avec mqtt, envoyer id before everything in the request
// Envoyer timestamp puis value (Json)
//
// DO NOT MODIFY, CRETAE A NEW MQTT CLIENT FILE

ESP8266WebServer server(80);

void NodeMCU_Server::handle_lcd() {
    if (server.pathArg(0).toInt() != 0) {
        server.send(404, "application/json", "The lcd screen requested doesn't exists");
    }

    int line = server.pathArg(1).toInt();
    if (line < 0 || line >= 2) {
        server.send(404, "application/json", "The lcd screen requested doesn't have that much lines");
    }

    LCD::lcd_display(server.arg("plain"), line);
    server.send(200, "application/json", "Hello from lcd");
}

void NodeMCU_Server::handle_led() {
    int led_id = server.pathArg(0).toInt();
    int on_or_off = server.pathArg(1).toInt();

    // Check if value "led_id" is a registered led
    if (led_id < 0 || led_id >= 2) {
        server.send(400, "application/json", "No registered led under this id");
    }
    // Check if the state is ok
    if (on_or_off != 0 && on_or_off != 1) {
        server.send(400, "application/json", "A led can only be on or off (1 or 0)");
    }

    // TODO: Implement the leds
    Led::led_set_status(led_id, on_or_off);
    server.send(200, "application/json", "hey " + led_id);

    // Change state of the led according to the body of the request
    // If on_or_off == 0 -> switch off the led
    // If on_or_off == 1 -> switch on the led
}

void NodeMCU_Server::handle_hum_threshold() {
    int new_threshold_value = server.pathArg(0).toInt();

    HumTempSensor::hum_change_threshold(new_threshold_value);

    server.send(200, "application/json", "Humidity threshold changed to " + String(new_threshold_value));
}


// Not implemented here
void NodeMCU_Server::handle_lum_threshold() {
    server.send(200, "application/json", "Luminosity threshold changed to ");
    return;

    int new_threshold_value = server.pathArg(0).toInt();

    HumTempSensor::hum_change_threshold(new_threshold_value);
}

void NodeMCU_Server::server_setup() {
    // Define endpoints
    server.on(UriBraces("/lcd/{}/{}"), handle_lcd);
    server.on(UriBraces("/led/{}/{}"), handle_led);
    server.on(UriBraces("/humidity_threshold/{}"), handle_hum_threshold);
    server.on(UriBraces("/luminosity_threshold/{}"), handle_lum_threshold);


    // Start server
    server.begin();
}

void NodeMCU_Server::server_listen() {
    server.handleClient();
}

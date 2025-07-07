#include <Arduino.h>
#include <ChainableLED.h>
#include <Wire.h>

#include "rgb_lcd.h"

#include "wifi.h"
#include "led.h"
#include "button.h"
#include "lcd.h"
#include "NodeMCU_server.h"
#include "NodeMCU_client.h"
#include "hum_temp_sensor.h"
#include "light_sensor.h"
#include "MQTT_publisher.h"

void setup() {

    debug.begin(115200);
    debug.println("DHTxx test!");
    Wire.begin();

    Led::led_setup();
    Led::led_off(0); // Turn off the builtin LED

    Wifi::wifi_setup();
    NodeMCU_Server::server_setup();

    LCD::lcd_setup();
    Button::button_setup();
    // Light_Sensor::setup();

    MQTT_Publisher::setup();

    // Register both leds
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, Led::register_component(0));
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, Led::register_component(1));

    // Register lcd
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, LCD::register_component(0));

    // Register Button
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, Button::register_component(0));

    // Register temp/hum sensor
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, HumTempSensor::register_component_temp(0));
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, HumTempSensor::register_component_hum(0));

    // Register light sensor
    NodeMCU_Client::send_post(BACKEND_CREATE_URI, Light_Sensor::register_component(0));
}

void loop() {
    MQTT_Publisher::loop();

    Button::button_main();
    HumTempSensor::hum_temp_sensor_main();
    // Light_Sensor::lum_sensor_main();

    NodeMCU_Server::server_listen();

    delay(500);
}

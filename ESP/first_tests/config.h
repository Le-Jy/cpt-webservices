#ifndef CONFIG_H
#define CONFIG_H

#include <Arduino.h>


// #define MANUAL

// Platform-dependent debug interface
#if defined(ARDUINO_ARCH_AVR)
    #define debug  Serial
#elif defined(ARDUINO_ARCH_SAMD) || defined(ARDUINO_ARCH_SAM)
    #ifdef SerialUSB
        #define debug  SerialUSB
    #else
        #define debug  Serial
    #endif
#else
    #define debug  Serial
#endif

// Wi-Fi credentials
#define WIFI_SSID "Theo"
#define WIFI_PASSWORD "abribus31"

// Pin definitions for the button and LEDs
#define BUTTON_PIN D8
#define LED_PIN1 D5
#define LED_PIN2 D6

// Used by the humidity and temperature sensor
#define DHTTYPE DHT11   // DHT 11
// Pin for the humidity and temperature sensor
#define DHTPIN D3

#define LCD_COLS 2
#define LCD_ROWS 16

#define TEAM_NAME "JOLIVY"
#define BROKER_IP "172.20.10.3"
#define BROKER_PORT 1883
#define BACKEND_CREATE_URI "http://172.20.10.3:8080/ssse/sensor/create"
#define BACKEND_UPDATE_THREASHOLD_URI "http://172.20.10.3:8080/ssse/sensor/create"

enum ComponentType {
  e_Humidity = 0,
  e_Sunlight,
  e_Button,
  e_Temperature,
  e_Lcd,
  e_Led
};

#endif

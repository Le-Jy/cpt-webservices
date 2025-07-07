// Example testing sketch for various DHT humidity/temperature sensors
// Written by ladyada, public domain

#include <ChainableLED.h>
#include <Wire.h>

#include "rgb_lcd.h"


void setup() {

    debug.begin(115200);
    debug.println("DHTxx test!");
    Wire.begin();

    /*if using WIO link,must pull up the power pin.*/
    // pinMode(PIN_GROVE_POWER, OUTPUT);
    // digitalWrite(PIN_GROVE_POWER, 1);

    lcd_setup();
    button_setup();
}

void loop() {
    button_main();

    hum_temp_sensor_main();

    delay(1500);
}

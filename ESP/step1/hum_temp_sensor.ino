#include "Grove_Temperature_And_Humidity_Sensor.h"

// Uncomment whatever type you're using!
#define DHTTYPE DHT11   // DHT 11
//#define DHTTYPE DHT22   // DHT 22  (AM2302)
//#define DHTTYPE DHT21   // DHT 21 (AM2301)
//#define DHTTYPE DHT10   // DHT 10
//#define DHTTYPE DHT20   // DHT 20

/*Notice: The DHT10 and DHT20 is different from other DHT* sensor ,it uses i2c interface rather than one wire*/
/*So it doesn't require a pin.*/
#define DHTPIN D3     // what pin we're connected to（DHT10 and DHT20 don't need define it）

DHT dht_hum_temp_sensor(DHTPIN, DHTTYPE);   //   DHT11 DHT21 DHT22


#if defined(ARDUINO_ARCH_AVR)
    #define debug  Serial

#elif defined(ARDUINO_ARCH_SAMD) ||  defined(ARDUINO_ARCH_SAM)
#ifdef SerialUSB
    #define debug  SerialUSB
#else
    #define debug  Serial
#endif
#else
    #define debug  Serial
#endif

// Here to optimise the lcd display, do not print if value is already displayed
int temp_prev_value = 0;
int hum_prev_value = 0;

void hum_temp_sensor_setup() {
    dht_hum_temp_sensor.begin();
}

void hum_sensor_checks(float hum_val) {
    constexpr int hum_threshold = 60;

    if (hum_val < 0 || hum_val > 100) {
        debug.println("Humidity out of bounds");
    }
    else if (hum_val <= hum_threshold) {
        debug.println("Humidity ok, Green led");
        if (hum_prev_value != 1) {
            lcd_display("Humidite ok     ", 0);
        }
        hum_prev_value = 1;
    }
    else {
        debug.println("Humidity too high, led red");
        if (hum_prev_value != 2) {
            lcd_display("Humidite Elevee ", 0);
        }
        hum_prev_value = 2;
    }
}

void temp_sensor_checks(float temp_val) {
    constexpr int temp_low_threshold = 18;
    constexpr int temp_high_threshold = 25;

    if (temp_val < -88 || temp_val > 57) { 
        debug.println("Temperature out of bounds");
    }
    else if (temp_val < temp_low_threshold) {
        debug.println("Température Basse");
        if (temp_prev_value != 1) {
            lcd_display("Temperature Basse ", 1);
        }

        temp_prev_value = 1;
    }
    else if (temp_val > temp_high_threshold) {
        debug.println("Température Elevée");
        if (temp_prev_value != 2) {
            lcd_display("Temperature Elevee ", 1);
        }
        temp_prev_value = 2;
    }
    else {
        debug.println("Température Normale");
        if (temp_prev_value != 3) {
            lcd_display("Temperature Normale ", 1);
        }
        temp_prev_value = 3;
    }
}

void hum_temp_sensor_main() {
    float temp_hum_val[2] = {0};
    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)


    if (!dht_hum_temp_sensor.readTempAndHumidity(temp_hum_val)) {

        hum_sensor_checks(temp_hum_val[0]);
        temp_sensor_checks(temp_hum_val[1]);
    }
    else {
        debug.println("Failed to get temprature and humidity value.");
    }

}
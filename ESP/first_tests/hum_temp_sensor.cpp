#include "hum_temp_sensor.h"

#include "Grove_Temperature_And_Humidity_Sensor.h"

#include "config.h"
#include "lcd.h"
#include "component_dto.h"
#include "MQTT_publisher.h"

DHT dht_hum_temp_sensor(DHTPIN, DHTTYPE);   //   DHT11 DHT21 DHT22
int hum_threshold = 60;

// Here to optimise the lcd display, do not print if value is already displayed
int temp_prev_value = 0;
int hum_prev_value = 0;

void HumTempSensor::hum_temp_sensor_setup() {
    dht_hum_temp_sensor.begin();
}

void HumTempSensor::hum_sensor_checks(float hum_val) {
    
    if (hum_val < 0 || hum_val > 100) {
        debug.println("Humidity out of bounds");
    }
    else if (hum_val <= hum_threshold) {
        debug.println("Humidity ok, Green led");
        if (hum_prev_value != 1) {
          LCD::lcd_display("Humidite ok     ", 0);
        }
        hum_prev_value = 1;
    }
    else {
        debug.println("Humidity too high, led red");
        if (hum_prev_value != 2) {
          LCD::lcd_display("Humidite Elevee ", 0);
        }
        hum_prev_value = 2;
    }
}

void HumTempSensor::hum_change_threshold(int new_threshold_value) {
    hum_threshold = new_threshold_value;
}

void HumTempSensor::temp_sensor_checks(float temp_val) {
    constexpr int temp_low_threshold = 18;
    constexpr int temp_high_threshold = 25;

    if (temp_val < -88 || temp_val > 57) { 
        debug.println("Temperature out of bounds");
    }
    else if (temp_val < temp_low_threshold) {
        if (temp_prev_value != 1) {
          LCD::lcd_display("Temperature Basse ", 1);
        }

        temp_prev_value = 1;
    }
    else if (temp_val > temp_high_threshold) {
        if (temp_prev_value != 2) {
          LCD::lcd_display("Temperature Elevee ", 1);
        }
        temp_prev_value = 2;
    }
    else {
        if (temp_prev_value != 3) {
          LCD::lcd_display("Temperature Normale ", 1);
        }
        temp_prev_value = 3;
    }
}

void HumTempSensor::hum_temp_sensor_main() {
    float temp_hum_val[2] = {0};
    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)

    if (!dht_hum_temp_sensor.readTempAndHumidity(temp_hum_val)) {
#ifdef MANUAL
        hum_sensor_checks(temp_hum_val[0]);
        temp_sensor_checks(temp_hum_val[1]);
        debug.printf("Temperature: %.1f °C, Humidity: %.1f %%\n", temp_hum_val[1], temp_hum_val[0]);
#else
        // Publish to MQTT broker
        MQTT_Publisher::publish_temp(0, temp_hum_val[1], Component_DTO::create_id(0, "TempSensor"));
        MQTT_Publisher::publish_hum(0, temp_hum_val[0], Component_DTO::create_id(0, "HumSensor"));
#endif
    
    }
    else {
        debug.println("Failed to get temprature and humidity value.");
    }
}

String HumTempSensor::register_component_temp(int id) {
    return Component_DTO::create(ComponentType::e_Temperature, id, "0", "TempSensor");
}

String HumTempSensor::create_dto_temp(int id, String value) {
    return Component_DTO::create(ComponentType::e_Temperature, id, value, "TempSensor");
}

String HumTempSensor::create_MQTT_temp(int id, String value) {
    return Component_DTO::create_MQTT_mess(ComponentType::e_Temperature, id, value, "TempSensor");
}

String HumTempSensor::register_component_hum(int id) {
    return Component_DTO::create(ComponentType::e_Humidity, id, "0", "HumSensor");
}

String HumTempSensor::create_dto_hum(int id, String value) {
    return Component_DTO::create(ComponentType::e_Humidity, id, value, "HumSensor");
}

String HumTempSensor::create_MQTT_hum(int id, String value) {
    return Component_DTO::create_MQTT_mess(ComponentType::e_Humidity, id, value, "HumSensor");
}

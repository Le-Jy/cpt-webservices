#ifndef HUM_TEMP_SENSOR_H
#define HUM_TEMP_SENSOR_H

#include <Arduino.h>

namespace HumTempSensor {
  void hum_temp_sensor_setup();
  void hum_sensor_checks(float hum_val);
  void hum_change_threshold(int new_threshold_value);
  void temp_sensor_checks(float temp_val);
  void hum_temp_sensor_main();
  String register_component_temp(int id);
  String register_component_hum(int id);
  String create_dto_temp(int id, String value);
  String create_dto_hum(int id, String value);
  String create_MQTT_hum(int id, String value);
  String create_MQTT_temp(int id, String value);
}

#endif /* !HUM_TEMP_SENSOR_H */

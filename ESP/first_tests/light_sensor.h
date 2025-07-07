#ifndef LIGHT_SENSOR_H
#define LIGHT_SENSOR_H

#include <Arduino.h>

namespace Light_Sensor {
  void setup();
  void lum_sensor_main();
  String register_component(int id);
  String create_dto(int id, String value);
}

#endif /* !LIGHT_SENSOR_H */

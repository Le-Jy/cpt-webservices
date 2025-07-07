#ifndef MQTT_PUBLISHER_H
#define MQTT_PUBLISHER_H

#include <Arduino.h>

namespace MQTT_Publisher {
  void setup();
  void publish_temp(int id, float temp_value, String id_final);
  void publish_hum(int id, int hum_value, String id_final);
  void publish_lum(int id, int lum_value);
  void publish_button(int id, int button_value, String id_final);
  void reconnect();
  void loop();
}

#endif /* !MQTT_PUBLISHER_H */

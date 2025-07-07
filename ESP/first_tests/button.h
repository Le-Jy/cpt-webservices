#ifndef BUTTON_H
#define BUTTON_H

#include <Arduino.h>

namespace Button {
  void button_setup();
  void button_main();
  String register_component(int id);
  String create_dto(int id, String value);
  String create_MQTT(int id, String value);
}

#endif /* !BUTTON_H */

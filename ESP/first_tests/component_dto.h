#ifndef COMPONENT_DTO_H
#define COMPONENT_DTO_H

#include <Arduino.h>

namespace Component_DTO {
  String create(int type, int id, String value, String component_name);
  String create_id(int id, String component_name);
  String create_MQTT_mess(int type, int id, String value, String component_name);
}

#endif /* !COMPONENT_DTO_H */

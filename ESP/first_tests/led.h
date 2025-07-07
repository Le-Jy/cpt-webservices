#ifndef LED_H
#define LED_H

#include "config.h"

#define NUM_LEDS  1

namespace Led {
  void led_setup();
  void led_set_status(int id, int status);
  void led_on(int id);
  void led_off(int id);
  String register_component(int id);
}

#endif /* !LED_H */

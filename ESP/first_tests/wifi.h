#ifndef WIFI_H
#define WIFI_H

#include <Arduino.h>

namespace Wifi {
  void wifi_setup();
  String get_ip();
}

#endif /* !WIFI_H */

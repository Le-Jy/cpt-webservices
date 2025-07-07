#ifndef NODEMCU_CLIENT_H
#define NODEMCU_CLIENT_H

#include <Arduino.h>

namespace NodeMCU_Client {
  void send_get(String url);
  void send_post(String url, String body);
}

#endif /* !NODEMCU_CLIENT_H */

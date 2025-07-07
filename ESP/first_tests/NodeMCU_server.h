#ifndef NODEMCU_SERVER_H
#define NODEMCU_SERVER_H

namespace NodeMCU_Server {
  void handle_lcd();
  void handle_led();
  void handle_hum_threshold();
  void handle_lum_threshold();
  void server_setup();
  void server_listen();
}

#endif /* !NODEMCU_SERVER_H */

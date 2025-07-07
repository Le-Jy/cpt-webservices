#include "MQTT_publisher.h"

#include <ESP8266WiFi.h>
#include <PubSubClient.h>

#include "config.h"
#include "wifi.h"
#include "hum_temp_sensor.h"
#include "light_sensor.h"
#include "button.h"


WiFiClient espClient;
PubSubClient client(espClient);

void MQTT_Publisher::setup() {
  client.setServer(BROKER_IP, 1883);
}

void onConnectionEstablished() {
  return;
}

void MQTT_Publisher::reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");

    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void MQTT_Publisher::loop() {
  if (!client.connected()) {
    reconnect();
  }

  client.loop();

  // You can now publish messages
}

void MQTT_Publisher::publish_temp(int id, float temp_value, String id_final) {

  String topic = String("esp12/" + Wifi::get_ip() + "/TEMPERATURE/" + id_final);
  String message = HumTempSensor::create_MQTT_temp(id, String(temp_value, 2));

  debug.printf("Message: %s\n", message);
  bool pub_res = client.publish(topic.c_str(), message.c_str());

  debug.printf("Published to %s, response code %d\n", topic, pub_res);
}

void MQTT_Publisher::publish_hum(int id, int hum_value, String id_final) {

  String topic = String("esp12/" + Wifi::get_ip() + "/HUMIDITY/" + id_final);
  String message = HumTempSensor::create_MQTT_hum(id, String(hum_value));
  bool pub_res = client.publish(topic.c_str(), message.c_str());

  debug.printf("Published to %s, response code %d\n", topic, pub_res);
}

void MQTT_Publisher::publish_lum(int id, int lum_value) {

  String topic = String("esp12/" + Wifi::get_ip() + "/SUNLIGHT/" + String(id, DEC));
  String message = Light_Sensor::create_dto(id, String(lum_value));
  bool pub_res = client.publish(topic.c_str(), message.c_str());

  debug.printf("Published to %s, response code %d\n", topic, pub_res);
}

void MQTT_Publisher::publish_button(int id, int button_value, String id_final) {

  String topic = String("esp12/" + Wifi::get_ip() + "/BUTTON/" + id_final);
  String message = Button::create_dto(id, String(button_value));
  bool pub_res = client.publish(topic.c_str(), message.c_str());

  debug.printf("Published to %s, response code %d\n", topic, pub_res);
}

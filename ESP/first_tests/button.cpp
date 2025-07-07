#include "button.h"

#include "config.h"
#include "led.h"
#include "component_dto.h"
#include "MQTT_publisher.h"

int button_state = 0;

void Button::button_setup() {
    pinMode(BUTTON_PIN, INPUT);
}

void Button::button_main() {
    button_state = digitalRead(BUTTON_PIN);

    if (button_state == HIGH) {
        // turn LED on:
#ifdef MANUAL
        Led::led_on(BUILTIN_LED);
        debug.println("button pressed");
#else
        MQTT_Publisher::publish_button(0, 1, Component_DTO::create_id(0, "Button"));
        debug.printf("Publishing button state to broker\n");
#endif
    }
    else {
        // turn LED off:
#ifdef MANUAL
        Led::led_off(BUILTIN_LED);
        debug.println("button released");
#else
        MQTT_Publisher::publish_button(0, 0, Component_DTO::create_id(0, "Button"));
#endif
    }
}

String Button::register_component(int id) {
    return Component_DTO::create(ComponentType::e_Button, id, "0", "Button");
}

String Button::create_dto(int id, String value) {
    return Component_DTO::create(ComponentType::e_Button, id, value, "Button");
}

String Button::create_MQTT(int id, String value) {
    return Component_DTO::create_MQTT_mess(ComponentType::e_Button, id, value, "Button");
}

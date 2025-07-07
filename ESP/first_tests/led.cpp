#include "led.h"

#include <ChainableLED.h>

#include "config.h"
#include "component_dto.h"


ChainableLED leds(LED_PIN1, LED_PIN2, NUM_LEDS);

void Led::led_setup()
{
    leds.init();
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);
}

void Led::led_set_status(int id, int status) {
    debug.printf("led_set_status: id=%d, status=%d\n", id, status);
    (status == 1) ? led_on(id) : led_off(id);
}

void Led::led_on(int id) {
    // Builtin LED
    if (id == LED_BUILTIN || id == 0) {
        digitalWrite(LED_BUILTIN, LOW);
    }
    else {
        leds.setColorRGB(0, 255, 255, 255);
    }

    debug.println("led on");
}

void Led::led_off(int id) {
    // Builtin LED
    if (id == LED_BUILTIN || id == 0) {
        digitalWrite(LED_BUILTIN, HIGH);
    }
    else {
        leds.setColorRGB(0, 0, 0, 0);
    }

    debug.println("led off");
}

String Led::register_component(int id) {
    return Component_DTO::create(ComponentType::e_Led, id, "0", "Led");
}

#if defined(ARDUINO_ARCH_AVR)
    #define debug  Serial

#elif defined(ARDUINO_ARCH_SAMD) ||  defined(ARDUINO_ARCH_SAM)
#ifdef SerialUSB
    #define debug  SerialUSB
#else
    #define debug  Serial
#endif
#else
    #define debug  Serial
#endif


#define BUTTON_PIN D8
#define LED_PIN    D0

int button_state = 0;

void button_setup() {
    pinMode(BUTTON_PIN, INPUT);
    pinMode(LED_BUILTIN, OUTPUT);
}

void button_main() {
    button_state = digitalRead(BUTTON_PIN);

    if (button_state == HIGH) {
        // turn LED on:
        debug.println("button pressed");
        digitalWrite(LED_BUILTIN, HIGH);
    }
    else {
        // turn LED off:
        debug.println("button released");
        digitalWrite(LED_BUILTIN, LOW);
    }
}
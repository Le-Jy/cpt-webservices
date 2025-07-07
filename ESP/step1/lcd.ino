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


#include <Wire.h>
#include "rgb_lcd.h"

#define COLS 2
#define ROWS 16

rgb_lcd lcd;

void lcd_setup() {
    lcd.begin(ROWS, COLS);
}

void lcd_display(String to_display, int col) {
    lcd.setCursor(0, col);
    debug.println(to_display);
    lcd.print(to_display);
    lcd.display();
}
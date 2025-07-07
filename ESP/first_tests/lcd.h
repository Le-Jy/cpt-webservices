#ifndef LCD_H
#define LCD_H

#include <Arduino.h>

namespace LCD {
    void lcd_setup();
    void lcd_display(String to_display, int col);
    String register_component(int id);
}

#endif /* !LCD_H */

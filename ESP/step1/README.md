# Step 1: How to use all devices

## Board architecture:
- ESP8266
- Carrier Board
- Grove - Temperature & Humidity Sensor (DHT11)
- Grove - Chainable RGB LED
- Grove - 16x2 LCD
- Grove - Light Sensor
- Grove - Button

## Work Done:

How the example system should behave:
- Change the color of the LED: red for high luminosity and green for normal luminosity (make sure to define a threshold).
- Display “High / Normal / Low Temperature” on the first line of the lcd based on the measured temperature value.
- Display “High / Normal / Low Humidity” on the second line of the lcd based on the measured humidity value.
- Manually turn the BUILTIN LED on/off using a button.

## How to use:
1. Install the required libraries in your Arduino IDE (see below).
2. Connect the devices to the carrier board.
3. Upload the code to your ESP8266 board.
4. Open the Serial Monitor to see the output from the temperature and humidity sensor.
5. Play with the sensors to see the full behaviour of the system.

### Useful docs:

- Temperature and humidity sensor: https://wiki.seeedstudio.com/GroveTemperatureAndHumidity_Sensor
- LED : https://wiki.seeedstudio.com/Grove-Chainable_RGB_LED/
- LCD : https://wiki.seeedstudio.com/Grove-16x2_LCD_Series
- Capteur de luminosité : veuillez télécharger la bibliothèque disponible sur moodle (TODO change)

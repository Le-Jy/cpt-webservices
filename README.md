# How I Run it ?
## Build based images
**For backend** (At root of homeManager):
- `docker build -t backend-service:latest .`
___
**For services** (At root of component-service):
- `docker build -t component-service:latest .`
___
**For the app** (At root of this directory):
- `docker compose up -d`

# Where do I send my requests ?

You only need to perform requests to backend, `http://localhost:8080/ssse/sensor`:

-`/create`: with DTO: 
```Java
public class ComponentDTO {
    private String id; //Must be ipaddress of the esp + componentname/nbcomponent (for example 192.126.1.1.0temperature1)
    private ComponentType type;
    private String value;
    private String timestamp;
}

public enum ComponentType {
    Humidity = 0,
    Sunlight,
    Button,
    Temperature,
    LCD,
    LED
}
```

- `/update/threshold` : with DTO:
```Java
public class ThresholdDTO {
    ComponentType componentType;
    Integer treshold;
}
```

The rest of the communications are by MQTT throught `tcp://localhost:1883`:
The backend listent on `esp12/+/+/+` where:
- `first +`: ip address of esp
- `second +`: sensork KEY {HUMIDITY, TEMPERATURE, SUNLIGHT, BUTTON}
- `third +`: sensor ID = esp ip address + sensorname/nbcomponent (for example 192.126.1.1.0temperature1)

ATTENTION, for the temperature and the sunlight sensors: the associated led id is led0 and for button it's led1 (fixed name). 
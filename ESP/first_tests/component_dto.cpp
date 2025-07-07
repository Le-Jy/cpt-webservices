#include "component_dto.h"

#include <ctime>

#include "config.h"
#include "wifi.h"

String Component_DTO::create(int type, int id, String value, String component_name)
{
    String ts_val = String(std::time(nullptr), DEC);

    String id_final = create_id(id, component_name);
    String type_val = String(type, DEC);

    return String("{\"id\":\"" + id_final + "\", \"type\":\"" + type_val + "\", \"value\":\"" + value + "\", \"timestamp\":\"" + ts_val + "\"}");
}

String Component_DTO::create_id(int id, String component_name) {
    String ip = Wifi::get_ip();

    return String(ip + component_name + id);
}

String Component_DTO::create_MQTT_mess(int type, int id, String value, String component_name)
{
    String ts_val = String(std::time(nullptr), DEC);

    String id_final = create_id(id, component_name);
    String type_val = String(type, DEC);

    return String("{\"id\":\"" + id_final + "\", \"type\":\"" + type_val + "\", \"value\":\"" + value + "\", \"timestamp\":\"" + ts_val + "\"}");
}


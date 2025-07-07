package epita.conception.homeManager.service;

import epita.conception.homeManager.DTO.ComponentDTO;
import epita.conception.homeManager.repository.ComponentRepository;
import epita.conception.homeManager.repository.entity.ComponentEntity;
import epita.conception.homeManager.utils.Mapper;
import epita.conception.homeManager.utils.ComponentConf;
import epita.conception.homeManager.utils.ComponentType;
import epita.conception.homeManager.utils.ComponentValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.rmi.AlreadyBoundException;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ComponentService {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentService.class);

    private final ComponentRepository componentRepository;
    private final RestTemplate    restTemplate = new RestTemplate();
    private final Environment     env;

    public void updateThreshold(ComponentType ComponentType, Integer newThreshold) {
        ComponentConf conf = confMap.get(ComponentType);
        if (conf == null) {
            LOG.error("No config found for sensor type: {}", ComponentType);
            throw new IllegalArgumentException("Invalid sensor type: " + ComponentType);
        }
        conf.setThreshold(newThreshold);
        LOG.info("Threshold for {} updated to {}", ComponentType, newThreshold);
    }

    /* ------------  configuration par type de capteur ------------- */

    private final Map<ComponentType, ComponentConf> confMap = new EnumMap<>(Map.of(
            ComponentType.e_Sunlight, new ComponentConf(
                    "sunlight.service.url",
                    1000,
                    "/led/0/0", "",
                    "/led/0/1", ""),
            ComponentType.e_Humidity, new ComponentConf(
                    "humidity.service.url",
                    40,
                    "/lcd/0/0",  "Hum. high",
                    "/lcd/0/0",  "Hum. normal"),
            ComponentType.e_Temperature, new ComponentConf(
                    "temperature.service.url",
                    25,
                    "/lcd/0/1",  "Temp. high",
                    "/lcd/0/1",  "Temp. low")
    ));

    @Autowired
    public ComponentService(ComponentRepository repo, Environment env) {
        this.componentRepository = repo;
        this.env              = env;
    }

    /* ==============================================================
       ===============  MÉTHODES PUBLIQUES ===========================
       ============================================================== */

    public void handleSunlight(String mac, String id, ComponentDTO payload) {
        int result = handleSensor(ComponentType.e_Sunlight, mac, id, payload);
        if (result == 1) {
            callUpdateEndpoint(payload, "led.service.url", mac + "Led0", mac);
            actuate(mac, "/led/0/1", "");
        } else if (result == 2) {
            actuate(mac, "/led/0/0", "");
        }
    }

    public void handleHumidity(String mac, String id, ComponentDTO payload) {
        int result = handleSensor(ComponentType.e_Humidity, mac, id, payload);
        String lcdValue = "";
        if (result== 1) {
            lcdValue = "Hum. high";
            callUpdateEndpoint(payload, "humidity.service.url", mac + "Lcd0", mac);
        } else if (result == 2) {
            lcdValue = "Hum. normal";
        }
        if (result != -1) {
            actuate(mac, "/lcd/0/0", lcdValue);
            ComponentDTO lcdDTO = new ComponentDTO(mac + "Lcd0", ComponentType.e_LCD,
                    lcdValue, payload.getTimestamp());
            callUpdateEndpoint(lcdDTO, "lcd.service.url", mac + "Lcd0", mac);
        }


    }

    public void handleTemperature(String mac, String id, ComponentDTO payload) {
        int result = handleSensor(ComponentType.e_Temperature, mac, id, payload);
        String lcdValue = "";
        if (result== 1) {
            callUpdateEndpoint(payload, "temperature.service.url", mac + "Lcd0", mac);
            lcdValue = "Temp. high";
        } else if (result == 2) {
            lcdValue = "Temp. normal";
        }
        if (result != -1) {
            actuate(mac, "/lcd/0/1", lcdValue);
            ComponentDTO lcdDTO = new ComponentDTO(mac + "Lcd0", ComponentType.e_LCD,
                    lcdValue, payload.getTimestamp());
            callUpdateEndpoint(lcdDTO, "lcd.service.url", mac + "Lcd0", mac);
        }

    }

    public void handleButton(String mac, String id, ComponentDTO payload) {
        if (validateInput(id, payload, ComponentType.e_Button)) {
            //   ComponentDTO buttonDTO = new ComponentDTO(id, ComponentType.e_Button,
            //         payload.getValue(), payload.getTimestamp());
            LOG.info("Handling button press for ID: {}", id);
            callUpdateEndpoint(payload, "button.service.url", id, mac);
            ComponentDTO ledDTO = new ComponentDTO(mac + "Led1", ComponentType.e_LED,
                    payload.getValue(), payload.getTimestamp());
            callUpdateEndpoint(ledDTO, "led.service.url", mac + "Led1", mac);
            actuate(mac, "/led/1/"+payload.getValue(),  "");
        }
    }

    public void createSensor(ComponentDTO componentDTO) throws AlreadyBoundException, HttpServerErrorException.InternalServerError {
        LOG.info("Creating a new sensor with data: {}", componentDTO);

        ComponentEntity componentEntity = Mapper.mapToSensorEntity(componentDTO);

        if (componentRepository.existsById(componentEntity.getId())) {
            LOG.error("Sensor with ID {} already exists", componentEntity.getId());
            throw new AlreadyBoundException("Sensor with ID " + componentEntity.getId() + " already exists");
        }

        // Construction de l'URL du microservice en fonction du type
        String componentServiceUrl = switch (componentDTO.getType()) {
            case e_Humidity -> env.getProperty("humidity.service.url") + "/create";
            case e_Sunlight -> env.getProperty("sunlight.service.url") + "/create";
            case e_Button -> env.getProperty("button.service.url") + "/create";
            case e_Temperature -> env.getProperty("temperature.service.url") + "/create";
            case e_LCD -> env.getProperty("lcd.service.url") + "/create";
            case e_LED -> env.getProperty("led.service.url") + "/create";
            default -> {
                LOG.error("Unknown sensor type: {}", componentDTO.getType());
                throw new IllegalArgumentException("Unknown sensor type: " + componentDTO.getType());
            }
        };
        LOG.info(componentServiceUrl);

        ResponseEntity<ComponentDTO> result = restTemplate.postForEntity(
                componentServiceUrl,
                componentDTO,
                ComponentDTO.class
        );

        if (!result.getStatusCode().is2xxSuccessful()) {
            LOG.error("Failed to create sensor: {}", result.getStatusCode());
            throw new InternalError("Failed to create sensor: " + result.getStatusCode());
        }

        componentRepository.save(componentEntity);
        LOG.info("Sensor created successfully with ID: {}", componentEntity.getId());
    }

    /* ==============================================================
       ===============  MÉTHODES PRIVÉES  ============================
       ============================================================== */

    private int handleSensor(ComponentType type, String mac,
                             String sensorId, ComponentDTO payload) {

        int result = -1;
        if (!validateInput(sensorId, payload, type)) return result;

        ComponentDTO dto  = new ComponentDTO(sensorId, type,
                payload.getValue(), payload.getTimestamp());
        ComponentConf cfg = confMap.get(type);

        ResponseEntity<String> resp = callUpdateEndpoint(dto, cfg.getUpdateUrlProp(), sensorId, mac);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null)
        {
            LOG.error("Failed to update sensor: {}, body: {}", resp.getStatusCode(), resp.getBody());
            return result;
        }

        if (type == ComponentType.e_Temperature) {
            float previous = Float.parseFloat(resp.getBody());
            float thresh   = (float) cfg.getThreshold();
            float current  = Float.parseFloat(payload.getValue());
            LOG.info("previous: {},  value: {}, threshold: {}", previous, current, thresh);

            if (current > thresh && (previous < current || previous == 0)) {  // franchissement ↑
                actuate(mac, cfg.getAboveMsgEndpoint(), cfg.getAboveMsgBody());
                LOG.info("{} value {} exceeds threshold {}", type, current, thresh);
                result = 1;
            } else if (current <= thresh && (previous > current || previous == 0)) {  // franchissement ↓
                actuate(mac, cfg.getBelowMsgEndpoint(), cfg.getBelowMsgBody());
                LOG.info("{} value {} under threshold {}", type, current, thresh);
                result = 2;
            }
        }
        else {
            int previous = Integer.parseInt(resp.getBody());
            int thresh   = cfg.getThreshold();
            int current  = Integer.parseInt(payload.getValue());
            LOG.info("previous: {},  value: {}, threshold: {}", previous, current, thresh);

            if (current > thresh && (previous < current || previous == 0)) {  // franchissement ↑
                actuate(mac, cfg.getAboveMsgEndpoint(), cfg.getAboveMsgBody());
                LOG.info("{} value {} exceeds threshold {}", type, current, thresh);
                result = 1;
            } else if (current <= thresh && (previous > current || previous == 0)) {  // franchissement ↓
                actuate(mac, cfg.getBelowMsgEndpoint(), cfg.getBelowMsgBody());
                LOG.info("{} value {} under threshold {}", type, current, thresh);
                result = 2;
            }
        }

        return result;
    }

    /* --------- helpers ------------------------------------------------ */

    private boolean validateInput(String id, ComponentDTO payload, ComponentType t) {
        if (payload == null) {
            LOG.error("Empty payload for {} sensor ID {}", t, id);
            return false;
        }
        if (!componentRepository.existsById(id)) {
            LOG.error("Sensor ID {} does not exist", id);
            return false;
        }
        return true;
    }

    private HttpEntity<ComponentDTO> buildRequest(ComponentDTO dto) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(dto, h);
    }

    private ResponseEntity<String> callUpdateEndpoint(
            ComponentDTO dto, String urlProp, String id, String mac) {

        String url = env.getProperty(urlProp) + "/update";
        return restTemplate.exchange(url, HttpMethod.PUT,
                buildRequest(dto), String.class);
    }

    private void actuate(String mac, String endpoint, String body) {
        restTemplate.postForEntity("http://" + mac + endpoint,
                body.isEmpty() ? null : body, Void.class);
    }
}
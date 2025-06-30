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
            ComponentType.Sunlight, new ComponentConf(
                    "sunlight-sensor.service.url",
                    1000,
                    "/led/0/0", "",
                    "/led/0/1", ""),
            ComponentType.Humidity, new ComponentConf(
                    "humidity-sensor.service.url",
                    40,
                    "/lcd/0/0",  "valeur normale, VMC : faible vitesse",
                    "/lcd/0/0",  "valeur élevée, VMC : forte vitesse"),
            ComponentType.Temperature, new ComponentConf(
                    "temperature-sensor.service.url",
                    25,
                    "/lcd/0/1",  "température normale, CLIM : off",
                    "/lcd/0/1",  "température élevée, CLIM : on")
    ));

    @Autowired
    public ComponentService(ComponentRepository repo, Environment env) {
        this.componentRepository = repo;
        this.env              = env;
    }

    /* ==============================================================
       ===============  MÉTHODES PUBLIQUES ===========================
       ============================================================== */

    public void handleSunlight(String mac, String id, ComponentValue payload) {
        if (handleSensor(ComponentType.Sunlight, mac, id, payload) == 1) {
            // Si le seuil est dépassé, on allume la LED
            ComponentDTO ledDTO = new ComponentDTO(mac + "led0", ComponentType.LED,
                    payload.getValue(), payload.getTimestamp());
            callUpdateEndpoint(ledDTO, "led0.service.url", mac + "led0", mac);
            actuate(mac, "/led/0/1", "");
        } else if (handleSensor(ComponentType.Sunlight, mac, id, payload) == 2) {
            // Si le seuil n'est plus dépassé, on éteint la LED
            actuate(mac, "/led/0/0", "");
        }
    }

    public void handleHumidity(String mac, String id, ComponentValue payload) {
        if (handleSensor(ComponentType.Humidity, mac, id, payload) == 1) {
            // Si le seuil est dépassé, on allume la VMC
            ComponentDTO lcdDTO = new ComponentDTO(mac + "lcd0", ComponentType.LCD,
                    payload.getValue(), payload.getTimestamp());
            callUpdateEndpoint(lcdDTO, "lcd.service.url", mac + "lcd0", mac);
            actuate(mac, "/lcd/0/0", "valeur élevée, VMC : forte vitesse");
        } else if (handleSensor(ComponentType.Humidity, mac, id, payload) == 2) {
            // Si le seuil n'est plus dépassé, on éteint la VMC
            actuate(mac, "/lcd/0/0", "valeur normale, VMC : faible vitesse");
        }
    }

    public void handleTemperature(String mac, String id, ComponentValue payload) {
        if (handleSensor(ComponentType.Temperature, mac, id, payload) == 1) {
            // Si le seuil est dépassé, on allume la climatisation
            ComponentDTO lcdDTO = new ComponentDTO(mac + "lcd0", ComponentType.LCD,
                    payload.getValue(), payload.getTimestamp());
            callUpdateEndpoint(lcdDTO, "lcd.service.url", mac + "lcd0", mac);
            actuate(mac, "/lcd/0/1", "température élevée, CLIM : on");
        } else if (handleSensor(ComponentType.Temperature, mac, id, payload) == 2) {
            // Si le seuil n'est plus dépassé, on éteint la climatisation
            actuate(mac, "/lcd/0/1", "température normale, CLIM : off");
        }
    }

    public void handleButton(String mac, String id, ComponentValue payload) {
        if (validateInput(id, payload, ComponentType.Button)) {
            ComponentDTO buttonDTO = new ComponentDTO(id, ComponentType.Button,
                    payload.getValue(), payload.getTimestamp());
            LOG.info("Handling button press for ID: {}", id);
            callUpdateEndpoint(buttonDTO, "button.service.url", id, mac);
            ComponentDTO ledDTO = new ComponentDTO(mac + "led1", ComponentType.LED,
                    payload.getValue(), payload.getTimestamp());
            callUpdateEndpoint(ledDTO, "led.service.url", mac + "led1", mac);
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
            case Humidity -> env.getProperty("humidity-sensor.service.url") + "/create";
            case Sunlight -> env.getProperty("sunlight-sensor.service.url") + "/create";
            case Button -> env.getProperty("button.service.url") + "/create";
            case Temperature -> env.getProperty("temperature-sensor.service.url") + "/create";
            case LCD -> env.getProperty("lcd.service.url") + "/create";
            case LED -> env.getProperty("led.service.url") + "/create";
            default -> {
                LOG.error("Unknown sensor type: {}", componentDTO.getType());
                throw new IllegalArgumentException("Unknown sensor type: " + componentDTO.getType());
            }
        };

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
                              String sensorId, ComponentValue payload) {

        int result = -1;
        if (!validateInput(sensorId, payload, type)) return result;

        ComponentDTO dto  = new ComponentDTO(sensorId, type,
                payload.getValue(), payload.getTimestamp());
        ComponentConf cfg = confMap.get(type);

        ResponseEntity<String> resp = callUpdateEndpoint(dto, cfg.getUpdateUrlProp(), sensorId, mac);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return result;

        int previous = Integer.parseInt(resp.getBody());
        int thresh   = cfg.getThreshold();
        int current  = Integer.parseInt(payload.getValue());

        if (current > thresh && previous <= thresh) {
            actuate(mac, cfg.getAboveMsgEndpoint(), cfg.getAboveMsgBody());
            LOG.warn("{} value {} exceeds threshold {}", type, current, thresh);
            result = 1;
        } else if (current <= thresh && previous > thresh) {  // franchissement ↓
            actuate(mac, cfg.getBelowMsgEndpoint(), cfg.getBelowMsgBody());
            LOG.warn("{} value {} under threshold {}", type, current, thresh);
            result = 2;
        }
        return result;
    }

    /* --------- helpers ------------------------------------------------ */

    private boolean validateInput(String id, ComponentValue payload, ComponentType t) {
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

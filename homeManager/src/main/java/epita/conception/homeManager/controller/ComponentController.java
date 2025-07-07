package epita.conception.homeManager.controller;

import epita.conception.homeManager.DTO.ComponentDTO;
import epita.conception.homeManager.DTO.ThresholdDTO;
import epita.conception.homeManager.service.ComponentService;
import epita.conception.homeManager.utils.ComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.AlreadyBoundException;

@RestController
@RequestMapping("/ssse/sensor")
public class ComponentController {
    private static final Logger logger =
            LoggerFactory.getLogger(ComponentController.class);
    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @PostMapping("/test")
    public ResponseEntity<Void> testSensor(@RequestBody String componentDTO) {
        // Logic to test a sensor
        logger.info("Testing sensor with data: {}", componentDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createSensor(@RequestBody ComponentDTO componentDTO) {
        // Logic to create a sensor
        logger.info("Creating a new sensor");
        if (componentDTO == null || componentDTO.getType() == null) {
            logger.error("Invalid sensor data received: {}", componentDTO);
            return ResponseEntity.badRequest().build();
        }
        try {
            componentService.createSensor(componentDTO);
        } catch (AlreadyBoundException e) {
            return ResponseEntity.noContent().build();
        }
        catch (InternalError e) {
            logger.error("Internal server error while creating sensor: {}", componentDTO);
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/threshold")
    public ResponseEntity<Void> updateThreshold(@RequestBody ThresholdDTO thresholdDTO) {
        ComponentType sensorType = thresholdDTO.getComponentType();
        Integer newThreshold = thresholdDTO.getTreshold();

        componentService.updateThreshold(sensorType, newThreshold);
        logger.info("Updated threshold for sensor type {} to {}", sensorType, newThreshold);
        return ResponseEntity.ok().build();
    }
}
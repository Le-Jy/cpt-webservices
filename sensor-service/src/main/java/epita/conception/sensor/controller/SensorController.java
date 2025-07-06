package epita.conception.sensor.controller;

import epita.conception.sensor.DTO.SensorDTO;
import epita.conception.sensor.service.SensorService;
import epita.conception.sensor.utils.SensorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;

@RestController
@RequestMapping("/ssse/sensor")
public class SensorController {
    private static final Logger logger =
            LoggerFactory.getLogger(SensorController.class);
    private final SensorService sensorService;
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createSensor(@RequestBody SensorDTO sensorDTO) {
        // Logic to create a sensor
        logger.info("Creating a new sensor");
        if (sensorDTO == null || sensorDTO.getType() == null) {
            logger.error("Invalid sensor data received: {}", sensorDTO);
            return ResponseEntity.badRequest().build();
        }
        try {
            sensorService.createSensor(sensorDTO);
        } catch (AlreadyBoundException e) {
            return ResponseEntity.noContent().build();
        }
        catch (InternalError e) {
            logger.error("Internal server error while creating sensor: {}", sensorDTO);
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateValue(@RequestBody SensorDTO sensorDTO) {
        logger.info("Updating a new value");
        try {
            String modified = sensorService.updateSensor(sensorDTO);
            logger.info("Sensor last Value: {}", modified);
            return modified != null && modified.equals("no change")
                    ?  ResponseEntity.status(HttpStatus.NOT_MODIFIED).build()
                    :ResponseEntity.ok(modified);
        } catch (ChangeSetPersister.NotFoundException e) {
            logger.error("Error while updating sensor: {} NOT FOUND", sensorDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/allValues/{id}")
    ResponseEntity<ArrayList<SensorValue>> getAllValues(@PathVariable String id) {
        logger.info("Retrieving all values for a given id");
        ArrayList<SensorValue> values;
        try {
            values = sensorService.getSensorValues(id);
        }
        catch (InternalError e) {
            logger.error("Internal Server error while retrieving values");
            return ResponseEntity.status(500).build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(values);
    }

    @GetMapping("/lastValue/{id}")
    ResponseEntity<String> getLastValue(@PathVariable String id) {
        logger.info("Retrieving last value for a given id");

        String value;
        try {
            value = sensorService.getLastValue(id);
        }
        catch (InternalError e) {
            logger.error("Internal Server error while retrieving values");
            return ResponseEntity.status(500).build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }
}

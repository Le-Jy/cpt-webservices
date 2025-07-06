package epita.conception.sensor.service;

import epita.conception.sensor.DTO.SensorDTO;
import epita.conception.sensor.repository.SensorRepository;
import epita.conception.sensor.repository.entity.SensorEntity;
import epita.conception.sensor.utils.Mapper;
import epita.conception.sensor.utils.SensorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;

@Service
public class SensorService {
    private static final Logger logger = LoggerFactory.getLogger(SensorService.class);

    private final SensorRepository sensorRepository;


    @Autowired
    private Environment env;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public void createSensor(SensorDTO sensorDTO) throws AlreadyBoundException{
        logger.info("Creating a new sensor with data: {}", sensorDTO);

        SensorEntity sensorEntity = Mapper.mapToSensorEntity(sensorDTO);

        if (sensorRepository.existsById(sensorEntity.getId())) {
            logger.info("Sensor with ID {} already exists", sensorEntity.getId());
            throw new AlreadyBoundException("Sensor with ID " + sensorEntity.getId() + " already exists");
        }

        sensorRepository.save(sensorEntity);
        logger.info("Sensor created successfully with ID: {}", sensorEntity.getId());
    }

    public String updateSensor(SensorDTO sensorDTO) throws ChangeSetPersister.NotFoundException {
        logger.info("Updating a sensor with data: {}", sensorDTO);

        SensorEntity sensorEntity = sensorRepository.findById(sensorDTO.getId())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        String lastValue = sensorEntity.getLastValue(); // Should return null if none
        String newValue = sensorDTO.getValue();

        if (lastValue != null && lastValue.equals(newValue)) {
            logger.info("Sensor value unchanged: {}", newValue);
            return "no change";
        }

        SensorValue value = new SensorValue(sensorDTO.getDate(), newValue);
        sensorEntity.addValue(value);

        sensorRepository.save(sensorEntity);
        logger.info("Sensor value updated to: {}, sending back lastValue: {}", newValue, lastValue);
        return lastValue;
    }


    public ArrayList<SensorValue> getSensorValues(String id) throws ChangeSetPersister.NotFoundException {
        logger.info("Getting sensor values for sensor with ID {}", id);
        SensorEntity sensorEntity = sensorRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return sensorEntity.getValuesHistoric();
    }

    public String getLastValue(String id) throws ChangeSetPersister.NotFoundException {
        logger.info("Getting sensor last value for sensor with ID {}", id);
        SensorEntity sensorEntity = sensorRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return sensorEntity.getLastValue();
    }
}

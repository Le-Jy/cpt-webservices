package epita.conception.sensor.utils;

import epita.conception.sensor.DTO.SensorDTO;
import epita.conception.sensor.repository.entity.SensorEntity;

public class Mapper {
    public static SensorDTO mapToSensorDTO(SensorEntity sensorEntity) {
        if (sensorEntity == null) {
            return null;
        }

        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setId(sensorEntity.getId());
        sensorDTO.setValue("");

        return sensorDTO;
    }

    public static SensorEntity mapToSensorEntity(SensorDTO sensorDTO) {
        if (sensorDTO == null) {
            return null;
        }

        SensorEntity sensorEntity = new SensorEntity();
        sensorEntity.setId(sensorDTO.getId());

        return sensorEntity;
    }
}

package epita.conception.homeManager.utils;

import epita.conception.homeManager.DTO.ComponentDTO;
import epita.conception.homeManager.repository.entity.ComponentEntity;

public class Mapper {
    public static ComponentDTO mapToSensorDTO(ComponentEntity componentEntity) {
        if (componentEntity == null) {
            return null;
        }

        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setId(componentEntity.getId());
        componentDTO.setType(componentEntity.getComponentType());
        componentDTO.setValue("");

        return componentDTO;
    }

    public static ComponentEntity mapToSensorEntity(ComponentDTO componentDTO) {
        if (componentDTO == null) {
            return null;
        }

        ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setId(componentDTO.getId());
        componentEntity.setComponentType(componentDTO.getType());

        return componentEntity;
    }

    public static ComponentDTO convertJsonToSensorDTO(String payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }

        String[] parts = payload.split(",");
        if (parts.length < 3) {
            return null; // Invalid payload format
        }

        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setId(parts[0].trim());
        componentDTO.setType(ComponentType.valueOf(parts[1].trim().toUpperCase()));
        componentDTO.setValue(parts[2].trim());

        return componentDTO;
    }
}

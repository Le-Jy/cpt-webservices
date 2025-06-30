package epita.conception.sensor.repository;

import epita.conception.sensor.repository.entity.SensorEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends MongoRepository<SensorEntity, String> {
}

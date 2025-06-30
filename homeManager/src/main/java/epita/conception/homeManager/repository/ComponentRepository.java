package epita.conception.homeManager.repository;

import epita.conception.homeManager.repository.entity.ComponentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends MongoRepository<ComponentEntity, String> {
}

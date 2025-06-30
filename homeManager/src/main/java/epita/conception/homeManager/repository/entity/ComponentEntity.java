package epita.conception.homeManager.repository.entity;

import epita.conception.homeManager.utils.ComponentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sensors")
public class ComponentEntity {
    @Id
    String id;
    @NotNull(message = "Sensor type cannot be null")
    ComponentType componentType;
}

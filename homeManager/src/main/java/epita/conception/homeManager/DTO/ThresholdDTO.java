package epita.conception.homeManager.DTO;

import epita.conception.homeManager.utils.ComponentType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ThresholdDTO {
    ComponentType componentType;
    Integer treshold;
}

package epita.conception.sensor.DTO;
import epita.conception.sensor.utils.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorDTO {
    private String id;
    private ComponentType type;
    private String value;
    private String date;
}

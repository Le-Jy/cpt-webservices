package epita.conception.homeManager.DTO;
import epita.conception.homeManager.utils.ComponentType;
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
public class ComponentDTO {
    private String id;
    private ComponentType type;
    private String value;
    private String timestamp;
}

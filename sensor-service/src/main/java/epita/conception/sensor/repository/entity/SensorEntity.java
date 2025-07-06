package epita.conception.sensor.repository.entity;

import epita.conception.sensor.utils.SensorValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sensors")
public class SensorEntity {
    @Id
    String id;
    ArrayList<SensorValue> valuesHistoric = new ArrayList<>();
    String lastValue;

    public void addValue(SensorValue value) {
        this.valuesHistoric.add(value);
        lastValue = value.getValue();
    }

    public String getLastValue() {
        if (valuesHistoric == null || valuesHistoric.isEmpty()) {
            return "0";
        }
        return valuesHistoric.get(valuesHistoric.size() - 1).getValue();
    }
}

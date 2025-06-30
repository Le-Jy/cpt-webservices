package epita.conception.homeManager.utils;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ComponentValue {
    private String timestamp;
    private String value;
}

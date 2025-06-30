package epita.conception.homeManager.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComponentConf {
    private final String updateUrlProp;
    // Setter pour le threshold uniquement
    @Setter
    private int threshold;
    private final String aboveMsgEndpoint;
    private final String aboveMsgBody;
    private final String belowMsgEndpoint;
    private final String belowMsgBody;

    public ComponentConf(String updateUrlProp, int threshold,
                         String aboveMsgEndpoint, String aboveMsgBody,
                         String belowMsgEndpoint, String belowMsgBody) {
        this.updateUrlProp = updateUrlProp;
        this.threshold = threshold;
        this.aboveMsgEndpoint = aboveMsgEndpoint;
        this.aboveMsgBody = aboveMsgBody;
        this.belowMsgEndpoint = belowMsgEndpoint;
        this.belowMsgBody = belowMsgBody;
    }
}


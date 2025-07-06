package epita.conception.homeManager.utils;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import epita.conception.homeManager.DTO.ComponentDTO;
import epita.conception.homeManager.service.ComponentService;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.LoggerFactory; // <-- Ajoute ceci
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Autowired
    Environment env;
    private final Logger logger = (Logger) LoggerFactory.getLogger(MqttConfig.class);

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        String brokerUrl = env.getProperty("mqtt.broker.url");
        logger.info("MQTT: Using broker URL: {}", brokerUrl);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setAutomaticReconnect(true);
        return options;
    }

    @Bean
    public ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager() {
        logger.info("MQTT: Creating client manager...");
        return new Mqttv3ClientManager(mqttConnectOptions(), "backend-client");
    }

    @Bean
    public MessageProducer inbound() {
        logger.info("MQTT: Setting up inbound adapter and subscribing to topic esp12/+/+/+");
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientManager(), "backend-listener");
        logger.info("MQTT: Configuring adapter for topic esp12/+/+/+");
        adapter.addTopic("esp12/+/+/+"); // Wildcard pour tous les capteurs
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());

        logger.info("MQTT: Adapter configured, waiting for messages...");
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(ComponentService componentService,
                                  ObjectMapper mapper) {
        return message -> {
            String topic   = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = (String) message.getPayload();

            logger.info("MQTT: Message received on topic '{}': {}", topic, payload);

            // esp12/{mac}/{sensorType}/{id}
            String[] parts   = topic.split("/");
            String mac       = parts[1];
            String sensorKey = parts[2];
            String sensorId  = parts[3];

            ComponentDTO value;
            try {
                value = mapper.readValue(payload, ComponentDTO.class);
            } catch (IOException e) {
                logger.error("MQTT: Invalid payload for topic {} : {}", topic, payload, e);
                return;
            }

            try {
            switch (sensorKey) {
                case "SUNLIGHT"  -> {
                    logger.info("MQTT: Handling SUNLIGHT for mac={} id={}", mac, sensorId);
                    componentService.handleSunlight(mac, sensorId, value);
                }
                case "HUMIDITY"  -> {
                    logger.info("MQTT: Handling HUMIDITY for mac={} id={}", mac, sensorId);
                    componentService.handleHumidity(mac, sensorId, value);
                }
                case "BUTTON"    -> {
                    logger.info("MQTT: Handling BUTTON for mac={} id={}", mac, sensorId);
                    componentService.handleButton(mac, sensorId, value);
                }
                case "TEMPERATURE" -> {
                    logger.info("MQTT: Handling TEMPERATURE for mac={} id={}", mac, sensorId);
                    componentService.handleTemperature(mac, sensorId, value);
                }
                default -> logger.warn("MQTT: Unknown sensor type: {}", sensorKey);
            }
            }catch (Exception e) {
                logger.error("MQTT: Error handling message for topic {}: {}", topic, payload, e);
            }
        };
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
}

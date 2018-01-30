# Broker Agent
The underlying framework we build upon is JADE. JADE came with a Topic management service. The only problem with this in comparison to for example MQTT which also conists of topic functionality is that it didn't persist message for us.

We created the Broker Agent which can be turned on or off by using the config.properties. This Agent will start listening to a certain topic when it receives an ACLMessage with the performative SUBSCRIBE.
It will then create a buffer in memory for that topic and every time a subscriber asks for a message it will give the oldest message from his buffer.
This way no messages will get lost in the process. 

The Broker Agent also persists the message from memory to disk if this is correctly setup in the config.properties.

```properties
# Broker Agent parameters
brokeragent.service_name=BROKER-AGENT
brokeragent.store_sensor_agents_on_disk=true
brokeragent.storage_basepath=C:/BrokerAgent/
brokeragent.storage_filename=topics
brokeragent.store_interval_in_ms=60000
brokeragent.logger_handler=false
```

Next to the config.properties the agent-stack.properties should also include the Broker Agent.

```properties
agents=broker-agent:com.mlaf.hu.casestudy.brokeragent.BrokerAgent;decision-agent:com.mlaf.hu.casestudy.decisionagent.DecisionAgent;sensor-agent:com.mlaf.hu.casestudy.sensoragent.SensorAgent;
services=jade.core.event.NotificationService;jade.core.messaging.TopicManagementService
port=1600
host=localhost
main=false
no-display=true
```

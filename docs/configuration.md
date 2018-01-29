# Configuration
The MLAF framework is highly configurable, through the use of a config.properties file. This configuration file enables you to set 
certain default messages and paths, enable or disable features, set refresh rates and so on. Note that certain agents will not function
properly or not function at all when not configured through the properties file. 

## Quickstart
To configure the framework, create a config.properties file in the root directory of your project. If no config.properties is found on startup,
a default configuration will be used. Note that properties are validated in runtime, so properties of unused agents don't need to be configured 
or even exist. For example: if you're not making use of SlackAgent, the property ```slack.webhook_url``` will never be called.
An example of config.properties is shown in the section below, so from here on out just mix and match the properties needed for your framework implementation.

## Example config.properties
```
# proxy configuration (refresh_rate value = time in seconds. Default = 60)
proxy.port=6789
proxy.mdns.refresh_rate=60

# smtp mail server configuration
mail.host=smtp.gmail.com
mail.port=587
mail.from=msda.hogeschoolutrecht@gmail.com
mail.password=minorsda2018
mail.default_subject=Multi Agent Life Cycle Framework Notification
mailagent.logger_handler=false

# slack configuration (make sure that slackbot is a member of the selected channel)
slack.webhook_url=https://hooks.slack.com/services/your/web/hook
slack.message_title=MLAF Custom Message Title
slack.logger_handler=false

# configuration for external TCP monitoring server
monitoring.cache_size=200
monitoring.server.ip=localhost
monitoring.server.port=1738
monitoring.server.message_delimiter=

# Decision Agent parameters
decisionagent.service_name=DECISION-AGENT
decisionagent.store_sensor_agents_on_disk=true
decisionagent.storage_basepath=C:/DecisionAgent/
decisionagent.storage_filename=sensoragents
decisionagent.store_interval_in_ms=60000
decisionagent.logger_handler=false

# InstructionSet parameters
instructionset.num_readings_in_memory=100

# Broker Agent parameters
brokeragent.service_name=BROKER-AGENT
brokeragent.store_sensor_agents_on_disk=true
brokeragent.storage_basepath=C:/BrokerAgent/
brokeragent.storage_filename=topics
brokeragent.store_interval_in_ms=60000
brokeragent.logger_handler=false

# Sensor Agent parameters
sensoragent.logger_handler=false
```
## Custom properties
The framework makes use of a singleton class called Configuration that manages runtime configuration values. This makes it possible to add your own configuration values and use them in implementation. Say for example I would add ```my.custom_property=I am custom``` to the bottom of my properties file. The code snipplet below shows how to read the property in runtime.

```
import com.mlaf.hu.helpers.Configuration;
...
Configuration config = Configuration.getInstance();
String myCustomProperty = config.getProperty("my.custom_property");
```

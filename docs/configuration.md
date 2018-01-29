# Configuration
The MLAF framework is highly configurable, through the use of a config.properties file. This configuration file enables you to set 
certain default messages and paths, enable or disable features, set refresh rates and so on. Note that certain agents will not function
properly or not function at all when not configured through the properties file. 

## Quickstart
To configure the framework, create a config.properties file in the root directory of your project. If no config.properties is found on startup,
a default configuration will be used. Note that properties are validated in runtime, so properties of unused agents don't need to be configured 
or even exist. For example: if you're not making use of SlackAgent, the property ```slack.webhook_url``` will never be called.
All available properties are explained in the section below, so from here on out just mix and match the properties needed for your framework implementation.

## Available properties


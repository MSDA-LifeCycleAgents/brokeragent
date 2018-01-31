# Logger Agent
An Agent that combines with the LoggerAgentHandler to consolidate all logs from every agent to one central place.

The Agent logs all the messages to the console for now, but can easily be inherited into another agent to add your own handler for your own purpose.

Adding the LoggerAgentHandler is as easy as adding any handler:
```java
YOUR_LOGGER.addHandler(new LoggerAgentLogHandler(this, 60));
```
Where the first parameter is your agent object and the second one is the amount of time in seconds the handler is allowed to take before sending the log message.

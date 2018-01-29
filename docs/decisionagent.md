#Decision Agent
Used as it's name already described this Agent is the center of the framework and decides based on so called Instruction Sets if a described plan should be executed.

## Quickstart
1. Create your own class and let it extend the DecisionAgent class. This will force you to implement three methods:
- unregisterSensorAgentCallback(AID sensoragent) which enables you to execute code upon the unregistering of a Sensor Agent.
Unregistering happens whenever a Sensor Agent lacks responsiveness to the Decision Agent.
- storeReading(double value, InstructionSet is, Sensor sensor, String measurementId) enables you to use any sort of persistence you would like to keep track of the information sent by the Sensor Agents.
The Decision Agent does store every reading (with a circulating supply of 100) in a HashMap in memory. If enabled in the config.properties the Decision Agent also stores the HashMap on disk.
- executePlanCallback(Plan plan) enables you to execute code whenever a Plan is executed by the Decision Agent. How plans work exactly is specified in (TODO ADD REFERENCE).

These 3 methods should be implemented, but do not need to contain any working code as the Decision Agent can run without any line of code written.
You are however free to override any method you would like. Be aware though that overriding certain methods can cause the framework to malfunction.
# Sensor Agent and Sensors
Used to be attached to a (Smart) device to be able to read out properties and the status of it via Sensors.
This is the Java version of the Sensor Agent, but we also have a [C++ version](https://github.com/MSDA-LifeCycleAgents/mlaf-arduino)  to make it more compliant to real world scenarios where it's more 
likely that microcontrollers will be used.

## Quickstart
1. Create a Sensor Agent implementation class and let it extend the abstract Sensor Agent class. This way you well get 2 methods that need to be implemented:
- getInstructionXML() which enables you to execute code to retrieve your Instruction Set in XML format from your preferred place.
- onReceivingRefuseRegistration() which enables you to execute code after the Decision Agent declined your registration. Most of the time this means that your Instruction Set XML is malformed.
This requires the assistance of a programmer and it is wise to stop / delete your current agent and change whatever is necessary. Checkout the Instruction Set specifications below for clear instructions.
2. Create a Sensor implementation class for every sensor that you want to attach to your microcontroller. Make all of these classes extend the abstract class Sensor. This way you well get 2 methods that need to be implemented:
- getSensorID() where you can write the code that will return a unique identifier for your sensor.
- getMeasurements() where you should return a Measurements object including as many measurements as you specified in your Instruction Set XML.

## Instruction Set in XML format
The most important part for MLAF is the Instruction Set XML. Written by hand and then put onto disk or written directly into the getInstructionXML() method.
It described the Sensor Agent and specifies which Sensors are present and how the data should be treated by the Decision Agent. Below is an example, it's that same one that can be found in the decisionagent pa.
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<instructions>
    <identifier>
        fVTz7OCaD8WFJE5Jvw7K
    </identifier>
    <messaging>
        <topic>
            <name>sensor_agent#fVTz7OCaD8WFJE5Jvw7K</name>
            <daysToKeepMessages>1</daysToKeepMessages>
        </topic>
        <directToDecisionAgent>false</directToDecisionAgent>
    </messaging>
    <amountOfMissedDataPackages>5</amountOfMissedDataPackages>
    <sensors>
        <sensor id="MotionSensor">
            <label>Systolic Blood Pressure</label>
            <intervalinseconds>30</intervalinseconds>
            <unit>mm</unit>
            <measurements>
                <measurement id="y">
                    <min>0</min>
                    <max>200</max>
                    <plans>
                        <plan>
                            <below>0.4</below>
                            <above>0.8</above>
                            <message>Watch out!</message>
                            <via>SlackAgent</via>
                            <to></to>
                            <limit>3</limit>
                        </plan>
                        <plan>
                            <below>0.4</below>
                            <above>0.8</above>
                            <message>Panic! Measurement Y, Sensor: MotionSensor, Value:</message>
                            <via>MailAgent</via>
                            <to>example@example.com</to>
                            <limit>3</limit>
                        </plan>
                    </plans>
                </measurement>
                <measurement id="x">
                    <min>0</min>
                    <max>200</max>
                    <plans>
                        <plan>
                            <below>0.6</below>
                            <message>Watch out!</message>
                            <via>SlackAgent</via>
                            <to>#general</to>
                            <limit>30</limit>
                        </plan>
                        <plan>
                            <below>0.4</below>
                            <message>Panic! Measurement X, Sensor: MotionSensor, Value:</message>
                            <via>MailAgent</via>
                            <to>example@example.com</to>
                            <limit>3</limit>
                        </plan>
                    </plans>
                </measurement>
            </measurements>
            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>
        </sensor>
        <sensor id="HeartRate">
            <label>Heart Rate</label>
            <unit>bpm</unit>
            <intervalinseconds>30</intervalinseconds>
            <measurements>
                <measurement id="bpm">
                    <min>0</min>
                    <max>200</max>
                    <plans>
                        <plan>
                            <below>0.6</below>
                            <message>Watch out!</message>
                            <via>SlackAgent</via>
                            <to>#general</to>
                            <limit>30</limit>
                        </plan>
                        <plan>
                            <below>0.4</below>
                            <message>Panic! Measurement bpm, Sensor: HeartRate, Value:</message>
                            <via>MailAgent</via>
                            <to>example@example.com</to>
                            <limit>3</limit>
                        </plan>
                    </plans>
                </measurement>
            </measurements>
            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>
        </sensor>
    </sensors>
    <fallback>
        <message>This is the fallback message. \The sensor_agent#fVTz7OCaD8WFJE5Jvw7K is unregistered.</message>
        <via>MailAgent</via>
        <to>example@example.com</to>
    </fallback>
</instructions>

```

The Instruction Set consists of 5 elements:
- **identifier**, which is just like the name explains: a identifier for the Sensor Agent.
- **messaging**, where the way of communication is specified. This can be directly to the Decision Agent or via Topic based communication. You can't use them both at the same time. It will choose `<directToDecisionAgent>` over Topic based communcation.
- **amountOfMissedDataPackages**, specified per Sensor Agent. This will tell the Decision Agent how many times a (specified by the highest interval of all the Sensors) Sensor Agent is allowed to not send a data package
before the Decision Agent unregisters the Sensor Agent. This means that to start sending data again, the Sensor Agent should register with the Decision Agent first.
- **sensors**, in here you can specify per sensor what ID it should have. The label and the unit which the measurements are measured in. A min and a max for the sensor. If these are exceeded a overridable method called executeSensorReadingWarning() will execute.
This will only log that a sensor is measuring outside his given minimum and maximum value for now. Per sensor you can specify multiple `<plans>`. Per `<plan>` you can specify if the plan should execute `<below>` and/or `<above>` (as a percentage of the max value) the max value.
Also you need to specify what the `<message>` is you want to send `<via>` a [communcation agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/communication.md), `<to>` whom and `<limit>` how many times.
- **fallback**, when a Sensor Agent gets unregistered by the Decision Agent the fallback will be executed. It works in the same way as a plan does. The onyl difference is that it only gets executed when a Sensor Agent goes offline.

## Multi Lifecycle Agent Framework
[![Build Status](https://travis-ci.org/MSDA-LifeCycleAgents/mlaf-java.svg?branch=develop)](https://travis-ci.org/MSDA-LifeCycleAgents/mlaf-java)

![mlaf](https://raw.githubusercontent.com/MSDA-LifeCycleAgents/mlaf-java/feature/readme/docs/images/mlaf.gif)

This framework is put together by a group of students from the University of Applied Sciences in Utrecht. It serves the simplification of the creation of a Multi Lifecycle Agent System.


## Disclaimer
This software is is created as an assignment for a school project by 4th grade students. USE THE SOFTWARE AT YOUR OWN RISK. THE AUTHORS AND ALL AFFILIATES ASSUME NO RESPONSIBILITY FOR YOUR RESULTS.

The software is LGPLv2 licensed. For any implementation and use of our code should be agreed upon beforehand. See contact details.
## Table of Contents
- [Features](#features)
- [Quick start](#quickstart)
- [Documentations](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/index.md)
   - [Installation](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/installation.md)
   - [Configuration](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/configuration.md)
   - [Decision Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/decisionagent.md)
   - [Sensor Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/sensoragent.md)
   - [Communication Agents](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/communication.md)
   - [Topics and the Broker Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/brokeragent.md)
   - [Monitoring Dashboard](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/monitoring.md)
   - [Logger Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/loggeragent.md)
- [Support](#support)
   - [Bugs](#bugs--issues)
   - [Pull Requests](#pull-requests)
- [Requirements](#requirements)
    - [Min hardware required](#minimal-hardware-required)
    - [Software requirements](#software-requirements)

## Features
- [x] The entire framework can be used with mostly configuration and little to no implementation of the abstract classes. Implementing on top of the framework on the other hand is easy and straightforward.
- [x] **Decision Agent** making decisions based on so called plans specified in the Instruction Set XML.
- [x] **Broker Agent** for persisting messages sent via JADE's Topic management service.
- [x] **Monitoring Dashboard** build on a _generic TCP service_ in node-red. TCP service could be used to create your own dashboard.
- [x] **Logger Agent** that logs all the messages to console sent by Agents where there is LoggerAgentLogHandler attached to the logger.

## Quickstart
To get up and running with developing your own Multi Lifecycle Agent System follow these steps:
1. git clone https://github.com/MSDA-LifeCycleAgents/mlaf-java.git into your desired directory.
2. run mvn install inside of the the mlaf-java directory.
3. use the installed maven project as a dependency in the pom of your maven project.
4. create a resources folder in the root of your main project (as a sibling of your src folder).
5. inside this new folder you need to put a config.properties and a agents-stack.properties (examples can be found inside the resources folder of this repository). For furter documentation about how the config and the agents-stack should look like [go here](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/configuration.md))

Check out the pages about the Sensor Agent, Decision Agent, Broker Agent and Communication Agents to go further into detail.

## Support
### [Bugs / Issues](https://github.com/MSDA-LifeCycleAgents/mlaf-java/issues)
If you discover a bug in the framework, please 
[search our issue tracker](https://github.com/MSDA-LifeCycleAgents/mlaf-java/issues) 
first. If it hasn't been reported, please 
[create a new issue](https://github.com/MSDA-LifeCycleAgents/mlaf-java/issues/new) and 
ensure you follow the template guide so that our team can assist you as 
quickly as possible.
### [Pull Requests](https://github.com/MSDA-LifeCycleAgents/mlaf-java/pulls)
Feel like our framework is missing a feature? We welcome your pull requests! 
Please read our [Contributing document](https://github.com/MSDA-LifeCycleAgents/mlaf-java/tree/feature/readme/docs/contributing.md)
to understand the requirements before sending your pull-requests. 

**Important:** Always create your PR against the `develop` branch, not 
`master`.

## Requirements

### Minimal hardware required

### Software requirements

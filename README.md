## Multi Lifecycle Agent Framework

## Disclaimer
This software is is created as an assignment for a school project by 4th grade students. USE THE SOFTWARE AT YOUR OWN RISK. THE AUTHORS AND ALL AFFILIATES ASSUME NO RESPONSIBILITY FOR YOUR RESULTS.

The software is LGPLv2 licensed. For any implementation and use of our code should be agreed upon beforehand. See contact details.
## Table of Contents
- [Features](#features)
- [Quick start](#quickstart)
- [Documentations](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/docs/index.md)
   - [Installation](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/installation.md)
   - [Configuration](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/configuration.md)
   - [Decision Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/decisionagent.md)
   - [Sensor Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/sensoragent.md)
   - [Topics and the Broker Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/brokeragent.md)
   - [Monitoring Dashboard](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/monitoring.md)
   - [Logger Agent](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/loggeragent.md)
- [Support](#support)
   - [Bugs](#bugs--issues)
   - [Pull Requests](#pull-requests)
- [Basic Usage](#basic-usage)
  - [Bot commands](#bot-commands)
  - [Telegram RPC commands](#telegram-rpc-commands)
- [Requirements](#requirements)
    - [Min hardware required](#minimal-hardware-required)
    - [Software requirements](#software-requirements)

## Features
- [x] Decision Agent making decisions based on so called plans specified in the Instruction Set XML.
- [x] Broker Agent for persisting messages sent via JADE's Topic management service.
- [x] Monitoring Dashboard build on a generic TCP service. TCP service could be used to create your own dashboard.
- [x] Logger Agent that logs all the messages to console sent by Agents where there is LoggerAgentLogHandler attached to the logger.

## Quickstart
To get up and running with developing your own Multi Lifecycle Agent System follow these steps:
1. git clone https://github.com/MSDA-LifeCycleAgents/mlaf-java.git into your desired directory.
2. run mvn install inside of the the mlaf-java directory.
3. 

## Basic Usage

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
Please read our [Contributing document](https://github.com/MSDA-LifeCycleAgents/mlaf-java/docs/contributing.md)
to understand the requirements before sending your pull-requests. 

**Important:** Always create your PR against the `develop` branch, not 
`master`.

## Requirements

### Minimal hardware required

### Software requirements

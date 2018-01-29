# Communication Agent
Abstract class CommunicationAgent can be inherited to dispatch messages outside the boundaries of JADE.
The decision agent and sensor agents use these classes for notification purposes in their instruction sets.

A communication agent always expects an ACL message with request performative. The content of this message will be dispatched. 
There also is an optional user defined parameter "to", which represents the receiver of the message. Note that some communication agents,
for instance an e-mail agent, need to have a receiver (we can't send a mail to everyone) and consequences of the optional "to" parameter 
should be dealt with accordingly in method implementation. 

## Included implementations
The MLAF framework comes with two implementations for CommunicationAgent. The first one is a MailAgent, using the SMTP protocol. Usage and 
implementation are pretty straight forward, but for additional configuration see the [Configuration page](configuration.md).

The second implementation is a SlackAgent, using webhooks. This agent is a good example of why the "to" parameter is optional. A webhook
usually is based on a default channel. When the "to" parameter is not filled, the message will be sent here. However, when a #channel or
@person is filled as the "to" parameter, the message will be sent there.

## Quickstart
An implementation of CommunicationAgent has two implemented methods: ```createServiceDescription()``` and ```send(String message, String to)```.
All communication agents are registered in the DF by default. The ```createServiceDescription()``` is used for the description of your CommunicationAgent
implementation. The ```send``` method is used for the actual sending of the message, this is where te magic happens.

A straight forward implementation of a CommunicationAgent could look like this.

```

import com.mlaf.hu.communication.CommunicationAgent;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleCommAgent extends CommunicationAgent{
    private static final Logger LOGGER = Logger.getLogger(ExampleCommAgent.class.getName());
    
    @Override
    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName("ExampleCommAgent");
        sd.setType("ExampleCommAgent");
        return sd;
    }

    @Override
    protected void send(String message, String to) {
        if(to == null){
            LOGGER.log(Level.WARNING, "Could not send message, invalid receiver: {0}", to);
            return;
        }
        
        // send message here over mail, slack, twitter, facebook, telegram or carrier pigeon.
            
    }
}
'''

package com.mlaf.hu.decisionagent.behaviour;

import com.mlaf.hu.decisionagent.DecisionAgent;
import jade.core.behaviours.TickerBehaviour;

public class SaveToDiskBehaviour extends TickerBehaviour {
    private final DecisionAgent decisionAgent;

    public SaveToDiskBehaviour(DecisionAgent broker) {
        super(broker, DecisionAgent.STORE_INTERVAL_IN_MS);
        this.decisionAgent = broker;
    }

    @Override
    protected void onTick() {
        this.decisionAgent.storeSensorAgents();
    }

}

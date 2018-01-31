package com.mlaf.hu.brokeragent.behaviour;

import com.mlaf.hu.brokeragent.BrokerAgent;
import jade.core.behaviours.TickerBehaviour;

public class SaveToDiskBehaviour extends TickerBehaviour {
    private final BrokerAgent brokerAgent;

    public SaveToDiskBehaviour(BrokerAgent broker) {
        super(broker, BrokerAgent.STORE_INTERVAL_IN_MS);
        this.brokerAgent = broker;
    }

    @Override
    protected void onTick() {
        this.brokerAgent.storeTopics();
    }

}

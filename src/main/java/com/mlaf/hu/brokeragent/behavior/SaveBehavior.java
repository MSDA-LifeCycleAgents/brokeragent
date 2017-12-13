package com.mlaf.hu.brokeragent.behavior;

import com.mlaf.hu.brokeragent.BrokerAgent;
import jade.core.behaviours.TickerBehaviour;

public class SaveBehavior extends TickerBehaviour {
    private final BrokerAgent brokerAgent;

    public SaveBehavior(BrokerAgent broker) {
        super(broker, BrokerAgent.STORE_INTERVAL_IN_MS);
        this.brokerAgent = broker;
    }

    @Override
    protected void onTick() {
        this.brokerAgent.storeTopics();
    }

}

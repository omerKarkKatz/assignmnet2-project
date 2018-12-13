package bgu.spl.mics.application.messages;


import bgu.spl.mics.Broadcast;

import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {

    private AtomicInteger currTick;

    public TickBroadcast (int currTick){
        this.currTick = new AtomicInteger(currTick);
    }

    public int getCurrTick() {
        return currTick.get();
    }
}

package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent implements Event {
    private int distance;
    private String address;

    public DeliveryEvent(String address, int distance) {
        this.distance = distance;
        this.address = address;
    }

    public String getAddress(){
        return address;
    }
    public int getDistance(){
        return distance;
    }
}

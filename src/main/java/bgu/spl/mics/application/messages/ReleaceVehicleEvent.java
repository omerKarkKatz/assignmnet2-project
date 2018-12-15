package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaceVehicleEvent implements Event {
    private DeliveryVehicle vehicle;

    public ReleaceVehicleEvent(DeliveryVehicle v){
        vehicle = v;
    }
    public DeliveryVehicle getVehicle(){
        return vehicle;
    }
}

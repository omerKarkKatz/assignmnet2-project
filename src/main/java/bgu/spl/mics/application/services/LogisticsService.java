package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaceVehicleEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private CountDownLatch countDownLatch;

	public LogisticsService(int id, CountDownLatch countDownLatch) {
		super("LogisticsService "+id);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		AcquireVehicleEvent AV = new AcquireVehicleEvent();
		subscribeEvent(DeliveryEvent.class, deliver ->{
			Future<Future<DeliveryVehicle>> f=sendEvent(AV);
			if(f==null || f.get()==null || f.get().get()==null){
				complete(deliver,null);
				return;
			}
			DeliveryVehicle deliveryVehicle = f.get().get();
			deliveryVehicle.deliver(deliver.getAddress(),deliver.getDistance());

			ReleaceVehicleEvent RV = new ReleaceVehicleEvent(deliveryVehicle);
			sendEvent(RV);
		});
		subscribeBroadcast(TerminationBroadcast.class, closingStore -> terminate());
		countDownLatch.countDown();
	}

}

package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static class SingletonResourceHolder {
		private static ResourcesHolder ResourceHOlderInstance = new ResourcesHolder();
	}
	/**
     * Retrieves the single instance of this class.
     */

	private LinkedBlockingQueue<DeliveryVehicle> deliveryVehicleQueue;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> deliveryVehicleFutureQueue;

	public static ResourcesHolder getInstance() {
		return SingletonResourceHolder.ResourceHOlderInstance;
	}

	public ResourcesHolder() {
		this.deliveryVehicleQueue = new LinkedBlockingQueue<DeliveryVehicle>();
		this.deliveryVehicleFutureQueue = new LinkedBlockingQueue<Future<DeliveryVehicle>>();
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> deliveryVehicleFuture = new Future<>();
		synchronized (this) {
		    if (deliveryVehicleQueue.size() > 0)
                deliveryVehicleFuture.resolve(deliveryVehicleQueue.poll());
		     else
		         deliveryVehicleFutureQueue.add(deliveryVehicleFuture);
			return deliveryVehicleFuture;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if (vehicle == null)
			resovleAll();
		else {
			if (deliveryVehicleFutureQueue.size() > 0)
				deliveryVehicleFutureQueue.poll().resolve(vehicle);
			else
				deliveryVehicleQueue.add(vehicle);
		}
	}

	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
	    for (DeliveryVehicle vehicle: vehicles)
	        deliveryVehicleQueue.add(vehicle);
	}
	private void resovleAll(){
		for (int i=0; i < deliveryVehicleFutureQueue.size(); i++)
		deliveryVehicleFutureQueue.poll().resolve(null);
	}
}

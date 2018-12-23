package bgu.spl.mics.application.services;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvilabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory = Inventory.getInstance();
	private String bookTitle;
	private Integer priceOrMinus1;
	private CountDownLatch countDownLatch;

	public InventoryService(int id, CountDownLatch countDownLatch) {
		super("InventoryService " + id);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvilabilityEvent.class, checkAvilabilityEv -> {
			bookTitle = checkAvilabilityEv.getBookTitle();
			priceOrMinus1 = inventory.checkAvailabiltyAndGetPrice(bookTitle);
			// resolves future with the price of the book if available
			complete(checkAvilabilityEv, priceOrMinus1);
		});

		subscribeEvent(TakeBookEvent.class, takeBookEv ->
				complete(takeBookEv,inventory.take(takeBookEv.getBookTitle())));
		subscribeBroadcast(TerminationBroadcast.class, closingStore -> terminate());
		countDownLatch.countDown();
	}

}

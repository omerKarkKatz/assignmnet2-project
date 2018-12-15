package bgu.spl.mics.application.services;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvilabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.*;

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

	public InventoryService(int id) {
		super("InventoryService" + id);
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvilabilityEvent.class, checkAvilabilityEv -> {
			bookTitle = checkAvilabilityEv.getBookTitle();
			priceOrMinus1 = inventory.checkAvailabiltyAndGetPrice(bookTitle);
			// resoves future with the price of the book if avilable
			MessageBusImpl.getInstance().complete(checkAvilabilityEv, priceOrMinus1);
		});

		subscribeEvent(TakeBookEvent.class, takeBookEv -> {

		});
		
	}

}

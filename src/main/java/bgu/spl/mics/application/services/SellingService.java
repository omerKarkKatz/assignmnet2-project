package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvilabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	// TODO add the receipt to the the customer list
	private int currTick;
	private MoneyRegister moneyRegister = MoneyRegister.getInstance();
	private CountDownLatch countDownLatch;

	public SellingService(int id, CountDownLatch countDownLatch) {
		super("SellingService " + id);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		// sets the curr Tick.
		subscribeBroadcast(TickBroadcast.class, tickEv -> currTick = tickEv.getCurrTick());

		subscribeEvent(BookOrderEvent.class , bookOrderEv -> {
			Customer customer = bookOrderEv.getCustomer();
			String bookTitle = bookOrderEv.getBookTitle();
			int price = sendEvent(new CheckAvilabilityEvent(bookTitle)).get();
			if(price != -1){
				OrderResult orderResult = null;
				synchronized (customer.getMoneyLock()) {
					if (customer.getAvailableCreditAmount() >= price) {
						 orderResult = sendEvent(new TakeBookEvent(bookTitle)).get();
						if (orderResult == OrderResult.SUCCESSFULLY_TAKEN)
							moneyRegister.chargeCreditCard(customer, price);
					}
				}
				if (orderResult == OrderResult.SUCCESSFULLY_TAKEN) {
					OrderReceipt orderReceipt = new OrderReceipt(1, getName(), customer.getId(),
										bookTitle, price, currTick, bookOrderEv.getOrderTick(), currTick);
					customer.getReceipts().add(orderReceipt);
					moneyRegister.file(orderReceipt);
				}
			}
		});

		countDownLatch.countDown();
	}



}

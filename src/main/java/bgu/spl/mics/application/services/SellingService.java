package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
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
		System.out.println("started: " + this.getName());
		subscribeBroadcast(TickBroadcast.class, tickEv -> currTick = tickEv.getCurrTick());

		subscribeEvent(BookOrderEvent.class , bookOrderEv -> {
			Customer customer = bookOrderEv.getCustomer();
			String bookTitle = bookOrderEv.getBookTitle();
			Future<Integer> price = sendEvent(new CheckAvilabilityEvent(bookTitle));
			System.out.println("checking availability of book "+bookTitle);
			if(price != null && price.get() != -1) {
				synchronized (customer.getMoneyLock()) {
					System.out.println("customer money "+customer.getAvailableCreditAmount());
					System.out.println("book price "+price.get());
				if (customer.getAvailableCreditAmount() >= price.get()) {
					System.out.println("enough money for buying the book");
						Future<OrderResult> orderResult = sendEvent(new TakeBookEvent(bookTitle));
						if (orderResult != null && orderResult.get() == OrderResult.SUCCESSFULLY_TAKEN) {
							System.out.println("acquisition succeeded");
							moneyRegister.chargeCreditCard(customer, price.get());

							OrderReceipt orderReceipt = new OrderReceipt(1, getName(), customer.getId(), bookTitle, price.get(), currTick, bookOrderEv.getOrderTick(), currTick);
							customer.getReceipts().add(orderReceipt);
							moneyRegister.file(orderReceipt);
							sendEvent(new DeliveryEvent(customer.getAddress(),customer.getDistance()));
						}
						else
					System.out.println("book is not in stock");
					}
				}
			}
		});

		subscribeBroadcast(TerminationBroadcast.class, closingStore -> terminate());
		countDownLatch.countDown();
	}
}

package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import jdk.internal.vm.compiler.collections.Pair;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer customer;
	private AtomicInteger currentTick = new AtomicInteger(0);
	private Vector<Pair<String,Integer >> bookOrderSchedule = new Vector<>();
	private Iterator<Pair<String, Integer>> bookOrderSheduleIter;
	// id num of this APIService the number of
	//TODO: think where are we sapoused to recive the orderRecite or null.

	public APIService(int id , Customer customer, Vector<Pair<String,Integer>> orderSchedule) {
		super("APIService" + id);
		this.customer = customer;
		this.bookOrderSchedule = orderSchedule;
		// sorts the list of order book Schedule
		sortShedule(bookOrderSchedule);
		this.bookOrderSheduleIter = bookOrderSchedule.iterator();
	}



	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class , tickBrod -> {
			currentTick.set(tickBrod.getCurrTick());

			// check if there are more orders to do
			if (bookOrderSheduleIter.hasNext()) {
				Pair<String, Integer> bookOrder = bookOrderSheduleIter.next();
				int orderTick = bookOrder.getRight();
				// checks all books that need to be ordered on this tick.
				while (currentTick.get() == orderTick) {
					String bookTitle = bookOrder.getLeft();
					// sending the order book event
					sendEvent(new BookOrderEvent(customer, bookTitle, ));
					if(bookOrderSheduleIter.hasNext()) {
						bookOrder = bookOrderSheduleIter.next();
						orderTick = bookOrder.getRight();
					}
				}
			}

		});
	}

	private void sortShedule(List<Pair<String,Integer>> bookOrderSchedule){
		Collections.sort(bookOrderSchedule, new Comparator<Pair<String, Integer>>() {
			@Override
			public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
				if(o1.getRight() > o2.getRight()){
					return 1;
				}else if(o1.getRight() == o2.getRight())
					return 0;
				else
					return -1;
			}
		});
	}

}

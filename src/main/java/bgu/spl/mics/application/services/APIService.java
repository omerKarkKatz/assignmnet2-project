package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import javafx.util.Pair;



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
	private Iterator<Pair<String, Integer>> bookOrderScheduleIter;
	// id num of this APIService the number of
	//TODO: think where are we sapoused to recive the orderRecite or null.

	public APIService(int id , Customer customer, Vector<Pair<String,Integer>> orderSchedule) {
		super("APIService" + id);
		this.customer = customer;
		this.bookOrderSchedule = orderSchedule;
		// sorts the list of order book Schedule
		sortSchedule(bookOrderSchedule);
		this.bookOrderScheduleIter = bookOrderSchedule.iterator();
	}



	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class , tickBrod -> {
			currentTick.set(tickBrod.getCurrTick());
			boolean order = true;
			while (order & bookOrderScheduleIter.hasNext()){
				if (order) {
					bookOrder = bookOrderScheduleIter.next();
				}
				if (bookOrder.getValue()== currentTick.get()){
					sendEvent(BookOrderEvent());
				}
				else
					order = false;
			}

		});
	}

	private void sortSchedule(List<Pair<String,Integer>> bookOrderSchedule){
		Collections.sort(bookOrderSchedule, (o1, o2) -> {
			if(o1.getValue() > o2.getValue()){
				return 1;
			}else if(o1.getValue() == o2.getValue())
				return 0;
			else
				return -1;
		});
	}

}

package bgu.spl.mics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.HashMap;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap<Integer, Queue<MicroService>> MicroServices;
	private Object lock1;

	private static class SingletonHolder {
		private static MessageBusImpl MessegeBusInstance = new MessageBusImpl();
	}

	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> QueueOfMicroTasks;
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<Event, Future> EventToFuture;

	private MessageBusImpl(){
	    QueueOfMicroTasks = new ConcurrentHashMap<>();
	    eventsSubscribers = new ConcurrentHashMap<>();
	    broadcastSubscribers = new ConcurrentHashMap<>();
	    EventToFuture = new ConcurrentHashMap<>();
    }

	public static MessageBus getInstance() {
		return SingletonHolder.MessegeBusInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());
		eventsSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		broadcastSubscribers.putIfAbsent(type, new Vector<>());
		//check if this method should be synchonized
		broadcastSubscribers.get(type).add(m);


	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// find the futere object related to event e, resolve the future obj(future method)
		EventToFuture.get(e).resolve(result);
		// check if to delete the event from the hash or you need it fot log.


	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// check if this syntax is ok//
		Iterator<MicroService> iter = broadcastSubscribers.get(b).iterator();
		while (iter.hasNext()){
			QueueOfMicroTasks.get(iter.next()).add(b);

		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> futureEvent = new Future<T>();
		EventToFuture.putIfAbsent(e, futureEvent);
		MicroService m = null;
		synchronized (eventsSubscribers.get(e)) {
			m = eventsSubscribers.get(e).poll();
			// checkes if there is a micro service that can handle this.
			if( m != null)
				return null;
			// moves the micro to the end of the queue (round robin manner), adds message to the microMessageQueue.
			else {
				eventsSubscribers.get(e).add(m);
				QueueOfMicroTasks.get(m).add(e);
			}
			return futureEvent;
		}
	}

	@Override
	public void register(MicroService m) {
		QueueOfMicroTasks.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			if (!QueueOfMicroTasks.contains(m))
				throw new IllegalStateException();
			else
				return QueueOfMicroTasks.get(m).take();
	}

	

}

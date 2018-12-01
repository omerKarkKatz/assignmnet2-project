package bgu.spl.mics;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletonHolder {
		private static MessageBusImpl MessegeBusInstance = new MessageBusImpl();
	}

	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> QueueOfMicroTasks;
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<Event, Future> EventToFuture;
	private ConcurrentHashMap<MicroService, Vector<Class<? extends Message>>> messagesOfMicroToDelete;
	private Object lockSendTask;

	private MessageBusImpl(){
	    QueueOfMicroTasks = new ConcurrentHashMap<>();
	    eventsSubscribers = new ConcurrentHashMap<>();
	    broadcastSubscribers = new ConcurrentHashMap<>();
	    EventToFuture = new ConcurrentHashMap<>();
		messagesOfMicroToDelete = new ConcurrentHashMap<>();
    }

	public static MessageBus getInstance() {
		return SingletonHolder.MessegeBusInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// check if to sync these 2 lines.
		if(QueueOfMicroTasks.contains(m)) {
			messagesOfMicroToDelete.putIfAbsent(m, new Vector<>());
			messagesOfMicroToDelete.get(m).add(type);

			eventsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());
			eventsSubscribers.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(QueueOfMicroTasks.contains(m)) {
			messagesOfMicroToDelete.putIfAbsent(m, new Vector<>());
			messagesOfMicroToDelete.get(m).add(type);

			//check if this method should be synchonized
			broadcastSubscribers.putIfAbsent(type, new Vector<>());
			broadcastSubscribers.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// find the futere object related to event e, resolve the future obj(future method)
		EventToFuture.get(e).resolve(result);
		// check if to delete the event from the hash or you need it fot log.
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Iterator<MicroService> iter = broadcastSubscribers.get(b).iterator();
		while (iter.hasNext()){
			synchronized (lockSendTask) {
				QueueOfMicroTasks.get(iter.next()).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> futureEvent = new Future<T>();
		EventToFuture.putIfAbsent(e, futureEvent);
		MicroService m = null;
		if(eventsSubscribers.contains(e)) {
			synchronized (eventsSubscribers.get(e)) {
				m = eventsSubscribers.get(e).poll();
				// checkes if there is a micro service that can handle this.
				if (m != null)
					return null;
					// moves the micro to the end of the queue (round robin manner), adds message to the microMessageQueue.
				else {
					eventsSubscribers.get(e).add(m);
					synchronized (lockSendTask) {
						QueueOfMicroTasks.get(m).add(e);
					}
				}
			}
		}
			return futureEvent;
	}

	@Override
	public void register(MicroService m) {
		QueueOfMicroTasks.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}


	@Override
	public void unregister(MicroService m) {
		// check if clear is sync.
		QueueOfMicroTasks.get(m).clear();
		QueueOfMicroTasks.remove(m);
		Iterator<Class<? extends Message>> iter = messagesOfMicroToDelete.get(m).iterator();
		while(iter.hasNext()) {
			Class<? extends Message> message = iter.next();
			if (message.getClass().equals(Event.class))
				eventsSubscribers.get(message).remove(m);
			else
				broadcastSubscribers.get(message).remove(m);
		}
		m.terminate();
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			if (!QueueOfMicroTasks.contains(m))
				throw new IllegalStateException();
			else
				return QueueOfMicroTasks.get(m).take();
	}
}

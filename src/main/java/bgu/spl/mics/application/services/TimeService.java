package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

    private int speed;
    private static int duration;
    private AtomicInteger passedTickes = new AtomicInteger(1);

    public TimeService(int speed, int duration) {
        super("Timer service");
        this.speed = speed;
        this.duration = duration;
    }

    Timer timer = new Timer();

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (duration >= passedTickes.get()) {
                System.out.println(passedTickes.get());
                sendBroadcast(new TickBroadcast(passedTickes.get()));
                passedTickes.incrementAndGet();
            } else {
                sendBroadcast(new TerminationBroadcast());
                timer.cancel();
                timer.purge();

            }
        }

    };
    @Override
    protected void initialize() {
        System.out.println("started: " + this.getName());
        timer.scheduleAtFixedRate(timerTask, 0, speed);
        terminate();
    }
    public static int getduration(){
        return duration;
    }
}

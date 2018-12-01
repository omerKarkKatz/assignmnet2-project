import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<Object> future;
    private Object result;

    @Before
    public void setUp() throws Exception {
        future = new Future();
        result = new Object();
    }

    @Test
    public void get() {
        future.resolve(result);
        assertEquals(result, future.get());
    }

    @Test
    public void resolve() {
        future.resolve(result);
        assertEquals("resolved different than should",result,future.get());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void get1() {
        assertNull(future.get(0, TimeUnit.MILLISECONDS));
        assertNull(future.get(100,TimeUnit.MILLISECONDS));
        future.resolve(result);
        assertNull(future.get(0,TimeUnit.MILLISECONDS));
        //check if setting finite time is legitimate.
        //assertEquals(result,future.get(100,TimeUnit.MILLISECONDS));
    }
}
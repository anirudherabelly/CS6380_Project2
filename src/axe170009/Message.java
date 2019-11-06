package axe170009;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed {

    private static final int factor = 100;

    private int uid;
    private long time;
    private long delayTime;
    private int round;

    // Constructor of DelayObject
    public Message(int _uid, int _round, long _delayTime)
    {
        this.uid = _uid;
        this.time = System.currentTimeMillis()
                + _delayTime*factor;
        this.delayTime = _delayTime;
        this.round = _round;
    }

    // Implementing getDelay() method of Delayed
    @Override
    public long getDelay(TimeUnit unit)
    {
        long diff = time - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    // Implementing compareTo() method of Delayed
    @Override
    public int compareTo(Delayed other)
    {
        if (this.time < ((Message)other).time) {
            return -1;
        }
        if (this.time > ((Message)other).time) {
            return 1;
        }
        return 0;
    }

    // Implementing toString() method of Delayed
    @Override
    public String toString()
    {
        return "\n{"
                + "process uid =" + uid
                + ", time =" + time
                + "}";
    }


    public int getUid() {
        return uid;
    }

    public long getDelayTime() {
        return delayTime;
    }
}

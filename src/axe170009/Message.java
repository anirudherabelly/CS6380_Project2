package axe170009;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed {

    // Time factor
    private static final int factor = 100;

    // UID of process which is sending this message
    private int uid;
    // Current Time
    private long time;
    // Random delay for this message
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

    /**
     * Method to implement getDelay() method of Delayed
     * @return Time in milli seconds
     */
    @Override
    public long getDelay(TimeUnit unit)
    {
        long diff = time - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Method to implement compareTo() method of Delayed
     * @return -1,1,0
     */
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

    /**
     * Method to get the UID of this message
     * @return message UID
     */
    public int getUid() {
        return uid;
    }

    /**
     * Method to get the delay time of this message
     * @return delayed time
     */
    public long getDelayTime() {
        return delayTime;
    }
}

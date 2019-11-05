package axe170009;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Process extends Thread{

    private static final int DELAY_MIN = 1;
    private static final int DELAY_MAX = 10;

    private int uid;
    private int numOfMessages;
    private int diameter;
    private int max_uid;
    private int currentRound;
    private int numOfNeighbours;
    private boolean terminate;

    private List<Process> neighbours;

    private BlockingQueue<Message> messageBuffer;

    private Status leaderStatus;

    public Process(int _uid, int _diameter){
        this.uid = _uid;
        this.numOfMessages = 0;
        this.diameter = _diameter;
        this.max_uid = this.uid;
        this.currentRound = 0;
        this.neighbours = new ArrayList<>();
        this.terminate = false;
        this.messageBuffer = new DelayQueue<>();
        this.leaderStatus = Status.UNKNOWN;
    }

    @Override
    public void run(){
        while(!terminate){
            if (this.currentRound == 0) {
                this.sendMessage();
            }
            else if (this.currentRound < this.diameter){
                 if (this.messageBuffer.size() == this.numOfNeighbours) {
                    while (this.messageBuffer.size() > 0) {
                        this.receiveMessage();
                    }
                    this.sendMessage();
                 }
            }
            else if(this.currentRound == this.diameter) {
                if(max_uid == uid){
                    this.leaderStatus = Status.LEADER;
                    System.out.println("Process with uid-"+this.uid+" is Leader.");
                }
                else{
                    this.leaderStatus = Status.NON_LEADER;
                    System.out.println("Process with uid-"+this.uid+" is Non-Leader.");
                }
                System.out.println("Total number of messages with process uid- "+ this.uid + ":" +this.getNumOfMessages());
                this.currentRound += 1;
            }
        }
    }

    public void addNeighbour(Process _p){
        this.neighbours.add(_p);
        this.numOfNeighbours += 1;
    }

    public boolean sendMessage(){
        this.currentRound += 1;
        for(Process p : this.neighbours){
            int randDelay = ThreadLocalRandom.current().nextInt(DELAY_MIN, DELAY_MAX+1);
            p.getMessageBuffer().add(new Message(max_uid, randDelay));
            this.numOfMessages += 1;
            System.out.println("SEND : from process uid-"+this.uid+" to process uid-"+p.uid+" with channel delay of "+ randDelay);
        }
        return true;
    }

    public boolean receiveMessage(){
        List<Message> availableMessages = new ArrayList<>();
        this.messageBuffer.drainTo(availableMessages);
        if(availableMessages.isEmpty())return false;
        for(Message message : availableMessages){
            max_uid = Math.max(max_uid, message.getUid());
            System.out.println("RECEIVE : from process uid-"+message.getUid()+" to process uid-"+this.uid+" after a channel delay of "+ message.getDelayTime());
        }
        return true;
    }

    public BlockingQueue<Message> getMessageBuffer() {
        return messageBuffer;
    }

    public Status getLeaderStatus() {
        return leaderStatus;
    }

    public int getNumOfMessages() {
        return numOfMessages;
    }

    public void setTerminate(boolean _terminate) {
        this.terminate = _terminate;
    }
}

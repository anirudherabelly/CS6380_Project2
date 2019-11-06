package axe170009;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<Integer, DelayQueue<Message>> messageBuffer;

    private Status leaderStatus;

    public Process(int _uid, int _diameter){
        this.uid = _uid;
        this.numOfMessages = 0;
        this.diameter = _diameter;
        this.max_uid = this.uid;
        this.currentRound = 0;
        this.neighbours = new ArrayList<>();
        this.terminate = false;
        this.messageBuffer = initializeMessageBuffer();
        this.leaderStatus = Status.UNKNOWN;

    }

    private HashMap<Integer, DelayQueue<Message>> initializeMessageBuffer(){
        HashMap<Integer, DelayQueue<Message>> map = new HashMap<>();
        for(int i=0; i<this.diameter; i++){
            map.put(i, new DelayQueue<>());
        }
        return map;
    }


    @Override
    public void run(){
        boolean canSendMessage = true;
        while(!terminate){
            if (this.currentRound < this.diameter){
                if(canSendMessage){
                    this.sendMessage();
                    canSendMessage = false;
                }
                boolean isProcessed = this.receiveMessage();
                if(isProcessed) canSendMessage = true;
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

    private boolean sendMessage(){
        for(Process p : this.neighbours){
            int randDelay = ThreadLocalRandom.current().nextInt(DELAY_MIN, DELAY_MAX+1);

            p.messageBuffer.get(this.currentRound).add(new Message(max_uid, this.currentRound, randDelay));

            this.numOfMessages += 1;

            System.out.println("SEND : from process uid-"+this.uid+" to process uid-"+p.uid+" with channel delay of "+ randDelay+" in round "+this.currentRound);
        }
        return true;
    }

    private boolean receiveMessage(){
//        if(this.uid  == 5)
//            System.out.println(this.messageBuffer.get(this.currentRound).size() + " "+this.numOfNeighbours);
        if (this.messageBuffer.get(this.currentRound).size() == this.numOfNeighbours) {

            List<Message> availableMessages;

            while (this.messageBuffer.get(this.currentRound).size() > 0) {

                availableMessages = new ArrayList<>();

                this.messageBuffer.get(this.currentRound).drainTo(availableMessages);

                for (Message message : availableMessages) {
                    max_uid = Math.max(max_uid, message.getUid());

                    System.out.println("RECEIVE : from process uid-" + message.getUid() + " to process uid-" + this.uid + " after a channel delay of " + message.getDelayTime() + " in round " + this.currentRound);
                }
            }
            this.messageBuffer.remove(this.currentRound);
            this.currentRound += 1;

            return true;
        }
        return false;
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

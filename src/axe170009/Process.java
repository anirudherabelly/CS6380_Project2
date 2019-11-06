package axe170009;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Process extends Thread{

    private static final int DELAY_MIN = 1;
    private static final int DELAY_MAX = 10;

    // This process' UID
    private int uid;
    // Number of messages sent by this process
    private int numOfMessages;
    private int diameter;
    // Max UID seen so far
    private int max_uid;
    // Storing the round info
    private int currentRound;
    private int numOfNeighbours;
    // Flag to terminate the process when the leader is elected
    private boolean terminate;

    private List<Process> neighbours;

    // Specifies the number of messages this process has received in a specific round
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

    /**
     * Method to initialize this process' message buffer
     * @return message buffer of this process
     */
    private HashMap<Integer, DelayQueue<Message>> initializeMessageBuffer(){
        HashMap<Integer, DelayQueue<Message>> map = new HashMap<>();
        for(int i=0; i<this.diameter; i++){
            map.put(i, new DelayQueue<>());
        }
        return map;
    }


    @Override
    public void run(){
        // Inititally canSendMessage flag of this process is true  
        // and it remains false until it receives messages from all it's neighbours  
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

    /**
     * Method to add the neighbours of this process
     * @param _p process object
     */
    public void addNeighbour(Process _p){
        this.neighbours.add(_p);
        this.numOfNeighbours += 1;
    }

    /**
     * Method to send message to all it's neighbours
     * @return boolean specifies whether the message are sent to all it's neighbours 
     */
    private boolean sendMessage(){
        for(Process p : this.neighbours){
            // Introduces random delay for this message
            int randDelay = ThreadLocalRandom.current().nextInt(DELAY_MIN, DELAY_MAX+1);
            
            // Add the new message to this processes' messageBuffer  
            p.messageBuffer.get(this.currentRound).add(new Message(max_uid, this.currentRound, randDelay));

            this.numOfMessages += 1;

            System.out.println("SEND : from process uid-"+this.uid+" to process uid-"+p.uid+" with channel delay of "+ randDelay+" in round "+this.currentRound);
        }
        return true;
    }

    /**
     * Method to receive messages from all it's neighbours
     * @return TRUE only when it receives message from all it's neighbours
     */
    private boolean receiveMessage(){
        if (this.messageBuffer.get(this.currentRound).size() == this.numOfNeighbours) {

            // contains the messages which are available to the 
            // process in this round
            List<Message> availableMessages;

            while (this.messageBuffer.get(this.currentRound).size() > 0) {

                availableMessages = new ArrayList<>();

                this.messageBuffer.get(this.currentRound).drainTo(availableMessages);

                for (Message message : availableMessages) {
                    max_uid = Math.max(max_uid, message.getUid());

                    System.out.println("RECEIVE : from process uid-" + message.getUid() + " to process uid-" + this.uid + " after a channel delay of " + message.getDelayTime() + " in round " + this.currentRound);
                }
            }

            // Removing the message from this buffer after 
            // processing the received messages
            this.messageBuffer.remove(this.currentRound);
            this.currentRound += 1;

            return true;
        }
        return false;
    }

    /**
     * Method to know the current status of the process
     * @return leader status
     */
    public Status getLeaderStatus() {
        return leaderStatus;
    }

    /**
     * Method to ge the count of messages
     * @return numOfMessages
     */
    public int getNumOfMessages() {
        return numOfMessages;
    }

    /**
     * Method to toggle the terminate flag of this process
     * @return numOfMessages
     */
    public void setTerminate(boolean _terminate) {
        this.terminate = _terminate;
    }
}

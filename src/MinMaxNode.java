import java.util.ArrayList;
import java.util.List;

public class MinMaxNode {
    // -1 as stage number is flag that we are done and that the message received contains the leader node number
    private int stageNumber = 1;
    private Integer curVal;
    private List<Message> receivedMessages;
    private MinMaxNode rightNeighbor;
    private MinMaxState minMaxState = MinMaxState.ACTIVE;

    private Integer leaderNode;

    public MinMaxNode (int value) {
        curVal = value;
        receivedMessages = new ArrayList<>();
    }

    public void linkRightNeighborNode(MinMaxNode node) {
        rightNeighbor = node;
    }

    public void sendMessage(Message receivedMessage) {
        this.receivedMessages.add(receivedMessage);
        MinMax.messageCount++;
    }

    public Integer getCurVal() {
        return curVal;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    private enum MinMaxState {
        ACTIVE,
        PASSIVE,
        LEADER,
        NOTIFIED
        ;
    }

    public void action() {
        if (!receivedMessages.isEmpty()) {
            Message recievedMessage = receivedMessages.get(0);
            switch(minMaxState) {
                case ACTIVE:
                    checkAndSetIfLeader(recievedMessage);
                    sendMessageIfActiveBeforeCheckAndSurvive(recievedMessage);
                    break;
                case LEADER:
                    // check if notify stage has completed
                    if (recievedMessage.getStageNum() == -1) {
                        MinMax.done = true;
                    }
                    break;
                // otherwise a pacified node, so just pass on the message you received unless being notified of leader
                case PASSIVE:
                    if (recievedMessage.getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = recievedMessage.getVal();
                    }
                    rightNeighbor.sendMessage(recievedMessage);
                    break;
            }
            // message consumed
            receivedMessages.remove(recievedMessage);
        }
    }

    public void minMaxSurvive(Message recievedMessage) {
        // even stage, kill node if smaller
        if (recievedMessage.getStageNum()%2 == 0) {
            if (recievedMessage.getVal() < curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
        // odd stage, kill node if bigger
        else {
            if (recievedMessage.getVal() > curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
    }

    public void sendMessageIfActiveBeforeCheckAndSurvive(Message recievedMessage) {
        // if not leader, then minmax survive test
        if (minMaxState == MinMaxState.ACTIVE && recievedMessage.getStageNum() >= stageNumber) {
            // if survived, send new message after incrementing stage # and updating current value
            minMaxSurvive(recievedMessage);
            if (minMaxState == MinMaxState.ACTIVE) {
                stageNumber++;
                curVal = recievedMessage.getVal();
                rightNeighbor.sendMessage(new Message(curVal, stageNumber));
            }
        }
    }

    public void checkAndSetIfLeader(Message recievedMessage) {
        if (curVal == recievedMessage.getVal()) {
            System.out.println("--------Leader was elected " + curVal + " and had state: " + toString() + "----------");
            minMaxState = MinMaxState.LEADER;
            leaderNode = curVal;
            rightNeighbor.sendMessage(new Message(curVal, -1));
        }
    }

    public String toString() {
        return "Node: " + curVal + " S" + stageNumber + " M" + receivedMessages + " status: " + minMaxState.toString().substring(0,3);
    }
}

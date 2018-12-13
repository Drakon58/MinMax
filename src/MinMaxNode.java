import java.util.ArrayList;
import java.util.List;

public class MinMaxNode {
    // -1 as stage number is flag that we are done and that the message received contains the leader node number
    private int stageNumber = 1;
    private Integer originalVal;
    private Integer curVal;
    private List<Message> receivedMessage;
    private MinMaxNode rightNeighbor;
    private MinMaxState minMaxState = MinMaxState.ACTIVE;

    private Integer leaderNode;

    public MinMaxNode (int value) {
        originalVal = value;
        curVal = value;
        receivedMessage = new ArrayList<>();
    }

    public void linkRightNeighborNode(MinMaxNode node) {
        rightNeighbor = node;
    }

    public void sendMessage(Message receivedMessage) {
        this.receivedMessage.add(receivedMessage);
        MinMax.messageCount++;
    }

    public Integer getCurVal() {
        return curVal;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public Integer getLeaderNode() {
        return leaderNode;
    }

    public MinMaxNode getRightNeighbor() {
        return rightNeighbor;
    }

    public List<Message> getReceivedMessage() {
        return receivedMessage;
    }

    private enum MinMaxState {
        ACTIVE,
        PASSIVE,
        LEADER,
        NOTIFIED
        ;
    }

    public void action() {
//        System.out.println("Node " + curVal + " with status " + minMaxState + " has a new message? " + !receivedMessage.isEmpty());
        if (!receivedMessage.isEmpty()) {
            switch(minMaxState) {
                case ACTIVE:
//                    System.out.println("Before leader check, current value is " + curVal + " and received message of " + receivedMessage.get(0).getVal());
                    checkAndSetIfLeader();
                    sendMessageIfActiveBeforeCheckAndSurvive();
                    break;
                case LEADER:
                    // check if notify stage has completed
                    if (receivedMessage.get(0).getStageNum() == -1) {
                        MinMax.done = true;

                    }
                    break;
                // otherwise a pacified node, so just pass on the message you received unless being notified of leader
                case PASSIVE:
                    if (receivedMessage.get(0).getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = receivedMessage.get(0).getVal();
//                        System.out.println("Node " + curVal + " has been notified");
                    }
                    else {
//                        System.out.println("Pacified node forwarding message containing " + receivedMessage.get(0).getVal() + " stage " + receivedMessage.get(0).getStageNum());
                    }
                    rightNeighbor.sendMessage(receivedMessage.get(0));
                    break;
            }
            // message consumed
            receivedMessage.remove(0);
//            System.out.println("Node " + curVal + " has messages: " + receivedMessage);
        }
    }

    public void minMaxSurvive() {
//        System.out.println("Node " + curVal + " recieved message " + receivedMessage.get(0).getVal() + " stage " + receivedMessage.get(0).getStageNum());
        // even stage, kill node if smaller
        if (receivedMessage.get(0).getStageNum()%2 == 0) {
            if (receivedMessage.get(0).getVal() < curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
        // odd stage, kill node if bigger
        else {
            if (receivedMessage.get(0).getVal() > curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
//        System.out.println("Node " + curVal +" survived? " + (minMaxState == MinMaxState.ACTIVE));
    }

    public void sendMessageIfActiveBeforeCheckAndSurvive() {
        // if not leader, then minmax survive test
        if (minMaxState == MinMaxState.ACTIVE && receivedMessage.get(0).getStageNum() >= stageNumber) {
            // if survived, send new message after incrementing stage # and updating current value
            minMaxSurvive();
            if (minMaxState == MinMaxState.ACTIVE) {
//                int formerValue = curVal;
//                System.out.println("Formerly " + formerValue + " becoming " + receivedMessage.get(0).getVal() + " and sending stage " + (stageNumber + 1) + " message to node " + rightNeighbor.curVal + " with status " + rightNeighbor.minMaxState);
                stageNumber++;
                curVal = receivedMessage.get(0).getVal();
                rightNeighbor.sendMessage(new Message(curVal, stageNumber));
//                System.out.println("Node with former val " + formerValue + " is now " + curVal + " with status " + minMaxState + " and right neighbor received message of " + rightNeighbor.receivedMessage.get(0).getVal());
            }
        }
    }

    public void checkAndSetIfLeader() {
        if (curVal == receivedMessage.get(0).getVal()) {
            System.out.println("--------Leader was elected " + curVal + " and had state: " + toString() + "----------");
            minMaxState = MinMaxState.LEADER;
            leaderNode = curVal;
            rightNeighbor.sendMessage(new Message(curVal, -1));
        }
    }

    public String toString() {
//        return "Stage number: " + stageNumber + ", Node value: " + curVal;
//        return curVal.toString();
//        return curVal + " right to " + rightNeighbor.curVal.toString() + " has messages " + receivedMessage + " and thinks the leader is " + leaderNode + " has status " + minMaxState;
        return curVal + " stage " + stageNumber + " has messages " + receivedMessage + " has status " + minMaxState;
//        return curVal + " thinks the leader is " + leaderNode;
//        return curVal + " has message with " + receivedMessage.get(0).getVal() + " and stage " + stageNumber + " and state of " + minMaxState.toString() + " and thinks leader is " + leaderNode;
    }
}

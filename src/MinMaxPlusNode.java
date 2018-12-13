import java.util.ArrayList;
import java.util.List;

public class MinMaxPlusNode  {

    // -1 as stage number is flag that we are done and that the message received contains the leader node number
    private int stageNumber = 1;
    // -1 indicates hasn't died ever yet
    private int lastDeathStageNumber = -1;
    private Integer originalVal;
    private Integer curVal;
    // - idnicates hasn't died ever yet
    private Integer lastDeathCurVal = -1;
    private List<MessagePlus> receivedMessage;
    private MinMaxPlusNode rightNeighbor;
    private MinMaxState minMaxState = MinMaxState.ACTIVE;

    private Integer leaderNode;

    private enum MinMaxState {
        ACTIVE,
        PASSIVE,
        LEADER,
        NOTIFIED
        ;
    }

    public MinMaxPlusNode (int value) {
        originalVal = value;
        curVal = value;
        receivedMessage = new ArrayList<>();
    }

    public void linkRightNeighborNode(MinMaxPlusNode node) {
        rightNeighbor = node;
    }

    public void sendMessage(MessagePlus receivedMessage) {
        if (receivedMessage.getStageNum()%2 == 0) {
            receivedMessage.decrementDistance();
        } else {
            // odd stage travels as far as it wants
            receivedMessage.setDistance(-1);
        }
        this.receivedMessage.add(receivedMessage);
        MinMaxPlus.messageCount++;
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

    public MinMaxPlusNode getRightNeighbor() {
        return rightNeighbor;
    }

    public List<MessagePlus> getReceivedMessage() {
        return receivedMessage;
    }

    public void action() {
        if (!receivedMessage.isEmpty()) {
            MessagePlus recievedMessagePlus = receivedMessage.get(0);
            switch(minMaxState) {
                case ACTIVE:
                    if (recievedMessagePlus.getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = recievedMessagePlus.getVal();
                        System.out.println("Node " + originalVal + " has been notified");
                        rightNeighbor.sendMessage(receivedMessage.get(0));
                    } else {
                        checkAndSetIfLeader();
                        sendMessageIfActiveBeforeCheckAndSurvive();
                    }
                    break;
                case LEADER:
                    // check if notify stage has completed
                    if (receivedMessage.get(0).getStageNum() != -1) {
                        // begin notification
                        rightNeighbor.sendMessage(new MessagePlus(curVal, -1));
//                        System.out.println("Leader setting left neighbor " + rightNeighbor.curVal + "'s received message to " + rightNeighbor.receivedMessage.get(0).getVal() + " stage " + rightNeighbor.receivedMessage.get(0).getStageNum() + " and has status of " + rightNeighbor.minMaxState);

                    } else {
                        MinMaxPlus.done = true;
//                        System.out.println("Leader " + leaderNode + " is done");
                    }
                    break;
                case PASSIVE:
                    if (recievedMessagePlus.getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = recievedMessagePlus.getVal();
                        System.out.println("Node " + originalVal + " has been notified");
                        rightNeighbor.sendMessage(receivedMessage.get(0));
                    }
                    // revive node on even stage stop, else skip over
                    else if ((recievedMessagePlus.getStageNum()%2 == 0 && recievedMessagePlus.getDistance() == 0) ||
                    // revive node on odd stage encounter if it was defeated in the stage prior to the message and message is smaller than last defeated value
                            (recievedMessagePlus.getDistance() == -1 && lastDeathStageNumber == (recievedMessagePlus.stageNum - 1) && recievedMessagePlus.val < lastDeathCurVal)) {
//                        System.out.println("Node " + curVal + " reviving as " +  recievedMessagePlus.val + " at stage " + stageNumber);
                        minMaxState = MinMaxState.ACTIVE;
                        stageNumber = recievedMessagePlus.stageNum;
                        curVal = recievedMessagePlus.val;
                        stageNumber++;
                        MessagePlus messageToSend = new MessagePlus(curVal, stageNumber);
                        setDistance(messageToSend);
                        rightNeighbor.sendMessage(messageToSend);
                    } else {
                        rightNeighbor.sendMessage(recievedMessagePlus);
                    }

                    break;
            }
            // message consumed
            receivedMessage.remove(0);
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

    public void checkAndSetIfLeader() {
        if (curVal == receivedMessage.get(0).getVal()) {
            System.out.println("--------Leader was elected " + curVal + " and had state: " + toString() + "----------");
            minMaxState = MinMaxState.LEADER;
            leaderNode = curVal;
            rightNeighbor.sendMessage(new MessagePlus(curVal, -1));
        }
    }

    public void sendMessageIfActiveBeforeCheckAndSurvive() {
        // if not leader, then minmax survive test
        MessagePlus messagePlus = receivedMessage.get(0);
        if (minMaxState == MinMaxState.ACTIVE && messagePlus.getStageNum() >= stageNumber) {
            // kill node if it receives a future stage message, store death info, forward message
            if (messagePlus.getStageNum() > stageNumber) {
                minMaxState = MinMaxState.PASSIVE;
                lastDeathCurVal = curVal;
                lastDeathStageNumber = stageNumber;
                rightNeighbor.sendMessage(messagePlus);
            } else {
                // if survived, send new message after incrementing stage # and updating current value
                minMaxSurvive();
                if (minMaxState == MinMaxState.ACTIVE) {
//                    int formerValue = curVal;
//                    System.out.println("Formerly " + formerValue + " becoming " + receivedMessage.get(0).getVal() + " and sending stage " + (stageNumber + 1) + " message to node " + rightNeighbor.curVal + " with status " + rightNeighbor.minMaxState);
                    stageNumber++;
                    curVal = messagePlus.getVal();
                    MessagePlus messageToSend = new MessagePlus(curVal, stageNumber);
                    setDistance(messageToSend);
                    rightNeighbor.sendMessage(messageToSend);
//                System.out.println("Node with former val " + formerValue + " is now " + curVal + " with status " + minMaxState + " and right neighbor received message of " + rightNeighbor.receivedMessage.get(0).getVal());
                }

            }
        }
    }

    public void setDistance(MessagePlus messageToSend) {
        // if even stage, should set distance following fib sequence
        if (messageToSend.getStageNum()%2 == 0) {
            messageToSend.setDistance(MinMaxHelper.fibonacci(stageNumber));
        } else {
            messageToSend.setDistance(-1);
        }
    }

    public String toString() {
        return curVal + " at stage " + stageNumber + " has messages " + receivedMessage + " has status " + minMaxState;
    }
}

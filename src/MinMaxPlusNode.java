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
        if (MinMaxHelper.isEvenStageMessage(receivedMessage)) {
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
//            MessagePlus receivedMessagePlus = receivedMessage.get(0);
            MessagePlus receivedMessagePlus = receivedMessage.get(receivedMessage.size()-1);
            switch(minMaxState) {
                case ACTIVE:
                    if (receivedMessagePlus.getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = receivedMessagePlus.getVal();
//                        System.out.println("Node " + originalVal + " has been notified");
                        rightNeighbor.sendMessage(receivedMessagePlus);
                    } else {
                        checkAndSetIfLeader(receivedMessagePlus);
                        sendMessageIfActiveBeforeCheckAndSurvive(receivedMessagePlus);
                    }
                    break;
                case LEADER:
                    // check if notify stage has completed
                    if (receivedMessagePlus.getStageNum() == -1) {
                        MinMaxPlus.done = true;
                    }
                    break;
                case PASSIVE:
                    if (receivedMessagePlus.getStageNum() == -1) {
                        minMaxState = MinMaxState.NOTIFIED;
                        leaderNode = receivedMessagePlus.getVal();
//                        System.out.println("Node " + originalVal + " has been notified");
                        rightNeighbor.sendMessage(receivedMessagePlus);
                    }
                    else if (shouldRevive(receivedMessagePlus))
                    {
//                        System.out.println("Node " + curVal + " reviving as " +  recievedMessagePlus.val + " at stage " + stageNumber);
                        minMaxState = MinMaxState.ACTIVE;
                        stageNumber = receivedMessagePlus.stageNum + 1;
                        curVal = receivedMessagePlus.val;
                        MessagePlus messageToSend = new MessagePlus(curVal, stageNumber);
                        setDistance(messageToSend);
                        rightNeighbor.sendMessage(messageToSend);
                    } else {
                        rightNeighbor.sendMessage(receivedMessagePlus);
                    }

                    break;
            }
            // message consumed
            receivedMessage.remove(receivedMessagePlus);
        }
    }

    // TODO Was last working on checking if revival was being done properly. Suspicious that we do ressurection at up to stage 12 for size 300.
   // At least the nodes are reviving on the right disntace conditions.DOublec check the odd cases to see if we are ressurecting the nodes only if we
   // have a smaller value than the received messages and an old stage one less than the message stage. Mayeb the queued messages are screwing
   // things up too due to the pacified node just consuming it's stack of messages? MAybe clear message queue once pacified.
    public boolean shouldRevive(MessagePlus receivedMessagePlus) {
       // revive node on even stage stop, else skip over
       boolean shouldRevive = false;
       if (MinMaxHelper.isEvenStageMessage(receivedMessagePlus)) {
          shouldRevive = receivedMessagePlus.getDistance() == 0;
       } else {
          // revive node on odd stage encounter if it was defeated in the stage prior to the message and message is smaller than last defeated value
          shouldRevive = (lastDeathStageNumber == (receivedMessagePlus.stageNum - 1)) && lastDeathCurVal < receivedMessagePlus.val;
       }
       if (shouldRevive) {
          System.out.println("Node " + curVal + " received message " + receivedMessagePlus + " and should revive" );
       }
       return shouldRevive;
    }

    public void minMaxSurvive(MessagePlus receivedMessagePlus) {
//        System.out.println("Node " + curVal + " recieved message " + receivedMessage.get(0).getVal() + " stage " + receivedMessage.get(0).getStageNum());
        // even stage, kill node if smaller
        if (MinMaxHelper.isEvenStageMessage(receivedMessagePlus)) {
            if (receivedMessagePlus.getVal() < curVal) {
                minMaxState = MinMaxState.PASSIVE;
               lastDeathCurVal = curVal;
               lastDeathStageNumber = stageNumber;
            }
        }
        // odd stage, kill node if bigger
        else {
            if (receivedMessagePlus.getVal() > curVal) {
                minMaxState = MinMaxState.PASSIVE;
               lastDeathCurVal = curVal;
               lastDeathStageNumber = stageNumber;
            }
        }
//        System.out.println("Node " + curVal +" survived? " + (minMaxState == MinMaxState.ACTIVE));
    }

    public void checkAndSetIfLeader(MessagePlus messagePlus) {
        if (curVal == messagePlus.getVal()) {
            System.out.println("--------Leader was elected " + curVal + " and had state: " + toString() + "----------");
            minMaxState = MinMaxState.LEADER;
            leaderNode = curVal;
            rightNeighbor.sendMessage(new MessagePlus(curVal, -1));
        }
    }

    public void sendMessageIfActiveBeforeCheckAndSurvive(MessagePlus messagePlus) {
        // if not leader, then minmax survive test
        if (minMaxState == MinMaxState.ACTIVE
              && messagePlus.getStageNum() >= stageNumber
        ) {
            // kill node if it receives a future stage message, store death info, forward message
            if (messagePlus.getStageNum() > stageNumber) {
                minMaxState = MinMaxState.PASSIVE;
                lastDeathCurVal = curVal;
                lastDeathStageNumber = stageNumber;
                rightNeighbor.sendMessage(messagePlus);
            } else {
                // if survived, send new message after incrementing stage # and updating current value
                minMaxSurvive(messagePlus);
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
        if (MinMaxHelper.isEvenStageMessage(messageToSend)) {
            messageToSend.setDistance(MinMaxHelper.fibonacci(stageNumber));
        } else {
            messageToSend.setDistance(-1);
        }
    }

    public String toString() {
        return "Node: " + curVal + " S" + stageNumber + " M" + receivedMessage + " status: " + minMaxState.toString().substring(0,0);
    }
}

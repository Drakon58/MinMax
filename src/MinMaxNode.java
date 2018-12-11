public class MinMaxNode {
    // -1 as stage number is flag that we are done and that the message received contains the leader node number
    private int stageNumber = 1;
    private Integer originalVal;
    private Integer curVal;
    private Message recievedMessage;
    private MinMaxNode rightNeighbor;
    private MinMaxState minMaxState = MinMaxState.ACTIVE;

    private Integer leaderNode;

    public MinMaxNode (int value) {
        originalVal = value;
        curVal = value;
    }

    public void linkNode(MinMaxNode node) {
        rightNeighbor = node;
    }

    private enum MinMaxState {
        ACTIVE,
        PASSIVE,
        LEADER,
        NOTIFIED
        ;
    }

    public void action() {

        switch(minMaxState) {
            case ACTIVE:
                checkAndSetIfLeader();
                // if not leader, then minmax survive test
                if (minMaxState == MinMaxState.ACTIVE) {
                    // if passed, send new message after incrementing stage # and updated current value
                    minMaxSurvive(recievedMessage.getVal());
                    if (minMaxState == MinMaxState.ACTIVE) {
                        stageNumber++;
                        curVal = recievedMessage.getVal();
                        rightNeighbor.recievedMessage = new Message(curVal, stageNumber);
                    }

                }
                break;
            case LEADER:
                // check if notify stage has completed
                if (recievedMessage.getStageNum() != -1) {
                    // begin notification
                    rightNeighbor.recievedMessage = new Message(curVal, -1);
                } else {
                    MinMax.done = true;
                }
                break;
            // otherwise a pacified node, so just pass on the message you received
            case PASSIVE:
                if (recievedMessage.getStageNum() == -1) {
                    minMaxState = MinMaxState.NOTIFIED;
                }
                rightNeighbor.recievedMessage = recievedMessage;
                break;
        }

        // TODO REMOVE THIS OLD CODE BELOW
        if (minMaxState == MinMaxState.ACTIVE) {

            else {
            }
        }
        // notify stage
        else if (minMaxState == MinMaxState.LEADER) {
        }
    }

    public void minMaxSurvive(int recievedMessageVal) {
        // even, kill node if smaller
        if (stageNumber%2 == 0) {
            if (recievedMessageVal < curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
        // odd, kill node if bigger
        else {
            if (recievedMessageVal > curVal) {
                minMaxState = MinMaxState.PASSIVE;
            }
        }
    }

    public void checkAndSetIfLeader() {
        if (curVal == recievedMessage.getVal()) {
            minMaxState = MinMaxState.LEADER;
            leaderNode = curVal;
        }
    }
}

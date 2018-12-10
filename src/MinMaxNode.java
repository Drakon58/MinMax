public class MinMaxNode {
    private int stageNumber = 1;
    private Integer originalVal;
    private Integer curVal;
    private Message recievedMessage;
    private MinMaxNode rightNeighbor;
    private MinMaxState minMaxState = MinMaxState.ACTIVE;

    private MinMaxNode leaderNode;

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
        if (minMaxState == MinMaxState.ACTIVE) {
            minMaxSurvive(recievedMessage.getVal());
            // if pass minmax survive test, send new message after incrementing stage # and updated current value
            if (minMaxState == MinMaxState.ACTIVE) {
                stageNumber++;
                curVal = recievedMessage.getVal();
                rightNeighbor.recievedMessage = new Message(curVal, stageNumber);
            }
            // otherwise a pacified node, so just pass on the message you received
            else {
                rightNeighbor.recievedMessage = recievedMessage;
            }
        }
    }

    public void minMaxSurvive(int recievedMessageVal) {
        // even, kill node if smaller
        if (stageNumber%2 == 0) {
            if (recievedMessageVal < curVal) {

            }
        }
        // odd, kill node if bigger
        else {
            if (recievedMessageVal > curVal) {

            }
        }
    }

    public void isLeader() {
        if (curVal == recievedMessage.getVal()) {

        }
    }
}

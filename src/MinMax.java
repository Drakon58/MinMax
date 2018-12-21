import java.util.*;

public class MinMax {

    public static final int TIMES_TO_RUN_ALG = 20;

    public static List<Integer> uniqueNodeListOrder;
    public static List<MinMaxNode> nodelist;
    public static List<Double> timings;
    public static List<Integer> messagesTotal;
    public static double startTime;
    public static double endTime;
    public static int messageCount = 0;
    public static boolean done = false;
    // TODO restore this once finished debugging issue
//    public static List<Integer> listSizes = new ArrayList<>(Arrays.asList(300, 500, 1000, 2000));
    public static List<Integer> listSizes = new ArrayList<>(Arrays.asList(5));
    public static List<MinMaxNode> testList;

    public static void main(String[] args) {
        testList = new ArrayList<>();
        MinMaxHelper.generateTestList(testList);
        // debounce start timer and vars
        MinMaxHelper.generateRandomizedUniqueNodes(1);
        startTimer();
        for(Integer listSize: listSizes) {
            timings = new ArrayList<>();
            messagesTotal = new ArrayList<>();
            System.out.println("List size: " + listSize);
//            System.out.println(nodelist);
            // TODO restore this once finished debugging issue
//            for (int i = 0; i < TIMES_TO_RUN_ALG; i++) {
                uniqueNodeListOrder = MinMaxHelper.generateRandomizedUniqueNodes(listSize);
                nodelist = new ArrayList<>();
                setupNodeList(listSize);
                startTimer();
                // TODO restore this once finished debugging issue
                runAlg(listSize);
                stopTimer();
                done = false;
                timings.add(getTimeElapsed());
                messagesTotal.add(messageCount);
                messageCount = 0;
                // TODO restore this once finished debugging issue
//            }
            System.out.println("Timings were: \n" + timings);
            System.out.println("Message counts were: \n" + messagesTotal);
            System.out.println("Avg time was: " + MinMaxHelper.avgTime(timings));
            System.out.println("Avg messages was: " + MinMaxHelper.avgMessages(messagesTotal));
        }
    }

    public static void setupNodeList(int listSize) {
        for (int i = 0; i < listSize; i++) {
            nodelist.add(new MinMaxNode(uniqueNodeListOrder.get(i)));
        }
        for (int i = 0; i < listSize - 1; i++) {
            MinMaxNode currentMinMaxNode = nodelist.get(i);
            MinMaxNode rightMinMaxNode = nodelist.get(i+1);
            currentMinMaxNode.linkRightNeighborNode(rightMinMaxNode);
            // set first round of messages
            rightMinMaxNode.sendMessage(new Message(currentMinMaxNode.getCurVal(), currentMinMaxNode.getStageNumber()));
        }
            // link the last node generated
        MinMaxNode firstNode = nodelist.get(0);
        MinMaxNode lastNode = nodelist.get(nodelist.size()-1);
        lastNode.linkRightNeighborNode(firstNode);
//        System.out.println("First node properly linked? " + (nodelist.get(0).getRightNeighbor() == nodelist.get(nodelist.size()-1)));
        // set last node's message to be from first node
        firstNode.sendMessage(new Message(lastNode.getCurVal(), lastNode.getStageNumber()));
//        System.out.println("Last node properly messaged? " + (nodelist.get(nodelist.size()-1).getReceivedMessage().getVal() == nodelist.get(0).getCurVal()));

    }

    public static void runAlg(int listSize) {
//        System.out.println("----INITIAL ROUND START----");
                System.out.println(testList);
        // TODO restore this once finished debugging issue
//        for (MinMaxNode node : nodelist) {
        for (MinMaxNode node : testList) {
//            System.out.println("Doing initial setup for node " + node.getCurVal());
            node.sendMessageIfActiveBeforeCheckAndSurvive();
            node.getReceivedMessage().remove(0);
        }
//        System.out.println("----INITIAL ROUND END----");
//        System.out.println(nodelist);
        int currentNodeIndex = 0;
        while (!done) {
            System.out.println(testList);
            // TODO restore this once finished debugging issue
//            nodelist.get(currentNodeIndex).action();
            testList.get(currentNodeIndex).action();
            currentNodeIndex = (currentNodeIndex + 1)%listSize;
        }
        System.out.println(testList);
    }

    public static void startTimer() {
        System.out.println("Starting timer");
        startTime = System.nanoTime();
    }

    public static void stopTimer() {
        endTime = System.nanoTime();
        System.out.println("Ending timer");
    }

    public static double getTimeElapsed() {
        return ((endTime - startTime)/1000000);
    }

}

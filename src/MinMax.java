import java.util.*;

public class MinMax {

    public static List<Integer> uniqueNodeList;
    public static double startTime;
    public static double endTime;
    public static boolean done = false;
    public static int numDone = 0;
    public static List<Integer> listSizes = new ArrayList<>(Arrays.asList(300, 500, 1000, 2000));

    public static void main(String[] args) {
        // debounce start timer and vars
        generateRandomizedUniqueNodes(1);
        startTimer();
//        for(Integer listSize: listSizes) {
//            System.out.println("List size: " + listSize);
//            startTimer();
//            uniqueNodeList = generateRandomizedUniqueNodes(listSize);
//            for (int i = 0; i < listSize;i++) {
//                MinMaxNode minMaxNode = new MinMaxNode(uniqueNodeList.get(i).toString());
//                minMaxNode.start();
//            }
//            stopTimer();
//            System.out.println("Total time elapsed: " + getTimeElapsed());
//        }
        startTimer();
        uniqueNodeList = generateRandomizedUniqueNodes(100);
        for (int i = 0; i < 100;i++) {
            MinMaxNode minMaxNode = new MinMaxNode(uniqueNodeList.get(i).toString());
            minMaxNode.start();
        }
        while (numDone != 100) {
            System.out.println("STILL WAITING");
        }
        stopTimer();
        System.out.println("Total time elapsed: " + getTimeElapsed());
    }

    public static List<Integer> generateRandomizedUniqueNodes(int size) {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            integerList.add(i);
        }
        Collections.shuffle(integerList);
        return integerList;
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

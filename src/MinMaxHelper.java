import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinMaxHelper {

    public static List<Integer> generateRandomizedUniqueNodes(int size) {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            integerList.add(i);
        }
        Collections.shuffle(integerList);
        return integerList;
    }

    public static Double avgTime(List<Double> timings) {
        double avgTime = 0.0;
        for (Double timing: timings) {
            avgTime += timing;
        }
        return avgTime/timings.size();
    }

    public static int avgMessages(List<Integer> messages) {
        int avgMessage = 0;
        for (Integer message: messages) {
            avgMessage += message;
        }
        return avgMessage/messages.size();
    }

    public static int fibonacci(int n)  {
        if(n == 0)
            return 0;
        else if(n == 1)
            return 1;
        else
            return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void generateTestList(List<MinMaxNode> testList) {
        testList.add(new MinMaxNode(5));
        testList.add(new MinMaxNode(4));
        testList.add(new MinMaxNode(3));
        testList.add(new MinMaxNode(2));
        testList.add(new MinMaxNode(1));
    }
}

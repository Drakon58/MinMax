public class MessagePlus extends Message {

    private int distance = -1;

    public  MessagePlus(int val, int stageNum) {
        super(val, stageNum);
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void decrementDistance() {
        distance--;
    }

    public String toString() {
        return "Val : " + val + " stage " + stageNum + " distance " + distance;
    }

}

public class Message {

    protected int val;
    protected int stageNum;

    public Message(int val, int stageNum) {
        this.val = val;
        this.stageNum = stageNum;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getStageNum() {
        return stageNum;
    }

    public void setStageNum(int stageNum) {
        this.stageNum = stageNum;
    }

    public String toString() {
        return "Val : " + val + " stage " + stageNum;
    }
}

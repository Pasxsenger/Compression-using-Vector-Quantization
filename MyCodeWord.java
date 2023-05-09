public class MyCodeWord extends MyVector implements Comparable<MyCodeWord>{
    int vecCnt;
    MyVector errVec;

    MyCodeWord(int x, int y) {
        super(x, y);
        this.errVec = new MyVector(0, 0);
        this.vecCnt = 0;
    }

    public int diffCalculate(){
        if(vecCnt == 0)
            return -1;

        errVec.x = Math.round(((float)errVec.x / vecCnt));
        errVec.y = Math.round(((float)errVec.y / vecCnt));

        int diff = Math.abs(this.x - errVec.x) + Math.abs(this.y - errVec.y);
        this.x = errVec.x;
        this.y = errVec.y;
        vecCnt = 0;
        errVec.x = 0;
        errVec.y = 0;
        return diff;
    }

    @Override
    public int compareTo(MyCodeWord o) {
        return this.vecCnt - o.vecCnt;
    }

}
import java.util.ArrayList;

public class MyCodeWord2 extends MyVector2 implements Comparable<MyCodeWord2>{
    int vecCnt;
    MyVector2 errVec;

    MyCodeWord2(ArrayList<Integer> arrayList) {
        super(arrayList);
        int M = arrayList.size();
        ArrayList<Integer> zeros = new ArrayList<>();
        for(int i = 0; i < M; i++)
            zeros.add(0);
        this.errVec = new MyVector2(zeros);
        this.vecCnt = 0;
    }

    public int diffCalculate(){
        if(vecCnt == 0)
            return -1;

        int M = errVec.vecCoordinate.size(), diff = 0;
        for(int i = 0; i < M; i++) {
            errVec.vecCoordinate.set(i, Math.round(((float) errVec.vecCoordinate.get(i) / vecCnt)));
            diff += Math.abs(this.vecCoordinate.get(i) - errVec.vecCoordinate.get(i));
            this.vecCoordinate.set(i, errVec.vecCoordinate.get(i));
        }

        vecCnt = 0;
        for(int i = 0; i < M; i++)
            errVec.vecCoordinate.set(i, 0);

        return diff;
    }

    @Override
    public int compareTo(MyCodeWord2 cw) {
        return this.vecCnt - cw.vecCnt;
    }

    public static void main(String[] args) {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(191);
        temp.add(63);
        MyCodeWord2 myCW2 = new MyCodeWord2(temp);
        myCW2.vecCnt = 100;
        int diff = myCW2.diffCalculate();
        System.out.println(diff);
    }
}
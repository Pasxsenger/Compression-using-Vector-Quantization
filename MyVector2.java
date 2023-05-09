import java.util.ArrayList;

public class MyVector2{
    ArrayList<Integer> vecCoordinate;
    MyVector2(ArrayList<Integer> arrayList){
        this.vecCoordinate = arrayList;
    }

    @Override
    public String toString() {
        int M = vecCoordinate.size();
        StringBuilder vecStr = new StringBuilder("Vector->(");
        for(int i = 0; i < M; i++){
            vecStr.append(vecCoordinate.get(i));
            if(i < M-1)
                vecStr.append(", ");
        }
        vecStr.append(")");
        return vecStr.toString();
    }
}
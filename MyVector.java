public class MyVector{
    int x, y;
    MyVector(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return ("Vector-> (x: " + x + ", y: " + y + ")");
    }
}
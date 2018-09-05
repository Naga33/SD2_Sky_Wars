import java.io.Serializable;

public class Square implements Serializable{
    private int xPos;
    private int yPos;

    public Square(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    @Override
    public String toString() {
        return "Row: "+xPos+" Column: "+yPos;
    }

    @Override
    public boolean equals(Object o) {
        Square square = (Square) o;
        if(o == this){
            return true;
        }
        if(!(o instanceof Square)){
            return false;
        }
        if (o == null) {
            return false;
        }
        return Integer.compare(xPos, square.xPos) == 0
                && Integer.compare(yPos, square.yPos)==0;
    }
}

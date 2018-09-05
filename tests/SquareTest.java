import static org.junit.Assert.*;

public class SquareTest {
    @org.junit.Test
    public void getxPos() {
        Square s = new Square(0,0);
        int expected = 0;
        int actual = s.getxPos();
        assertTrue(expected==actual);
    }

    @org.junit.Test
    public void getyPos() {
        Square s = new Square(0,0);
        int expected = 0;
        int actual = s.getyPos();
        assertTrue(expected==actual);
    }

}
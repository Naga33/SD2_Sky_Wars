import static org.junit.Assert.*;
import org.junit.Test;
import java.util.HashMap;
import java.util.Random;

public class GridTest {
    @Test
    public void getRows() {
        Grid g1 = new Grid(true);
        int expected = 4;
        int actual = g1.getRows();

        assertTrue(expected == actual);
    }

    @Test
    public void getColumns() {
        Grid g1 = new Grid(true);
        int expected = 4;
        int actual = g1.getColumns();

        assertTrue(expected == actual);
    }

    @Test
    public void getMasterPosition(){
        Grid grid = new Grid(true);
        grid.randomStartPosition();
        Square expected = new Square(2,2);
        Square actual = grid.getMasterPosition();
        assertTrue(expected.equals(actual));
    }

    @Test
    public void setMasterPosition(){
        Grid grid = new Grid(true);
        grid.randomStartPosition();
        grid.setMasterPosition(new Square(0,0));
        Square actual = grid.getMasterPosition();
        Square expected = new Square(0,0);
        assertTrue(actual.equals(expected));
    }

    @Test
    public void getEnemyEnters(){
        Grid grid = new Grid(true);
        Boolean expected = false;
        Boolean actual = grid.getEnemyEnters();
        assertTrue(actual==expected);
    }

    @Test
    public void getEnemyShipPositions(){
        Grid grid = new Grid(true);
        HashMap<Ship, Square> expected = new HashMap<>();
        HashMap<Ship, Square> actual = grid.getEnemyShipPositions();
        assertTrue(actual.equals(expected));
    }

    @Test
    public void getMode(){
        Grid grid = new Grid(true);
        Boolean expected = false;
        Boolean actual = grid.getMode();
        assertTrue(actual==expected);
    }

    @Test
    public void randomStartPosition() {
        Grid grid = new Grid(true);
        grid.randomStartPosition();
        Square actual = grid.getMasterPosition();

        Square[][] testGrid = new Square[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                testGrid[i][j] = new Square(i,j);
            }
        }
        Random r = new Random();
        r.setSeed(1);
        int rowIndex = r.nextInt(4);
        r.setSeed(1);
        int columnIndex = r.nextInt(4);
        Square expected = testGrid[rowIndex][columnIndex];

        assertTrue(actual.equals(expected));
    }

    @Test
    public void moveMaster() {
        Grid grid = new Grid(true);
        grid.setMasterPosition(new Square(0,0));
        grid.moveMaster();
        Square actual = grid.getMasterPosition();
        System.out.println(actual);

        Square masterTestPos = new Square(0,0);
        Square newMasterPos;
        int[] neighbourRows = {0,1};
        int[] neighbourCols = {0,1};
        do{
            Random r = new Random();
            r.setSeed(1);
            int xIndex = r.nextInt(neighbourRows.length);
            r.setSeed(1);
            int yIndex = r.nextInt(neighbourCols.length);
            newMasterPos = new Square(neighbourRows[xIndex], neighbourCols[yIndex]);
        }while (newMasterPos == masterTestPos);

        Square expected = newMasterPos;
        System.out.println(expected);

        assertTrue(actual.equals(expected));


    }

    @Test
    public void enemyEnter() {
        Grid grid = new Grid(true);
        grid.enemyEnter();
        Boolean actual = grid.getEnemyEnters();

        Random r = new Random();
        r.setSeed(1);
        int enter = r.nextInt(3);
        Boolean expected = false;

        if (enter == 0){
            expected = true;
        }
        assertTrue(actual==expected);
    }

    @Test
    public void moveEnemies() {
        Grid grid = new Grid(true);
        HashMap<Ship, Square> enemies;
        Square originalTestPos = null;
        Square newTestPos;
        Square newEnemyPos = null;

        //put one enemy ship and position in Grid hashmap
        do {
            grid.enemyEnter();
        }while(grid.getEnemyShipPositions().isEmpty());
        //both hashmaps have one enemy ship and square of same position (0,0)

        //get original test position
        enemies = grid.getEnemyShipPositions();
        for (Ship ship:enemies.keySet()) {
            originalTestPos = enemies.get(ship);
        }

        //use moveEnemies() method
        grid.moveEnemies();

        //get new actual enemy position after method use
        HashMap<Ship, Square> actualEnemyHM = grid.getEnemyShipPositions();
        for (Ship ship:actualEnemyHM.keySet()) {
            newEnemyPos = actualEnemyHM.get(ship);
        }

        //test method:
        //only available moves from original position of 0,0
        int[] neighbourRows = {0,1};
        int[] neighbourCols = {0,1};

        //pick random index until it doesn't match original 0,0 position
        do{
            Random r = new Random();
            r.setSeed(1);
            int xIndex = r.nextInt(neighbourRows.length);
            r.setSeed(1);
            int yIndex = r.nextInt(neighbourCols.length);
            newTestPos = new Square(neighbourRows[xIndex], neighbourCols[yIndex]);
        }while (newTestPos.equals(originalTestPos));

        Square actual = newEnemyPos;
        Square expected = newTestPos;
        assertTrue(actual.equals(expected));
    }

    @Test
    public void changeMode() {
        Grid grid = new Grid(true);
        Boolean originalMode = grid.getMode();
        Boolean testMode = null;
        Boolean newMode = null;

        //use method
        grid.changeMode();
        newMode = grid.getMode();

        //test method
        if (originalMode){
            testMode = false;
        }
        else{
            testMode = true;
        }

        Boolean actual = newMode;
        Boolean expected = testMode;
        assertTrue(actual==expected);
    }

}
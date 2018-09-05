import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Grid implements Subject {
    private boolean newGame;
    private int rows = 4;
    private int columns = 4;
    private Square[][] grid = new Square[rows][columns];
    private Square masterPosition;
    private boolean masterDies = false;
    private boolean enemyEnters = false;
    private boolean enemyDies = false;
    private boolean mode;
    private HashMap<Ship,Square> enemyShipPositions = new HashMap<>();
    private final String FILE_NAME = "game.data";
    private ArrayList<Observer> observers;

    //constructor
    public Grid(boolean newGame){
        this.newGame = newGame;
        observers = new ArrayList<>();
        mode = false;
        load();
    }

    private void load(){
        if (this.newGame){
            if (this.FILE_NAME != null){
                initialise();
            }
            else{
                JOptionPane.showMessageDialog(null, "No previous game on file!");
                System.exit(0);
            }
        }
        else{
            deserialize();
        }
    }

    private void initialise(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.grid[i][j] = new Square(i,j);
            }
        }
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public Square getMasterPosition(){
        return this.masterPosition;
    }

    public void setMasterPosition(Square square){
        this.masterPosition = square;
    }

    public boolean getEnemyEnters(){
        return this.enemyEnters;
    }

    public HashMap<Ship, Square> getEnemyShipPositions() {
        return enemyShipPositions;
    }

    public Boolean getMode(){
        return this.mode;
    }

    public void randomStartPosition(){
        Random r = new Random();
        //r.setSeed(1);
        int rowIndex = r.nextInt(rows);
        //r.setSeed(1);
        int columnIndex = r.nextInt(columns);
        this.masterPosition = this.grid[rowIndex][columnIndex];
        notifyObservers();
        serialize();
    }

    public void moveMaster(){
        ArrayList<Integer> neighbourRows = findNeighbourRows(this.masterPosition);
        ArrayList<Integer> neighbourColumns = findNeighbourColumns(this.masterPosition);

        int currentRow = this.masterPosition.getxPos();
        int currentCol = this.masterPosition.getyPos();
        int row;
        int column;
        Random r = new Random();

        do {
            //r.setSeed(1);
            int randomRowIndex = r.nextInt(neighbourRows.size());
            //r.setSeed(1);
            int randomColumnIndex = r.nextInt(neighbourColumns.size());
            row = neighbourRows.get(randomRowIndex);
            column = neighbourColumns.get(randomColumnIndex);
        }while(row == currentRow && column == currentCol);

        this.masterPosition = this.grid[row][column];
        notifyObservers();
        serialize();
    }

    private ArrayList<Integer> findNeighbourColumns(Square currentSquare){
        ArrayList<Integer> neighbours = new ArrayList<>();
        int yIndex = currentSquare.getyPos();
        int downCol = yIndex-1;
        int upCol = yIndex+1;

        neighbours.add(yIndex);
        for (int i = 0; i < columns; i++) {
            if(yIndex == i){
                if(i>0){
                    neighbours.add(downCol);
                }
                if(i<3){
                    neighbours.add(upCol);
                }
            }
        }
        return neighbours;
    }

    private ArrayList<Integer> findNeighbourRows(Square currentSquare){
        ArrayList<Integer> neighbours = new ArrayList<>();
        int xIndex = currentSquare.getxPos();
        int downRow = xIndex-1;
        int upRow = xIndex+1;

        neighbours.add(xIndex);
        for (int i = 0; i < rows; i++) {
            if(xIndex == i){
                if(i>0){
                    neighbours.add(downRow);
                }
                if(i<3){
                    neighbours.add(upRow);
                }
            }
        }
        return neighbours;
    }

    private int generateRandomNumber(){
        Random r = new Random();
        //r.setSeed(1);
        return r.nextInt(3);
    }

    private Ship generateRandomEnemy(){
        //factory pattern
        EnemyShipFactory shipFactory = new EnemyShipFactory();
        return shipFactory.makeEnemyShip(generateRandomNumber());
    }

    public void enemyEnter(){
        Square enemyStart = this.grid[0][0];
        int chance = generateRandomNumber();

        if(chance == 0){
            this.enemyEnters = true;
            this.enemyShipPositions.put(generateRandomEnemy(), enemyStart);
        }
        else{
            this.enemyEnters = false;
        }
        serialize();
        notifyObservers();
    }

    public void moveEnemies(){

        for (Ship ship :this.enemyShipPositions.keySet()) {
            Square pos = this.enemyShipPositions.get(ship);
            ArrayList<Integer> neighbourRows = findNeighbourRows(pos);
            ArrayList<Integer> neighbourCols = findNeighbourColumns(pos);

            int currentRow = pos.getxPos();
            int currentCol = pos.getyPos();
            int row;
            int column;
            Random r = new Random();

            do {
                //r.setSeed(1);
                int randomRowIndex = r.nextInt(neighbourRows.size());
                //r.setSeed(1);
                int randomColumnIndex = r.nextInt(neighbourCols.size());
                row = neighbourRows.get(randomRowIndex);
                column = neighbourCols.get(randomColumnIndex);
            }while(row == currentRow && column == currentCol);

            this.enemyShipPositions.put(ship, grid[row][column]);
            serialize();
            notifyObservers();
        }//end foreach
    }

    private boolean checkForCollision(){
        for (Square enemyPosition:this.enemyShipPositions.values()) {
            if(enemyPosition == this.masterPosition){
                return true;
            }
        }
        this.enemyDies = false;
        return false;
    }

    public void changeMode(){
        if (this.mode){
            this.mode = false;
        }
        else{
            this.mode = true;
        }
        serialize();
        notifyObservers();
    }

    public void collide(){
        if (this.mode){
            collisionDefense();
        }
        else{
            collisionOffense();
        }
    }

    private void collisionDefense(){
        if(checkForCollision()){
            HashMap<Ship, Square> enemyPositions = new HashMap<>();

            //check if any enemies on mastership position
            for(Ship enemy:this.enemyShipPositions.keySet()){
                Square enemyPosition = this.enemyShipPositions.get(enemy);

                if(enemyPosition == this.masterPosition){
                    //add enemies on same square as master to their own hashmap
                    enemyPositions.put(enemy, enemyPosition);
                }
            }

            //check how many enemies in same position as master to determine who dies
            if(enemyPositions.size() > 1){
                this.enemyDies = false;
                this.masterDies = true;
            }
            else{
                for (Ship enemy:enemyPositions.keySet()) {
                    this.enemyShipPositions.remove(enemy);
                }
                this.enemyDies = true;
            }
        }
        serialize();
        notifyObservers();
    }

    private void collisionOffense(){
        //need to serialise whether mode or defensive mode is on
        if(checkForCollision()){
            HashMap<Ship, Square> enemyPositions = new HashMap<>();

            //check if any enemies on master space
            for(Ship enemy:this.enemyShipPositions.keySet()){
                Square enemyPosition = this.enemyShipPositions.get(enemy);

                if(enemyPosition == this.masterPosition){
                    enemyPositions.put(enemy, enemyPosition);
                }
            }

            //check how many enemies in same position as master to determine who dies
            if(enemyPositions.size() > 2){
                this.masterDies = true;
            }
            else{
                for (Ship enemy:enemyPositions.keySet()) {
                    this.enemyShipPositions.remove(enemy);
                }
                this.enemyDies = true;
            }
        }
        serialize();
        notifyObservers();
    }

    public void gameEnd(){
        this.enemyShipPositions.clear();
        this.masterPosition = null;
    }

    private void serialize() {
        // create output stream
        try {
            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this.rows);
            oos.writeObject(this.columns);
            oos.writeObject(this.grid);
            oos.writeObject(this.masterPosition);
            oos.writeObject(this.masterDies);
            oos.writeObject(this.enemyEnters);
            oos.writeObject(this.enemyDies);
            oos.writeObject(this.mode);
            oos.writeObject(this.enemyShipPositions);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deserialize() {
        // create input stream
        try {
            FileInputStream fis = new FileInputStream(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            this.rows = (int) ois.readObject();
            this.columns = (int) ois.readObject();
            this.grid = (Square[][]) ois.readObject();
            this.masterPosition = (Square) ois.readObject();
            this.masterDies = (boolean) ois.readObject();
            this.enemyEnters = (boolean) ois.readObject();
            this.enemyDies = (boolean) ois.readObject();
            this.mode = (boolean) ois.readObject();
            this.enemyShipPositions = (HashMap<Ship, Square>) ois.readObject();
            ois.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        int index = this.observers.indexOf(o);
        this.observers.remove(index);
    }

    @Override
    public void notifyObservers() {
        for (Observer anObserver:this.observers) {
            anObserver.update(this.masterPosition, this.enemyShipPositions, this.masterDies, this.enemyEnters, this.enemyDies, this.mode);
        }
    }
}

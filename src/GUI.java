import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class GUI implements Observer, ActionListener {
    //declare observer variables
    private Grid theGrid;
    private Square masterPosition;
    private boolean masterDies;
    private boolean enemyEnters;
    private boolean enemyDies;
    private boolean mode;
    private HashMap<Ship, Square> enemyHM;

    //declare other variables
    private int labelSize = 160;
    private ImageIcon[] shipImages;
    private JFrame frame;
    private JPanel panel;
    private JButton moveBtn;
    private JButton toggleMode;
    private JButton newGameBtn;
    private JLabel[][] gridLabels = new JLabel[4][4];
    private JLabel enemyCount;
    private JLabel announceMasterDies;
    private JLabel announceEnemyDies;
    private JLabel announceEnemyEnters;
    private JLabel currentMode;
    private final String MASTER_PATH = "/ship_images/Star Destroyer.G03.shadowless.2k.png";
    private final String SHOOTER_PATH = "/ship_images/Luke's X-Wing Starfighter.G03.shadowless.2k.png";
    private final String STAR_PATH = "/ship_images/Rebel Starfighter (U-Wing) Flying.G03.shadowless.2k.png";
    private final String CRUISER_PATH = "/ship_images/Rebel Starfighter (U-Wing) Landed.G03.shadowless.2k.png";
    private final String[] SHIP_PATHS = {MASTER_PATH, SHOOTER_PATH, STAR_PATH, CRUISER_PATH};

    //constructor
    public GUI(Grid aSubject){
        this.theGrid = aSubject;
        aSubject.addObserver(this);
        loadImageIcons();
        defineGUIElements();
        //no layout manager
        panel.setLayout(null);
        setEmptyGrid();
        setActionListeners();
        setPositions();
        addButtonsToPanel();
        addLabelsToPanel();
        addElementsToFrame();

        //if deserializing, sets gui to previous state
        theGrid.notifyObservers();
    }//end constructor

    public void setEmptyGrid(){
        for (int i = 0; i < theGrid.getRows(); i++) {
            for (int j = 0; j < theGrid.getColumns(); j++) {
                this.gridLabels[i][j] = new JLabel();
                this.gridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
            }
        }
        setLabelBounds();
    }

    public void defineGUIElements(){
        frame = new JFrame();
        panel = new JPanel();
        moveBtn = new JButton("MOVE");
        toggleMode = new JButton("OFFENCE/DEFENSE MODE");
        enemyCount = new JLabel("ENEMIES: 0");
        announceMasterDies = new JLabel();
        announceEnemyDies = new JLabel();
        announceEnemyEnters = new JLabel();
        currentMode = new JLabel("CURRENT MODE: DEFENSIVE");
        newGameBtn = new JButton("ENTER MASTER");
    }

    private void setActionListeners(){
        this.newGameBtn.addActionListener(this);
        this.moveBtn.addActionListener(this);
        this.toggleMode.addActionListener(this);
    }

    private void setPositions(){
        //buttons
        newGameBtn.setBounds(190, 850, 250, 60);
        moveBtn.setBounds(445, 850, 100, 60);
        toggleMode.setBounds(550, 850, 250, 60);
        //labels
        enemyCount.setBounds(160, 120, 300, 60);
        announceMasterDies.setBounds(350, 30, 300, 60);
        announceEnemyEnters.setBounds(50,30, 300, 60);
        announceEnemyDies.setBounds(650, 30, 300, 60);
        currentMode.setBounds(500, 120, 300, 60);
        //set fonts
        enemyCount.setFont(new Font("Arial", Font.BOLD, 15));
        announceEnemyDies.setFont(new Font("Arial", Font.BOLD, 20));
        announceEnemyEnters.setFont(new Font("Arial", Font.BOLD, 20));
        announceMasterDies.setFont(new Font("Arial", Font.BOLD, 20));
        currentMode.setFont(new Font("Arial", Font.BOLD, 15));
        newGameBtn.setFont(new Font("Arial", Font.BOLD, 15));
        moveBtn.setFont(new Font("Arial", Font.BOLD, 15));
        toggleMode.setFont(new Font("Arial", Font.BOLD, 15));
        //center jlabel text
        enemyCount.setHorizontalAlignment(JLabel.CENTER);
        announceEnemyDies.setHorizontalAlignment(JLabel.CENTER);
        announceEnemyEnters.setHorizontalAlignment(JLabel.CENTER);
        announceMasterDies.setHorizontalAlignment(JLabel.CENTER);
        currentMode.setHorizontalAlignment(JLabel.CENTER);


    }

    private void refreshEnemyNumber(){
        this.enemyCount.setText("ENEMIES: "+enemyHM.size());
    }

    private void clearGrid(){
        //set all squares to no image icon
        for (int i = 0; i < theGrid.getRows(); i++) {
            for (int j = 0; j < theGrid.getColumns(); j++) {
                this.gridLabels[i][j].setIcon(null);
            }
        }
    }

    private void refreshShipPositions(){
        clearGrid();

        if (!this.enemyHM.isEmpty()){
            //set enemy ships icons
            for (Ship ship:this.enemyHM.keySet()) {
                Square position = this.enemyHM.get(ship);
                int xIndex = position.getxPos();
                int yIndex = position.getyPos();

                if(ship instanceof BattleCruiser){
                    this.gridLabels[xIndex][yIndex].setIcon(getImageIcon(3));
                }
                if(ship instanceof BattleStar){
                    this.gridLabels[xIndex][yIndex].setIcon(getImageIcon(2));
                }
                if (ship instanceof BattleShooter){
                    this.gridLabels[xIndex][yIndex].setIcon(getImageIcon(1));
                }
            }
        }

        if(this.masterPosition != null){
            //set master position
            int masterX = this.masterPosition.getxPos();
            int masterY = this.masterPosition.getyPos();
            this.gridLabels[masterX][masterY].setIcon(getImageIcon(0));
        }
    }

    private void setLabelBounds(){
        this.gridLabels[0][0].setBounds(200, 200, 150, 150);
        this.gridLabels[0][1].setBounds(350, 200, 150, 150);
        this.gridLabels[0][2].setBounds(500, 200, 150, 150);
        this.gridLabels[0][3].setBounds(650, 200, 150, 150);

        this.gridLabels[1][0].setBounds(200, 350, 150, 150);
        this.gridLabels[1][1].setBounds(350, 350, 150, 150);
        this.gridLabels[1][2].setBounds(500, 350, 150, 150);
        this.gridLabels[1][3].setBounds(650, 350, 150, 150);

        this.gridLabels[2][0].setBounds(200, 500, 150, 150);
        this.gridLabels[2][1].setBounds(350, 500, 150, 150);
        this.gridLabels[2][2].setBounds(500, 500, 150, 150);
        this.gridLabels[2][3].setBounds(650, 500, 150, 150);

        this.gridLabels[3][0].setBounds(200, 650, 150, 150);
        this.gridLabels[3][1].setBounds(350, 650, 150, 150);
        this.gridLabels[3][2].setBounds(500, 650, 150, 150);
        this.gridLabels[3][3].setBounds(650, 650, 150, 150);

    }

    private void loadImageIcons(){
        this.shipImages = new ImageIcon[this.SHIP_PATHS.length];

        for (int i = 0; i < shipImages.length; i++) {
            BufferedImage img = null;
            Image resize;
            ImageIcon png;

            //import ship images
            try {
                URL url = this.getClass().getResource(SHIP_PATHS[i]);
                img = ImageIO.read(url);
//                img = ImageIO.read(new File(SHIP_PATHS[i]));

            } catch (IOException e) {
                e.printStackTrace();
            }

            //resize png to fit label size
            resize = img.getScaledInstance(labelSize, labelSize, Image.SCALE_SMOOTH);
            png = new ImageIcon(resize);

            this.shipImages[i] = png;
        }
    }

    private ImageIcon getImageIcon(int index){
        return this.shipImages[index];
    }

    private void addButtonsToPanel(){
        panel.add(newGameBtn);
        panel.add(moveBtn);
        panel.add(toggleMode);
    }

    private void addLabelsToPanel(){
        for (int i = 0; i < theGrid.getRows(); i++) {
            for (int j = 0; j < theGrid.getColumns(); j++) {
                this.panel.add(this.gridLabels[i][j]);
            }
        }
        panel.add(enemyCount);
        panel.add(announceMasterDies);
        panel.add(announceEnemyDies);
        panel.add(announceEnemyEnters);
        panel.add(currentMode);
    }

    private void addElementsToFrame(){
        frame.add(panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setTitle("SKY WARS!");
        frame.setVisible(true);
    }

    private void announceEnemyDies(){
        if(this.enemyDies){
            this.announceEnemyDies.setText("ENEMY DIES!");
        }
        else{
            this.announceEnemyDies.setText("");
        }
    }

    private void announceEnemyEnters(){
        if(this.enemyEnters){
            this.announceEnemyEnters.setText("ENEMY ENTERS!");
        }
        else{
            this.announceEnemyEnters.setText("");
        }

    }

    private void announceMasterDies(){
        this.announceMasterDies.setText("MASTER DIES! GAME OVER!");
    }

    public void gameOver(){
        if(masterDies){
            announceMasterDies();
            clearGrid();
            theGrid.gameEnd();
            //System.exit(0);
        }
    }

    private void displayMode(){
        if (this.mode){
            this.currentMode.setText("CURRENT MODE: OFFENSIVE");
        }
        else{
            this.currentMode.setText("CURRENT MODE: DEFENSIVE");
        }
    }


    @Override
    public void update(Square masterPosition, HashMap<Ship, Square> enemyHM, boolean masterDies, boolean enemyEnters, boolean enemyDies, boolean mode) {
        this.masterPosition = masterPosition;
        this.enemyHM = enemyHM;
        this.masterDies = masterDies;
        this.enemyEnters = enemyEnters;
        this.enemyDies = enemyDies;
        this.mode = mode;
        displayMode();
        refreshShipPositions();
        refreshEnemyNumber();
        announceEnemyEnters();
        announceEnemyDies();
        gameOver();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newGameBtn){
            if(this.masterPosition == null && this.masterDies == false){
                theGrid.randomStartPosition();
            }
        }
        if(e.getSource() == moveBtn){
            if(this.masterPosition != null && this.masterDies == false){
                theGrid.moveMaster();
                theGrid.moveEnemies();
                theGrid.enemyEnter();
                theGrid.collide();
            }
        }
        if (e.getSource() == toggleMode){
            theGrid.changeMode();
        }
    }
}

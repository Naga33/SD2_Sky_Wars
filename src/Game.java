import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        Grid grid;
        int game = JOptionPane.showConfirmDialog(null, "Would you like to load the prevoius game?", "Sky Wars", JOptionPane.YES_NO_OPTION);

        if(game == JOptionPane.YES_OPTION){
            grid = new Grid(false);
        }
        else{
            grid = new Grid(true);
        }
        GUI gui = new GUI(grid);
    }
}

import java.util.HashMap;

public interface Observer {
    void update(Square masterPosition,
                HashMap<Ship, Square> enemyHM,
                boolean masterDies,
                boolean enemyEnters,
                boolean enemyDies,
                boolean mode);
}

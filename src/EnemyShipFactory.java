public class EnemyShipFactory {
    public Ship makeEnemyShip(int newShipType){

        if(newShipType == 0){
            return new BattleCruiser();
        }
        else if (newShipType == 1){
            return new BattleShooter();
        }
        else if (newShipType == 2){
            return new BattleStar();
        }
        else return null;
    }
}

public class Treasure extends Piece {
    Player player;
    int nTreasureFound = 0;
    boolean treasureFound;
    Treasure (int x, int y) {
        super(x, y);
        setTraversable(true);
    }   

    public int getTreasureFound(){
        if(player.hasTreasure == true){
            nTreasureFound++;
        }
        return nTreasureFound;
    }

    @Override
    public void walkIn(){
        Handler.onTreasureCollect();
    }
}

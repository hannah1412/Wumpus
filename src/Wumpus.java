public class Wumpus extends Piece{
    boolean isAlive;
    private static int wumpusKilled;


    Wumpus(int x, int y) {
        super(x, y);
        
        isAlive = true;
        // setKnown(true);
        setTraversable(true);
        setMovable(true);
        // setHazardous(true);
    }

    public static int wumpusKilled() {
        return wumpusKilled;
    }
    
    @Override
    public void walkIn(){
        if (isAlive) {
            Handler.onGameOver(Board.WUMPUSGAMEOVERMESSAGE);
        } else {
            Handler.givePlayerArrow();
        }
    }

    @Override
    public void hit() {
        isAlive = false;
        String message = "Your arrow hits a creature at " + getX() + " : " + getY();
        wumpusKilled++;
        Handler.hasHit(message);
    }
}

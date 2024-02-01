public class Pit extends Piece{
    boolean isAlive;


    Pit(int x, int y) {
        super(x, y);
        // setKnown(true);
        setTraversable(true);
        setMovable(true);
    } 

    @Override
    public void walkIn() {
        Handler.onGameOver(Board.PITGAMEOVERMESSAGE);
    }
}

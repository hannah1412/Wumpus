public class Bats extends Piece{
    boolean isAlive;


    Bats(int x, int y) {
        super(x, y);
        // setKnown(true);
        setTraversable(true);
        setMovable(true);
    }

    @Override
    public void walkIn(){
        //you will be moved to a random place
        Handler.onBats();
    }

    @Override
    public void hit() {
        Handler.onBats();
    }


}

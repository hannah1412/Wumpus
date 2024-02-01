public class BonusArrows extends Piece{
    boolean isAlive;

    BonusArrows(int x, int y){
        super(x, y);
        setTraversable(true);
        setMovable(true);
    }

    @Override
    public void walkIn(){
        Handler.givePlayerArrow();
    }
}

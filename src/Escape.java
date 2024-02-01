public class Escape extends Piece{
    Escape(int x, int y) {
        super(x,y);
        setTraversable(true);
    }

    @Override
    public void walkIn() {
        Handler.onEscape();
    }
}

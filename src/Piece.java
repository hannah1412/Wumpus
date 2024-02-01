public class Piece {
    private int x, y;
    private boolean isMovable = false;; 
    private boolean isTraversable = false;
    // private boolean isKnown = false; 
    // private boolean isHazardous = false;

    Piece() {
    }

    Piece(int x, int y) {
        setXY(x,y);
    }

    public void hit() {
        //meant to be overridden
    }

    public void walkIn(){
        //meant to be overridden for walk into the wumpus
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean isTraversable() {
        return isTraversable;
    }
    public void setTraversable(boolean isTraversable) {
        this.isTraversable = isTraversable;
    }

}
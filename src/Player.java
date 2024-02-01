public class Player extends Piece {
    public boolean hasTreasure;
    public int treaseFound;
    private int numberOfArrows;

    Player(int x, int y) {
        super(x, y);
        super.setTraversable(false);
        super.setMovable(true);
        numberOfArrows = 10;
    }

    public boolean hasTreasure() {
        return hasTreasure;
    }
 
    public void setTreasure(boolean treasure) {
        this.hasTreasure = treasure;
    }

    public void incrementCollectedTreasure(){
        treaseFound++;
    }

    public int getNumberCollectedTreasure(){
        return treaseFound;
    }
    // public int getRange() {
    //     return range;
    // }

    // public void setRange(int range) {
    //     this.range = range;
    // }

    public boolean removeArrow() {
        if (numberOfArrows < 1) {
            return false;
        }
        numberOfArrows--;
        return true;
    }

    public int getNumberOfArrows() {
        return numberOfArrows;
    }

    public void addArrow() {
        numberOfArrows++;
    }
}

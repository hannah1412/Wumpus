import java.util.Arrays;
import java.util.ArrayList;
public class Cell {

    //constants for the type field of the cell
    static final public String WALL = "wall";
    static final public String EMPTY = ".";
    static final public String WUMPUS = "Wumpus";
    static final public String SUPERBAT = "bat";
    static final public String PLAYER = "player";
    static final public String TREASURE = "treasure";
    static final public String PIT = "Pit";
    static final public String ESCAPE = "Escape";
    static final public String TREASUREPATH = "path";
    static final public String BONUSARROW = "bonus";

    private String type;
    private Piece piece;
    private ArrayList<String> hints;
    private boolean isVisited = false;

    Cell() {
        piece = new Piece();
        hints = new ArrayList<String>();
    }

    
    //places hints onto this cell
    public void placeHint(String hint) {
        hints.add(hint);
    }

    // clears all hints emitted by a piece. Doesn't clear permanent hints like the arrow hit notification hint.
    public void clearHints() {
        //clears only piece hints
        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<String> pieceHints = new ArrayList<String>(Arrays.asList(new String[] {Board.WUMPUSHINT, Board.ESCAPEHINT, Board.PITHINT, Board.BATHINT, Board.TREASUREHINT}));
        for (String hint : hints) {
            if (!pieceHints.contains(hint)) {
                temp.add(hint);
            }
        } 
        this.hints = temp;
    }

    //getters and setters
    public ArrayList<String> getHints() {
        return hints;
    }

    public Piece getPiece() {
        return piece;
    }
    
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    
    public String getType() {
        return type;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean value) {
        this.isVisited = value;
    }

    public void setType(String type) {
        this.type = type;
    }
}
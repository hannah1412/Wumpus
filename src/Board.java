import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiConsumer;

class Board {
    // constants for the hint messages
    public static final String WUMPUSHINT = "There is a stinky smell near here!";
    public static final String PITHINT = "I can feel a strong fresh breeze!";
    public static final String BATHINT = "Flapping wings alert! Careful or you will be pull away!";
    public static final String TREASUREHINT = "Oopsie daisy";
    public static final String ESCAPEHINT = "You smell freedom!";
    public static final String WINMESSAGE = "You have escaped with the cave's treasure";
    public static final String LOSEESCAPE = "You escape the Wumpus' lair in defeat without the treasure.";
    public static final String WUMPUSGAMEOVERMESSAGE = "You have met the devil! Congrats";
    public static final String PITGAMEOVERMESSAGE = "You are forever remembered for your bravery as jumping in the bottomless pit";

    private Cell[][] board;
    int treasureCount = 0;
    private int xLength;
    private int yLength;
    private double caveDensity; // this number represents the % of the map that is traversable
    private ArrayList<Piece> pieces;
    private int BATNUMBER = 5, PITNUMBER = 5, WUMPUSNUMBER = 5, TREASURENUMBER = 5, BONUSARROWNUMBER = 6;

    Board() {
    }

    Board(int x, int y, double caveDensity) {
        this.xLength = x;
        this.yLength = y;
        this.caveDensity = caveDensity;
        if (caveDensity > 1 || caveDensity < 0) {
            this.caveDensity = 0.5;
        }
        board = new Cell[yLength][xLength];
    }

    // setting the number of obstacles based on the choosen difficult level
    public int getPitNumber() {
        return PITNUMBER;
    }

    public void setPitNumber(int PITNUMBER) {
        this.PITNUMBER = PITNUMBER;
    }

    public int getWumpusNumber() {
        return WUMPUSNUMBER;
    }

    public void setWumpusNumber(int WUMPUSNUMBER) {
        this.WUMPUSNUMBER = WUMPUSNUMBER;
    }

    public int getBatNumber() {
        return BATNUMBER;
    }

    public void setBatNumber(int BATNUMBER) {
        this.BATNUMBER = BATNUMBER;
    }

    public int getTreasureNumber() {
        return TREASURENUMBER;
    }

    public void setTreasureNumber(int TREASURENUMBER) {
        this.TREASURENUMBER = TREASURENUMBER;
    }

    public int getBonusArrowNumber() {
        return BONUSARROWNUMBER;
    }

    public void setBonusArrowNumber(int BONUSARROWNUMBER) {
        this.BONUSARROWNUMBER = BONUSARROWNUMBER;
    }

    public void generate(GameGUI gui, int randomNumber) throws Exception {
        // generating the connecting cave
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                board[j][i] = new Cell();
                board[j][i].setType(Cell.WALL);
            }
        }
        Random rand = new Random(randomNumber);
        int startingX = rand.nextInt(xLength), startingY = rand.nextInt(yLength);

        getCell(startingX, startingY).setType(Cell.EMPTY);

        int numberOfSpaces = (int) Math.round(xLength * yLength * caveDensity);

        int currentX = startingX;
        int currentY = startingY;
        int nextX = startingX;
        int nextY = startingY;
        // while the number of spaces needed to be placed is not zero
        while (numberOfSpaces > 0) {
            ArrayList<Integer[]> availableDirs = new ArrayList<Integer[]>();

            // counts the number of adjacent walls
            plusIterate(currentX, currentY, (a, b) -> {
                if (getCell(a, b).getType().equals(Cell.WALL)) {
                    availableDirs.add(new Integer[] { a, b });
                }
            });

            if (availableDirs.size() != 0) {
                // if there is an available direction
                int dir = rand.nextInt(availableDirs.size());
                nextX = availableDirs.get(dir)[0];
                nextY = availableDirs.get(dir)[1];
                getCell(nextX, nextY).setType(Cell.EMPTY);
                numberOfSpaces--;
                currentY = nextY;
                currentX = nextX;
            } else {
                if (currentY != startingY || currentX != startingX) {
                    // return to starting point if no adjacent wall is found
                    currentY = startingY;
                    currentX = startingX;
                } else {
                    // find random coordinate and test if empty. if not it moves onto the next
                    // iteration, and this is done again.
                    int testX = rand.nextInt(xLength);
                    int testY = rand.nextInt(yLength);
                    if (getCell(testX, testY).getType().equals(Cell.EMPTY)) {
                        currentX = testX;
                        currentY = testY;
                    }
                }
            }

            gui.updateFrame(getArgBoard());
            Thread.sleep(2);
        }

        // place pieces
        pieces = new ArrayList<Piece>();

        // Initialise random piece positions
        placePiece(new Integer[] { startingX, startingY }, Cell.PLAYER);

        for (int i = 0; i < getWumpusNumber(); i++) {
            placePiece(generateRandomEmptySpace(rand), Cell.WUMPUS);
        }

        for (int i = 0; i < getBatNumber(); i++) {
            placePiece(generateRandomEmptySpace(rand), Cell.SUPERBAT);
        }

        for (int i = 0; i < getPitNumber(); i++) {
            placePiece(generateRandomEmptySpace(rand), Cell.PIT);
        }

        for (int i = 0; i < getTreasureNumber(); i++) {
            placePiece(generateRandomEmptySpace(rand), Cell.TREASURE);
        }

        placePiece(generateRandomEmptySpace(rand), Cell.ESCAPE);

        for (int i = 0; i < getBonusArrowNumber(); i++) {
            placePiece(generateRandomEmptySpace(rand), Cell.BONUSARROW);
        }

        getCell(startingX, startingY).setVisited(true);
    }

    public Integer findingDistance(int currentX, int currentY, int treasureX, int treasureY) {
        Integer distance = (((currentX - treasureX) * (currentX - treasureX))
                + ((currentY - treasureY) * (currentY - treasureY)));
        return distance;
    }

    public ArrayList<CustomPair> getPathToTreasure() throws Exception {
        ArrayList<CustomPair> result = new ArrayList<>();
        ArrayList<Piece> targets = new ArrayList<>(getArrayOfTreasure());

        // sorting the treasure by distance to the player
        Collections.sort(targets, new Comparator<Piece>() {
            @Override
            public int compare(Piece t1, Piece t2) {
                int distance1 = 0, distance2 = 0;
                try {
                    distance1 = manhattanDistance(new Node(t1.getX(), t1.getY()),
                            new Node(getPlayer().getX(), getPlayer().getY()));
                    distance2 = manhattanDistance(new Node(t2.getX(), t2.getY()),
                            new Node(getPlayer().getX(), getPlayer().getY()));

                } catch (Exception e) {
                    return 1;
                }

                return Integer.compare(distance1, distance2);
            }
        });

        if (targets.size() == 0) {
            targets.add(getEscape());
        }

        if (targets.size() != 0) {
            ArrayList<Node> temp = optimalPath(getPlayer().getX(), getPlayer().getY(), targets.get(0).getX(),
                    targets.get(0).getY());
            temp.forEach((Node n) -> {
                result.add(new CustomPair(n.x, n.y));
            });

        }
        return result;
    }

    public Integer[] generateRandomEmptySpace(Random rand) {
        // returns coordinates {x,y} of a random empty space on the board.
        Integer[] result = { 0, 0 };

        do {
            int randno = rand.nextInt(xLength * yLength);
            result[0] = randno % xLength;
            result[1] = randno / yLength;
        } while (getCell(result[0], result[1]).getType() != Cell.EMPTY);

        return result;
    }

    public boolean movePiece(int x0, int y0, int x1, int y1) {
        Cell original = getCell(x0, y0);
        Cell destination = getCell(x1, y1);

        original.setVisited(true);
        if (original.getType().equals(Cell.WALL) || destination.getType().equals(Cell.WALL)) {
            // blocks moving into and moving walls
            return false;
        }

        // destination validity
        if (!destination.getType().equals(Cell.EMPTY) && !destination.getPiece().isTraversable()) {

            destination.getPiece().isTraversable();
            return false;
        }

        // checks moving body validity
        if (original.getPiece().isMovable()) {
            // call walkIn on the piece
            if (destination.getPiece() != null) {
                destination.getPiece().walkIn();
                pieces.remove(destination.getPiece());
            }
            // move the piece
            destination.setPiece(original.getPiece());
            destination.setType(original.getType());
            destination.getPiece().setXY(Math.floorMod(x1, xLength), Math.floorMod(y1, yLength));
            original.setPiece(null);
            original.setType(Cell.EMPTY);
            if (destination.getType() == Cell.PLAYER) {
                destination.setVisited(true);
            }
        }

        return true;
    }

    public void shootArrow(int x1, int y1, int x2, int y2) {

        Integer[] hit = checkPath(x1, y1, x2, y2);

        System.out.println("Arrow hit: " + hit[0] + ", " + hit[1]);
        if (getCell(hit[0], hit[1]).getPiece() != null) {
            getCell(hit[0], hit[1]).getPiece().hit();
        }
    }

    // from my (sms31) CS1002 chess practical
    // modified to check if the straight line path is clear and return the
    // coordinates of the first obstruction.
    public Integer[] checkPath(int x1, int y1, int x2, int y2) {
        Integer[] hitCoords = { x2, y2 };

        // I have a slight suspicion that this is overcomplicated, and might not be the
        // best solution.
        // Increment X,Y determine the direction that the target square is from the
        // initial square.
        double incrementX = 0;
        double incrementY = 0;

        if (x1 != x2)
            incrementX = (x2 - x1);
        if (y1 != y2)
            incrementY = (y2 - y1);
        incrementX = incrementX / (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
        incrementY = incrementY / (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));

        // the algorithm starts one square in the direction of the target and checks if
        // the square is empty, before incrementing i,j with incrementx or increment y
        // respectively.
        // Each step the while loop moves closer to the target, until it is on the
        // target. If a square is not empty it returns false to indicate that the path
        // is not valid.
        double i = x1 + incrementX;
        double j = y1 + incrementY;
        while ((i != x2) || (j != y2)) {
            int xGrid = (int) Math.round(i);
            int yGrid = (int) Math.round(j);
            if (!getCell(xGrid, yGrid).getType().equals(Cell.EMPTY)) {
                return new Integer[] { xGrid, yGrid };
            }

            // getCell(xGrid, yGrid).debug_highlight = true;

            if (i != x2) {
                i = i + incrementX;
            }
            if (j != y2) {
                j = j + incrementY;
            }
        }
        return hitCoords;
    }

    public void placePiece(Integer[] xy, String type) {
        int x = xy[0];
        int y = xy[1];
        Piece p;
        // places piece by intitialising an object of that type
        switch (type) {
            case Cell.WUMPUS:
                p = new Wumpus(x, y);
                getCell(x, y).setType(Cell.WUMPUS);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.PLAYER:
                p = new Player(x, y);
                getCell(x, y).setType(Cell.PLAYER);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.TREASURE:
                p = new Treasure(x, y);
                getCell(x, y).setType(Cell.TREASURE);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.SUPERBAT:
                p = new Bats(x, y);
                getCell(x, y).setType(Cell.SUPERBAT);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.PIT:
                p = new Pit(x, y);
                getCell(x, y).setType(Cell.PIT);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.ESCAPE:
                p = new Escape(x, y);
                getCell(x, y).setType(Cell.ESCAPE);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
            case Cell.BONUSARROW:
                p = new BonusArrows(x, y);
                getCell(x, y).setType(Cell.BONUSARROW);
                getCell(x, y).setPiece(p);
                pieces.add(p);
                break;
        }
    }

    public void updateHints() {
        // clear old hints
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {
                getCell(i, j).clearHints();
            }
        }

        ArrayList<CustomPair> piecesLocation = new ArrayList<>();
        // place new hints
        for (Piece p : pieces) {
            int x = p.getX();
            int y = p.getY();

            // this is definitely over engineered, but I think its cool.
            // plusIterate(x, y, (a, b) -> {
            // getCell(a, b).placeHint(getCell(x, y).getType());
            // });

            String hintMessage = "";
            switch (getCell(x, y).getType()) {
                case Cell.WUMPUS:
                    hintMessage = Board.WUMPUSHINT;
                    break;
                case Cell.PIT:
                    hintMessage = Board.PITHINT;
                    break;
                // case Cell.SUPERBAT:
                // hintMessage = Board.BATHINT;
                // break;
                case Cell.TREASURE:
                    hintMessage = Board.TREASUREHINT;
                    break;
                case Cell.ESCAPE:
                    hintMessage = Board.ESCAPEHINT;
                    break;
            }

            if (!hintMessage.equals("")) {
                piecesLocation = iterate(x, y);
                parseInHints(hintMessage, piecesLocation);

            }
        }
    }

    public void parseInHints(String hints, ArrayList<CustomPair> pieceLocation) {
        for (CustomPair pair : pieceLocation) {
            getCell(pair.getKey(), pair.getValue()).placeHint(hints);
            getCell(pair.getKey(), pair.getValue()).getHints();
        }
    }

    public ArrayList<CustomPair> iterate(int x, int y) {
        Integer[] index = { 1, -1 };
        ArrayList<CustomPair> positions = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            positions.add(new CustomPair(x + index[i], y));
            positions.add(new CustomPair(x, y + index[i]));
        }
        return positions;
    }

    // executes func above and below the given coordinates in a plus fashion.
    // I understand that this is very overengineered, but I really like using
    // lambdas and I can't help to hide this pretty ugly loop.
    public static void plusIterate(int x, int y, BiConsumer<Integer, Integer> func) {
        Integer[] temp = { 1, -1 };
        for (int i = 0; i < 2; i++) {
            func.accept(x + temp[i], y);
            func.accept(x, y + temp[i]);
        }
    }

    // Contains a $ separated String per square in the piece that describes the
    // current board state
    public String[][] getArgBoard() {
        String[][] argBoard = new String[yLength][xLength];
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {

                if (getCell(i, j).getType() == Cell.WALL) {

                    argBoard[j][i] = board[j][i].getType();

                    argBoard[j][i] += "$" + getCell(i, j).isVisited();

                } else if (getCell(i, j).isVisited() || getCell(i, j).getType().equals(Cell.PLAYER)) {
                    argBoard[j][i] = board[j][i].getType();
                    ArrayList<String> hints = board[j][i].getHints();

                    argBoard[j][i] += "$" + getCell(i, j).isVisited();

                    for (int k = 0; k < hints.size(); k++) {
                        argBoard[j][i] += "$" + hints.get(k);
                    }
                } else {
                    argBoard[j][i] = Cell.EMPTY;
                    argBoard[j][i] += "$" + getCell(i, j).isVisited();
                }
            }
        }

        return argBoard;
    }

    // A* pathing
    public ArrayList<Node> optimalPath(int xi, int yi, int xf, int yf) {
        // Astar path finding implementation made using [1]
        // No code was copied, but the algorithm was taken from the given reference.

        // closed list
        ArrayList<Node> closedList = new ArrayList<>();

        // open list
        ArrayList<Node> openList = new ArrayList<>();

        // find the arrayList of safely traversible spaces.
        ArrayList<Node> emptySpaces = new ArrayList<>();
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                if (getCell(i, j).getType().equals(Cell.EMPTY) || getCell(i, j).getType().equals(Cell.TREASURE)
                        || getCell(i, j).getType().equals(Cell.ESCAPE)) {
                    emptySpaces.add(new Node(i, j));
                }
            }
        }

        Node starting = new Node(xi, yi);
        Node destination = new Node(xf, yf);
        Node end = starting;

        starting.g = 0;
        openList.add(starting);
        Node current = starting;
        while (!current.equals(destination) || openList.size() != 0) {

            // checks if openList is empty
            if (openList.size() == 0) {
                break;
            }

            // gets the minimum f value node from the open List
            current = Collections.min(openList,
                    Comparator.comparing(Node::getF));

            // moves it to closed list
            openList.remove(current);
            closedList.add(current);

            // this copy is made to bypass restrictions around accessing state from lambda
            // functions
            final Node currentCopy = current;

            // iterate in a plus pattern around the current node
            toroidalPlusIterate(current.x, current.y, xLength, yLength, (a, b) -> {
                Node neighbor = new Node(a, b);
                if (!closedList.contains(neighbor) && emptySpaces.contains(neighbor)) {

                    if (openList.contains(neighbor)) {
                        // the neighbour is in the open list, check if this path to the neighbor is
                        // shorter than the previously calculated path to the neighbor.
                        // if the path is shorter, change the parent
                        if (currentCopy.g + 1 < neighbor.g) {
                            neighbor.parent = currentCopy;
                        }

                    } else {
                        // calculate the f,g,h and add to openList.
                        openList.add(neighbor);
                        neighbor.parent = currentCopy;

                        neighbor.h = manhattanDistance(neighbor, destination);
                        neighbor.g = currentCopy.g + 1;
                        neighbor.f = neighbor.h + neighbor.g;
                    }
                }
            });

            // check if n is in the closed List
            // .contains leads to bugs
            for (Node n : closedList) {
                if (n.x == xf && n.y == yf) {
                    end = n;
                    break;
                }
            }

        }
        // iterate through the parents from the destination, to find the optimal path
        ArrayList<Node> res = new ArrayList<>();
        Node iter = end;
        while (iter.parent != null) {
            res.add(iter);
            iter = iter.parent;
        }

        return res;
    }

    private static int manhattanDistance(Node i, Node f) {
        // distance heuristic for h value
        return Math.abs(f.x - i.x) + Math.abs(f.y - i.y);
    }

    public static void toroidalPlusIterate(int x, int y, int xmod, int ymod, BiConsumer<Integer, Integer> func) {
        Integer[] temp = { 1, -1 };
        for (int i = 0; i < 2; i++) {
            func.accept(Math.floorMod(x + temp[i], xmod), Math.floorMod(y, ymod));
            func.accept(Math.floorMod(x, xmod), Math.floorMod(y + temp[i], ymod));
        }
    }

    private class Node {
        // stores the necessary information for a cell for A* pathing.
        Node parent;
        int x;
        int y;
        int f = -1;
        int g;
        int h;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getF() {
            return f;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Node) {
                Node n = (Node) o;
                if (n.x == this.x && n.y == this.y) {
                    return true;
                }
            }
            return false;
        }

    }

    // debug functions
    public char[][] getCharBoard() {
        char[][] charBoard = new char[yLength][xLength];
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                charBoard[j][i] = board[j][i].getType().charAt(0);

                if (board[j][i].getHints().size() != 0) {
                    charBoard[j][i] = (char) (board[j][i].getHints().size() + '0');
                }
            }
        }
        return charBoard;
    }

    public void printCharBoard(char[][] charB) {
        for (int i = 0; i < charB.length; i++) {
            for (int j = 0; j < charB[i].length; j++) {
                System.out.print(charB[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printHintBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getHints().size());
            }
            System.out.println();
        }
        System.out.println();

    }

    // returns a Map of coordinates not containing a wall
    public ArrayList<CustomPair> excludeWall() {
        ArrayList<CustomPair> result = new ArrayList<CustomPair>();

        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                if (!getCell(i, j).getType().equals(Cell.WALL)) {
                    result.add(new CustomPair(i, j));
                }
            }

        }
        return result;
    }

    // getters
    public Player getPlayer() throws Exception {
        if (pieces.get(0) instanceof Player) {
            return (Player) pieces.get(0);
        }
        throw new Exception("Invalid board setup: Player missing");
    }

    public Escape getEscape() throws Exception {
        for (Piece p : pieces) {
            if (p instanceof Escape) {
                return (Escape) p;
            }
        }
        throw new Exception("There was no escape. Error");
    }

    public Cell getCell(int x, int y) {
        return board[Math.floorMod(y, yLength)][Math.floorMod(x, xLength)];
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }

    public ArrayList<Treasure> getArrayOfTreasure() {
        ArrayList<Treasure> array = new ArrayList<Treasure>();
        for (Piece p : pieces) {
            if (p instanceof Treasure) {
                array.add((Treasure) p);
            }
        }
        return array;
    }

    public double getCaveDensity() {
        return caveDensity;
    }

}

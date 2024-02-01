import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Handler {
    // A queue that stores all the events that are to be executed. This can be added
    // to asynchronously, allowing for actionListen and keyListener threads to
    // interact
    public static ArrayBlockingQueue<String> eventQueue;
    // A log of the events
    public ArrayList<String> eventList;
    private boolean running = true;
    private Integer randomNumber;
    private Random random;
    private Board board;
    private GameGUI gui;
    private int xLength, yLength;
    private double caveDensity;

    public static void main(String[] args) {
        try {
            // use command line arguments to set initial board state.
            int x = 30;
            int y = 30;
            double cave = 0.5;

            if (args.length == 2) {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
            }
            if (args.length == 3) {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                cave = Double.parseDouble(args[2]);
            }

            Handler h = new Handler(x, y, cave);
            h.run();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    Handler(int x, int y, double caveDensity) throws Exception {
        eventQueue = new ArrayBlockingQueue<String>(100);
        eventList = new ArrayList<String>();

        // sets board size
        xLength = x;
        yLength = y;
        this.caveDensity = caveDensity;

        // Initialise gui and generate random board. with random seed
        gui = new GameGUI(xLength, yLength);
        board = new Board(xLength, yLength, caveDensity);
        random = new Random();
        randomNumber = random.nextInt();
        board.generate(gui, randomNumber);
    }

    public void run() throws Exception {

        // update graphics and hints
        gui.updateFrame(board.getArgBoard());
        board.updateHints();

        while (running) {
            // Handles a single event from the eventQueue
            handleEvent();
            // updates graphics and hints
            board.updateHints();
            String[][] currentArgBoard = board.getArgBoard();
            gui.updateFrame(currentArgBoard);

            while (!running) {
                String event = eventQueue.poll();
                if (event != null && event.equals("resetGame")) {
                    parseEvent(event, board.getPlayer());
                }
            }
        }
    }

    public void handleEvent() throws Exception {
        Player player = board.getPlayer();

        // polls event from blocking queue
        String event = eventQueue.poll();

        // parse event if queue isn't empty
        if (event != null) {
            parseEvent(event, player);
        }
    }

    public void parseEvent(String event, Player player) throws Exception {
        // The events are Strings of the form "word,arg1,arg2,..." where word indicates
        // the type of event
        String[] eventArr = event.split(",");
        // switch based on the 'word'
        switch (eventArr[0]) {
            case "move":
                // move the player to absolute coordinates. unused in the game, but used for
                // debugging
                board.movePiece(player.getX(), player.getY(), Integer.parseInt(eventArr[1]),
                        Integer.parseInt(eventArr[2]));
                break;
            case "moveOffset":
                // move the player as an offset from the player's current coordinates
                board.movePiece(player.getX(), player.getY(), player.getX() + Integer.parseInt(eventArr[1]),
                        player.getY() + Integer.parseInt(eventArr[2]));
                break;
            case "moveWumpus":
                // randomly move the wumpus 1 distance away from its original position
                Integer[] temp = { -1, 1 };
                int x = random.nextInt(2);
                int y = random.nextInt(2);
                int wumpusX = Integer.parseInt(eventArr[1]), wumpusY = Integer.parseInt(eventArr[2]);
                board.movePiece(wumpusX, wumpusY, wumpusX + temp[x], wumpusY + temp[y]);
                break;
            case "shoot":
                // shoots at the square with (arg1,arg2) coordinates
                if (player.removeArrow()) {
                    board.shootArrow(player.getX(), player.getY(), Integer.parseInt(eventArr[1]),
                            Integer.parseInt(eventArr[2]));

                    gui.changeArrowCount(player.getNumberOfArrows());
                    gui.changeWumpusCount(Wumpus.wumpusKilled());
                    // iterate around the arrow's hit location to see if the wumpus is present
                    // if present move wumpus
                    Board.plusIterate(Integer.parseInt(eventArr[1]), Integer.parseInt(eventArr[2]), (xtemp, ytemp) -> {
                        if (board.getCell(xtemp, ytemp).getType().equals(Cell.WUMPUS)) {
                            onWumpusMiss(xtemp, ytemp);
                        }
                    });
                }
                break;
            case "addArrow":
                player.addArrow();
                gui.changeArrowCount(player.getNumberOfArrows());
                break;
            case "gameOver":
                // end the game
                String score = "" + (player.getNumberCollectedTreasure() + Wumpus.wumpusKilled());

                gui.gameOver(eventArr[1] + " " + "Score: " + score);
                this.running = false;
                break;
            case "treasure":
                // give the player treasure
                player.setTreasure(true);
                player.incrementCollectedTreasure();
                gui.changeTreasureCount(player.getNumberCollectedTreasure());
                break;
            case "fly":
                // Send the player to a random non-wall space on the map
                ArrayList<CustomPair> excluded = board.excludeWall();
                int i = 0;
                Integer number = random.nextInt(excluded.size());
                for (CustomPair pair : excluded) {
                    if (i == number) {
                        board.movePiece(player.getX(), player.getY(), pair.getKey(), pair.getValue());
                    }
                    i++;
                }
                break;
            case "hit":
                // places the hint for an arrow hit
                board.getCell(player.getX(), player.getY()).placeHint(eventArr[1]);
                break;
            case "resetGame":
                this.running = true;
                // new random generator for new seed
                Random rand = new Random();
                randomNumber = rand.nextInt();
                // regenerate board with new seed
                // gui.setUpBoard();
                board.generate(gui, randomNumber);
                gui.clearAlert();
                eventList.clear();
                // gui.clearAlert();
                break;
            case "escape":
                // ends the game if escape is reached
                if (player.hasTreasure()) {
                    Handler.onGameOver(Board.WINMESSAGE);
                } else {
                    // the game continue if walks in exit without treasures
                    Handler.onGameOver(Board.LOSEESCAPE);
                }
                break;
            case "loadBoard":
                // loads the baord from file
                loadFromFile(eventArr[1], player);
                break;
            case "saveBoard":
                // saves the board to a file
                saveToFile(eventArr[1], eventList, randomNumber);
                break;
            case "easy":
                board.setBatNumber(2);
                board.setPitNumber(2);
                board.setWumpusNumber(2);
                board.setTreasureNumber(5);
                Handler.resetGame();
                break;
            case "medium":
                board.setBatNumber(5);
                board.setPitNumber(5);
                board.setWumpusNumber(5);
                board.setTreasureNumber(6);

                Handler.resetGame();
                break;
            case "hard":
                board.setBatNumber(18);
                board.setPitNumber(15);
                board.setWumpusNumber(18);
                board.setTreasureNumber(9);

                Handler.resetGame();
                break;
            case "hint":
                // gets the coordinates with the cells that lie in the path to nearest treasure
                ArrayList<CustomPair> routes = board.getPathToTreasure();
                for (CustomPair route : routes) {
                    // marks them as visited to highlight them and make them visible.
                    board.getCell(route.getKey(), route.getValue()).setVisited(true);
                }
                break;
        }
        // debug
        System.out.println(event);

        // update hints for the baord
        gui.addHints(board.getCell(player.getX(), player.getY()).getHints());

        // keeps track of the game's events
        eventList.add(event);

    }

    // Methods for adding events to the event queue
    public static void resetGame() {
        eventQueue.add("resetGame");
    }

    public static void savedBoard() {
        eventQueue.add("savedBoard");
    }

    public static void onTreasureCollect() {
        eventQueue.add("treasure");
    }

    public static void onGameOver(String reason) {
        eventQueue.add("gameOver" + "," + reason);
    }

    public static void onClick(int x, int y) {
        eventQueue.add("shoot" + "," + x + "," + y);
    }

    public static void easyLevel() {
        eventQueue.add("easy");
    }

    public static void mediumLevel() {
        eventQueue.add("medium");
    }

    public static void hardLevel() {
        eventQueue.add("hard");
    }

    public static void hintButton() {
        eventQueue.add("hint");
    }

    public static void onBats() {
        eventQueue.add("fly");
    }

    public static void onKey(int x, int y) {
        // destination coordinates
        eventQueue.add("moveOffset" + "," + x + "," + y);
    }

    public static void onWumpusMiss(int x, int y) {
        // (x,y) wumpus coodinates
        eventQueue.add("moveWumpus" + "," + x + "," + y);
    }

    public static void hasHit(String message) {
        eventQueue.add("hit" + "," + message);
    }

    public static void givePlayerArrow() {
        eventQueue.add("addArrow");
    }

    public static void onEscape() {
        eventQueue.add("escape");
    }

    public static void saveBoard(String fileName) throws Exception {
        eventQueue.add("saveBoard" + "," + fileName);
    }

    public static void loadBoard(String fileName) throws Exception {
        eventQueue.add("loadBoard" + "," + fileName);
    }

    // getters
    public String[][] getArgBoard() {
        return board.getArgBoard();
    }

    public GameGUI getGameGUI() {
        return gui;
    }

    public Board getBoard() {
        return board;
    }

    public Integer[] getPlayerCoords() throws Exception {
        return new Integer[] { board.getPlayer().getX(), board.getPlayer().getY() };
    }

    public boolean getRunning() {
        return running;
    }

    public void setXY(int x, int y) {
        this.xLength = x;
        this.yLength = y;
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }

    // loading and saving for files
    private void loadFromFile(String fileName, Player p) throws Exception {
        // the file stores starting parameters and random seed for the board starting
        // states, and the event list.
        Scanner sc = new Scanner(new File(fileName));

        // loads the starting parameterss
        String line = sc.nextLine();
        String[] firstLine = line.split(" ");
        Integer randomSeed = Integer.parseInt(firstLine[0]);
        Double caveDensity = Double.parseDouble(firstLine[1]);
        Integer xLength = Integer.parseInt(firstLine[2]);
        Integer yLength = Integer.parseInt(firstLine[3]);

        ArrayList<String> newEventList = new ArrayList<String>();

        // loads the list of events
        while (sc.hasNextLine()) {
            newEventList.add(sc.nextLine());
        }

        board = new Board(xLength, yLength, caveDensity);
        board.generate(gui, randomSeed);

        // parses the events
        for (String event : newEventList) {
            parseEvent(event, p);
        }
    }

    private void saveToFile(String fileName, ArrayList<String> eventList, Integer randomNumber)
            throws IOException {
        // writes the starting parameters to the file's first line
        FileWriter write = new FileWriter(fileName);
        write.write(randomNumber.toString() + " " + board.getCaveDensity() + " " + board.getXLength() + " "
                + board.getYLength());
        write.write('\n');
        // writes the list of events.
        for (String event : eventList) {
            write.write(event + "\n");
        }
        write.close();
    }
}

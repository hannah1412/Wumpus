import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Arrays;

public class AI extends Handler {
    // This AI is an attempt at the Wave-function collapse algorithm.

    private final double caveDensity;
    private String[][] visBoard;
    private Integer[] player;
    private String[][] currentArgBoard;
    final private ArrayList<String> friendlyHints = new ArrayList<>(
            Arrays.asList(new String[] { "empty", Board.TREASUREHINT, Board.BATHINT }));

    public static void main(String[] args) {
        try {
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

            AI ai = new AI(x, y, cave);
            ai.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AI(int x, int y, double caveDensity) throws Exception {
        super(x, y, caveDensity);
        this.caveDensity = caveDensity;
        visBoard = new String[getYLength()][getXLength()];
    }

    @Override
    public void run() throws Exception {
        GameGUI gui = getGameGUI();
        Board board = getBoard();

        currentArgBoard = board.getArgBoard();
        gui.updateFrame(currentArgBoard);
        board.updateHints();

        this.startUp(currentArgBoard);

        while (this.getRunning()) {
            handleEvent();
            board.updateHints();
            currentArgBoard = board.getArgBoard();

            gui.updateFrame(currentArgBoard);
            this.update(currentArgBoard);
            this.printCurrentVisibilityBoard();

            if (AI.eventQueue.size() == 0) {
                // only does a move if the event queue is empty
                this.move();

            }
        }
        while (!this.getRunning()) {
            String event = eventQueue.poll();
            if (event != null && event.equals("resetGame")) {
                parseEvent(event, board.getPlayer());
            }
        }
    }

    public void startUp(String[][] argBoard) {
        // setup the board to mark walls
        for (int i = 0; i < getXLength(); i++) {
            for (int j = 0; j < getYLength(); j++) {
                String str = argBoard[j][i].split("\\$")[0];
                if (str.equals(Cell.WALL) || str.equals(Cell.PLAYER)) {
                    visBoard[j][i] = (str);
                } else {
                    visBoard[j][i] = ("unknown");
                }
            }
        }
    }

    public void update(String[][] argBoard) throws Exception {
        player = getPlayerCoords();

        String str = currentArgBoard[player[1]][player[0]];

        String[] strarr = str.split("\\$");
        // iterate in a plus pattern around the player to update the visboard
        for (int i = -1; i <= +1; i += 2) {
            if (strarr.length > 2) {
                for (int k = 2; k < strarr.length; k++) {
                    // this can be improved with a priority system.
                    if (getToroidal(visBoard, player[0], player[1] + i).equals("unknown")) {
                        setToroidal(visBoard, player[0], player[1] + i, strarr[k]);
                    }
                    if (getToroidal(visBoard, player[0] + i, player[1]).equals("unknown")) {
                        setToroidal(visBoard, player[0] + i, player[1], strarr[k]);
                    }
                }
            } else if (strarr.length == 2) {
                // this is for the case when the square has no hints
                if (!getToroidal(visBoard, player[0], player[1] + i).equals(Cell.WALL)) {
                    setToroidal(visBoard, player[0], player[1] + i, "empty");
                }
                if (!getToroidal(visBoard, player[0] + i, player[1]).equals(Cell.WALL)) {
                    setToroidal(visBoard, player[0] + i, player[1], "empty");
                }

            }
        }
    }

    public void move() throws Exception {
        ArrayList<Integer[]> availableDirs = new ArrayList<>();

        // iterate in a plus pattern around the player to determine safe routes.
        for (int i = -1; i <= +1; i += 2) {
            if (friendlyHints.contains(getToroidal(visBoard, player[0], player[1] + i))) {
                availableDirs.add(new Integer[] { player[0], player[1] + i });
            }
            if (friendlyHints.contains(getToroidal(visBoard, player[0] + i, player[1]))) {
                availableDirs.add(new Integer[] { player[0] + i, player[1] });
            }
        }

        // choose an unvisited safe route, if none are found choose a visited safe route.
        Random rand = new Random();
        if (availableDirs.size() > 0) {
            // filters the available directions to cancel out visited ones
            ArrayList<Integer[]> unvisitedDirs = new ArrayList<>(availableDirs.stream().filter((Integer[] x) -> {
                return getToroidal(currentArgBoard, x[0], x[1]).split("\\$")[1].equals("false");
            }).collect(Collectors.toList()));

            if (unvisitedDirs.size() > 0) {
                // execute the move
                int temp = rand.nextInt(availableDirs.size());
                Integer[] move = availableDirs.get(temp);

                Integer[] tempDir = { move[0] - player[0], move[1] - player[1] };
                AI.onKey(tempDir[0], tempDir[1]);
            } else {
                // execute the move
                int temp = rand.nextInt(availableDirs.size());
                Integer[] move = availableDirs.get(temp);

                Integer[] tempDir = { move[0] - player[0], move[1] - player[1] };
                AI.onKey(tempDir[0], tempDir[1]);
            }
        }

        Thread.sleep(10);

    }

    public void printCurrentVisibilityBoard() {
        for (int i = 0; i < getXLength(); i++) {
            for (int j = 0; j < getYLength(); j++) {
                if (getToroidal(visBoard, i, j).equals(Cell.PLAYER)) {
                    System.out.print("P");
                } else {
                    System.out.print(getToroidal(visBoard, i, j).charAt(0));
                }
            }
            System.out.println();
        }

        System.out.println();
    }

    // helper methods for accessing the board toroidally.
    public String getToroidal(String[][] board, int x, int y) {
        return board[Math.floorMod(y, getYLength())][Math.floorMod(x, getXLength())];
    }

    public void setToroidal(String[][] board, int x, int y, String str) {
        board[Math.floorMod(y, getYLength())][Math.floorMod(x, getXLength())] = str;
    }
}
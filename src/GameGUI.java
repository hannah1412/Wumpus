import java.util.ArrayList;
import java.util.Optional;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameGUI {
    static Board gameBoard;
    private int board_size_x, board_size_y;
    private JPanel hintPanel;
    private JPanel alertPanel;

    private JLabel treasureCount = new JLabel("Treasure found: 0 \tArrows: Full \tWumpus Killed: 0");
    private static JFrame frame;
    private static JPanel board;
    private ArrayList<JButton> buttons;


    public GameGUI() {
        buttons = new ArrayList<JButton>();
        setUpFrame();
        setUpBoard();
    }

    public GameGUI(int x, int y) {
        this.board_size_x = x;
        this.board_size_y = y;
        buttons = new ArrayList<JButton>();
        setUpFrame();
        setUpBoard();
        setUpMenu();
        frame.setVisible(true);
    }

    public void setUpFrame() {
        frame = new JFrame();
        frame.setTitle("Wumpus");
        frame.setSize(new Dimension(800, 800));
        frame.setBackground(Color.orange);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setDefaultLookAndFeelDecorated(true);
    }

    public void setUpBoard() {

        if (board != null) {
            frame.remove(board);
            board.removeAll();
            buttons.clear();
        }

        board = new JPanel();

        // display hints if there is any
        hintPanel = new JPanel(new GridLayout(0, 1));
        hintPanel.setBackground(Color.WHITE);
        hintPanel.setVisible(true);
        // hintPanel.setBounds(1, 1, 2, 2);
        // hintPanel.setSize(2, 2);
        // hintPanel.setPreferredSize(new Dimension(200,100));
        // hintPanel.add(new JLabel("HINTS", JLabel.RIGHT));

        // alert panel
        alertPanel = new JPanel(new GridLayout(1, 1));
        alertPanel.setBackground(Color.WHITE);
        // alertPanel.setVisible(true);
        // alertPanel.setSize(200, 100);
        // alertPanel.setPreferredSize(new Dimension(10, 50));

        board.setLayout(new GridLayout(board_size_y, board_size_x));
        board.setBackground(Color.MAGENTA);
        board.setVisible(true);
        // gameBoard = new Board(x,y);

        for (int col = 0; col < board_size_y; col++) {
            for (int row = 0; row < board_size_x; row++) {
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setOpaque(true);
                button.setSize((int) board_size_y / 2, (int) board_size_x / 2);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                button.setVisible(true);

                board.setFocusable(true);
                board.requestFocusInWindow();
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String[] coordinates = e.getActionCommand().split(",");
                        // for(String c : coordinates){
                        // System.out.println(c);
                        // }
                        // get coordinates from the board
                        // gameBoard.getCell(Integer.parseInt(coordinates[0]),
                        // Integer.parseInt(coordinates[1]));
                        // System.out.println("Row: " + coordinates[0] + "Col: " + coordinates[1]);
                        int x = Integer.parseInt(coordinates[0]), y = Integer.parseInt(coordinates[1]);
                        Handler.onClick(x, y);
                    }
                });
                button.setActionCommand(row + "," + col);
                board.add(button);
                buttons.add(button);
            }
        }

        frame.add(board);

        // frame.add(hintPanel, BorderLayout.SOUTH);
        // frame.add(alertPanel, BorderLayout.NORTH);
        frame.add(hintPanel, BorderLayout.NORTH);
        // frame.add(alertPanel, BorderLayout.SOUTH);

        board.addKeyListener(new CustomKeyListener());
    }

    public void setUpMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        // add hint button - suggest directions for player
        ImageIcon hintIcon = new ImageIcon("../hint.png");
        Image image = hintIcon.getImage();
        Image newImage = image.getScaledInstance(10, 10, java.awt.Image.SCALE_SMOOTH);
        hintIcon = new ImageIcon(newImage);
        JButton hintButton = new JButton("hints", hintIcon);
        hintButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

                // need a method to calculate all possible move, avoiding pits and bats as well
                // as guiding to the treasure, display all possible routes
                // ArrayList<CustomPair> suggestedRoutes;
                try {
                    Handler.hintButton();

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

        });
        menuBar.add(hintButton);

        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem savingGame = new JMenuItem("Save");
        JMenuItem loadingGame = new JMenuItem("Load games");
        JMenuItem quit = new JMenuItem("Quit");

        // JLabel count = new JLabel("Treasure found: ");
        frame.add(treasureCount, BorderLayout.SOUTH);
        // frame.add(arrowCount, BorderLayout.SOUTH);

        menu.add(newGame);
        menu.add(savingGame);
        menu.add(loadingGame);
        menu.add(quit);

        menu.addSeparator();
        JMenu difficulty = new JMenu("Level");
        JMenuItem easy = new JMenuItem("Easy");
        JMenuItem medium = new JMenuItem("Medium");
        JMenuItem hard = new JMenuItem("Hard");

        newGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Handler.resetGame();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

        });

        savingGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // // TODO Auto-generated method stub
                // // String folder = System.getProperty("../savedGame/");
                // JFileChooser fc = new JFileChooser();
                // String osName = System.getProperty("os.name");
                // String homeDir = System.getProperty("user.home");
                // File selectedPath = null;
                // if (osName.contains("Mac")) {
                // System.setProperty("apple.awt.fileDialogForDirectories", "true");
                // FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE);
                // fd.setDirectory("../savedGame/");
                // fd.setVisible(true);
                // String filename = fd.getDirectory();
                // selectedPath = new File(filename);
                // int result = fc.showSaveDialog(frame);
                // if (result == JFileChooser.APPROVE_OPTION) {
                // try {
                // Handler.saveBoard(filename);
                // } catch (Exception e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // }
                // if (filename == null) {
                // System.out.println("You cancelled the choice");
                // } else {
                // System.out.println("You chose " + filename);
                // }
                // System.setProperty("apple.awt.fileDialogForDirectories", "true");
                // } else {
                // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // fc.setCurrentDirectory(new File("../savedGame/"));
                // fc.setAcceptAllFileFilterUsed(false);
                // fc.showOpenDialog(null);
                // selectedPath = fc.getSelectedFile();
                // }
                try {
                    // if (System.getProperty("os.name").contains("Mac")) {
                    System.setProperty("apple.awt.fileDialogForDirectories", "true");
                    FileDialog dialog = new FileDialog(frame, "Choose destination", FileDialog.SAVE);
                    dialog.setDirectory("../savedGame");
                    dialog.setVisible(true);
                    String name = dialog.getFile();
                    String dir = dialog.getDirectory();

                    System.out.println(dir + name);
                    if (dir != null && name != null) {
                        Handler.saveBoard(dir + name);
                    }
                    // System.out.println(name + " " + dir);

                    // } else {
                    // JFileChooser fc = new JFileChooser(new File("../savedGame/"));
                    // FileNameExtensionFilter filter = new FileNameExtensionFilter("Wumpus ",
                    // "wumpus");
                    // fc.setFileFilter(filter);
                    // int value = fc.showSaveDialog(frame);

                    // if (value == JFileChooser.APPROVE_OPTION) {
                    // File file = fc.getSelectedFile();
                    // // String fileName = file.getName();
                    // String fileName = file.getCanonicalPath();
                    // // FileWriter writeFile = new FileWriter(fc.getSelectedFile() + ".wumpus");
                    // // writeFile.write("yourPath".toString());
                    // Handler.saveBoard(fileName);

                    // // //checking whether the name is already existed
                    // // if(getFileExtension(fileName).isPresent() == false){
                    // // fileName += ".gol";
                    // // Handler.saveBoard(fileName);
                    // // System.out.println("saved");
                    // // }else{
                    // // Handler.saveBoard(fileName);

                    // // }
                    // }
                    // }

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

        });

        loadingGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                // JFileChooser fc = new JFileChooser(new File("../savedGame/"));
                // FileNameExtensionFilter filter = new FileNameExtensionFilter("Wumpus ",
                // "wumpus");
                // fc.setFileFilter(filter);
                // int value = fc.showOpenDialog(frame);

                // if (value == JFileChooser.APPROVE_OPTION) {
                // File file = fc.getSelectedFile();
                // // System.out.println(file.getAbsolutePath());
                // try {
                // Handler.loadBoard(file.getAbsolutePath());
                // } catch (Exception e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // }
                // if (value == JFileChooser.CANCEL_OPTION) {
                // System.out.println();
                // }
                try {
                    System.setProperty("apple.awt.fileDialogForDirectories", "true");
                    FileDialog dialog = new FileDialog(frame, "Choose destination", FileDialog.LOAD);
                    dialog.setDirectory("../savedGame");
                    dialog.setVisible(true);
                    String name = dialog.getFile();
                    String dir = dialog.getDirectory();

                    if (dir != null && name != null) {
                        Handler.loadBoard(dir + "/" + name);
                    }

                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }

        });

        easy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                Handler.easyLevel();

            }

        });
        difficulty.add(easy);

        medium.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                Handler.mediumLevel();
            }

        });
        difficulty.add(medium);

        hard.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                Handler.hardLevel();
            }

        });
        difficulty.add(hard);

        menu.add(difficulty);

        quit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }

        });

        // JMenu boardSize = new JMenu("Board Size");
        // JMenuItem boardPrefferedSize = new JMenuItem("Preffered Board Size");

        // boardPrefferedSize.addActionListener(new ActionListener(){

        // @Override
        // public void actionPerformed(ActionEvent e) {
        // // TODO Auto-generated method stub
        // String size = JOptionPane.showInputDialog("Enter your board size: ");
        // System.out.println("Board size has been changed to : " + size);
        // int sizeInInt = 0;
        // try{
        // sizeInInt = Integer.parseInt(size);
        // //handler.set board size
        // Handler.resetBoardSize(sizeInInt);

        // }catch(NumberFormatException nfe){

        // } catch (Exception e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // }

        // });
        // boardSize.add(boardPrefferedSize);
        // menuBar.add(boardSize);

    }

    public void resizeBoard(int x, int y) {
        this.board_size_x = x;
        this.board_size_y = y;
        buttons = new ArrayList<JButton>();
        setUpFrame();
        setUpBoard();
        setUpMenu();
        frame.setVisible(true);
    }

    public Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public String addHints(ArrayList<String> hints) {
        String hint = "";
        hintPanel.removeAll();
        for (String h : hints) {
            hint += h;
            hintPanel.add(new JLabel(h, JLabel.CENTER));
        }
        return hint;
    }

    // public void displayMessage(String message) {
    // hintPanel.add(new JLabel(message, JLabel.RIGHT));
    // }

    public void gameOver(String whyYouDie) {
        frame.add(alertPanel, BorderLayout.NORTH);
        alertPanel.setVisible(true);
        alertPanel.add(new JLabel(whyYouDie, JLabel.CENTER));

    }

    public void clearAlert() {
        frame.remove(alertPanel);
        alertPanel.setVisible(false);
        alertPanel.removeAll();
    }

    public void changeWumpusCount(Integer number) {
        String text = treasureCount.getText();
        String[] strArr = text.split(" \t");
        String newText = strArr[0] + " \t" + strArr[1] + " \t" + "Wumpus Killed: " + number;
        treasureCount.setText(newText);
    }

    public void changeTreasureCount(Integer number) {
        String text = treasureCount.getText();
        String[] strArr = text.split(" \t");
        String newText = "Treasure Found: " + number + " \t" + strArr[1] + " \t" + strArr[2];

        treasureCount.setText(newText);
    }

    public void changeArrowCount(Integer number) {
        String text = treasureCount.getText();

        String[] strArr = text.split(" \t");
        String newText = strArr[0] + " \t" + "Arrows: " + number + " \t" + strArr[2];

        treasureCount.setText(newText);
    }

    public void updateFrame(String[][] argArr) {
        for (int i = 0; i < buttons.size(); i++) {
            Color c = new Color(80, 44, 0); // brown

            String arg = argArr[i / board_size_x][i % board_size_x];
            // piecetype,hint1,hint2,..
            String[] arguments = arg.split("\\$");
            switch (arguments[0]) {
                case Cell.WUMPUS:
                    c = Color.RED;
                    break;
                case Cell.WALL:
                    c = Color.BLACK;
                    break;
                case Cell.PLAYER:
                    c = Color.YELLOW;
                    break;
                case Cell.SUPERBAT:
                    c = Color.LIGHT_GRAY;
                    break;
                case Cell.PIT:
                    c = Color.BLUE;
                    break;
                case Cell.TREASURE:
                    c = Color.ORANGE;
                    break;
                case Cell.BONUSARROW:
                    c = Color.PINK;
                case Cell.ESCAPE:
                    c = Color.WHITE;
            }

            if (arguments[1].equals("true")) {
                c = tint(c, Color.RED, 0.2);
            }

            if (arguments.length > 2) {
                for (int k = 2; k < arguments.length; k++) {
                    // this doesn't work for some reason, even though arguments[k] is equal tp
                    c = tint(c, Color.CYAN, 0.2);
                }
            }

            buttons.get(i).setBackground(c);
        }

        board.requestFocusInWindow();
        frame.setVisible(true);
    }

    public Color tint(Color base, Color tint, double tintFactor) {
        // creates a new colour with the formula:
        // c1 = c3 + (255 - c2) * (the intensity of the tint)
        Color c = new Color(
                Math.min(255, (int) (base.getRed() + (255 - tint.getRed()) * tintFactor)),
                Math.min(255, (int) (base.getGreen() + (255 - tint.getGreen()) * tintFactor)),
                Math.min(255, (int) (base.getBlue() + (255 - tint.getBlue()) * tintFactor)));

        return c;
    }

    private class CustomKeyListener extends KeyAdapter {
        // Listens to keyboard input and sends relevent events to the handler
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Handler.onKey(e.paramString() + "," + "DOWN");
            // Handler.onClick(0, -1);
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                Handler.onKey(0, -1); // eventQueue -> Handle.handleEvent -> Board.movePiece
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                Handler.onKey(0, 1);
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                Handler.onKey(-1, 0);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                Handler.onKey(1, 0);
            }
            // System.out.println(e.paramString());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Handler.onKey(e.paramString() + "," + "UP");

            // System.out.println(e.paramString());
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improvecaro;

import static improvecaro.Menu.NORMAL;
import static improvecaro.Menu.RETURN;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Acer
 */
public class ImproveCaro extends javax.swing.JFrame {

    public static int BorderGap = 70;

    //This blankCell value only available for Enhanced Caro only
    public static int blankCell = 5;
    // This will make these cells match the perfect screen ratio 16:9 and each cells is 50x50 pixels
    public static final int ROWS = 18;
    public static final int COLS = 32;

    // These 2 attributes will be used by Enhanced Caro and Normal Caro
    private int numRows;
    private int numCols;

    private Cell map[][];
    private int NumberOfCellUnOccupied;

    // Turn = 0 is O turn, while Turn 1 is X turn
    private int Turn;
    private int CountTurn;

    public static int OCell = 0;
    public static int XCell = 1;
    public static int CellSize = 50;
    public static int turnOnValue = 10;

    //For displaying player info
    private GameInfo RedInfo;
    private GameInfo BlueInfo;
    //For drawing win line
    private Board board;
    // Start and End to draw the winning Line
    private Cell Start;
    private Cell End;

    // Sub-Panel
    private JLayeredPane Lpane;
    private Menu pnlMenu;
    private NormalCaro pnlNormalMode;
    private GameNotfication pnlGameNotfication;

    private boolean RPG = false;
    private boolean Normal = false;
    private JLabel lblReturn;

    public ImproveCaro() {
        initComponents();
        this.setTitle("Improve Caro!");
        this.setLayout(null);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);         //Maximize the frame
        this.setLocationRelativeTo(null); // Center The ImproveCaro

        btnClear.setVisible(false);
        btnResetSkill.setVisible(false);

        Lpane = this.getLayeredPane();

        pnlMenu = new Menu(this);
        initLblReturn();

        pnlNormalMode = new NormalCaro(this);

        pnlGameNotfication = new GameNotfication(this);

        remove(pnlRPGMode);
        Lpane.add(pnlGameNotfication, 0);
        Lpane.add(lblReturn, 1);
        Lpane.add(pnlMenu, 2);

        Lpane.add(pnlRPGMode, 3);
        Lpane.add(pnlNormalMode, 3);

        pnlRPGMode.setVisible(false);
        pnlGameNotfication.setVisible(false);
        pnlNormalMode.setVisible(false);
        pnlMenu.setVisible(true);

        this.repaint();
    }

    public void initPnlRPG() {
        removeAllComponent(pnlBoard);
        removeAllComponent(board);

        if (RedInfo != null && BlueInfo != null) {
            pnlOGameInfo.remove(RedInfo);
            pnlXGameInfo.remove(BlueInfo);
        }

        pnlBoard.setLayout(null);
        pnlRPGMode.setLayout(null);

        board = new Board(this, (int) pnlBoard.getPreferredSize().getWidth(), (int) pnlBoard.getPreferredSize().getHeight());
        board.setLayout(null);

        pnlOGameInfo.setBackground(Color.white);
        pnlXGameInfo.setBackground(Color.white);
        pnlOGameInfo.setOpaque(false);
        pnlXGameInfo.setOpaque(false);

        pnlBoard.add(board);

        // Displaying the game infomation
        RedInfo = new GameInfo(this, GameInfo.Red, (int) pnlOGameInfo.getPreferredSize().getWidth(), (int) pnlOGameInfo.getPreferredSize().getHeight());
        BlueInfo = new GameInfo(this, GameInfo.Blue, (int) pnlXGameInfo.getPreferredSize().getWidth(), (int) pnlXGameInfo.getPreferredSize().getHeight());
        RedInfo.setBgValue(OCell);
        BlueInfo.setBgValue(XCell);

        pnlOGameInfo.add(RedInfo);
        pnlXGameInfo.add(BlueInfo);

        pnlRPGMode.repaint();
        pnlOGameInfo.repaint();
        pnlXGameInfo.repaint();

    }

    public void initLblReturn() {
        lblReturn = new JLabel();

        lblReturn.setBounds(10, 10, 70, 70);

        lblReturn.setIcon(pnlMenu.autoResize(Menu.RETURN, Menu.NORMAL, 70, 70));
        lblReturn.setVisible(false);

        lblReturn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pnlMenu.switchTitle("gameStart");
                pnlMenu.enableMenuMode();
            }
        });
    }

    public void generateBoard() {

        //Reset the board if there is any components on it
        //board = new Board(this, (int) pnlBoard.getPreferredSize().getWidth(), (int) pnlBoard.getPreferredSize().getHeight());
        numRows = ROWS;
        numCols = COLS;

        initPnlRPG();

        map = new Cell[numRows][numCols];
        // Using null layout to have better control
//        int cellGap = 2, borderGap = 15;
        int cellGap = 0, borderGap = 0;

        int x = borderGap, y = borderGap;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                map[i][j] = new Cell(this, i, j, blankCell, CellSize);

                //Using null layout will break everything so need to set Location of each cell
                map[i][j].setLocation(x, y);
                x += CellSize + cellGap;

                board.add(map[i][j]);
            }
            x = borderGap;
            y += CellSize;
        }
        //TEST (removed later)
        RedInfo.getActorSkill().setCurrentSkill(Skill.getRandomSkill());
        BlueInfo.getActorSkill().setCurrentSkill(Skill.getRandomSkill());
//
        RedInfo.updateSkillIcon();
        BlueInfo.updateSkillIcon();

        Turn = 0;
        CountTurn = 0;
        lblCountTurn.setText("Turn: " + CountTurn);

        BlueInfo.lockSkill();
        NumberOfCellUnOccupied = 18 * 32;
        lblReturn.setVisible(true);
        //Garbage collecting each time generating a new board
        System.gc();
        repaint();
    }

    /**
     * Creates new form Frame
     */
    public void nextTurn() {

        //Updating turn
        Turn = 1 - Turn;

        CountTurn++;
        lblCountTurn.setText("Turn: " + CountTurn);

        if (Turn == OCell) {
            RedInfo.setBackground(GameInfo.RedTurn);
            BlueInfo.setBackground(GameInfo.BlueWait);

            if (!RedInfo.isSkillUsed()) {
                RedInfo.unlockSkill();
            }
            BlueInfo.lockSkill();
        } else {
            RedInfo.setBackground(GameInfo.RedWait);
            BlueInfo.setBackground(GameInfo.BlueTurn);

            if (!BlueInfo.isSkillUsed()) {
                BlueInfo.unlockSkill();
            }
            RedInfo.lockSkill();
        }
    }

    public int getTurn() {
        return Turn;
    }

    public void setTurn(int Turn) {
        this.Turn = Turn;
    }

    /* ============== Check section ============== */
    public boolean DifferentColor(int row, int col, int turn) {
        return map[row][col].getValue() % turnOnValue != turn;
    }

    public boolean SameColor(int row, int col, int turn) {
        return !DifferentColor(row, col, turn);
    }

    public boolean isBlank(int row, int col) {
        return map[row][col].getValue() % turnOnValue == 5;
    }

    /**
     * Check if the cell is blocked or not (BLOCK skill)
     *
     * @param row
     * @param col
     * @return
     */
    public boolean isBlock(int row, int col) {
        return map[row][col].getValue() == -1;
    }

    public boolean hasChess(int row, int col) {
        return !isBlank(row, col) && !isBlock(row, col);
    }

    /* ============== Check section ============== */
 /* ============== Counting section ============== */
    public int countUp(int row, int col) {
        int count = 0;
        int i;
        for (i = row - 1; i >= 0; i--) {
            if (hasChess(i, col)
                    && SameColor(i, col, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            Start = map[i + 1][col];
        }

        return count;
    }

    public int countDown(int row, int col) {
        int count = 0;
        int i;
        for (i = row + 1; i < numRows; i++) {
            if (hasChess(i, col)
                    && SameColor(i, col, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            End = map[i - 1][col];
        }
        return count;
    }

    public int countLeft(int row, int col) {
        int count = 0;
        int i;
        for (i = col - 1; i >= 0; i--) {
            if (hasChess(row, i)
                    && SameColor(row, i, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            Start = map[row][i + 1];
        }
        return count;
    }

    public int countRight(int row, int col) {
        int count = 0;
        int i;
        for (i = col + 1; i < numCols; i++) {
            if (hasChess(row, i)
                    && SameColor(row, i, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            End = map[row][i - 1];
        }
        return count;
    }

    public int countLeftUp(int row, int col) {
        int count = 0;
        int i, j;
        for (i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (hasChess(i, j)
                    && SameColor(i, j, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            Start = map[i + 1][j + 1];
        }
        return count;
    }

    public int countRightDown(int row, int col) {
        int count = 0;
        int i, j;
        for (i = row + 1, j = col + 1; i < numRows && j < numCols; i++, j++) {
            if (hasChess(i, j)
                    && SameColor(i, j, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            End = map[i - 1][j - 1];
        }
        return count;
    }

    public int countRightUp(int row, int col) {
        int count = 0;
        int i, j;
        for (i = row - 1, j = col + 1; i >= 0 && j < numCols; i--, j++) {
            if (hasChess(i, j)
                    && SameColor(i, j, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            Start = map[i + 1][j - 1];
        }
        return count;
    }

    public int countLeftDown(int row, int col) {
        int count = 0;
        int i, j;
        for (i = row + 1, j = col - 1; i < numRows && j >= 0; i++, j--) {
            if (hasChess(i, j)
                    && SameColor(i, j, Turn)) {
                count++;
            } else {
                break;
            }
        }
        if (count > 0) {
            End = map[i - 1][j + 1];
        }
        return count;
    }

    public int CountUpAndDown(int row, int col) {
        clearStartEnd();
        return countUp(row, col) + countDown(row, col);
    }

    public int CountLeftAndRight(int row, int col) {
        clearStartEnd();
        return countLeft(row, col) + countRight(row, col);
    }

    public int CountLeftUpAndRightDown(int row, int col) {
        clearStartEnd();
        return countLeftUp(row, col) + countRightDown(row, col);
    }

    public int CountLeftDownAndRightUp(int row, int col) {
        clearStartEnd();
        return countLeftDown(row, col) + countRightUp(row, col);
    }

    /* ============== Counting section ============== */
 /* ============== Special Operations section ============== */
    public void resetBoard() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (hasChess(i, j) || isBlock(i, j)) {
                    map[i][j].addMouseListener(map[i][j].getMouseClicked());
                    map[i][j].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                map[i][j].setValue(5);
                map[i][j].updateIcon();
            }
        }
    }

    public void resetSkill() {

        RedInfo.setIsSkillUsed(false);
        BlueInfo.setIsSkillUsed(false);

        RedInfo.enableSkill();

        if (RedInfo.isSkillUsed()) {
            RedInfo.unlockSkill();
        }

        BlueInfo.enableSkill();

        if (BlueInfo.isSkillUsed()) {
            BlueInfo.unlockSkill();
        }
    }

    public void reDraw(int[][] mapValue) {
        resetBoard();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                //Update value
                map[i][j].setValue(mapValue[i][j]);
                if (hasChess(i, j) || isBlock(i, j)) {
                    map[i][j].removeMouseListener(map[i][j].getMouseClicked());
                    map[i][j].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } else {
                    map[i][j].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                map[i][j].updateIcon();
            }
        }
        repaint();
    }

    public void clearStartEnd() {
        Start = null;
        End = null;
    }

    public int[][] getCopy2DimesionalArray(int[][] oldArray) {
        int copyArray[][] = new int[oldArray.length][];
        for (int i = 0; i < oldArray.length; i++) {
            copyArray[i] = new int[oldArray[i].length];
            for (int j = 0; j < oldArray[i].length; j++) {
                copyArray[i][j] = oldArray[i][j];
            }
        }
        return copyArray;
    }

    public void removeAllComponent(JPanel pnl) {
        if (pnl == null) {
            return;
        }
        pnl.removeAll();
        pnl.revalidate();
        pnl.repaint();
    }

    /* ============== Special Operations section ============== */
 /* ============== Animation section ============== */
    public TimerTask moveComponentRightToLeft(Component o, int location, int speed) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (o.getX() > location) {
                    o.setLocation(o.getX() - speed, o.getY());
                }
            }
        };
        repaint();
        return tt;
    }

    public TimerTask moveComponentLeftToRight(Component o, int location, int speed) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (o.getX() < location) {
                    o.setLocation(o.getX() + speed, o.getY());
                }
            }
        };
        repaint();
        return tt;
    }

    public TimerTask moveComponentLeftToRightRepeat(Component o, int location, int speed) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (o.getX() >= 1920) {
                    o.setLocation(0 - o.getWidth(), o.getY());
                } else {
                    o.setLocation(o.getX() + speed, o.getY());
                }
            }
        };
        repaint();
        return tt;
    }

    public TimerTask moveComponentLeftToRightUpRepeat(Component o, int location, int speed) {
        TimerTask tt = new TimerTask() {
            int jump = speed;

            @Override
            public void run() {
                if (o.getX() >= 1920) {
                    o.setLocation(0 - o.getWidth(), o.getY());
                } else {
                    o.setLocation(o.getX() + speed, o.getY());
                }
                if (jump > 0) {
                    if (o.getY() > 750) {
                        o.setLocation(o.getX(), o.getY() - speed);
                    } else {
                        jump = -jump;
                    }
                } else {//getHeight() - o.getHeight() - 70
                    if (o.getY() < getHeight() - o.getHeight() - 20) {
                        o.setLocation(o.getX(), o.getY() + speed);
                    } else {
                        jump = -jump;
                    }
                }
            }
        };
        repaint();
        return tt;
    }

    public TimerTask ShakeComponent(Component o, int locationLeft, int locationRight, int speed) {

        TimerTask tt = new TimerTask() {
            int s = speed;

            @Override
            public void run() {
                if (s > 0) {
                    if (o.getX() < locationRight) {
                        o.setLocation(o.getX() + s, o.getY());
                    } else {
                        s = -s;
                    }
                } else if (s < 0) {
                    if (o.getX() > locationLeft) {
                        o.setLocation(o.getX() + s, o.getY());
                    } else {
                        s = -s;
                    }
                }
            }
        };
        repaint();
        return tt;
    }

    /* ============== Animation section ============== */
 /* ============== Getters and Setters section ============== */
    public Cell getStart() {
        return Start;
    }

    public void setStart(Cell Start) {
        this.Start = Start;
    }

    public Cell getEnd() {
        return End;
    }

    public void setEnd(Cell End) {
        this.End = End;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Cell[][] getMap() {
        return map;
    }

    public void setMapValue(int[][] mapValue) {

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                this.map[i][j].setValue(mapValue[i][j]);
                this.map[i][j].updateIcon();
            }
        }
    }

    public void setMap(Cell[][] newMap) {
        this.map = newMap;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public GameInfo getRedInfo() {
        return RedInfo;
    }

    public void setRedInfo(GameInfo RedInfo) {
        this.RedInfo = RedInfo;
    }

    public GameInfo getBlueInfo() {
        return BlueInfo;
    }

    public void setBlueInfo(GameInfo BlueInfo) {
        this.BlueInfo = BlueInfo;
    }

    public int getCountTurn() {
        return CountTurn;
    }

    public void setCountTurn(int CountTurn) {
        this.CountTurn = CountTurn;
    }

    public JPanel getPnlRPGMode() {
        return pnlRPGMode;
    }

    public void setPnlRPGMode(JPanel pnlRPGMode) {
        this.pnlRPGMode = pnlRPGMode;
    }

    public boolean isRPG() {
        return RPG;
    }

    public void setRPG(boolean RPG) {
        this.RPG = RPG;
    }

    public boolean isNormal() {
        return Normal;
    }

    public void setNormal(boolean Normal) {
        this.Normal = Normal;
    }

    public NormalCaro getPnlNormalMode() {
        return pnlNormalMode;
    }

    public void setPnlNormalMode(NormalCaro pnlNormalMode) {
        this.pnlNormalMode = pnlNormalMode;
    }

    public GameNotfication getPnlGameNotfication() {
        return pnlGameNotfication;
    }

    public void setPnlGameNotfication(GameNotfication pnlGameNotfication) {
        this.pnlGameNotfication = pnlGameNotfication;
    }

    public JLayeredPane getLpane() {
        return Lpane;
    }

    public void setLpane(JLayeredPane Lpane) {
        this.Lpane = Lpane;
    }

    public Menu getPnlMenu() {
        return pnlMenu;
    }

    public void setPnlMenu(Menu pnlMenu) {
        this.pnlMenu = pnlMenu;
    }

    public int getNumberOfCellUnOccupied() {
        return NumberOfCellUnOccupied;
    }

    public void setNumberOfCellUnOccupied(int NumberOfCellUnOccupied) {
        this.NumberOfCellUnOccupied = NumberOfCellUnOccupied;
    }

    public JLabel getLblReturn() {
        return lblReturn;
    }

    public void setLblReturn(JLabel lblReturn) {
        this.lblReturn = lblReturn;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlRPGMode = new javax.swing.JPanel();
        pnlOGameInfo = new javax.swing.JPanel();
        pnlBoard = new javax.swing.JPanel();
        pnlXGameInfo = new javax.swing.JPanel();
        btnClear = new javax.swing.JToggleButton();
        btnResetSkill = new javax.swing.JButton();
        lblCountTurn = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlRPGMode.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 5, true));
        pnlRPGMode.setPreferredSize(new java.awt.Dimension(1920, 1080));
        pnlRPGMode.setLayout(null);

        pnlOGameInfo.setBackground(new java.awt.Color(254, 66, 66));
        pnlOGameInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Game infomation"));

        javax.swing.GroupLayout pnlOGameInfoLayout = new javax.swing.GroupLayout(pnlOGameInfo);
        pnlOGameInfo.setLayout(pnlOGameInfoLayout);
        pnlOGameInfoLayout.setHorizontalGroup(
            pnlOGameInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 105, Short.MAX_VALUE)
        );
        pnlOGameInfoLayout.setVerticalGroup(
            pnlOGameInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 932, Short.MAX_VALUE)
        );

        pnlRPGMode.add(pnlOGameInfo);
        pnlOGameInfo.setBounds(17, 18, 117, 956);

        pnlBoard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        pnlBoard.setName(""); // NOI18N
        pnlBoard.setPreferredSize(new java.awt.Dimension(1600, 900));
        pnlBoard.setLayout(null);
        pnlRPGMode.add(pnlBoard);
        pnlBoard.setBounds(147, 74, 1600, 900);

        pnlXGameInfo.setBackground(new java.awt.Color(60, 80, 177));
        pnlXGameInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Game infomation"));
        pnlXGameInfo.setToolTipText("");

        javax.swing.GroupLayout pnlXGameInfoLayout = new javax.swing.GroupLayout(pnlXGameInfo);
        pnlXGameInfo.setLayout(pnlXGameInfoLayout);
        pnlXGameInfoLayout.setHorizontalGroup(
            pnlXGameInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );
        pnlXGameInfoLayout.setVerticalGroup(
            pnlXGameInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 936, Short.MAX_VALUE)
        );

        pnlRPGMode.add(pnlXGameInfo);
        pnlXGameInfo.setBounds(1754, 14, 128, 960);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlRPGMode.add(btnClear);
        btnClear.setBounds(453, 30, 92, 25);

        btnResetSkill.setText("reset Skill");
        btnResetSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetSkillActionPerformed(evt);
            }
        });
        pnlRPGMode.add(btnResetSkill);
        btnResetSkill.setBounds(557, 30, 91, 25);

        lblCountTurn.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblCountTurn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCountTurn.setText("Turn: 0");
        lblCountTurn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlRPGMode.add(lblCountTurn);
        lblCountTurn.setBounds(808, 18, 275, 38);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRPGMode, javax.swing.GroupLayout.PREFERRED_SIZE, 1904, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlRPGMode, javax.swing.GroupLayout.PREFERRED_SIZE, 987, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 76, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        resetBoard();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnResetSkillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetSkillActionPerformed
        // TODO add your handling code here:
        resetSkill();
    }//GEN-LAST:event_btnResetSkillActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ImproveCaro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImproveCaro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImproveCaro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImproveCaro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ImproveCaro().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnClear;
    private javax.swing.JButton btnResetSkill;
    private javax.swing.JLabel lblCountTurn;
    private javax.swing.JPanel pnlBoard;
    private javax.swing.JPanel pnlOGameInfo;
    private javax.swing.JPanel pnlRPGMode;
    private javax.swing.JPanel pnlXGameInfo;
    // End of variables declaration//GEN-END:variables
}

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe {
    private JFrame frame;
    private JButton[][] board;
    private JButton viewTreeButton;
    private String humanPiece;
    private String aiPiece;
    private boolean isHumanTurn;
    private boolean gameOver;
    
    private DefaultMutableTreeNode fullGameTree;
    private DefaultMutableTreeNode currentHistoryNode; 

    public TicTacToe() {
        frame = new JFrame("Tic Tac Toe vs AI");
        board = new JButton[3][3];
        gameOver = false;
        setupGUI();
        startGame();
    }

    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 550 height gives room for the button at the bottom
        frame.setSize(500, 550); 
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = new JButton("");
                board[row][col].setFont(new Font("Arial", Font.BOLD, 80));
                board[row][col].setFocusPainted(false);
                board[row][col].addActionListener(new ButtonClickListener());
                gridPanel.add(board[row][col]);
            }
        }
        frame.add(gridPanel, BorderLayout.CENTER);
        
        // The Live View Button is back!
        viewTreeButton = new JButton("View Live Game Tree");
        viewTreeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewTreeButton.addActionListener(e -> TreeViewerWindow.display(fullGameTree, frame));
        frame.add(viewTreeButton, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startGame() {
        fullGameTree = new DefaultMutableTreeNode(new GameState(
                "Game Start", new String[]{"","","","","","","","",""}, "", false));
        ((GameState) fullGameTree.getUserObject()).setActualGamePath(true);
        currentHistoryNode = fullGameTree;

        Object[] options = {"I'll go first (X)", "AI goes first (O)"};
        int choice = JOptionPane.showOptionDialog(frame, "Who should make the first move?", "Game Setup",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) System.exit(0);

        if (choice == 0) {
            humanPiece = "X"; aiPiece = "O"; isHumanTurn = true;
        } else {
            humanPiece = "O"; aiPiece = "X"; isHumanTurn = false; scheduleAIMove();
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clicked = (JButton) e.getSource();
            if (!gameOver && isHumanTurn && clicked.getText().equals("")) {
                clicked.setText(humanPiece);
                
                GameState humanState = new GameState("Human Plays " + humanPiece, getBoardSnapshot(), "", false);
                humanState.setActualGamePath(true);
                DefaultMutableTreeNode humanNode = new DefaultMutableTreeNode(humanState);
                currentHistoryNode.add(humanNode);
                currentHistoryNode = humanNode; 

                if (checkGameState(humanPiece)) return;
                isHumanTurn = false;
                scheduleAIMove();
            }
        }
    }

    private void scheduleAIMove() {
        Timer timer = new Timer(500, e -> makeAIMove());
        timer.setRepeats(false);
        timer.start();
    }

    private void makeAIMove() {
        if (gameOver) return;

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};
        
        DefaultMutableTreeNode bestBranch = null;
        DefaultMutableTreeNode worstBranch = null;
        DefaultMutableTreeNode drawBranch = null;
        int minScoreForFilter = Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().equals("")) {
                    board[i][j].setText(aiPiece);
                    
                    DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(
                            new GameState("AI considers move", getBoardSnapshot(), "", false));
                    
                    int score = minimax(0, false, tempNode, 3); 
                    
                    GameState state = (GameState) tempNode.getUserObject();
                    state.setScoreInfo("Score: " + score);
                    state.setRawScore(score);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i; bestMove[1] = j;
                        bestBranch = tempNode; 
                    }
                    if (score < minScoreForFilter) {
                        minScoreForFilter = score; worstBranch = tempNode;
                    }
                    if (score == 0) drawBranch = tempNode;

                    board[i][j].setText("");
                }
            }
        }

        if (bestBranch != null) {
            ((GameState) bestBranch.getUserObject()).setActualGamePath(true);
            ((GameState) bestBranch.getUserObject()).setOptimal(true);
            ((GameState) bestBranch.getUserObject()).setTitle("AI Chosen Move");
            currentHistoryNode.add(bestBranch);
        }
        if (worstBranch != null && worstBranch != bestBranch) currentHistoryNode.add(worstBranch);
        if (drawBranch != null && drawBranch != bestBranch && drawBranch != worstBranch) currentHistoryNode.add(drawBranch);

        if (bestBranch != null) {
            currentHistoryNode = bestBranch;
        }

        board[bestMove[0]][bestMove[1]].setText(aiPiece);
        if (checkGameState(aiPiece)) return;
        isHumanTurn = true;
    }

    private int minimax(int depth, boolean isMaximizing, DefaultMutableTreeNode parentNode, int maxDepth) {
        int boardScore = evaluateBoard();

        if (boardScore == 10) return boardScore - depth;
        if (boardScore == -10) return boardScore + depth;
        if (isBoardFull()) return 0;
        
        if (depth >= maxDepth) return 0; 

        DefaultMutableTreeNode bestBranch = null;
        DefaultMutableTreeNode worstBranch = null;
        DefaultMutableTreeNode drawBranch = null;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            int minScoreForFilter = Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText(aiPiece);
                        DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(
                                new GameState("AI Evaluation", getBoardSnapshot(), "", false));
                        
                        int score = minimax(depth + 1, false, tempNode, maxDepth);
                        GameState state = (GameState) tempNode.getUserObject();
                        state.setScoreInfo("Score: " + score);
                        state.setRawScore(score);
                        
                        if (score > bestScore) {
                            bestScore = score; bestBranch = tempNode; 
                        }
                        if (score < minScoreForFilter) {
                            minScoreForFilter = score; worstBranch = tempNode;
                        }
                        if (score == 0) drawBranch = tempNode;

                        board[i][j].setText("");
                    }
                }
            }
            if (bestBranch != null) {
                ((GameState) bestBranch.getUserObject()).setOptimal(true);
                parentNode.add(bestBranch);
            }
            if (worstBranch != null && worstBranch != bestBranch) parentNode.add(worstBranch);
            if (drawBranch != null && drawBranch != bestBranch && drawBranch != worstBranch) parentNode.add(drawBranch);
            return bestScore;
            
        } else {
            int bestScore = Integer.MAX_VALUE;
            int maxScoreForFilter = Integer.MIN_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText(humanPiece);
                        DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(
                                new GameState("Human Reply Eval", getBoardSnapshot(), "", false));
                        
                        int score = minimax(depth + 1, true, tempNode, maxDepth);
                        GameState state = (GameState) tempNode.getUserObject();
                        state.setScoreInfo("Score: " + score);
                        state.setRawScore(score);
                        
                        if (score < bestScore) {
                            bestScore = score; bestBranch = tempNode; 
                        }
                        if (score > maxScoreForFilter) {
                            maxScoreForFilter = score; worstBranch = tempNode;
                        }
                        if (score == 0) drawBranch = tempNode;

                        board[i][j].setText("");
                    }
                }
            }
            if (bestBranch != null) {
                ((GameState) bestBranch.getUserObject()).setOptimal(true);
                parentNode.add(bestBranch);
            }
            if (worstBranch != null && worstBranch != bestBranch) parentNode.add(worstBranch);
            if (drawBranch != null && drawBranch != bestBranch && drawBranch != worstBranch) parentNode.add(drawBranch);
            return bestScore;
        }
    }

    private String[] getBoardSnapshot() {
        String[] snap = new String[9];
        int index = 0;
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) snap[index++] = board[i][j].getText();
        return snap;
    }

    private int evaluateBoard() {
        String[][] field = new String[3][3];
        for (int i=0; i<3; i++) for (int j=0; j<3; j++) field[i][j] = board[i][j].getText();
        for (int i=0; i<3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2])) {
                if (field[i][0].equals(aiPiece)) return 10; else if (field[i][0].equals(humanPiece)) return -10;
            }
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i])) {
                if (field[0][i].equals(aiPiece)) return 10; else if (field[0][i].equals(humanPiece)) return -10;
            }
        }
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2])) {
            if (field[0][0].equals(aiPiece)) return 10; else if (field[0][0].equals(humanPiece)) return -10;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0])) {
            if (field[0][2].equals(aiPiece)) return 10; else if (field[0][2].equals(humanPiece)) return -10;
        }
        return 0;
    }

    private boolean checkGameState(String pieceToCheck) {
        int score = evaluateBoard();
        if (score == 10 || score == -10 || isBoardFull()) {
            gameOver = true;
            
            String resultMessage = (score == 10 || score == -10) ? 
                (pieceToCheck.equals(humanPiece) ? "You win!" : "AI wins!") : "It's a draw!";
            
            Object[] options = {"Play Again", "View Final Tree", "Exit Game"};
            
            int response = JOptionPane.showOptionDialog(frame, 
                    resultMessage + "\nWhat would you like to do?", 
                    "Game Over", 
                    JOptionPane.YES_NO_CANCEL_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, 
                    options[0]);
            
            if (response == 0) {
                resetBoard(); 
            } else if (response == 1) {
                TreeViewerWindow.display(fullGameTree, frame); 
            } else {
                System.exit(0); 
            }
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i=0; i<3; i++) for (int j=0; j<3; j++) if (board[i][j].getText().equals("")) return false;
        return true;
    }

    private void resetBoard() {
        for (int i=0; i<3; i++) for (int j=0; j<3; j++) board[i][j].setText("");
        gameOver = false;
        startGame(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe());
    }
}
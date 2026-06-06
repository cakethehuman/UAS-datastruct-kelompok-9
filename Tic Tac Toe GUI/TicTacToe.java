import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

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
        frame = new JFrame("Tic Tac Toe AI Kelompok 9 Ganjil");
        board = new JButton[3][3];
        gameOver = false;
        setupGUI();
        startGame();
    }

    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 550); 
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
        
        viewTreeButton = new JButton("Liat Game Tree");
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

        Object[] options = {"User gerak pertama (X)", "AI gerak pertama (O)"};
        int choice = JOptionPane.showOptionDialog(frame, "Siapa yang bakal gerak pertama?", "Game Setup",
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
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Timer timer = new Timer(500, e -> {
            makeAIMove();
            frame.setCursor(Cursor.getDefaultCursor());
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void makeAIMove() {
        if (gameOver) return;

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};
        DefaultMutableTreeNode bestBranch = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().equals("")) {
                    board[i][j].setText(aiPiece);
                    
                    DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(
                            new GameState("AI considers move", getBoardSnapshot(), "", false));
                    
                    int score = minimax(0, false, tempNode); 
                    
                    GameState state = (GameState) tempNode.getUserObject();
                    state.setScoreInfo("Score: " + score);
                    state.setRawScore(score);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i; 
                        bestMove[1] = j;
                        bestBranch = tempNode; 
                    }
                    if(tempNode != bestBranch){
                        currentHistoryNode.add(tempNode);
                    }
                    board[i][j].setText("");
                }
            }
        }

        if (bestBranch != null) {
            ((GameState) bestBranch.getUserObject()).setActualGamePath(true);
            ((GameState) bestBranch.getUserObject()).setOptimal(true);
            ((GameState) bestBranch.getUserObject()).setTitle("AI Chosen Move");
            currentHistoryNode.add(bestBranch);
            currentHistoryNode = bestBranch;
        }

        board[bestMove[0]][bestMove[1]].setText(aiPiece);
        if (checkGameState(aiPiece)) return;
        isHumanTurn = true;
    }

    private int minimax(int depth, boolean isMaximizing, DefaultMutableTreeNode parentNode) {
        int boardScore = evaluateBoard();

        if (boardScore == 10) return boardScore - depth; 
        if (boardScore == -10) return boardScore + depth; 
        if (isBoardFull()) return 0; 

        boolean recordToTree = depth < 3; 

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText(aiPiece);
                        
                        DefaultMutableTreeNode childNode = null;
                        if (recordToTree) {
                            childNode = new DefaultMutableTreeNode(
                                new GameState("AI Eval (Depth " + depth + ")", getBoardSnapshot(), "", false));
                            parentNode.add(childNode);
                        }

                        int score = minimax(depth + 1, false, childNode);
                        
                        if (recordToTree && childNode != null) {
                            GameState state = (GameState) childNode.getUserObject();
                            state.setScoreInfo("Eval: " + score);
                        }

                        bestScore = Math.max(score, bestScore);
                        board[i][j].setText("");
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText(humanPiece);
                        
                        DefaultMutableTreeNode childNode = null;
                        if (recordToTree) {
                            childNode = new DefaultMutableTreeNode(
                                new GameState("Human Reply (Depth " + depth + ")", getBoardSnapshot(), "", false));
                            parentNode.add(childNode);
                        }

                        int score = minimax(depth + 1, true, childNode);
                        
                        if (recordToTree && childNode != null) {
                            GameState state = (GameState) childNode.getUserObject();
                            state.setScoreInfo("Eval: " + score);
                        }

                        bestScore = Math.min(score, bestScore);
                        board[i][j].setText("");
                    }
                }
            }
            return bestScore;
        }
    }

    private String[] getBoardSnapshot() {
        String[] snap = new String[9];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                snap[index++] = board[i][j].getText();
            }
        }
        return snap;
    }

    private int evaluateBoard() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(board[i][1].getText()) && 
                board[i][1].getText().equals(board[i][2].getText()) && 
                !board[i][0].getText().equals("")) {
                return board[i][0].getText().equals(aiPiece) ? 10 : -10;
            }
            if (board[0][i].getText().equals(board[1][i].getText()) && 
                board[1][i].getText().equals(board[2][i].getText()) && 
                !board[0][i].getText().equals("")) {
                return board[0][i].getText().equals(aiPiece) ? 10 : -10;
            }
        }
        if (board[0][0].getText().equals(board[1][1].getText()) && 
            board[1][1].getText().equals(board[2][2].getText()) && 
            !board[0][0].getText().equals("")) {
            return board[0][0].getText().equals(aiPiece) ? 10 : -10;
        }
        if (board[0][2].getText().equals(board[1][1].getText()) && 
            board[1][1].getText().equals(board[2][0].getText()) && 
            !board[0][2].getText().equals("")) {
            return board[0][2].getText().equals(aiPiece) ? 10 : -10;
        }
        return 0;
    }

    private boolean checkGameState(String pieceToCheck) {
        int score = evaluateBoard();
        if (score == 10 || score == -10 || isBoardFull()) {
            gameOver = true;
            
            String resultMessage = (score == 10 || score == -10) ? 
                (pieceToCheck.equals(humanPiece) ? "Kamu menang!" : "AI menang!") : "Draw!";
            
            Object[] options = {"Play Again", "Liat hasil Tree", "Exit Game"};
            
            int response = JOptionPane.showOptionDialog(frame, 
                    resultMessage + "\nPilih opsi:", 
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText("");
            }
        }
        gameOver = false;
        startGame(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe());
    }
}
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
        frame = new JFrame("Tic Tac Toe AI kelompok 9 kelas A Kelompok ganjil");
        board = new JButton[3][3];
        gameOver = false;
        setupGUI();
        startGame();
    }

    // Setup GUI awal seperti tombol untuk taro x dan o serta tombol melihat game tree
    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 650); 
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
        viewTreeButton.addActionListener(e -> TreeViewerWindow.display(fullGameTree, frame));
        frame.add(viewTreeButton, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Setup game awal dengan value kosong dan membuat tombol option untuk pemain gerak pertama atau tidak
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

    // class yang berguna sebagai logika button
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clicked = (JButton) e.getSource();
            if (!gameOver && isHumanTurn && clicked.getText().equals("")) {
                clicked.setText(humanPiece);
                
                GameState humanState = new GameState("Pemain Taro " + humanPiece, getBoardInfo(), "", false);
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

    // Menentukan seberapa lama untuk AI (algoritma minimax) menaruh X atau O ke board
    private void scheduleAIMove() {
        Timer timer = new Timer(200, e -> makeAIMove());
        timer.setRepeats(false);
        timer.start();
    }

    // Logika gerak dari AI
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
                    new GameState("Opsi Gerak AI", getBoardInfo(), "", false));
                    int score = minimax(0, false, tempNode, 9); 
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
            ((GameState) bestBranch.getUserObject()).setTitle("Gerakan Terbaik AI");
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
        // algoritma rekursif untuk prediksi semua posibilitas gerekan
        private int minimax(int depth, boolean isMaximizing, DefaultMutableTreeNode parentNode, int maxDepth) {
            int boardScore = evaluateBoard();

            if (boardScore == 10) return boardScore - depth;
            if (boardScore == -10) return boardScore + depth;
            if (isBoardFull()) return 0;
            
            if (depth >= maxDepth) return 0; 
            boolean recordVisuals = (parentNode != null && depth <= 2);

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
                            
                            DefaultMutableTreeNode tempNode = null;
                            if (recordVisuals) {
                                tempNode = new DefaultMutableTreeNode(
                                        new GameState("Gerakan AI", getBoardInfo(), "", false));
                            }
                            
                            int score = minimax(depth + 1, false, tempNode, maxDepth);
                            
                            if (recordVisuals && tempNode != null) {
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
                            } else {
                                bestScore = Math.max(score, bestScore);
                            }

                            board[i][j].setText("");
                        }
                    }
                }
                if (recordVisuals) {
                    if (bestBranch != null) {
                        ((GameState) bestBranch.getUserObject()).setOptimal(true);
                        parentNode.add(bestBranch);
                    }
                    if (worstBranch != null && worstBranch != bestBranch) parentNode.add(worstBranch);
                    if (drawBranch != null && drawBranch != bestBranch && drawBranch != worstBranch) parentNode.add(drawBranch);
                }
                return bestScore;
                
            } else {
                int bestScore = Integer.MAX_VALUE;
                int maxScoreForFilter = Integer.MIN_VALUE;

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (board[i][j].getText().equals("")) {
                            board[i][j].setText(humanPiece);
                            
                            DefaultMutableTreeNode tempNode = null;
                            if (recordVisuals) {
                                tempNode = new DefaultMutableTreeNode(
                                        new GameState("Gerakan Manusia", getBoardInfo(), "", false));
                            }
                            
                            int score = minimax(depth + 1, true, tempNode, maxDepth);
                            
                            if (recordVisuals && tempNode != null) {
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
                            } else {
                                bestScore = Math.min(score, bestScore);
                            }

                            board[i][j].setText("");
                        }
                    }
                }
                if (recordVisuals) {
                    if (bestBranch != null) {
                        ((GameState) bestBranch.getUserObject()).setOptimal(true);
                        parentNode.add(bestBranch);
                    }
                    if (worstBranch != null && worstBranch != bestBranch) parentNode.add(worstBranch);
                    if (drawBranch != null && drawBranch != bestBranch && drawBranch != worstBranch) parentNode.add(drawBranch);
                }
                return bestScore;
            }
        }
    
    // mendapatkan data board
    private String[] getBoardInfo() {
        String[] boardInfo = new String[9];
        int index = 0;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                boardInfo[index++] = board[i][j].getText();
            }
        }
        return boardInfo;
    }

    // evaluasi untuk melihat siapa yang menang
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

    // check siapa yang menang
    private boolean checkGameState(String pieceToCheck) {
        int score = evaluateBoard();
        if (score == 10 || score == -10 || isBoardFull()) {
            gameOver = true;
            
            String resultMessage = (score == 10 || score == -10) ? 
                (pieceToCheck.equals(humanPiece) ? "Kamu menang!" : "AI menang!") : "Draw!";
            
            Object[] options = {"Play Again", "Liat hasil Tree", "Exit Game"};
            
            int response = JOptionPane.showOptionDialog(frame, 
                    resultMessage + "\nPilih?", 
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

    // melihat apakah board sudah full atau tidak
    private boolean isBoardFull() {
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                if (board[i][j].getText().equals("")){
                    return false;
                }
            }
        }
        return true;
    }

    // Reset board ke kondisi awal
    private void resetBoard() {
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++) {
                board[i][j].setText("");
            }
        } 
        gameOver = false;
        startGame(); 
    }

    // setup game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe());
    }
}
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class BoardTreeRenderer extends JPanel implements TreeCellRenderer {
    private JLabel titleLabel, scoreLabel;
    private JLabel[] cells;
    private JPanel centerWrapper;

    // setup render board
    public BoardTreeRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        gridPanel.setPreferredSize(new Dimension(90, 90));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        cells = new JLabel[9];
        for (int i = 0; i < 9; i++) {
            cells[i] = new JLabel("", SwingConstants.CENTER);
            cells[i].setFont(new Font("Arial", Font.BOLD, 20));
            cells[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            gridPanel.add(cells[i]);
        }
        
        centerWrapper = new JPanel(); 
        centerWrapper.add(gridPanel);
        add(centerWrapper, BorderLayout.CENTER);

        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        add(scoreLabel, BorderLayout.SOUTH);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof GameState) {
            GameState state = (GameState) node.getUserObject();
            titleLabel.setText(state.getTitle());
            scoreLabel.setText(state.getScoreInfo());
            
            String[] board = state.getBoardState();
            for (int i = 0; i < 9; i++) {
                cells[i].setText(board[i]);
                if (board[i].equals("X")) {
                    cells[i].setForeground(Color.BLUE);
                }else if (board[i].equals("O")) {
                    cells[i].setForeground(Color.RED);
                }else {
                    cells[i].setForeground(Color.BLACK);
                }
            }

            if (state.isOptimal()) {
                if (state.getRawScore() > 0) { 
                    setBorder(BorderFactory.createLineBorder(new Color(40, 167, 69), 8));
                    setBackground(new Color(200, 255, 210));
                    centerWrapper.setBackground(new Color(200, 255, 210));
                } else if (state.getRawScore() < 0) { 
                    setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 8));
                    setBackground(new Color(255, 210, 210));
                    centerWrapper.setBackground(new Color(255, 210, 210));
                } else { 
                    setBorder(BorderFactory.createLineBorder(new Color(253, 126, 20), 8));
                    setBackground(new Color(255, 235, 200));
                    centerWrapper.setBackground(new Color(255, 235, 200));
                }
            } else {
                setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                setBackground(Color.WHITE);
                centerWrapper.setBackground(Color.WHITE);
            }
        }
        return this;
    }
}
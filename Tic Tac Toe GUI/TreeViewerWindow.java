import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeViewerWindow {
    public static void display(DefaultMutableTreeNode treeRoot, JFrame parentFrame) {
        JFrame treeFrame = new JFrame("Final Game History Tree");
        treeFrame.setSize(1200, 800);
        treeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        CustomTreeCanvas canvas = new CustomTreeCanvas(treeRoot);
        treeFrame.add(canvas);
        treeFrame.setLocationRelativeTo(parentFrame);
        treeFrame.setVisible(true);
    }
}

class CustomTreeCanvas extends JPanel {
    private DefaultMutableTreeNode root;
    private Map<DefaultMutableTreeNode, Point> nodeLocations;
    
    private final int NODE_WIDTH = 130; 
    private final int NODE_HEIGHT = 180;
    private final int HORIZONTAL_GAP = 60;
    private final int VERTICAL_GAP = 80;

    private double scale = 1.0;
    private double translateX = 0;
    private double translateY = 0;
    private int lastMouseX, lastMouseY;

    private BoardTreeRenderer cellRenderer;
    
    private CellRendererPane rendererPane;

    public CustomTreeCanvas(DefaultMutableTreeNode root) {
        this.root = root;
        this.nodeLocations = new HashMap<>();
        this.cellRenderer = new BoardTreeRenderer();
        
        this.rendererPane = new CellRendererPane();
        add(rendererPane);
        
        setBackground(new Color(245, 245, 250));
        calculateLayout(root, 50, 50);

        Point rootLoc = nodeLocations.get(root);
        
        if (rootLoc != null) {
            translateX = (1200 / 2.0) - rootLoc.x - (NODE_WIDTH / 2.0);
            translateY = 50; // 50px margin from the top
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX(); lastMouseY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                translateX += e.getX() - lastMouseX;
                translateY += e.getY() - lastMouseY;
                lastMouseX = e.getX(); lastMouseY = e.getY();
                repaint(); 
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) scale *= 1.1; 
            else scale /= 1.1; 
            repaint(); 
        });
    }

    private int calculateLayout(DefaultMutableTreeNode node, int startX, int y) {
        int currentX = startX;
        if (node.isLeaf()) {
            nodeLocations.put(node, new Point(currentX, y));
            return currentX + NODE_WIDTH + HORIZONTAL_GAP;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            currentX = calculateLayout((DefaultMutableTreeNode) node.getChildAt(i), currentX, y + NODE_HEIGHT + VERTICAL_GAP);
        }
        Point firstChild = nodeLocations.get((DefaultMutableTreeNode) node.getFirstChild());
        Point lastChild = nodeLocations.get((DefaultMutableTreeNode) node.getLastChild());
        nodeLocations.put(node, new Point((firstChild.x + lastChild.x) / 2, y));
        return currentX; 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(translateX, translateY);
        g2.scale(scale, scale);

        g2.setColor(new Color(150, 150, 150));
        g2.setStroke(new BasicStroke(3));
        drawLines(g2, root);
        drawNodes(g2, root);
    }

    private void drawLines(Graphics2D g2, DefaultMutableTreeNode node) {
        Point p1 = nodeLocations.get(node);
        if (p1 == null) return;
        
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            Point p2 = nodeLocations.get(child);
            
            if (p2 != null) {
                GameState childState = (GameState) child.getUserObject();
                
                if (childState.isActualGamePath()) {
                    g2.setStroke(new BasicStroke(10)); 
                    g2.setColor(new Color(0, 123, 255)); // Bright Blue
                } else if (childState.isOptimal()) {
                    g2.setStroke(new BasicStroke(6));
                    if (childState.getRawScore() > 0) {
                        g2.setColor(new Color(40, 167, 69)); // Green
                    } else if (childState.getRawScore() < 0) {
                        g2.setColor(new Color(220, 53, 69)); // Red
                    } else {
                        g2.setColor(new Color(253, 126, 20)); // Orange
                    }
                } else {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(new Color(200, 200, 200)); // Light Gray
                }

                g2.drawLine(p1.x + (NODE_WIDTH / 2), p1.y + NODE_HEIGHT, p2.x + (NODE_WIDTH / 2), p2.y);
                
             
                drawLines(g2, child);
            }
        }
    }

    private void drawNodes(Graphics2D g2, DefaultMutableTreeNode node) {
        Point p = nodeLocations.get(node);
        if (p != null) {
            Component stamp = cellRenderer.getTreeCellRendererComponent(new JTree(), node, false, false, node.isLeaf(), 0, false);
            rendererPane.paintComponent(g2, stamp, this, p.x, p.y, NODE_WIDTH, NODE_HEIGHT, true);
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            drawNodes(g2, (DefaultMutableTreeNode) node.getChildAt(i));
        }
    }
}
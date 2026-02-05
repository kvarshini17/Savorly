import javax.swing.*;
import java.awt.*;

// ====== Main Recipe Manager UI ======
public class RecipeManagerUI {
    public static void main(String[] args) {
           DatabaseHelper.initializeDatabase();
        SwingUtilities.invokeLater(RecipeManagerUI::new);
    }

    public RecipeManagerUI() {
        JFrame frame = new JFrame("Savourly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout(5, 5));

        // ===== Black background for frame border =====
        frame.getContentPane().setBackground(Color.BLACK);

        // ===== Main beige content panel inside black frame =====
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(new Color(245, 245, 220));
        frame.add(mainPanel, BorderLayout.CENTER);

        ImageIcon image = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(image.getImage());

        // ===== Top Banner (Cherry Red with Title) =====
        JPanel bannerPanel = new JPanel();
        bannerPanel.setBackground(new Color(139, 0, 0));
        bannerPanel.setPreferredSize(new Dimension(1000, 120));
        bannerPanel.setLayout(new BorderLayout());

        JLabel bannerLabel = new JLabel("Cookbook Recipes", SwingConstants.CENTER);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setFont(new Font("Serif", Font.BOLD, 36));
        bannerPanel.add(bannerLabel, BorderLayout.CENTER);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ===== Center Panel (Buttons) =====
        JPanel buttonPanel = new JPanel(new GridBagLayout()); // centers vertically + horizontally
        buttonPanel.setBackground(new Color(245, 245, 220));

        JPanel innerButtonPanel = new JPanel(new GridLayout(4, 1, 15, 20));
        innerButtonPanel.setBackground(new Color(245, 245, 220));

        JButton viewBtn = new OvalButton("🏠  View Recipe");
        viewBtn.addActionListener(_ -> {
            frame.dispose();         // close current window
            new ViewPage();          // open ViewPage window
        });

        JButton favBtn = new OvalButton("❤  Favorite List");
        favBtn.addActionListener(_ -> {
            frame.dispose();          
            new FavoritesPage();      
        });

        JButton addBtn = new OvalButton("➕  Add Recipe");
        addBtn.addActionListener(_ -> {
            frame.dispose();
            new AddRecipePage();       
        });

        JButton bmiBtn = new OvalButton("⚖  Check BMI");
        bmiBtn.addActionListener(_ -> {
            frame.dispose();           
            new BMICheckerpage();      
        });

        innerButtonPanel.add(viewBtn);
        innerButtonPanel.add(favBtn);
        innerButtonPanel.add(addBtn);
        innerButtonPanel.add(bmiBtn);

        buttonPanel.add(innerButtonPanel);

        // ===== Wrap buttons panel to add solid black dividing line =====
        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setBackground(new Color(245, 245, 220));
        buttonWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLACK)); // solid black line on right
        buttonWrapper.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(buttonWrapper, BorderLayout.CENTER);

        // ===== Right Panel (Quote) =====
        JPanel quotePanel = new JPanel(new BorderLayout());
        quotePanel.setBackground(new Color(245, 245, 220));
        quotePanel.setPreferredSize(new Dimension(300, 0));

        JLabel quoteLabel = new JLabel(
                "<html><center><h3 style='color:black;'>Quote of the Day</h3>" +
                        "<p style='color:black;'>\"Cooking is love made visible.\"</p></center></html>",
                SwingConstants.CENTER);
        quotePanel.add(quoteLabel, BorderLayout.CENTER);

        mainPanel.add(quotePanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    // ===== OvalButton Class Inside RecipeManagerUI =====
    class OvalButton extends JButton {
        private boolean hover = false;

        public OvalButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(220, 55));

            // Hover effect listener
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw shadow
            g2.setColor(new Color(80, 0, 0, 100));
            g2.fillOval(4, 4, w - 4, h - 4);

            // Main color (maroon with lighter hover)
            Color fill = hover ? new Color(178, 34, 34) : new Color(128, 0, 0);
            g2.setColor(fill);
            g2.fillOval(0, 0, w - 4, h - 4);

            // Draw text centered
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), (w - textWidth) / 2, (h + textHeight / 2) / 2);

            g2.dispose();
        }
    }
}

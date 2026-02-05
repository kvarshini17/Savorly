import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ChickenRecipes {

    private static final String RECIPE_FILE = "recipes.txt";
    private JPanel gridPanel;
    private JFrame frame;

    public ChickenRecipes() {
        frame = new JFrame("Chicken Recipes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon image = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(image.getImage());

        // 🔹 Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(139, 0, 0));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(60, 50));
        backButton.setOpaque(true);
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(_ -> {
            frame.dispose();
            new ViewPage();
        });

        JLabel titleLabel = new JLabel("Chicken Recipes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔹 Scrollable grid
        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(new Color(245, 245, 220));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Load default + saved dishes
        List<String[]> allDishes = loadChickenDishes();

        for (String[] dish : allDishes) {
            JButton btn = createDishButton(dish[0], dish[1]);
            gridPanel.add(btn);
        }

        // Add New Dish button
        JButton addBtn = new JButton("Add New Dish");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addBtn.setBackground(new Color(128, 0, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 0, 0), 2, true));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.setIcon(new ImageIcon(new ImageIcon("plus.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        addBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        addBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        addBtn.addActionListener(_ -> {
            frame.dispose();
            new AddRecipePage(); // Opens the Add Recipe page
        });
        gridPanel.add(addBtn);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JButton createDishButton(String name, String imgPath) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setBackground(new Color(128, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 0, 0), 2, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        try {
            BufferedImage originalImage = ImageIO.read(new File(imgPath));
            Image scaled = makeRoundedImage(originalImage, 50)
                    .getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            System.out.println("Image not found: " + imgPath);
        }

        btn.addActionListener(_ -> {
            JOptionPane.showMessageDialog(frame, "Opening " + name + " recipe details soon!");
        });

        return btn;
    }

    private List<String[]> loadChickenDishes() {
        List<String[]> dishes = new ArrayList<>();

        // Default dishes
        dishes.add(new String[]{"Grilled Chicken", "grilledchicken.png"});
        dishes.add(new String[]{"Chicken Curry", "chickencurry.png"});
        dishes.add(new String[]{"Fried Chicken", "friedchicken.png"});
        dishes.add(new String[]{"BBQ Chicken", "bbqchicken.png"});

        // Load user-added chicken recipes from recipes.txt
        File f = new File(RECIPE_FILE);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Expected format: category|title|time|serves|calories|ingredients|instructions|image
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2 && parts[0].equalsIgnoreCase("Chicken")) {
                        String title = parts[1];
                        String image = (parts.length >= 8) ? parts[7] : "chicken.png";
                        dishes.add(new String[]{title, image});
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dishes;
    }

    private Image makeRoundedImage(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChickenRecipes::new);
    }
}

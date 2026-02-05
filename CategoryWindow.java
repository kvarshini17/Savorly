import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CategoryWindow {

    private static final String RECIPES_FILE = "recipes.txt";

    public CategoryWindow(String categoryName) {
        JFrame frame = new JFrame("Savorly");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon image = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(image.getImage());

        // 🔹 Top Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(139, 0, 0));
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(60, 50));
        backButton.addActionListener(_ -> {
            frame.dispose();
            new ViewPage();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(categoryName + " Recipes", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔹 Grid panel (3 columns fixed)
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(new Color(245, 245, 220));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 🔹 Add New Dish Button (match ViewPage button size)
        JButton addDishBtn = new JButton("Add New Dish");
        addDishBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addDishBtn.setBackground(new Color(128, 0, 0));
        addDishBtn.setForeground(Color.WHITE);
        addDishBtn.setFocusPainted(false);
        addDishBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 0, 0), 2, true));
        addDishBtn.setPreferredSize(new Dimension(150, 150)); // same as ViewPage buttons
        addDishBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addDishBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        addDishBtn.setVerticalTextPosition(SwingConstants.BOTTOM);

        File plusIconFile = new File("plus.png");
        if (plusIconFile.exists()) {
            addDishBtn.setIcon(new ImageIcon(
                    new ImageIcon("plus.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)
            ));
        }

        addDishBtn.addActionListener(_ -> {
            frame.dispose();
            new AddRecipePage(categoryName);
        });

        gridPanel.add(addDishBtn);

        // 🔹 Load recipe buttons
        loadRecipes(categoryName, gridPanel, frame);

        frame.setVisible(true);
    }

    private void loadRecipes(String categoryName, JPanel gridPanel, JFrame frame) {
        File file = new File(RECIPES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8 && parts[0].equalsIgnoreCase(categoryName)) {
                    String recipeTitle = parts[1];
                    String imagePath = parts[7];

                    JButton recipeButton = new JButton(recipeTitle);
                    recipeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
                    recipeButton.setBackground(new Color(139, 0, 0));
                    recipeButton.setForeground(Color.WHITE);
                    recipeButton.setPreferredSize(new Dimension(150, 150)); // match ViewPage
                    recipeButton.setFocusPainted(false);
                    recipeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    if (!imagePath.isEmpty()) {
                        ImageIcon icon = new ImageIcon(imagePath);
                        Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // rounded square image
                        recipeButton.setIcon(new ImageIcon(scaled));
                        recipeButton.setHorizontalTextPosition(SwingConstants.CENTER);
                        recipeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                    }

                    recipeButton.addActionListener(_ -> {
                        frame.dispose();
                        new RecipeDetailsPage(categoryName, recipeTitle);
                    });

                    gridPanel.add(recipeButton);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

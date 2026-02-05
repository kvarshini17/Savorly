import javax.swing.*;
import java.awt.*;  
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsPage {

    private static final String RECIPES_FILE = "recipes.txt";
    private static final String FAVORITES_FILE = "favorites.txt";

    public RecipeDetailsPage(String categoryName, String recipeTitle) {
        JFrame frame = new JFrame("Savorly");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon logo = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(logo.getImage());

        // ===== Header =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(139, 0, 0));
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(_ -> {
            frame.dispose();
            new CategoryWindow(categoryName);
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(recipeTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);

        // Pencil (edit) button ✎
        JButton pencilButton = new JButton("✎");
        pencilButton.setFont(new Font("SansSerif", Font.BOLD, 35));
        pencilButton.setForeground(Color.WHITE);
        pencilButton.setFocusPainted(false);
        pencilButton.setBorderPainted(false);
        pencilButton.setContentAreaFilled(false);
        pencilButton.addActionListener(_ -> new AddRecipePage(categoryName, recipeTitle));

        // Heart toggle ♡❤
        JButton heartButton = new JButton(isFavorite(recipeTitle) ? "❤" : "♡");
        heartButton.setFont(new Font("SansSerif", Font.BOLD, 35));
        heartButton.setForeground(Color.WHITE);
        heartButton.setFocusPainted(false);
        heartButton.setBorderPainted(false);
        heartButton.setContentAreaFilled(false);

        heartButton.addActionListener(_ -> {
            boolean favorite = isFavorite(recipeTitle);
            if (favorite) {
                removeFromFavorites(recipeTitle);
                heartButton.setText("♡");
            } else {
                addToFavorites(recipeTitle);
                heartButton.setText("❤");
            }
        });

        // 🗑 Delete button
        JButton deleteButton = new JButton("🗑");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 35));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);

        deleteButton.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this recipe?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteRecipe(categoryName, recipeTitle);
                JOptionPane.showMessageDialog(frame, "Recipe deleted successfully!");
                frame.dispose();
                new CategoryWindow(categoryName);
            }
        });

        // Icons panel
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topRightPanel.setOpaque(false);
        topRightPanel.add(pencilButton);
        topRightPanel.add(deleteButton);
        topRightPanel.add(heartButton);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(topRightPanel, BorderLayout.EAST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ===== Load recipe data =====
        String[] recipeData = getRecipeData(categoryName, recipeTitle);
        if (recipeData == null) {
            JOptionPane.showMessageDialog(frame, "Recipe not found in recipes.txt", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String time = recipeData[2];
        String serves = recipeData[3];
        String calories = recipeData[4];
        String ingredients = recipeData[5].replace("\\n", "\n");
        String instructions = recipeData[6].replace("\\n", "\n");
        String imagePath = recipeData[7];

        // ===== Main content =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 220));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(245, 245, 220));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel ingHeader = new JLabel("Ingredients:");
        ingHeader.setFont(new Font("Serif", Font.BOLD, 22));
        ingHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(ingHeader);

        for (String ing : ingredients.split("\n")) {
            JCheckBox box = new JCheckBox(ing.trim());
            box.setBackground(new Color(245, 245, 220));
            box.setFont(new Font("SansSerif", Font.PLAIN, 16));
            leftPanel.add(box);
        }

        JLabel instrHeader = new JLabel("Instructions:");
        instrHeader.setFont(new Font("Serif", Font.BOLD, 22));
        instrHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        instrHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        leftPanel.add(instrHeader);

        int stepNum = 1;
        for (String step : instructions.split("\n")) {
            JCheckBox box = new JCheckBox("Step " + (stepNum++) + ": " + step.trim());
            box.setBackground(new Color(245, 245, 220));
            box.setFont(new Font("SansSerif", Font.PLAIN, 16));
            leftPanel.add(box);
        }

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Center panel (image + info + rating)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 245, 220));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaled = icon.getImage().getScaledInstance(400, 250, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                centerPanel.add(imgLabel);
            }
        }

        JLabel infoLabel = new JLabel("⏱ " + time + "     🍽 Serves: " + serves + "     🔥 " + calories + " kcal",
                SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        centerPanel.add(infoLabel);

        JLabel ratingHeader = new JLabel("Rating:");
        ratingHeader.setFont(new Font("Serif", Font.BOLD, 22));
        ratingHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(ratingHeader);

        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        starsPanel.setBackground(new Color(245, 245, 220));
        JButton[] stars = new JButton[5];
        for (int i = 0; i < 5; i++) {
            final int index = i;
            JButton star = new JButton("★");
            star.setFont(new Font("SansSerif", Font.BOLD, 24));
            star.setForeground(Color.GRAY);
            star.setFocusPainted(false);
            star.setBorderPainted(false);
            star.setContentAreaFilled(false);
            star.addActionListener(_ -> {
                for (int j = 0; j < 5; j++) {
                    stars[j].setForeground(j <= index ? Color.ORANGE : Color.GRAY);
                }
            });
            stars[i] = star;
            starsPanel.add(star);
        }
        centerPanel.add(starsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ===== Helper Methods =====
    private String[] getRecipeData(String category, String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECIPES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8 &&
                        parts[0].equalsIgnoreCase(category) &&
                        parts[1].equalsIgnoreCase(title)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isFavorite(String title) {
        File file = new File(FAVORITES_FILE);
        if (!file.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().anyMatch(line -> line.equalsIgnoreCase(title));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addToFavorites(String title) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FAVORITES_FILE, true))) {
            writer.write(title);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromFavorites(String title) {
        File file = new File(FAVORITES_FILE);
        if (!file.exists()) return;
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equalsIgnoreCase(title)) lines.add(line);
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecipe(String category, String title) {
        File file = new File(RECIPES_FILE);
        if (!file.exists()) return;
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", -1);
                    if (!(parts.length >= 2 && parts[0].equalsIgnoreCase(category) && parts[1].equalsIgnoreCase(title))) {
                        lines.add(line);
                    }
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

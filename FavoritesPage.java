import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class FavoritesPage {
    private static final String FAVORITES_FILE = "favorites.txt";
    private static final String RECIPES_FILE = "recipes.txt";

    public FavoritesPage() {
        JFrame frame = new JFrame("Savorly");
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon logo = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(logo.getImage());

        // 🔹 Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(139, 0, 0));
        header.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JButton backBtn = new JButton("←");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(139, 0, 0));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(_ -> {
            frame.dispose();
            new RecipeManagerUI();
        });

        JLabel title = new JLabel("Favorite List", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        frame.add(header, BorderLayout.NORTH);

        // 🔹 Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 220));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.add(scrollPane, BorderLayout.CENTER);

        java.util.List<String> favorites = loadFavorites();

        if (favorites.isEmpty()) {
            JLabel noFav = new JLabel("No favorite recipes found.", SwingConstants.CENTER);
            noFav.setFont(new Font("SansSerif", Font.PLAIN, 18));
            noFav.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));
            contentPanel.add(noFav);
        } else {
            for (String fav : favorites) {
                String[] recipeData = findRecipeData(fav);
                if (recipeData != null) {
                    String category = recipeData[0];
                    String titleText = recipeData[1];
                    String imagePath = recipeData[7];

                    JPanel recipePanel = createRecipePanel(titleText, imagePath, category);
                    contentPanel.add(recipePanel);
                }
            }
        }

        frame.setVisible(true);
    }

    private java.util.List<String> loadFavorites() {
        java.util.List<String> list = new ArrayList<>();
        File file = new File(FAVORITES_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) list.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String[] findRecipeData(String recipeTitle) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECIPES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8 && parts[1].equalsIgnoreCase(recipeTitle)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JPanel createRecipePanel(String title, String imagePath, String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(800, 120));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.setBackground(new Color(245, 245, 220));

        // 🔹 Image
        JLabel imgLabel;
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            imgLabel = new JLabel(new ImageIcon(scaled));
        } else {
            imgLabel = new JLabel("IMG", SwingConstants.CENTER);
            imgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            imgLabel.setPreferredSize(new Dimension(90, 90));
            imgLabel.setOpaque(true);
            imgLabel.setBackground(Color.GRAY);
            imgLabel.setForeground(Color.WHITE);
        }
        imgLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(imgLabel, BorderLayout.WEST);

        // 🔹 Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 30, 10));
        panel.add(titleLabel, BorderLayout.CENTER);

        // 🔹 Clickable
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RecipeDetailsPage(category, title);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(255, 240, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 220));
            }
        });

        return panel;
    }
}

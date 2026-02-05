import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class ViewPage {

    private static final String RECIPES_FILE = "recipes.txt";
    private static final String CATEGORIES_FILE = "categories.txt";

    public ViewPage() {
        JFrame frame = new JFrame("Savorly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(_ -> {
            frame.dispose();
            new RecipeManagerUI();
        });

        JLabel titleLabel = new JLabel("CookBook Recipes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleWrapper.setOpaque(false);
        titleWrapper.add(titleLabel);

        topPanel.add(titleWrapper, BorderLayout.CENTER);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        searchField.setBackground(Color.WHITE);
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.setToolTipText("Search dishes...");

        JButton searchButton = new JButton("🔍");
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchButton.setFocusPainted(false);
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel roundedSearchPanel = new JPanel(new BorderLayout(5, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        roundedSearchPanel.setOpaque(false);
        roundedSearchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        roundedSearchPanel.add(searchField, BorderLayout.CENTER);
        roundedSearchPanel.add(searchButton, BorderLayout.EAST);
        roundedSearchPanel.setPreferredSize(new Dimension(250, 40));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(roundedSearchPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔹 Grid for categories
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(new Color(245, 245, 220));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Load category images
        Map<String, String> categoryImages = loadCategoryImages();

        // Load recipes
        List<Recipe> allRecipes = loadAllRecipes();
        allRecipes.sort(Comparator.comparing(r -> r.dishName.toLowerCase()));

        // 🔹 Update grid with category buttons
        updateCategoryGrid(gridPanel, categoryImages, frame);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 🚫 remove horizontal bar
        frame.add(scrollPane, BorderLayout.CENTER);

        // 🔹 Autocomplete popup (scrollable & themed)
        JPopupMenu suggestionPopup = new JPopupMenu();
        suggestionPopup.setFocusable(false);
        suggestionPopup.setBackground(new Color(245, 245, 220));

        JPanel suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        suggestionPanel.setBackground(new Color(245, 245, 220));

        JScrollPane suggestionScroll = new JScrollPane(suggestionPanel);
        suggestionScroll.setPreferredSize(new Dimension(250, 120));
        suggestionScroll.setBorder(BorderFactory.createLineBorder(new Color(139, 0, 0), 2));
        suggestionScroll.getVerticalScrollBar().setUnitIncrement(12);
        suggestionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 🚫 no horizontal scroll

        suggestionPopup.add(suggestionScroll);

        Runnable updateSuggestions = () -> {
            String query = searchField.getText().trim().toLowerCase();
            suggestionPanel.removeAll();

            int count = 0;
            for (Recipe r : allRecipes) {
                if (r.dishName.toLowerCase().contains(query)) {
                    JButton item = new JButton(r.dishName + " (" + r.category + ")");
                    item.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    item.setBackground(new Color(245, 245, 220));
                    item.setForeground(new Color(80, 0, 0));
                    item.setFocusPainted(false);
                    item.setHorizontalAlignment(SwingConstants.LEFT);
                    item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    item.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            item.setBackground(new Color(255, 204, 203));
                        }

                        public void mouseExited(java.awt.event.MouseEvent e) {
                            item.setBackground(new Color(245, 245, 220));
                        }
                    });

                    item.addActionListener(_ -> {
                        searchField.setText(r.dishName);
                        suggestionPopup.setVisible(false);
                        frame.dispose();
                        new RecipeDetailsPage(r.category, r.dishName);
                    });

                    suggestionPanel.add(item);
                    count++;
                }
            }

            if (count == 0) {
                JLabel noResults = new JLabel("No results found", SwingConstants.CENTER);
                noResults.setFont(new Font("SansSerif", Font.BOLD, 14));
                noResults.setForeground(new Color(128, 0, 0));
                noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
                suggestionPanel.add(noResults);
            }

            suggestionPanel.revalidate();
            suggestionPanel.repaint();
            suggestionPopup.show(searchField, 0, searchField.getHeight());
        };

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions.run(); }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                updateSuggestions.run();
            }
        });

        frame.setVisible(true);
    }

    @SuppressWarnings("unused")
    private static class Recipe {
        String category, dishName, time, servings, calories, ingredients, steps, imagePath;

        public Recipe(String category, String dishName, String time, String servings, String calories,
                      String ingredients, String steps, String imagePath) {
            this.category = category;
            this.dishName = dishName;
            this.time = time;
            this.servings = servings;
            this.calories = calories;
            this.ingredients = ingredients;
            this.steps = steps;
            this.imagePath = imagePath;
        }
    }

    private List<Recipe> loadAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        File f = new File(RECIPES_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8) {
                    list.add(new Recipe(parts[0], parts[1], parts[2], parts[3], parts[4],
                            parts[5], parts[6], parts[7]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private Map<String, String> loadCategoryImages() {
        Map<String, String> map = new LinkedHashMap<>();
        File f = new File(CATEGORIES_FILE);
        if (!f.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) map.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) { e.printStackTrace(); }
        return map;
    }

    private void updateCategoryGrid(JPanel gridPanel, Map<String, String> categoryImages, JFrame frame) {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(0, 3, 20, 20));

        for (String category : categoryImages.keySet()) {
            String imgPath = categoryImages.get(category);
            JButton btn = new JButton(category);
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setBackground(new Color(128, 0, 0));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            try {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    BufferedImage img = ImageIO.read(imgFile);
                    Image scaled = makeRoundedImage(img, 50, 150, 150);
                    btn.setIcon(new ImageIcon(scaled));
                }
            } catch (Exception ignored) {}

            btn.addActionListener(_ -> {
                frame.dispose();
                new CategoryWindow(category);
            });

            gridPanel.add(btn);
        }

        JButton addBtn = new JButton("Add New Category");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addBtn.setBackground(new Color(128, 0, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(_ -> {
            frame.dispose();
            new AddRecipePage();
        });
        gridPanel.add(addBtn);

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private Image makeRoundedImage(BufferedImage image, int cornerRadius, int width, int height) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return output;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewPage::new);
    }
}

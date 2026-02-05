import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AddRecipePage extends JFrame {

    private static final String RECIPES_FILE = "recipes.txt";
    private static final String CATEGORIES_FILE = "categories.txt";

    private JTextField titleField, timeField, servesField, caloriesField;
    private JTextArea ingredientsArea, instructionsArea;
    private JComboBox<String> categoryDropdown;
    private File selectedImageFile = null;
    private String preselectedCategory;
    private String originalTitle = null; // for editing

    // New constructor to edit existing recipe
    public AddRecipePage(String categoryName, String recipeTitle) {
        this.preselectedCategory = categoryName;
        this.originalTitle = recipeTitle;
        initUI();
        loadRecipeData(categoryName, recipeTitle);
    }

    public AddRecipePage(String categoryName) {
        this.preselectedCategory = categoryName;
        initUI();
    }

    public AddRecipePage() {
        this(null);
    }

    private void initUI() {
        JFrame frame = new JFrame("Savorly");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon image = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(image.getImage());

        // 🔹 Top banner
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(139, 0, 0));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(60, 60));
        backButton.addActionListener(_ -> {
            frame.dispose();
            if (preselectedCategory != null)
                new CategoryWindow(preselectedCategory);
            else
                new RecipeManagerUI();
        });

        JLabel bannerLabel = new JLabel(originalTitle != null ? "Edit Recipe" : "Add Recipe", SwingConstants.CENTER);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setFont(new Font("Serif", Font.BOLD, 36));

        bannerPanel.add(backButton, BorderLayout.WEST);
        bannerPanel.add(bannerLabel, BorderLayout.CENTER);
        frame.add(bannerPanel, BorderLayout.NORTH);

        // 🔹 Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font textFont = new Font("SansSerif", Font.PLAIN, 16);
        int y = 0;

        // Category dropdown
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(labelFont);
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        categoryDropdown = new JComboBox<>();
        categoryDropdown.setFont(textFont);
        categoryDropdown.setPreferredSize(new Dimension(250, 30));
        loadCategories();
        categoryDropdown.addItem("➕ Add New Category");

        if (preselectedCategory != null) categoryDropdown.setSelectedItem(preselectedCategory);

        categoryDropdown.addActionListener(_ -> {
            String selected = (String) categoryDropdown.getSelectedItem();
            if ("➕ Add New Category".equals(selected)) {
                addNewCategoryDialog();
            }
        });

        formPanel.add(categoryDropdown, gbc);
        y++;

        // Title, Time, Serves, Calories
        titleField = addField(formPanel, gbc, "Title:", y++, labelFont, textFont);
        timeField = addField(formPanel, gbc, "Time:", y++, labelFont, textFont);
        servesField = addField(formPanel, gbc, "Serves:", y++, labelFont, textFont);
        caloriesField = addField(formPanel, gbc, "Calories:", y++, labelFont, textFont);

        // Ingredients
        gbc.gridx = 0; gbc.gridy = y;
        JLabel ingLabel = new JLabel("Ingredients:");
        ingLabel.setFont(labelFont);
        formPanel.add(ingLabel, gbc);
        gbc.gridx = 1;
        ingredientsArea = new JTextArea(5, 30);
        ingredientsArea.setFont(textFont);
        ingredientsArea.setLineWrap(true);
        formPanel.add(new JScrollPane(ingredientsArea), gbc);
        y++;

        // Instructions
        gbc.gridx = 0; gbc.gridy = y;
        JLabel instrLabel = new JLabel("Instructions:");
        instrLabel.setFont(labelFont);
        formPanel.add(instrLabel, gbc);
        gbc.gridx = 1;
        instructionsArea = new JTextArea(5, 30);
        instructionsArea.setFont(textFont);
        instructionsArea.setLineWrap(true);
        formPanel.add(new JScrollPane(instructionsArea), gbc);
        y++;

        // Image button
        gbc.gridx = 0; gbc.gridy = y;
        JLabel imgLabel = new JLabel("Add Image:");
        imgLabel.setFont(labelFont);
        formPanel.add(imgLabel, gbc);
        gbc.gridx = 1;
        JButton imgButton = new JButton("Choose Image");
        imgButton.setFont(textFont);
        imgButton.addActionListener(_ -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = chooser.getSelectedFile();
                JOptionPane.showMessageDialog(frame, "Selected: " + selectedImageFile.getName());
            }
        });
        formPanel.add(imgButton, gbc);
        y++;

        frame.add(formPanel, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton(originalTitle != null ? "💾 Update Recipe" : "💾 Save Recipe");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        saveButton.setBackground(new Color(139, 0, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.addActionListener(_ -> saveRecipe(frame));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 220));
        bottomPanel.add(saveButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int y, Font labelFont, Font textFont) {
        gbc.gridx = 0; gbc.gridy = y;
        JLabel l = new JLabel(label);
        l.setFont(labelFont);
        panel.add(l, gbc);
        gbc.gridx = 1;
        JTextField f = new JTextField(25);
        f.setFont(textFont);
        panel.add(f, gbc);
        return f;
    }

    private void loadCategories() {
        categoryDropdown.removeAllItems();
        File file = new File(CATEGORIES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length >= 1 && !parts[0].trim().isEmpty())
                        categoryDropdown.addItem(parts[0].trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadRecipeData(String category, String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECIPES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8 &&
                        parts[0].equalsIgnoreCase(category) &&
                        parts[1].equalsIgnoreCase(title)) {
                    categoryDropdown.setSelectedItem(parts[0]);
                    titleField.setText(parts[1]);
                    timeField.setText(parts[2]);
                    servesField.setText(parts[3]);
                    caloriesField.setText(parts[4]);
                    ingredientsArea.setText(parts[5].replace("\\n","\n"));
                    instructionsArea.setText(parts[6].replace("\\n","\n"));
                    if(!parts[7].isEmpty()) selectedImageFile = new File(parts[7]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewCategoryDialog() {
        JTextField nameField = new JTextField(20);
        JButton imageButton = new JButton("Choose Image");
        final File[] imageFile = {null};

        imageButton.addActionListener(_ -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                imageFile[0] = chooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Selected: " + imageFile[0].getName());
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Category Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Image:"));
        panel.add(imageButton);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Category",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newCategory = nameField.getText().trim();
            if (!newCategory.isEmpty() && !categoryExists(newCategory)) {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(CATEGORIES_FILE, true)))) {
                    out.println(newCategory + "|" + ((imageFile[0] != null) ? imageFile[0].getAbsolutePath() : ""));
                    JOptionPane.showMessageDialog(this, "New category added: " + newCategory);

                    loadCategories();
                    categoryDropdown.addItem("➕ Add New Category");
                    categoryDropdown.setSelectedItem(newCategory);

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving category.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Category already exists!");
            }
        }
    }

    private boolean categoryExists(String category) {
        File file = new File(CATEGORIES_FILE);
        if (!file.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith(category.toLowerCase() + "|") ||
                        line.equalsIgnoreCase(category)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveRecipe(JFrame frame) {
        String category = (String) categoryDropdown.getSelectedItem();
        String title = titleField.getText().trim();
        String time = timeField.getText().trim();
        String serves = servesField.getText().trim();
        String calories = caloriesField.getText().trim();
        String ingredients = ingredientsArea.getText().trim().replace("\n", "\\n");
        String instructions = instructionsArea.getText().trim().replace("\n", "\\n");
        String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : "";

        if (category == null || title.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.");
            return;
        }

        try {
            File tempFile = new File("temp_recipes.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                 BufferedReader reader = new BufferedReader(new FileReader(RECIPES_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", -1);
                    if (originalTitle != null && parts.length >= 2 &&
                        parts[0].equalsIgnoreCase(preselectedCategory) &&
                        parts[1].equalsIgnoreCase(originalTitle)) {
                        // skip old line (editing)
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
                // Write new/updated recipe
                writer.write(category + "|" + title + "|" + time + "|" + serves + "|" + calories + "|" +
                        ingredients + "|" + instructions + "|" + imagePath);
                writer.newLine();
            }
            new File(RECIPES_FILE).delete();
            tempFile.renameTo(new File(RECIPES_FILE));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving recipe.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        JOptionPane.showMessageDialog(frame, originalTitle != null ? "Recipe updated!" : "Recipe saved!");
        frame.dispose();
        new CategoryWindow(category);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddRecipePage::new);
    }
}

import javax.swing.*;
import java.awt.*;

public class BMICheckerpage {

    public BMICheckerpage() {
        JFrame frame = new JFrame("Savorly");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.getContentPane().setBackground(new Color(245, 245, 220));

        ImageIcon image = new ImageIcon("Savorlylogo.png");
        frame.setIconImage(image.getImage());

        // ---------- HEADER PANEL ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(139, 0, 0));
        header.setPreferredSize(new Dimension(frame.getWidth(), 60));

        // Back Button
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
        header.add(backBtn, BorderLayout.WEST);

        // Title Label
        JLabel titleLabel = new JLabel("Check BMI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36)); // same as others
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);

        frame.add(header, BorderLayout.NORTH);

        // ---------- MAIN CONTENT PANEL ----------
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 220));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // ---------- Mode Selection ----------
        JPanel modePanel = new JPanel(new FlowLayout());
        modePanel.setBackground(new Color(245, 245, 220));

        JRadioButton metric = new JRadioButton("Metric");
        JRadioButton imperial = new JRadioButton("Imperial");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(metric);
        modeGroup.add(imperial);
        metric.setSelected(true);

        metric.setFont(new Font("SansSerif", Font.BOLD, 18));
        imperial.setFont(new Font("SansSerif", Font.BOLD, 18));
        metric.setForeground(new Color(139, 0, 0));
        imperial.setForeground(new Color(139, 0, 0));

        modePanel.add(metric);
        modePanel.add(imperial);
        mainPanel.add(modePanel);

        // ---------- Input Panel ----------
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(245, 245, 220));

        // Weight Field
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        weightPanel.setBackground(new Color(245, 245, 220));
        JLabel weightLbl = new JLabel("Weight:");
        weightLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        JTextField weightField = new JTextField(10);
        JLabel weightUnit = new JLabel("kg");
        weightUnit.setFont(new Font("SansSerif", Font.PLAIN, 16));
        weightPanel.add(weightLbl);
        weightPanel.add(weightField);
        weightPanel.add(weightUnit);

        // Height Field
        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        heightPanel.setBackground(new Color(245, 245, 220));
        JLabel heightLbl = new JLabel("Height:");
        heightLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        JTextField heightField = new JTextField(10);
        JLabel heightUnit = new JLabel("cm");
        heightUnit.setFont(new Font("SansSerif", Font.PLAIN, 16));
        heightPanel.add(heightLbl);
        heightPanel.add(heightField);
        heightPanel.add(heightUnit);

        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        inputPanel.add(weightPanel);
        inputPanel.add(heightPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------- Calculate Button ----------
        JButton calcBtn = new JButton("Calculate BMI");
        calcBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        calcBtn.setBackground(new Color(139, 0, 0));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.setPreferredSize(new Dimension(150, 50));
        inputPanel.add(calcBtn);

        // ---------- Result Display ----------
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(245, 245, 220));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel weightHeightLabel = new JLabel("", SwingConstants.CENTER);
        weightHeightLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        weightHeightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        weightHeightLabel.setForeground(new Color(80, 0, 0));

        JLabel bmiValueLabel = new JLabel("", SwingConstants.CENTER);
        bmiValueLabel.setFont(new Font("Serif", Font.BOLD, 26));
        bmiValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bmiValueLabel.setForeground(new Color(139, 0, 0));

        JLabel categoryLabel = new JLabel("", SwingConstants.CENTER);
        categoryLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryLabel.setForeground(new Color(102, 51, 0));

        resultPanel.add(weightHeightLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        resultPanel.add(bmiValueLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        resultPanel.add(categoryLabel);

        // ---------- BMI Table ----------
        String[] columnNames = {"BMI", "Category"};
        Object[][] data = {
                {"< 15", "Very Severely Underweight"},
                {"15 - 16", "Severely Underweight"},
                {"16 - 18.5", "Underweight"},
                {"18.5 - 25", "Normal (Healthy Weight)"},
                {"25 - 30", "Overweight"},
                {"30 - 35", "Moderately Obese"},
                {"35 - 40", "Severely Obese"},
                {"> 40", "Very Severely Obese"}
        };
        JTable bmiTable = new JTable(data, columnNames);
        bmiTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bmiTable.setRowHeight(28);
        bmiTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        bmiTable.getTableHeader().setBackground(new Color(139, 0, 0));
        bmiTable.getTableHeader().setForeground(Color.WHITE);
        bmiTable.setBackground(new Color(245, 245, 220));
        bmiTable.setGridColor(new Color(210, 180, 140));
        bmiTable.setShowGrid(true);
        bmiTable.setEnabled(false);

        JScrollPane tableScroll = new JScrollPane(bmiTable);
        tableScroll.getViewport().setBackground(new Color(245, 245, 220));
        tableScroll.setBackground(new Color(245, 245, 220));
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tableScroll.setPreferredSize(new Dimension(700, 250));

        // Add to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(resultPanel);
        mainPanel.add(tableScroll);

        // ---------- Outer Scroll Pane ----------
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(new Color(245, 245, 220));
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ---------- Action Listeners ----------
        calcBtn.addActionListener(_ -> {
            try {
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());

                if (imperial.isSelected()) {
                    weight *= 0.453592; // lb → kg
                    height *= 2.54;     // in → cm
                }

                double bmi = weight / Math.pow(height / 100, 2);
                String category = getBMICategory(bmi);

                weightHeightLabel.setText(String.format("Weight: %.1f kg | Height: %.1f cm", weight, height));
                bmiValueLabel.setText(String.format("Your BMI: %.2f", bmi));
                categoryLabel.setText(category);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Dynamic unit switching
        metric.addActionListener(_ -> {
            weightUnit.setText("kg");
            heightUnit.setText("cm");
        });
        imperial.addActionListener(_ -> {
            weightUnit.setText("lb");
            heightUnit.setText("in");
        });

        frame.setVisible(true);
    }

    private String getBMICategory(double bmi) {
        if (bmi < 15) return "Very Severely Underweight";
        if (bmi < 16) return "Severely Underweight";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal (Healthy Weight)";
        if (bmi < 30) return "Overweight";
        if (bmi < 35) return "Moderately Obese";
        if (bmi < 40) return "Severely Obese";
        return "Very Severely Obese";
    }

    public static void main(String[] args) {
        new BMICheckerpage();
    }
}

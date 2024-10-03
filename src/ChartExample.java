
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.text.DecimalFormat;

public class ChartExample {
    private DefaultPieDataset pieDataset;
    private DefaultCategoryDataset barDataset;
    private DefaultCategoryDataset lineDataset;
    private DefaultCategoryDataset areaDataset;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField;
    private JTextField valueField;
    private JFreeChart pieChart;
    private JFreeChart barChart;
    private JFreeChart lineChart;
    private JFreeChart areaChart;

    private JComboBox<String> chartSelector;
    private JPanel selectedChartPanel;

    public ChartExample() {
        initializeDatasets();

        pieChart = createPieChart(pieDataset);
        barChart = createBarChart(barDataset);
        lineChart = createLineChart(lineDataset);
        areaChart = createAreaChart(areaDataset);

        createUI();
    }

    private void initializeDatasets() {
        pieDataset = new DefaultPieDataset();
        barDataset = new DefaultCategoryDataset();
        lineDataset = new DefaultCategoryDataset();
        areaDataset = new DefaultCategoryDataset();
    }

    private JFreeChart createPieChart(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Pie Chart ",
                dataset,
                true,
                true,
                false
        );

        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "Marks {0} : ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        ((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);

        return chart;
    }

    private JFreeChart createBarChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Bar Chart ",
                "Category",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = new BarRenderer() {
            private final Paint[] colors = {
                    Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE
            };

            @Override
            public Paint getItemPaint(int row, int column) {
                return colors[column % colors.length];
            }
        };

        plot.setRenderer(renderer);

        return chart;
    }

    private JFreeChart createLineChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Line Chart ",
                "Category",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }

    private JFreeChart createAreaChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createAreaChart(
                "Area Chart ",
                "Category",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }

    private void createUI() {
        JFrame frame = new JFrame("Charts Window");
        frame.setLayout(new BorderLayout());

        chartSelector = new JComboBox<>(new String[]{"All Charts", "Pie Chart", "Bar Chart", "Line Chart", "Area Chart"});
        chartSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedChart();
            }
        });

        JPanel chartSelectionPanel = new JPanel();
        chartSelectionPanel.add(new JLabel("Select Chart:"));
        chartSelectionPanel.add(chartSelector);

        frame.add(chartSelectionPanel, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(2, 2));
        chartsPanel.add(new ChartPanel(pieChart));
        chartsPanel.add(new ChartPanel(barChart));
        chartsPanel.add(new ChartPanel(lineChart));
        chartsPanel.add(new ChartPanel(areaChart));

        selectedChartPanel = new JPanel();
        selectedChartPanel.setLayout(new BorderLayout());
        selectedChartPanel.add(chartsPanel, BorderLayout.CENTER);

        frame.add(selectedChartPanel, BorderLayout.CENTER);

        String[] columnNames = {"Category", "Value"};
        String[][] rowData = new String[0][2];
        tableModel = new DefaultTableModel(rowData, columnNames);
        dataTable = new JTable(tableModel);

        JPanel dataPanel = new JPanel(new GridLayout(3, 2));
        categoryField = new JTextField();
        valueField = new JTextField();

        dataPanel.add(new JLabel("Category:"));
        dataPanel.add(categoryField);
        dataPanel.add(new JLabel("Value:"));
        dataPanel.add(valueField);

        JButton addButton = new JButton("Add Data");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addData();
            }
        });

        dataPanel.add(addButton);

        frame.add(dataPanel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void updateSelectedChart() {
        String selectedChart = (String) chartSelector.getSelectedItem();

        if (selectedChart.equals("All Charts")) {
            selectedChartPanel.removeAll();
            JPanel chartsPanel = new JPanel(new GridLayout(2, 2));
            chartsPanel.add(new ChartPanel(pieChart));
            chartsPanel.add(new ChartPanel(barChart));
            chartsPanel.add(new ChartPanel(lineChart));
            chartsPanel.add(new ChartPanel(areaChart));
            selectedChartPanel.add(chartsPanel, BorderLayout.CENTER);
        } else {
            selectedChartPanel.removeAll();
            switch (selectedChart) {
                case "Pie Chart":
                    selectedChartPanel.add(new ChartPanel(pieChart), BorderLayout.CENTER);
                    break;
                case "Bar Chart":
                    selectedChartPanel.add(new ChartPanel(barChart), BorderLayout.CENTER);
                    break;
                case "Line Chart":
                    selectedChartPanel.add(new ChartPanel(lineChart), BorderLayout.CENTER);
                    break;
                case "Area Chart":
                    selectedChartPanel.add(new ChartPanel(areaChart), BorderLayout.CENTER);
                    break;
            }
        }

        selectedChartPanel.revalidate();
        selectedChartPanel.repaint();
    }

    private void addData() {
        String category = categoryField.getText();
        String value = valueField.getText();

        if (!category.isEmpty() && !value.isEmpty()) {
            if (!isCategoryAlreadyExists(category)) {

                tableModel.addRow(new String[]{category, value});

                double doubleValue = Double.parseDouble(value);
                pieDataset.setValue(category, doubleValue);

                barDataset.addValue(doubleValue, "Category", category);

                lineDataset.addValue(doubleValue, "Category", category);

                areaDataset.addValue(doubleValue, "Category", category);

                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/miniproject", "root", "Pranav@2004")) {
                    String insertQuery = "INSERT INTO data (category, value) VALUES (?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, category);
                        preparedStatement.setDouble(2, doubleValue);
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error inserting data into the database", "Error", JOptionPane.ERROR_MESSAGE);
                }

                categoryField.setText("");
                valueField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Category already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Category and value must not be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isCategoryAlreadyExists(String category) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String existingCategory = (String) tableModel.getValueAt(i, 0);
            if (existingCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChartExample();
        });
    }
}



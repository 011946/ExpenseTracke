package expensetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

/**
 * View class for displaying the GUI and handling user interactions.
 * Implements Observer to update when the model changes.
 */
public class TransactionView extends JFrame implements Observer {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<Category> categoryFilter;
    private JComboBox<String> sortCombo;
    private JTextField currencyField;
    private JComboBox<String> themeCombo;
    private TransactionController controller;
    private JPanel controlPanel;
    private JScrollPane scrollPane;

    /**
     * Constructs the main application window.
     * 
     * @ensures the GUI is initialized with a table, input panel, and settings panel
     */
    public TransactionView() {
        setTitle("Personal Expense Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Amount", "Date", "Category", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Control panel
        controlPanel = new JPanel(new GridLayout(2, 1));
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        JButton addTransactionButton = new JButton("Add New Transaction");
        JButton addCategoryButton = new JButton("Add New Category");
        JButton editTransactionButton = new JButton("Edit Exact Transaction");
        JButton editCategoryButton = new JButton("Edit Exact Category");
        inputPanel.add(addTransactionButton);
        inputPanel.add(addCategoryButton);
        inputPanel.add(editTransactionButton);
        inputPanel.add(editCategoryButton);
        controlPanel.add(inputPanel);

        // Settings panel
        JPanel settingsPanel = new JPanel(new FlowLayout());
        categoryFilter = new JComboBox<>();
        sortCombo = new JComboBox<>(new String[]{"date", "amount"});
        currencyField = new JTextField(3);
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        settingsPanel.add(new JLabel("Filter by Category:"));
        settingsPanel.add(categoryFilter);
        settingsPanel.add(new JLabel("Sort by:"));
        settingsPanel.add(sortCombo);
        settingsPanel.add(new JLabel("Currency:"));
        settingsPanel.add(currencyField);
        settingsPanel.add(new JLabel("Theme:"));
        settingsPanel.add(themeCombo);
        JButton deleteButton = new JButton("Delete Selected");
        settingsPanel.add(deleteButton);
        controlPanel.add(settingsPanel);

        add(controlPanel, BorderLayout.NORTH);

        // Button actions
        addTransactionButton.addActionListener(e -> showAddTransactionDialog());
        addCategoryButton.addActionListener(e -> showAddCategoryDialog());
        editTransactionButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                showEditTransactionDialog(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        editCategoryButton.addActionListener(e -> showEditCategoryDialog());
        deleteButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                notifyController("delete", selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        sortCombo.addActionListener(e -> notifyController("sort", sortCombo.getSelectedItem()));
        categoryFilter.addActionListener(e -> notifyController("filter", categoryFilter.getSelectedItem()));
        currencyField.addActionListener(e -> notifyController("currency", currencyField.getText()));
        themeCombo.addActionListener(e -> {
            notifyController("theme", themeCombo.getSelectedItem());
            updateTheme();
        });

        // Apply theme after all components are initialized
        updateTheme();
    }

    /**
     * Sets the controller for this view.
     * 
     * @param controller the controller to set
     * @requires controller != null
     * @ensures this.controller == controller
     */
    public void setController(TransactionController controller) {
        this.controller = controller;
    }

    /**
     * Notifies the controller of a user action.
     * 
     * @param action the action type
     * @param data the associated data
     * @requires action != null
     */
    private void notifyController(String action, Object data) {
        if (controller != null) {
            controller.handleAction(action, data);
        }
    }

    /**
     * Shows a dialog for adding a new transaction.
     */
    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Add New Transaction", true);
        dialog.setLayout(new GridLayout(5, 2));
        dialog.setSize(400, 300);

        // Fields for transaction details
        JTextField amountField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<Category> categoryCombo = new JComboBox<>();
        JTextField dateField = new JTextField("yyyy-mm-dd");
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        // Populate category dropdown
        TransactionModel model = controller.getModel();
        model.getCategories().forEach(categoryCombo::addItem);
        categoryCombo.setEditable(true);

        dialog.add(new JLabel("Amount*:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Description*:"));
        dialog.add(descriptionField);
        dialog.add(new JLabel("Category*:"));
        dialog.add(categoryCombo);
        dialog.add(new JLabel("Date (yyyy-mm-dd)*:"));
        dialog.add(dateField);
        dialog.add(cancelButton);
        dialog.add(addButton);

        // Apply theme to dialog
        SettingsManager settings = SettingsManager.getInstance();
        dialog.getContentPane().setBackground(settings.getBackgroundColor());
        for (Component c : dialog.getContentPane().getComponents()) {
            c.setBackground(settings.getBackgroundColor());
            c.setForeground(settings.getForegroundColor());
            if (c instanceof JComboBox) {
                ((JComboBox<?>) c).setBackground(settings.getBackgroundColor());
                ((JComboBox<?>) c).setForeground(settings.getForegroundColor());
            }
            if (c instanceof JTextField) {
                ((JTextField) c).setCaretColor(settings.getForegroundColor());
            }
        }

        // Add button action
        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText().trim();
                Object selectedCategory = categoryCombo.getSelectedItem();
                Category category;
                if (selectedCategory instanceof Category) {
                    category = (Category) selectedCategory;
                } else {
                    category = new Category(selectedCategory.toString());
                    model.addCategory(category);
                }
                String dateText = dateField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Description is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!DATE_PATTERN.matcher(dateText).matches()) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate date = LocalDate.parse(dateText);
                Transaction transaction = new Transaction(amount, date, category, description);
                notifyController("add", transaction);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Shows a dialog for editing an existing transaction.
     * 
     * @param row the index of the transaction to edit
     * @requires 0 <= row < transactionTable.getRowCount()
     */
    private void showEditTransactionDialog(int row) {
        JDialog dialog = new JDialog(this, "Edit Exact Transaction", true);
        dialog.setLayout(new GridLayout(5, 2));
        dialog.setSize(400, 300);

        // Fields for transaction details, pre-filled with current values
        String amountStr = String.valueOf(tableModel.getValueAt(row, 0)).replace(SettingsManager.getInstance().getCurrencySymbol(), "");
        JTextField amountField = new JTextField(amountStr);
        JTextField descriptionField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)));
        JComboBox<Category> categoryCombo = new JComboBox<>();
        JTextField dateField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Populate category dropdown and select current category
        TransactionModel model = controller.getModel();
        model.getCategories().forEach(categoryCombo::addItem);
        categoryCombo.setEditable(true);
        String currentCategoryName = String.valueOf(tableModel.getValueAt(row, 2));
        Category selectedCategory = model.getCategories().stream()
            .filter(cat -> cat.getName().equals(currentCategoryName))
            .findFirst()
            .orElse(new Category(currentCategoryName));
        categoryCombo.setSelectedItem(selectedCategory);

        dialog.add(new JLabel("Amount*:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Description*:"));
        dialog.add(descriptionField);
        dialog.add(new JLabel("Category*:"));
        dialog.add(categoryCombo);
        dialog.add(new JLabel("Date (yyyy-mm-dd)*:"));
        dialog.add(dateField);
        dialog.add(cancelButton);
        dialog.add(saveButton);

        // Apply theme to dialog
        SettingsManager settings = SettingsManager.getInstance();
        dialog.getContentPane().setBackground(settings.getBackgroundColor());
        for (Component c : dialog.getContentPane().getComponents()) {
            c.setBackground(settings.getBackgroundColor());
            c.setForeground(settings.getForegroundColor());
            if (c instanceof JComboBox) {
                ((JComboBox<?>) c).setBackground(settings.getBackgroundColor());
                ((JComboBox<?>) c).setForeground(settings.getForegroundColor());
            }
            if (c instanceof JTextField) {
                ((JTextField) c).setCaretColor(settings.getForegroundColor());
            }
        }

        // Save button action
        saveButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().replace(settings.getCurrencySymbol(), ""));
                String description = descriptionField.getText().trim();
                Object selectedCategoryObj = categoryCombo.getSelectedItem();
                Category category;
                if (selectedCategoryObj instanceof Category) {
                    category = (Category) selectedCategoryObj;
                } else {
                    category = new Category(selectedCategoryObj.toString());
                    model.addCategory(category);
                }
                String dateText = dateField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Description is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!DATE_PATTERN.matcher(dateText).matches()) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate date = LocalDate.parse(dateText);
                Transaction updatedTransaction = new Transaction(amount, date, category, description);
                controller.getModel().editTransaction(row, updatedTransaction);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Shows a dialog for adding a new category.
     */
    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog(this, "Add New Category", true);
        dialog.setLayout(new GridLayout(2, 2));
        dialog.setSize(300, 150);

        // Fields for category details
        JTextField categoryField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(new JLabel("Category Name*:"));
        dialog.add(categoryField);
        dialog.add(cancelButton);
        dialog.add(addButton);

        // Apply theme to dialog
        SettingsManager settings = SettingsManager.getInstance();
        dialog.getContentPane().setBackground(settings.getBackgroundColor());
        for (Component c : dialog.getContentPane().getComponents()) {
            c.setBackground(settings.getBackgroundColor());
            c.setForeground(settings.getForegroundColor());
            if (c instanceof JTextField) {
                ((JTextField) c).setCaretColor(settings.getForegroundColor());
            }
        }

        // Add button action
        addButton.addActionListener(e -> {
            try {
                String categoryName = categoryField.getText().trim();
                if (categoryName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Category name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Category category = new Category(categoryName);
                TransactionModel model = controller.getModel();
                if (model.getCategories().stream().noneMatch(cat -> cat.getName().equals(categoryName))) {
                    model.addCategory(category);
                    JOptionPane.showMessageDialog(dialog, "Category '" + categoryName + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Category '" + categoryName + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Shows a dialog for editing an existing category.
     */
    private void showEditCategoryDialog() {
        JDialog dialog = new JDialog(this, "Edit Exact Category", true);
        dialog.setLayout(new GridLayout(3, 2));
        dialog.setSize(300, 200);

        // Fields for category editing
        JComboBox<Category> categoryCombo = new JComboBox<>();
        JTextField newCategoryField = new JTextField();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Populate category dropdown
        TransactionModel model = controller.getModel();
        model.getCategories().forEach(categoryCombo::addItem);

        dialog.add(new JLabel("Select Category*:"));
        dialog.add(categoryCombo);
        dialog.add(new JLabel("New Category Name*:"));
        dialog.add(newCategoryField);
        dialog.add(cancelButton);
        dialog.add(saveButton);

        // Apply theme to dialog
        SettingsManager settings = SettingsManager.getInstance();
        dialog.getContentPane().setBackground(settings.getBackgroundColor());
        for (Component c : dialog.getContentPane().getComponents()) {
            c.setBackground(settings.getBackgroundColor());
            c.setForeground(settings.getForegroundColor());
            if (c instanceof JComboBox) {
                ((JComboBox<?>) c).setBackground(settings.getBackgroundColor());
                ((JComboBox<?>) c).setForeground(settings.getForegroundColor());
            }
            if (c instanceof JTextField) {
                ((JTextField) c).setCaretColor(settings.getForegroundColor());
            }
        }

        // Save button action
        saveButton.addActionListener(e -> {
            try {
                Category oldCategory = (Category) categoryCombo.getSelectedItem();
                String newCategoryName = newCategoryField.getText().trim();
                if (oldCategory == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a category to edit.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newCategoryName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "New category name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Category newCategory = new Category(newCategoryName);
                if (model.getCategories().stream().anyMatch(cat -> cat.getName().equals(newCategoryName))) {
                    JOptionPane.showMessageDialog(dialog, "Category '" + newCategoryName + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (model.editCategory(oldCategory, newCategory)) {
                    JOptionPane.showMessageDialog(dialog, "Category updated to '" + newCategoryName + "' successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update category.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Updates the GUI theme based on SettingsManager.
     */
    private void updateTheme() {
        SettingsManager settings = SettingsManager.getInstance();
        Color bg = settings.getBackgroundColor();
        Color fg = settings.getForegroundColor();

        getContentPane().setBackground(bg);
        setBackground(bg);

        if (scrollPane != null) {
            scrollPane.setBackground(bg);
            scrollPane.getViewport().setBackground(bg);
        }
        if (transactionTable != null) {
            transactionTable.setBackground(bg);
            transactionTable.setForeground(fg);
            transactionTable.setGridColor(fg);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setBackground(bg);
            renderer.setForeground(fg);
            for (int i = 0; i < transactionTable.getColumnCount(); i++) {
                transactionTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }

        if (controlPanel != null) {
            controlPanel.setBackground(bg);
            controlPanel.setForeground(fg);
            for (Component panel : controlPanel.getComponents()) {
                panel.setBackground(bg);
                panel.setForeground(fg);
                if (panel instanceof JPanel) {
                    for (Component c : ((JPanel) panel).getComponents()) {
                        c.setBackground(bg);
                        c.setForeground(fg);
                        if (c instanceof JComboBox) {
                            ((JComboBox<?>) c).setBackground(bg);
                            ((JComboBox<?>) c).setForeground(fg);
                        }
                        if (c instanceof JTextField) {
                            ((JTextField) c).setCaretColor(fg);
                        }
                    }
                }
            }
        }

        repaint();
    }

    /**
     * Updates the view when the model changes.
     * 
     * @param o the observable object
     * @param arg an argument passed to the notifyObservers method
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TransactionModel model) {
            tableModel.setRowCount(0);
            List<Transaction> transactions = model.getTransactions();
            SettingsManager settings = SettingsManager.getInstance();
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                    settings.getCurrencySymbol() + String.format("%.2f", t.getAmount()),
                    t.getDate().toString(),
                    t.getCategory().getName(),
                    t.getDescription()
                });
            }

            categoryFilter.removeAllItems();
            model.getCategories().forEach(categoryFilter::addItem);
            
            currencyField.setText(settings.getCurrencySymbol());
            themeCombo.setSelectedItem(settings.getTheme());
            updateTheme();
        }
    }

    /**
     * Gets a transaction from the table (deprecated).
     * 
     * @param row the row index
     * @return the transaction
     * @deprecated No longer used with dialog-based editing
     */
    @Deprecated
    public Transaction getEditedTransaction(int row) {
        String amountStr = (String) tableModel.getValueAt(row, 0);
        String dateStr = (String) tableModel.getValueAt(row, 1);
        String categoryName = (String) tableModel.getValueAt(row, 2);
        String description = (String) tableModel.getValueAt(row, 3);
        
        double amount = Double.parseDouble(amountStr.replace(SettingsManager.getInstance().getCurrencySymbol(), ""));
        LocalDate date = LocalDate.parse(dateStr);
        Category category = new Category(categoryName);
        return new Transaction(amount, date, category, description);
    }
}
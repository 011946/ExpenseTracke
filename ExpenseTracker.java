package expensetracker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Main class to launch the Personal Expense Tracker application.
 */
public class ExpenseTracker {
    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
            TransactionModel model = new TransactionModel();
            TransactionView view = new TransactionView();
            new TransactionController(model, view);
            view.setVisible(true);
        });
    }
}
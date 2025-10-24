package expensetracker;

import java.time.LocalDate;

/**
 * Controller class handling user interactions and updating model/view.
 */
public class TransactionController {
    private TransactionModel model;
    private TransactionView view;

    /**
     * Constructs a controller with the given model and view.
     * 
     * @param model the transaction model
     * @param view the transaction view
     * @requires model != null && view != null
     * @ensures this.model == model && this.view == view && view has this as controller
     */
    public TransactionController(TransactionModel model, TransactionView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException("Model and view cannot be null");
        }
        this.model = model;
        this.view = view;
        this.view.setController(this);
        model.addObserver(view);
    }

    /**
     * Returns the model.
     * 
     * @return the model
     * @ensures result != null
     */
    public TransactionModel getModel() {
        return model;
    }

    /**
     * Handles user actions by updating the model.
     * 
     * @param action the action type
     * @param data the associated data
     * @requires action != null
     */
    public void handleAction(String action, Object data) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        switch (action) {
            case "add":
                model.addTransaction((Transaction) data);
                break;
            case "edit":
                // No longer used; editing is handled directly in the dialog
                break;
            case "delete":
                model.deleteTransaction((Integer) data);
                break;
            case "sort":
                model.sortTransactions((String) data);
                break;
            case "filter":
                model.setCategoryFilter((Category) data);
                break;
            case "currency":
                model.setCurrencySymbol((String) data);
                break;
            case "theme":
                model.setTheme((String) data);
                break;
        }
    }
}
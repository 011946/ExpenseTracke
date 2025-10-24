package expensetracker;

import java.time.LocalDate;
import java.util.*;

/**
 * Model class managing transactions and categories.
 * Implements Observable for the Observer pattern.
 * 
 * Abstraction Function: Represents a collection of financial transactions and their categories, 
 * with an optional category filter.
 * AF(m) = a model with transactions m.transactions, categories m.categories, and current filter m.currentFilter
 * 
 * Representation Invariant:
 * - transactions != null
 * - categories != null
 * - for all t in transactions: t != null && categories.contains(t.getCategory())
 * - currentFilter == null || categories.contains(currentFilter)
 */
public class TransactionModel extends Observable {
    private List<Transaction> transactions;
    private List<Category> categories;
    private Category currentFilter;

    /**
     * Constructs a new TransactionModel with an empty transaction list and a default "General" category.
     * 
     * @ensures transactions is empty, categories contains "General", currentFilter is null
     */
    public TransactionModel() {
        transactions = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add(new Category("General"));
        currentFilter = null;
        checkRep();
    }

    /**
     * Adds a transaction and notifies observers.
     * 
     * @param transaction the transaction to add
     * @requires transaction != null && categories.contains(transaction.getCategory())
     * @ensures transactions contains transaction && observers are notified
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null || !categories.contains(transaction.getCategory())) {
            throw new IllegalArgumentException("Transaction cannot be null and its category must exist");
        }
        transactions.add(transaction);
        checkRep();
        setChanged();
        notifyObservers();
    }

    /**
     * Edits a transaction and notifies observers.
     * 
     * @param index the index of the transaction to edit
     * @param transaction the updated transaction
     * @requires 0 <= index < transactions.size() && transaction != null && categories.contains(transaction.getCategory())
     * @ensures transactions[index] == transaction && observers are notified
     */
    public void editTransaction(int index, Transaction transaction) {
        if (index < 0 || index >= transactions.size() || transaction == null || !categories.contains(transaction.getCategory())) {
            throw new IllegalArgumentException("Invalid index or transaction");
        }
        transactions.set(index, transaction);
        checkRep();
        setChanged();
        notifyObservers();
    }

    /**
     * Deletes a transaction and notifies observers.
     * 
     * @param index the index of the transaction to delete
     * @requires 0 <= index < transactions.size()
     * @ensures transactions does not contain the transaction at index && observers are notified
     */
    public void deleteTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            throw new IllegalArgumentException("Invalid index");
        }
        transactions.remove(index);
        checkRep();
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a category.
     * 
     * @param category the category to add
     * @requires category != null
     * @ensures categories contains category if it was not already present && observers are notified
     */
    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (!categories.contains(category)) {
            categories.add(category);
            checkRep();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Edits a category by replacing the old category with a new one in transactions and categories.
     * 
     * @param oldCategory the category to replace
     * @param newCategory the new category
     * @requires oldCategory != null && newCategory != null
     * @ensures categories contains newCategory && !categories.contains(oldCategory) &&
     *          all transactions with oldCategory now use newCategory && observers are notified
     * @return true if the edit was successful, false otherwise
     */
    public boolean editCategory(Category oldCategory, Category newCategory) {
        if (oldCategory == null || newCategory == null || !categories.contains(oldCategory) || categories.contains(newCategory)) {
            return false;
        }
        transactions.forEach(t -> {
            if (t.getCategory().equals(oldCategory)) {
                t.setCategory(newCategory);
            }
        });
        categories.remove(oldCategory);
        categories.add(newCategory);
        if (currentFilter != null && currentFilter.equals(oldCategory)) {
            currentFilter = newCategory;
        }
        checkRep();
        setChanged();
        notifyObservers();
        return true;
    }

    /**
     * Deletes a category if no transactions use it.
     * 
     * @param category the category to delete
     * @requires category != null
     * @ensures !categories.contains(category) if no transactions use it && observers are notified
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteCategory(Category category) {
        if (category == null || transactions.stream().anyMatch(t -> t.getCategory().equals(category))) {
            return false;
        }
        categories.remove(category);
        if (currentFilter != null && currentFilter.equals(category)) {
            currentFilter = null;
        }
        checkRep();
        setChanged();
        notifyObservers();
        return true;
    }

    /**
     * Sets the currency symbol and notifies observers.
     * 
     * @param symbol the new currency symbol
     * @requires symbol != null && !symbol.isEmpty()
     * @ensures SettingsManager.getInstance().getCurrencySymbol() == symbol && observers are notified
     */
    public void setCurrencySymbol(String symbol) {
        SettingsManager.getInstance().setCurrencySymbol(symbol);
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the theme and notifies observers.
     * 
     * @param theme the new theme
     * @requires theme != null && theme is "Light" or "Dark"
     * @ensures SettingsManager.getInstance().getTheme() == theme && observers are notified
     */
    public void setTheme(String theme) {
        SettingsManager.getInstance().setTheme(theme);
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the category filter and notifies observers.
     * 
     * @param category the category to filter by, or null for no filter
     * @requires category == null || categories.contains(category)
     * @ensures currentFilter == category && observers are notified
     */
    public void setCategoryFilter(Category category) {
        if (category != null && !categories.contains(category)) {
            throw new IllegalArgumentException("Category must exist in categories");
        }
        this.currentFilter = category;
        checkRep();
        setChanged();
        notifyObservers();
    }

    /**
     * Gets a copy of the transactions list, applying the current filter.
     * 
     * @return a list of transactions
     * @ensures result != null && if currentFilter != null, all transactions in result have category == currentFilter
     */
    public List<Transaction> getTransactions() { 
        if (currentFilter == null) {
            return new ArrayList<>(transactions);
        }
        return transactions.stream()
            .filter(t -> t.getCategory().equals(currentFilter))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Gets a copy of the categories list.
     * 
     * @return a list of categories
     * @ensures result != null
     */
    public List<Category> getCategories() { 
        return new ArrayList<>(categories); 
    }

    /**
     * Sorts transactions by date or amount.
     * 
     * @param by the field to sort by ("date" or "amount")
     * @requires by != null && by is "date" or "amount"
     * @ensures transactions are sorted by the specified field && observers are notified
     */
    public void sortTransactions(String by) {
        if (by == null || (!by.equals("date") && !by.equals("amount"))) {
            throw new IllegalArgumentException("Sort field must be 'date' or 'amount'");
        }
        if (by.equals("date")) {
            transactions.sort(Comparator.comparing(Transaction::getDate));
        } else {
            transactions.sort(Comparator.comparingDouble(Transaction::getAmount));
        }
        checkRep();
        setChanged();
        notifyObservers();
    }

    /**
     * Filters transactions by category.
     * 
     * @param category the category to filter by
     * @requires category != null
     * @return a list of transactions with the specified category
     * @ensures result != null && all transactions in result have the specified category
     */
    public List<Transaction> filterByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return transactions.stream()
            .filter(t -> t.getCategory().equals(category))
            .toList();
    }

    /**
     * Checks the representation invariant.
     */
    private void checkRep() {
        assert transactions != null : "Transactions list cannot be null";
        assert categories != null : "Categories list cannot be null";
        for (Transaction t : transactions) {
            assert t != null : "Transaction cannot be null";
            assert categories.contains(t.getCategory()) : "Transaction category must exist in categories";
        }
        assert currentFilter == null || categories.contains(currentFilter) : "Current filter must be null or in categories";
    }
}
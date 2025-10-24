package expensetracker;

import java.time.LocalDate;

/**
 * Mutable ADT representing a financial transaction.
 * 
 * Abstraction Function: Represents a financial transaction with an amount, date, category, and description.
 * AF(t) = a transaction with amount t.amount, date t.date, category t.category, and description t.description
 * 
 * Representation Invariant:
 * - amount != Double.NaN
 * - date != null
 * - category != null
 * - description != null
 */
public class Transaction {
    private double amount;
    private LocalDate date;
    private Category category;
    private String description;

    /**
     * Constructs a new Transaction.
     * 
     * @param amount the transaction amount
     * @param date the transaction date
     * @param category the transaction category
     * @param description the transaction description
     * @requires amount != Double.NaN, date != null, category != null, description != null
     * @throws IllegalArgumentException if any requirement is violated
     */
    public Transaction(double amount, LocalDate date, Category category, String description) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
        checkRep();
    }

    /**
     * Checks the representation invariant.
     */
    private void checkRep() {
        assert amount != Double.NaN : "Amount cannot be NaN";
        assert date != null : "Date cannot be null";
        assert category != null : "Category cannot be null";
        assert description != null : "Description cannot be null";
    }

    /**
     * Returns the transaction amount.
     * 
     * @return the amount
     * @ensures result != Double.NaN
     */
    public double getAmount() { 
        return amount; 
    }

    /**
     * Sets the transaction amount.
     * 
     * @param amount the new amount
     * @requires amount != Double.NaN
     * @ensures this.amount == amount
     */
    public void setAmount(double amount) { 
        this.amount = amount; 
        checkRep(); 
    }

    /**
     * Returns the transaction date.
     * 
     * @return the date
     * @ensures result != null
     */
    public LocalDate getDate() { 
        return date; 
    }

    /**
     * Sets the transaction date.
     * 
     * @param date the new date
     * @requires date != null
     * @ensures this.date == date
     */
    public void setDate(LocalDate date) { 
        this.date = date; 
        checkRep(); 
    }

    /**
     * Returns the transaction category.
     * 
     * @return the category
     * @ensures result != null
     */
    public Category getCategory() { 
        return category; 
    }

    /**
     * Sets the transaction category.
     * 
     * @param category the new category
     * @requires category != null
     * @ensures this.category == category
     */
    public void setCategory(Category category) { 
        this.category = category; 
        checkRep(); 
    }

    /**
     * Returns the transaction description.
     * 
     * @return the description
     * @ensures result != null
     */
    public String getDescription() { 
        return description; 
    }

    /**
     * Sets the transaction description.
     * 
     * @param description the new description
     * @requires description != null
     * @ensures this.description == description
     */
    public void setDescription(String description) { 
        this.description = description; 
        checkRep(); 
    }

    /**
     * Compares this transaction to another object for equality.
     * 
     * @param o the object to compare with
     * @return true if o is a Transaction with the same amount, date, category, and description
     * @ensures result == true if and only if o instanceof Transaction &&
     *          this.amount == ((Transaction)o).amount &&
     *          this.date.equals(((Transaction)o).date) &&
     *          this.category.equals(((Transaction)o).category) &&
     *          this.description.equals(((Transaction)o).description)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
               date.equals(that.date) &&
               category.equals(that.category) &&
               description.equals(that.description);
    }

    /**
     * Returns a hash code for this transaction.
     * 
     * @return a hash code based on amount, date, category, and description
     */
    @Override
    public int hashCode() {
        int result = 17;
        long temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + date.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    /**
     * Returns a string representation of this transaction.
     * 
     * @return a string in the format "description: $amount on date (category)"
     */
    @Override
    public String toString() {
        return String.format("%s: $%.2f on %s (%s)", description, amount, date, category);
    }
}
package expensetracker;

/**
 * Immutable ADT representing a category for transactions.
 * 
 * Abstraction Function: Represents a transaction category identified by a unique name.
 * AF(c) = a category with name c.name
 * 
 * Representation Invariant: 
 * - name != null
 * - name.trim() is not empty
 */
public class Category {
    private final String name;

    /**
     * Constructs a new Category with the specified name.
     * 
     * @param name the name of the category
     * @requires name != null && !name.trim().isEmpty()
     * @throws IllegalArgumentException if name is null or empty
     */
    public Category(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.name = name;
        checkRep();
    }

    /**
     * Returns the category name.
     * 
     * @return the name of the category
     * @ensures result != null && !result.trim().isEmpty()
     */
    public String getName() {
        return name;
    }

    /**
     * Checks the representation invariant.
     */
    private void checkRep() {
        assert name != null : "Name cannot be null";
        assert !name.trim().isEmpty() : "Name cannot be empty";
    }

    /**
     * Compares this category to another object for equality.
     * 
     * @param o the object to compare with
     * @return true if o is a Category with the same name
     * @ensures result == true if and only if o instanceof Category && this.name.equals(((Category)o).name)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    /**
     * Returns a hash code for this category.
     * 
     * @return a hash code based on the name
     * @ensures result == name.hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns a string representation of this category.
     * 
     * @return the category name
     */
    @Override
    public String toString() {
        return name;
    }
}
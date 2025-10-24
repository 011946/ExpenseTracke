# Personal Expense Tracker

## Overview

The Personal Expense Tracker is a Java-based desktop application designed to help users manage financial transactions efficiently. It allows users to add, edit, delete, and categorize expenses, with customizable currency and theme settings. Built with high-quality software practices, it adheres to the Model-View-Controller (MVC) pattern, Observer and Singleton design patterns, and includes robust Abstract Data Types (ADTs), formal Javadoc specifications, immutability for stability, equality for data consistency, and regular expressions for input validation, ensuring code is safe from bugs, easy to understand, and ready for change.

## Features

- **Transaction Management**: Add, edit, or delete transactions with amount, date, category, and description.
- **Category Management**: Create, edit, or delete categories (e.g., "Food", "Travel").
- **Filtering and Sorting**: Filter transactions by category or sort by date/amount.
- **Settings**: Customize currency symbol and theme (Light/Dark).
- **Robust Design**: Uses MVC, Observer for UI updates, Singleton for settings, and regular expressions for date validation (yyyy-mm-dd).

## Technical Requirements

- **MVC Pattern**: Separates data (`TransactionModel`), UI (`TransactionView`), and input handling (`TransactionController`).
- **Design Patterns**: Observer for automatic UI updates, Singleton for consistent settings.
- **ADTs**: `Transaction` (mutable) and `Category` (immutable) for core data management.
- **Specifications**: Full Javadoc with preconditions/postconditions; `checkRep()` enforces invariants.
- **Equality**: Implemented for `Category` to prevent duplicates.
- **Regular Expressions**: Validates dates in dialogs, ensuring robust input.

## Setup Instructions

1. **Prerequisites**: Java 8+ and NetBeans IDE.
2. **Clone Repository**:
   ```bash
   git clone https://github.com/username/ExpenseTracker.git

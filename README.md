# Task 3: ATM Interface

This is a console-based ATM (Automated Teller Machine) simulation, developed as the final project for the Oasis Infobyte Java Development internship. This project demonstrates a comprehensive understanding of Java's core functionalities, Object-Oriented Programming (OOP) principles, and robust database management.

### **Features**

* **Secure Login:** Prompts the user for a User ID and PIN to authenticate against a backend database.
* **Core Banking Operations:** After successful login, the user can perform the following actions:
    * **Transaction History:** View a complete history of all deposits, withdrawals, and transfers.
    * **Withdrawal:** Deduct funds from the account, with validation for insufficient balance.
    * **Deposit:** Add funds to the account.
    * **Transfer:** Transfer funds from the current account to another existing account, with full transaction integrity.
* **Data Persistence:** All account balances and transaction records are stored permanently in a MySQL database.
* **Console-Based UI:** The application interacts with the user through a simple, clear command-line interface.

### **Technology Stack**

* **Language:** Java
* **User Interface:** Console-based using `java.util.Scanner` for input and `System.out.println` for output.
* **Backend:** MySQL Database.
* **Connectivity:** JDBC (Java Database Connectivity) Driver.
* **Object-Oriented Design:** The project is structured using multiple classes (`ATM`, `Account`, `ATM_Operations`, `DatabaseManager`) to ensure a clear separation of concerns and maintainable code.

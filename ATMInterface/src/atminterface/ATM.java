import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        ATM_Operations atmOps = new ATM_Operations();
        Connection conn = null;
        Account loggedInAccount = null;

        System.out.println("------------------------------------");
        System.out.println(" Welcome to the ATM Interface ");
        System.out.println("------------------------------------");

        // Step 1: Login
        try {
            conn = DatabaseManager.getConnection();
            boolean loggedIn = false;
            
            while (!loggedIn) {
                System.out.print("Enter User ID: ");
                int userId = scanner.nextInt();
                System.out.print("Enter PIN: ");
                String pin = scanner.next();

                String sql = "SELECT * FROM accounts WHERE user_id = ? AND user_pin = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, pin);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            // Login successful, create an Account object
                            loggedInAccount = new Account(
                                rs.getInt("user_id"),
                                rs.getString("user_pin"),
                                rs.getDouble("balance")
                            );
                            System.out.println("\nLogin successful! Welcome, User " + loggedInAccount.getUserId());
                            loggedIn = true;
                        } else {
                            System.out.println("\nInvalid User ID or PIN. Please try again.");
                        }
                    }
                }
            }

            // Step 2: Display menu and handle operations
            while (true) {
                System.out.println("\n------------------------------------");
                System.out.println("1. Transaction History");
                System.out.println("2. Withdraw");
                System.out.println("3. Deposit");
                System.out.println("4. Transfer");
                System.out.println("5. Quit");
                System.out.println("------------------------------------");
                
                System.out.print("Enter your choice: ");
                int choice;
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Clear the invalid input from the scanner
                    continue;
                }
                
                switch (choice) {
                    case 1:
                        atmOps.showTransactionHistory(conn, loggedInAccount.getUserId());
                        break;
                    case 2:
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        atmOps.withdraw(conn, loggedInAccount, withdrawAmount);
                        break;
                    case 3:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        atmOps.deposit(conn, loggedInAccount, depositAmount);
                        break;
                    case 4:
                        System.out.print("Enter receiver User ID: ");
                        int receiverId = scanner.nextInt();
                        System.out.print("Enter amount to transfer: ");
                        double transferAmount = scanner.nextDouble();
                        atmOps.transfer(conn, loggedInAccount, receiverId, transferAmount);
                        break;
                    case 5:
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        return; // Exit the main method
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            scanner.close();
        }
    }
}
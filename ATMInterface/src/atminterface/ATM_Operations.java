import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class ATM_Operations {

    // 1. Deposit Method
    public boolean deposit(Connection conn, Account account, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount.");
            return false;
        }

        try {
            // Update account balance in the database
            String sqlUpdate = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, account.getUserId());
                pstmt.executeUpdate();
            }

            // Record the transaction
            String sqlInsert = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, account.getUserId());
                pstmt.setString(2, "Deposit");
                pstmt.setDouble(3, amount);
                pstmt.executeUpdate();
            }

            // Update local account object
            account.setBalance(account.getBalance() + amount);
            System.out.printf("Deposit successful. New balance: $%.2f\n", account.getBalance());
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error performing deposit: " + e.getMessage());
            return false;
        }
    }

    // 2. Withdraw Method
    public boolean withdraw(Connection conn, Account account, double amount) {
        if (amount <= 0 || amount > account.getBalance()) {
            System.out.println("Invalid withdrawal amount or insufficient balance.");
            return false;
        }

        try {
            // Update account balance in the database
            String sqlUpdate = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, account.getUserId());
                pstmt.executeUpdate();
            }

            // Record the transaction
            String sqlInsert = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, account.getUserId());
                pstmt.setString(2, "Withdrawal");
                pstmt.setDouble(3, amount);
                pstmt.executeUpdate();
            }
            
            // Update local account object
            account.setBalance(account.getBalance() - amount);
            System.out.printf("Withdrawal successful. New balance: $%.2f\n", account.getBalance());
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error performing withdrawal: " + e.getMessage());
            return false;
        }
    }
    
    // 3. Transfer Method
    public boolean transfer(Connection conn, Account sender, int receiverId, double amount) {
        if (amount <= 0 || amount > sender.getBalance()) {
            System.out.println("Invalid transfer amount or insufficient balance.");
            return false;
        }

        try {
            // Check if the receiver account exists
            String checkReceiverSql = "SELECT user_id FROM accounts WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkReceiverSql)) {
                pstmt.setInt(1, receiverId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Receiver account ID not found.");
                        return false;
                    }
                }
            }

            // Start a transaction to ensure both updates succeed or fail together
            conn.setAutoCommit(false); 

            // Withdraw from sender
            String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(withdrawSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, sender.getUserId());
                pstmt.executeUpdate();
            }

            // Deposit to receiver
            String depositSql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(depositSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, receiverId);
                pstmt.executeUpdate();
            }

            // Record sender transaction
            String senderTransSql = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(senderTransSql)) {
                pstmt.setInt(1, sender.getUserId());
                pstmt.setString(2, "Transfer to " + receiverId);
                pstmt.setDouble(3, -amount); // Negative amount for sender's history
                pstmt.executeUpdate();
            }
            
            // Record receiver transaction
            String receiverTransSql = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(receiverTransSql)) {
                pstmt.setInt(1, receiverId);
                pstmt.setString(2, "Transfer from " + sender.getUserId());
                pstmt.setDouble(3, amount);
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Commit the transaction
            sender.setBalance(sender.getBalance() - amount); // Update local object
            System.out.printf("Transfer successful. New balance: $%.2f\n", sender.getBalance());
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback if an error occurs
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error performing transfer: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 4. Show Transaction History Method
    public void showTransactionHistory(Connection conn, int userId) {
        System.out.println("------------------------------------");
        System.out.println("Transaction History for User " + userId + ":");
        System.out.println("------------------------------------");

        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                    System.out.println("No transactions found.");
                } else {
                    while (rs.next()) {
                        System.out.printf("ID: %d, Type: %s, Amount: $%.2f, Time: %s\n",
                            rs.getInt("transaction_id"),
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("timestamp")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transaction history: " + e.getMessage());
        }
        System.out.println("------------------------------------");
    }
}
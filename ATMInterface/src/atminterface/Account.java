public class Account {
    
    private int userId;
    private String userPin;
    private double balance;

    public Account(int userId, String userPin, double balance) {
        this.userId = userId;
        this.userPin = userPin;
        this.balance = balance;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public String getUserPin() {
        return userPin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    // A method to display account details
    public void displayAccount() {
        System.out.println("------------------------------------");
        System.out.println("Account Details:");
        System.out.println("User ID: " + userId);
        System.out.println("Current Balance: $" + String.format("%.2f", balance));
        System.out.println("------------------------------------");
    }
}
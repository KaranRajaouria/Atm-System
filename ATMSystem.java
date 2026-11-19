package MCA; 
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
 
public class ATMSystem extends JFrame {
  
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=ATMSystem;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "1234";
 
    Connection con;
  
    String accountNo = "", userName = "";
    double balance = 0;
 
    String adminUsername = "";
 
    CardLayout card = new CardLayout();
    JPanel main = new JPanel(card);
 
    JTextField  accField;
    JPasswordField pinField;
    JTextField  amountField;
    JTextField  transferAcc, transferAmt;
 
    JTextField     adminUserField;
    JPasswordField adminPassField;
 
    JLabel welcome, balLbl;
    JLabel adminWelcome;
 
    Color bg       = new Color(20,  20,  30);
    Color cardBg   = new Color(40,  40,  55);
    Color btnColor = new Color(0,  120, 255);
    Color adminBtn = new Color(180,  40,  40);   
    
    public ATMSystem() {
        setTitle("ATM Machine System");
        setSize(500, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
 
        connect();
        
        main.add(loginUI(),      "login");
        main.add(menuUI(),       "menu");
        main.add(withdrawUI(),   "withdraw");
        main.add(depositUI(),    "deposit");
        main.add(transferUI(),   "transfer");
 
        main.add(adminLoginUI(), "adminLogin");
        main.add(adminMenuUI(),  "adminMenu");
 
        add(main);
        card.show(main, "login");
    }
 
    void connect() {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) { msg(e); }
    }
    String hash(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(p.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { return p; }
    }
 
    JPanel loginUI() {
        JPanel p   = base();
        JPanel box = cardBox();
 
        accField = input();
        pinField = new JPasswordField();
        style(pinField);
 
        JButton login     = btn("LOGIN");
        JButton reg       = btn("REGISTER");
        JButton adminLink = btnColored("ADMIN LOGIN", adminBtn);
 
        box.add(title("ATM LOGIN"));
        box.add(label("Account Number"));
        box.add(accField);
        box.add(label("PIN"));
        box.add(pinField);
        box.add(login);
        box.add(reg);
        box.add(adminLink);
 
        p.add(box);
 
        login.addActionListener(e -> login());
        reg.addActionListener(e -> register());
        adminLink.addActionListener(e -> {
            adminUserField.setText("");
            adminPassField.setText("");
            card.show(main, "adminLogin");
        });
 
        return p;
    }
 
    JPanel menuUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
 
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(bg);
 
        welcome = center("");
        balLbl  = center("");
 
        top.add(welcome);
        top.add(balLbl);
 
        JPanel grid = new JPanel(new GridLayout(3, 2, 15, 15));
        grid.setBackground(bg);
        grid.setBorder(new EmptyBorder(20, 40, 20, 40));
 
        grid.add(menuBtn("Withdraw",       () -> card.show(main, "withdraw")));
        grid.add(menuBtn("Deposit",        () -> card.show(main, "deposit")));
        grid.add(menuBtn("Transfer",       () -> card.show(main, "transfer")));
        grid.add(menuBtn("Mini Statement", () -> mini()));
        grid.add(menuBtn("Change PIN",     () -> changePin()));
        grid.add(menuBtn("Logout",         () -> logout()));
 
        p.add(top,  BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
 
        return p;
    }
 
    JPanel withdrawUI() {
        JPanel p   = base();
        JPanel box = cardBox();
 
        amountField = input();
 
        JButton w    = btn("WITHDRAW");
        JButton back = btn("BACK");
 
        box.add(title("Withdraw"));
        box.add(label("Amount (₹)"));
        box.add(amountField);
        box.add(w);
        box.add(back);
 
        p.add(box);
 
        w.addActionListener(e -> withdraw());
        back.addActionListener(e -> card.show(main, "menu"));
 
        return p;
    }
 
    JPanel depositUI() {
        JPanel p   = base();
        JPanel box = cardBox();
 
        amountField = input();
 
        JButton d    = btn("DEPOSIT");
        JButton back = btn("BACK");
 
        box.add(title("Deposit"));
        box.add(label("Amount (₹)"));
        box.add(amountField);
        box.add(d);
        box.add(back);
 
        p.add(box);
 
        d.addActionListener(e -> deposit());
        back.addActionListener(e -> card.show(main, "menu"));
 
        return p;
    }
 
    JPanel transferUI() {
        JPanel p   = base();
        JPanel box = cardBox();
 
        transferAcc = input();
        transferAmt = input();
 
        JButton t    = btn("TRANSFER");
        JButton back = btn("BACK");
 
        box.add(title("Transfer Funds"));
        box.add(label("Receiver Account No"));
        box.add(transferAcc);
        box.add(label("Amount (₹)"));
        box.add(transferAmt);
        box.add(t);
        box.add(back);
 
        p.add(box);
 
        t.addActionListener(e -> transfer());
        back.addActionListener(e -> card.show(main, "menu"));
 
        return p;
    }
 
    JPanel adminLoginUI() {
        JPanel p   = base();
        JPanel box = cardBox();
 
        adminUserField = input();
        adminPassField = new JPasswordField();
        style(adminPassField);
 
        JButton login = btnColored("ADMIN LOGIN", adminBtn);
        JButton back  = btn("BACK TO USER LOGIN");
 
        box.add(title("ADMIN LOGIN"));
        box.add(label("Admin Username"));
        box.add(adminUserField);
        box.add(label("Admin Password"));
        box.add(adminPassField);
        box.add(login);
        box.add(back);
 
        p.add(box);
 
        login.addActionListener(e -> adminLogin());
        back.addActionListener(e -> card.show(main, "login"));
 
        return p;
    }
 
    JPanel adminMenuUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
 
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(new Color(100, 20, 20));   
 
        adminWelcome = center("ADMIN PANEL");
        adminWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
 
        JLabel subLbl = center("Privileged Access — Handle with Care");
        subLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subLbl.setForeground(new Color(255, 180, 180));
 
        top.add(adminWelcome);
        top.add(subLbl);
        top.setBorder(new EmptyBorder(10, 0, 10, 0));
 
        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setBackground(bg);
        grid.setBorder(new EmptyBorder(30, 40, 30, 40));
 
        grid.add(menuBtnColored("View All Users",       () -> adminViewUsers(), adminBtn));
        grid.add(menuBtnColored("View Transactions",    () -> adminViewTx(),    adminBtn));
        grid.add(menuBtnColored("Delete Account",       () -> adminDeleteAcc(), adminBtn));
        grid.add(menuBtnColored("Logout",               () -> adminLogout(),    new Color(80, 80, 80)));
 
        p.add(top,  BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
 
        return p;
    }

    void login() {
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE account_no=? AND pin=?");
            ps.setString(1, accField.getText());
            ps.setString(2, hash(new String(pinField.getPassword())));
 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accountNo = rs.getString("account_no");
                userName  = rs.getString("name");
                balance   = rs.getDouble("balance");
 
                welcome.setText("Welcome, " + userName);
                balLbl.setText("Balance: ₹" + String.format("%.2f", balance));
 
                card.show(main, "menu");
            } else {
                msg("Invalid account number or PIN.");
            }
        } catch (Exception e) { msg(e); }
    }
 
    void register() {
        try {
            String name = JOptionPane.showInputDialog(this, "Full Name");
            String acc  = JOptionPane.showInputDialog(this, "New Account Number");
            String pin  = JOptionPane.showInputDialog(this, "4-digit PIN");
 
            if (name == null || acc == null || pin == null) return;
 
            if (pin.length() != 4 || !pin.matches("\\d{4}")) {
                msg("PIN must be exactly 4 numeric digits."); return;
            }
 
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(account_no,name,pin,balance) VALUES(?,?,?,0)");
            ps.setString(1, acc);
            ps.setString(2, name);
            ps.setString(3, hash(pin));
            ps.executeUpdate();
 
            msg("Account created successfully!\nAccount No: " + acc);
        } catch (Exception e) { msg(e); }
    }
 
    void withdraw() {
        try {
            double amt = Double.parseDouble(amountField.getText());
 
            if (amt <= 0) { msg("Amount must be greater than zero."); return; }
            if (amt > balance) { msg("Insufficient balance."); return; }
 
            balance -= amt;
            update();
            log("Withdraw", amt);
 
            balLbl.setText("Balance: ₹" + String.format("%.2f", balance));
            msg("Withdrawn ₹" + String.format("%.2f", amt) + " successfully.");
            amountField.setText("");
 
        } catch (NumberFormatException e) { msg("Please enter a valid numeric amount."); }
        catch (Exception e) { msg(e); }
    }
 
    void deposit() {
        try {
            double amt = Double.parseDouble(amountField.getText());
 
            if (amt <= 0) { msg("Amount must be greater than zero."); return; }
 
            balance += amt;
            update();
            log("Deposit", amt);
 
            balLbl.setText("Balance: ₹" + String.format("%.2f", balance));
            msg("Deposited ₹" + String.format("%.2f", amt) + " successfully.");
            amountField.setText("");
 
        } catch (NumberFormatException e) { msg("Please enter a valid numeric amount."); }
        catch (Exception e) { msg(e); }
    }
 
    void transfer() {
        try {
            double amt     = Double.parseDouble(transferAmt.getText());
            String recvAcc = transferAcc.getText().trim();
 
            if (amt <= 0) { msg("Amount must be greater than zero."); return; }
            if (amt > balance) { msg("Insufficient balance."); return; }
            if (recvAcc.equals(accountNo)) { msg("Cannot transfer to your own account."); return; }
 
            con.setAutoCommit(false);
 
            PreparedStatement credit = con.prepareStatement(
                "UPDATE users SET balance=balance+? WHERE account_no=?");
            credit.setDouble(1, amt);
            credit.setString(2, recvAcc);
 
            if (credit.executeUpdate() == 0) {
                con.rollback();
                msg("Receiver account not found. Transfer cancelled.");
                return;
            }
 
            PreparedStatement debit = con.prepareStatement(
                "UPDATE users SET balance=balance-? WHERE account_no=?");
            debit.setDouble(1, amt);
            debit.setString(2, accountNo);
            debit.executeUpdate();
 
            log("Transfer", amt);
            con.commit();
 
            balance -= amt;
            balLbl.setText("Balance: ₹" + String.format("%.2f", balance));
            msg("Transfer of ₹" + String.format("%.2f", amt) + " to " + recvAcc + " successful.");
            transferAcc.setText("");
            transferAmt.setText("");
 
        } catch (NumberFormatException e) {
            msg("Please enter a valid numeric amount.");
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {}
            msg(e);
        } finally {
            try { con.setAutoCommit(true); } catch (Exception e) {}
        }
    }
 
    void mini() {
        try {
            DefaultTableModel m = new DefaultTableModel(
                new String[]{"Type", "Amount (₹)", "Date/Time"}, 0);
 
            PreparedStatement ps = con.prepareStatement(
                "SELECT TOP 10 * FROM transactions WHERE account_no=? ORDER BY tid DESC");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                m.addRow(new Object[]{
                    rs.getString("type"),
                    String.format("%.2f", rs.getDouble("amount")),
                    rs.getString("tdate")
                });
            }
 
            if (m.getRowCount() == 0) {
                msg("No transactions found."); return;
            }
 
            JTable t = new JTable(m);
            t.setEnabled(false);
            JOptionPane.showMessageDialog(
                this, new JScrollPane(t), "Mini Statement (Last 10)", JOptionPane.PLAIN_MESSAGE);
 
        } catch (Exception e) { msg(e); }
    }
 
    void changePin() {
        try {
            String cur = JOptionPane.showInputDialog(this, "Enter Current PIN");
            if (cur == null) return;
 
            PreparedStatement check = con.prepareStatement(
                "SELECT account_no FROM users WHERE account_no=? AND pin=?");
            check.setString(1, accountNo);
            check.setString(2, hash(cur));
            ResultSet rs = check.executeQuery();
 
            if (!rs.next()) { msg("Current PIN is incorrect."); return; }
 
            String np = JOptionPane.showInputDialog(this, "Enter New 4-digit PIN");
            if (np == null) return;
 
            if (np.length() != 4 || !np.matches("\\d{4}")) {
                msg("New PIN must be exactly 4 numeric digits."); return;
            }
 
            PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET pin=? WHERE account_no=?");
            ps.setString(1, hash(np));
            ps.setString(2, accountNo);
            ps.executeUpdate();
 
            msg("PIN updated successfully.");
 
        } catch (Exception e) { msg(e); }
    }
 
    void logout() {
        accountNo = "";
        userName  = "";
        balance   = 0;
        accField.setText("");
        pinField.setText("");
        card.show(main, "login");
    }
 
 
    void update() throws Exception {
        PreparedStatement ps = con.prepareStatement(
            "UPDATE users SET balance=? WHERE account_no=?");
        ps.setDouble(1, balance);
        ps.setString(2, accountNo);
        ps.executeUpdate();
    }
 
    void log(String type, double amt) throws Exception {
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO transactions(account_no,type,amount) VALUES(?,?,?)");
        ps.setString(1, accountNo);
        ps.setString(2, type);
        ps.setDouble(3, amt);
        ps.executeUpdate();
    }
    void adminLogin() {
        try {
            String uname = adminUserField.getText().trim();
            String pword = new String(adminPassField.getPassword());
 
            if (uname.isEmpty() || pword.isEmpty()) {
                msg("Please enter username and password."); return;
            }
 
            PreparedStatement ps = con.prepareStatement(
                "SELECT username FROM admin WHERE username=? AND password=?");
            ps.setString(1, uname);
            ps.setString(2, hash(pword));
 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                adminUsername = rs.getString("username");
                adminWelcome.setText("ADMIN PANEL  |  " + adminUsername);
                card.show(main, "adminMenu");
            } else {
                msg("Invalid admin credentials.");
            }
        } catch (Exception e) { msg(e); }
    }
 
    
    void adminViewUsers() {
        try {
            DefaultTableModel m = new DefaultTableModel(
                new String[]{"Account No", "Name", "Balance (₹)", "Created At"}, 0);
 
            PreparedStatement ps = con.prepareStatement(
                "SELECT account_no, name, balance, created_at FROM users ORDER BY created_at DESC");
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                m.addRow(new Object[]{
                    rs.getString("account_no"),
                    rs.getString("name"),
                    String.format("%.2f", rs.getDouble("balance")),
                    rs.getString("created_at")
                });
            }
 
            if (m.getRowCount() == 0) { msg("No user accounts found."); return; }
 
            JTable t = new JTable(m);
            t.setEnabled(false);
            t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
 
            JOptionPane.showMessageDialog(
                this, new JScrollPane(t),
                "All Registered Users (" + m.getRowCount() + " accounts)",
                JOptionPane.PLAIN_MESSAGE);
 
        } catch (Exception e) { msg(e); }
    }
 
    void adminViewTx() {
        try {
            DefaultTableModel m = new DefaultTableModel(
                new String[]{"TID", "Account No", "Type", "Amount (₹)", "Date/Time"}, 0);
 
            PreparedStatement ps = con.prepareStatement(
                "SELECT TOP 100 tid, account_no, type, amount, tdate " +
                "FROM transactions ORDER BY tid DESC");
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                m.addRow(new Object[]{
                    rs.getInt("tid"),
                    rs.getString("account_no"),
                    rs.getString("type"),
                    String.format("%.2f", rs.getDouble("amount")),
                    rs.getString("tdate")
                });
            }
 
            if (m.getRowCount() == 0) { msg("No transactions found."); return; }
 
            JTable t = new JTable(m);
            t.setEnabled(false);
            t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
 
            JOptionPane.showMessageDialog(
                this, new JScrollPane(t),
                "Latest 100 Transactions",
                JOptionPane.PLAIN_MESSAGE);
 
        } catch (Exception e) { msg(e); }
    }
    
    void adminDeleteAcc() {
        try {
            String accNo = JOptionPane.showInputDialog(
                this, "Enter Account Number to Delete:");
            if (accNo == null || accNo.trim().isEmpty()) return;
            accNo = accNo.trim();
 
            // verify account exists before asking for confirmation
            PreparedStatement chk = con.prepareStatement(
                "SELECT name FROM users WHERE account_no=?");
            chk.setString(1, accNo);
            ResultSet rs = chk.executeQuery();
 
            if (!rs.next()) {
                msg("Account number '" + accNo + "' not found."); return;
            }
 
            String accName = rs.getString("name");
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to DELETE account:\n" +
                "Account No : " + accNo + "\n" +
                "Name       : " + accName + "\n\n" +
                "This will also delete ALL transactions for this account.\n" +
                "This action CANNOT be undone.",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
 
            if (confirm != JOptionPane.YES_OPTION) return;
            con.setAutoCommit(false);
 
            PreparedStatement delTx = con.prepareStatement(
                "DELETE FROM transactions WHERE account_no=?");
            delTx.setString(1, accNo);
            int txDeleted = delTx.executeUpdate();
 
            PreparedStatement delUser = con.prepareStatement(
                "DELETE FROM users WHERE account_no=?");
            delUser.setString(1, accNo);
            delUser.executeUpdate();
 
            con.commit();
 
            msg("Account '" + accNo + "' (" + accName + ") deleted successfully.\n" +
                txDeleted + " transaction record(s) also removed.");
 
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {}
            msg("Deletion failed and was rolled back.\n" + e.getMessage());
        } finally {
            try { con.setAutoCommit(true); } catch (Exception e) {}
        }
    }
 
    void adminLogout() {
        adminUsername = "";
        adminUserField.setText("");
        adminPassField.setText("");
        card.show(main, "login");
    }
 
    JPanel base() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        return p;
    }
 
    JPanel cardBox() {
        JPanel p = new JPanel(new GridLayout(10, 1, 10, 10));
        p.setBackground(cardBg);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        return p;
    }
 
    JTextField input() {
        JTextField f = new JTextField();
        style(f);
        return f;
    }
 
    void style(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
 
    JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(Color.WHITE);
        return l;
    }
 
    JLabel title(String s) {
        JLabel l = new JLabel(s, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return l;
    }
 
    JLabel center(String s) {
        JLabel l = new JLabel(s, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        return l;
    }
 
    JButton btn(String s) {
        JButton b = new JButton(s);
        b.setBackground(btnColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }
    JButton btnColored(String s, Color c) {
        JButton b = btn(s);
        b.setBackground(c);
        return b;
    }
 
    JButton menuBtn(String s, Runnable r) {
        JButton b = btn(s);
        b.addActionListener(e -> r.run());
        return b;
    }
 
    JButton menuBtnColored(String s, Runnable r, Color c) {
        JButton b = btnColored(s, c);
        b.addActionListener(e -> r.run());
        return b;
    }
 
    void msg(Object o) {
        JOptionPane.showMessageDialog(this, o.toString());
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ATMSystem().setVisible(true));
    }
}
 
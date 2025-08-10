package ui;

import javax.swing.*;
import java.awt.*;
import model.UserManager;
import model.User;
import ui.MainFrame;

public class LoginFrame extends JFrame {
    private UserManager userManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton switchModeButton;
    private boolean isLoginMode = true;
    private MainFrame mainFrame;

    public LoginFrame() {
        userManager = new UserManager();
        setupUI();
    }

    private void setupUI() {
        setTitle("E-Commerce Cart System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Welcome to E-Commerce Cart System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        
        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        registerButton.setVisible(false);
        
        switchModeButton = new JButton("Switch to Register");
        switchModeButton.addActionListener(e -> switchMode());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(switchModeButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void switchMode() {
        isLoginMode = !isLoginMode;
        
        if (isLoginMode) {
            setTitle("E-Commerce Cart System - Login");
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            switchModeButton.setText("Switch to Register");
        } else {
            setTitle("E-Commerce Cart System - Register");
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            switchModeButton.setText("Switch to Login");
        }
        
        // Clear fields
        usernameField.setText("");
        passwordField.setText("");
        
        revalidate();
        repaint();
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userManager.loginUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
            openMainFrame(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userManager.userExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userManager.registerUser(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            switchMode(); // Switch back to login mode
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainFrame(User user) {
        if (mainFrame == null) {
            mainFrame = new MainFrame(user);
        }
        mainFrame.setVisible(true);
        this.setVisible(false);
    }
}

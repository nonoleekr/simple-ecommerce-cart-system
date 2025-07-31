package ui;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        setTitle("E-Commerce Cart System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to E-Commerce Cart System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.CENTER);
    }
}

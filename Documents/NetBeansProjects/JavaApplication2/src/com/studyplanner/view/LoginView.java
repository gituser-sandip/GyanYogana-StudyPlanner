package com.studyplanner.view;

import com.studyplanner.controller.UserController;
import com.studyplanner.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {

    // Colors
    private final Color BG_COLOR = Color.decode("#10101E");
    private final Color CARD_COLOR = Color.decode("#1F1F30");
    private final Color ACCENT_COLOR = Color.decode("#6C63FF");
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color TEXT_GRAY = Color.LIGHT_GRAY;

    private UserController userController;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private boolean isLoginMode = true; 

    public LoginView() {
        userController = new UserController();
        
        setTitle("GyanYogana - Login");
        setSize(400, 500); // Adjusted height since we removed email
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Background Panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Close Button
        JLabel closeBtn = new JLabel("X");
        closeBtn.setForeground(Color.GRAY);
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        mainPanel.add(closeBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title
        JLabel title = new JLabel("Welcome Back");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        
        JLabel subtitle = new JLabel("Please sign in to continue.");
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- INPUT FIELDS ---
        // We use createLabel helper to ensure text is WHITE
        
        mainPanel.add(createLabel("Username:")); 
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small gap
        txtUser = createField();
        mainPanel.add(txtUser);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        mainPanel.add(createLabel("Password:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small gap
        txtPass = createPasswordField();
        mainPanel.add(txtPass);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- BUTTON ---
        JButton btnAction = new JButton("Login");
        styleButton(btnAction);
        btnAction.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(btnAction);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- TOGGLE LINK ---
        JLabel toggleLink = new JLabel("Don't have an account? Sign Up");
        toggleLink.setForeground(ACCENT_COLOR);
        toggleLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(toggleLink);

        // --- LOGIC ---
        
        // 1. Switch between Login and Signup
        toggleLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isLoginMode = !isLoginMode;
                if (isLoginMode) {
                    title.setText("Welcome Back");
                    subtitle.setText("Please sign in to continue.");
                    btnAction.setText("Login");
                    toggleLink.setText("Don't have an account? Sign Up");
                } else {
                    title.setText("Create Account");
                    subtitle.setText("Join GyanYogana today.");
                    btnAction.setText("Sign Up");
                    toggleLink.setText("Already have an account? Login");
                }
                revalidate();
                repaint();
            }
        });

        // 2. Button Action
        btnAction.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            
            if (isLoginMode) {
                // LOGIN
                User loggedInUser = userController.loginUser(user, pass);
                if (loggedInUser != null) {
                    dispose(); // Close Login
                    new DashboardUI(loggedInUser.getUsername()).setVisible(true); // Open Dashboard
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // SIGNUP (Now only uses User & Pass)
                boolean success = userController.registerUser(user, pass);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Account Created! Please Login.");
                    // Switch back to login view
                    isLoginMode = true;
                    btnAction.setText("Login");
                    title.setText("Welcome Back");
                    toggleLink.setText("Don't have an account? Sign Up");
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists or empty fields.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(mainPanel);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 500, 30, 30));
    }

    // --- HELPER METHODS ---

    // New helper to ensure labels are visible on dark background
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_WHITE); // Make text WHITE
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setMaximumSize(new Dimension(320, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setMaximumSize(new Dimension(320, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(320, 45));
    }
}
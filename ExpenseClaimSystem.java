import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.border.*;
import java.io.File;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

// Main Application Class with Custom Graphics
public class ExpenseClaimSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ArrayList<ExpenseClaim> claims;
    private String currentUser;
    private String currentDepartment;
    private String currentIP;
    
    // Chai.co inspired Color Palette
    private static final Color CHAI_CREAM = new Color(245, 235, 220);
    private static final Color CHAI_BROWN = new Color(139, 90, 60);
    private static final Color CHAI_DARK_BROWN = new Color(101, 67, 33);
    private static final Color EMPLOYEE_COLOR = new Color(230, 180, 140);
    private static final Color FINANCE_COLOR = new Color(220, 160, 130);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    
    public ExpenseClaimSystem() {
        claims = new ArrayList<>();
        
        setTitle("Chai.Co - Employee Expense Claim Management");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createEmployeeLoginPanel(), "EMPLOYEE_LOGIN");
        mainPanel.add(createFinanceLoginPanel(), "FINANCE_LOGIN");
        mainPanel.add(createEmployeePanel(), "EMPLOYEE");
        mainPanel.add(createManagerPanel(), "MANAGER");
        mainPanel.add(createNetworkPanel(), "NETWORK");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    // Custom Rounded Button with Shadow Effect
    class RoundedButton extends JButton {
        private Color bgColor;
        private Color hoverColor;
        private boolean isHovered = false;
        
        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            this.hoverColor = bgColor.brighter();
            
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 50));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow effect
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 25, 25);
            
            // Button background with gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, isHovered ? hoverColor : bgColor,
                0, getHeight(), isHovered ? bgColor : bgColor.darker()
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 25, 25);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom Rounded Panel with Solid/Gradient Background
    class RoundedPanel extends JPanel {
        private Color topColor;
        private Color bottomColor;
        private int radius;
        private boolean useGradient;
        
        public RoundedPanel(Color color, int radius) {
            this(color, color, radius, false);
        }
        
        public RoundedPanel(Color topColor, Color bottomColor, int radius) {
            this(topColor, bottomColor, radius, true);
        }
        
        private RoundedPanel(Color topColor, Color bottomColor, int radius, boolean useGradient) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.radius = radius;
            this.useGradient = useGradient;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (radius > 0) {
                // Shadow for cards only
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, radius, radius);
            }
            
            // Background
            if (useGradient) {
                GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, getHeight(), bottomColor
                );
                g2.setPaint(gradient);
            } else {
                g2.setColor(topColor);
            }
            
            if (radius > 0) {
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, radius, radius);
            } else {
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom Rounded Text Field
    class RoundedTextField extends JTextField {
        private int radius = 15;
        
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom Rounded ComboBox
    class RoundedComboBox<E> extends JComboBox<E> {
        public RoundedComboBox(E[] items) {
            super(items);
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setBackground(Color.WHITE);
        }
    }
    
    // Custom Chai Cup Panel with Animation
    class ChaiCupPanel extends JPanel {
        private float steamOffset = 0;
        private javax.swing.Timer steamTimer;
        
        public ChaiCupPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(200, 200));
            
            // Steam animation
            steamTimer = new javax.swing.Timer(50, e -> {
                steamOffset += 0.5f;
                if (steamOffset > 30) steamOffset = 0;
                repaint();
            });
            steamTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2 + 20;
            
            // Draw steam (animated wavy lines)
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 3; i++) {
                int x = centerX - 20 + (i * 20);
                float offset = steamOffset + (i * 10);
                
                // Create wavy steam path
                Path2D steam = new Path2D.Float();
                steam.moveTo(x, centerY - 30);
                
                for (int j = 0; j < 5; j++) {
                    float y = centerY - 30 - (j * 10);
                    float wave = (float) Math.sin((offset + j * 2) * 0.3) * 6;
                    steam.lineTo(x + wave, y);
                }
                
                // Gradient for steam
                int alpha = 150 - (i * 30);
                g2.setColor(new Color(150, 150, 150, alpha));
                g2.draw(steam);
            }
            
            // Draw saucer (ellipse)
            GradientPaint saucerGradient = new GradientPaint(
                centerX - 80, centerY + 35,
                new Color(220, 220, 220),
                centerX + 80, centerY + 50,
                new Color(180, 180, 180)
            );
            g2.setPaint(saucerGradient);
            g2.fillOval(centerX - 80, centerY + 35, 160, 35);
            
            // Saucer outline
            g2.setColor(new Color(160, 160, 160));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(centerX - 80, centerY + 35, 160, 35);
            
            // Draw cup body with gradient
            GradientPaint cupGradient = new GradientPaint(
                centerX - 50, centerY - 25,
                new Color(220, 190, 150),
                centerX + 50, centerY + 30,
                new Color(180, 140, 100)
            );
            g2.setPaint(cupGradient);
            
            // Cup shape (trapezoid)
            Path2D cupBody = new Path2D.Float();
            cupBody.moveTo(centerX - 45, centerY - 25);
            cupBody.lineTo(centerX - 50, centerY + 30);
            cupBody.lineTo(centerX + 50, centerY + 30);
            cupBody.lineTo(centerX + 45, centerY - 25);
            cupBody.closePath();
            g2.fill(cupBody);
            
            // Cup outline
            g2.setColor(new Color(139, 90, 60));
            g2.setStroke(new BasicStroke(2));
            g2.draw(cupBody);
            
            // Cup rim (ellipse on top)
            GradientPaint rimGradient = new GradientPaint(
                centerX - 45, centerY - 30,
                new Color(160, 120, 90),
                centerX + 45, centerY - 20,
                new Color(200, 160, 120)
            );
            g2.setPaint(rimGradient);
            g2.fillOval(centerX - 45, centerY - 30, 90, 15);
            
            // Rim outline
            g2.setColor(new Color(139, 90, 60));
            g2.drawOval(centerX - 45, centerY - 30, 90, 15);
            
            // Draw handle
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(200, 160, 120));
            Arc2D handle = new Arc2D.Float(centerX + 35, centerY - 15, 35, 45, -80, 160, Arc2D.OPEN);
            g2.draw(handle);
            
            // Handle inner shadow
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(160, 120, 90));
            Arc2D handleInner = new Arc2D.Float(centerX + 38, centerY - 12, 29, 39, -80, 160, Arc2D.OPEN);
            g2.draw(handleInner);
            
            // Chai liquid surface
            GradientPaint chaiGradient = new GradientPaint(
                centerX - 40, centerY - 26,
                new Color(180, 120, 80),
                centerX + 40, centerY - 24,
                new Color(140, 90, 60)
            );
            g2.setPaint(chaiGradient);
            g2.fillOval(centerX - 40, centerY - 28, 80, 12);
            
            // Liquid highlight
            g2.setColor(new Color(220, 180, 140, 100));
            g2.fillOval(centerX - 25, centerY - 27, 30, 6);
            
            g2.dispose();
        }
    }
    
    // Decorative Cookie/Snack Panel
    class SnackPanel extends JPanel {
        private String type; // "cookie", "brownie", "macaron"
        
        public SnackPanel(String type) {
            this.type = type;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 100));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 10;
            int x = (w - size) / 2;
            int y = (h - size) / 2;
            
            if (type.equals("cookie")) {
                // Draw cookie with gradient
                GradientPaint cookieGrad = new GradientPaint(
                    x, y, new Color(220, 190, 150),
                    x + size, y + size, new Color(200, 160, 120)
                );
                g2.setPaint(cookieGrad);
                g2.fillOval(x, y, size, size);
                
                // Cookie outline
                g2.setColor(new Color(180, 140, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, size, size);
                
                // Chocolate chips
                g2.setColor(new Color(101, 67, 33));
                int chipSize = size / 6;
                g2.fillOval(x + size/4, y + size/4, chipSize, chipSize);
                g2.fillOval(x + size*3/5, y + size/5, chipSize, chipSize);
                g2.fillOval(x + size/5, y + size*3/5, chipSize, chipSize);
                g2.fillOval(x + size*3/5, y + size*3/5, chipSize, chipSize);
                g2.fillOval(x + size*2/5, y + size/2, chipSize, chipSize);
                
            } else if (type.equals("brownie")) {
                // Draw brownie layers
                int layer1 = y + size/3;
                int layer2 = y + size*2/3;
                
                // Bottom layer (darker)
                g2.setColor(new Color(101, 67, 33));
                g2.fillRoundRect(x, layer1, size, size/3, 10, 10);
                
                // Frosting layer
                g2.setColor(new Color(139, 90, 60));
                g2.fillRect(x + 5, layer1, size - 10, 8);
                
                // Middle cake layer
                g2.setColor(new Color(160, 110, 70));
                g2.fillRoundRect(x, layer1 + 8, size, size/3 - 8, 8, 8);
                
                // Top frosting
                g2.setColor(new Color(139, 90, 60));
                g2.fillRect(x + 5, layer2 - 8, size - 10, 8);
                
                // Top layer
                g2.setColor(new Color(101, 67, 33));
                g2.fillRoundRect(x, layer2, size, size/3, 10, 10);
                
                // Decorative dots
                g2.setColor(new Color(200, 100, 100));
                g2.fillOval(x + size/3, y + size/2, 6, 6);
                
            } else if (type.equals("macaron")) {
                // Top shell (purple)
                GradientPaint topGrad = new GradientPaint(
                    x, y, new Color(210, 190, 230),
                    x + size, y + size/3, new Color(180, 160, 210)
                );
                g2.setPaint(topGrad);
                g2.fillOval(x, y, size, size/3);
                
                // Bottom shell (green)
                GradientPaint bottomGrad = new GradientPaint(
                    x, y + size*2/3, new Color(170, 230, 170),
                    x + size, y + size, new Color(130, 200, 130)
                );
                g2.setPaint(bottomGrad);
                g2.fillOval(x, y + size*2/3, size, size/3);
                
                // Cream filling
                g2.setColor(new Color(255, 250, 235));
                g2.fillRect(x + size/8, y + size/3, size*3/4, size/3);
                
                // Filling edges
                g2.fillOval(x + size/8 - 5, y + size/3, 10, size/3);
                g2.fillOval(x + size*7/8 - 5, y + size/3, 10, size/3);
                
                // Outlines
                g2.setColor(new Color(150, 130, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, size, size/3);
                g2.setColor(new Color(100, 160, 100));
                g2.drawOval(x, y + size*2/3, size, size/3);
            }
            
            g2.dispose();
        }
    }
    
    // Launch Page with Chai Theme - No Card, Full Space for Animations
    private JPanel createLoginPanel() {
        JPanel mainPanel = new RoundedPanel(CHAI_CREAM, 0);
        mainPanel.setLayout(new BorderLayout());
        
        // Center content panel with better spacing
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Top decorative cookies
        JPanel topDecor = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 0));
        topDecor.setOpaque(false);
        topDecor.setMaximumSize(new Dimension(900, 120));
        topDecor.add(new SnackPanel("cookie"));
        topDecor.add(new SnackPanel("brownie"));
        centerPanel.add(topDecor);
        
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Animated Chai Cup (smaller to fit)
        JPanel cupContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cupContainer.setOpaque(false);
        cupContainer.setMaximumSize(new Dimension(900, 180));
        ChaiCupPanel cupPanel = new ChaiCupPanel();
        cupPanel.setPreferredSize(new Dimension(180, 180));
        cupContainer.add(cupPanel);
        centerPanel.add(cupContainer);
        
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(900, 180));
        
        JLabel arcText = new JLabel("Employee Expense Claim Management");
        arcText.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        arcText.setForeground(CHAI_BROWN);
        arcText.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(arcText);
        
        titlePanel.add(Box.createVerticalStrut(5));
        
        JLabel titleLabel = new JLabel("chai.co");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        titleLabel.setForeground(CHAI_DARK_BROWN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        
        titlePanel.add(Box.createVerticalStrut(5));
        
        JLabel tagline = new JLabel("→ ek adrak wali chai");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        tagline.setForeground(CHAI_BROWN);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(tagline);
        
        centerPanel.add(titlePanel);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Department selection buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(900, 80));
        
        RoundedButton employeeBtn = new RoundedButton("Employee", EMPLOYEE_COLOR);
        employeeBtn.setPreferredSize(new Dimension(200, 60));
        employeeBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        RoundedButton financeBtn = new RoundedButton("Finance", FINANCE_COLOR);
        financeBtn.setPreferredSize(new Dimension(200, 60));
        financeBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        buttonPanel.add(employeeBtn);
        buttonPanel.add(financeBtn);
        
        centerPanel.add(buttonPanel);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Bottom snacks decoration (full visibility)
        JPanel bottomDecor = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 0));
        bottomDecor.setOpaque(false);
        bottomDecor.setMaximumSize(new Dimension(900, 120));
        bottomDecor.add(new SnackPanel("cookie"));
        bottomDecor.add(new SnackPanel("macaron"));
        bottomDecor.add(new SnackPanel("brownie"));
        bottomDecor.add(new SnackPanel("macaron"));
        centerPanel.add(bottomDecor);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Credits
        JPanel creditsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        creditsPanel.setOpaque(false);
        creditsPanel.setMaximumSize(new Dimension(900, 40));
        JLabel credits = new JLabel("Aarushee and Atharva Presents");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        credits.setForeground(CHAI_BROWN);
        creditsPanel.add(credits);
        centerPanel.add(creditsPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Event Listeners - Navigate to separate login pages
        employeeBtn.addActionListener(e -> {
            cardLayout.show(this.mainPanel, "EMPLOYEE_LOGIN");
        });
        
        financeBtn.addActionListener(e -> {
            cardLayout.show(this.mainPanel, "FINANCE_LOGIN");
        });
        
        return mainPanel;
    }
    
    // Employee Login Page
    private JPanel createEmployeeLoginPanel() {
        JPanel mainPanel = new RoundedPanel(CHAI_CREAM, 0);
        mainPanel.setLayout(new BorderLayout());
        
        // Center content panel with BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(600, 60));
        JLabel titleLabel = new JLabel("Employee Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(CHAI_DARK_BROWN);
        titlePanel.add(titleLabel);
        centerPanel.add(titlePanel);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Chai cup with full space
        JPanel cupContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cupContainer.setOpaque(false);
        cupContainer.setMaximumSize(new Dimension(600, 200));
        ChaiCupPanel cupPanel = new ChaiCupPanel();
        cupPanel.setPreferredSize(new Dimension(180, 180));
        cupContainer.add(cupPanel);
        centerPanel.add(cupContainer);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Username label
        JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userLabelPanel.setOpaque(false);
        userLabelPanel.setMaximumSize(new Dimension(600, 40));
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(CHAI_DARK_BROWN);
        userLabelPanel.add(userLabel);
        centerPanel.add(userLabelPanel);
        
        centerPanel.add(Box.createVerticalStrut(5));
        
        // Username field
        JPanel userFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userFieldPanel.setOpaque(false);
        userFieldPanel.setMaximumSize(new Dimension(600, 60));
        RoundedTextField usernameField = new RoundedTextField(25);
        userFieldPanel.add(usernameField);
        centerPanel.add(userFieldPanel);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Email label
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailLabelPanel.setOpaque(false);
        emailLabelPanel.setMaximumSize(new Dimension(600, 40));
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emailLabel.setForeground(CHAI_DARK_BROWN);
        emailLabelPanel.add(emailLabel);
        centerPanel.add(emailLabelPanel);
        
        centerPanel.add(Box.createVerticalStrut(5));
        
        // Email field
        JPanel emailFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailFieldPanel.setOpaque(false);
        emailFieldPanel.setMaximumSize(new Dimension(600, 60));
        RoundedTextField emailField = new RoundedTextField(25);
        emailFieldPanel.add(emailField);
        centerPanel.add(emailFieldPanel);
        
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(600, 70));
        
        RoundedButton loginBtn = new RoundedButton("Login", SUCCESS_COLOR);
        RoundedButton backBtn = new RoundedButton("Back", CHAI_BROWN);
        
        btnPanel.add(loginBtn);
        btnPanel.add(backBtn);
        centerPanel.add(btnPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Event listeners
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (username.isEmpty()) {
                showStyledMessage("Please enter username!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (email.isEmpty()) {
                showStyledMessage("Please enter email address!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                showStyledMessage("Please enter a valid email address!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            currentUser = username + " (" + email + ")";
            currentDepartment = "Employee";
            currentIP = "192.168.10." + (10 + new Random().nextInt(20));
            cardLayout.show(this.mainPanel, "EMPLOYEE");
            showNetworkActivity("Login", currentIP, "192.168.40.40 (DB-Server)");
        });
        
        backBtn.addActionListener(e -> cardLayout.show(this.mainPanel, "LOGIN"));
        
        return mainPanel;
    }
    
    // Finance Login Page
    private JPanel createFinanceLoginPanel() {
        JPanel mainPanel = new RoundedPanel(CHAI_CREAM, 0);
        mainPanel.setLayout(new BorderLayout());
        
        // Center content panel with BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(600, 60));
        JLabel titleLabel = new JLabel("Finance Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(CHAI_DARK_BROWN);
        titlePanel.add(titleLabel);
        centerPanel.add(titlePanel);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Chai cup with full space
        JPanel cupContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cupContainer.setOpaque(false);
        cupContainer.setMaximumSize(new Dimension(600, 200));
        ChaiCupPanel cupPanel = new ChaiCupPanel();
        cupPanel.setPreferredSize(new Dimension(180, 180));
        cupContainer.add(cupPanel);
        centerPanel.add(cupContainer);
        
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Username label
        JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userLabelPanel.setOpaque(false);
        userLabelPanel.setMaximumSize(new Dimension(600, 40));
        JLabel userLabel = new JLabel("Manager Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(CHAI_DARK_BROWN);
        userLabelPanel.add(userLabel);
        centerPanel.add(userLabelPanel);
        
        centerPanel.add(Box.createVerticalStrut(5));
        
        // Username field
        JPanel userFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userFieldPanel.setOpaque(false);
        userFieldPanel.setMaximumSize(new Dimension(600, 60));
        RoundedTextField usernameField = new RoundedTextField(25);
        userFieldPanel.add(usernameField);
        centerPanel.add(userFieldPanel);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Email label
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailLabelPanel.setOpaque(false);
        emailLabelPanel.setMaximumSize(new Dimension(600, 40));
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emailLabel.setForeground(CHAI_DARK_BROWN);
        emailLabelPanel.add(emailLabel);
        centerPanel.add(emailLabelPanel);
        
        centerPanel.add(Box.createVerticalStrut(5));
        
        // Email field
        JPanel emailFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailFieldPanel.setOpaque(false);
        emailFieldPanel.setMaximumSize(new Dimension(600, 60));
        RoundedTextField emailField = new RoundedTextField(25);
        emailFieldPanel.add(emailField);
        centerPanel.add(emailFieldPanel);
        
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(600, 70));
        
        RoundedButton loginBtn = new RoundedButton("Login", SUCCESS_COLOR);
        RoundedButton backBtn = new RoundedButton("Back", CHAI_BROWN);
        
        btnPanel.add(loginBtn);
        btnPanel.add(backBtn);
        centerPanel.add(btnPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Event listeners
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (username.isEmpty()) {
                showStyledMessage("Please enter username!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (email.isEmpty()) {
                showStyledMessage("Please enter email address!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                showStyledMessage("Please enter a valid email address!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            currentUser = username + " (" + email + ")";
            currentDepartment = "Finance";
            currentIP = "192.168.30." + (10 + new Random().nextInt(2));
            cardLayout.show(this.mainPanel, "MANAGER");
            showNetworkActivity("Login", currentIP, "192.168.40.40 (DB-Server)");
        });
        
        backBtn.addActionListener(e -> cardLayout.show(this.mainPanel, "LOGIN"));
        
        return mainPanel;
    }
    
    // Employee Panel
    private JPanel createEmployeePanel() {
        JPanel panel = new RoundedPanel(CHAI_CREAM, 0);
        panel.setLayout(new BorderLayout(0, 0));
        
        // Header with gradient
        JPanel headerPanel = new RoundedPanel(EMPLOYEE_COLOR, CHAI_BROWN, 0);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel headerLabel = new JLabel("Employee");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel();
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(25, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Form Card
        RoundedPanel formCard = new RoundedPanel(Color.WHITE, new Color(255, 250, 245), 25);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel formTitle = new JLabel("Submit New Expense Claim");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        formTitle.setForeground(CHAI_DARK_BROWN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        
        // Expense Type
        JLabel typeLabel = new JLabel("Expense Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        typeLabel.setForeground(CHAI_DARK_BROWN);
        formCard.add(typeLabel, gbc);
        
        String[] types = {"Travel", "Food", "Supplies", "Training", "Other"};
        RoundedComboBox<String> typeCombo = new RoundedComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(300, 45));
        gbc.gridx = 1;
        formCard.add(typeCombo, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel amountLabel = new JLabel("Amount (Rs)");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountLabel.setForeground(CHAI_DARK_BROWN);
        formCard.add(amountLabel, gbc);
        
        RoundedTextField amountField = new RoundedTextField(20);
        gbc.gridx = 1;
        formCard.add(amountField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        descLabel.setForeground(CHAI_DARK_BROWN);
        formCard.add(descLabel, gbc);
        
        JTextArea descArea = new JTextArea(4, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        gbc.gridx = 1;
        formCard.add(scrollPane, gbc);
        
        // Proof of Expense (File Upload)
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel proofLabel = new JLabel("Proof of Expense");
        proofLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        proofLabel.setForeground(CHAI_DARK_BROWN);
        formCard.add(proofLabel, gbc);
        
        JPanel proofPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        proofPanel.setOpaque(false);
        
        JLabel fileLabel = new JLabel("No file selected");
        fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        fileLabel.setForeground(new Color(120, 120, 120));
        
        RoundedButton browseBtn = new RoundedButton("Browse", WARNING_COLOR);
        browseBtn.setPreferredSize(new Dimension(120, 40));
        
        final String[] selectedFilePath = {null};
        
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Proof of Expense");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".pdf") || name.endsWith(".jpg") || 
                           name.endsWith(".jpeg") || name.endsWith(".png");
                }
                public String getDescription() {
                    return "Image & PDF files (*.jpg, *.png, *.pdf)";
                }
            });
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                selectedFilePath[0] = selectedFile.getAbsolutePath();
                fileLabel.setText(selectedFile.getName());
                fileLabel.setForeground(SUCCESS_COLOR);
            }
        });
        
        proofPanel.add(browseBtn);
        proofPanel.add(fileLabel);
        
        gbc.gridx = 1;
        formCard.add(proofPanel, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        btnPanel.setOpaque(false);
        
        RoundedButton submitBtn = new RoundedButton("Submit Claim", SUCCESS_COLOR);
        submitBtn.setPreferredSize(new Dimension(160, 50));
        RoundedButton viewBtn = new RoundedButton("View My Claims", CHAI_BROWN);
        viewBtn.setPreferredSize(new Dimension(160, 50));
        RoundedButton backToMainBtn = new RoundedButton("Back to Main", WARNING_COLOR);
        backToMainBtn.setPreferredSize(new Dimension(160, 50));
        RoundedButton logoutBtn = new RoundedButton("Logout", DANGER_COLOR);
        logoutBtn.setPreferredSize(new Dimension(160, 50));
        
        btnPanel.add(submitBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(backToMainBtn);
        btnPanel.add(logoutBtn);
        
        formCard.add(btnPanel, gbc);
        
        contentPanel.add(formCard, BorderLayout.CENTER);
        
        // Network Activity Log
        RoundedPanel logCard = new RoundedPanel(new Color(250, 250, 250), Color.WHITE, 20);
        logCard.setLayout(new BorderLayout());
        logCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel logTitle = new JLabel("Network Activity Log");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logTitle.setForeground(CHAI_DARK_BROWN);
        logTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        logCard.add(logTitle, BorderLayout.NORTH);
        
        JTextArea networkLog = new JTextArea(6, 40);
        networkLog.setEditable(false);
        networkLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        networkLog.setBackground(new Color(255, 255, 255));
        JScrollPane logScroll = new JScrollPane(networkLog);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        logCard.add(logScroll, BorderLayout.CENTER);
        
        contentPanel.add(logCard, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Event Listeners
        submitBtn.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String desc = descArea.getText();
                
                if (desc.trim().isEmpty()) {
                    showStyledMessage("Please enter description!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (selectedFilePath[0] == null) {
                    showStyledMessage("Please upload proof of expense!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Extract employee name and email from currentUser
                // Format: "username (email@example.com)"
                String empname = currentUser.substring(0, currentUser.indexOf("(")).trim();
                String email = currentUser.substring(currentUser.indexOf("(") + 1, currentUser.indexOf(")"));
                
                // Create File object from selected path
                java.io.File proofFile = new java.io.File(selectedFilePath[0]);
                
                // Insert into database using DAO
                ClaimDAO dao = new ClaimDAO();
                boolean success = dao.insertClaim(empname, email, amount, desc, proofFile);
                
                if (success) {
                    // Also add to in-memory list for UI
                    ExpenseClaim claim = new ExpenseClaim(
                        claims.size() + 1,
                        currentUser,
                        currentDepartment,
                        currentIP,
                        type,
                        amount,
                        desc,
                        selectedFilePath[0]
                    );
                    claims.add(claim);
                    
                    networkLog.append("\n[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]\n");
                    networkLog.append("→ " + currentIP + " (Employee PC) - Sending claim data...\n");
                    networkLog.append("→ 192.168.40.30 (App-Server) - Processing claim...\n");
                    networkLog.append("→ 192.168.40.40 (DB-Server) - Storing in PostgreSQL\n");
                    networkLog.append("→ 192.168.40.20 (Email-Server) - Notification sent\n");
                    networkLog.append("→ 192.168.30.10 (Finance PC) - New claim received!\n");
                    
                    showStyledMessage(
                        "Claim submitted successfully!\n\n" +
                        "Employee: " + empname + "\n" +
                        "Amount: Rs " + amount + "\n" +
                        "Proof: " + proofFile.getName() + "\n" +
                        "Status: Stored in PostgreSQL Database\n\n" +
                        "Network Route:\n" + currentIP + " → App Server → DB Server → Finance",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear form
                    amountField.setText("");
                    descArea.setText("");
                    selectedFilePath[0] = null;
                    fileLabel.setText("No file selected");
                    fileLabel.setForeground(new Color(120, 120, 120));
                } else {
                    showStyledMessage("Failed to submit claim to database!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                showStyledMessage("Please enter valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        viewBtn.addActionListener(e -> showMyClaims());
        backToMainBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        panel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                userLabel.setText("User: " + currentUser + " | " + currentDepartment + " | IP: " + currentIP);
            }
        });
        
        return panel;
    }
    
    
    // Manager Panel
    private JPanel createManagerPanel() {
        JPanel panel = new RoundedPanel(CHAI_CREAM, 0);
        panel.setLayout(new BorderLayout(0, 0));
        
        // Header
        JPanel headerPanel = new RoundedPanel(FINANCE_COLOR, CHAI_DARK_BROWN, 0);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel headerLabel = new JLabel("Finance");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel();
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(25, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        RoundedPanel tableCard = new RoundedPanel(Color.WHITE, new Color(255, 250, 245), 25);
        tableCard.setLayout(new BorderLayout(0, 20));
        tableCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel tableTitle = new JLabel("Pending Expense Claims");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        tableTitle.setForeground(CHAI_DARK_BROWN);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Employee", "Email", "Type", "Amount", "Status", "Date", "Proof"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(CHAI_BROWN);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(CHAI_DARK_BROWN);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.setGridColor(new Color(220, 220, 220));
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Add mouse listener to open proof file
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                // Check if Proof column was clicked
                if (col == 7 && row >= 0) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    for (ExpenseClaim claim : claims) {
                        if (claim.getId() == id && claim.getProofFilePath() != null) {
                            try {
                                java.io.File proofFile = new java.io.File(claim.getProofFilePath());
                                if (proofFile.exists()) {
                                    Desktop.getDesktop().open(proofFile);
                                } else {
                                    showStyledMessage("Proof file not found: " + proofFile.getName(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                showStyledMessage("Could not open proof file: " + ex.getMessage(), 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        }
                    }
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        tableCard.add(tableScroll, BorderLayout.CENTER);
        
        contentPanel.add(tableCard, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton approveBtn = new RoundedButton("Approve", SUCCESS_COLOR);
        RoundedButton rejectBtn = new RoundedButton("Reject", DANGER_COLOR);
        RoundedButton refreshBtn = new RoundedButton("Refresh", WARNING_COLOR);
        RoundedButton logoutBtn = new RoundedButton("Logout", CHAI_DARK_BROWN);
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(logoutBtn);
        
        RoundedButton backToMainBtn = new RoundedButton("Back to Main", WARNING_COLOR);
        backToMainBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        buttonPanel.add(backToMainBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // ============= EVENT LISTENERS =============
        
        // APPROVE BUTTON - with email notification
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Get the claim ID from table
                int claimId = (int) tableModel.getValueAt(row, 0);
                String empEmail = (String) tableModel.getValueAt(row, 2); // Get email from table
                String empName = (String) tableModel.getValueAt(row, 1); // Get employee name
                double amount = Double.parseDouble(
                    tableModel.getValueAt(row, 4).toString().replace("Rs ", "")
                );
                
                // Update in database using DAO
                ClaimDAO dao = new ClaimDAO();
                boolean dbSuccess = dao.updateClaimStatus(claimId, "Approved");
                
                if (dbSuccess) {
                    // Send approval email
                    sendApprovalEmail(empEmail, empName, claimId, amount, "Approved");
                    
                    // Also update in-memory list for UI
                    for (ExpenseClaim claim : claims) {
                        if (claim.getId() == claimId) {
                            claim.setStatus("Approved");
                            break;
                        }
                    }
                    
                    refreshTable(tableModel);
                    showNetworkActivity("Approval", "192.168.30.10", "192.168.40.40 (DB-Server)");
                    showStyledMessage(
                        "Claim #" + claimId + " approved successfully!\n\n" +
                        "✓ Status updated in PostgreSQL database\n" +
                        "✓ Email notification sent to employee\n" +
                        "✓ Employee: " + empName + "\n" +
                        "✓ Email: " + empEmail, 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    showStyledMessage(
                        "Failed to update claim status in database!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                showStyledMessage("Please select a claim!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // REJECT BUTTON - with rejection reason and email notification
        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Get the claim ID from table
                int claimId = (int) tableModel.getValueAt(row, 0);
                String empEmail = (String) tableModel.getValueAt(row, 2); // Get email from table
                String empName = (String) tableModel.getValueAt(row, 1); // Get employee name
                double amount = Double.parseDouble(
                    tableModel.getValueAt(row, 4).toString().replace("Rs ", "")
                );
                
                // Ask for rejection reason
                String reason = JOptionPane.showInputDialog(
                    this,
                    "Please enter rejection reason:",
                    "Rejection Reason",
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (reason == null || reason.trim().isEmpty()) {
                    showStyledMessage("Rejection reason is required!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Update in database using DAO
                ClaimDAO dao = new ClaimDAO();
                boolean dbSuccess = dao.updateClaimStatus(claimId, "Rejected");
                
                if (dbSuccess) {
                    // Send rejection email with reason
                    sendRejectionEmail(empEmail, empName, claimId, amount, reason);
                    
                    // Also update in-memory list for UI
                    for (ExpenseClaim claim : claims) {
                        if (claim.getId() == claimId) {
                            claim.setStatus("Rejected");
                            break;
                        }
                    }
                    
                    refreshTable(tableModel);
                    showNetworkActivity("Rejection", "192.168.30.10", "192.168.40.40 (DB-Server)");
                    showStyledMessage(
                        "Claim #" + claimId + " rejected successfully!\n\n" +
                        "✓ Status updated in PostgreSQL database\n" +
                        "✓ Email notification sent to employee\n" +
                        "✓ Employee: " + empName + "\n" +
                        "✓ Reason: " + reason, 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    showStyledMessage(
                        "Failed to update claim status in database!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                showStyledMessage("Please select a claim!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // REFRESH BUTTON - load from database
        refreshBtn.addActionListener(e -> {
            // Fetch latest claims from database
            ClaimDAO dao = new ClaimDAO();
            ArrayList<ClaimData> dbClaims = dao.getAllClaims();
            
            // Clear and populate table with database data
            tableModel.setRowCount(0);
            
            for (ClaimData dbClaim : dbClaims) {
                String proofFileName = dbClaim.getProofname() != null ? 
                    "📎 " + dbClaim.getProofname() : "No proof";
                
                tableModel.addRow(new Object[]{
                    dbClaim.getSrNo(),
                    dbClaim.getEmpname(),
                    dbClaim.getEmail(),
                    "N/A", // Type (not in DB)
                    "Rs " + dbClaim.getAmount(),
                    dbClaim.getStatus(),
                    new SimpleDateFormat("dd-MM-yyyy HH:mm").format(dbClaim.getCreatedAt()),
                    proofFileName
                });
            }
            
            showStyledMessage(
                "Refreshed!\n\nLoaded " + dbClaims.size() + " claims from database", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        panel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                userLabel.setText("Manager: " + currentUser + " | IP: " + currentIP);
                refreshTable(tableModel);
            }
        });
        
        return panel;
    }
    
    
    
    // Network Topology Panel
    private JPanel createNetworkPanel() {
        JPanel panel = new RoundedPanel(CHAI_CREAM, 0);
        panel.setLayout(new BorderLayout(0, 0));
        
        // Header
        JPanel headerPanel = new RoundedPanel(new Color(70, 70, 70), new Color(40, 40, 40), 0);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        headerPanel.setPreferredSize(new Dimension(0, 90));
        
        JLabel headerLabel = new JLabel("Network Topology - Multi-VLAN Architecture");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(25, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        RoundedPanel topologyCard = new RoundedPanel(Color.WHITE, new Color(255, 255, 250), 25);
        topologyCard.setLayout(new BorderLayout());
        topologyCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JTextArea topologyArea = new JTextArea();
        topologyArea.setEditable(false);
        topologyArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        topologyArea.setBackground(new Color(255, 255, 255));
        topologyArea.setText(
            "\n" +
            "                    [ Edge-Router - 2911 ]\n" +
            "                            |\n" +
            "                   [ Core-Switch - 3560-24PS ]\n" +
            "                   (Inter-VLAN Routing Enabled)\n" +
            "              _____||_\n" +
            "             |                          |      |\n" +
            "      [ Employee-Switch ]         [ Finance ]  [ Servers ]\n" +
            "        (VLAN 10)                  (VLAN 30)   (VLAN 40)\n" +
            "            |                          |          |\n" +
            "      20 Employee PCs            2 Finance   4 Servers\n" +
            "   192.168.10.10-29             192.168.30.10+  |\n" +
            "                                                 |\n" +
            "                        Servers VLAN 40:        |\n" +
            "                        • File-Server    : 192.168.40.10\n" +
            "                        • Email-Server   : 192.168.40.20\n" +
            "                        • App-Server     : 192.168.40.30\n" +
            "                        • DB-Server      : 192.168.40.40\n" +
            "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "\n" +
            "Expense Claim Workflow:\n" +
            "\n" +
            "1. Employee submits claim from Employee PC (VLAN 10)\n" +
            "   → Packet travels through access switch\n" +
            "   → Core-Switch routes to VLAN 40\n" +
            "\n" +
            "2. App-Server (192.168.40.30) processes request\n" +
            "   → Validates claim data\n" +
            "   → Forwards to DB-Server\n" +
            "\n" +
            "3. DB-Server (192.168.40.40) stores claim\n" +
            "   → Triggers Email-Server notification\n" +
            "\n" +
            "4. Email-Server (192.168.40.20) sends alert\n" +
            "   → Notification to Finance VLAN\n" +
            "\n" +
            "5. Finance Manager (192.168.30.10) reviews\n" +
            "   → Approves/Rejects claim\n" +
            "   → Response sent back through network\n" +
            "\n" +
            "All inter-VLAN communication passes through Core-Switch\n" +
            "with routing enabled for seamless connectivity!\n"
        );
        
        JScrollPane scrollPane = new JScrollPane(topologyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        topologyCard.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(topologyCard, BorderLayout.CENTER);
        
        RoundedButton backBtn = new RoundedButton("Back to Login", CHAI_BROWN);
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(backBtn);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void refreshTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (ExpenseClaim claim : claims) {
            String proofFileName = "No proof";
            if (claim.getProofFilePath() != null) {
                java.io.File proofFile = new java.io.File(claim.getProofFilePath());
                proofFileName = "📎 " + proofFile.getName();
            }
            
            // Extract employee name and email
            String fullEmployee = claim.getEmployee();
            String empName = fullEmployee;
            String empEmail = "";
            
            if (fullEmployee.contains("(") && fullEmployee.contains(")")) {
                empName = fullEmployee.substring(0, fullEmployee.indexOf("(")).trim();
                empEmail = fullEmployee.substring(fullEmployee.indexOf("(") + 1, fullEmployee.indexOf(")"));
            }
            
            model.addRow(new Object[]{
                claim.getId(),
                empName,
                empEmail,
                claim.getType(),
                "Rs " + claim.getAmount(),
                claim.getStatus(),
                claim.getDate(),
                proofFileName
            });
        }
    }
    
    private void showMyClaims() {
        StringBuilder sb = new StringBuilder("Your Claims:\n\n");
        boolean found = false;
        for (ExpenseClaim claim : claims) {
            if (claim.getEmployee().equals(currentUser)) {
                found = true;
                sb.append("Claim #").append(claim.getId()).append("\n");
                sb.append("Type: ").append(claim.getType()).append("\n");
                sb.append("Amount: Rs ").append(claim.getAmount()).append("\n");
                sb.append("Status: ").append(claim.getStatus()).append("\n");
                sb.append("Date: ").append(claim.getDate()).append("\n\n");
            }
        }
        if (!found) {
            sb.append("No claims found.");
        }
        showStyledMessage(sb.toString(), "My Claims", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showNetworkActivity(String action, String sourceIP, String destIP) {
        String message = String.format(
            "Network Activity - %s\n\n" +
            "Source: %s\n" +
            "App Server: 192.168.40.30\n" +
            "DB Server: 192.168.40.40\n" +
            "Destination: %s\n\n" +
            "All packets routed through Core-Switch!",
            action, sourceIP, destIP
        );
        System.out.println(message);
    }
    
    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // ========== EMAIL HELPER METHODS ==========
    
    // Helper method to send approval email
    private void sendApprovalEmail(String recipient, String empName, int claimId, double amount, String status) {
        final String sender = "aarusheepandagare@gmail.com";
        final String password = "zgqt mklx fxsd tfyk";
        final String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Expense Claim Approved - Claim #" + claimId);
            message.setText(
                "Hello " + empName + ",\n\n" +
                "Great news! Your expense claim has been APPROVED by the Finance team.\n\n" +
                "Claim Details:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Claim ID: #" + claimId + "\n" +
                "Amount: ₹" + amount + "\n" +
                "Status: " + status + "\n\n" +
                "The approved amount will be processed and credited to your account shortly.\n\n" +
                "If you have any questions, please contact the Finance Department.\n\n" +
                "Thank you,\n" +
                "Finance Team\n" +
                "Chai.Co"
            );
            Transport.send(message);
            System.out.println("✅ Approval email sent to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("✗ Failed to send approval email to: " + recipient);
            e.printStackTrace();
            showStyledMessage(
                "Warning: Status updated but email failed to send!\n" + e.getMessage(),
                "Email Error",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // Helper method to send rejection email
    private void sendRejectionEmail(String recipient, String empName, int claimId, double amount, String reason) {
        final String sender = "aarusheepandagare@gmail.com";
        final String password = "zgqt mklx fxsd tfyk";
        final String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Expense Claim Rejected - Claim #" + claimId);
            message.setText(
                "Hello " + empName + ",\n\n" +
                "We regret to inform you that your expense claim has been REJECTED by the Finance team.\n\n" +
                "Claim Details:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Claim ID: #" + claimId + "\n" +
                "Amount: ₹" + amount + "\n" +
                "Status: Rejected\n\n" +
                "Reason for Rejection:\n" +
                reason + "\n\n" +
                "If you believe this decision was made in error or need clarification,\n" +
                "please contact the Finance Department.\n\n" +
                "You may submit a revised claim with proper documentation.\n\n" +
                "Thank you,\n" +
                "Finance Team\n" +
                "Chai.Co"
            );
            Transport.send(message);
            System.out.println("✅ Rejection email sent to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("✗ Failed to send rejection email to: " + recipient);
            e.printStackTrace();
            showStyledMessage(
                "Warning: Status updated but email failed to send!\n" + e.getMessage(),
                "Email Error",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseClaimSystem app = new ExpenseClaimSystem();
            app.setVisible(true);
        });
    }
}

// ExpenseClaim Class
class ExpenseClaim {
    private int id;
    private String employee;
    private String department;
    private String employeeIP;
    private String type;
    private double amount;
    private String description;
    private String status;
    private String date;
    private String proofFilePath;
    
    public ExpenseClaim(int id, String employee, String department, String employeeIP,
                       String type, double amount, String description, String proofFilePath) {
        this.id = id;
        this.employee = employee;
        this.department = department;
        this.employeeIP = employeeIP;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.status = "Pending";
        this.date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        this.proofFilePath = proofFilePath;
    }
    
    public int getId() { return id; }
    public String getEmployee() { return employee; }
    public String getDepartment() { return department; }
    public String getEmployeeIP() { return employeeIP; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getProofFilePath() { return proofFilePath; }
    public void setStatus(String status) { this.status = status; }
}

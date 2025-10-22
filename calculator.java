package CalculatorApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class calculator extends JFrame implements ActionListener {
    private CustomTextField display;
    private String operator = "";
    private double firstNumber = 0;
    private boolean isOperatorClicked = false;
    private boolean isDarkTheme = true;
    private Color glowColor;

    // Custom TextField with placeholder and glow effect
    private static class CustomTextField extends JTextField {
        private String placeholder = "";
        private Color glowColor;

        public CustomTextField(Color glowColor) {
            this.glowColor = glowColor;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }

        public void setGlowColor(Color glowColor) {
            this.glowColor = glowColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(glowColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            // Draw placeholder if text is empty
            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics();
                int x = getInsets().left;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(Color.GRAY);
                g2.drawString(placeholder, x, y);
            }

            g2.dispose();
        }
    }

    // Helper method to safely parse display text to double
    private Double safeParseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Custom RoundedButton class for modern styling with gradients and animations
    private static class RoundedButton extends JButton {
        private Color startColor;
        private Color endColor;
        private Color hoverStartColor;
        private Color hoverEndColor;
        private float scale = 1.0f;

        public RoundedButton(String text, Color start, Color end, Color hStart, Color hEnd) {
            super(text);
            this.startColor = start;
            this.endColor = end;
            this.hoverStartColor = hStart;
            this.hoverEndColor = hEnd;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.WHITE);

            // Add mouse listeners for scale animation
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setScale(0.95f);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    setScale(1.0f);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setScale(1.05f);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setScale(1.0f);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Apply scale for animation
            g2.scale(scale, scale);

            // Create gradient
            Color sColor = getModel().isRollover() ? hoverStartColor : startColor;
            Color eColor = getModel().isRollover() ? hoverEndColor : endColor;
            GradientPaint gradient = new GradientPaint(0, 0, sColor, getWidth(), getHeight(), eColor);
            g2.setPaint(gradient);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

            super.paintComponent(g2);
            g2.dispose();
        }

        public void setScale(float scale) {
            this.scale = scale;
            repaint();
        }
    }

    public calculator() {
        setTitle("Creative Calculator");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        updateTheme();

        // Center the window
        setLocationRelativeTo(null);

        // Custom display with glow effect and placeholder
        display = new CustomTextField(glowColor);
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 32));
        display.setBackground(isDarkTheme ? new Color(50, 50, 50) : Color.WHITE);
        display.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 4, 10, 10));
        buttonPanel.setBackground(isDarkTheme ? new Color(30, 30, 30) : new Color(250, 250, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "sin", "cos", "tan", "sqrt",
            "log", "ln", "^", "pi",
            "e", "C"
        };

        for (String text : buttons) {
            RoundedButton button;
            if (text.matches("[0-9]") || text.equals(".")) {
                button = new RoundedButton(text,
                    new Color(100, 100, 100), new Color(80, 80, 80),
                    new Color(120, 120, 120), new Color(100, 100, 100));
            } else if (text.equals("=")) {
                button = new RoundedButton(text,
                    new Color(34, 139, 34), new Color(0, 100, 0),
                    new Color(50, 205, 50), new Color(34, 139, 34));
            } else if (text.equals("C")) {
                button = new RoundedButton(text,
                    new Color(220, 20, 60), new Color(180, 0, 40),
                    new Color(255, 69, 0), new Color(220, 20, 60));
            } else {
                button = new RoundedButton(text,
                    new Color(255, 165, 0), new Color(200, 130, 0),
                    new Color(255, 185, 20), new Color(255, 165, 0));
            }
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void updateTheme() {
        glowColor = isDarkTheme ? new Color(0, 255, 255, 100) : new Color(255, 0, 255, 100);
        getContentPane().setBackground(isDarkTheme ? new Color(30, 30, 30) : new Color(250, 250, 250));
        if (display != null) {
            display.setBackground(isDarkTheme ? new Color(50, 50, 50) : Color.WHITE);
            display.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
            display.setGlowColor(glowColor);
            display.repaint(); // Repaint display to update glow
        }

        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]") || command.equals(".")) {
            String currentText = display.getText();
            if (isOperatorClicked || currentText.equals("Error")) {
                display.setText("");
                currentText = "";
                isOperatorClicked = false;
            }
            if (command.equals(".") && currentText.contains(".")) {
                // Prevent multiple decimals
                return;
            }
            display.setText(display.getText() + command);
        } else if (command.matches("[+\\-*/^]")) {
            Double value = safeParseDouble(display.getText());
            if (value != null) {
                firstNumber = value;
                operator = command;
                display.setText("");
                display.setPlaceholder(command);
                isOperatorClicked = true;
            }
        } else if (command.equals("=")) {
            if (!operator.isEmpty()) {
                Double secondNumber = safeParseDouble(display.getText());
                if (secondNumber != null) {
                    double result = 0;
                    switch (operator) {
                        case "+":
                            result = firstNumber + secondNumber;
                            break;
                        case "-":
                            result = firstNumber - secondNumber;
                            break;
                        case "*":
                            result = firstNumber * secondNumber;
                            break;
                        case "/":
                            if (secondNumber != 0) {
                                result = firstNumber / secondNumber;
                            } else {
                                display.setText("Error");
                                display.setPlaceholder("");
                                return;
                            }
                            break;
                        case "^":
                            result = Math.pow(firstNumber, secondNumber);
                            break;
                    }
                    display.setText(String.valueOf(result));
                    display.setPlaceholder("");
                    operator = "";
                    isOperatorClicked = false;
                }
            }
        } else if (command.equals("sin")) {
            Double value = safeParseDouble(display.getText());
            if (value != null) {
                display.setText(String.valueOf(Math.sin(Math.toRadians(value))));
            }
        } else if (command.equals("cos")) {
            Double value = safeParseDouble(display.getText());
            if (value != null) {
                display.setText(String.valueOf(Math.cos(Math.toRadians(value))));
            }
        } else if (command.equals("tan")) {
            Double value = safeParseDouble(display.getText());
            if (value != null) {
                display.setText(String.valueOf(Math.tan(Math.toRadians(value))));
            }
        } else if (command.equals("log")) {
            Double value = safeParseDouble(display.getText());
            if (value != null && value > 0) {
                display.setText(String.valueOf(Math.log10(value)));
            } else {
                display.setText("Error");
            }
        } else if (command.equals("ln")) {
            Double value = safeParseDouble(display.getText());
            if (value != null && value > 0) {
                display.setText(String.valueOf(Math.log(value)));
            } else {
                display.setText("Error");
            }
        } else if (command.equals("sqrt")) {
            Double value = safeParseDouble(display.getText());
            if (value != null && value >= 0) {
                display.setText(String.valueOf(Math.sqrt(value)));
            } else {
                display.setText("Error");
            }
        } else if (command.equals("pi")) {
            display.setText(String.valueOf(Math.PI));
        } else if (command.equals("e")) {
            display.setText(String.valueOf(Math.E));
        } else if (command.equals("C")) {
            display.setText("");
            display.setPlaceholder("");
            operator = "";
            firstNumber = 0;
            isOperatorClicked = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new calculator());
    }
}

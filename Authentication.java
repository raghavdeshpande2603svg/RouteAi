import java.awt.*;
import java.awt.event.*;

public class Authentication extends Frame implements ActionListener {

    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final TextField idField;
    private final TextField passwordField;
    private final Label statusLabel;

    public Authentication() {
        super("Campus Navigation System - Login");
        setSize(420, 280);
        setResizable(false);
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(212, 208, 200));

        Label title = new Label("CAMPUS NAVIGATION SYSTEM", Label.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        Panel form = new Panel(new GridLayout(3, 2, 8, 12));
        form.setBackground(new Color(212, 208, 200));
        form.add(new Label("Administrator ID:"));
        idField = new TextField(20);
        form.add(idField);
        form.add(new Label("Password:"));
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        passwordField.addActionListener(this);
        form.add(passwordField);
        form.add(new Label(""));
        statusLabel = new Label("Enter your administrator credentials.");
        form.add(statusLabel);
        add(form, BorderLayout.CENTER);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.setBackground(new Color(212, 208, 200));
        Button loginButton = new Button("Log In");
        loginButton.addActionListener(this);
        buttons.add(loginButton);
        Button exitButton = new Button("Exit");
        exitButton.addActionListener(e -> dispose());
        buttons.add(exitButton);
        add(buttons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent event) {
        if (ADMIN_ID.equals(idField.getText().trim())
                && ADMIN_PASSWORD.equals(passwordField.getText())) {
            dispose();
            new CampusMap();
        } else {
            statusLabel.setText("Invalid ID or password. Please try again.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    public static void main(String[] args) {
        new Authentication().setVisible(true);
    }
}

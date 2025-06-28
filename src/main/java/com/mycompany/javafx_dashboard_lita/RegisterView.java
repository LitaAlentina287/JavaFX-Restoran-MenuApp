package com.mycompany.javafx_dashboard_lita;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterView {
    private Stage primaryStage;
    private UserOperations userOperations;

    public RegisterView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ADD8E6, #E0FFFF);");

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setMaxWidth(400);
        container.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15px;" +
                "-fx-border-radius: 15px;"
        );
        container.setEffect(new DropShadow(10, Color.GRAY));

        Label iconLabel = new Label("\uD83D\uDC64"); // Ikon user
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        iconLabel.setTextFill(Color.DARKBLUE);

        Label titleLabel = new Label("Register");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.DARKBLUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-pref-width: 300px; -fx-padding: 10px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 300px; -fx-padding: 10px;");

        Button registerButton = new Button("Register");
        registerButton.setStyle(
                "-fx-background-color: #00008B;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 10px;"
        );
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(
                "-fx-background-color: #0000CD;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 10px;"
        ));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(
                "-fx-background-color: #00008B;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 10px;"
        ));

        Label loginLink = new Label("Sudah punya akun? Login di sini.");
        loginLink.setStyle("-fx-text-fill: #00008B; -fx-underline: true; -fx-cursor: hand;");
        loginLink.setOnMouseClicked(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = "User"; // Hardcoded role ke User

            try {
                userOperations = new UserOperations();
                boolean success = userOperations.registerUser(username, password, role);
                if (success) {
                    showInfo("Registrasi berhasil! Silakan login.");
                    LoginView loginView = new LoginView(primaryStage);
                    primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
                } else {
                    showError("Registrasi gagal! Username sudah digunakan.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RegisterView.class.getName()).log(Level.SEVERE, null, ex);
                showError("Terjadi kesalahan saat mengakses database.");
            }
        });

        container.getChildren().addAll(iconLabel, titleLabel, usernameField, passwordField, registerButton, loginLink);
        root.setCenter(container);
        return root;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registrasi Gagal");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrasi Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

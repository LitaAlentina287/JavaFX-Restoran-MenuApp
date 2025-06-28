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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;
    private UserOperations userOperations;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ADD8E6, #E0FFFF);");

        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30));

        // Judul utama paling atas
        Label appTitle = new Label("Aplikasi Menu Restoran");
        appTitle.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 26));
        appTitle.setTextFill(Color.DARKBLUE);
        appTitle.setEffect(new DropShadow(3, Color.LIGHTGRAY));
        appTitle.setStyle("-fx-letter-spacing: 1px;");

        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(30));
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15px;" +
                "-fx-border-radius: 15px;"
        );
        formContainer.setEffect(new DropShadow(10, Color.GRAY));

        Label logoLabel = new Label("\uD83D\uDD11");  // emoji kunci
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        logoLabel.setTextFill(Color.DARKBLUE);

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00008B;");

        Separator separator = new Separator();

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-pref-width: 300px; -fx-padding: 10px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 300px; -fx-padding: 10px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle(
                "-fx-background-color: #00008B;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 10px;"
        );
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #0000CD; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 10px;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #00008B; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 10px;"));

        Label registerLink = new Label("Belum punya akun? Register di sini.");
        registerLink.setStyle("-fx-text-fill: #00008B; -fx-underline: true; -fx-cursor: hand;");
        registerLink.setOnMouseClicked(event -> {
            RegisterView registerView = new RegisterView(primaryStage);
            Scene registerScene = new Scene(registerView.getView(), 800, 600);
            primaryStage.setScene(registerScene);
        });

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                userOperations = new UserOperations();
            } catch (SQLException ex) {
                Logger.getLogger(LoginView.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (userOperations.loginUser(username, password)) {
                DashboardView dashboardView = new DashboardView(primaryStage, username);
                Scene dashboardScene = new Scene(dashboardView.getView(), 800, 600);
                primaryStage.setScene(dashboardScene);
            } else {
                showError("Login gagal! Periksa username dan password Anda.");
            }
        });

        // Tambahkan ke form login
        formContainer.getChildren().addAll(logoLabel, titleLabel, separator, usernameField, passwordField, loginButton, registerLink);

        // Tambahkan judul dan form ke tampilan utama
        mainContainer.getChildren().addAll(appTitle, formContainer);
        root.setCenter(mainContainer);

        return root;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package com.mycompany.javafx_dashboard_lita;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class DashboardView {
    private Stage primaryStage;
    private UserOperations userOperations;
    private TodoOperations todoOperations;
    private String username;

    public DashboardView(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ADD8E6, #E0FFFF);");

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setMaxWidth(600);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        container.setEffect(new DropShadow(10, Color.GRAY));

        String userRole = "";
        User user = null;
        try {
            userOperations = new UserOperations();
            user = userOperations.getProfile(username);
            if (user != null) {
                userRole = user.getRole();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Label welcomeLabel = new Label("👋 Selamat Datang, " + username + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.DARKBLUE);

        Label roleLabel = new Label("Role: " + userRole);
        roleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        container.getChildren().addAll(welcomeLabel, roleLabel);

        if ("User".equalsIgnoreCase(userRole)) {
            Label infoLabel = new Label("Berikut adalah daftar menu dan harga dari Admin:");
            infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

            try {
                todoOperations = new TodoOperations();
                ObservableList<Todo> todos = FXCollections.observableArrayList(todoOperations.getTodos());

                TableView<Todo> tableView = new TableView<>();
                tableView.setItems(todos);
                tableView.setMaxHeight(300);

                TableColumn<Todo, String> titleColumn = new TableColumn<>("Nama Menu");
                titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());

                TableColumn<Todo, String> descriptionColumn = new TableColumn<>("Harga");
                descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());

                TableColumn<Todo, String> statusColumn = new TableColumn<>("Status");
                statusColumn.setCellValueFactory(data -> {
                    boolean isCompleted = data.getValue().isIsCompleted();
                    return new ReadOnlyStringWrapper(isCompleted ? "Tidak Tersedia" : "Tersedia");
                });

                tableView.getColumns().addAll(titleColumn, descriptionColumn, statusColumn);
                tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                tableView.setStyle("-fx-background-color: white; -fx-font-size: 14px;");

                container.getChildren().addAll(infoLabel, tableView);
            } catch (SQLException ex) {
                Logger.getLogger(DashboardView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Admin".equalsIgnoreCase(userRole)) {
            // Deskripsi admin dengan wrap text dan center
            Label infoLabel = new Label("Sebagai Admin, Anda memiliki akses penuh untuk mengelola data menu. Anda dapat menambah, mengedit, menghapus, serta mengubah status ketersediaan menu.");
            infoLabel.setWrapText(true);
            infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            infoLabel.setAlignment(Pos.CENTER);
            infoLabel.setMaxWidth(480);
            infoLabel.setStyle("-fx-text-alignment: center;");

            // Bungkus dalam StackPane untuk efek background
            StackPane infoPane = new StackPane(infoLabel);
            infoPane.setPadding(new Insets(10));
            infoPane.setMaxWidth(500);
            infoPane.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 10;");
            infoPane.setEffect(new DropShadow(4, Color.LIGHTGRAY));

            Button todoButton = new Button("Kelola Menu");
            todoButton.setStyle(
                    "-fx-background-color: #00008B;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 8px 16px;" +
                    "-fx-background-radius: 10px;"
            );

            todoButton.setOnAction(e -> {
                TodoView todoView = new TodoView(primaryStage, username);
                primaryStage.setScene(new Scene(todoView.getView(), 800, 600));
            });

            container.getChildren().addAll(infoPane, todoButton);
        }

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: #00008B;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 10px;"
        );

        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
        });

        container.getChildren().add(logoutButton);
        root.setCenter(container);

        return root;
    }
}

package com.mycompany.javafx_dashboard_lita;

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class TodoView {
    private TodoOperations todoOperations;
    private TableView<Todo> tableView;
    private ObservableList<Todo> todoList;
    private Stage primaryStage;
    private String username;
    private String userRole;

    public TodoView(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;

        try {
            todoOperations = new TodoOperations();
            todoList = FXCollections.observableArrayList(todoOperations.getTodos());

            UserOperations userOperations = new UserOperations();
            User user = userOperations.getProfile(username);
            if (user != null) {
                userRole = user.getRole();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading todos: " + e.getMessage());
        }
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ADD8E6, #E0FFFF);");

        VBox menu = new VBox(20);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        menu.setEffect(new DropShadow(10, Color.GRAY));

        Label titleLabel = new Label(" Menu Admin");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button dashboardButton = new Button("🏠 Dashboard");
        dashboardButton.setStyle(buttonStyle());
        dashboardButton.setOnAction(e -> {
            DashboardView dashboardView = new DashboardView(primaryStage, username);
            primaryStage.setScene(new Scene(dashboardView.getView(), 800, 600));
        });

        Button logoutButton = new Button("🔓 Logout");
        logoutButton.setStyle(buttonStyle());
        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
        });

        menu.getChildren().addAll(titleLabel, dashboardButton, logoutButton);
        root.setLeft(menu);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        tableView = new TableView<>();
        tableView.setItems(todoList);
        tableView.setStyle("-fx-font-size: 14px;");

        TableColumn<Todo, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());

        TableColumn<Todo, String> titleColumn = new TableColumn<>("Nama Menu");
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Todo, String> descriptionColumn = new TableColumn<>("Harga");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());

        TableColumn<Todo, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> {
            boolean isCompleted = data.getValue().isCompletedProperty().get();
            return new ReadOnlyStringWrapper(isCompleted ? "Tidak Tersedia" : "Tersedia");
        });

        tableView.getColumns().addAll(idColumn, titleColumn, descriptionColumn, statusColumn);

        if ("Admin".equalsIgnoreCase(userRole)) {
            TableColumn<Todo, Void> actionColumn = new TableColumn<>("Actions");
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("✏️ Edit");
                private final Button deleteButton = new Button("🗑 Hapus");
                private final Button toggleStatusButton = new Button("🔄 Ubah Status");

                {
                    editButton.setStyle(buttonMiniStyle());
                    deleteButton.setStyle(buttonMiniStyle());
                    toggleStatusButton.setStyle(buttonMiniStyle());
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Todo selected = getTableView().getItems().get(getIndex());

                        editButton.setOnAction(event -> showTodoModal(selected));
                        deleteButton.setOnAction(event -> {
                            todoOperations.deleteTodo(selected.getId());
                            refreshTable();
                            showSuccess("Menu berhasil dihapus.");
                        });
                        toggleStatusButton.setOnAction(event -> {
                            boolean newStatus = !selected.isIsCompleted();
                            todoOperations.toggleStatus(selected.getId(), newStatus);
                            selected.setCompleted(newStatus);
                            tableView.refresh();
                            showSuccess("Status berhasil diubah.");
                        });

                        HBox actions = new HBox(5, editButton, deleteButton, toggleStatusButton);
                        setGraphic(actions);
                    }
                }
            });
            tableView.getColumns().add(actionColumn);

            Button addButton = new Button("➕ Tambah Menu");
            addButton.setStyle(buttonStyle());
            addButton.setOnAction(e -> showTodoModal(null));
            content.getChildren().add(addButton);
        }

        content.getChildren().add(tableView);
        root.setCenter(content);
        return root;
    }

    private void showTodoModal(Todo todo) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle(todo == null ? "Tambah Menu" : "Edit Menu");

        TextField titleField = new TextField(todo == null ? "" : todo.getTitle());
        TextField descriptionField = new TextField(todo == null ? "" : todo.getDescription());

        Button saveButton = new Button(todo == null ? "➕ Tambah" : "💾 Simpan");
        saveButton.setStyle(buttonStyle());
        saveButton.setOnAction(e -> {
            if (todo == null) {
                todoOperations.addTodo(new Todo(0, titleField.getText(), descriptionField.getText(), false, null));
                showSuccess("Menu berhasil ditambahkan!");
            } else {
                todoOperations.updateTodo(todo.getId(), titleField.getText(), descriptionField.getText());
                showSuccess("Menu berhasil diperbarui!");
            }
            refreshTable();
            modalStage.close();
        });

        VBox modalContent = new VBox(10);
        modalContent.setPadding(new Insets(10));
        modalContent.getChildren().addAll(
                new Label("Nama Menu:"), titleField,
                new Label("Harga:"), descriptionField,
                saveButton
        );

        Scene modalScene = new Scene(modalContent);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    private void refreshTable() {
        todoList.setAll(todoOperations.getTodos());
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String buttonStyle() {
        return "-fx-background-color: #00008B; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 10px;";
    }

    private String buttonMiniStyle() {
        return "-fx-background-color: #4169E1; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4px 8px; -fx-background-radius: 6px;";
    }
}

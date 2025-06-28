# 🍽️ Aplikasi Menu Restoran JavaFX

Aplikasi Menu Restoran berbasis JavaFX yang memungkinkan pengguna (user) untuk melihat daftar menu dan admin untuk mengelola data menu (tambah, edit, hapus, ubah status tersedia/tidak tersedia).

## 🛠 Teknologi yang Digunakan

- **JavaFX** (UI)
- **NetBeans 23**
- **JDK 21**
- **MySQL** (via Laragon)
- **Maven Project**

## 👥 Role Pengguna

- **Admin**  
  Username: `admin`  
  Password: `123`  
  Bisa mengelola data menu.

- **User**  
  Mendaftar melalui fitur *Register*.  
  Hanya dapat melihat daftar menu.

## 📦 Fitur Utama

- Login untuk semua pengguna
- Register hanya untuk User
- Akun Admin sudah dibuat sebelumnya secara manual (misalnya: Admin / 123), tidak perlu register

- Admin:
  - Tambah menu baru
  - Edit data menu
  - Hapus menu
  - Ubah status tersedia/tidak tersedia
- User:
  - Melihat daftar menu
  - Melihat status ketersediaan

## 💽 Struktur Database

```sql
CREATE DATABASE todo_system;

USE todo_system;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS todos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 📽️ Demo Aplikasi
Tonton demo aplikasi di YouTube:  
🔗 (https://youtu.be/klz2-AVIM1Q?si=6h_i4sEySB_GH3p2)


---


## 🧩 Kode Program Utama

| File                | Peran                                                              |
| ------------------- | ------------------------------------------------------------------ |
| App.java            | Entry point, menampilkan `LoginView`                               |
| DatabaseConnection  | Koneksi database global                                            |
| User.java           | Model data pengguna                                                |
| Todo.java           | Model data menu                                                    |
| UserOperations.java | Operasi database terkait user (login/register/profile/update)      |
| TodoOperations.java | Operasi database untuk menu (CRUD dan toggle status)               |
| LoginView\.java     | UI login dan navigasi ke dashboard atau register                   |
| RegisterView\.java  | UI pendaftaran user baru                                           |
| DashboardView\.java | UI utama berdasarkan role (user/admin)                             |
| TodoView\.java      | UI lengkap CRUD menu untuk admin dengan interaksi pengguna (modal) |


---

## 1. 📄 App.java
App.java adalah titik masuk utama (main class) dari aplikasi JavaFX. File ini:

- Menjalankan aplikasi dengan launch(args)
- Membuat window utama (Stage)
- Menampilkan tampilan login pertama kali melalui LoginView

```java
package com.mycompany.javafx_dashboard_lita;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.layout.BorderPane;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

   @Override
    public void start(Stage primaryStage) {
        try {
            // Inisialisasi Root Layout
            BorderPane root = new BorderPane();

            // Tampilkan Halaman LoginView terlebih dahulu
            LoginView loginView = new LoginView(primaryStage);
            root.setCenter(loginView.getView());

            // Buat Scene
            Scene scene = new Scene(root, 800, 600);

            // Atur Stage
            primaryStage.setTitle("Todo App");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

---

## 2. 📄 DatabaseConnection.java - Koneksi database global  
DatabaseConnection.java adalah class utilitas untuk mengelola koneksi ke database MySQL. File ini:

- Menghubungkan aplikasi ke database todo_system
- Menggunakan JDBC driver MySQL (com.mysql.cj.jdbc.Driver)
- Menyediakan method getConnection() untuk mengambil koneksi
- Menyediakan method closeConnection() untuk menutup koneksi
- Mencetak status koneksi sukses atau gagal ke konsol

```java
package com.mycompany.javafx_dashboard_lita;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/todo_system"; // Ganti 'your_database_name' dengan nama database Anda
    private static final String USER = "root"; // Ganti 'your_username' dengan username database Anda
    private static final String PASSWORD = ""; // Ganti 'your_password' dengan password database Anda

    private static Connection connection;

    static {
        try {
            // Memuat driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Membuat koneksi ke database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection.");
                e.printStackTrace();
            }
        }
    }
}
```

---

## 3. 📄 User.java - Model data pengguna  
User.java adalah class model yang merepresentasikan data pengguna (user) dalam aplikasi. File ini:

- Menyimpan data pengguna berupa id, username, password, dan role
- Menggunakan JavaFX Property (IntegerProperty, StringProperty) untuk mendukung data binding di UI
- Menyediakan getter, setter, dan property accessor untuk setiap atribut
- Digunakan dalam operasi login, register, dan pengelolaan role (Admin/User)

```java
package com.mycompany.javafx_dashboard_lita;

import javafx.beans.property.*;

public class User {
    private IntegerProperty id;
    private StringProperty username;
    private StringProperty password;
    private StringProperty role;

    public User(int id, String username, String password, String role) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
    }

    // Getter and Property for 'id'
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // Getter and Property for 'username'
    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    // Getter and Property for 'password'
    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    // Getter and Property for 'role'
    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }
}
```

---

## 4. 📄 Todo.java – Model data menu
Todo.java adalah class model yang merepresentasikan satu item menu makanan/minuman di aplikasi restoran. File ini berfungsi sebagai jembatan antara data di database dengan tampilan antarmuka (UI).

- Menyimpan data id, judul menu (title), deskripsi/harga (description), status ketersediaan (isCompleted), dan waktu dibuat (createdAt)
- Menggunakan JavaFX Property seperti StringProperty, IntegerProperty, BooleanProperty untuk mendukung fitur data binding dengan UI seperti TableView
- Menyediakan getter dan property method agar data bisa diakses dan diikat langsung ke elemen antarmuka

```java
package com.mycompany.javafx_dashboard_lita;

import javafx.beans.property.*;

public class Todo {
    private IntegerProperty id;
    private StringProperty title;
    private StringProperty description;
    private BooleanProperty isCompleted;
    private StringProperty createdAt;

    public Todo(int id, String title, String description, boolean isCompleted, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.isCompleted = new SimpleBooleanProperty(isCompleted);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public boolean isIsCompleted() {
        return isCompleted.get();
    }

    public BooleanProperty isCompletedProperty() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted.set(completed);
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    public StringProperty createdAtProperty() {
        return createdAt;
    }
}
```

---

## 5. 📄 UserOperations.java
UserOperations.java adalah class yang menangani semua operasi database terkait pengguna (user dan admin). File ini:

- Menghubungkan ke database menggunakan DatabaseConnection
- Memastikan akun admin default (admin / 123) tersedia secara otomatis
- Menyediakan fitur register user baru ke database
- Menyediakan fitur login dengan autentikasi username & password
- Menyediakan fitur logout sederhana (dengan log)
- Mendapatkan data profil user berdasarkan username

```java
package com.mycompany.javafx_dashboard_lita;

import java.sql.*;

public class UserOperations {
    private Connection connection;

    public UserOperations() throws SQLException {
        connection = DatabaseConnection.getConnection();
        // Tidak perlu lagi menambahkan admin default
    }

    // Register User
    public boolean registerUser(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            System.out.println("User registered successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("Register failed: " + e.getMessage());
            return false;
        }
    }

    // Login User
    public boolean loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Logout User
    public void logoutUser() {
        System.out.println("User logged out successfully!");
    }

    // Get User Profile
    public User getProfile(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update Password
    public void updatePassword(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Failed to update password. User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 6. 📄 TodoOperations.java – Operasi Menu Restoran (CRUD)
TodoOperations.java adalah class yang menangani semua operasi database untuk tabel todos, yang mewakili data menu makanan/minuman di aplikasi restoran ini.

File ini:

- Menyimpan menu baru ke database
- Mengambil seluruh data menu dari database
- Memperbarui judul/deskripsi menu
- Menghapus menu berdasarkan ID
- Mengubah status ketersediaan menu (Tersedia / Tidak Tersedia)

File ini bekerja sama dengan class Todo, dan menggunakan koneksi dari DatabaseConnection.java.

```java
package com.mycompany.javafx_dashboard_lita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoOperations {
    private Connection connection;

    public TodoOperations() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Create
    public void addTodo(Todo todo) {
        String query = "INSERT INTO todos (title, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.executeUpdate();
            System.out.println("To-Do added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read
    public List<Todo> getTodos() {
        List<Todo> todos = new ArrayList<>();
        String query = "SELECT * FROM todos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                todos.add(new Todo(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("is_completed"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todos;
    }

    // Update
    public void updateTodo(int id, String newTitle, String newDescription) {
        String query = "UPDATE todos SET title = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newTitle);
            stmt.setString(2, newDescription);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            System.out.println("To-Do updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteTodo(int id) {
        String query = "DELETE FROM todos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("To-Do deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Toggle status
    public void toggleStatus(int id, boolean newStatus) {
        String query = "UPDATE todos SET is_completed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("To-Do status updated to: " + (newStatus ? "Tidak Tersedia" : "Tersedia"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 7. 📄 LoginView.java – Tampilan Form Login

`LoginView.java` bertanggung jawab untuk menampilkan tampilan **login pengguna** di awal aplikasi.

#### Fungsi Utama:

- Menampilkan **judul aplikasi** di bagian atas.
- Membuat **form login** yang terdiri dari:
  - Input **username** dan **password**
  - Tombol **Login**
  - Tautan ke halaman Register (hanya untuk User)
- Menyediakan **desain UI modern**, meliputi:
  - Efek **shadow**
  - Warna **gradasi biru muda**
  - Tata letak komponen UI yang rapi
- Melakukan **autentikasi login**:
  - ✅ Jika berhasil login → ditampilkan halaman `DashboardView`
  - ❌ Jika gagal login → muncul pesan kesalahan (popup alert)

#### Catatan:

- Akun **Admin** sudah pernah dibuat di database secara manual (contoh: Admin / 123).
- Link **"Register"** hanya ditujukan untuk **User baru**, bukan Admin.

```java
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
```

---

## 8. 📄 RegisterView.java - Form Registrasi User
RegisterView.java adalah tampilan UI yang digunakan untuk pendaftaran akun baru khusus pengguna dengan role User. File ini:

- Menampilkan form registrasi (username & password)
- Memastikan role yang didaftarkan adalah "User" (bukan admin)
- Menyimpan data user ke database melalui UserOperations
- Mengarahkan user kembali ke halaman Login setelah berhasil registrasi
- Menampilkan notifikasi jika registrasi gagal atau berhasil

```java
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
```

---

## 9. 📄 `DashboardView.java` – Tampilan Beranda Utama

`DashboardView.java` adalah tampilan utama yang muncul setelah pengguna berhasil login, baik sebagai Admin maupun User.

#### ✅ Fungsi Utama:

- Menampilkan **sapaan selamat datang** dan **peran pengguna (role)**.
- Menyesuaikan isi tampilan berdasarkan peran:
  - 👤 **User**:
    - Menampilkan daftar menu yang tersedia dan harganya (dalam bentuk tabel).
  - 👑 **Admin**:
    - Menampilkan deskripsi peran Admin.
    - Tombol navigasi ke halaman pengelolaan menu.
- Menyediakan tombol **Logout** untuk kembali ke halaman login.
- Mengatur layout dan desain tampilan dengan efek bayangan (shadow) dan styling modern.

#### 🧩 Keterkaitan:
- Menggunakan `UserOperations` untuk mengambil data profil pengguna dari database.
- Menggunakan `TodoOperations` untuk mengambil data daftar menu dari database.

```java
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
```

---

## 10. 📄 `TodoView.java` - Halaman Manajemen Menu (Admin)

`TodoView.java` adalah tampilan utama **khusus untuk Admin** yang digunakan untuk **mengelola daftar menu** makanan/minuman. File ini:

- Menampilkan semua data menu dari database dalam bentuk tabel
- Menyediakan tombol-tombol aksi untuk Admin:  
  - ➕ Tambah Menu  
  - ✏️ Edit Menu  
  - 🗑 Hapus Menu  
  - 🔄 Ubah Status (Tersedia / Tidak Tersedia)  
- Terhubung dengan database melalui `TodoOperations`  
- Hanya pengguna dengan role `Admin` yang bisa melihat & mengelola menu

```java
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
```




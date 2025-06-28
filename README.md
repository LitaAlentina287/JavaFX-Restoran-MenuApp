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
  Password: `admin123`  
  Bisa mengelola data menu.

- **User**  
  Mendaftar melalui fitur *Register*.  
  Hanya dapat melihat daftar menu.

## 📦 Fitur Utama

- Login dan Register (khusus user)
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
- Memastikan akun admin default (admin / admin123) tersedia secara otomatis
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
        ensureDefaultAdmin(); // Tambahkan pengecekan admin default saat objek dibuat
    }

    // Menambahkan akun Admin default jika belum ada
    private void ensureDefaultAdmin() {
        String defaultAdminUsername = "admin";
        String defaultAdminPassword = "admin123";
        String defaultAdminRole = "Admin";

        try {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, defaultAdminUsername);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, defaultAdminUsername);
                insertStmt.setString(2, defaultAdminPassword);
                insertStmt.setString(3, defaultAdminRole);
                insertStmt.executeUpdate();
                System.out.println("Admin default berhasil dibuat (admin / admin123).");
            } else {
                System.out.println("Admin default sudah tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal memastikan admin default: " + e.getMessage());
        }
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

6. 📄 TodoOperations.java – Operasi Menu Restoran (CRUD)
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

### 📄 LoginView.java – Tampilan Form Login

`LoginView.java` bertanggung jawab untuk menampilkan tampilan **login pengguna** di awal aplikasi.

#### ✅ Fungsi Utama:

- Menampilkan **judul aplikasi** di bagian atas.
- Membuat **form login** yang terdiri dari:
  - Input **username** dan **password**
  - Tombol **Login**
  - Link ke halaman **Register** (khusus untuk User)
- Menyediakan **desain UI modern**, meliputi:
  - Efek **shadow**
  - Warna **gradasi biru muda**
  - Tata letak komponen UI yang rapi
- Melakukan **autentikasi login**:
  - ✅ Jika berhasil login → ditampilkan halaman `DashboardView`
  - ❌ Jika gagal login → muncul pesan kesalahan (popup alert)

#### 🧠 Catatan:

- Akun **Admin** tidak perlu register karena **otomatis dibuat** melalui `UserOperations`.
- Link **"Register"** hanya ditujukan untuk **User baru**, bukan Admin.





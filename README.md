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

## 1. 📄 App.java - Fungsi Utama
App.java adalah titik masuk utama (main class) dari aplikasi JavaFX kamu. File ini:

✅ Menjalankan aplikasi dengan launch(args)
✅ Membuat window utama (Stage)
✅ Menampilkan tampilan login pertama kali melalui LoginView

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



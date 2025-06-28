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

### 📁 File: App.java
```java

# Система управління учбовим процесом — Проєкт на Java з DAO

---

## Файли проєкту з поясненнями

### 1. `Course.java` — модель курсу

```java
package com.example.model;

import java.util.UUID;

/**
 * Модель курсу учбового процесу.
 * Зберігає дані курсу: унікальний ідентифікатор, назву, опис, а також id викладача і групи.
 */
public class Course {
    private UUID id;
    private String name;
    private String description;
    private UUID teacherId;
    private UUID groupId;

    public Course(UUID id, String name, String description, UUID teacherId, UUID groupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
        this.groupId = groupId;
    }

    // Геттери та сеттери...
}
```

*Пояснення:* Клас просто зберігає інформацію про курс. UUID використовується для унікальної ідентифікації.

---

### 2. `DatabaseConnection.java` — утиліта підключення

```java
package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас для отримання з'єднання з базою даних MySQL.
 * Забезпечує єдине повторно використовуване підключення.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/course?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Замінити на свій логін
    private static final String PASSWORD = "your_password_here"; // Замінити на свій пароль

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
```

*Пояснення:* Забезпечує створення одного підключення до бази, яке потім можна повторно використовувати.

---

### 3. `CourseDAO.java` — інтерфейс DAO

```java
package com.example.dao;

import com.example.model.Course;
import java.util.List;
import java.util.UUID;

/**
 * Інтерфейс для CRUD операцій з курсом.
 * Описує, які методи має реалізовувати DAO.
 */
public interface CourseDAO {
    void addCourse(Course course);
    Course getCourseById(UUID id);
    List<Course> getAllCourses();
    void updateCourse(Course course);
    void deleteCourse(UUID id);
}
```

*Пояснення:* Визначає основні методи для роботи з курсами у базі.

---

### 4. `CourseDAOImpl.java` — реалізація DAO

```java
package com.example.dao.impl;

import com.example.dao.CourseDAO;
import com.example.model.Course;
import com.example.util.DatabaseConnection;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Реалізація CourseDAO з використанням JDBC.
 * Виконує CRUD операції над таблицею course.
 */
public class CourseDAOImpl implements CourseDAO {

    private Connection conn;

    public CourseDAOImpl() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Конвертує UUID в байти для збереження у BINARY(16)
    private byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    // Конвертує байти з BINARY(16) назад в UUID
    private UUID toUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    @Override
    public void addCourse(Course course) {
        String sql = "INSERT INTO course (id, name, description, teacher_id, group_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, toBytes(course.getId()));
            stmt.setString(2, course.getName());
            stmt.setString(3, course.getDescription());
            if (course.getTeacherId() != null) {
                stmt.setBytes(4, toBytes(course.getTeacherId()));
            } else {
                stmt.setNull(4, Types.BINARY);
            }
            if (course.getGroupId() != null) {
                stmt.setBytes(5, toBytes(course.getGroupId()));
            } else {
                stmt.setNull(5, Types.BINARY);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Course getCourseById(UUID id) {
        String sql = "SELECT * FROM course WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, toBytes(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Course(
                    toUUID(rs.getBytes("id")),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBytes("teacher_id") != null ? toUUID(rs.getBytes("teacher_id")) : null,
                    rs.getBytes("group_id") != null ? toUUID(rs.getBytes("group_id")) : null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Course(
                    toUUID(rs.getBytes("id")),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBytes("teacher_id") != null ? toUUID(rs.getBytes("teacher_id")) : null,
                    rs.getBytes("group_id") != null ? toUUID(rs.getBytes("group_id")) : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateCourse(Course course) {
        String sql = "UPDATE course SET name = ?, description = ?, teacher_id = ?, group_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            if (course.getTeacherId() != null) {
                stmt.setBytes(3, toBytes(course.getTeacherId()));
            } else {
                stmt.setNull(3, Types.BINARY);
            }
            if (course.getGroupId() != null) {
                stmt.setBytes(4, toBytes(course.getGroupId()));
            } else {
                stmt.setNull(4, Types.BINARY);
            }
            stmt.setBytes(5, toBytes(course.getId()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCourse(UUID id) {
        String sql = "DELETE FROM course WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, toBytes(id));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

*Пояснення:* Реалізує всі CRUD операції, перетворюючи UUID в байти і назад, щоб зберігати їх компактно у базі.

---

### 5. `Main.java` — тестування

```java
package com.example;

import com.example.dao.CourseDAO;
import com.example.dao.impl.CourseDAOImpl;
import com.example.model.Course;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CourseDAO courseDAO = new CourseDAOImpl();

        // Створення нового курсу
        Course newCourse = new Course(UUID.randomUUID(), "Програмування Java", "Вивчення основ Java", null, null);

        // Додавання курсу
        courseDAO.addCourse(newCourse);
        System.out.println("Курс додано: " + newCourse.getName());

        // Отримання і вивід усіх курсів
        List<Course> courses = courseDAO.getAllCourses();
        System.out.println("Всі курси у базі:");
        for (Course c : courses) {
            System.out.println("- " + c.getName() + ": " + c.getDescription());
        }

        // Оновлення опису курсу
        newCourse.setDescription("Повний курс з Java");
        courseDAO.updateCourse(newCourse);
        System.out.println("Опис курсу оновлено.");

        // Перевірка оновлення
        Course updatedCourse = courseDAO.getCourseById(newCourse.getId());
        System.out.println("Оновлений опис: " + updatedCourse.getDescription());

        // Видалення курсу (розкоментувати при необхідності)
        // courseDAO.deleteCourse(newCourse.getId());
        // System.out.println("Курс видалено.");
    }
}
```

*Пояснення:* Демонструє приклад послідовних CRUD операцій із виводом у консоль.

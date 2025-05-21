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

/* *Пояснення:* Реалізує всі CRUD операції, перетворюючи UUID в байти і назад, щоб зберігати їх компактно у базі.
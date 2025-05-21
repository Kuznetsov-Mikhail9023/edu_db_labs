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
 

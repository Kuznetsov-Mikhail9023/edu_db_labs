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

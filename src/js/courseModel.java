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

/* *Пояснення:* Клас просто зберігає інформацію про курс. UUID використовується для унікальної ідентифікації.
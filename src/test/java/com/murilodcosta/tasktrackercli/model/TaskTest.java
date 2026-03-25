package com.murilodcosta.tasktrackercli.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @BeforeEach
    void resetLastId() throws NoSuchFieldException, IllegalAccessException {
        Field lastIdField = Task.class.getDeclaredField("lastId");
        lastIdField.setAccessible(true);
        lastIdField.set(null, 0);
    }

    @Test
    @DisplayName("Task(String) should set defaults and generate id")
    void constructorWithDescriptionShouldSetDefaults() {
        Task task = new Task("Write tests");

        assertEquals(1, task.getId());
        assertEquals("Write tests", task.getDescription());
        assertEquals(Status.TODO, task.getStatus());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
        assertFalse(task.getUpdatedAt().isBefore(task.getCreatedAt()));
    }

    @Test
    @DisplayName("Task(full args) should keep provided values")
    void fullConstructorShouldKeepProvidedValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 24, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 3, 24, 11, 30, 0);

        Task task = new Task(42, "Imported task", Status.IN_PROGRESS, createdAt, updatedAt);

        assertEquals(42, task.getId());
        assertEquals("Imported task", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(updatedAt, task.getUpdatedAt());
    }

    @Test
    @DisplayName("setDescription should change description and refresh updatedAt")
    void setDescriptionShouldUpdateUpdatedAt() {
        Task task = new Task("Old description");
        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        task.setDescription("New description");

        assertEquals("New description", task.getDescription());
        assertTrue(task.getUpdatedAt().isAfter(oldUpdatedAt) || task.getUpdatedAt().isEqual(oldUpdatedAt));
    }

    @Test
    @DisplayName("setStatus should change status and refresh updatedAt")
    void setStatusShouldUpdateUpdatedAt() {
        Task task = new Task("Status task");
        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        task.setStatus(Status.DONE);

        assertEquals(Status.DONE, task.getStatus());
        assertTrue(task.getUpdatedAt().isAfter(oldUpdatedAt) || task.getUpdatedAt().isEqual(oldUpdatedAt));
    }

    @Test
    @DisplayName("Task(String) should auto increment id across instances")
    void constructorShouldAutoIncrementId() {
        Task first = new Task("first");
        Task second = new Task("second");

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    @DisplayName("toJson should serialize task fields in expected format")
    void toJsonShouldSerializeInExpectedFormat() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 24, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 3, 24, 12, 15, 30);
        Task task = new Task(10, "JSON test", Status.DONE, createdAt, updatedAt);

        String expected = "{"
                + "\"id\":\"10\","
                + "\"description\":\"JSON test\","
                + "\"status\":\"Status{value='Done'}\","
                + "\"createdAt\":\"2026-03-24T10:00:00\","
                + "\"updatedAt\":\"2026-03-24T12:15:30\""
                + "}";

        assertEquals(expected, task.toJson());
    }
}


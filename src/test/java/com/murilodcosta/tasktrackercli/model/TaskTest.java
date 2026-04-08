package com.murilodcosta.tasktrackercli.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @BeforeEach
    void resetLastId() throws Exception {
        Field lastIdField = Task.class.getDeclaredField("lastId");
        lastIdField.setAccessible(true);
        lastIdField.set(null, 0);
    }

    @Test
    @DisplayName("Task(String) should set defaults and generate id")
    void constructorWithDescriptionShouldSetDefaults() throws Exception {
        Task task = new Task("Write tests");

        String description = (String) getField(task, "description");
        LocalDateTime createdAt = (LocalDateTime) getField(task, "createdAt");
        LocalDateTime updatedAt = (LocalDateTime) getField(task, "updatedAt");

        assertEquals(1, task.getId());
        assertEquals("Write tests", description);
        assertEquals(Status.TODO, task.getStatus());
        assertNotNull(createdAt);
        assertNotNull(updatedAt);
        assertFalse(updatedAt.isBefore(createdAt));
    }

    @Test
    @DisplayName("updateDescription should change description and refresh updatedAt")
    void updateDescriptionShouldUpdateFields() throws Exception {
        Task task = new Task("Old description");
        LocalDateTime oldUpdatedAt = (LocalDateTime) getField(task, "updatedAt");

        task.updateDescription("New description");

        String description = (String) getField(task, "description");
        LocalDateTime newUpdatedAt = (LocalDateTime) getField(task, "updatedAt");

        assertEquals("New description", description);
        assertTrue(newUpdatedAt.isAfter(oldUpdatedAt) || newUpdatedAt.isEqual(oldUpdatedAt));
    }

    @Test
    @DisplayName("markInProgress and markDone should update status")
    void markMethodsShouldUpdateStatus() {
        Task task = new Task("Status task");

        task.markInProgress();
        assertEquals(Status.IN_PROGRESS, task.getStatus());

        task.markDone();
        assertEquals(Status.DONE, task.getStatus());
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
    void toJsonShouldSerializeInExpectedFormat() throws Exception {
        Task task = new Task("JSON test");

        setField(task, "id", 10);
        setField(task, "status", Status.DONE);
        setField(task, "createdAt", LocalDateTime.of(2026, 3, 24, 10, 0, 0));
        setField(task, "updatedAt", LocalDateTime.of(2026, 3, 24, 12, 15, 30));

        String expected = "{" +
                "\"id\":\"10\", " +
                "\"description\":\"JSON test\", " +
                "\"status\":\"done\", " +
                "\"createdAt\":\"2026-03-24T10:00:00\", " +
                "\"updatedAt\":\"2026-03-24T12:15:30\"" +
                "}";

        assertEquals(expected, task.toJson());
    }

    @Test
    @DisplayName("toJson/fromJson should preserve escaped description content")
    void jsonShouldHandleEscapedDescription() {
        Task task = new Task("Texto com \"aspas\" e barra \\\\.");
        String json = task.toJson();

        Task restored = Task.fromJson(json);

        assertTrue(json.contains("\\\"aspas\\\""));
        assertTrue(restored.toJson().contains("Texto com \\\"aspas\\\" e barra \\\\\\\\."));
    }

    @Test
    @DisplayName("fromJson should create task with parsed values")
    void fromJsonShouldDeserialize() {
        String json = "{" +
                "\"id\":\"7\", " +
                "\"description\":\"Imported\", " +
                "\"status\":\"DONE\", " +
                "\"createdAt\":\"2026-03-24T10:00:00\", " +
                "\"updatedAt\":\"2026-03-24T12:15:30\"" +
                "}";

        Task task = Task.fromJson(json);

        assertEquals(7, task.getId());
        assertEquals(Status.DONE, task.getStatus());
        assertTrue(task.toJson().contains("\"description\":\"Imported\""));
    }

    private Object getField(Task task, String fieldName) throws Exception {
        Field field = Task.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(task);
    }

    private void setField(Task task, String fieldName, Object value) throws Exception {
        Field field = Task.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(task, value);
    }
}

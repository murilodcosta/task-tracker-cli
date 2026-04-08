package com.murilodcosta.tasktrackercli.manager;

import com.murilodcosta.tasktrackercli.model.Status;
import com.murilodcosta.tasktrackercli.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private static final Path TASKS_FILE = Path.of("tasks.json");
    private String tasksFileBackup;

    @BeforeEach
    void setUp() throws Exception {
        tasksFileBackup = Files.exists(TASKS_FILE) ? Files.readString(TASKS_FILE) : null;
        Files.deleteIfExists(TASKS_FILE);
        resetLastId();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tasksFileBackup == null) {
            Files.deleteIfExists(TASKS_FILE);
            return;
        }
        Files.writeString(TASKS_FILE, tasksFileBackup);
    }

    private void resetLastId() throws Exception {
        Field lastIdField = Task.class.getDeclaredField("lastId");
        lastIdField.setAccessible(true);
        lastIdField.set(null, 0);
    }

    @Test
    @DisplayName("addTask + findTask should store and return the new task")
    void addTaskAndFindTaskShouldWork() {
        TaskManager manager = new TaskManager();

        manager.addTask("Write TaskManager tests");

        Task task = manager.findTask("1").orElseThrow();
        assertEquals(1, task.getId());
        assertEquals(Status.TODO, task.getStatus());
        assertTrue(task.toJson().contains("\"description\":\"Write TaskManager tests\""));
    }

    @Test
    @DisplayName("updateTask should change the task description")
    void updateTaskShouldChangeDescription() {
        TaskManager manager = new TaskManager();
        manager.addTask("Old");

        manager.updateTask("1", "New description");

        Task task = manager.findTask("1").orElseThrow();
        assertTrue(task.toJson().contains("\"description\":\"New description\""));
    }

    @Test
    @DisplayName("deleteTask should remove task from list")
    void deleteTaskShouldRemoveTask() {
        TaskManager manager = new TaskManager();
        manager.addTask("Task to delete");

        manager.deleteTask("1");

        assertTrue(manager.findTask("1").isEmpty());
    }

    @Test
    @DisplayName("markInProgress and markDone should update status")
    void markMethodsShouldUpdateStatus() {
        TaskManager manager = new TaskManager();
        manager.addTask("Status flow");

        manager.markInProgress("1");
        assertEquals(Status.IN_PROGRESS, manager.findTask("1").orElseThrow().getStatus());

        manager.markDone("1");
        assertEquals(Status.DONE, manager.findTask("1").orElseThrow().getStatus());
    }

    @Test
    @DisplayName("saveTasks + constructor load should persist tasks in tasks.json")
    void saveAndLoadShouldPersistTasks() {
        TaskManager manager = new TaskManager();
        manager.addTask("Persisted task");
        manager.markDone("1");
        manager.saveTasks();

        TaskManager loadedManager = new TaskManager();

        Task loadedTask = loadedManager.findTask("1").orElseThrow();
        assertEquals(Status.DONE, loadedTask.getStatus());
        assertTrue(loadedTask.toJson().contains("\"description\":\"Persisted task\""));
    }

    @Test
    @DisplayName("methods that require existing id should throw when task is missing")
    void methodsShouldThrowForMissingId() {
        TaskManager manager = new TaskManager();

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> manager.updateTask("999", "x")),
                () -> assertThrows(IllegalArgumentException.class, () -> manager.deleteTask("999")),
                () -> assertThrows(IllegalArgumentException.class, () -> manager.markInProgress("999")),
                () -> assertThrows(IllegalArgumentException.class, () -> manager.markDone("999"))
        );
    }
}


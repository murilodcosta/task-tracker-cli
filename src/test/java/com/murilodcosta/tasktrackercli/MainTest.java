package com.murilodcosta.tasktrackercli;

import com.murilodcosta.tasktrackercli.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

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

    @Test
    @DisplayName("main add should create tasks.json and print success")
    void addShouldPersistAndPrintSuccess() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            Application.main(new String[]{"add", "Comprar leite"});
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(Files.exists(TASKS_FILE));
        String json = Files.readString(TASKS_FILE);
        assertTrue(json.contains("\"description\":\"Comprar leite\""));
        assertTrue(output.toString().contains("Task added successfully (ID: 1)"));
    }

    @Test
    @DisplayName("main should print usage for unknown command")
    void unknownCommandShouldPrintErrorAndUsage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(output));
        System.setErr(new PrintStream(error));

        try {
            Application.main(new String[]{"unknown-cmd"});
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        assertTrue(error.toString().contains("Error: Unknown command"));
        assertTrue(output.toString().contains("Usage:"));
    }

    @Test
    @DisplayName("main list with invalid status should print error")
    void listWithInvalidStatusShouldPrintError() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(output));
        System.setErr(new PrintStream(error));

        try {
            Application.main(new String[]{"list", "doing"});
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        assertTrue(error.toString().contains("Invalid status"));
        assertTrue(output.toString().contains("task-cli list todo|in-progress|done"));
    }

    private void resetLastId() throws Exception {
        Field lastIdField = Task.class.getDeclaredField("lastId");
        lastIdField.setAccessible(true);
        lastIdField.set(null, 0);
    }
}


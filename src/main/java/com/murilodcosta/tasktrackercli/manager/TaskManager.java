package com.murilodcosta.tasktrackercli.manager;

import com.murilodcosta.tasktrackercli.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TaskManager {
    private final Path FILE_PATH = Path.of("tasks.json");
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = loadTasks();
    }

    /* read tasks.json file */
    private List<Task> loadTasks(){
        List<Task> storedTasks = new ArrayList<>();

        if (!Files.exists(FILE_PATH)){
            return new ArrayList<>();
        }

        try {
            String jsonContent = Files.readString(FILE_PATH).trim();
            if (jsonContent.isEmpty() || jsonContent.equals("[]")) {
                return new ArrayList<>();
            }

            for (String taskJson : splitJsonArrayObjects(jsonContent)) {
                try {
                    storedTasks.add(Task.fromJson(taskJson));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid task entry: " + e.getMessage());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return storedTasks;
    }

    private List<String> splitJsonArrayObjects(String jsonArray) {
        String trimmed = jsonArray.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array in tasks file.");
        }

        List<String> objects = new ArrayList<>();
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;
        int objectStart = -1;

        for (int i = 0; i < trimmed.length(); i++) {
            char current = trimmed.charAt(i);

            if (!escaped && current == '\\') {
                escaped = true;
                continue;
            }

            if (!escaped && current == '"') {
                inString = !inString;
            }

            if (!inString) {
                if (current == '{') {
                    if (depth == 0) {
                        objectStart = i;
                    }
                    depth++;
                } else if (current == '}') {
                    depth--;
                    if (depth == 0 && objectStart >= 0) {
                        objects.add(trimmed.substring(objectStart, i + 1));
                        objectStart = -1;
                    }
                }
            }

            if (escaped) {
                escaped = false;
            }
        }

        if (depth != 0 || inString) {
            throw new IllegalArgumentException("Invalid JSON array structure in tasks file.");
        }

        return objects;
    }

    public void saveTasks(){
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n");
        for (int i = 0; i < tasks.size(); i++){
            jsonBuilder.append(tasks.get(i).toJson());
            if (i < tasks.size() - 1){
                jsonBuilder.append(",\n");
            }
        }
        jsonBuilder.append("\n]");

        try {
            Files.writeString(FILE_PATH, jsonBuilder.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addTask(String description){
        Task newTask = new Task(description);
        tasks.add(newTask);
        saveTasks();
        System.out.println("Task added successfully (ID: " + newTask.getId() + ")");
    }

    public void updateTask(String id, String newDescription){
        Task task = findTask(id).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found!"));
        task.updateDescription(newDescription);
        saveTasks();
    }

    public void deleteTask(String id){
        Task task = findTask(id).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found!"));
        tasks.remove(task);
        saveTasks();
    }

    public void markInProgress(String id){
        Task task = findTask(id).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found!"));
        task.markInProgress();
        saveTasks();
    }

    public void markDone(String id){
        Task task = findTask(id).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found!"));
        task.markDone();
        saveTasks();
    }

    public void listTasks(String type){
        String normalizedType = type == null ? "all" : type.strip().toLowerCase(Locale.ROOT);
        for (Task task : tasks){
            String statusKey = task.getStatus().toStorageValue();
            if (normalizedType.equals("all") || statusKey.equals(normalizedType)){
                System.out.println(task);
            }
        }
    }

    public Optional<Task> findTask(String id) {
        int parsedId = parseId(id);
        return tasks.stream().filter((task) -> task.getId() == parsedId).findFirst();
    }

    private int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID: " + id + ". ID must be a number.");
        }
    }
}

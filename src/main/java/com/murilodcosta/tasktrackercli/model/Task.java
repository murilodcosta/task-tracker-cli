package com.murilodcosta.tasktrackercli.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private static int lastId = 0;
    private int id;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Task(String description) {
        this.id = ++lastId;
        this.description = description;
        this.status = Status.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void markInProgress() {
        this.status = Status.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markDone() {
        this.status = Status.DONE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String toJson() {
        return "{" +
                    "\"id\":\"" + id + "\", " +
                    "\"description\":\"" + description.strip() + "\", " +
                    "\"status\":\"" + status.name() + "\", " +
                    "\"createdAt\":\"" + createdAt.format(formatter) + "\", " +
                    "\"updatedAt\":\"" + updatedAt.format(formatter) + "\"" +
                "}";
    }

    public static Task fromJson(String json) {
        String id = extractJsonValue(json, "id");
        String description = extractJsonValue(json, "description");
        String statusString = extractJsonValue(json, "status");
        String createdAtStr = extractJsonValue(json, "createdAt");
        String updatedAtStr = extractJsonValue(json, "updatedAt");

        Status status = Status.valueOf(statusString.toUpperCase().replace("-", "_").replace(" ", "_"));

        Task task = new Task(description);
        task.id = Integer.parseInt(id);
        task.status = status;
        task.createdAt = LocalDateTime.parse(createdAtStr, formatter);
        task.updatedAt = LocalDateTime.parse(updatedAtStr, formatter);

        if (task.id > lastId) {
            lastId = task.id;
        }

        return task;
    }

    private static String extractJsonValue(String json, String key) {
        String token = "\"" + key + "\":\"";
        int start = json.indexOf(token);
        if (start < 0) {
            throw new IllegalArgumentException("Invalid JSON. Missing key: " + key);
        }

        int valueStart = start + token.length();
        int valueEnd = json.indexOf("\"", valueStart);
        if (valueEnd < 0) {
            throw new IllegalArgumentException("Invalid JSON. Invalid value for key: " + key);
        }

        return json.substring(valueStart, valueEnd).strip();
    }

    @Override
    public String toString() {
        return "id: " + id + ", description: " + description.strip() + ", status: " + status.toString() +
                ", createdAt: " + createdAt.format(formatter) + ", updatedAt: " + updatedAt.format(formatter);
    }
}
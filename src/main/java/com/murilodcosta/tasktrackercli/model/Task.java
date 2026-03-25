package com.murilodcosta.tasktrackercli.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private final int id;
    private String description;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static int lastId = 0;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Task(String description) {
        this.description = description;

        this.id = ++lastId;
        this.status = Status.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(int id, String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String toJson() {
        return "{" +
                "\"id\":" + "\"" +this.id + "\"," +
                "\"description\":" + "\"" + this.description + "\"," +
                "\"status\":" + "\"" +this.status.toString() + "\"," +
                "\"createdAt\":" + "\"" + this.createdAt.format(formatter) + "\"," +
                "\"updatedAt\":" + "\"" + this.updatedAt.format(formatter) + "\"" +
                "}";
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

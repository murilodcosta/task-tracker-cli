package com.murilodcosta.tasktrackercli.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
                "\"description\":\"" + escapeJson(description.strip()) + "\", " +
                "\"status\":\"" + status.toStorageValue() + "\", " +
                "\"createdAt\":\"" + createdAt.format(formatter) + "\", " +
                "\"updatedAt\":\"" + updatedAt.format(formatter) + "\"" +
                "}";
    }

    public static Task fromJson(String json) {
        Map<String, String> values = parseJsonObject(json);

        String id = requireKey(values, "id");
        String description = requireKey(values, "description");
        String statusString = requireKey(values, "status");
        String createdAtStr = requireKey(values, "createdAt");
        String updatedAtStr = requireKey(values, "updatedAt");

        Task task = new Task(description);
        task.id = Integer.parseInt(id);
        task.status = Status.fromString(statusString);
        task.createdAt = LocalDateTime.parse(createdAtStr, formatter);
        task.updatedAt = LocalDateTime.parse(updatedAtStr, formatter);

        if (task.id > lastId) {
            lastId = task.id;
        }

        return task;
    }

    private static String requireKey(Map<String, String> values, String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Invalid JSON. Missing key: " + key);
        }
        return values.get(key).strip();
    }

    private static Map<String, String> parseJsonObject(String json) {
        String trimmed = json == null ? "" : json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON object: " + json);
        }

        Map<String, String> values = new HashMap<>();
        int i = 1;
        while (i < trimmed.length() - 1) {
            i = skipWhitespaceAndComma(trimmed, i);
            if (i >= trimmed.length() - 1) {
                break;
            }

            String key = readQuotedToken(trimmed, i);
            i += key.length() + 2;

            i = skipWhitespace(trimmed, i);
            if (i >= trimmed.length() || trimmed.charAt(i) != ':') {
                throw new IllegalArgumentException("Invalid JSON. Missing ':' after key: " + key);
            }
            i++;

            i = skipWhitespace(trimmed, i);
            String value = readQuotedToken(trimmed, i);
            i += value.length() + 2;

            values.put(key, unescapeJson(value));
        }

        return values;
    }

    private static int skipWhitespaceAndComma(String value, int i) {
        int cursor = skipWhitespace(value, i);
        if (cursor < value.length() && value.charAt(cursor) == ',') {
            return skipWhitespace(value, cursor + 1);
        }
        return cursor;
    }

    private static int skipWhitespace(String value, int i) {
        int cursor = i;
        while (cursor < value.length() && Character.isWhitespace(value.charAt(cursor))) {
            cursor++;
        }
        return cursor;
    }

    private static String readQuotedToken(String value, int start) {
        if (start >= value.length() || value.charAt(start) != '"') {
            throw new IllegalArgumentException("Invalid JSON. Expected quoted value at index: " + start);
        }

        StringBuilder token = new StringBuilder();
        boolean escaped = false;
        for (int i = start + 1; i < value.length(); i++) {
            char current = value.charAt(i);
            if (!escaped && current == '"') {
                return token.toString();
            }
            if (!escaped && current == '\\') {
                escaped = true;
                token.append(current);
                continue;
            }
            escaped = false;
            token.append(current);
        }

        throw new IllegalArgumentException("Invalid JSON. Unterminated quoted value.");
    }

    private static String escapeJson(String raw) {
        return raw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String raw) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < raw.length(); i++) {
            char current = raw.charAt(i);
            if (!escaped) {
                if (current == '\\') {
                    escaped = true;
                } else {
                    result.append(current);
                }
                continue;
            }

            switch (current) {
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case '"':
                    result.append('"');
                    break;
                case '\\':
                    result.append('\\');
                    break;
                default:
                    result.append(current);
                    break;
            }
            escaped = false;
        }

        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return "id: " + id + ", description: " + description.strip() + ", status: " + status +
                ", createdAt: " + createdAt.format(formatter) + ", updatedAt: " + updatedAt.format(formatter);
    }
}


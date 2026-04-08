package com.murilodcosta.tasktrackercli.model;

import java.util.Locale;

public enum Status {
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    DONE("Done", "done");

    private final String value;
    private final String storageValue;

    Status(String value, String storageValue) {
        this.value = value;
        this.storageValue = storageValue;
    }

    public String getValue() {
        return value;
    }

    public String toStorageValue() {
        return storageValue;
    }

    public static Status fromString(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        String normalized = rawStatus.strip().toLowerCase(Locale.ROOT)
                .replace("_", "-")
                .replace(" ", "-");

        for (Status status : values()) {
            if (status.storageValue.equals(normalized)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid status: " + rawStatus);
    }

    @Override
    public String toString() {
        return value;
    }
}

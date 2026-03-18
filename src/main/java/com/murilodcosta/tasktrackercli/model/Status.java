package com.murilodcosta.tasktrackercli.model;

public enum Status {
    TODO ("To Do"),
    IN_PROGRESS ("In Progress"),
    DONE ("Done");


    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Status{" +
                "value='" + value + '\'' +
                '}';
    }
}

package com.murilodcosta.tasktrackercli.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    @DisplayName("getValue should return display text")
    void testGetValue() {
        assertEquals("To Do", Status.TODO.getValue());
        assertEquals("In Progress", Status.IN_PROGRESS.getValue());
        assertEquals("Done", Status.DONE.getValue());
    }

    @Test
    @DisplayName("toStorageValue should return serialized status key")
    void testToStorageValue() {
        assertEquals("todo", Status.TODO.toStorageValue());
        assertEquals("in-progress", Status.IN_PROGRESS.toStorageValue());
        assertEquals("done", Status.DONE.toStorageValue());
    }

    @Test
    @DisplayName("fromString should parse status in different formats")
    void testFromString() {
        assertEquals(Status.TODO, Status.fromString("todo"));
        assertEquals(Status.IN_PROGRESS, Status.fromString("IN_PROGRESS"));
        assertEquals(Status.IN_PROGRESS, Status.fromString("in progress"));
        assertEquals(Status.DONE, Status.fromString("Done"));
    }

    @Test
    @DisplayName("toString should return display text")
    void testToString() {
        assertEquals("To Do", Status.TODO.toString());
        assertEquals("In Progress", Status.IN_PROGRESS.toString());
        assertEquals("Done", Status.DONE.toString());
    }

    @Test
    @DisplayName("fromString should throw for invalid value")
    void testFromStringInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Status.fromString("doing"));
    }
}
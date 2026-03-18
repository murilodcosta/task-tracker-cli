package com.murilodcosta.tasktrackercli.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    @DisplayName("Test getValue() method of Status enum")
    void testGetValue() {
        assertEquals("To Do", Status.TODO.getValue());
        assertEquals("In Progress", Status.IN_PROGRESS.getValue());
        assertEquals("Done", Status.DONE.getValue());
    }

    @Test
    @DisplayName("Test toString() method of Status enum")
    void testToString() {
        assertEquals("Status{value='To Do'}", Status.TODO.toString());
        assertEquals("Status{value='In Progress'}", Status.IN_PROGRESS.toString());
        assertEquals("Status{value='Done'}", Status.DONE.toString());
    }

}
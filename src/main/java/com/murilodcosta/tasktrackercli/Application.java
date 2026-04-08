package com.murilodcosta.tasktrackercli;

import com.murilodcosta.tasktrackercli.manager.TaskManager;
import com.murilodcosta.tasktrackercli.model.Status;

public class Application {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].strip().toLowerCase();

        try {
            switch (command) {
                case "add":
                    requireArgs(command, args, 2);
                    manager.addTask(args[1]);
                    break;
                case "update":
                    requireArgs(command, args, 3);
                    manager.updateTask(args[1], args[2]);
                    System.out.println("Task updated successfully");
                    break;
                case "delete":
                    requireArgs(command, args, 2);
                    manager.deleteTask(args[1]);
                    System.out.println("Task deleted successfully");
                    break;
                case "mark-in-progress":
                    requireArgs(command, args, 2);
                    manager.markInProgress(args[1]);
                    System.out.println("Task marked as in-progress");
                    break;
                case "mark-done":
                    requireArgs(command, args, 2);
                    manager.markDone(args[1]);
                    System.out.println("Task marked as done");
                    break;
                case "list":
                    if (args.length == 1) {
                        manager.listTasks("all");
                    } else if (args.length == 2) {
                        validateListType(args[1]);
                        manager.listTasks(args[1]);
                    } else {
                        throw new IllegalArgumentException("Usage: task-cli list [todo|in-progress|done]");
                    }
                    break;
                case "help":
                    printUsage();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command: " + command);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
        }
    }

    private static void requireArgs(String command, String[] args, int expectedLength) {
        if (args.length != expectedLength) {
            throw new IllegalArgumentException("Invalid arguments for command: " + command);
        }
    }

    private static void validateListType(String type) {
        Status.fromString(type);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  task-cli add \"description\"");
        System.out.println("  task-cli update <id> \"new description\"");
        System.out.println("  task-cli delete <id>");
        System.out.println("  task-cli mark-in-progress <id>");
        System.out.println("  task-cli mark-done <id>");
        System.out.println("  task-cli list");
        System.out.println("  task-cli list todo|in-progress|done");
    }
}
package com.example.personaltasksmanagement.models;

public class Task {
    private final String task;
    private final String status;

    public Task(String task, String status) {
        this.task = task;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public String getStatus() {
        return status;
    }
}

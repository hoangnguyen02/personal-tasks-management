package com.example.personaltasksmanagement.models;

import java.time.LocalDate;

public class TaskData {
    private Integer task_id;
    private Integer user_id;
    private String task;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private LocalDate createdDate;
    private LocalDate complete;
    private LocalDate delete;

    // Constructor
    public TaskData(Integer task_id, Integer user_id, String task, String description, String status, String priority, LocalDate createdDate, LocalDate dueDate, LocalDate complete, LocalDate delete) {
        this.task_id = task_id;
        this.user_id = user_id;
        this.task = task;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.complete = complete;
        this.delete = delete;
    }

    // Getters
    public Integer getTask_id() {
        return task_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getTask() {
        return task;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getComplete() {
        return complete;
    }

    public LocalDate getDelete() {
        return delete;
    }

    // Setters
    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public void setComplete(LocalDate complete) {
        this.complete = complete;
    }

    public void setDelete(LocalDate delete) {
        this.delete = delete;
    }

    // toString method
    @Override
    public String toString() {
        return "Task{" +
                "task_id=" + task_id +
                ", user_id=" + user_id +
                ", task='" + task + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createdDate=" + createdDate +
                ", complete=" + complete +
                '}';
    }
}

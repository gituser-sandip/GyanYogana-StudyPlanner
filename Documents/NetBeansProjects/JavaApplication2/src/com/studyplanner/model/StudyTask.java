package com.studyplanner.model;

public class StudyTask {
    private int id;
    private String topic;
    private String subject;
    private String deadline; // Must match "deadline" used in Controller
    private String status;

    public StudyTask(int id, String topic, String subject, String deadline, String status) {
        this.id = id;
        this.topic = topic;
        this.subject = subject;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
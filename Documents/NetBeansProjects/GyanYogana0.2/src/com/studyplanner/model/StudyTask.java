package com.studyplanner.model;

public class StudyTask implements Comparable<StudyTask> {
    private static int idCounter = 1;
    private int id;
    private String topic;
    private String subject;
    private String deadline; // Format: YYYY-MM-DD
    private String status;   // Pending, Done, etc.

    public StudyTask(String topic, String subject, String deadline, String status) {
        this.id = idCounter++;
        this.topic = topic;
        this.subject = subject;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTopic() { return topic; }
    public String getSubject() { return subject; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }

    public void setTopic(String topic) { this.topic = topic; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return topic + " (" + subject + ")";
    }

    // Default sorting by ID
    @Override
    public int compareTo(StudyTask other) {
        return Integer.compare(this.id, other.id);
    }
}

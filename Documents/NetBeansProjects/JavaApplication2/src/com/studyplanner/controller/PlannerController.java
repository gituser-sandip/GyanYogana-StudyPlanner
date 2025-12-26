package com.studyplanner.controller;

import com.studyplanner.model.StudyTask;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlannerController {
    private ArrayList<StudyTask> tasks;

    public PlannerController() {
        tasks = new ArrayList<>();
        // Mock Data - Using Safe Dates
        tasks.add(new StudyTask(1, "Binary Search Algo", "Data Structures", "2025-12-30", "Pending"));
        tasks.add(new StudyTask(2, "MVC Architecture", "Java Programming", "2025-12-28", "In Progress"));
        tasks.add(new StudyTask(3, "UI Design Pattern", "HCI Module", "2026-01-05", "Completed"));
        tasks.add(new StudyTask(4, "Old Homework", "Maths", "2023-01-01", "Pending"));
    }

    public List<StudyTask> getAllTasks() { return tasks; }

    public String addTask(String topic, String subject, String deadline) {
        if (topic.isEmpty() || subject.isEmpty()) return "Error: Fields cannot be empty.";
        // Regex Validation for Date (YYYY-MM-DD)
        if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) return "Error: Date must be YYYY-MM-DD.";
        
        tasks.add(new StudyTask(tasks.size() + 1, topic, subject, deadline, "Pending"));
        return "Success";
    }

    public String updateTask(int index, String topic, String subject, String deadline, String status) {
        if (index >= 0 && index < tasks.size()) {
            if (topic.isEmpty() || subject.isEmpty()) return "Error: Fields cannot be empty.";
            if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) return "Error: Date must be YYYY-MM-DD.";
            
            StudyTask task = tasks.get(index);
            task.setTopic(topic);
            task.setSubject(subject);
            task.setDeadline(deadline);
            task.setStatus(status);
            return "Success";
        }
        return "Error: Task not found.";
    }

    public void deleteTask(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) tasks.remove(rowIndex);
    }

    // --- Stats Logic (with Error Safety) ---
    public int getTotalCount() { return tasks.size(); }
    public int getPendingCount() { return (int) tasks.stream().filter(t -> t.getStatus().equals("Pending")).count(); }
    public int getCompletedCount() { return (int) tasks.stream().filter(t -> t.getStatus().equals("Completed")).count(); }
    
    public int getOverdueCount() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (StudyTask t : tasks) {
            if (t.getStatus().equalsIgnoreCase("Completed")) continue;
            try {
                // Parsing Date safely
                if (LocalDate.parse(t.getDeadline()).isBefore(today)) count++;
            } catch (Exception e) {
                // Ignore invalid dates, don't crash
                System.out.println("Invalid date found: " + t.getDeadline());
            }
        }
        return count;
    }

    // --- Merge Sort for Deadline ---
    public void sortTasksByDeadline() {
        tasks.sort(Comparator.comparing(StudyTask::getDeadline));
    }

    // --- Search Logic ---
    public int binarySearchByTopic(String topicToFind) {
        tasks.sort(Comparator.comparing(StudyTask::getTopic, String.CASE_INSENSITIVE_ORDER));
        int low = 0, high = tasks.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int res = tasks.get(mid).getTopic().compareToIgnoreCase(topicToFind);
            if (res == 0) return mid;
            if (res < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    public List<StudyTask> filterTasks(String query) {
        List<StudyTask> filtered = new ArrayList<>();
        for (StudyTask t : tasks) {
            if (t.getTopic().toLowerCase().contains(query.toLowerCase()) || 
                t.getSubject().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(t);
            }
        }
        return filtered;
    }
}
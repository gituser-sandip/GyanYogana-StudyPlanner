package com.studyplanner.controller;

import com.studyplanner.model.StudyTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlannerController {
    private static ArrayList<StudyTask> tasks = new ArrayList<>();

    public PlannerController() {
        // Dummy data so the table isn't empty when you run it
        if(tasks.isEmpty()) {
            tasks.add(new StudyTask("Java Streams", "Programming", "2024-05-20", "Pending"));
            tasks.add(new StudyTask("Calculus Limits", "Math", "2024-05-10", "Done"));
            tasks.add(new StudyTask("MVC Pattern", "Design", "2024-05-15", "In Progress"));
        }
    }

    // --- CRUD Operations ---
    
    public List<StudyTask> getAllTasks() {
        return tasks;
    }

    public String addTask(String topic, String subject, String deadline) {
        if(topic.isEmpty() || deadline.isEmpty()) return "Topic and Deadline required!";
        tasks.add(new StudyTask(topic, subject, deadline, "Pending"));
        return "Success";
    }

    public String updateTask(int index, String topic, String subject, String deadline, String status) {
        if (index >= 0 && index < tasks.size()) {
            StudyTask t = tasks.get(index);
            t.setTopic(topic);
            t.setSubject(subject);
            t.setDeadline(deadline);
            t.setStatus(status);
            return "Success";
        }
        return "Task not found";
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
    }

    // --- Statistics ---

    public int getTotalCount() { return tasks.size(); }
    public int getPendingCount() { return (int) tasks.stream().filter(t -> t.getStatus().equals("Pending")).count(); }
    public int getCompletedCount() { return (int) tasks.stream().filter(t -> t.getStatus().equals("Completed") || t.getStatus().equals("Done")).count(); }
    // Simple logic: if deadline string is alphabetically less than "2024-01-01" (Need real date logic later)
    public int getOverdueCount() { return 0; /* Placeholder for complex date logic */ }


    // --- ALGORITHMS (The Core Logic) ---

    // 1. Merge Sort (Sort by Deadline)
    public void sortTasksByDeadline() {
        tasks = mergeSort(tasks);
    }

    private ArrayList<StudyTask> mergeSort(ArrayList<StudyTask> list) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        ArrayList<StudyTask> left = new ArrayList<>(list.subList(0, mid));
        ArrayList<StudyTask> right = new ArrayList<>(list.subList(mid, list.size()));

        return merge(mergeSort(left), mergeSort(right));
    }

    private ArrayList<StudyTask> merge(ArrayList<StudyTask> left, ArrayList<StudyTask> right) {
        ArrayList<StudyTask> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            // Compare Deadlines (String comparison works for YYYY-MM-DD)
            if (left.get(i).getDeadline().compareTo(right.get(j).getDeadline()) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    // 2. Binary Search (Find by Topic)
    // Note: List must be sorted by Topic for Binary Search to work. 
    // We will auto-sort inside this method to ensure it works.
    public int binarySearchByTopic(String topic) {
        // Sort by Topic first using Java's built-in sort for speed
        tasks.sort(Comparator.comparing(StudyTask::getTopic));

        int low = 0, high = tasks.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int res = topic.compareToIgnoreCase(tasks.get(mid).getTopic());

            if (res == 0) return mid; // Found
            if (res > 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1; // Not found
    }

    // 3. Linear Search (Filter for Search Bar)
    public List<StudyTask> filterTasks(String query) {
        return tasks.stream()
                .filter(t -> t.getTopic().toLowerCase().contains(query.toLowerCase()) 
                          || t.getSubject().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
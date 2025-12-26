package com.studyplanner.controller;

import com.studyplanner.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static List<User> users = new ArrayList<>();

    public UserController() {
        // Pre-load a test user
        if (users.isEmpty()) {
            users.add(new User("admin", "admin123"));
        }
    }

    public boolean registerUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return false;
        
        // Check if username exists
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return false;
        }

        users.add(new User(username, password));
        return true;
    }

    public User loginUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null; // Login failed
    }
}
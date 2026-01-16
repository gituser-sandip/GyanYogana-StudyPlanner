package com.studyplanner.controller;

import com.studyplanner.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    // Temporary memory storage (Database/File IO will come later)
    private static List<User> users = new ArrayList<>();

    public UserController() {
        // Add a default user for testing
        if (users.isEmpty()) {
            users.add(new User("admin", "123")); 
        }
    }

    public boolean registerUser(String username, String password) {
        // Check if user exists
        for (User u : users) {
            if (u.getUsername().equals(username)) return false;
        }
        users.add(new User(username, password));
        return true;
    }

    public boolean loginUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
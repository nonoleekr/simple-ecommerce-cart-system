package model;

import java.io.*;
import java.util.*;

public class UserManager {
    private static final String USERS_FILE = "data/users.txt";
    private List<User> users;

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                User user = User.fromString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine for new installations
        }
    }

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                bw.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // Username already exists
            }
        }

        // Add new user
        User newUser = new User(username, password);
        users.add(newUser);
        saveUsers();
        return true;
    }

    public User loginUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // Invalid credentials
    }

    public boolean userExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}

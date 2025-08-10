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

    public boolean removeUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public static void performanceTest(int numUsers) {
        UserManager userManager = new UserManager();
        long startAdd = System.nanoTime();
        for (int i = 0; i < numUsers; i++) {
            userManager.registerUser("user" + i, "password" + i);
        }
        long endAdd = System.nanoTime();
        long addTime = endAdd - startAdd;

        long startRemove = System.nanoTime();
        for (int i = 0; i < numUsers; i++) {
            userManager.removeUser("user" + i);
        }
        long endRemove = System.nanoTime();
        long removeTime = endRemove - startRemove;

        System.out.println("Add " + numUsers + " users: " + addTime + " ns");
        System.out.println("Remove " + numUsers + " users: " + removeTime + " ns");
    }
}

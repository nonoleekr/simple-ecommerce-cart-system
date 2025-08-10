package model;

import java.io.*;
import java.util.*;

public class OrderManager {
    private static final String ORDERS_FILE = "data/orders.txt";
    private List<Order> allOrders;

    public OrderManager() {
        allOrders = new ArrayList<>();
        loadOrders();
    }

    private void loadOrders() {
        allOrders.clear();
        System.out.println("Starting to load orders from file: " + ORDERS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(ORDERS_FILE))) {
            StringBuilder currentOrder = new StringBuilder();
            StringBuilder currentItems = new StringBuilder();
            String line;
            boolean readingOrder = false;
            boolean readingItems = false;
            int orderCount = 0;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                System.out.println("Line " + lineNumber + ": '" + line + "'");
                
                if (line.startsWith("Order ID:") || line.startsWith("User:")) {
                    // Start of a new order
                    if (readingOrder && currentOrder.length() > 0) {
                        // Process previous order
                        System.out.println("Processing previous order...");
                        processOrder(currentOrder.toString(), currentItems.toString());
                        orderCount++;
                    }
                    currentOrder = new StringBuilder();
                    currentItems = new StringBuilder();
                    readingOrder = true;
                    readingItems = false;
                    currentOrder.append(line).append("\n");
                    System.out.println("Started reading new order");
                } else if (line.startsWith("Order at:")) {
                    currentOrder.append(line).append("\n");
                    readingItems = true;
                    System.out.println("Started reading items");
                } else if (line.equals("---")) {
                    if (readingOrder && currentOrder.length() > 0) {
                        // Process the complete order
                        System.out.println("Processing complete order...");
                        processOrder(currentOrder.toString(), currentItems.toString());
                        orderCount++;
                    }
                    currentOrder = new StringBuilder();
                    currentItems = new StringBuilder();
                    readingOrder = false;
                    readingItems = false;
                    System.out.println("Finished order, resetting");
                } else if (readingItems && !line.trim().isEmpty()) {
                    currentItems.append(line).append("\n");
                }
            }
            
            // Process any remaining order at the end of file
            if (readingOrder && currentOrder.length() > 0) {
                System.out.println("Processing final order...");
                processOrder(currentOrder.toString(), currentItems.toString());
                orderCount++;
            }
            
            System.out.println("Loaded " + orderCount + " orders from file");
        } catch (IOException e) {
            System.out.println("Orders file not found or empty - starting with no orders");
            // File might not exist yet, which is fine for new installations
        }
    }

    private void processOrder(String orderData, String itemsData) {
        try {
            // Parse the order header
            String[] lines = orderData.split("\n");
            String orderId = "";
            String username = "";
            String timestampStr = "";
            double total = 0.0;

            for (String line : lines) {
                if (line.startsWith("Order ID:")) {
                    orderId = line.substring("Order ID: ".length()).trim();
                } else if (line.startsWith("User:")) {
                    username = line.substring("User: ".length()).trim();
                } else if (line.startsWith("Order at:")) {
                    String[] parts = line.split(", Total: \\$");
                    if (parts.length == 2) {
                        timestampStr = parts[0].substring("Order at: ".length()).trim();
                        total = Double.parseDouble(parts[1].trim());
                    }
                }
            }

            System.out.println("Processing order - ID: '" + orderId + "', User: '" + username + "', Total: " + total);

            if (!orderId.isEmpty() && !username.isEmpty()) {
                // Create a temporary cart to hold the items
                Cart tempCart = new Cart();
                String[] itemLines = itemsData.split("\n");
                for (String itemLine : itemLines) {
                    if (itemLine.equals("---") || itemLine.trim().isEmpty()) continue;
                    String[] itemParts = itemLine.split(",");
                    if (itemParts.length == 4) {
                        String id = itemParts[0];
                        String name = itemParts[1];
                        int quantity = Integer.parseInt(itemParts[2]);
                        double price = Double.parseDouble(itemParts[3]);
                        Product product = new Product(id, name, price, 0);
                        tempCart.addItem(product, quantity);
                    }
                }

                // Create and add the order
                Order order = new Order(username, tempCart, total);
                order.setOrderId(orderId);
                // Parse timestamp (simplified - you might want to use a proper date parser)
                allOrders.add(order);
                System.out.println("Successfully processed order for user: " + username);
            } else {
                System.out.println("Skipping order - missing orderId or username");
            }
        } catch (Exception e) {
            // Skip malformed orders
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveOrder(Order order) {
        allOrders.add(order);
        appendOrderToFile(order);
    }

    private void appendOrderToFile(Order order) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
            bw.write(order.toString() + "\n\n");
            System.out.println("Order saved successfully: " + order.getOrderId() + " for user: " + order.getUsername());
        } catch (IOException e) {
            System.err.println("Failed to save order to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveOrders() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : allOrders) {
                bw.write(order.toString() + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getUserOrders(String username) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getUsername().equals(username)) {
                userOrders.add(order);
            }
        }
        // Sort by timestamp (newest first)
        userOrders.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
        return userOrders;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    public void clearOrders() {
        allOrders.clear();
        saveOrders();
    }

    /**
     * Rebuilds the orders file from memory - useful for maintenance
     * This ensures file consistency with in-memory data
     */
    public void rebuildOrdersFile() {
        saveOrders();
    }

    /**
     * Debug method to show current orders in memory
     */
    public void debugPrintOrders() {
        System.out.println("=== Current Orders in Memory ===");
        System.out.println("Total orders: " + allOrders.size());
        for (Order order : allOrders) {
            System.out.println("Order ID: " + order.getOrderId() + 
                             ", User: " + order.getUsername() + 
                             ", Total: $" + String.format("%.2f", order.getTotal()));
        }
        System.out.println("================================");
    }
}

package model;

import java.util.Date;

public class Order {
    private String orderId;
    private String username;
    private Cart cart;
    private double total;
    private Date timestamp;

    public Order(String username, Cart cart, double total) {
        this.orderId = generateOrderId();
        this.username = username;
        this.cart = cart;
        this.total = total;
        this.timestamp = new Date();
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUsername() { return username; }
    public Cart getCart() { return cart; }
    public double getTotal() { return total; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("User: ").append(username).append("\n");
        sb.append("Order at: ").append(timestamp).append(", Total: $").append(String.format("%.2f", total)).append("\n");
        
        CartItem current = cart.getHead();
        while (current != null) {
            Product product = current.getProduct();
            sb.append(product.getId()).append(",").append(product.getName()).append(",")
              .append(current.getQuantity()).append(",").append(String.format("%.2f", product.getPrice())).append("\n");
            current = current.getNext();
        }
        sb.append("---");
        return sb.toString();
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(orderId).append("|").append(username).append("|")
          .append(timestamp.getTime()).append("|").append(String.format("%.2f", total)).append("\n");
        
        CartItem current = cart.getHead();
        while (current != null) {
            Product product = current.getProduct();
            sb.append(product.getId()).append(",").append(product.getName()).append(",")
              .append(current.getQuantity()).append(",").append(String.format("%.2f", product.getPrice())).append("\n");
            current = current.getNext();
        }
        sb.append("---");
        return sb.toString();
    }

    public static Order fromFileString(String orderData, String itemsData) {
        String[] parts = orderData.split("\\|");
        if (parts.length != 4) return null;
        
        String orderId = parts[0];
        String username = parts[1];
        long timestamp = Long.parseLong(parts[2]);
        double total = Double.parseDouble(parts[3]);
        
        // Create a temporary cart to hold the items
        Cart tempCart = new Cart();
        String[] lines = itemsData.split("\n");
        for (String line : lines) {
            if (line.equals("---")) break;
            String[] itemParts = line.split(",");
            if (itemParts.length == 4) {
                String id = itemParts[0];
                String name = itemParts[1];
                int quantity = Integer.parseInt(itemParts[2]);
                double price = Double.parseDouble(itemParts[3]);
                Product product = new Product(id, name, price, 0); // stock not relevant for orders
                tempCart.addItem(product, quantity);
            }
        }
        
        Order order = new Order(username, tempCart, total);
        order.orderId = orderId;
        order.timestamp = new Date(timestamp);
        return order;
    }
}

package model;

import java.util.Date;

public class Order {
    private Cart cart;
    private double total;
    private Date timestamp;

    public Order(Cart cart, double total) {
        this.cart = cart;
        this.total = total;
        this.timestamp = new Date();
    }

    public Cart getCart() { return cart; }
    public double getTotal() { return total; }
    public Date getTimestamp() { return timestamp; }
}

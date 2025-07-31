package model;

public class OrderQueue {
    private static class Node {
        Order order;
        Node next;
        Node(Order order) { this.order = order; }
    }
    private Node front, rear;

    public OrderQueue() {
        front = rear = null;
    }

    public void placeOrder(Order order) {
        Node node = new Node(order);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    public Order processNextOrder() {
        if (front == null) return null;
        Order order = front.order;
        front = front.next;
        if (front == null) rear = null;
        return order;
    }

    public boolean isEmpty() {
        return front == null;
    }
}

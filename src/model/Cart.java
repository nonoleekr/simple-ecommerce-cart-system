package model;

public class Cart {
    private CartItem head;

    public Cart() {
        head = null;
    }

    public void addItem(Product product, int quantity) {
        CartItem current = head;
        while (current != null) {
            if (current.getProduct().getId().equals(product.getId())) {
                current.setQuantity(current.getQuantity() + quantity);
                return;
            }
            current = current.getNext();
        }
        CartItem newItem = new CartItem(product, quantity);
        newItem.setNext(head);
        head = newItem;
    }

    public void removeItem(String productId) {
        CartItem current = head, prev = null;
        while (current != null) {
            if (current.getProduct().getId().equals(productId)) {
                if (prev == null) {
                    head = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                return;
            }
            prev = current;
            current = current.getNext();
        }
    }

    public void displayCart() {
        CartItem current = head;
        while (current != null) {
            System.out.println(current.getProduct().getName() + " x " + current.getQuantity());
            current = current.getNext();
        }
    }

    public double calculateTotal() {
        double total = 0;
        CartItem current = head;
        while (current != null) {
            total += current.getProduct().getPrice() * current.getQuantity();
            current = current.getNext();
        }
        return total;
    }

    public CartItem getHead() { return head; }
}

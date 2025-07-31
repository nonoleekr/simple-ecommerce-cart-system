package model;

public class CartItem {
    private Product product;
    private int quantity;
    private CartItem next;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.next = null;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public CartItem getNext() { return next; }
    public void setNext(CartItem next) { this.next = next; }
}

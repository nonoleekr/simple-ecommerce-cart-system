package model;

public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return id + "," + name + "," + price + "," + stock;
    }

    public static Product fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) return null;
        return new Product(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
    }
}

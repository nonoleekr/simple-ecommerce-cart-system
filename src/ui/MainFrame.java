package ui;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Cart;
import model.CartItem;
import model.Order;
import model.OrderQueue;
import model.Product;
import model.User;
import ui.LoginFrame;

public class MainFrame extends JFrame {
    private java.util.List<Product> products = new ArrayList<>();
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private Cart cart = new Cart();
    private OrderQueue orderQueue = new OrderQueue();
    private User currentUser;

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("E-Commerce Cart System" + (user != null ? " - Welcome " + user.getUsername() : ""));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load products from file
        loadProducts();

        // Product Table
        String[] columns = {"ID", "Name", "Price", "Stock"};
        productTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        refreshProductTable();

        JScrollPane scrollPane = new JScrollPane(productTable);

        // Add to Cart Button
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.addActionListener(e -> addSelectedProductToCart());

        // View Cart Button
        JButton viewCartBtn = new JButton("View Cart");
        viewCartBtn.addActionListener(e -> showCartDialog());

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addToCartBtn);
        bottomPanel.add(viewCartBtn);
        bottomPanel.add(logoutBtn);

        add(new JLabel("Product List", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        products.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("data/products.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                Product p = Product.fromString(line);
                if (p != null) products.add(p);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock()});
        }
    }

    private void addSelectedProductToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }
        String id = (String) productTableModel.getValueAt(row, 0);
        Product selected = null;
        for (Product p : products) {
            if (p.getId().equals(id)) {
                selected = p;
                break;
            }
        }
        if (selected == null) return;
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null) return;
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0 || qty > selected.getStock()) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }
        cart.addItem(selected, qty);
        JOptionPane.showMessageDialog(this, "Added to cart: " + selected.getName() + " x " + qty);
    }

    private void showCartDialog() {
        // Build cart table data
        java.util.List<CartItem> cartItems = new ArrayList<>();
        CartItem current = cart.getHead();
        while (current != null) {
            cartItems.add(current);
            current = current.getNext();
        }
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.", "Cart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] columns = {"Product", "Quantity", "Price"};
        Object[][] data = new Object[cartItems.size()][3];
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            data[i][0] = item.getProduct().getName();
            data[i][1] = item.getQuantity();
            data[i][2] = item.getProduct().getPrice();
        }
        DefaultTableModel cartModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an item to remove.");
                return;
            }
            String prodName = (String) cartModel.getValueAt(row, 0);
            // Find product id by name
            String prodId = null;
            for (CartItem item : cartItems) {
                if (item.getProduct().getName().equals(prodName)) {
                    prodId = item.getProduct().getId();
                    break;
                }
            }
            if (prodId != null) {
                cart.removeItem(prodId);
                ((JDialog) SwingUtilities.getWindowAncestor(cartTable)).dispose();
                showCartDialog();
            }
        });

        JButton purchaseBtn = new JButton("Purchase");
        purchaseBtn.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty.");
                return;
            }
            // Create order and add to queue
            Order order = new Order(cart, cart.calculateTotal());
            orderQueue.placeOrder(order);
            saveOrderToFile(order);
            // Clear the cart
            cart = new Cart();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            ((JDialog) SwingUtilities.getWindowAncestor(cartTable)).dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(removeBtn);
        btnPanel.add(purchaseBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(new JLabel("Total: $" + String.format("%.2f", cart.calculateTotal()), SwingConstants.CENTER), BorderLayout.NORTH);

        JDialog dialog = new JDialog(this, "Cart", true);
        dialog.setContentPane(panel);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveOrderToFile(Order order) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/orders.txt", true))) {
            bw.write("Order at: " + order.getTimestamp() + ", Total: $" + String.format("%.2f", order.getTotal()) + "\n");
            CartItem current = order.getCart().getHead();
            while (current != null) {
                bw.write(current.getProduct().getId() + "," + current.getProduct().getName() + "," + current.getQuantity() + "," + current.getProduct().getPrice() + "\n");
                current = current.getNext();
            }
            bw.write("---\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save order.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel btnPanel = new JPanel();
        

        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(new JLabel("Total: $" + String.format("%.2f", cart.calculateTotal()), SwingConstants.CENTER), BorderLayout.NORTH);

        JDialog dialog = new JDialog(this, "Cart", true);
        dialog.setContentPane(panel);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void processNextOrder() {
        Order order = orderQueue.processNextOrder();
        if (order != null) {
            // Process the order
            System.out.println("Processing order:");
            System.out.println("Order ID: " + order.getTimestamp());
            System.out.println("Total: $" + String.format("%.2f", order.getTotal()));
            CartItem current = order.getCart().getHead();
            while (current != null) {
                System.out.println(current.getProduct().getName() + " x " + current.getQuantity());
                current = current.getNext();
            }
            // Save the order to file
            saveOrderToFile(order);
            JOptionPane.showMessageDialog(this, "Order processed successfully.", "Order Processed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No orders in queue.", "No Orders", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            // Create new login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
package ui;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Cart;
import model.CartItem;
import model.Order;
import model.OrderManager;
import model.OrderQueue;
import model.Product;
import model.User;

public class MainFrame extends JFrame {
    private java.util.List<Product> products = new ArrayList<>();
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private Cart cart = new Cart();
    private OrderQueue orderQueue = new OrderQueue();
    private OrderManager orderManager;
    private User currentUser;
    private JButton viewCartBtn;

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        this.currentUser = user;
        this.orderManager = new OrderManager();
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
        JButton addToCartBtn = new JButton("🛒 Add to Cart");
        addToCartBtn.addActionListener(e -> addSelectedProductToCart());

        // View Cart Button
        this.viewCartBtn = new JButton("🛍️ View Cart (" + getCartItemCount(cart) + ")");
        this.viewCartBtn.addActionListener(e -> showCartDialog());

        // Order History Button
        JButton orderHistoryBtn = new JButton("Order History");
        orderHistoryBtn.addActionListener(e -> showOrderHistoryDialog());

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addToCartBtn);
        bottomPanel.add(viewCartBtn);
        bottomPanel.add(orderHistoryBtn);
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
        // Update cart button text
        updateCartButtonText();
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
        
        // Enhanced cart table with subtotals
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        Object[][] data = new Object[cartItems.size()][4];
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            double subtotal = item.getQuantity() * item.getProduct().getPrice();
            data[i][0] = item.getProduct().getName();
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("$%.2f", item.getProduct().getPrice());
            data[i][3] = String.format("$%.2f", subtotal);
        }
        
        DefaultTableModel cartModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        // Enhanced buttons with better styling
        JButton removeBtn = new JButton("🗑️ Remove");
        removeBtn.setBackground(new Color(255, 99, 71));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        
        JButton editQtyBtn = new JButton("✏️ Edit Quantity");
        editQtyBtn.setBackground(new Color(70, 130, 180));
        editQtyBtn.setForeground(Color.WHITE);
        editQtyBtn.setFocusPainted(false);
        
        JButton purchaseBtn = new JButton("💳 Proceed to Payment");
        purchaseBtn.setBackground(new Color(34, 139, 34));
        purchaseBtn.setForeground(Color.WHITE);
        purchaseBtn.setFocusPainted(false);
        purchaseBtn.setFont(purchaseBtn.getFont().deriveFont(Font.BOLD, 14f));

        // Remove item functionality
        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String prodName = (String) cartModel.getValueAt(row, 0);
            String prodId = null;
            for (CartItem item : cartItems) {
                if (item.getProduct().getName().equals(prodName)) {
                    prodId = item.getProduct().getId();
                    break;
                }
            }
            if (prodId != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Remove " + prodName + " from cart?", "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cart.removeItem(prodId);
                    updateCartButtonText();
                    ((JDialog) SwingUtilities.getWindowAncestor(cartTable)).dispose();
                    showCartDialog();
                }
            }
        });

        // Edit quantity functionality
        editQtyBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String prodName = (String) cartModel.getValueAt(row, 0);
            CartItem selectedItem = null;
            for (CartItem item : cartItems) {
                if (item.getProduct().getName().equals(prodName)) {
                    selectedItem = item;
                    break;
                }
            }
            if (selectedItem != null) {
                String newQtyStr = JOptionPane.showInputDialog(this, 
                    "Enter new quantity for " + prodName + " (current: " + selectedItem.getQuantity() + "):", 
                    String.valueOf(selectedItem.getQuantity()));
                if (newQtyStr != null && !newQtyStr.trim().isEmpty()) {
                    try {
                        int newQty = Integer.parseInt(newQtyStr);
                        if (newQty <= 0) {
                            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (newQty > selectedItem.getProduct().getStock()) {
                            JOptionPane.showMessageDialog(this, "Quantity exceeds available stock.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Remove old item and add new one with updated quantity
                        cart.removeItem(selectedItem.getProduct().getId());
                        cart.addItem(selectedItem.getProduct(), newQty);
                        updateCartButtonText();
                        
                        ((JDialog) SwingUtilities.getWindowAncestor(cartTable)).dispose();
                        showCartDialog();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Enhanced purchase functionality
        purchaseBtn.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showPaymentDialog(cart);
        });

        // Button panel with better layout
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.add(removeBtn);
        btnPanel.add(editQtyBtn);
        btnPanel.add(purchaseBtn);

        // Enhanced total display
        double total = cart.calculateTotal();
        JLabel totalLabel = new JLabel("Total: $" + String.format("%.2f", total), SwingConstants.CENTER);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));
        totalLabel.setForeground(new Color(34, 139, 34));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(totalLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Shopping Cart - " + currentUser.getUsername(), true);
        dialog.setContentPane(panel);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showPaymentDialog(Cart cart) {
        // Create order summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        
        // Build summary table
        java.util.List<CartItem> cartItems = new ArrayList<>();
        CartItem current = cart.getHead();
        while (current != null) {
            cartItems.add(current);
            current = current.getNext();
        }
        
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        Object[][] data = new Object[cartItems.size()][4];
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            double subtotal = item.getQuantity() * item.getProduct().getPrice();
            data[i][0] = item.getProduct().getName();
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("$%.2f", item.getProduct().getPrice());
            data[i][3] = String.format("$%.2f", subtotal);
        }
        
        DefaultTableModel summaryModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(summaryTable);
        
        // Total display
        double total = cart.calculateTotal();
        JLabel totalLabel = new JLabel("Total Amount: $" + String.format("%.2f", total), SwingConstants.CENTER);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 18f));
        totalLabel.setForeground(new Color(34, 139, 34));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        summaryPanel.add(totalLabel, BorderLayout.NORTH);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Payment method selection
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        
        String[] paymentMethods = {"Credit Card", "Debit Card", "PayPal", "Cash on Delivery"};
        JComboBox<String> paymentCombo = new JComboBox<>(paymentMethods);
        paymentCombo.setSelectedIndex(0);
        
        JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentMethodPanel.add(new JLabel("Select Payment Method:"));
        paymentMethodPanel.add(paymentCombo);
        
        paymentPanel.add(paymentMethodPanel, BorderLayout.CENTER);
        
        // Payment confirmation
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.setBorder(BorderFactory.createTitledBorder("Payment Confirmation"));
        
        JLabel confirmLabel = new JLabel("Please review your order and confirm payment.");
        confirmLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton confirmBtn = new JButton("💳 Confirm Payment");
        confirmBtn.setBackground(new Color(34, 139, 34));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(confirmBtn.getFont().deriveFont(Font.BOLD, 14f));
        confirmBtn.setFocusPainted(false);
        
        JButton cancelBtn = new JButton("❌ Cancel");
        cancelBtn.setBackground(new Color(220, 20, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        
        confirmPanel.add(confirmLabel, BorderLayout.CENTER);
        confirmPanel.add(btnPanel, BorderLayout.SOUTH);
        
        // Main payment dialog layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(paymentPanel, BorderLayout.CENTER);
        mainPanel.add(confirmPanel, BorderLayout.SOUTH);
        
        JDialog paymentDialog = new JDialog(this, "Payment - " + currentUser.getUsername(), true);
        paymentDialog.setContentPane(mainPanel);
        paymentDialog.setSize(700, 600);
        paymentDialog.setLocationRelativeTo(this);
        
        // Button actions
        cancelBtn.addActionListener(e -> paymentDialog.dispose());
        
        confirmBtn.addActionListener(e -> {
            String selectedMethod = (String) paymentCombo.getSelectedItem();
            
            // Show processing message
            JOptionPane.showMessageDialog(paymentDialog, 
                "Processing payment via " + selectedMethod + "...", 
                "Processing Payment", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Simulate payment processing delay
            javax.swing.Timer timer = new javax.swing.Timer(2000, evt -> {
                // Create and save the order
                Order order = new Order(currentUser.getUsername(), cart, total);
                orderQueue.placeOrder(order);
                orderManager.saveOrder(order);
                
                // Clear the cart
                this.cart = new Cart();
                updateCartButtonText();
                
                // Show success message
                JOptionPane.showMessageDialog(paymentDialog, 
                    "Payment successful! Order #" + order.getOrderId() + " has been placed.\n\n" +
                    "Thank you for your purchase!", 
                    "Payment Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                paymentDialog.dispose();
            });
            timer.setRepeats(false);
            timer.start();
        });
        
        paymentDialog.setVisible(true);
    }

    private void updateCartButtonText() {
        if (viewCartBtn != null) {
            viewCartBtn.setText("🛍️ View Cart (" + getCartItemCount(cart) + ")");
        }
    }

    private void showOrderHistoryDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "User not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.util.List<Order> userOrders = orderManager.getUserOrders(currentUser.getUsername());
        if (userOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order history available.", "Order History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] columns = {"Order ID", "Date", "Total", "Items"};
        Object[][] data = new Object[userOrders.size()][4];
        for (int i = 0; i < userOrders.size(); i++) {
            Order order = userOrders.get(i);
            data[i][0] = order.getOrderId();
            data[i][1] = order.getTimestamp();
            data[i][2] = String.format("$%.2f", order.getTotal());
            data[i][3] = getCartItemCount(order.getCart());
        }
        
        DefaultTableModel historyModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            int row = historyTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an order to view details.");
                return;
            }
            Order selectedOrder = userOrders.get(row);
            showOrderDetailsDialog(selectedOrder);
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(viewDetailsBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Order History - " + currentUser.getUsername(), true);
        dialog.setContentPane(panel);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private int getCartItemCount(Cart cart) {
        int count = 0;
        CartItem current = cart.getHead();
        while (current != null) {
            count += current.getQuantity();
            current = current.getNext();
        }
        return count;
    }

    private void showOrderDetailsDialog(Order order) {
        String[] columns = {"Product ID", "Name", "Quantity", "Price", "Subtotal"};
        java.util.List<Object[]> dataList = new ArrayList<>();
        
        CartItem current = order.getCart().getHead();
        while (current != null) {
            double subtotal = current.getQuantity() * current.getProduct().getPrice();
            dataList.add(new Object[]{
                current.getProduct().getId(),
                current.getProduct().getName(),
                current.getQuantity(),
                String.format("$%.2f", current.getProduct().getPrice()),
                String.format("$%.2f", subtotal)
            });
            current = current.getNext();
        }
        
        Object[][] data = dataList.toArray(new Object[0][]);
        DefaultTableModel detailsModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable detailsTable = new JTable(detailsModel);
        JScrollPane scrollPane = new JScrollPane(detailsTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(new JLabel("Order Total: $" + String.format("%.2f", order.getTotal()), SwingConstants.CENTER), BorderLayout.NORTH);

        JDialog dialog = new JDialog(this, "Order Details - " + order.getOrderId(), true);
        dialog.setContentPane(panel);
        dialog.setSize(500, 300);
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
            // Order already saved by OrderManager when placed
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
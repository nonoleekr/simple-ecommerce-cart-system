# 🛒 Simple E-commerce Cart System

This project is a basic e-commerce cart system developed in **Java** using **Java Swing**. It is designed as a practical implementation of core data structures for the **Data Structures & Algorithms (BIC2214)** module.

The system simulates the essential features of an online shopping platform, including cart management, order processing, and text-based data persistence.

---

## 📌 Project Description

The main goal of this application is to demonstrate the use of **Linked Lists** and **Queues** in a real-world scenario.

Users can:
- Browse products
- Add/remove items from their shopping cart
- Place orders using a FIFO (First-In-First-Out) queue

All data (products, carts, orders) is stored in `.txt` files — **no database required**.

---

## ✅ Features

### 🖼️ Graphical User Interface
- Built with Java Swing
- Simple and intuitive for smooth interaction

### 🛍️ Product Catalog
- Displays a list of products from a `.txt` file

### 🧺 Shopping Cart Management
- Add items to the cart
- Remove items from the cart
- View current cart items
- Calculate total price

### 📦 Order Processing
- Place orders (enqueue)
- Process orders in FIFO order (dequeue)

### 💾 Data Persistence
- All data is read from and saved to `.txt` files

### ⚙️ Performance Analysis
- Measures `addItem()` and `removeItem()` time in nanoseconds
- Supports performance tests with 100 and 150 items

---

## 🧠 Data Structures Used

| Structure    | Purpose                       |
|--------------|-------------------------------|
| `LinkedList` | Implements the shopping cart  |
| `Queue`      | Manages order processing (FIFO) |

---

## 💻 Technologies Used

- **Language:** Java
- **UI:** Java Swing
- **File I/O:** Standard Java I/O (`FileReader`, `FileWriter`)

---

## 🚀 Setup & Installation

1. **Install JDK**  
   Make sure Java is installed on your system.

2. **Clone the Repository**  
   ```bash
   git clone https://github.com/your-username/simple-ecommerce-cart-system.git
   cd simple-ecommerce-cart-system

3. **Compile the Code**

   ```bash
   javac -d bin src/**/*.java
   ```

4. **Run the Application**

   ```bash
   java -cp bin ui.MainFrame
   ```

   > Replace `ui.MainFrame` with the correct path to your main class if different.

---

## 📊 Performance Analysis

The system includes a utility that uses `System.nanoTime()` to measure the time it takes to:

* Add 100 and 150 items to the cart
* Remove 100 and 150 items from the cart

These results are discussed in the final project report, with comparisons to theoretical Big O complexities.

---

## 👥 Contributors

* \[Your Name]
* \[Group Member 2's Name]
* \[Group Member 3's Name]
* \[Group Member 4's Name]

---

## 📁 Directory Structure (Simplified)

```
simple-ecommerce-cart-system/
├── src/
│   ├── ui/               # Swing UI
│   ├── cart/             # Cart using LinkedList
│   ├── order/            # Order processing using Queue
│   ├── model/            # Product model
│   └── utils/            # File I/O, performance tester
├── data/                 # .txt files for persistence
├── bin/                  # Compiled .class files (ignored in .gitignore)
├── README.md
└── .gitignore
```

---

## 🏛️ Educational Use

This system is developed strictly for educational purposes under the **Data Structures & Algorithms** course and is not intended for production or commercial use.

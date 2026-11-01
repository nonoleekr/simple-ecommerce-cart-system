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

Products, users, and orders are stored in local `.txt` files. Shopping carts
remain in memory for the current application session — **no database required**.

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

The ongoing Cartiva modernization adds a separate Spring Boot REST API with
PostgreSQL, Flyway migrations, health endpoints, and generated OpenAPI
documentation under [`backend/`](backend/). The original Swing application is
kept intact as the legacy assignment baseline.

### Cartiva backend development

Requirements: JDK 25, Maven 3.6.3+, and Docker with Docker Compose.

```bash
docker compose up -d postgres
mvn -f backend/pom.xml spring-boot:run
```

Once started:

- API metadata: `http://localhost:8080/api/v1/system`
- Health check: `http://localhost:8080/actuator/health`
- API documentation: `http://localhost:8080/swagger-ui.html`

Database configuration can be overridden with `CARTIVA_DB_URL`,
`CARTIVA_DB_USERNAME`, and `CARTIVA_DB_PASSWORD`. The checked-in defaults are
for local development only.

---

## 🚀 Setup & Installation

1. **Install JDK 25 (latest LTS)**
   Make sure Java 25 or a newer compatible JDK is installed on your system.

2. **Clone the Repository**  
   ```bash
   git clone https://github.com/your-username/simple-ecommerce-cart-system.git
   cd simple-ecommerce-cart-system

3. **Create Local Data Files**

   Create a `data` directory containing `products.txt`, `users.txt`, and
   `orders.txt`. These runtime files are intentionally ignored by Git. Product
   records use this format:

   ```text
   P001,Keyboard,25.00,10
   ```

4. **Build and Test**

   ```bash
   mvn clean test
   ```

5. **Run the Application**

   ```bash
   mvn package
   java -cp target/classes App
   ```

---

## 📊 Performance Analysis

The system includes a separate educational timing utility that uses
`System.nanoTime()` to measure the time it takes to:

* Add 100 and 150 items to the cart
* Remove 100 and 150 items from the cart

These results are discussed in the final project report, with comparisons to theoretical Big O complexities.

Run the cart timings separately from the desktop application:

```bash
java -cp target/classes benchmark.PerformanceBenchmarks
```

The legacy user timing also rewrites `data/users.txt`, so it is disabled by
default. Run it only with disposable data by adding
`--include-user-persistence`.

> `Cart.addItem()` searches for an existing product before inserting. Its
> worst-case complexity is therefore O(n), even though insertion at the head
> is O(1). Adding n distinct products with the current implementation is O(n²).

---

## 👥 Contributors

* \[Your Name]
* \[Group Member 2's Name]
* \[Group Member 3's Name]
* \[Group Member 4's Name]

---

## 📁 Directory Structure (Simplified)

```
Simple E-Commerce Cart System/
├── Src/                  # Contains all Java source code for the application.
│   ├── Model/            # Defines the core data entities and business logic of the application.
│   │   ├── Cart.java             # Manages the user's shopping cart using a Linked List.
│   │   ├── CartItem.java         # Represents an individual product item within the shopping cart.
│   │   ├── Order.java            # Defines the structure for a customer's order.
│   │   ├── OrderManager.java     # Handles order creation, storage, and retrieval operations.
│   │   ├── OrderQueue.java       # Implements the queue for processing customer orders.
│   │   ├── Product.java          # Represents a product available in the e-commerce system.
│   │   ├── User.java             # Represents a system user with login credentials.
│   │   └── UserManager.java      # Manages user registration, authentication, and retrieval.
│   ├── Ui/                       # Contains Java Swing classes for the graphical user interface.
│   │   ├── LoginFrame.java       # Handles user login interface and authentication flow.
│   │   └── MainFrame.java        # Handles the main application window and user interactions.
│   └── App.java                  # The main entry point for the application.
└── Data/                         # Stores persistent application data in text files.
    ├── Orders.txt                # Stores records of processed customer orders.
    ├── Products.txt              # Contains the list of available products.
    └── Users.txt                 # Stores user-related information.

```

---

## 🏛️ Educational Use

This system is developed strictly for educational purposes under the **Data Structures & Algorithms** course and is not intended for production or commercial use.

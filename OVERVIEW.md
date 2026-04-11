# Adaptive Intelligent Cache Management System (AICS)

This project is an **Adaptive Intelligent Cache Management System (AICS)**. It is a full-stack application (Next.js frontend + Spring Boot backend) designed to visualize how advanced computer caches can use AI and mathematical models to optimize performance in real-time.

---

## 🧠 Core Project Logic
The "brain" (the backend) uses three main concepts to optimize data handling:

*   **Hybrid Eviction**: Instead of just using LRU (Least Recently Used) or LFU (Least Frequently Used), it calculates a dynamic score based on both.
*   **Entropy Adaptation**: It monitors how "random" the incoming traffic is.
    *   **Random Traffic** -> System favors recency (LRU).
    *   **Repetitive Traffic** -> System favors frequency (LFU).
*   **Markov Predictions**: It analyzes sequences of requests and uses Markov Chains to **pre-fetch** data into the cache before it is even requested.

---

## 🖥️ The 6 Visual Panels (Dashboard Functionality)

### 1. Panel A: Request Stream
*   **What it does:** Displays the incoming flow of data requests.
*   **What it displays:** A live count of total requests and a visual log of items (like A, B, C, D) being requested.
*   **Purpose:** Helps visualize the traffic pattern (random vs. predictable).

### 2. Panel B: Cache State (Hybrid)
*   **What it does:** Shows exactly what is currently stored in the cache.
*   **What it displays:** Current cache contents, size, and total evictions.
*   **Purpose:** Monitors the physical limits and efficiency of the cache memory.

### 3. Panel C: Decision Engine (The Brain)
*   **What it does:** Visualizes the mathematical weights the AI is using.
*   **What it displays:**
    *   **Entropy Score:** 0.00 (Predictable) to 1.00 (Random).
    *   **Alpha (α) / Beta (β) Sliders:** Weighting for LFU vs. LRU.
*   **Purpose:** Shows real-time adaptation of the eviction logic based on traffic behavior.

### 4. Panel D: Prediction
*   **What it does:** Displays the AI's predictions about future requests.
*   **What it displays:** Prediction Accuracy, Prefetch Hit Rate, and learned N-gram patterns.
*   **Purpose:** Visualizes the power of predictive pre-fetching in reducing wait times.

### 5. Panel E: Benchmark Graph
*   **What it does:** A real-time comparison "race."
*   **What it displays:** A line graph comparing **Pure LRU**, **Pure LFU**, and **AICS Hybrid**.
*   **Purpose:** Demonstrates the performance advantage of the intelligent hybrid approach.

### 6. Panel F: Controls
*   **What it does:** Allows you to manipulate the simulation.
*   **What it displays:** Speed slider, Traffic Mode selector (Random, Pattern Loop, Zipfian), and Start/Pause/Reset buttons.
*   **Purpose:** Allows for testing the system under different scenarios and stress levels.

---

## 🛠️ Technical Stack
*   **Backend:** Java (Spring Boot) using **WebSockets (STOMP)** for real-time data streaming.
*   **Frontend:** React (Next.js) with **TailwindCSS** for UI and **Recharts** for data visualization.
*   **Communication:** REST APIs for control commands and WebSockets for the live metrics stream.

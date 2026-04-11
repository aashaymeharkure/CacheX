# ADAPTIVE INTELLIGENT CACHE MANAGEMENT SYSTEM (AICS)

Welcome to the AICS application! This system simulates and visualizes how advanced computer caches work in real-time. 

If these concepts are new to you, this guide will explain everything you are seeing on the screen and how to use the application.

## 🧠 1. What is Caching?

A **Cache** is a small, ultra-fast memory layer used by computers to store frequently requested data. Because it is small, it gets full very quickly. When it gets full and new data needs to be stored, the cache has to "evict" (kick out) an old item. The rule it uses to decide *who* gets kicked out is called the **Eviction Policy**.

### Common Eviction Policies:
*   **LRU (Least Recently Used):** Kicks out the item that hasn't been accessed for the longest time. Good for "temporary" reading patterns.
*   **LFU (Least Frequently Used):** Kicks out the item that is requested the least amount of times overall. Good for "repetitive" reading patterns.

### The AICS Hybrid Approach:
Traditional caches only use LRU *or* LFU. AICS uses a **Hybrid Algorithm** that calculates a score using both:
*   **α (Alpha) weight:** How much importance to give to LFU (frequency).
*   **β (Beta) weight:** How much importance to give to LRU (recency).

The system monitors **Entropy** (how random the requests are). 
*   If traffic is very random, it shifts to LRU mode (increases Beta).
*   If traffic is highly repetitive, it shifts to LFU mode (increases Alpha).

### Markov Prediction:
Instead of just waiting for data to be requested, AICS looks at patterns (e.g., `A -> B -> C`) and uses mathematical **Markov Chains** to *guess* what you will request next. If it is confident, it **Prefetches** the data before you even ask for it!

---

## 🖥️ 2. How to Use the Dashboard

The dashboard is split into 6 visual panels:

*   **Panel A (Request Stream):** Shows the incoming "traffic" (web requests). Think of this as users asking for items (A, B, C).
*   **Panel B (Cache State):** Shows your current Hybrid Cache and how many items have been evicted across the lifecycle.
*   **Panel C (Decision Engine):** The brain! Watch the alpha (LFU) and beta (LRU) sliders adapt automatically when the traffic changes from random to repetitive.
*   **Panel D (Prediction):** Shows you the patterns the AI is recognizing (N-grams) and its success rate at pre-fetching data.
*   **Panel E (Benchmark Graph):** A live race between 3 isolated caches running at the same time: Pure LRU, Pure LFU, and AICS Hybrid. The higher the line, the better the cache is performing (Hit Rate).
*   **Panel F (Controls):** Adjust the speed, change the traffic pattern mode (Random vs. Pattern Loop), and press **START / PAUSE / RESET**.

---

## 🚀 3. How to Run the Application

Your buttons likely weren't working because the **Backend Server** (which processes the math) was not running! You need to run both the Backend (Java) and Frontend (Next.js) at the same time.

### Step 1: Start the Backend (Java)
Open a terminal, navigate to the `backend` folder, and compile/run the Spring Boot server. If you don't have Maven (`mvn`), you must install it first.

```bash
# 1. Install Java and Maven (if not already installed on your Linux machine)
sudo apt update
sudo apt install maven openjdk-17-jdk

# 2. Go to the backend folder
cd backend

# 3. Start the server
mvn spring-boot:run
```
*(Wait until you see "Started BackendApplication" in the terminal logs).*

### Step 2: Start the Frontend (Node.js/Next.js)
Open a **new** separate terminal and start the web interface.

```bash
# 1. Go to the frontend folder
cd frontend

# 2. Install dependencies
npm install

# 3. Start up Next.js
npm run dev
```

Now, open your browser and go to `http://localhost:3000`. When you click **START**, the frontend will talk to the backend, and you will see the charts and data start flying!

---

## 🛠️ Troubleshooting

**"I click START but nothing happens!"**
This means the React frontend cannot connect to the Java backend over port 8080.
1. Check your terminal running Java `mvn spring-boot:run`. Did it crash? Is it running?
2. If it failed to compile, ensure you installed JDK 17 with `sudo apt install openjdk-17-jdk`.
3. (Optional) Check the browser console (F12 -> Console) to see if you have "Connection Refused" errors. We have added alerts to the UI so it will explicitly tell you if the backend is down now!# CacheX

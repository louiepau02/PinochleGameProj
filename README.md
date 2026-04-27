# 🃏 Pinochle Game (JavaScript)

A command-line or console-based implementation of the classic **Pinochle card game**, built in JavaScript with an emphasis on game logic, modular design, and AI decision-making.

---

## 📖 Overview

This project is a full simulation of the Pinochle card game written in JavaScript. It supports multiple players (human and computer-controlled) and implements core gameplay mechanics including dealing, bidding, meld evaluation, and trick-taking.

A key focus of the project is building **rule-based AI players** that make strategic decisions based on hand strength and game state rather than random actions.

---

## 🎮 Live Demo

👉 https://drive.google.com/file/d/1L0cOeSVj5zkD8GhXtoeC5jV2mVMs8A38/view?usp=drive_link

## ✨ Features

* 🧠 **AI Bidding System**

    * Evaluates hand strength using meld potential and high-value cards (Aces, 10s, Kings)
    * Makes intelligent bid/pass decisions

* 🎯 **Trick-Taking Logic**

    * Computer players select cards strategically based on current trick state

* 🃏 **Meld Detection & Scoring**

    * Automatically identifies valid meld combinations and calculates scores

* 👥 **Human + AI Players**

    * Supports mixed gameplay between user and computer opponents

* 🧩 **Modular Design**

    * Clean separation of game engine, player logic, and rule systems

---

## 🛠️ Tech Stack

* **Language:** JavaScript (Node.js or browser-based depending on setup)
* **Concepts:** Object-Oriented Design / Functional Modules, Game Logic, AI Heuristics
* **Runtime:** Node.js (if CLI-based)

---

## 🚀 How to Run

### 1. Clone the repository

```bash id="js1"
git clone https://github.com/louiepau02/PinochleGameProj.git
cd PinochleGameProj
```

### 2. Install dependencies (if applicable)

```bash id="js2"
npm install
```

### 3. Run the game

```bash id="js3"
node main.js
```

> ⚠️ Update `main.js` if your entry file has a different name.

---

## 🎮 Gameplay Flow

* Cards are shuffled and dealt to all players
* Players enter the **bidding phase**, where AI evaluates hand strength
* Highest bidder selects trump suit
* Players proceed through **trick-taking rounds**
* Scores are calculated based on melds and tricks won

---

## 🧩 Project Structure

```id="js4"
.
├── src/              # Game logic modules
├── players/          # Human and AI player logic
├── game/             # Core game engine
├── utils/            # Helper functions
├── main.js           # Entry point
```

---

## 🧠 Key Concepts Implemented

* Rule-based AI decision making
* State management across game rounds
* Modular JavaScript architecture
* Card game logic (bidding, trick resolution, scoring)
* Event-driven or turn-based flow control

---

## 📚 What I Learned

* Designing modular JavaScript applications
* Implementing AI heuristics for decision-making
* Managing complex game state in a turn-based system
* Structuring scalable game logic without frameworks

---

## ⚠️ Notes

This project was originally developed as part of a university assignment and later refined for personal portfolio use.

---

## 📌 Future Improvements

* Improve AI strategy using probability-based decisions
* Improve cut-throat mode - a simpler version
* Refactor into TypeScript for better type safety
* Add multiplayer support (online or local)



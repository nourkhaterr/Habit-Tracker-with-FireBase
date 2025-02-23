package com.example.habitotracker;

public class Habit {
    private String name;
    private int streak;
    private int completed; // 1 if completed, 0 if not
    private String lastUpdated;

    // Constructor
    public Habit(String name, int streak, int completed, String lastUpdated) {
        this.name = name;
        this.streak = streak;
        this.completed = completed;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}


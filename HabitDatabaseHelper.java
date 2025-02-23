package com.example.habitotracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HabitDatabaseHelper {
    private SQLiteDatabase myDb;

    public HabitDatabaseHelper(Context context) {
        myDb = context.openOrCreateDatabase("HabitsTracker", Context.MODE_PRIVATE, null);
        myDb.execSQL("CREATE TABLE IF NOT EXISTS habits (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "streak INTEGER DEFAULT 0, " +
                "completed INTEGER DEFAULT 0, " +
                "last_updated TEXT)");
    }

    public void addHabit(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("streak", 0);
        values.put("completed", 0);
        values.put("last_updated", "");
        myDb.insert("habits", null, values);
    }

    public ArrayList<String> getAllHabits() {
        ArrayList<String> habits = new ArrayList<>();
        Cursor cursor = myDb.rawQuery("SELECT name FROM habits", null);
        if (cursor.moveToFirst()) {
            do {
                habits.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return habits;
    }

    // Method to mark a habit as completed and update streak
    public void markHabitCompleted(String name) {
        String currentDate = getCurrentDate();
        Cursor cursor = myDb.rawQuery("SELECT streak, last_updated FROM habits WHERE name = ?", new String[]{name});

        if (cursor.moveToFirst()) {
            // Check if the column indexes are valid
            int streakIndex = cursor.getColumnIndex("streak");
            int lastUpdatedIndex = cursor.getColumnIndex("last_updated");

            if (streakIndex >= 0 && lastUpdatedIndex >= 0) {
                int streak = cursor.getInt(streakIndex);
                String lastUpdated = cursor.getString(lastUpdatedIndex);

                // If it's a new day and the habit was completed yesterday, increment streak
                if (isConsecutiveDays(currentDate, lastUpdated)) {
                    streak++;
                } else {
                    streak = 1;  // Reset streak if not consecutive
                }

                // Update the habit completion status, streak, and last updated date
                ContentValues values = new ContentValues();
                values.put("completed", 1);
                values.put("streak", streak);
                values.put("last_updated", currentDate);
                myDb.update("habits", values, "name = ?", new String[]{name});
            } else {
                // Handle the case where column indexes are invalid
                // Optionally, you can log an error or set default values
                System.out.println("Invalid column indexes for streak or last_updated.");
            }
        }
        cursor.close();
    }


    // Method to reset daily completion
    public void resetDailyCompletion() {
        myDb.execSQL("UPDATE habits SET completed = 0");
    }

    // Method to delete a habit
    public void deleteHabit(String name) {
        myDb.delete("habits", "name = ?", new String[]{name});
    }

    // Method to clear all habits
    public void clearHabits() {
        myDb.execSQL("DELETE FROM habits");
    }

    // Method to get habit details
    public String viewHabitDetails(String habitName) {
        Cursor cursor = myDb.rawQuery("SELECT * FROM habits WHERE name = ?", new String[]{habitName});
        String details = "Habit not found";


        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int streakIndex = cursor.getColumnIndex("streak");
            int completedIndex = cursor.getColumnIndex("completed");
            int lastUpdatedIndex = cursor.getColumnIndex("last_updated");

            if (idIndex >= 0 && nameIndex >= 0 && streakIndex >= 0 && completedIndex >= 0 && lastUpdatedIndex >= 0) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int streak = cursor.getInt(streakIndex);
                int completed = cursor.getInt(completedIndex);
                String lastUpdated = cursor.getString(lastUpdatedIndex);

                details = "ID: " + id +
                        "\nName: " + name +
                        "\nStreak: " + streak +
                        "\nCompleted: " + completed +
                        "\nLast Updated: " + lastUpdated;
            }
        }
        cursor.close();
        return details;
    }

    // Helper method to get the current date as a String
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Helper method to check if the current date is the day after the last completed date
    private boolean isConsecutiveDays(String currentDate, String lastUpdated) {
        // If lastUpdated is empty (habit hasn't been completed yet), return false
        if (lastUpdated.isEmpty()) {
            return false;
        }

        // Parse both currentDate and lastUpdated to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date current = dateFormat.parse(currentDate);
            Date last = dateFormat.parse(lastUpdated);

            // Get the difference in days
            long diffInMillis = current.getTime() - last.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            // If the difference is exactly 1 day, return true
            return diffInDays == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
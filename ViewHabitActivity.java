package com.example.habitotracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewHabitActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> habitList;
    private String selectedHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);

        ListView habitListView = findViewById(R.id.habitListView);
        Button viewHabitDetailsButton = findViewById(R.id.viewHabitDetailsButton);
        Button deleteHabitButton = findViewById(R.id.deleteHabitButton);

        habitList = new ArrayList<>();

        // Use Firebase to fetch data once
        String userId = "kQ6PaeGeSZb9FWHlKDcZ1vsgpJs2"; // Replace with actual user ID dynamically if needed
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("habits");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    habitList.clear(); // Clear existing data

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String habit = snapshot.getKey(); // Get habit name (key)
                        habitList.add(habit);
                    }

                    adapter.notifyDataSetChanged(); // Update ListView
                } else {
                    Toast.makeText(ViewHabitActivity.this, "No habits found in Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewHabitActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up adapter for ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, habitList);
        habitListView.setAdapter(adapter);

        // Handle list item click to select habit
        habitListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            selectedHabit = habitList.get(position);
            Toast.makeText(this, "Selected: " + selectedHabit, Toast.LENGTH_SHORT).show();

            // Open a dialog to update habit details
            openUpdateHabitDialog(selectedHabit);
        });

        // Delete selected habit
        deleteHabitButton.setOnClickListener(v -> {
            if (selectedHabit != null) {
                int position = habitList.indexOf(selectedHabit);
                showDeleteConfirmation(selectedHabit, position);
            } else {
                Toast.makeText(this, "Please select a habit to delete.", Toast.LENGTH_SHORT).show();
            }
        });

        // View habit details
        viewHabitDetailsButton.setOnClickListener(v -> {
            if (selectedHabit != null) {
                Toast.makeText(this, "Please select a habit to view details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update habit dialog
    private void openUpdateHabitDialog(String habitName) {
        // Create the dialog with an EditText field for updating habit details
        final EditText editText = new EditText(ViewHabitActivity.this);
        editText.setText(habitName);  // Set the current habit name in the EditText

        new AlertDialog.Builder(this)
                .setTitle("Update Habit")
                .setMessage("Enter new details for your habit:")
                .setView(editText)  // Display EditText in the dialog
                .setPositiveButton("Update", (dialog, which) -> {
                    String newHabitName = editText.getText().toString().trim();
                    if (!newHabitName.isEmpty()) {
                        // Update the habit in Firebase
                        updateHabitInFirebase(habitName, newHabitName);
                    } else {
                        Toast.makeText(ViewHabitActivity.this, "Please enter a habit name.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Update habit in Firebase
    private void updateHabitInFirebase(String oldHabitName, String newHabitName) {
        String userId = "kQ6PaeGeSZb9FWHlKDcZ1vsgpJs2"; // Use the actual user ID dynamically
        DatabaseReference habitsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("habits");

        // Remove the old habit
        habitsRef.child(oldHabitName).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Add the new habit with the updated name
                habitsRef.child(newHabitName).setValue(true).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Success - Update the local list and adapter
                        habitList.remove(oldHabitName);  // Remove the old habit from the list
                        habitList.add(newHabitName);     // Add the new habit to the list
                        adapter.notifyDataSetChanged(); // Update ListView
                        Toast.makeText(ViewHabitActivity.this, "Habit updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewHabitActivity.this, "Failed to update habit", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ViewHabitActivity.this, "Failed to remove old habit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show delete confirmation dialog
    private void showDeleteConfirmation(String habitName, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete this habit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove habit from Firebase
                    String userId = "kQ6PaeGeSZb9FWHlKDcZ1vsgpJs2"; // Use the actual user ID dynamically
                    DatabaseReference habitsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("habits");
                    habitsRef.child(habitName).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Remove from local list and update adapter
                            habitList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ViewHabitActivity.this, "Habit deleted", Toast.LENGTH_SHORT).show();
                            selectedHabit = null;
                        } else {
                            Toast.makeText(ViewHabitActivity.this, "Failed to delete habit", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Show habit details
    private void showHabitDetails(String details) {
        new AlertDialog.Builder(this)
                .setTitle("Habit Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }
}

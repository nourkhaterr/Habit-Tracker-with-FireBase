
package com.example.habitotracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

            public class AddHabitActivity extends AppCompatActivity {
                private static final int MAX_HABITS = 3;
                private ListView habitListView;
                private ArrayList<String> selectedHabits = new ArrayList<>();
                private final String[] habits = {
                        "Exercise", "Meditation", "Reading", "Journaling", "Healthy Eating",
                        "Drinking 2L of Water", "Sleeping by 10pm", "Learning to Code"
                };

                private DatabaseReference databaseReference;
                private FirebaseUser currentUser;

                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_add_habit);

                    habitListView = findViewById(R.id.habitListView);
                    Button enterFocusModeButton = findViewById(R.id.enterFocusModeButton);

                    // Get current user
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser == null) {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity if user is not logged in
                        return;
                    }

                    // Firebase database reference
                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("habits");

                    // Setup ListView adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, habits);
                    habitListView.setAdapter(adapter);
                    habitListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                    // Load habits from Firebase
                    loadSavedHabits();

                    // Handle ListView item selection
                    habitListView.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedHabit = habits[position];
                        if (selectedHabits.contains(selectedHabit)) {
                            selectedHabits.remove(selectedHabit);
                        } else if (selectedHabits.size() < MAX_HABITS) {
                            selectedHabits.add(selectedHabit);
                        } else {
                            Toast.makeText(this, "You can only select up to " + MAX_HABITS + " habits.", Toast.LENGTH_SHORT).show();
                        }

                        if (selectedHabits.size() >= MAX_HABITS) {
                            disableUnselectedItems();
                        } else {
                            enableAllItems();
                        }
                    });

                    // Add Habit Button
                    findViewById(R.id.addHabitButton).setOnClickListener(v -> {
                        if (selectedHabits.isEmpty()) {
                            Toast.makeText(this, "Please select at least one habit.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        saveHabitsToFirebase();
                    });

                    // Enter Focus Mode Button
                    enterFocusModeButton.setOnClickListener(v -> {
                        Toast.makeText(this, "Entering Focus Mode...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddHabitActivity.this, FocusModeActivity.class);
                        startActivity(intent);
                    });
                }

                private void saveHabitsToFirebase() {
                    Map<String, Object> habitMap = new HashMap<>();
                    for (String habit : selectedHabits) {
                        habitMap.put(habit, true); // Storing habits as key-value pairs
                    }

                    databaseReference.setValue(habitMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddHabitActivity.this, "Habits saved successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddHabitActivity.this, ViewHabitActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(AddHabitActivity.this, "Failed to save habits: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                private void loadSavedHabits() {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            selectedHabits.clear();
                            for (DataSnapshot habitSnapshot : snapshot.getChildren()) {
                                selectedHabits.add(habitSnapshot.getKey()); // Get habit names
                            }

                            for (int i = 0; i < habits.length; i++) {
                                if (selectedHabits.contains(habits[i])) {
                                    habitListView.setItemChecked(i, true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddHabitActivity.this, "Failed to load habits", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                private void disableUnselectedItems() {
                    for (int i = 0; i < habitListView.getCount(); i++) {
                        if (!selectedHabits.contains(habits[i])) {
                            habitListView.setItemChecked(i, false);
                            if (habitListView.getChildAt(i) != null) {
                                habitListView.getChildAt(i).setEnabled(false);
                            }
                        }
                    }
                }

                private void enableAllItems() {
                    for (int i = 0; i < habitListView.getCount(); i++) {
                        if (habitListView.getChildAt(i) != null) {
                            habitListView.getChildAt(i).setEnabled(true);
                        }
                    }
                }
            }
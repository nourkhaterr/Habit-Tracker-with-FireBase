package com.example.habitotracker;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CheckHabitActivity extends Activity {

    private ListView habitCheckListView;
    private ArrayList<Habit> habitList;
    private HabitAdapter habitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_habit);

        // Initialize the ListView
        habitCheckListView = findViewById(R.id.habitCheckListView);

        // Initialize your habit data (example data)
        habitList = new ArrayList<>();
        habitList.add(new Habit("Exercise", 0));
        habitList.add(new Habit("Drink Water", 0));

        // Initialize the custom adapter
        habitAdapter = new HabitAdapter(this, habitList);
        habitCheckListView.setAdapter(habitAdapter);
    }

    // Define the Habit class (model for habit data)
    public class Habit {
        private String name;
        private int streak;

        public Habit(String name, int streak) {
            this.name = name;
            this.streak = streak;
        }

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
    }

    // Custom adapter for the ListView
    private class HabitAdapter extends ArrayAdapter<Habit> {

        public HabitAdapter(CheckHabitActivity context, ArrayList<Habit> habits) {
            super(context, 0, habits);  // Initialize the adapter with context and habit data
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Reuse or create new view for each habit item
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_habit, parent, false);
            }

            // Get the current habit at the given position
            Habit currentHabit = getItem(position);

            // Bind data to the views in the layout
            TextView habitNameTextView = convertView.findViewById(R.id.habitNameTextView);
            TextView habitStreakTextView = convertView.findViewById(R.id.habitStreakTextView);
            Button markDoneButton = convertView.findViewById(R.id.markDoneButton);

            habitNameTextView.setText(currentHabit.getName());
            habitStreakTextView.setText("Streak: " + currentHabit.getStreak());

            // Handle button click: mark habit as completed and increase streak
            markDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Increment streak when clicked
                    currentHabit.setStreak(currentHabit.getStreak() + 1);
                    habitStreakTextView.setText("Streak: " + currentHabit.getStreak());
                    Toast.makeText(CheckHabitActivity.this, currentHabit.getName() + " completed!", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}
package com.example.habitotracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;  // Declare FirebaseAnalytics
    private TextView quoteText;
    private Button QuoteButton;
    private ImageButton addHabitButton, checkHabitButton, viewHabitButton;

    private String[] quotes = {
            "The only limit to our realization of tomorrow is our doubts of today. – Franklin D. Roosevelt",
            "Do not wait to strike till the iron is hot, but make it hot by striking. – William Butler Yeats",
            "It does not matter how slowly you go as long as you do not stop. – Confucius",
            "You don't have to be great to start, but you have to start to be great. – Zig Ziglar",
            "The best way to predict the future is to create it. – Abraham Lincoln",
            "Small daily improvements over time lead to stunning results. – Robin Sharma",
            "The distance between who I am and who I want to be is only separated by what I do. – Unknown",
            "Don’t wait for opportunity. Create it. – Unknown",
            "The secret of getting ahead is getting started. – Mark Twain",
            "You are never too old to set another goal or to dream a new dream. – C.S. Lewis",
            "What we fear of doing most is usually what we most need to do. – Ralph Waldo Emerson",
            "Your habits will determine your future. – Jack Canfield",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Write a message to the database
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

  
        // Initialize FirebaseAnalytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Log an event when the activity is created
        firebaseAnalytics.logEvent("activity_started", null);

        // Enable Edge to Edge
        EdgeToEdge.enable(this);

        // Set up the window insets listener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        QuoteButton = findViewById(R.id.QuoteButton);
        quoteText = findViewById(R.id.quoteText);
        addHabitButton = findViewById(R.id.addHabitButton);
        checkHabitButton = findViewById(R.id.checkHabitButton);
        viewHabitButton = findViewById(R.id.viewHabitButton);

        // Set onClickListeners for buttons and log events when clicked
        addHabitButton.setOnClickListener(v -> {
            logFirebaseEvent("add_habit_button_clicked");
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Close MainActivity
        });


        checkHabitButton.setOnClickListener(v -> {
            logFirebaseEvent("check_habit_button_clicked");
            Intent intent = new Intent(MainActivity.this, CheckHabitActivity.class);
            startActivity(intent);
        });

        viewHabitButton.setOnClickListener(v -> {
            logFirebaseEvent("view_habit_button_clicked");
            Intent intent = new Intent(MainActivity.this, ViewHabitActivity.class);
            startActivity(intent);
        });

        QuoteButton.setOnClickListener(v -> {
            // Show a random quote when the button is clicked
            int randomIndex = (int) (Math.random() * quotes.length);
            quoteText.setText(quotes[randomIndex]);

            // Log Firebase event when a quote is shown
            logFirebaseEvent("quote_shown", "quote", quotes[randomIndex]);
        });
    }

    // Helper method to log Firebase events
    private void logFirebaseEvent(String eventName) {
        firebaseAnalytics.logEvent(eventName, null);
    }

    // Overloaded method to log Firebase events with parameters
    private void logFirebaseEvent(String eventName, String paramName, String paramValue) {
        Bundle bundle = new Bundle();
        bundle.putString(paramName, paramValue);
        firebaseAnalytics.logEvent(eventName, bundle);
    }
}

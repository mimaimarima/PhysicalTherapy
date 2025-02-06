package com.example.physical_therapy;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExerciseDetails extends AppCompatActivity {

    ImageView exerciseIV;
    String exerciseName;
    TextView exerciseNameTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_details);

        exerciseIV = findViewById(R.id.exerciseIV);
        exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        exerciseNameTV = findViewById(R.id.exerciseNameTV);
        exerciseNameTV.setText(exerciseName);
        String imageName = exerciseName.toLowerCase().replace(" ", "_");
        int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (resID != 0) {
            exerciseIV.setImageResource(resID);
        } else {
            exerciseIV.setImageResource(R.drawable.logogo); // Fallback image if not found
        }
    }
}
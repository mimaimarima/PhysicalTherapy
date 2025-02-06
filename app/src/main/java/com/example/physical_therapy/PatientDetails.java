package com.example.physical_therapy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientDetails extends AppCompatActivity {

    TextView name, age, bmi, diagnosis, pain, mood;
    FirebaseDatabase db;
    private DatabaseReference dbref;
    Button editEP, viewEP;
    String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_details);

        initViews();
        db = FirebaseDatabase.getInstance("https://physther-b19d2-default-rtdb.europe-west1.firebasedatabase.app/");
        retrieveData();

        editEP.setOnClickListener(v->
        {
            showAddExerciseDialog();
        });

        viewEP.setOnClickListener(v->
        {
            Intent intent = new Intent(PatientDetails.this, PatientCalendar.class);
            intent.putExtra("PATIENT_ID", patientId);
            startActivity(intent);
        });
    }
    private void initViews() {

        patientId = getIntent().getStringExtra("PATIENT_ID");

        if (patientId == null || patientId.isEmpty()) {
            Toast.makeText(this, "Invalid patient ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        name = findViewById(R.id.nameTV);
        age = findViewById(R.id.ageTV);
        bmi = findViewById(R.id.bmiTV);
        diagnosis = findViewById(R.id.diagnosisTV);
        pain = findViewById(R.id.painTV);
        mood = findViewById(R.id.moodTV);
        editEP = findViewById(R.id.addExerciseButton);
        viewEP = findViewById(R.id.viewPlanButton);
    }
    private void retrieveData() {

        dbref = db.getReference("users").child(patientId);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String nameValue = snapshot.child("name").getValue(String.class);
                    String ageValue = snapshot.child("age").getValue(String.class);
                    String bmiValue = snapshot.child("bmi").getValue(String.class);
                    String diagnosisValue = snapshot.child("diagnosis").getValue(String.class);
                    int painValue = snapshot.child("pain").getValue(Integer.class);
                    int moodValue = snapshot.child("mood").getValue(Integer.class);

                    name.setText("Name: " + nameValue);
                    age.setText("Age: " + ageValue);
                    bmi.setText("BMI: " + bmiValue);
                    diagnosis.setText("Diagnosis: " + diagnosisValue);
                    pain.setText("Pain: " + painValue);
                    mood.setText("Mood: " + moodValue);
                } else {
                    Toast.makeText(getApplicationContext(), "Patient not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showAddExerciseDialog() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .setTheme(R.style.CustomMaterialDateRangePicker)
                .build();

        dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;

                showExerciseDialogForRange(startDateMillis, endDateMillis);
            }
        });
    }

    private void showExerciseDialogForRange(long startDateMillis, long endDateMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Add Exercise");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        Spinner exerciseSpinner = new Spinner(this);
        layout.addView(exerciseSpinner);

        final EditText durationInput = new EditText(this);
        durationInput.setHint("Enter duration");
        layout.addView(durationInput);

        builder.setView(layout);

        DatabaseReference exerciseRef = db.getReference("exer");
        List<String> exerciseList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);

        exerciseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String exercise = snapshot.getValue(String.class);
                    if (exercise != null) {
                        exerciseList.add(exercise);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to fetch exercises", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String selectedExercise = exerciseSpinner.getSelectedItem().toString();
            String durationStr = durationInput.getText().toString().trim();

            if (selectedExercise.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Please select an exercise and enter duration", Toast.LENGTH_SHORT).show();
                return;
            }

            addExerciseToDateRange(startDateMillis, endDateMillis, selectedExercise, durationStr);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addExerciseToDateRange(long startDateMillis, long endDateMillis, String exerciseName, String duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDateMillis);

        while (calendar.getTimeInMillis() <= endDateMillis) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            dbref = db.getReference("users").child(patientId).child("exercises");
            DatabaseReference dateRef = dbref.child(currentDate).child(exerciseName);
            Map<String, Object> exerciseData = new HashMap<>();
            exerciseData.put("duration", duration);
            exerciseData.put("done", 0);

            dateRef.setValue(exerciseData)
                    .addOnSuccessListener(aVoid -> Log.d("ExerciseSave", "Added to " + currentDate))
                    .addOnFailureListener(e -> Log.e("ExerciseSave", "Failed to add exercise for " + currentDate, e));

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Toast.makeText(this, "Exercise added to the selected range", Toast.LENGTH_SHORT).show();
    }
}

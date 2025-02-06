package com.example.physical_therapy;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatientCalendar extends AppCompatActivity {
    private String patientId;
    FirebaseDatabase db;
    DatabaseReference dbRef, dbRef2;
    String date;
    private RecyclerView exercisesRV;
    Button pmb;
    TextView quote;
    private FirebaseRecyclerAdapter<String, PatientCalendar.ExerciseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_calendar);
        initViews();
        setQuote();
        setPainMoodButton();
        db = FirebaseDatabase.getInstance("https://physther-b19d2-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    private void initViews() {
        patientId = getIntent().getStringExtra("PATIENT_ID");
        quote = findViewById(R.id.quote);
        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView day = findViewById(R.id.day);
        pmb = findViewById(R.id.pmB);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                java.util.Calendar calendar = eventDay.getCalendar();
                int year = calendar.get(java.util.Calendar.YEAR);
                int month = calendar.get(java.util.Calendar.MONTH) + 1;
                int dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                String daay;
                if (dayOfMonth < 9)
                {
                    daay = "0" + String.valueOf(dayOfMonth);
                }
                else
                {
                    daay = String.valueOf(dayOfMonth);
                }
                if (month > 9){
                    date = year + "-" + month + "-" + daay; }
                else {
                    date = year + "-0" + month + "-" + daay;
                }
                day.setText("Exercise plan for " + date);

                dbRef = db.getReference("users").child(patientId).child("exercises").child(date);

                setupRecyclerView();
            }
        });

        exercisesRV = findViewById(R.id.exercisesRV);
        exercisesRV.setLayoutManager(new LinearLayoutManager(this));
        exercisesRV.setItemAnimator(null);
        if (exercisesRV.getAdapter() != null) {
            exercisesRV.swapAdapter(null, false);
        }
    }

    private void setPainMoodButton() {
        int check = getIntent().getIntExtra("check", 0);
        if (check == 1)
        {
            pmb.setVisibility(View.VISIBLE);
        }
        else
        {
            pmb.setVisibility(View.GONE);
        }
        pmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoodPainDialog();
            }
        });
    }

    private void setQuote() {
        Random random = new Random();
        int randomQuoteIndex = random.nextInt(16);
        String quoteRes = "quote_" + randomQuoteIndex;

        int quoteResId = getResources().getIdentifier(quoteRes, "string", getPackageName());

        if (quoteResId != 0) {
            String randomQuote = getString(quoteResId);
            quote.setText(randomQuote);
        } else {

            quote.setText("No quote found!");
            Log.e("setQuote", "Quote resource not found for: " + quoteRes);
        }
    }

    private void setupRecyclerView(){
        if (adapter == null)
        {
            FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                    .setQuery(dbRef, snapshot -> snapshot.getKey())
                    .build();

            adapter = new FirebaseRecyclerAdapter<String, PatientCalendar.ExerciseViewHolder>(options) {
                @NonNull
                @Override
                public PatientCalendar.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
                    return new PatientCalendar.ExerciseViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull PatientCalendar.ExerciseViewHolder holder, int position, @NonNull String exercise) {
                    bindExercise(holder, exercise);
                }
                public void onDataChanged() {
                    super.onDataChanged();
                    exercisesRV.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);
                }
            };
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                exercisesRV.setAdapter(adapter);
            }, 200);
            adapter.startListening();
        }
        else
        {
            adapter.updateOptions(new FirebaseRecyclerOptions.Builder<String>()
                    .setQuery(dbRef, snapshot -> snapshot.getKey())
                    .build());
        }
    }
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDuration;
        CheckBox checkBox;
        ImageButton view;
        ImageButton edit;
        ImageButton delete;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseNameTV);
            exerciseDuration = itemView.findViewById(R.id.exerciseDurationTV);
            checkBox = itemView.findViewById(R.id.checkBox);
            view = itemView.findViewById(R.id.exerciseIB);
            edit = itemView.findViewById(R.id.editIB);
            delete = itemView.findViewById(R.id.deleteIB);
        }
    }
    private void bindExercise(PatientCalendar.ExerciseViewHolder holder, String exerciseName) {
        DatabaseReference exerRef = dbRef.child(exerciseName);
        exerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                if (snapshot.exists()) {
                    String duration = snapshot.child("duration").getValue(String.class);
                    Long done = snapshot.child("done").getValue(Long.class);

                    if (!isDestroyed()) {
                        holder.exerciseName.setText(exerciseName);
                        holder.exerciseDuration.setText(duration != null ? duration : "No duration available");
                        int check = getIntent().getIntExtra("check", 0);
                        if (check == 1) {
                            holder.checkBox.setVisibility(View.VISIBLE);
                            holder.delete.setVisibility(View.GONE);
                            holder.edit.setVisibility(View.GONE);
                        holder.checkBox.setOnCheckedChangeListener(null); // Clear any existing listener to prevent triggering it
                        holder.checkBox.setChecked(done != null && done == 1); // Check if 'done' is 1
                        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                holder.checkBox.setChecked(isChecked); // Immediately reflect UI change without waiting for Firebase
                                exerRef.child("done").setValue(isChecked ? 1 : 0).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("Firebase", "Done field updated successfully");
                                    } else {
                                        Log.e("Firebase", "Error updating done field", task.getException());
                                    }
                                });

                            }
                        });}
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ExerciseDetails.class);
                                intent.putExtra("EXERCISE_NAME", exerciseName);
                                startActivity(intent);
                            }
                        });
                        if (check == 0)
                        {
                            holder.checkBox.setVisibility(View.GONE);
                            holder.delete.setVisibility(View.VISIBLE);
                            holder.edit.setVisibility(View.VISIBLE);

                            holder.edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showEditDialog(holder, exerRef);
                                }
                            });
                            holder.delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDeleteDialog(holder, exerRef);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                if (!isDestroyed()) {
                    holder.exerciseDuration.setText("");
                    holder.checkBox.setChecked(false);
                }
            }
        });
    }
    private void showEditDialog(PatientCalendar.ExerciseViewHolder holder, DatabaseReference exerRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(), R.style.CustomAlertDialog);
        builder.setTitle("Edit Duration");

        final EditText input = new EditText(holder.itemView.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDurationStr = input.getText().toString().trim();
                if (!newDurationStr.isEmpty()) {
                    exerRef.child("duration").setValue(newDurationStr).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            holder.exerciseDuration.setText(newDurationStr);
                            Log.d("Firebase", "Duration updated successfully");
                        } else {
                            Log.e("Firebase", "Error updating duration", task.getException());
                        }
                    });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void showDeleteDialog(PatientCalendar.ExerciseViewHolder holder, DatabaseReference exerRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(), R.style.CustomAlertDialog);
        builder.setTitle("Delete Exercise");
        builder.setMessage("Are you sure you want to delete this exercise?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exerRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Exercise deleted successfully");
                    } else {
                        Log.e("Firebase", "Error deleting exercise", task.getException());
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
    protected void onPause() {
        super.onPause();
        exercisesRV.getRecycledViewPool().clear();
    }
    @Override
    protected void onDestroy() {
        clearRecyclerViewPool();

        super.onDestroy();
    }
    // Recycler View refresh
    public void onBackPressed() {
        clearRecyclerViewPool();
        super.onBackPressed();
    }private void clearRecyclerViewPool() {
        if (exercisesRV != null && exercisesRV.getRecycledViewPool() != null) {
            exercisesRV.getRecycledViewPool().clear();
        }
    }

    private void showMoodPainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Update Mood and Pain Levels, 1 low, 5 high");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        TextView moodLabel = new TextView(this);
        moodLabel.setText("Mood:");
        moodLabel.setPadding(0, 0, 0, 10);
        layout.addView(moodLabel);

        final Spinner moodSpinner = new Spinner(this);
        ArrayAdapter<Integer> moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getNumberList());
        moodSpinner.setAdapter(moodAdapter);
        layout.addView(moodSpinner);

        TextView painLabel = new TextView(this);
        painLabel.setText("Pain:");
        painLabel.setPadding(0, 20, 0, 10); // Space between the labels
        layout.addView(painLabel);



        final Spinner painSpinner = new Spinner(this);
        ArrayAdapter<Integer> painAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getNumberList());
        painSpinner.setAdapter(painAdapter);
        layout.addView(painSpinner);

        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mood = (int) moodSpinner.getSelectedItem();
                int pain = (int) painSpinner.getSelectedItem();
                saveToFirebase(mood, pain);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }

    private List<Integer> getNumberList() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            numbers.add(i);
        }
        return numbers;
    }
    private void saveToFirebase(int mood, int pain) {
        dbRef2 = db.getReference("users").child(patientId);
        dbRef2.child("mood").setValue(mood);
        dbRef2.child("pain").setValue(pain).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatientCalendar.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PatientCalendar.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}



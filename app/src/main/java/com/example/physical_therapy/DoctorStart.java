package com.example.physical_therapy;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DoctorStart extends AppCompatActivity {
    TextView hello;
    private Button addPatientB;
    private RecyclerView patientRV;
    private FirebaseAuth auth;
    private DatabaseReference patientsRef;
    private FirebaseRecyclerAdapter<String, PatientViewHolder> adapter;
    public FirebaseDatabase db;
    private WeakReference<Context> contextRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextRef = new WeakReference<>(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_start);
        hello = findViewById(R.id.helloDoctor);
        String name = getIntent().getStringExtra("name");

        hello.setText("Hello " + name);

        db = FirebaseDatabase.getInstance("https://physther-b19d2-default-rtdb.europe-west1.firebasedatabase.app/");

        auth = FirebaseAuth.getInstance();
        patientRV = findViewById(R.id.patientsRV);
        patientRV.setLayoutManager(new LinearLayoutManager(this));
        patientRV.setItemAnimator(null);

        addPatientB = findViewById(R.id.addPatientB);
        addPatientB.setOnClickListener(v -> showSavePatientDialog());

        if (patientRV.getAdapter() != null) {
            patientRV.swapAdapter(null, false);
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();
        patientsRef = db
                .getReference("users")
                .child(currentUserId)
                .child("assignedPatients");

        if (adapter == null) {
            FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                    .setQuery(patientsRef, snapshot -> snapshot.getKey())
                    .build();

            adapter = new FirebaseRecyclerAdapter<String, PatientViewHolder>(options) {
                @NonNull
                @Override
                public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
                    return new PatientViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull PatientViewHolder holder, int position, @NonNull String patientId) {
                    bindPatient(holder, patientId);
                }
                public void onDataChanged() {
                    super.onDataChanged();
                    patientRV.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);
                }
            };
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                patientRV.setAdapter(adapter);
            }, 200);
            adapter.startListening();
        }else {
            adapter.updateOptions(new FirebaseRecyclerOptions.Builder<String>()
                    .setQuery(patientsRef, snapshot -> snapshot.getKey())
                    .build());
        }
    }
    private void bindPatient(PatientViewHolder holder, String patientId) {
        DatabaseReference patientRef = db.getReference("users").child(patientId);
        patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String diagnosis = snapshot.child("diagnosis").getValue(String.class);

                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(holder.itemView.getContext(), PatientDetails.class);
                        intent.putExtra("PATIENT_ID", patientId);
                        holder.itemView.getContext().startActivity(intent);
                    });
                    if (!isDestroyed()) {
                        holder.patientName.setText(name != null ? name : "Unknown");
                        holder.patientDiagnosis.setText(diagnosis != null ? diagnosis : "No diagnosis available");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                if (!isDestroyed()) {
                    holder.patientName.setText("Error loading name");
                    holder.patientDiagnosis.setText("");
                }
            }
        });
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
        patientRV.getRecycledViewPool().clear();  // Recycler View refresh
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
        if (patientRV != null && patientRV.getRecycledViewPool() != null) {
            patientRV.getRecycledViewPool().clear();
        }
    }


    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientDiagnosis;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientNameTV);
            patientDiagnosis = itemView.findViewById(R.id.patientDiagnosisTV);
        }
    }

    private void showSavePatientDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_patient, null);

        EditText patientEmail = dialogView.findViewById(R.id.patientEmail);
        EditText patientPassword = dialogView.findViewById(R.id.patientPassword);
        EditText patientName = dialogView.findViewById(R.id.patientName);
        EditText patientAge = dialogView.findViewById(R.id.patientAge);
        EditText patientBMI = dialogView.findViewById(R.id.patientBMI);
        EditText patientDiagnosis = dialogView.findViewById(R.id.patientDiagnosis);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(dialogView)
                .setTitle("Add New Patient")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#659798")));
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.BLACK);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.BLACK);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = patientEmail.getText().toString().trim();
            String password = patientPassword.getText().toString().trim();
            String name = patientName.getText().toString().trim();
            String age = patientAge.getText().toString().trim();
            String bmi = patientBMI.getText().toString().trim();
            String diagnosis = patientDiagnosis.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty() || bmi.isEmpty() || diagnosis.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            savePatient(email, password, name, age, bmi, diagnosis, dialog);
        });
    }

    private void savePatient(String email, String password, String name, String age, String bmi, String diagnosis, AlertDialog dialog) {
        String currentUserId = auth.getCurrentUser().getUid();
        DatabaseReference userRoleRef = db.getReference().child("users").child(currentUserId).child("role");

        userRoleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    if ("admin".equals(role)) {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();

                                        DatabaseReference userRef = db.getReference().child("users").child(uid);
                                        Map<String, Object> patientData = new HashMap<>();
                                        patientData.put("name", name);
                                        patientData.put("age", age);
                                        patientData.put("email", email);
                                        patientData.put("bmi", bmi);
                                        patientData.put("diagnosis", diagnosis);
                                        patientData.put("doctorId", currentUserId);
                                        patientData.put("activityLevel", null);
                                        patientData.put("mood", 0);
                                        patientData.put("pain", 0);
                                        patientData.put("role", "patient");

                                        userRef.setValue(patientData)
                                                .addOnSuccessListener(aVoid -> {
                                                    DatabaseReference doctorRef = db.getReference().child("users").child(currentUserId).child("assignedPatients");
                                                    doctorRef.child(uid).setValue(true)
                                                            .addOnSuccessListener(aVoid1 -> {
                                                                Toast.makeText(DoctorStart.this, "Patient added successfully!", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(DoctorStart.this, "Failed to assign patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(DoctorStart.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });


                                    } else {
                                        Toast.makeText(DoctorStart.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(DoctorStart.this, "Only administrators can add patients", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DoctorStart.this, "User role not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DoctorStart.this, "Error checking role: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
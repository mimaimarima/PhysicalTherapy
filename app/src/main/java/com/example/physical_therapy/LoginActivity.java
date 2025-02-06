package com.example.physical_therapy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailET, passwordET;
    private Button loginB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        auth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginB = findViewById(R.id.loginB);

        loginB.setOnClickListener(view -> {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    String uid = auth.getCurrentUser().getUid();

                    FirebaseDatabase db = FirebaseDatabase.getInstance("https://physther-b19d2-default-rtdb.europe-west1.firebasedatabase.app/");
                    DatabaseReference usersRef = db.getReference("users").child(uid);

                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                String role = dataSnapshot.child("role").getValue(String.class);
                                if (role.equals("admin"))
                                {
                                    String name = dataSnapshot.child("fullName").getValue(String.class);
                                    Intent intent = new Intent(LoginActivity.this, DoctorStart.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("USER_UID", uid);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Intent intent = new Intent(LoginActivity.this, PatientCalendar.class);
                                    intent.putExtra("check", 1); // This check is for buttons' visibilities in next Activity
                                    intent.putExtra("PATIENT_ID", uid);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "User data not found in DB", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "User data does not exist in DB.");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.e("LoginActivity", "Database error: ", databaseError.toException());
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Login failed: " + task.getException().getMessage());
                }
            });
        });
    }
}
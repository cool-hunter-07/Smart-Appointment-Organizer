package com.cool.smartappointmentorganizer.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.smartappointmentorganizer.MainActivity;
import com.cool.smartappointmentorganizer.R;
import com.cool.smartappointmentorganizer.home.HomeActivity;
import com.cool.smartappointmentorganizer.model.User;
import com.cool.smartappointmentorganizer.utils.StaticConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    private EditText editTextName;
    private EditText editTextMobile;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Button buttonRegister;
    private TextView textViewSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserInput()) {
                    String mobile, name;
                    mobile = editTextMobile.getText().toString();
                    name = editTextName.getText().toString();

                    registerNewUser(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim(), name, mobile);
                }
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void registerNewUser(final String email, String password, final String name, final String mobile) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                User user = new User(firebaseUser.getUid(), email, mobile, "avatar", name);
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(StaticConfig.FIREBASE_USER)
                                        .child(firebaseUser.getUid()).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Authentication Success.",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Authentication Failed!.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication Failed!.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateUserInput() {
        boolean isOk = true;

        String email = editTextEmail.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String name = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.trim().length() < 6) {
            editTextEmail.setError("Enter Valid Email Address");
            isOk = false;
        }
        if (mobile.trim().length() < 6) {
            editTextMobile.setError("Enter Valid Mobile Number");
            isOk = false;
        }
        if (name.trim().length() < 6) {
            editTextName.setError("Name must be at least 6 letters long");
            isOk = false;
        }
        if (password.trim().length() < 6) {
            editTextPassword.setError("Enter Password more than 6 letters");
            isOk = false;
        }

        return isOk;
    }
}
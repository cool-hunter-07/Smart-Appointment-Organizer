package com.cool.smartappointmentorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cool.smartappointmentorganizer.auth.RegisterActivity;
import com.cool.smartappointmentorganizer.home.HomeActivity;
import com.cool.smartappointmentorganizer.model.User;
import com.cool.smartappointmentorganizer.utils.StaticConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        else {
            getUser(user);
        }
    }

    private void getUser(final FirebaseUser user) {
        FirebaseDatabase.getInstance().getReference().child(StaticConfig.FIREBASE_USER).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user1 = new User();
                    user1.id = user.getUid();
                    user1.email = user.getEmail();
                    user1.mobile = Objects.requireNonNull(dataSnapshot.child("mobile").getValue()).toString();
                    user1.avatar = Objects.requireNonNull(dataSnapshot.child("avatar").getValue()).toString();
                    StaticConfig.user = user1;

                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } catch (NullPointerException ne) {
                    Toast.makeText(getApplicationContext(), "Please Login Again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
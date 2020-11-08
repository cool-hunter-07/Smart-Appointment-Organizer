package com.cool.smartappointmentorganizer.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.smartappointmentorganizer.MainActivity;
import com.cool.smartappointmentorganizer.R;
import com.cool.smartappointmentorganizer.adapters.TimeLineAdapter;
import com.cool.smartappointmentorganizer.auth.RegisterActivity;
import com.cool.smartappointmentorganizer.model.Appointment;
import com.cool.smartappointmentorganizer.utils.BackgroundNotification;
import com.cool.smartappointmentorganizer.utils.DatePickerFragment;
import com.cool.smartappointmentorganizer.utils.StaticConfig;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private RecyclerView recyclerView;

    private TimeLineAdapter adapter;
    private DatabaseReference database;
    private Spinner spinnerFilterAppointment;
    private ImageView imageViewEmptyBackground;
    private TextView textViewEmptyBackgroundText;

    private ArrayList<String> filterDate = new ArrayList<>();

    private Query currentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (StaticConfig.user == null) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }

        database = FirebaseDatabase.getInstance().getReference().child(StaticConfig.FIREBASE_APPOINTMENT).child(String.valueOf(StaticConfig.user.id));
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddAppointment = findViewById(R.id.fabAddAppointment);
        spinnerFilterAppointment = findViewById(R.id.spinnerFilterAppointment);
        imageViewEmptyBackground = findViewById(R.id.imageViewEmptyBackground);
        textViewEmptyBackgroundText = findViewById(R.id.textViewEmptyBackgroundText);

//        setUpTimeLine();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        filterDate.add("All");
        filterDate.add("Today");
        filterDate.add("Tomorrow");
        filterDate.add("Calender");

        setUpFilterDates();

        fabAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddAppointmentActivity.class));
            }
        });

        spinnerFilterAppointment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                String day = filterDate.get(position);
                Query query;

                switch (day) {
                    case "All":
                        query = database.orderByChild("date");
                        filterTimeLine(query);
                        break;
                    case "Tomorrow":
                        calendar.add(Calendar.DATE, 1);
                        String date = dateFormat.format(calendar.getTime());
                        if (date.startsWith("0")) {
                            date = date.substring(1);
                        }
                        query = database.orderByChild("date").equalTo(date);
                        filterTimeLine(query);
                        break;
                    case "Calender":
                        DialogFragment datePicker = new DatePickerFragment();
                        datePicker.show(getSupportFragmentManager(), "date picker");
                        break;
                    case "Today":
                        String date1 = dateFormat.format(calendar.getTime());
                        if (date1.startsWith("0")) {
                            date1 = date1.substring(1);
                        }
                        query = database.orderByChild("date").equalTo(date1);
                        filterTimeLine(query);
                        break;
                    default:
                        query = database.orderByChild("date").equalTo(day);
                        filterTimeLine(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getApplicationContext(), "Select Filter to view appointments", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setUpFilterDates() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, filterDate);
        spinnerFilterAppointment.setAdapter(arrayAdapter);
        spinnerFilterAppointment.setSelection(1);
    }

    private void setUpTimeLine() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Query queryArg = database.orderByChild("date").equalTo(dateFormat.format(calendar.getTime()));
        FirebaseRecyclerOptions<Appointment> options = new FirebaseRecyclerOptions.Builder<Appointment>().setQuery(queryArg, Appointment.class).build();
//        adapter = new TimeLineAdapter(options, textViewEmptyBackgroundText, imageViewEmptyBackground, recyclerView);
//        adapter = new TimeLineAdapter(options, textViewEmptyBackgroundText, imageViewEmptyBackground, recyclerView);
        recyclerView.setAdapter(adapter);

        imageViewEmptyBackground.setVisibility(View.VISIBLE);
        textViewEmptyBackgroundText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void filterTimeLine(Query query) {
        currentQuery = query;
//        adapter.stopListening();
//        FirebaseRecyclerOptions<Appointment> options = new FirebaseRecyclerOptions.Builder<Appointment>().setQuery(query, Appointment.class).build();
//        adapter = new TimeLineAdapter(options, textViewEmptyBackgroundText, imageViewEmptyBackground, recyclerView);
//        recyclerView.setAdapter(adapter);
        final ArrayList<Appointment> arrayListAppointment = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appointment : dataSnapshot.getChildren()) {
                    Appointment appointment1 = new Appointment();
                    appointment1.id = appointment.child("id").getValue().toString();
                    appointment1.date = appointment.child("date").getValue().toString();
                    appointment1.startTime = appointment.child("startTime").getValue().toString();
                    appointment1.endTime = appointment.child("endTime").getValue().toString();
                    appointment1.contactNumber = appointment.child("contactNumber").getValue().toString();
                    appointment1.note = appointment.child("note").getValue().toString();
                    appointment1.tags = appointment.child("tags").getValue().toString();
                    appointment1.title = appointment.child("title").getValue().toString();
                    appointment1.location = appointment.child("location").getValue().toString();

                    arrayListAppointment.add(appointment1);
                }

                adapter = new TimeLineAdapter(getApplicationContext(), arrayListAppointment, textViewEmptyBackgroundText, imageViewEmptyBackground, recyclerView);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        adapter.startListening();

//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentQuery == null)
            setUpTimeLine();
        else
            filterTimeLine(currentQuery);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
//        startService(new Intent(this, BackgroundNotification.class));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        filterDate.add(currentDateString);
        spinnerFilterAppointment.setSelection(filterDate.size()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
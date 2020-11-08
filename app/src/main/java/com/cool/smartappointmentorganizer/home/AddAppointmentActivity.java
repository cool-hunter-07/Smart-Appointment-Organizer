package com.cool.smartappointmentorganizer.home;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cool.smartappointmentorganizer.MainActivity;
import com.cool.smartappointmentorganizer.R;
import com.cool.smartappointmentorganizer.model.Appointment;
import com.cool.smartappointmentorganizer.utils.AlertReceiver;
import com.cool.smartappointmentorganizer.utils.BackgroundNotification;
import com.cool.smartappointmentorganizer.utils.DatePickerFragment;
import com.cool.smartappointmentorganizer.utils.StaticConfig;
import com.cool.smartappointmentorganizer.utils.TimePickerFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.cool.smartappointmentorganizer.utils.StaticConfig.REQUEST_CODE_READ_CONTACTS;

// todo... appointment remainder updated or cancelled alarm not cancelled
public class AddAppointmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final String TAG = getClass().getName();

    private TextView textViewAppointmentDate;
    private TextView textViewStartTime;
    private TextView textViewEndTime;

    private AutoCompleteTextView autoCompleteTextViewContact;
    private TextInputEditText textInputEditTextTag;
    private TextInputEditText textInputEditTextNote;
    private TextInputEditText textInputEditTextTitle;
    private TextInputEditText textInputEditTextLocation;

    private Spinner spinnerRemainder;

    private ArrayList<String> contactName = new ArrayList<>();
    private ArrayList<String> remainderOption = new ArrayList<>();

    private boolean flagIsStartTime = true;
    private String appointmentId;
    private String phoneNumber;

    private Calendar startCalendar;
    private Calendar dateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        appointmentId = getIntent().getStringExtra(StaticConfig.appointmentId);

        if (appointmentId != null)
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Appointment");
        else
            Objects.requireNonNull(getSupportActionBar()).setTitle("Add Appointment");

        textViewAppointmentDate = findViewById(R.id.appointmentDate);
        textViewStartTime = findViewById(R.id.textInputEditTextStartTime);
        textViewEndTime = findViewById(R.id.textInputEditTextEndTime);
        autoCompleteTextViewContact = findViewById(R.id.textInputEditTextContact);
        spinnerRemainder = findViewById(R.id.spinnerRemainder);
        textInputEditTextNote = findViewById(R.id.textInputEditTextNote);
        textInputEditTextTag = findViewById(R.id.textInputEditTextTag);
        textInputEditTextTitle = findViewById(R.id.textInputEditTextTitle);
        textInputEditTextLocation = findViewById(R.id.textInputEditTextLocation);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        remainderOption.add("1 min");
        remainderOption.add("5 min");
        remainderOption.add("10 min");
        remainderOption.add("15 min");

        // loading contacts
        loadData();

        // contact adapter
        loadAdapter();

        // load data if on appointment is clicked
        if (appointmentId != null)
            loadAppointment();

        textViewAppointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagIsStartTime = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagIsStartTime = false;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointment();
            }
        });
    }

    private void loadAppointment() {
        Log.d(TAG, "Appointment ID " + appointmentId);
        FirebaseDatabase.getInstance().getReference()
                .child(StaticConfig.FIREBASE_APPOINTMENT)
                .child(StaticConfig.user.id)
                .child(appointmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "Appointment does not exists, please create new one", Toast.LENGTH_SHORT).show();
                }
                try {
                    textViewAppointmentDate.setText(Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString());
                    textViewStartTime.setText(Objects.requireNonNull(dataSnapshot.child("startTime").getValue()).toString());
                    textViewEndTime.setText(Objects.requireNonNull(dataSnapshot.child("endTime").getValue()).toString());
                    autoCompleteTextViewContact.setText(Objects.requireNonNull(dataSnapshot.child("contactNumber").getValue()).toString());

                    int position = 0;
                    for (int i = 0; i < remainderOption.size(); i++) {
                        if (remainderOption.get(i).equals(Objects.requireNonNull(dataSnapshot.child("remainderBefore").getValue()).toString())) {
                            position = i;
                            break;
                        }
                    }
                    spinnerRemainder.setSelection(position);
                    textInputEditTextNote.setText(Objects.requireNonNull(dataSnapshot.child("note").getValue()).toString());
                    textInputEditTextTag.setText(Objects.requireNonNull(dataSnapshot.child("tags").getValue()).toString());
                    textInputEditTextTitle.setText(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                    textInputEditTextLocation.setText(Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString());
                    phoneNumber = Objects.requireNonNull(dataSnapshot.child("contactNumber").getValue()).toString();

                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                    Date theDate = format.parse(dataSnapshot.child("date").getValue().toString());

                    Calendar c = Calendar.getInstance();
                    c.setTime(theDate);
                    dateCalendar = c;
                } catch (NullPointerException | ParseException ne) {
                    Log.e(TAG, "Hello World" + ne.getLocalizedMessage() + "");
                    Toast.makeText(getApplicationContext(), "Appointment does not exists, please create new one", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAdapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, contactName);
        autoCompleteTextViewContact.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapterRemainder = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, remainderOption);
        spinnerRemainder.setAdapter(arrayAdapterRemainder);
    }

    private void loadData() {
        if (ContextCompat.checkSelfPermission(AddAppointmentActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (managedCursor != null) {
                while (managedCursor.moveToNext()) {
                    contactName.add(managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                }
            }
        } else {
            ActivityCompat.requestPermissions(AddAppointmentActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            if (ContextCompat.checkSelfPermission(AddAppointmentActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                        ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                if (managedCursor != null) {
                    while (managedCursor.moveToNext()) {
                        contactName.add(managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    }
                }
            }
        }
    }

    private void saveAppointment() {
        if (validateAppointment()) {
            if (appointmentId == null) {
                appointmentId = String.valueOf(System.currentTimeMillis());
                phoneNumber = getPhoneNumber(autoCompleteTextViewContact.getText().toString());
            }

            Appointment appointment = new Appointment(appointmentId,
                    Objects.requireNonNull(textInputEditTextTitle.getText()).toString(),
                    autoCompleteTextViewContact.getText().toString(),
                    phoneNumber, textViewAppointmentDate.getText().toString(),
                    textViewStartTime.getText().toString(), textViewEndTime.getText().toString(),
                    Objects.requireNonNull(textInputEditTextNote.getText()).toString(),
                    Objects.requireNonNull(textInputEditTextTag.getText()).toString(),
                    spinnerRemainder.getSelectedItem().toString(),
                    Objects.requireNonNull(textInputEditTextLocation.getText()).toString(),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(System.currentTimeMillis()));

            // todo... push object to firebase
            pushAppointmentToFirebase(appointment);
            try {
                setRemainder(textInputEditTextTitle.getText().toString(), textInputEditTextNote.getText().toString());
            } catch (Exception e) {
                Log.d(TAG, "Error " + e.getLocalizedMessage());
            }

            Toast.makeText(getApplicationContext(), "Appointment saved successfully!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setRemainder(String title, String message) {
        int notificationId = StaticConfig.getNotificationId(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, 0);

        String reminderBefore = spinnerRemainder.getSelectedItem().toString();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
        c.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
        c.set(Calendar.SECOND, 0);

        switch (reminderBefore) {
            case "5 min":
                c.add(Calendar.MINUTE, -5);
                break;
            case "10 min":
                c.add(Calendar.MINUTE, -10);
                break;
            case "15 min":
                c.add(Calendar.MINUTE, -15);
                break;
            default:
                c.add(Calendar.MINUTE, -1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        SharedPreferences sharedPreferences = getSharedPreferences(StaticConfig.PREFERENCE_NOTIFICATION_ID_STORAGE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(appointmentId, notificationId);
        // Save the changes in SharedPreferences
        editor.apply(); // commit changes
    }

    private void cancelAppointment() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences sharedPreferences = getSharedPreferences(StaticConfig.PREFERENCE_NOTIFICATION_ID_STORAGE, MODE_PRIVATE);
                        int notificationId = sharedPreferences.getInt(appointmentId, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, 0);

                        alarmManager.cancel(pendingIntent);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference db = database.getReference(StaticConfig.FIREBASE_APPOINTMENT);

                        db.child(StaticConfig.user.id).child(appointmentId).removeValue();

                        Toast.makeText(getApplicationContext(), "Appointment deleted Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddAppointmentActivity.this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void pushAppointmentToFirebase(Appointment appointment) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference(StaticConfig.FIREBASE_APPOINTMENT);

        db.child(StaticConfig.user.id).child(appointment.id).setValue(appointment);
    }

    private boolean validateAppointment() {
        String date = textViewAppointmentDate.getText().toString();
        if (date.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Select Date", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Date today = c.getTime();

            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date date1 = format.parse(date);
                if (date1 != null) {
                    if (date1.before(today)) {
                        Toast.makeText(getApplicationContext(), "Date specified is before today!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String startTime = textViewStartTime.getText().toString();
        if (startTime.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Select From Time", Toast.LENGTH_LONG).show();
            return false;
        }

        String endTime = textViewStartTime.getText().toString();
        if (endTime.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Select To Time", Toast.LENGTH_LONG).show();
            return false;
        }

        String note = Objects.requireNonNull(textInputEditTextTitle.getText()).toString();
        if (note.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please add a title", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public String getPhoneNumber(String name) {
        if (name == null || name.trim().equals("")) {
            return "Unsaved";
        }

        String ret = null;

        if (ContextCompat.checkSelfPermission(AddAppointmentActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection, selection, null, null);
            if (c != null && c.moveToFirst()) {
                ret = c.getString(0);
            }
            if (c != null) {
                c.close();
            }

            if (ret == null)
                ret = "Unsaved";
            return ret.replace(" ", "");
        } else {
            return name;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        dateCalendar = c;
        textViewAppointmentDate.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        String strHour, strMinute;
        strHour = String.valueOf(hour);
        strMinute = String.valueOf(minute);

        if (strHour.length() == 1)
            strHour = "0" + strHour;

        if (strMinute.length() == 1)
            strMinute = "0" + strMinute;

        if (flagIsStartTime) {
            textViewStartTime.setText(strHour + ":" + strMinute);
            startCalendar = c;
        } else {
            textViewEndTime.setText(strHour + ":" + strMinute);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (appointmentId != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_appointment, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (appointmentId != null) {
            if (item.getItemId() == R.id.deleteAppointment) {
                cancelAppointment();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

//  keytool -list -v -keystore /Users/mayureshmanishsatao/Me/KEY/Playstore/SmartAppointmentOrganiser/key -alias key0 -storepass Mayuresh@123 -keypass Mayuresh@123
package com.cool.smartappointmentorganizer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.smartappointmentorganizer.R;
import com.cool.smartappointmentorganizer.home.AddAppointmentActivity;
import com.cool.smartappointmentorganizer.model.Appointment;
import com.cool.smartappointmentorganizer.utils.StaticConfig;
import com.github.vipulasri.timelineview.TimelineView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {
    private Context context;
    private TextView textViewEmptyBackgroundText;
    private ImageView imageViewEmptyBackground;
    private RecyclerView recyclerView;
    private ArrayList<Appointment> arrayListAppointments;

    public TimeLineAdapter(Context context, ArrayList<Appointment> appointments, TextView textViewEmptyBackgroundText, ImageView imageViewEmptyBackground, RecyclerView recyclerView) {
        this.context = context;
        this.textViewEmptyBackgroundText = textViewEmptyBackgroundText;
        this.imageViewEmptyBackground = imageViewEmptyBackground;
        this.recyclerView = recyclerView;
        this.arrayListAppointments = appointments;
        sortArrayList();
    }

    private int compareTime(String startTime1, String startTime2) {
        try {
            return new SimpleDateFormat("hh:mm").parse(startTime1).compareTo(new SimpleDateFormat("hh:mm").parse(startTime2));
        } catch (ParseException e) {
            return 0;
        }
    }

    private void sortArrayList() {
        for (int i=0; i<arrayListAppointments.size()-1; i++) {
            for (int j=i; j<arrayListAppointments.size(); j++) {
                String startTime1 = arrayListAppointments.get(i).startTime;
                String startTime2 = arrayListAppointments.get(j).startTime;

                if (compareTime(startTime1, startTime2) != -1) {
                    Collections.swap(arrayListAppointments, i, j);
                }
            }
        }
    }

    private int checkTimeOver(String startTime1) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int min = currentTime.get(Calendar.MINUTE);
        String currentTimestamp = hour + ":" + min;

        return compareTime(currentTimestamp, startTime1);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder timeLineViewHolder, int i) {
        final Appointment appointment = arrayListAppointments.get(i);

        timeLineViewHolder.textViewAppointmentTitle.setText(appointment.getTitle());
        timeLineViewHolder.textViewAppointmentDescription.setText(appointment.getNote());
        timeLineViewHolder.textViewAppointmentDate.setText(appointment.getDate());
        timeLineViewHolder.textViewAppointmentStartTime.setText(appointment.getStartTime());
        timeLineViewHolder.textViewAppointmentEndTime.setText(appointment.getEndTime());

        timeLineViewHolder.cardAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddAppointmentActivity.class);
                intent.putExtra(StaticConfig.appointmentId, appointment.id);
                view.getContext().startActivity(intent);
            }
        });

        if (checkTimeOver(appointment.getStartTime()) == -1) {
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_donut_large_24, context.getTheme());
            timeLineViewHolder.mTimelineView.setMarker(d);
        }
    }

    @Override
    public int getItemCount() {
        return arrayListAppointments.size();
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        textViewEmptyBackgroundText.setVisibility(View.GONE);
        imageViewEmptyBackground.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_timeline_card, parent, false);
        return new TimeLineViewHolder(v, viewType);
    }

    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAppointmentTitle;
        public TextView textViewAppointmentDescription;
        public TextView textViewAppointmentDate;
        public TextView textViewAppointmentStartTime;
        public TextView textViewAppointmentEndTime;
        public TimelineView mTimelineView;
        public CardView cardAppointment;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            textViewAppointmentDate = itemView.findViewById(R.id.textViewAppointmentDate);
            textViewAppointmentTitle = itemView.findViewById(R.id.textViewAppointmentTitle);
            textViewAppointmentEndTime = itemView.findViewById(R.id.textViewAppointmentEndTime);
            textViewAppointmentStartTime = itemView.findViewById(R.id.textViewAppointmentStartTime);
            textViewAppointmentDescription = itemView.findViewById(R.id.textViewAppointmentDescription);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeline);
            cardAppointment = itemView.findViewById(R.id.cardAppointment);
            mTimelineView.initLine(viewType);
        }
    }
}


//public class TimeLineAdapter extends FirebaseRecyclerAdapter <Appointment, TimeLineAdapter.TimeLineViewHolder> {
////    public TimeLineAdapter(ArrayList<Appointment> appointments, TextView textViewEmptyBackgroundText, ImageView imageViewEmptyBackground, RecyclerView recyclerView) {
////
////    }
//
//    private TextView textViewEmptyBackgroundText;
//    private ImageView imageViewEmptyBackground;
//    private RecyclerView recyclerView;
//
//    public TimeLineAdapter(@NonNull FirebaseRecyclerOptions options, TextView textViewEmptyBackgroundText, ImageView imageViewEmptyBackground, RecyclerView recyclerView) {
//        super(options);
//        this.textViewEmptyBackgroundText = textViewEmptyBackgroundText;
//        this.imageViewEmptyBackground = imageViewEmptyBackground;
//        this.recyclerView = recyclerView;
//    }
//
//    @Override
//    public int getItemCount() {
//        if (super.getItemCount() == 0) {
//            imageViewEmptyBackground.setVisibility(View.VISIBLE);
//            textViewEmptyBackgroundText.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.INVISIBLE);
//        }
//        return super.getItemCount();
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull TimeLineViewHolder timeLineViewHolder, int i, @NonNull final Appointment appointment) {
//        timeLineViewHolder.textViewAppointmentTitle.setText(appointment.getTitle());
//        timeLineViewHolder.textViewAppointmentDescription.setText(appointment.getNote());
//        timeLineViewHolder.textViewAppointmentDate.setText(appointment.getDate());
//        timeLineViewHolder.textViewAppointmentStartTime.setText(appointment.getStartTime());
//        timeLineViewHolder.textViewAppointmentEndTime.setText(appointment.getEndTime());
//
//        timeLineViewHolder.cardAppointment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), AddAppointmentActivity.class);
//                intent.putExtra(StaticConfig.appointmentId, appointment.id);
//                view.getContext().startActivity(intent);
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        textViewEmptyBackgroundText.setVisibility(View.GONE);
//        imageViewEmptyBackground.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.VISIBLE);
//
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_timeline_card, parent, false);
//        return new TimeLineViewHolder(v, viewType);
//    }
//
//    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
//        public TextView textViewAppointmentTitle;
//        public TextView textViewAppointmentDescription;
//        public TextView textViewAppointmentDate;
//        public TextView textViewAppointmentStartTime;
//        public TextView textViewAppointmentEndTime;
//        public TimelineView mTimelineView;
//        public CardView cardAppointment;
//
//        public TimeLineViewHolder(View itemView, int viewType) {
//            super(itemView);
//            textViewAppointmentDate = itemView.findViewById(R.id.textViewAppointmentDate);
//            textViewAppointmentTitle = itemView.findViewById(R.id.textViewAppointmentTitle);
//            textViewAppointmentEndTime = itemView.findViewById(R.id.textViewAppointmentEndTime);
//            textViewAppointmentStartTime = itemView.findViewById(R.id.textViewAppointmentStartTime);
//            textViewAppointmentDescription = itemView.findViewById(R.id.textViewAppointmentDescription);
//            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeline);
//            cardAppointment = itemView.findViewById(R.id.cardAppointment);
//            mTimelineView.initLine(viewType);
//        }
//    }
//}

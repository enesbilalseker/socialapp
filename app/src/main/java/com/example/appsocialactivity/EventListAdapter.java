package com.example.appsocialactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appsocialactivity.dbmodel.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "EVENT_LIST_ADAPTER";
    private Context mContext;
    int mResource;
    public EventListAdapter(@NonNull Context context, int resource, ArrayList<Event> object) {
        super(context, resource, object);
        this.mContext = mContext;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getEventName();
        String description  = getItem(position).getEventDescription();
        String address = getItem(position).getNameOfPlace();
        Long date = getItem(position).getDate();
        Integer numofpeople  = getItem(position).getNumOfPeople();
        Event event = new Event();
        event.setEventName(title);
        event.setNameOfPlace(address);
        event.setEventDescription(description);
        event.setDate(date);
        event.setNumOfPeople(numofpeople);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView tvTitle = convertView.findViewById(R.id.title);
        tvTitle.setText(title);

        return convertView;

    }
}

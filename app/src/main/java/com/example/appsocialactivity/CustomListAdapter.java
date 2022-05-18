package com.example.appsocialactivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.appsocialactivity.dbmodel.Event;

import org.w3c.dom.Text;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Event> items; //data source of the list adapter

    //public constructor
    public CustomListAdapter(Context context, ArrayList<Event> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.event_list, parent, false);
        }

        // get current item to be displayed
        Event currentItem = (Event) getItem(position);
        // get the TextView for item name and item description
        TextView textViewEventTag= (TextView)
                convertView.findViewById(R.id.event_tag);
        TextView textViewEventName = (TextView)
                convertView.findViewById(R.id.title);
        TextView textViewEventDescription = (TextView)
                convertView.findViewById(R.id.description);
        TextView textViewEventAddress = (TextView)
                convertView.findViewById(R.id.event_location);
        TextView textViewEventDate = (TextView)
                convertView.findViewById(R.id.event_date);
        TextView textViewEventNumOfPeople = (TextView)
                convertView.findViewById(R.id.number_of_people_TV);
        Button locbtn = convertView.findViewById(R.id.locbtneventlist);
        TextView textViewContact = (TextView)
                convertView.findViewById(R.id.contact);

        //sets the text for item name and item description from the current item object
        textViewEventTag.setText(currentItem.getInterestsOfEvent());
        textViewEventName.setText(currentItem.getEventName());
        textViewEventDescription.setText(currentItem.getEventDescription());
        textViewEventAddress.setText(currentItem.getNameOfPlace());
        Long tempDate = currentItem.getDate();
        Date date = new Date(tempDate);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        String formatString = format.format(date);
        textViewEventDate.setText(formatString);
        textViewContact.setText(currentItem.getContactNumber());

        textViewEventNumOfPeople.setText(currentItem.getNumOfPeople().toString());

        locbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity2.class);
                Double lat = currentItem.getLocation().getLatitude();
                Double lng = currentItem.getLocation().getLongitude();
                intent.putExtra("lat1", lat);
                intent.putExtra("lng1", lng);
                context.startActivity(intent);
            }
        });

        // returns the view for the current row
        return convertView;
    }
}
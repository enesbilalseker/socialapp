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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsocialactivity.dbmodel.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomListHistoryAdapter  extends BaseAdapter {
    private Context context; //context
    private ArrayList<Event> items; //data source of the list adapter
    private ArrayList<String> iditems; //data source of the list adapter
    private String suserid;
    private FirebaseFirestore db;

    //public constructor
    public CustomListHistoryAdapter(Context context, ArrayList<Event> items,ArrayList<String> iditems, String suserid) {
        this.context = context;
        this.items = items;
        this.iditems = iditems;
        this.suserid = suserid;
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

    public int getIdCount() {
        return iditems.size(); //returns total of items in the list
    }


    public Object getIdItem(int position) {
        return iditems.get(position); //returns list item at the specified position
    }


    public long getIdItemId(int position) {
        return position;
    }
    public String getsuserIdItem() {
        return suserid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get instance of firestore db
        db = FirebaseFirestore.getInstance();
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.event_list_history, parent, false);
        }

        // get current item to be displayed
        Event currentItem = (Event) getItem(position);
        String eventId = (String) getIdItem(position);
        String userId = getsuserIdItem();
        Button deleteButton = (Button) convertView.findViewById(R.id.delete_eventBTN);
        // get the TextView for item name and item description
        TextView textViewEventName = (TextView)
                convertView.findViewById(R.id.history_event_name);
        TextView textViewDateHistory = (TextView) convertView.findViewById(R.id.event_date_history) ;

        //sets the text for item name and item description from the current item object
        textViewEventName.setText(currentItem.getEventName());
        Long tempDate = currentItem.getDate();
        Date date = new Date(tempDate);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        String formatString = format.format(date);
        textViewDateHistory.setText(formatString);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Event").document(eventId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            iditems.remove(eventId);
                            db.collection("User").document(userId).update("eventList", iditems);
                        }
                    }
                });
            }
        });

        // returns the view for the current row
        return convertView;
    }
}

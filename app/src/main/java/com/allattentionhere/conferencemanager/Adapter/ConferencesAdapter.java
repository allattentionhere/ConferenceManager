package com.allattentionhere.conferencemanager.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.allattentionhere.conferencemanager.Activities.DoctorActivity;
import com.allattentionhere.conferencemanager.R;


public class ConferencesAdapter extends CursorAdapter
{
    ConferencesViewHolder viewHolder;

    public ConferencesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_suggestion, parent, false);

        ConferencesViewHolder viewConferenceHolder = new ConferencesViewHolder(view);
        view.setTag(viewConferenceHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor)
    {
        viewHolder = (ConferencesViewHolder) view.getTag();

        String sTopic = cursor.getString(DoctorActivity.COLUMN_TOPIC);
        viewHolder.topic.setText(sTopic);

        String sDescription = cursor.getString(DoctorActivity.COLUMN_DESCRIPTION);
        viewHolder.description.setText(sDescription);



        String sDate = cursor.getString(DoctorActivity.COLUMN_DATE);
        viewHolder.date.setText(sDate);
    }

    public class ConferencesViewHolder
    {
        public final TextView topic;
        public final TextView description;
        public final TextView date;

        public ConferencesViewHolder(View view)
        {
            topic = (TextView)view.findViewById(R.id.txt_topic);
            description = (TextView)view.findViewById(R.id.txt_desc);
            date = (TextView) view.findViewById(R.id.txt_date);
        }
    }
}

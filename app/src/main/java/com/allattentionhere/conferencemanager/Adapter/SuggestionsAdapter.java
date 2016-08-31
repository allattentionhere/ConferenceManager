package com.allattentionhere.conferencemanager.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.allattentionhere.conferencemanager.Activities.AdminActivity;
import com.allattentionhere.conferencemanager.R;


public class SuggestionsAdapter extends CursorAdapter
{
    public SuggestionsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_suggestion, parent, false);

        SuggestionsViewHolder viewHolder = new SuggestionsViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        SuggestionsViewHolder viewHolder = (SuggestionsViewHolder) view.getTag();

        String topic = cursor.getString(AdminActivity.COLUMN_TOPIC);
        viewHolder.topic.setText(topic);

        String description = cursor.getString(AdminActivity.COLUMN_DESCRIPTION);
        viewHolder.description.setText(description);



        String date = cursor.getString(AdminActivity.COLUMN_AVAILABILITY_DATE);
        viewHolder.date.setText(date);
    }

    public class SuggestionsViewHolder
    {
        public final TextView topic;
        public final TextView description;
        public final TextView date;

        public SuggestionsViewHolder(View view) {
            topic = (TextView)view.findViewById(R.id.txt_topic);
            description = (TextView)view.findViewById(R.id.txt_desc);
            date = (TextView) view.findViewById(R.id.txt_date);
        }
    }
}

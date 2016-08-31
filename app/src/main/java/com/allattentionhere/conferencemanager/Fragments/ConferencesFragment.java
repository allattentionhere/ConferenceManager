package com.allattentionhere.conferencemanager.Fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.allattentionhere.conferencemanager.Activities.DoctorActivity;
import com.allattentionhere.conferencemanager.Adapter.ConferencesAdapter;
import com.allattentionhere.conferencemanager.Data.DataContract;
import com.allattentionhere.conferencemanager.Data.DataProvider;
import com.allattentionhere.conferencemanager.Helper.Utils;
import com.allattentionhere.conferencemanager.R;


public class ConferencesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int FORECAST_LOADER = 0;

    ListView listView = null;
    ConferencesAdapter mConferencesAdapter = null;
    int mPosition = ListView.INVALID_POSITION;

    public ConferencesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        listView = (ListView) view.findViewById(R.id.lv);
        TextView txt_title=(TextView)view.findViewById(R.id.txt_title);
        txt_title.setText("Conferences");
        mConferencesAdapter = new ConferencesAdapter(getActivity(), null, 0);
        listView.setAdapter(mConferencesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    ((ConferenceCallback) getActivity()).onConferenceSelected(DataContract.ConferenceEntry
                                            .buildConferenceUri(cursor.getLong(DoctorActivity.COLUMN_CONFERENCE_ID)));
                }
            }
        });
        mConferencesAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DataContract.ConferenceEntry._ID+ " ASC";
        return new CursorLoader(getActivity(),
                DataContract.ConferenceEntry.CONTENT_URI,
                DoctorActivity.CONFERENCE_COLUMNS ,
                DataProvider.sConferenceByUserIDSelection,
                new String[]{""+ Utils.getUserId(getActivity())},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mConferencesAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION)
            listView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mConferencesAdapter.swapCursor(null);
    }

    public interface ConferenceCallback
    {
        void onConferenceSelected(Uri uri);
    }
}

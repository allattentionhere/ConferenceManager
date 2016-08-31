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

import com.allattentionhere.conferencemanager.Activities.AdminActivity;
import com.allattentionhere.conferencemanager.Adapter.SuggestionsAdapter;
import com.allattentionhere.conferencemanager.Data.DataContract;
import com.allattentionhere.conferencemanager.R;


public class SuggestionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int FORECAST_LOADER = 1;
    private ListView listView = null;
    SuggestionsAdapter mSuggestionAdapter = null;
    int mPosition = ListView.INVALID_POSITION;

    public SuggestionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        listView = (ListView)view.findViewById(R.id.lv);
        TextView txt_title=(TextView)view.findViewById(R.id.txt_title);
        txt_title.setText("Suggestions");
        mSuggestionAdapter = new SuggestionsAdapter(getActivity(), null, 0);
        listView.setAdapter(mSuggestionAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    ((SuggestionCallback) getActivity()).onSuggestionSelected(DataContract.SuggestionEntry
                                            .buildSuggestionUri(cursor.getLong(AdminActivity.COLUMN_SUGGESTION_ID)));
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String sortOrder = DataContract.SuggestionEntry._ID + " ASC";
        Uri SuggestionUri = DataContract.SuggestionEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), SuggestionUri, AdminActivity.SUGGESTION_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSuggestionAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION)
            listView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSuggestionAdapter.swapCursor(null);
    }

    public interface SuggestionCallback
    {
        void onSuggestionSelected(Uri uri);
    }

}

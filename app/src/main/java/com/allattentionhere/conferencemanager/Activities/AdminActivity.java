package com.allattentionhere.conferencemanager.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.conferencemanager.Data.DataContract;
import com.allattentionhere.conferencemanager.Data.DataProvider;
import com.allattentionhere.conferencemanager.Fragments.ConferencesFragment;
import com.allattentionhere.conferencemanager.Fragments.SuggestionsFragment;
import com.allattentionhere.conferencemanager.Helper.Utils;
import com.allattentionhere.conferencemanager.R;

import java.util.Calendar;

public class AdminActivity extends AppCompatActivity implements SuggestionsFragment.SuggestionCallback, ConferencesFragment.ConferenceCallback {
    public static StringBuilder globalString = new StringBuilder();

    EditText topic;
    EditText description;
    static TextView date,updateDate;
    Button dateButton, btn_add, btn_signout;

    String conferenceID;
    String suggestionID;
    int updated;
    TextView txt_suggestions, txt_conferences;


    //TODO: remove Read Column
    public static final String[] SUGGESTION_COLUMNS = {
            DataContract.SuggestionEntry.TABLE_NAME + "." + DataContract.SuggestionEntry._ID,
            DataContract.SuggestionEntry.COLUMN_USER_ID,
            DataContract.SuggestionEntry.COLUMN_TOPIC,
            DataContract.SuggestionEntry.COLUMN_DESCRIPTION,
            DataContract.SuggestionEntry.COLUMN_AVAILABILITY_DATE,
            DataContract.SuggestionEntry.COLUMN_READ_TAG
    };

    public static final int COLUMN_SUGGESTION_ID = 0;
    public static final int COLUMN_USER_ID = 1;
    public static final int COLUMN_TOPIC = 2;
    public static final int COLUMN_DESCRIPTION = 3;
    public static final int COLUMN_AVAILABILITY_DATE = 4;
    public static final int COLUMN_READ_TAG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        init();
        setListener();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_admin, new SuggestionsFragment(), SuggestionsFragment.class.getSimpleName())
                .commit();


    }

    private void setListener() {
        txt_conferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConferences();
                txt_conferences.setBackgroundResource(R.color.colorAccent);
                txt_suggestions.setBackgroundResource(R.color.white);

            }
        });
        txt_suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fl_admin, new SuggestionsFragment(), SuggestionsFragment.class.getSimpleName())
                        .commit();
                txt_conferences.setBackgroundResource(R.color.white);
                txt_suggestions.setBackgroundResource(R.color.colorAccent);
            }
        });
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewConference();
            }
        });
    }

    private void init() {
        txt_suggestions = (TextView) findViewById(R.id.txt_suggestions);
        txt_conferences = (TextView) findViewById(R.id.txt_conferences);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_signout = (Button) findViewById(R.id.btn_signout);

    }


    @Override
    public void onSuggestionSelected(final Uri uri) {
        Cursor cursor = getContentResolver().query(uri, SUGGESTION_COLUMNS, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            final String title = cursor.getString(AdminActivity.COLUMN_TOPIC);
            final String description = cursor.getString(AdminActivity.COLUMN_DESCRIPTION);
            final String date = cursor.getString(AdminActivity.COLUMN_AVAILABILITY_DATE);
            final String readTag = cursor.getString(AdminActivity.COLUMN_READ_TAG);

            if (readTag.equals("1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Make a Conference on this suggested topic?");

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Convert", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DataContract.ConferenceEntry.COLUMN_USER_ID, Utils.getUserId(AdminActivity.this));
                                contentValues.put(DataContract.ConferenceEntry.COLUMN_TOPIC, title);
                                contentValues.put(DataContract.ConferenceEntry.COLUMN_DESCRIPTION, description);
                                contentValues.put(DataContract.ConferenceEntry.COLUMN_DATE, date);

                                AdminActivity.this.getContentResolver()
                                        .insert(DataContract.ConferenceEntry.CONTENT_URI, contentValues);

                                Toast.makeText(AdminActivity.this, "Your Conference is added successfully", Toast.LENGTH_SHORT)
                                        .show();

                                ContentValues values = new ContentValues();
                                values.put(DataContract.SuggestionEntry.COLUMN_READ_TAG, "0");
                                suggestionID = Long.toString(DataContract.SuggestionEntry.getSuggestionIDFromUri(uri));
                                updated = getContentResolver()
                                        .update(DataContract.SuggestionEntry.CONTENT_URI,
                                                values,
                                                DataProvider.sSuggestionIDSelection,
                                                new String[]{suggestionID});
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            } else {
                Toast.makeText(AdminActivity.this, "There is already a conference on this topic", Toast.LENGTH_SHORT)
                        .show();
            }
            cursor.close();


        }
    }

    @Override
    public void onConferenceSelected(Uri uri) {
        editConference(uri);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private void addNewConference() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_conference_suggestion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        topic = (EditText) promptView.findViewById(R.id.etxt_topic);
        description = (EditText) promptView.findViewById(R.id.etxt_desc);
        date = (TextView) promptView.findViewById(R.id.txt_date);
        dateButton = (Button) promptView.findViewById(R.id.btn_date);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getFragmentManager(), "datePicker");
            }
        });

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String sTopic = topic.getText().toString();
                        String sDescription = description.getText().toString();
                        date.setText(globalString.toString());
                        String sDate = globalString.toString();

                        if (!sTopic.equals("") && !sDescription.equals("") && !sDate.equals("")) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_USER_ID, Utils.getUserId(AdminActivity.this));
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_TOPIC, sTopic);
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_DESCRIPTION, sDescription);
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_DATE, sDate);

                            AdminActivity.this.getContentResolver()
                                    .insert(DataContract.ConferenceEntry.CONTENT_URI, contentValues);

                            Toast.makeText(AdminActivity.this, "Your Conference is added successfully", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(AdminActivity.this, "Please Enter all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void signOut() {
        Utils.signOut(this);
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    private void openConferences() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(ConferencesFragment.class.getSimpleName())
                .replace(R.id.fl_admin, new ConferencesFragment(), ConferencesFragment.class.getSimpleName())
                .commit();
    }

    private void editConference(Uri uri) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_conference_suggestion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText updateTopic = (EditText) promptView.findViewById(R.id.etxt_topic);
        final EditText updateDescription = (EditText) promptView.findViewById(R.id.etxt_desc);
        updateDate = (TextView) promptView.findViewById(R.id.txt_date);
        final Button updateDateButton = (Button) promptView.findViewById(R.id.btn_date);

        Cursor cursor = getContentResolver().query(uri, DoctorActivity.CONFERENCE_COLUMNS, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            conferenceID = Long.toString(DataContract.ConferenceEntry.getConferenceIDFromUri(uri));
            updateTopic.setText(cursor.getString(DoctorActivity.COLUMN_TOPIC));
            updateDescription.setText(cursor.getString(DoctorActivity.COLUMN_DESCRIPTION));
            updateDate.setText(cursor.getString(DoctorActivity.COLUMN_DATE));
            cursor.close();
        }

        updateDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String s_Topic = updateTopic.getText().toString();
                        String s_Description = updateDescription.getText().toString();
                        String s_Date = updateDate.getText().toString().length() == 0 ? globalString.toString() : updateDate.getText().toString();

                        if (updateTopic.getText().toString().length() == 0 ||
                                updateDescription.getText().toString().length() == 0 ||
                                s_Date.length() == 0) {
                            Toast.makeText(AdminActivity.this, "Please Enter all fields", Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_USER_ID, Utils.getUserId(AdminActivity.this));
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_TOPIC, s_Topic);
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_DESCRIPTION, s_Description);
                            contentValues.put(DataContract.ConferenceEntry.COLUMN_DATE, s_Date);

                            updated = getContentResolver()
                                    .update(DataContract.ConferenceEntry.CONTENT_URI, contentValues,
                                            DataProvider.sConferenceIDSelection, new String[]{conferenceID});

                            Toast.makeText(AdminActivity.this, updated + " Conference is Updated.", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this conference?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        updated = getContentResolver()
                                                .delete(DataContract.ConferenceEntry.CONTENT_URI,
                                                        DataProvider.sConferenceIDSelection,
                                                        new String[]{conferenceID});

                                        Toast.makeText(AdminActivity.this, updated + " Conference has been Deleted.",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            globalString = new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ");
            if (updateDate!=null)updateDate.setText(globalString);
            if (date!=null)date.setText(globalString);

        }
    }
}

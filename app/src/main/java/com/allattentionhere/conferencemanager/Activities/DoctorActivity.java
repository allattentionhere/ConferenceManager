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
import com.allattentionhere.conferencemanager.Fragments.InvitesFragment;
import com.allattentionhere.conferencemanager.Fragments.SuggestionsFragment;
import com.allattentionhere.conferencemanager.Helper.Utils;
import com.allattentionhere.conferencemanager.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DoctorActivity extends AppCompatActivity implements InvitesFragment.Callback, SuggestionsFragment.SuggestionCallback {
    public static StringBuilder globalString = new StringBuilder();

    EditText topic;
    EditText description;
    Button datePicker, btn_add, btn_signout;
    static TextView txt_date;
    TextView txt_suggestions, txt_invitations,txt_user;
    String conferenceID;
    String suggestionID;
    int updated;
    static TextView updateDate;


    public static final String[] CONFERENCE_COLUMNS = {
            DataContract.ConferenceEntry.TABLE_NAME + "." + DataContract.ConferenceEntry._ID,
            DataContract.ConferenceEntry.COLUMN_USER_ID,
            DataContract.ConferenceEntry.COLUMN_TOPIC,
            DataContract.ConferenceEntry.COLUMN_DESCRIPTION,
            DataContract.ConferenceEntry.COLUMN_DATE,
            DataContract.ConferenceEntry.COLUMN_READ_TAG
    };

    public static final int COLUMN_CONFERENCE_ID = 0;
    public static final int COLUMN_USER_ID = 1;
    public static final int COLUMN_TOPIC = 2;
    public static final int COLUMN_DESCRIPTION = 3;
    public static final int COLUMN_DATE = 4;
    public static final int COLUMN_READ_TAG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        init();
        setListener();
        openSuggestions();


    }

    private void setListener() {
        txt_invitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fl_doctor, new InvitesFragment(), InvitesFragment.class.getSimpleName())
                        .commit();
                txt_invitations.setBackgroundResource(R.color.colorAccent);
                txt_suggestions.setBackgroundResource(R.color.white);
            }
        });
        txt_suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSuggestions();
                txt_invitations.setBackgroundResource(R.color.white);
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
                addNewSuggestion();
            }
        });
    }

    private void init() {
        txt_suggestions = (TextView) findViewById(R.id.txt_suggestions);
        txt_invitations = (TextView) findViewById(R.id.txt_invitations);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_signout = (Button) findViewById(R.id.btn_signout);
        txt_user = (TextView)findViewById(R.id.txt_user);
        txt_user.setText("Welcome "+Utils.getUserName(this)+" (Doctor)");
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onItemSelected(final Uri uri) {

        Cursor cursor = getContentResolver().query(uri, CONFERENCE_COLUMNS, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            final String title = cursor.getString(DoctorActivity.COLUMN_TOPIC);
            final String description = cursor.getString(DoctorActivity.COLUMN_DESCRIPTION);
            final String date = cursor.getString(DoctorActivity.COLUMN_DATE);
            final String readTag = cursor.getString(DoctorActivity.COLUMN_READ_TAG);

            String[] dateInParts = date.split("-");
            final Calendar cal = new GregorianCalendar(Integer.parseInt(dateInParts[2].trim()), Integer.parseInt(dateInParts[0].trim())
                    , Integer.parseInt(dateInParts[0].trim()));
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            final long startDate = cal.getTimeInMillis();

            if (readTag.equals("1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Add to calendar");

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra("beginTime", startDate);
                                intent.putExtra("allDay", true);
                                intent.putExtra("rrule", "FREQ=YEARLY");
                                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                                intent.putExtra("title", title);
                                intent.putExtra("description", description);

                                startActivity(intent);

                                ContentValues values = new ContentValues();
                                values.put(DataContract.ConferenceEntry.COLUMN_READ_TAG, "0");
                                conferenceID = Long.toString(DataContract.ConferenceEntry.getConferenceIDFromUri(uri));
                                updated = getContentResolver()
                                        .update(DataContract.ConferenceEntry.CONTENT_URI,
                                                values,
                                                DataProvider.sConferenceIDSelection,
                                                new String[]{conferenceID});
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
                Toast.makeText(DoctorActivity.this, "This Conference is already added to Calendar.", Toast.LENGTH_SHORT)
                        .show();
            }


            cursor.close();
        }
    }


    private void signOut() {
        Utils.signOut(this);

        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    private void addNewSuggestion() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_conference_suggestion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        topic = (EditText) promptView.findViewById(R.id.etxt_topic);
        description = (EditText) promptView.findViewById(R.id.etxt_desc);
        txt_date = (TextView) promptView.findViewById(R.id.txt_date);
        datePicker = (Button) promptView.findViewById(R.id.btn_date);
        datePicker.setOnClickListener(new View.OnClickListener() {
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
                        txt_date.setText(globalString.toString());
                        String sDate = globalString.toString();

                        if (!sTopic.equals("") && !sDescription.equals("") && !sDate.equals("")) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_USER_ID, Utils.getUserId(DoctorActivity.this));
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_TOPIC, sTopic);
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_DESCRIPTION, sDescription);
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_AVAILABILITY_DATE, sDate);

                            DoctorActivity.this.getContentResolver()
                                    .insert(DataContract.SuggestionEntry.CONTENT_URI, contentValues);

                            Toast.makeText(DoctorActivity.this, "Your Suggestion is submitted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DoctorActivity.this, "Please Enter all fields", Toast.LENGTH_SHORT).show();
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

    private void openSuggestions() {
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.fl_doctor, new SuggestionsFragment(), SuggestionsFragment.class.getSimpleName())
                .addToBackStack(SuggestionsFragment.class.getSimpleName()).commit();
    }

    private void editSuggestion(Uri uri) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_conference_suggestion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText updateTopic = (EditText) promptView.findViewById(R.id.etxt_topic);
        final EditText updateDescription = (EditText) promptView.findViewById(R.id.etxt_desc);
         updateDate = (TextView) promptView.findViewById(R.id.txt_date);
        final Button updateDateButton = (Button) promptView.findViewById(R.id.btn_date);

        Cursor cursor = getContentResolver().query(uri, AdminActivity.SUGGESTION_COLUMNS, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            suggestionID = Long.toString(DataContract.SuggestionEntry.getSuggestionIDFromUri(uri));
            updateTopic.setText(cursor.getString(AdminActivity.COLUMN_TOPIC));
            updateDescription.setText(cursor.getString(AdminActivity.COLUMN_DESCRIPTION));
            updateDate.setText(cursor.getString(AdminActivity.COLUMN_AVAILABILITY_DATE));
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
                            Toast.makeText(DoctorActivity.this, "Please Enter all fields", Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_USER_ID, Utils.getUserId(DoctorActivity.this));
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_TOPIC, s_Topic);
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_DESCRIPTION, s_Description);
                            contentValues.put(DataContract.SuggestionEntry.COLUMN_AVAILABILITY_DATE, s_Date);

                            updated = getContentResolver()
                                    .update(DataContract.SuggestionEntry.CONTENT_URI, contentValues,
                                            DataProvider.sSuggestionIDSelection, new String[]{suggestionID});

                            Toast.makeText(DoctorActivity.this, updated + " Suggestion is Updated.", Toast.LENGTH_SHORT)
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
                        new AlertDialog.Builder(DoctorActivity.this)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this suggestion?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        updated = getContentResolver()
                                                .delete(DataContract.SuggestionEntry.CONTENT_URI,
                                                        DataProvider.sSuggestionIDSelection,
                                                        new String[]{suggestionID});

                                        Toast.makeText(DoctorActivity.this, updated + " Suggestion has been Deleted.",
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

    @Override
    public void onSuggestionSelected(Uri uri) {
        editSuggestion(uri);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            globalString = new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ");
            if (updateDate!=null)updateDate.setText(globalString);
            if (txt_date!=null)txt_date.setText(globalString);

        }
    }
}

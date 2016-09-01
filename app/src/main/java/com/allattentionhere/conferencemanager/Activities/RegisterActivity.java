package com.allattentionhere.conferencemanager.Activities;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.allattentionhere.conferencemanager.Data.DataContract;
import com.allattentionhere.conferencemanager.Helper.Constants;
import com.allattentionhere.conferencemanager.Helper.Utils;
import com.allattentionhere.conferencemanager.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView actv_email;
    private EditText etxt_password;
    private EditText etxt_name;
    private EditText etxt_age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        populateAutoComplete();
        setListener();

    }

    private void setListener() {

        findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataValid()) {
                    doRegister(actv_email.getText().toString(), etxt_password.getText().toString(), etxt_name.getText().toString(), etxt_age.getText().toString());
                }
            }
        });
    }

    private boolean isDataValid() {
        // Reset errors.
        actv_email.setError(null);
        etxt_password.setError(null);

        // Store values at the time of the login attempt.
        String email = actv_email.getText().toString();
        String password = etxt_password.getText().toString();
        String name = etxt_name.getText().toString();
        String age = etxt_age.getText().toString();

        boolean isValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            actv_email.setError(getString(R.string.error_invalid_email));
            focusView = actv_email;
            isValid = false;
        } else if (TextUtils.isEmpty(password) || (password.length() < 3)) {
            etxt_password.setError(getString(R.string.error_invalid_password));
            focusView = etxt_password;
            isValid = false;
        } else if (TextUtils.isEmpty(name) || name.trim().length() < 3) {
            etxt_name.setError(getString(R.string.error_invalid_name));
            focusView = etxt_name;
            isValid = false;
        } else if (TextUtils.isEmpty(age)) {
            etxt_age.setError(getString(R.string.error_invalid_age));
            focusView = etxt_age;
            isValid = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }

        return isValid;
    }

    private void init() {
        actv_email = (AutoCompleteTextView) findViewById(R.id.actv_email);
        etxt_password = (EditText) findViewById(R.id.etxt_password);
        etxt_name = (EditText) findViewById(R.id.etxt_name);
        etxt_age = (EditText) findViewById(R.id.etxt_age);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(actv_email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void doRegister(String etxt_username, String etxt_password, String name, String age) {
        RadioButton rb_user = (RadioButton) findViewById(((RadioGroup) findViewById(R.id.rg_usertype)).getCheckedRadioButtonId());
        String usertype = rb_user.getText().toString();
        if (usertype.equals(getString(R.string.rb_doctor))) {
            Utils.setUserType(RegisterActivity.this, Constants.USERTYPE_DOCTOR);
        } else {
            Utils.setUserType(RegisterActivity.this, Constants.USERTYPE_ADMIN);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.AdminEntry.COLUMN_USERNAME, etxt_username);
        contentValues.put(DataContract.AdminEntry.COLUMN_PASSWORD, etxt_password);
        contentValues.put(DataContract.AdminEntry.COLUMN_NAME, name);
        contentValues.put(DataContract.AdminEntry.COLUMN_AGE, age);
        Uri uri;
        try {
            if (Utils.getUserType(this).equals(Constants.USERTYPE_ADMIN)) {
                Log.d("insert", "admin");
                uri = getContentResolver().insert(DataContract.AdminEntry.CONTENT_URI, contentValues);
            } else {
                Log.d("insert", "doctor");
                uri = getContentResolver().insert(DataContract.DoctorEntry.CONTENT_URI, contentValues);
            }
            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        actv_email.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


}


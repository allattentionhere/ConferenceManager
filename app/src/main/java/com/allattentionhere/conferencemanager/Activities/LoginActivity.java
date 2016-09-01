package com.allattentionhere.conferencemanager.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.conferencemanager.Data.DataContract;
import com.allattentionhere.conferencemanager.Helper.Constants;
import com.allattentionhere.conferencemanager.Helper.Utils;
import com.allattentionhere.conferencemanager.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView actv_email;
    private EditText etxt_password;
    private View pb_loginprogress;
    private View sv_form;

    private static final String[] ADMIN_PROJECTION = new String[]{
            DataContract.AdminEntry.TABLE_NAME + "." + DataContract.AdminEntry._ID,
            DataContract.AdminEntry.COLUMN_USERNAME,
            DataContract.AdminEntry.COLUMN_PASSWORD,
            DataContract.AdminEntry.COLUMN_NAME

    };

    private static final String[] DOCTOR_PROJECTION = new String[]{
            DataContract.DoctorEntry.TABLE_NAME + "." + DataContract.DoctorEntry._ID,
            DataContract.DoctorEntry.COLUMN_USERNAME,
            DataContract.DoctorEntry.COLUMN_PASSWORD,
            DataContract.DoctorEntry.COLUMN_NAME
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_USERNAME = 1;
    private static final int COLUMN_PASSWORD = 2;
    private static final int COLUMN_NAME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Utils.getUserId(this) > 0 && Utils.getUserName(this) != null) {
            startActivity(new Intent(this, ((Utils.getUserType(this).equals(Constants.USERTYPE_DOCTOR))) ? DoctorActivity.class : AdminActivity.class));
        }
        init();
        populateAutoComplete();
        setListener();

    }

    private void setListener() {
        etxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    doLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn_signin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //try login
                doLogin();
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void init() {
        actv_email = (AutoCompleteTextView) findViewById(R.id.actv_email);
        etxt_password = (EditText) findViewById(R.id.etxt_password);
        sv_form = findViewById(R.id.sv_form);
        pb_loginprogress = findViewById(R.id.pb_loginprogress);
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
                    .setAction(android.R.string.ok, new View.OnClickListener() {
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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void doLogin() {

        // Reset errors.
        actv_email.setError(null);
        etxt_password.setError(null);

        // Store values at the time of the login attempt.
        String email = actv_email.getText().toString();
        String password = etxt_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            etxt_password.setError(getString(R.string.error_invalid_password));
            focusView = etxt_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            actv_email.setError(getString(R.string.error_field_required));
            focusView = actv_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            actv_email.setError(getString(R.string.error_invalid_email));
            focusView = actv_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RadioButton rb_user = (RadioButton) findViewById(((RadioGroup) findViewById(R.id.rg_usertype)).getCheckedRadioButtonId());
            String usertype = rb_user.getText().toString();
            if (usertype.equals(getString(R.string.rb_doctor))) {
                Utils.setUserType(LoginActivity.this, Constants.USERTYPE_DOCTOR);
            } else {
                Utils.setUserType(LoginActivity.this, Constants.USERTYPE_ADMIN);
            }

            //perform login
            if (doLogin(actv_email.getText().toString(), etxt_password.getText().toString())) {
                Toast.makeText(LoginActivity.this, "User found", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ((Utils.getUserType(this).equals(Constants.USERTYPE_DOCTOR))) ? DoctorActivity.class : AdminActivity.class));
            }
        }
    }

    private boolean isEmailValid(String email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            actv_email.setError("Enter valid email");
            actv_email.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 3);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        sv_form.setVisibility(show ? View.GONE : View.VISIBLE);
        sv_form.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sv_form.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        pb_loginprogress.setVisibility(show ? View.VISIBLE : View.GONE);
        pb_loginprogress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pb_loginprogress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
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
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
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

    private boolean doLogin(String actv_username, String etxt_password) {
        boolean proceed = false;
        if ((Utils.getUserType(LoginActivity.this)).equals(Constants.USERTYPE_ADMIN)) {
            Log.d("insert", "login user admin");
            Uri adminUri = DataContract.AdminEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(adminUri, ADMIN_PROJECTION, null, null, null);

            int adminId = 0;
            String username = null, name = null;
            String password;

            if (cursor == null) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Please register before logging in as Admin", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                while (cursor.moveToNext()) {
                    adminId = cursor.getInt(COLUMN_ID);
                    username = cursor.getString(COLUMN_USERNAME);
                    password = cursor.getString(COLUMN_PASSWORD);
                    name = cursor.getString(COLUMN_NAME);
                    if (actv_username.equals(username) && etxt_password.equals(password)) {
                        proceed = true;
                        break;
                    }
                }
            }

            if (proceed) {
                Utils.setUserId(LoginActivity.this, adminId);
                Utils.setUserName(LoginActivity.this, name);
                return true;
            } else {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                cursor.close();
                return false;
            }


        } else if (((Utils.getUserType(LoginActivity.this)).equals(Constants.USERTYPE_DOCTOR))) {
            Log.d("insert", "login user doctor");

            Uri doctorUri = DataContract.DoctorEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(doctorUri, DOCTOR_PROJECTION, null, null, null);

            int doctorId = 0;
            String username = null,name=null;
            String password;


            if (cursor == null) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Please register before logging in as Doctor", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                while (cursor.moveToNext()) {
                    doctorId = cursor.getInt(COLUMN_ID);
                    username = cursor.getString(COLUMN_USERNAME);
                    password = cursor.getString(COLUMN_PASSWORD);
                    name = cursor.getString(COLUMN_NAME);

                    if (actv_username.equals(username) && etxt_password.equals(password)) {
                        proceed = true;
                        break;
                    }
                }
            }

            if (proceed) {
                Utils.setUserId(LoginActivity.this, doctorId);
                Utils.setUserName(LoginActivity.this, name);
                return true;
            } else {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                cursor.close();
                return false;
            }

        }

        return false;
    }

}


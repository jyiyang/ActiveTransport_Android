package activetransportation.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 *  An activity that creates a login screen offering login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public final static String CHECKLIST = "ActiveTransport.CHECKLIST";
    public final static String PASSWORD = "ActiveTransport.PASSWORD";
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String userEmail;
    public Boolean userApproved = false;

    // A local class to hold the results of login process
    private class LoginResults {
        private Boolean authSuccess;
        private FirebaseError loginError;
        public LoginResults (FirebaseError error, Boolean success) {
             this.authSuccess = success;
             this.loginError = error;
        }
        public void setError(FirebaseError error) {
            this.loginError = error;
        }

        public void setAuthSuccess(Boolean success) {
            this.authSuccess = success;
        }
        public Boolean getAuthSuccess() {
            return this.authSuccess;
        }
        public int getErrorCode() {
            return this.loginError.getCode();
        }
        public FirebaseError getError() {
            return this.loginError;
        }
    }
    private LoginResults loginResults = new LoginResults(FirebaseError.fromCode(FirebaseError.UNKNOWN_ERROR),false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);

        // Set up the login form
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Initialize the buttons to login, register, or send reset email
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
        Button mForgetPassword = (Button) findViewById(R.id.forget_password);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
        mForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPassword();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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

    private void startRegister() {
        finish();
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(myIntent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt
            showProgress(true);
            userEmail = email;
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void forgetPassword() {
        mEmailView.setError(null);
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt sending email
            // form field with an error
            focusView.requestFocus();
        } else {
            // Send change password email to user email address
            Firebase ref = new Firebase(FIREBASE_URL);
            ref.resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Context context = getApplicationContext();
                    CharSequence text = "Password changing email has been successfully sent!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    // error encountered
                    Context context = getApplicationContext();
                    CharSequence text = "Fail to send Email: "+firebaseError;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses
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
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, LoginResults> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected LoginResults doInBackground(Void... params) {

            Firebase ref = new Firebase(FIREBASE_URL);

            ref.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                    loginResults.setAuthSuccess(true);
                    userEmail = mEmail;
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // There was an error
                    loginResults.setAuthSuccess(false);
                    loginResults.setError(firebaseError);
                }
            });

            try {
                // Simulate network access
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return loginResults;
            }
            return loginResults;
        }

        @Override
        protected void onPostExecute(final LoginResults loginResults) {
            mAuthTask = null;
            showProgress(false);

            Firebase ref = new Firebase(FIREBASE_URL);
            Query userRef = ref.child("users").orderByChild("email").equalTo(userEmail);

            if (loginResults.getAuthSuccess()) {
                // Check if the user has routeID or childrenIDs to make sure the user is already approved and assigned 
                // a route or a list of children
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Map<String, Object> userMap = (Map<String, Object>) postSnapshot.getValue();
                            if (userMap.get("routeID") != null || userMap.get("childrenIDs") != null) {
                                userApproved = true;
                            }
                        }

                        if (userApproved) {
                            // Transit to check list screen and pass in password and email
                            finish();
                            Intent myIntent = new Intent(LoginActivity.this, ChecklistActivity.class);
                            myIntent.putExtra(CHECKLIST, mEmail);
                            myIntent.putExtra(PASSWORD, mPassword);
                            myIntent.putExtra("from", "login");

                            LoginActivity.this.startActivity(myIntent);
                            finish();
                        } else {
                            // User not approved, do not show check list
                            Context context = getApplicationContext();
                            CharSequence text = "User is not approved by school admin!";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            } else {
                // If the login was unsuccessful, catch some common errors.
                switch (loginResults.loginError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST: {
                        mEmailView.setError("User does not exist");
                        mEmailView.requestFocus();
                        break;
                    }
                    case FirebaseError.INVALID_PASSWORD: {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        break;
                    }
                    case FirebaseError.INVALID_EMAIL: {
                        mEmailView.setError("Please input a valid email");
                        mEmailView.requestFocus();
                        break;
                    }
                    case FirebaseError.NETWORK_ERROR: {
                        Context context = getApplicationContext();
                        CharSequence text = "Network error, try again later";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        break;
                    }
                    default: {
                        // Show default "unknown error" message
                        Context context = getApplicationContext();
                        CharSequence text = loginResults.loginError.toString();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        break;
                    }
                }
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


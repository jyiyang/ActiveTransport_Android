package activetransportation.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 *  An activity that creates a register screen.
 */
public class RegisterActivity extends AppCompatActivity {

    // Database link
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    // UI attributes
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private Button mRegister;
    private RadioButton mIsStaff;
    private RadioButton mIsParent;
    private View mProgressView;
    private View mRegisterFormView;

    // Data class attributes
    private String id;
    private Boolean registerSuccess = false;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (EditText) findViewById(R.id.reg_email);
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        mPhoneView = (EditText) findViewById(R.id.phone_num);
        mPasswordView = (EditText) findViewById(R.id.reg_password);
        mRePasswordView = (EditText) findViewById(R.id.reenter_password);
        mRegister = (Button) findViewById(R.id.register_button);

        mIsStaff = (RadioButton) findViewById(R.id.radio_staff);
        mIsParent = (RadioButton) findViewById(R.id.radio_parent);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mPhoneView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);

        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for none empty entries.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(rePassword)) {
            mRePasswordView.setError(getString(R.string.error_field_required));
            focusView = mRePasswordView;
            cancel = true;
        }


        // Check for a valid password and a valid reenter
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !password.equals(rePassword)){
            mRePasswordView.setError("Please make sure the passwords match");
            focusView = mRePasswordView;
            cancel = true;
        }

        // Check for a valid phone number
        if (!TextUtils.isEmpty(phone) && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            mPhoneView.setError("Please input a valid phone number");
            focusView = mPhoneView;
            cancel = true;
        }

        if (!mIsStaff.isChecked() && !mIsParent.isChecked()) {
            mIsStaff.setError("Please specify if you are a staff or a parent");
            focusView = mIsParent;
            cancel = true;
        }

        boolean isStaff;
        if (mIsStaff.isChecked()) {
            isStaff = true;
        } else {
            isStaff = false;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
                showProgress(true);
                mAuthTask = new UserRegisterTask(firstName, lastName,phone, email, password, isStaff);
                mAuthTask.execute((Void) null);
            }

    }

    public User createUser(String id, String firstName, String lastName,  String email,  String phone,
                           boolean isStaff)
    {
        return (new User (id, email, firstName + " " + lastName, phone, isStaff));
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }


    public void putUser(User user, Firebase usersRef) {
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("name", user.getName());
        userMap.put("isStaff", user.getIsStaff());
        userMap.put("email", user.getEmail());
        userMap.put("contactInfo", user.getContactInfo());
        userMap.put("uid", user.getUserID());
        Firebase stfRef = usersRef.child(user.getUserID());
        stfRef.setValue(userMap);
        user.setUserID(stfRef.getKey());
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mLastName;
        private final String mFirstName;
        private final String mPhone;
        private final boolean mIsStaff;

        UserRegisterTask(String firstName, String lastName, String phone, String email, String password, boolean isStaff) {
            mFirstName = firstName;
            mLastName = lastName;
            mEmail = email;
            mPassword = password;
            mPhone = phone;
            mIsStaff = isStaff;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            Firebase ref = new Firebase(FIREBASE_URL);
            ref.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    id = result.get("uid").toString();
                    Firebase ref = new Firebase(FIREBASE_URL);
                    User newUser = createUser(id, mFirstName, mLastName, mEmail, mPhone, mIsStaff);
                    Firebase usersRef = ref.child("users");
                    putUser(newUser, usersRef);

                    finish();
                    Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(myIntent);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    System.out.println(firebaseError);
                    registerSuccess = false;
                    Context context = getApplicationContext();
                    CharSequence text = "Registration Failed; " + firebaseError.toString();
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

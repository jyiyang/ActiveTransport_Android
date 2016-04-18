package activetransportation.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

/**
 * Created by KritiKAl on 4/15/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mOldPasswordView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private Button mSave;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Intent intent = getIntent();

        mEmailView = (EditText) findViewById(R.id.change_email);
        mPhoneView = (EditText) findViewById(R.id.change_phone);
        mOldPasswordView = (EditText) findViewById(R.id.old_password);
        mPasswordView = (EditText) findViewById(R.id.change_password);
        mRePasswordView = (EditText) findViewById(R.id.change_reenter_password);

        mPhoneView.setError(null);
        mEmailView.setError(null);
        mOldPasswordView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);
        mSave = (Button) findViewById(R.id.save_settings);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptChange(intent);
            }
        });
    }

    private void attemptChange(Intent intent) {

        String oldPasswordT = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
        final String oldEmail = intent.getStringExtra(ChecklistActivity.OLDEMAIL);
        String phone = mPhoneView.getText().toString();
        final String email = mEmailView.getText().toString();
        String oldPassword = mOldPasswordView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        boolean changePassword = false;
        boolean changePhone = false;
        boolean changeEmail = false;

        Firebase ref = new Firebase(FIREBASE_URL);

        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase userRef = ref.child("users").child(uid);

        if (!TextUtils.isEmpty(phone)) {
            if(!android.util.Patterns.PHONE.matcher(phone).matches()) {
                mPhoneView.setError("Please input a valid phone number");
                focusView = mPhoneView;
                cancel = true;
            } else {
                changePhone = true;
            }
        }

        if (!TextUtils.isEmpty(email)) {
            if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            } else {
                changeEmail = true;
            }
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && password.equals(rePassword)){
            if (oldPassword.equals(oldPasswordT)) {
                changePassword = true;
            } else {
                mOldPasswordView.setError("Wrong Password");
                focusView = mOldPasswordView;
                cancel = true;
            }
        }

        if (!TextUtils.isEmpty(password) && !password.equals(rePassword)) {
            mRePasswordView.setError("Please make sure the passwords match");
            focusView = mRePasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (changePhone) {
                userRef.child("contactInfo").setValue(phone);
            }

            if (changeEmail) {
                ref.changeEmail(oldEmail, oldPasswordT, email, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        userRef.child("email").setValue(email);
                        Context context = getApplicationContext();
                        CharSequence text = "Successfully changed email";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Context context = getApplicationContext();
                        CharSequence text = "Email change failed: " + firebaseError;
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
                try {
                    // Simulate network access.
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }

            }
            if (changePassword) {
                ref.changePassword(oldEmail, oldPasswordT, password, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        Context context = getApplicationContext();
                        CharSequence text = "Successfully changed password";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Context context = getApplicationContext();
                        CharSequence text = "Password change failed: " + firebaseError;
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
                try {
                    // Simulate network access.
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
            }
            Context context = getApplicationContext();
            CharSequence text = "Changes saved";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Intent ChecklistIntent = new Intent(this, ChecklistActivity.class);
            startActivity(ChecklistIntent);
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

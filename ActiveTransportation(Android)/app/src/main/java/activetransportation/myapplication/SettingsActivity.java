package activetransportation.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by KritiKAl on 4/15/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";
    private Button mEmailView;
    private Button mPhoneView;
    private EditText mOldPasswordInput;
    private EditText mPasswordInput;
    private EditText mRePasswordInput;
    private Button mChangePasswordView;
    private Button mLogOut;
    private final Context context = this;
    private EditText mPhoneInput;
    private EditText mEmailInput;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Intent intent = getIntent();
        final String oldPassword = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
        final String oldEmail = intent.getStringExtra(ChecklistActivity.OLDEMAIL);

        mEmailView = (Button) findViewById(R.id.change_email);
        mPhoneView = (Button) findViewById(R.id.change_phone);
        mChangePasswordView = (Button) findViewById(R.id.change_password);

        mLogOut = (Button) findViewById(R.id.log_out);

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(oldEmail,oldPassword, false);
            }
        });
        mPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoneNumber(intent);
            }
        });
        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail(intent);
            }
        });
        mChangePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(intent);
            }
        });
    }


    private void changePhoneNumber(Intent intent) {
        Firebase ref = new Firebase(FIREBASE_URL);
        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase userRef = ref.child("users").child(uid);
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_changephone, null);

        mPhoneInput = (EditText) promptsView.findViewById(R.id.change_phone_input);
        mPhoneInput.setError(null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String phone = mPhoneInput.getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    mPhoneInput.setError(getString(R.string.error_field_required));
                    mPhoneInput.requestFocus();
                }
                if (!TextUtils.isEmpty(phone)) {
                    if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
                        mPhoneInput.setError("Please input a valid phone number");
                        mPhoneInput.requestFocus();
                    } else {
                        userRef.child("contactInfo").setValue(phone);
                        wantToCloseDialog = true;
                    }
                }
                if (wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    private void changeEmail(Intent intent) {
        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase ref = new Firebase(FIREBASE_URL);
        final String oldPassword = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
        final String oldEmail = intent.getStringExtra(ChecklistActivity.OLDEMAIL);

        final Firebase userRef = ref.child("users").child(uid);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_changeemail, null);

        mEmailInput = (EditText) promptsView.findViewById(R.id.change_email_input);
        mOldPasswordInput = (EditText) promptsView.findViewById(R.id.old_password_input);
        mEmailInput.setError(null);
        mOldPasswordInput.setError(null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                final String oldPasswordIn = mOldPasswordInput.getText().toString();
                final String email = mEmailInput.getText().toString();
                Boolean cancel = false;

                if (TextUtils.isEmpty(oldPasswordIn)) {
                    mOldPasswordInput.setError(getString(R.string.error_field_required));
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError(getString(R.string.error_field_required));
                    mEmailInput.requestFocus();
                    cancel = true;
                }
                if (!oldPasswordIn.equals(oldPassword)) {
                    mOldPasswordInput.setError("Please input correct password");
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                }
                if (email.equals(oldEmail)) {
                    mEmailInput.setError("Please input a new Email address");
                    mEmailInput.requestFocus();
                    cancel = true;
                }

                if (!TextUtils.isEmpty(email)) {
                    if (!isEmailValid(email)) {
                        mEmailInput.setError(getString(R.string.error_field_required));
                        mEmailInput.requestFocus();
                        cancel = true;
                    }
                }

                if(!cancel) {
                    ref.changeEmail(oldEmail, oldPasswordIn, email, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            userRef.child("email").setValue(email);
                            Context context = getApplicationContext();
                            CharSequence text = "Successfully changed email, logging out";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            logout(email,oldPasswordIn,true);
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
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    private void changePassword(Intent intent) {
        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase ref = new Firebase(FIREBASE_URL);
        final String oldPassword = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
        final String oldEmail = intent.getStringExtra(ChecklistActivity.OLDEMAIL);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_changepassword, null);

        mOldPasswordInput = (EditText) promptsView.findViewById(R.id.old_password_input);
        mPasswordInput = (EditText) promptsView.findViewById(R.id.change_password_input);
        mRePasswordInput = (EditText) promptsView.findViewById(R.id.change_reenter_password);
        mOldPasswordInput.setError(null);
        mPasswordInput.setError(null);
        mRePasswordInput.setError(null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // do nothing
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String oldPasswordIn = mOldPasswordInput.getText().toString();
                final String password = mPasswordInput.getText().toString();
                final String rePassword = mRePasswordInput.getText().toString();
                Boolean cancel = false;

                if (TextUtils.isEmpty(oldPasswordIn)) {
                    mOldPasswordInput.setError(getString(R.string.error_field_required));
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                }
                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError(getString(R.string.error_field_required));
                    mPasswordInput.requestFocus();
                    cancel = true;
                }
                if (TextUtils.isEmpty(rePassword)) {
                    mRePasswordInput.setError(getString(R.string.error_field_required));
                    mRePasswordInput.requestFocus();
                    cancel = true;
                }

                if (!oldPasswordIn.equals(oldPassword)) {
                    mOldPasswordInput.setError("Please input correct password");
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                    System.out.println(oldPassword);
                }

                if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                    mPasswordInput.setError(getString(R.string.error_invalid_password));
                    mPasswordInput.requestFocus();
                    cancel = true;
                }

                if (!TextUtils.isEmpty(password) && password.equals(oldPassword)) {
                    mPasswordInput.setError("Please enter a new password.");
                    mPasswordInput.requestFocus();
                    cancel = true;
                }


                if (!TextUtils.isEmpty(password) && !password.equals(rePassword)) {
                    mRePasswordInput.setError("Please make sure the passwords match");
                    mRePasswordInput.requestFocus();
                    cancel = true;
                }


                if(!cancel) {
                    ref.changePassword(oldEmail, oldPasswordIn, password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            Context context = getApplicationContext();
                            CharSequence text = "Successfully changed password, logging out";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            logout(oldEmail,password,true);

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
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    private void logout(String newEmail, String newPassword, Boolean change) {
        final Firebase ref = new Firebase(FIREBASE_URL);
        ref.unauth();


        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);

    }


//    private void attemptChange(Intent intent) {
//
//        String oldPasswordT = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
//        final String oldEmail = intent.getStringExtra(ChecklistActivity.OLDEMAIL);
//        String phone = mPhoneView.getText().toString();
//        final String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//        String rePassword = mRePasswordView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//        boolean changePassword = false;
//        boolean changePhone = false;
//        boolean changeEmail = false;
//
//        Firebase ref = new Firebase(FIREBASE_URL);
//
//        String uid = intent.getStringExtra(ChecklistActivity.USERID);
//        final Firebase userRef = ref.child("users").child(uid);
//
//        if (!TextUtils.isEmpty(phone)) {
//            if(!android.util.Patterns.PHONE.matcher(phone).matches()) {
//                mPhoneView.setError("Please input a valid phone number");
//                focusView = mPhoneView;
//                cancel = true;
//            } else {
//                changePhone = true;
//            }
//        }
//
//        if (!TextUtils.isEmpty(email)) {
//            if (!isEmailValid(email)) {
//                mEmailView.setError(getString(R.string.error_invalid_email));
//                focusView = mEmailView;
//                cancel = true;
//            } else {
//                changeEmail = true;
//            }
//        }
//
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//
//        if (!TextUtils.isEmpty(password) && !password.equals(rePassword)) {
//            mRePasswordView.setError("Please make sure the passwords match");
//            focusView = mRePasswordView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            focusView.requestFocus();
//        } else {
//            if (changePhone) {
//                userRef.child("contactInfo").setValue(phone);
//            }
//
//            if (changeEmail) {
//                ref.changeEmail(oldEmail, oldPasswordT, email, new Firebase.ResultHandler() {
//                    @Override
//                    public void onSuccess() {
//                        userRef.child("email").setValue(email);
//                        Context context = getApplicationContext();
//                        CharSequence text = "Successfully changed email";
//                        int duration = Toast.LENGTH_SHORT;
//
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
//                    }
//
//                    @Override
//                    public void onError(FirebaseError firebaseError) {
//                        Context context = getApplicationContext();
//                        CharSequence text = "Email change failed: " + firebaseError;
//                        int duration = Toast.LENGTH_SHORT;
//
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
//                    }
//                });
//                try {
//                    // Simulate network access.
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//
//                }
//
//            }
//            if (changePassword) {
//                ref.changePassword(oldEmail, oldPasswordT, password, new Firebase.ResultHandler() {
//                    @Override
//                    public void onSuccess() {
//                        Context context = getApplicationContext();
//                        CharSequence text = "Successfully changed password";
//                        int duration = Toast.LENGTH_SHORT;
//
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
//
//                    }
//                    @Override
//                    public void onError(FirebaseError firebaseError) {
//                        Context context = getApplicationContext();
//                        CharSequence text = "Password change failed: " + firebaseError;
//                        int duration = Toast.LENGTH_SHORT;
//
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
//                    }
//                });
//                try {
//                    // Simulate network access.
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//
//                }
//            }
//            Context context = getApplicationContext();
//            CharSequence text = "Changes saved";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//
//            Intent ChecklistIntent = new Intent(this, ChecklistActivity.class);
//            startActivity(ChecklistIntent);
//        }
//
//    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 0;
    }
}

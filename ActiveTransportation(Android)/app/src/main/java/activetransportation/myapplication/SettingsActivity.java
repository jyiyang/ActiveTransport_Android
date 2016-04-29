package activetransportation.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 *  An activity that creates the settings screen.
 *  Users can change their phone numbers, passwords, or emails using their old passwords
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
    private String newPassword;
    private String newEmail;
    private String Password;
    private String Email;

    public final static String NEWPASSWORD = "ActiveTransport.NEWPASSWORD";
    public final static String NEWEMAIL = "ActiveTransport.NEWEMAIL";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Intent intent = getIntent();
        Password = intent.getStringExtra(ChecklistActivity.OLDPASSWORD);
        Email = intent.getStringExtra(ChecklistActivity.OLDEMAIL);

        mEmailView = (Button) findViewById(R.id.change_email);
        mPhoneView = (Button) findViewById(R.id.change_phone);
        mChangePasswordView = (Button) findViewById(R.id.change_password);

        mLogOut = (Button) findViewById(R.id.log_out);

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out the user
                logout();
            }
        });
        // Change phone number, email, or password by popin a new dialog
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

//Keep pop up dialog alive after rotation.
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    private void changePhoneNumber(Intent intent) {
        Firebase ref = new Firebase(FIREBASE_URL);
        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase userRef = ref.child("users").child(uid);
        // Get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_changephone, null);

        mPhoneInput = (EditText) promptsView.findViewById(R.id.change_phone_input);
        mPhoneInput.setError(null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // Set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // Set dialog message
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

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Show the dialog
        alertDialog.show();
        doKeepDialog(alertDialog);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String phone = mPhoneInput.getText().toString();
                // Phone number input sanity checks.
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
            }
        });
    }

    private void changeEmail(Intent intent) {
        String uid = intent.getStringExtra(ChecklistActivity.USERID);
        final Firebase ref = new Firebase(FIREBASE_URL);

        final Firebase userRef = ref.child("users").child(uid);

        // Get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_changeemail, null);

        mEmailInput = (EditText) promptsView.findViewById(R.id.change_email_input);
        mOldPasswordInput = (EditText) promptsView.findViewById(R.id.old_password_input);
        mOldPasswordInput.requestFocus();
        mEmailInput.setError(null);
        mOldPasswordInput.setError(null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // Set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // Set dialog message
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

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Show the dialog
        alertDialog.show();
        doKeepDialog(alertDialog);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                final String oldPasswordIn = mOldPasswordInput.getText().toString();
                final String email = mEmailInput.getText().toString();
                Boolean cancel = false;
                // Password and email input sanity checks.
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
                if (!oldPasswordIn.equals(Password)) {
                    mOldPasswordInput.setError("Please input correct password");
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                }
                if (email.equals(Email)) {
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
                    // Call Firebase function to change the email 
                    ref.changeEmail(Email, oldPasswordIn, email, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            userRef.child("email").setValue(email);
                            Context context = getApplicationContext();
                            CharSequence text = "Successfully changed email.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Email = email;
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
                        // Sleep to ensure the change email function finishes
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open.
            }
        });
    }

    private void changePassword(Intent intent) {
        final Firebase ref = new Firebase(FIREBASE_URL);

        // Get prompts.xml view
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
        // Set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // Set dialog message
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

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Show the dialog
        alertDialog.show();
        doKeepDialog(alertDialog);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String oldPasswordIn = mOldPasswordInput.getText().toString();
                final String password = mPasswordInput.getText().toString();
                final String rePassword = mRePasswordInput.getText().toString();
                Boolean cancel = false;

                // Old password and new message input sanity checks.
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

                if (!oldPasswordIn.equals(Password)) {
                    mOldPasswordInput.setError("Please input correct password");
                    mOldPasswordInput.requestFocus();
                    cancel = true;
                    System.out.println(Password);
                }

                if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                    mPasswordInput.setError(getString(R.string.error_invalid_password));
                    mPasswordInput.requestFocus();
                    cancel = true;
                }

                if (!TextUtils.isEmpty(password) && password.equals(Password)) {
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
                    // Call the firebase funcions to change the user password using email and oldpassword
                    ref.changePassword(Email, oldPasswordIn, password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            Context context = getApplicationContext();
                            CharSequence text = "Successfully changed password.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Password = password;

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
                        // Sleep to ensure the firebase function finishes
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    alertDialog.dismiss();
            }
        });
    }

    // Log out the user and return to login activity
    private void logout() {
        final Firebase ref = new Firebase(FIREBASE_URL);
        ref.unauth();

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);

    }

    // Return to check list activity if the back button is pressed
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, ChecklistActivity.class);
        // Pass in the new password and new email in case they are changed
        intent.putExtra("from", "settings");
        intent.putExtra(NEWEMAIL,Email);
        intent.putExtra(NEWPASSWORD,Password);
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        // Naive email validation, firebase does it
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        // Naive password validation
        return password.length() >= 6;
    }
}

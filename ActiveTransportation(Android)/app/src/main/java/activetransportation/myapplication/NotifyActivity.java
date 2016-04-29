package activetransportation.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 *  An activity that creates a notify screen.
 */
public class NotifyActivity extends AppCompatActivity {

    // Database link
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    // Data class attributes
    private String timeOfDay;
    private boolean isStaff;
    private ArrayList<String> stuIDs;
    private String routeID;

    // View attributes
    private EditText msg;

    // Data structures for storage
    // Use a hashmap to get student's parent's contact info
    private final Map<String, String> stuContactMap = new HashMap<String, String>();
    // Use a hashmap to store student's parent's ID
    private final Map<String, String> stuParentMap = new HashMap<String, String>();
    // Use a hashmap to store whether a student is present;
    private final Map<String, Boolean> stuArriveMap = new HashMap<String, Boolean>();
    // Use a hashmap to store whether a student is present;
    private final Map<String, String> stuNameMap = new HashMap<String, String>();

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;


    /** Switch to the ChecklistActivity when the user clicks on the corresponding button. */
    public void switchChecklist(View view) {

        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }

    /** Switch to the TimeAndLocationActivity when the user clicks on the corresponding button. */
    public void switchTimeAndLoc(View view) {
        Intent intent = new Intent(this, TimeAndLocationActivity.class);
        if (isStaff) {
            intent.putExtra(ChecklistActivity.ROUTEID, routeID);
        }
        intent.putExtra(ChecklistActivity.STUIDS, stuIDs);
        intent.putExtra(ChecklistActivity.ISSTAFF, isStaff);
        startActivity(intent);
    }

    /** Switch to the (current) NotifyActivity when the user clicks on the corresponding button. */
    public void switchNotify(View view) {
        Intent intent = new Intent(this, NotifyActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_notify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get intents from main checklist activity
        Intent intent = getIntent();
        stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);
        isStaff = intent.getExtras().getBoolean(ChecklistActivity.ISSTAFF);
        routeID = intent.getExtras().getString(ChecklistActivity.ROUTEID);

        // Time activity
        GregorianCalendar time = new GregorianCalendar();
        if (time.get(Calendar.AM_PM) == Calendar.PM) {
            timeOfDay = "afternoon";
        }
        else {
            timeOfDay = "morning";
        }

        // Register the message receiver to inquire sms sending and delivering status
        sendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // For different message sending status code
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message has been sent!", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Fail to send text: Generic failure. Please check your mobile connection.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Fail to send text: No service. Please contact your service carrier.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Fail to send text: Radio off. Please check your mobile connection. ", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        deliveryBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // For different message delivering status code
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message has been delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS did not successfully deliver",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(sendBroadcastReceiver, new IntentFilter("SENT"));
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter("DELIVERED"));




        // Obtain time string
        final String timeString =
                android.text.format.DateFormat.format("yyyy-MM-dd", time).toString();

        final Firebase ref = new Firebase(FIREBASE_URL);

        for (String stuID : stuIDs) {
            final Query stuRef = ref.child("students").orderByKey().equalTo(stuID);
            stuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // For each child, get its data snapshot
                        Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                        final String sID = postSnapshot.getKey();
                        String parentID = (String) stuMap.get("parentID");
                        String stuName = (String) stuMap.get("name");

                        // If the student-parent pair does not exist, add it into the map
                        if (!stuParentMap.containsKey(sID)) {
                            stuParentMap.put(sID, parentID);
                        }
                        // If the student-name pair does not exist, add it into the map
                        if (!stuNameMap.containsKey(sID)) {
                            stuNameMap.put(sID, stuName);
                        }

                        // Nested listener for parent's contact info
                        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String uID = postSnapshot.getKey();

                                    // If find the corresponding parent user
                                    if (uID.equals(stuParentMap.get(sID))) {
                                        // Obtain the user's phone num and put it into parMap
                                        Map<String, Object> parMap = (Map<String, Object>) postSnapshot.getValue();
                                        String phoneNum = (String) parMap.get("contactInfo");
                                        stuContactMap.put(sID, phoneNum);
                                    }
                                }

                                ref.child("logs").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean logArrived;
                                        if (dataSnapshot.child(timeString).child(timeOfDay).hasChild(sID)) {
                                            logArrived = (Boolean) dataSnapshot.child(timeString).child(timeOfDay).child(sID).getValue();
                                            // If the student-arrived pair does not exist, add it into the map
                                            if (!stuArriveMap.containsKey(sID)) {
                                                stuArriveMap.put(sID, logArrived);
                                            }
                                        }
                                        else {
                                            System.out.println("System log does not exist for this student!");
                                        }
                                        msg = (EditText) findViewById(R.id.msg_content);
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        System.out.println("The read failed: " + firebaseError.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });
                    }
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }

    /** A function that text all parents by calling textMsg helper function*/
    public void textAllParents(View view) {
        String message = msg.getText().toString();;
        String phoneNum;
        String stuName;

        // For every student in student contact map
        for (String sID : stuContactMap.keySet()) {
            phoneNum = stuContactMap.get(sID);
            stuName = stuNameMap.get(sID);
            textMsg(phoneNum, message, stuName);
        }
    }

    /** A function that text arrived students' parents by calling textMsg helper function*/
    public void textArrivedParents(View view) {
        String message = msg.getText().toString();
        String phoneNum;
        String stuName;
        boolean textSent = false;

        // For every student in student contact map and also is true in student arrive map
        for (String sID : stuContactMap.keySet()) {
            phoneNum = stuContactMap.get(sID);
            if (stuArriveMap.get(sID)) {
                stuName = stuNameMap.get(sID);
                textMsg(phoneNum, message, stuName);
                textSent = true;
            }
        }

        if (!textSent) {
            Toast.makeText(getApplicationContext(), "No student has been checked!", Toast.LENGTH_LONG).show();
        }

    }

    /** A function that text not arrived students' parents by calling textMsg helper function*/
    public void textNotArrivedParents(View view) {
        String message = msg.getText().toString();
        String phoneNum;
        String stuName;
        boolean textSent = false;

        // For every student in student contact map and is false in student arrive map
        for (String sID : stuContactMap.keySet()) {
            phoneNum = stuContactMap.get(sID);
            if (!stuArriveMap.get(sID)) {
                stuName = stuNameMap.get(sID);
                textMsg(phoneNum, message, stuName);
                textSent = true;
            }
        }
        if (!textSent) {
            Toast.makeText(getApplicationContext(), "All students have been checked!", Toast.LENGTH_LONG).show();
        }
    }

    /** A helper function for sending text*/
    public void textMsg(String phoneNum, String message, String stuName) {
        try {
            // Make toast msg to inform the user
            final String notifyMsg = "SMS is sending to " + stuName + "'s parent";

            // Initialize two pending intents for message delivery status
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SENT"), 0);
            PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0, new Intent("DELIVERED"), 0);

            // sending messgae through default sms messenger apps
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, sentPI, deliverPI);

            Toast.makeText(getApplicationContext(), notifyMsg, Toast.LENGTH_LONG).show();

        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please enter your message and try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {

        unregisterReceiver(sendBroadcastReceiver);
        unregisterReceiver(deliveryBroadcastReceiver);
        super.onStop();

    }
}

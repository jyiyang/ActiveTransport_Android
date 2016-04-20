package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
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

public class NotifyActivity extends AppCompatActivity {

    // Database link
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";
    // Class attributes
    private ArrayList<String> studentID;
    private String timeOfDay;

    // For changing activities
    private boolean isStaff;
    private ArrayList<String> stuIDs;
    private String routeID;

    // View attributes
    private Button sendTextAll;
    private Button sendTextUnarrived;
    private Button sendTextArrived;
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


    /* Switch activities when click on tabs */
    public void switchChecklist(View view) {

        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }

    public void switchTimeAndLoc(View view) {
        Intent intent = new Intent(this, TimeAndLocationActivity.class);
        if (isStaff) {
            intent.putExtra(ChecklistActivity.ROUTEID, routeID);
        }
        intent.putExtra(ChecklistActivity.STUIDS, stuIDs);
        intent.putExtra(ChecklistActivity.ISSTAFF, isStaff);
        startActivity(intent);
    }

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

        final String timeString =
                android.text.format.DateFormat.format("yyyy-MM-dd", time).toString();

        // Database reference
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


                        System.out.print("Size of the student-parent map is ");
                        System.out.println(stuParentMap.size());
                        for (String stu : stuParentMap.keySet()) {
                            String par = stuParentMap.get(stu);
                            System.out.println(stu + ": " + par);
                        }


                        // Nested listener for parent's contact info
                        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String uID = postSnapshot.getKey();

                                    if (uID.equals(stuParentMap.get(sID))) {
                                        System.out.println("Find the same user, put phone # in");
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

//                                System.out.print("Size of the student-contact map is ");
//                                System.out.println(stuContactMap.size());

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

    public void textAllParents(View view) {
        String message = msg.getText().toString();;
        String phoneNum;
        String stuName;

        for (String sID : stuContactMap.keySet()) {
            phoneNum = stuContactMap.get(sID);
            stuName = stuNameMap.get(sID);
            textMsg(phoneNum, message, stuName);
        }
    }

    public void textArrivedParents(View view) {
        String message = msg.getText().toString();
        String phoneNum;
        String stuName;
        boolean textSent = false;

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

    public void textNotArrivedParents(View view) {
        String message = msg.getText().toString();
        String phoneNum;
        String stuName;
        boolean textSent = false;

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

    // Helper function for texting a user
    public void textMsg(String phoneNum, String message, String stuName) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
            String notifyMsg = "SMS sent to " + stuName + "'s parent";
            Toast.makeText(getApplicationContext(), notifyMsg, Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please enter your message and try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

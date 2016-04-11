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
import java.util.HashMap;
import java.util.Map;

public class NotifyActivity extends AppCompatActivity {

    // Database link
    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    // Class attributes
    private ArrayList<String> studentID;

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


    /* Switch activities when click on tabs */
    public void switchChecklist(View view) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }

    public void switchTimeAndLoc(View view) {
        Intent intent = new Intent(this, TimeAndLocationActivity.class);
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
        ArrayList<String> stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);

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
                        boolean isArrived = (boolean) stuMap.get("isArrived");

                        // If the student-parent pair does not exist, add it into the map
                        if (!stuParentMap.containsKey(sID)) {
                            stuParentMap.put(sID, parentID);
                        }
                        // If the student-arrived pair does not exist, add it into the map
                        if (!stuArriveMap.containsKey(sID)) {
                            stuArriveMap.put(sID, isArrived);
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
//                                    System.out.println(stuParentMap.size());
//                                    System.out.println("The user IDs are ");
//                                    System.out.println("fr cur database: " + uID);
//                                    System.out.println("from parent map: " + stuParentMap.get(sID));
                                    if (uID.equals(stuParentMap.get(sID))) {
                                        System.out.println("Find the same user, put phone # in");
                                        Map<String, Object> parMap = (Map<String, Object>) postSnapshot.getValue();
                                        String phoneNum = (String) parMap.get("contactInfo");
                                        stuContactMap.put("sID", phoneNum);
                                    }
                                }

//                                System.out.print("Size of the student-contact map is ");
//                                System.out.println(stuContactMap.size());
                                msg = (EditText) findViewById(R.id.msg_content);
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
        System.out.println("text parents");
        System.out.println(stuContactMap.size());
        String message;
        String phoneNum;
        for (String sID : stuContactMap.keySet()) {
            message = msg.getText().toString();
            phoneNum = stuContactMap.get(sID);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNum, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            }

            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}

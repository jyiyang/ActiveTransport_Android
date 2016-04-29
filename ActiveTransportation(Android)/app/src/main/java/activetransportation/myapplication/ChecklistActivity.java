package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
 * An activity that creates the main checklist screen.
 */
public class ChecklistActivity extends AppCompatActivity {

    // UI attributes
    private ListView studentListView;
    private CustomListAdapter adapter;

    // Database link
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    // Extra external IDs
    public final static String STUIDS = "ActiveTransport.STUIDS";
    public final static String ROUTEID = "ActiveTransport.ROUTEID";
    public final static String ISSTAFF = "ActiveTransport.ISSTAFF";
    public final static String OLDPASSWORD = "ActiveTransport.OLDPASSWORD";
    public final static String USERID = "ActiveTransport.USERID";
    public final static String OLDEMAIL = "ActiveTransport.OLDEMAIL";

    // Data class attributes
    private ArrayList<Student> studentList;
    private ArrayList<String> stuIDList;
    private Boolean isStaff_;
    private String routeID_;
    private String userID_;
    private String userEmail;
    private Boolean logArrived = false;
    private String timeOfDay;
    private String password;


    /** Switch to the (current) ChecklistActivity when the user clicks on the corresponding button. */
    public void switchChecklist(View view) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }

    /** Switch to the TimeAndLocationActivity when the user clicks on the corresponding button. */
    public void switchTimeAndLoc(View view) {
        Intent intent = new Intent(this, TimeAndLocationActivity.class);
        intent.putExtra(STUIDS, stuIDList);
        intent.putExtra(ISSTAFF, isStaff_);
        if (isStaff_) {
            intent.putExtra(ROUTEID, routeID_);
        }
        startActivity(intent);
    }

    /** Switch to the NotifyActivity when the user clicks on the corresponding button. */
    public void switchNotify(View view) {
        Intent intent = new Intent(this, NotifyActivity.class);
        intent.putExtra(ROUTEID, routeID_);
        intent.putExtra(STUIDS, stuIDList);
        intent.putExtra(ISSTAFF, isStaff_);
        startActivity(intent);
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        // Get user email and password from LoginActivity or SettingsActivity
        Intent intent = getIntent();
        if (intent.getStringExtra("from").equals("settings")) {
            userEmail = intent.getStringExtra(SettingsActivity.NEWEMAIL);
            password = intent.getStringExtra(SettingsActivity.NEWPASSWORD);
        } else if (intent.getStringExtra("from").equals("login")){
            userEmail = intent.getStringExtra(LoginActivity.CHECKLIST);
            password = intent.getStringExtra(LoginActivity.PASSWORD);
        }

        Firebase ref = new Firebase(FIREBASE_URL);
        Query userRef = ref.child("users").orderByChild("email").equalTo(userEmail);

        // Attach a listener to retrieve the user object that we want from Firebase.
        // Also create the checklist inside the body of listener.
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    System.out.println(key);
                    Map<String, Object> userMap = (Map<String, Object>) postSnapshot.getValue();
                    isStaff_ = (Boolean) userMap.get("isStaff");
                    if (isStaff_) {       // The user is a staff
                        setContentView(R.layout.activity_checklist);
                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);
                        routeID_ = (String) userMap.get("routeID");
                    } else {              // The user is a parent
                        setContentView(R.layout.activity_checklist_parent);
                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
                        setSupportActionBar(toolbar);
                    }
                    userID_ = key;
                }

                createList(isStaff_, routeID_, userID_);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    /** Creates the checklist when called. */
    private void createList(Boolean isStaff, Object maybeRouteID, final String userID) {

        Firebase ref = new Firebase(FIREBASE_URL);
        if (isStaff) {      // The user is a staff
            String routeID = (String) maybeRouteID;
            // Attach a listener to retrieve from Firebase the IDs of the students that the staff user is responsible for
            ref.child("routes").child(routeID).child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    stuIDList = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        stuIDList.add(postSnapshot.getKey());
                    }
                    createListHelper();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        } else {         // The user is a parent
            // Attach a listener to retrive from Firebase the IDs of all children of the parent user
            ref.child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    stuIDList = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.child("parentID").getValue().equals(userID)) {
                            stuIDList.add(postSnapshot.getKey());
                        }
                    }
                    createListHelper();
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }

    /** A helper function for creating the checklist. */
    private void createListHelper() {
        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase studentsRef = ref.child("students");
        // Attach a listener to retrieve from Firebase the student objects corresponding to the IDs
        // that are already stored in the global variable stuIDlist.
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                studentList = new ArrayList<Student>();
                // Wrap isStaff information into the student list that will be passed to the custom list adapter
                studentList.add(new Student(isStaff_.toString()));
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // If the student corresponds to the current user
                    if (stuIDList.contains(postSnapshot.getKey())) {
                        Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                        Student student = new Student((String) stuMap.get("name"));
                        student.setID(postSnapshot.getKey());
                        boolean arrived = logHelper(student.getID());
                        student.setIsArrived(arrived);
                        student.setParentID((String) stuMap.get("parentID"));
                        student.setRouteID((String) stuMap.get("routeID"));
                        studentList.add(student);
                    }
                }
                adapter = new CustomListAdapter(studentList, ChecklistActivity.this);

                // Handle listview and assign adapter
                studentListView = (ListView) findViewById(R.id.custom_list);
                studentListView.setAdapter(adapter);
                studentListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    /* Retrieves from log and returns the arrival status of the given student.
     * Creates the log if it does not exist.
     */
    private boolean logHelper(String studentID) {
        Firebase ref = new Firebase(FIREBASE_URL);
        GregorianCalendar time = new GregorianCalendar();
        final String id = studentID;

        if (time.get(Calendar.AM_PM) == Calendar.PM) {
            timeOfDay = "afternoon";
        } else {
            timeOfDay = "morning";
        }

        final String timeString = android.text.format.DateFormat.format("yyyy-MM-dd", time).toString();
        final Firebase logRef = ref.child("logs");

        // Attach a listener to retrieve arrival status of the given student from log from Firebase
        logRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(timeString).child(timeOfDay).hasChild(id)) {
                    // If the log exists
                    logArrived = (Boolean) dataSnapshot.child(timeString).child(timeOfDay).child(id).getValue();
                } else {
                    // If the log does not exist
                    Map<String, Object> amOrPm = new HashMap<String, Object>();
                    amOrPm.put(id, false);
                    logRef.child(timeString).child(timeOfDay).updateChildren(amOrPm);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Fail to read from log");
            }
        });
        return logArrived;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflate the menu; this adds items to the action bar if it is present. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /** Handle action bar item clicks here. */
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(OLDPASSWORD, password);
            intent.putExtra(OLDEMAIL,userEmail);
            intent.putExtra(USERID, userID_);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_refresh) {
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }

}

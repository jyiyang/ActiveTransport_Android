package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  An activity that creates a time and location screen for both parent and staff users.
 *  If the user is a parent, it will launch the time and location view for parent.
 *  Otherwise, it will launch the time and location view for staff
 */
public class TimeAndLocationActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    private String routeID_;
    private Set<Route> routeSet;
    private ArrayList<Route> routeList;
    private boolean isStaff;
    private ArrayList<String> stuIDs;

    private ExpandableListView timeandLocListView;
    private ExpandableTimeLocationListAdapter adapter;

    private TextView mTimeDisplay;
    private TextView mLocationDisplay;

    /** Switch between three activities when the user clicks on buttons */
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
        intent.putExtra(ChecklistActivity.ROUTEID, routeID_);
        intent.putExtra(ChecklistActivity.STUIDS, stuIDs);
        intent.putExtra(ChecklistActivity.ISSTAFF, isStaff);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        // Get intents from activities that calls this activity
        Intent intent = getIntent();
        isStaff = intent.getBooleanExtra(ChecklistActivity.ISSTAFF, false);
        routeID_ = intent.getStringExtra(ChecklistActivity.ROUTEID);
        stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);
        // If it is staff, set the contentView to staff XML
        if (isStaff) {
            setContentView(R.layout.activity_time_and_location_staff);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setStaff(intent);
        // Otherwise, set the contentView to parent XML
        } else {
            setContentView(R.layout.activity_time_and_location_parent);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setParent(intent);
        }
    }

    /** This is a function to set the content timeAndLocation activity for a parent user*/
    private void setParent(Intent intent) {
        stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Map<String, ArrayList<String>> stuRouteMap = new HashMap<String, ArrayList<String>>();
        final Firebase ref = new Firebase(FIREBASE_URL);
        // Pulling every child that the parent has from firebase
        for (String stuID : stuIDs) {
            final Query stuRef = ref.child("students").orderByKey().equalTo(stuID);

            stuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // For every student, build a route-student hashmap that store a route
                    // the student as coresponding key-value pair 
                    ArrayList<String> temp;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                        String routeID = (String) stuMap.get("routeID");
                        String name = (String) stuMap.get("name");
                        if (stuRouteMap.containsKey(routeID)) {
                            temp = stuRouteMap.get(routeID);
                        } else {
                            temp = new ArrayList<String>();
                        }
                        temp.add(name);
                        stuRouteMap.put(routeID, temp);
                    }

                    System.out.print("Number of routes in stuRouteMap is: ");
                    System.out.println(stuRouteMap.keySet().size());
                    routeSet = new HashSet<Route>();
                    // Register another listener for route object on firebase
                    ref.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                // For every route object, find the corresponding route information
                                // such as route location and route meeting time
                                Map<String, Object> rMap = (Map<String, Object>) postSnapshot.getValue();
                                String rID = postSnapshot.getKey();
                                System.out.println(rID);
                                if (stuRouteMap.containsKey(rID)) {
                                    String rLocation = rMap.get("meetingLocation").toString();
                                    String rTime = rMap.get("meetingTime").toString();
                                    String rName = rMap.get("name").toString();
                                    Route route = new Route(rName, rLocation, rTime, stuRouteMap.get(rID));
                                    routeSet.add(route);
                                }
                            }
                            // Since the listener might be called multiple times, use a set to eliminate repetitions
                            if (routeSet.size() == stuRouteMap.keySet().size()) {
                                routeList = new ArrayList<Route>(routeSet);
                                System.out.print("Number of routes in routeList is: ");
                                System.out.println(routeList.size());
                                adapter = new ExpandableTimeLocationListAdapter(routeList, TimeAndLocationActivity.this);

                                // Handle listview and assign adapter
                                // Initialize the main view
                                timeandLocListView = (ExpandableListView) findViewById(R.id.time_loc_list);
                                timeandLocListView.setAdapter(adapter);
                            }
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

    /** This is a function to set the content timeAndLocation activity for a staff user*/
    private void setStaff(Intent intent) {
        routeID_ = intent.getStringExtra(ChecklistActivity.ROUTEID);
        Firebase ref = new Firebase(FIREBASE_URL);

        mTimeDisplay = (TextView) findViewById(R.id.meeting_time_staff);
        mLocationDisplay = (TextView) findViewById(R.id.meeting_location_staff);

        // Iniatialize a listener for meeting location for that route
        ref.child("routes").child(routeID_).child("meetingLocation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String DefaultLocation = snapshot.getValue().toString();
                mLocationDisplay.setText(DefaultLocation);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                mLocationDisplay.setText("Fail to read default location");
            }
        });
        // Iniatialize a listener for meeting time for that route
        ref.child("routes").child(routeID_).child("meetingTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String DefaulTime = snapshot.getValue().toString();
                mTimeDisplay.setText(DefaulTime);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                mTimeDisplay.setText("Fail to read default time");
            }
        });


    }
}

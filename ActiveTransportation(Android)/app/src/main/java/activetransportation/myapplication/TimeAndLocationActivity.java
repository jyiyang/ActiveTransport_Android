package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeAndLocationActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private String location;
    private String time;
    private String routeID_;
    private String routeName;
    private TextView locView;
    private TextView timeView;
    private TextView routeView;

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
        setContentView(R.layout.activity_time_and_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<String> stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);
        final Map<String, ArrayList<String>> stuRouteMap = new HashMap<String, ArrayList<String>>();

        final Firebase ref = new Firebase(FIREBASE_URL);
        //Firebase userRef = ref.child("users").child(userID);
        for (String stuID : stuIDs) {
            final Firebase stuRef = ref.child("students").child(stuID);
            //System.out.println(userRef);

            stuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    ArrayList<String> temp;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        System.out.println(key);
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
                    

                    if (stuRouteMap.size() == 1) {
                        ref.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    String key = postSnapshot.getKey();
                                    System.out.println(key);
                                    Map<String, Object> userMap = (Map<String, Object>) postSnapshot.getValue();
                                    routeID_ = (String) userMap.get("routeID");
                                    location = (String) userMap.get("Location");
                                    time = (String) userMap.get("Time");

                                }

                                routeView = (TextView) findViewById(R.id.route_name);
                                routeView.setText(routeName);

                                locView = (TextView) findViewById(R.id.meeting_location);
                                locView.setText(location);

                                timeView = (TextView) findViewById(R.id.meeting_time);
                                timeView.setText(time);
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

}

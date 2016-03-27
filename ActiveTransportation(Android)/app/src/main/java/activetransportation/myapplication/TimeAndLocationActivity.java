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
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class TimeAndLocationActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private String location;
    private String time;
    private TextView locView;
    private TextView timeView;

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
        String routeID = intent.getStringExtra(ChecklistActivity.ROUTEID);

        Firebase ref = new Firebase(FIREBASE_URL);
        //Firebase userRef = ref.child("users").child(userID);
        Query routeRef = ref.child("routes").orderByKey().equalTo(routeID);
        //System.out.println(userRef);

        routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    System.out.println(key);
                    Map<String, Object> userMap = (Map<String, Object>) postSnapshot.getValue();
                    location = (String) userMap.get("Location");
                    time = (String) userMap.get("Time");
                    //if (key == "isStaff") {
                    //    isStaff_ = (Boolean) postSnapshot.getValue();
                    //} else if (key == "routeID") {
                    //    routeID_ = (String) postSnapshot.getValue();
                    //}
                }

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

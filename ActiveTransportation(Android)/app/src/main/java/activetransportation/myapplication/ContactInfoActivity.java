package activetransportation.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ContactInfoActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";
    // private TextView nameView;
    private TextView userView;
    private TextView phoneView;
    private ListView contactView;

    // private String name;
    private String userName;
    private String userContactInfo;
    private String userEmail;
    private String staffID;
    private Boolean isStaff;

    private ContactInfoListAdapter adapter;


    public void switchChecklist(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase ref = new Firebase(FIREBASE_URL);

        Intent intent = getIntent();
        String someID = intent.getStringExtra(CustomListAdapter.CONTACT_INFO);
        if (someID.charAt(0) == '1') {
            isStaff = true;
        } else {
            isStaff = false;
        }
        someID = someID.substring(1);

        if (isStaff) {
            createView(someID);

        } else {

            Firebase routeRef = ref.child("routes").child(someID);

            routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        System.out.println(key);
                        if (key == "staffID") {
                            staffID = (String) postSnapshot.getValue();
                            break;
                        }
                    }
                    createView(staffID);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }

    private void createView(String userID) {
        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase userRef = ref.child("users").child(userID);
        // Attach an listener to read the data at our posts reference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key == "name") {
                        userName = (String) postSnapshot.getValue();
                    } else if (key == "contactInfo") {
                        userContactInfo = (String) postSnapshot.getValue();
                    } else if (key == "email") {
                        userEmail = (String) postSnapshot.getValue();
                    }

                }
                if (isStaff) {
                    System.out.println("parent name: " + userName);
                } else {
                    System.out.println("staff name: " + userName);
                }
                System.out.println("contactInfo: " + userContactInfo);

                //nameView = (TextView) findViewById(R.id.student_name);
                //nameView.setText(name + ":");
                ArrayList<String> contact = new ArrayList<String>();
                contact.add(userContactInfo);
                contact.add(userEmail);

                adapter = new ContactInfoListAdapter(contact, ContactInfoActivity.this);

                contactView = (ListView) findViewById(R.id.contact_list);
                contactView.setAdapter(adapter);

                userView = (TextView) findViewById(R.id.parent_name);
                userView.setText(userName);
//
//                phoneView = (TextView) findViewById(R.id.phone_num);
//                phoneView.setText(userContactInfo);


//                phoneView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
//                        phoneIntent.setData(Uri.parse("tel:" + userContactInfo));
//                        try {
//                            startActivity(phoneIntent);
//                        }
//                        catch (android.content.ActivityNotFoundException ex) {
//                            Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

}

package activetransportation.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ContactInfoActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private TextView textView;
    private TextView nameView;
    private TextView parentView;
    private TextView phoneView;

    private String name;
    private String parentName;
    private String parentContactInfo;

    public void switchChecklist(View view) {
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        System.out.println("call override method");
//        Intent intent = new Intent(this, ChecklistActivity.class);
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra(CustomListAdapter.CONTACT_INFO);

        name = info[0];
        String parentID = info[1];

        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase parentRef = ref.child("users").child(parentID);
        // Attach an listener to read the data at our posts reference
        parentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key == "name") {
                        parentName = (String) postSnapshot.getValue();
                    } else if (key == "contactInfo") {
                        parentContactInfo = (String) postSnapshot.getValue();
                    }
                }
                System.out.println("parent name: " + parentName);
                System.out.println("contactInfo: " + parentContactInfo);

                textView = new TextView(ContactInfoActivity.this);
                textView.setTextSize(20);
                textView.setText(name);

                nameView = (TextView) findViewById(R.id.student_name);
                nameView.setText(name + ":");

                parentView = (TextView) findViewById(R.id.parent_name);
                parentView.setText(parentName);

                phoneView = (TextView) findViewById(R.id.phone_num);
                phoneView.setText(parentContactInfo);


                phoneView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:" + parentContactInfo));
                        try {
                            startActivity(phoneIntent);
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //TextView textView = new TextView(this);
        //textView.setTextSize(20);
        //textView.setText(name);

        //RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        //layout.addView(textView);

        //TextView nameView = (TextView) findViewById(R.id.student_name);
        //nameView.setText(name + ":");

        //TextView parentView = (TextView) findViewById(R.id.parent_name);
        //parentView.setText(parentName);

        //TextView phoneView = (TextView) findViewById(R.id.phone_num);
        //phoneView.setText(parentContactInfo);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

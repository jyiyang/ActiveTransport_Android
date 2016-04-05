package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChecklistActivity extends AppCompatActivity {

    private ListView studentListView;
    //private ArrayAdapter arrayAdapter;
    private CustomListAdapter adapter;

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    public final static String STUIDS = "ActiveTransport.STUIDS";
    public final static String ROUTEID = "ActiveTransport.ROUTEID";
    public final static String ISSTAFF = "ActiveTransport.STAFF";

    //generate list
    private ArrayList<Student> studentList;
    private ArrayList<String> stuIDList;
    private Boolean isStaff_;
    private String routeID_;
    private Object childrenIDs_;
    private Object routeIDs;
    private String userID_;
    private String userEmail;


    /* Switch activities when click on tabs */
    public void switchChecklist(View view) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }

    public void switchTimeAndLoc(View view) {
        Intent intent = new Intent(this, TimeAndLocationActivity.class);
        intent.putExtra(STUIDS, stuIDList);
        intent.putExtra(ISSTAFF, isStaff_);
        startActivity(intent);
    }

    public void switchNotify(View view) {
        Intent intent = new Intent(this, NotifyActivity.class);
        startActivity(intent);
    }

    public void putStudent(Student student, Firebase studentsRef) {
        Map<String, Object> stuMap = new HashMap<String, Object>();
        stuMap.put("name", student.getName());
        stuMap.put("isArrived", student.getIsArrived());
        Firebase stuRef = studentsRef.push();
        stuRef.setValue(stuMap);
        student.setID(stuRef.getKey());
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_checklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        userEmail = intent.getStringExtra(LoginActivity.CHECKLIST);

        //System.out.println(userEmail);
        Firebase ref = new Firebase(FIREBASE_URL);
        //Firebase userRef = ref.child("users").child(userID);
        Query userRef = ref.child("users").orderByChild("email").equalTo(userEmail);
        //System.out.println(userRef);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    System.out.println(key);
                    Map<String, Object> userMap = (Map<String, Object>) postSnapshot.getValue();
                    isStaff_ = (Boolean) userMap.get("isStaff");
                    if (isStaff_) {
                        routeID_ = (String) userMap.get("routeID");
                    } else {
                        childrenIDs_= new ArrayList<String>(((Map<String, Object>) userMap.get("childrenIDs")).keySet());
                    }
                    userID_ = key;
                }
                createList(isStaff_, routeID_, childrenIDs_, userID_);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


    private void createList(Boolean isStaff, Object maybeRouteID, Object maybeChildrenIDs, final String userID) {

        Firebase ref = new Firebase(FIREBASE_URL);

        // the following code is commented since we only put data into Firebase once

        //Student student1 = new Student("Yiqing Cai");
        //Student student2 = new Student("Yi Yang");
        //Student student3 = new Student("Weiyun Ma");
        //putStudent(student1, studentsRef);
        //putStudent(student2, studentsRef);
        //putStudent(student3, studentsRef);

        if (isStaff) {
            String routeID = (String) maybeRouteID;
            ref.child("routes").child(routeID).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    stuIDList = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        stuIDList.add((String) postSnapshot.getKey());
                    }
                    createListHelper();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        } else {
            ArrayList<String> childrenIDs = (ArrayList<String>) maybeChildrenIDs;
            ref.child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    stuIDList = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.child("parentID").getValue().equals(userID)) {
                            stuIDList.add((String) postSnapshot.getKey());
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

    private void createListHelper() {
        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase studentsRef = ref.child("students");
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                studentList = new ArrayList<Student>();
                studentList.add(new Student(isStaff_.toString()));
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (stuIDList.contains(postSnapshot.getKey())) {
                        Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                        Student student = new Student((String) stuMap.get("name"));
                        student.setID(postSnapshot.getKey());
                        student.setIsArrived((Boolean) stuMap.get("isArrived"));
                        student.setParentID((String) stuMap.get("parentID"));
                        student.setRouteID((String) stuMap.get("routeID"));
                        studentList.add(student);
                    }
                }
                //stuListHolder.setValue(tempStuList);
                //System.out.println(stuListHolder.getValue().size());
                //studentList = tempStuList;

                adapter = new CustomListAdapter(studentList, ChecklistActivity.this);

                //handle listview and assign adapter
                studentListView = (ListView) findViewById(R.id.custom_list);
                studentListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

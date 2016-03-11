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
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChecklistActivity extends AppCompatActivity {

    private ListView studentListView;
    //private ArrayAdapter arrayAdapter;
    private CustomListAdapter adapter;

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";

    //generate list
    private ArrayList<Student> studentList;

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

        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase studentsRef = ref.child("students");
        // the following code is commented since we only put data into Firebase once


        //Student student1 = new Student("Yiqing Cai");
        //Student student2 = new Student("Yi Yang");
        //Student student3 = new Student("Weiyun Ma");

        //putStudent(student1, studentsRef);
        //putStudent(student2, studentsRef);
        //putStudent(student3, studentsRef);


        // Attach an listener to read the data at our posts reference
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Student> tempStuList = new ArrayList<Student>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                    Student student = new Student((String) stuMap.get("name"));
                    student.setID(postSnapshot.getKey());
                    student.setIsArrived((Boolean) stuMap.get("isArrived"));
                    tempStuList.add(student);
                }
                //stuListHolder.setValue(tempStuList);
                //System.out.println(stuListHolder.getValue().size());
                studentList = tempStuList;
                adapter = new CustomListAdapter(studentList, ChecklistActivity.this);

                //handle listview and assign adapter
                studentListView = (ListView)findViewById(R.id.custom_list);
                studentListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //studentList = stuListHolder.getValue();

        //instantiate custom adapter
        //System.out.println(stuListHolder.getValue().size());


        //studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        Object pickedItem = studentListView.getItemAtPosition(position);
        //        view.setSelected(true);
        //    }
        //});

        //studentListView = (ListView) findViewById(R.id.student_list);

        // this-The current activity context.
        // Second param is the resource Id for list layout row item
        // Third param is input array
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, studentArray);
        //studentListView.setAdapter(arrayAdapter);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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

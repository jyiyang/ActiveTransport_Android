package activetransportation.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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

public class TimeAndLocationActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private String location;
    private String time;
    private String routeID_;
    private String routeName;
    private TextView locView;
    private TextView timeView;
    private TextView routeView;

    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private TextView mTimeDisplay;
    private Button mPickTime;

    private int mhour;
    private int mminute;

    static final int TIME_DIALOG_ID = 1;

    static final int DATE_DIALOG_ID = 0;

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

        Intent intent = getIntent();
        boolean isStaff = intent.getBooleanExtra(ChecklistActivity.ISSTAFF, false);
        if (isStaff) {
            setContentView(R.layout.activity_time_and_location_staff);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setStaff(intent);
        } else {
            setContentView(R.layout.activity_time_and_location);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setParent(intent);
        }


    }

    private void setParent(Intent intent) {
        ArrayList<String> stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);
        final Map<String, ArrayList<String>> stuRouteMap = new HashMap<String, ArrayList<String>>();

        final Firebase ref = new Firebase(FIREBASE_URL);
        //Firebase userRef = ref.child("users").child(userID);
        for (String stuID : stuIDs) {
            final Query stuRef = ref.child("students").orderByKey().equalTo(stuID);
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

    private void setStaff(Intent intent) {
        String routeID = intent.getStringExtra(ChecklistActivity.ROUTEID);
        final Firebase ref = new Firebase(FIREBASE_URL);

        mPickDate =(Button)findViewById(R.id.set_date);
        mTimeDisplay = (TextView) findViewById(R.id.meeting_time_staff);
        mPickTime = (Button) findViewById(R.id.set_time);

        //Pick time's click event listener
        mPickTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        //PickDate's click event listener
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        final GregorianCalendar time  = new GregorianCalendar(2016, 1, 1);
        mYear = time.get(Calendar.YEAR);
        mMonth = time.get(Calendar.MONTH);
        mDay = time.get(Calendar.DAY_OF_MONTH);
        mhour = time.get(Calendar.HOUR_OF_DAY);
        mminute = time.get(Calendar.MINUTE);
    }

    //-------------------------------------------update date---//
    private void updateDate() {
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
        showDialog(TIME_DIALOG_ID);
    }

    //-------------------------------------------update time---//
    public void updatetime() {
        mTimeDisplay.setText(
                new StringBuilder()
                        .append(pad(mhour)).append(":")
                        .append(pad(mminute)));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


        //Datepicker dialog generation

        private DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        updateDate();
                    }
                };


        // Timepicker dialog generation
        private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mhour = hourOfDay;
                        mminute = minute;
                        updatetime();
                    }
                };

        @Override
        protected Dialog onCreateDialog(int id) {
            switch (id) {
                case DATE_DIALOG_ID:
                    return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);

                case TIME_DIALOG_ID:
                    return new TimePickerDialog(this,
                            mTimeSetListener, mhour, mminute, false);

            }
            return null;
        }
}

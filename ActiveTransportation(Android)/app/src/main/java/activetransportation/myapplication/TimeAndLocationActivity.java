package activetransportation.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TimeAndLocationActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private String location;
    private String time;
    private String routeID_;
    private String routeName;
    private TextView locView;
    private TextView timeView;
    private TextView routeView;
    private Set<Route> routeSet;
    private ArrayList<Route> routeList;


    private ExpandableListView timeandLocListView;
    private ExpandableTimeLocationListAdapter adapter;

    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private TextView mTimeDisplay;
    private Button mPickTime;
    private EditText mMeetLocation;
    private TextView mLocationDisplay;
    private Button mSetLocation;
    private TextView mRouteName;
    private String meetLocation;


    private int mhour;
    private int mminute;
    private final GregorianCalendar calendar  = new GregorianCalendar();

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
            setContentView(R.layout.activity_time_and_location_parent);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setParent(intent);
        }


    }

    private void setParent(Intent intent) {
        ArrayList<String> stuIDs = intent.getStringArrayListExtra(ChecklistActivity.STUIDS);
        // Get the user identity to find out if the current user is a staff
//        final boolean isStaff = intent.getExtras().getBoolean(ChecklistActivity.ISSTAFF);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Map<String, ArrayList<String>> stuRouteMap = new HashMap<String, ArrayList<String>>();

        final Firebase ref = new Firebase(FIREBASE_URL);
        for (String stuID : stuIDs) {
            final Query stuRef = ref.child("students").orderByKey().equalTo(stuID);

            stuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

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
                    ref.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Map<String, Object> rMap = (Map<String, Object>) postSnapshot.getValue();
                                String rID = postSnapshot.getKey();
                                System.out.println(rID);
                                if (stuRouteMap.containsKey(rID)) {
                                    String rLocation = rMap.get("Location").toString();
                                    String rTime = rMap.get("Time").toString();
                                    String rName = rMap.get("name").toString();
                                    Route route = new Route(rName, rLocation, rTime, stuRouteMap.get(rID));
                                    routeSet.add(route);
                                }
                            }
                            if (routeSet.size() == stuRouteMap.keySet().size()) {
                                routeList = new ArrayList<Route>(routeSet);
                                System.out.print("Number of routes in routeList is: ");
                                System.out.println(routeList.size());
                                adapter = new ExpandableTimeLocationListAdapter(routeList, TimeAndLocationActivity.this);

                                //handle listview and assign adapter
                                timeandLocListView = (ExpandableListView) findViewById(R.id.time_loc_list);
                                timeandLocListView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                    //System.out.print("Number of routes in routeList is: ");
                    //System.out.println(routeList.size());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }

    private void setStaff(Intent intent) {
        routeID_ = intent.getStringExtra(ChecklistActivity.ROUTEID);
        Firebase ref = new Firebase(FIREBASE_URL);

        mPickTime =(Button)findViewById(R.id.set_time);
        mTimeDisplay = (TextView) findViewById(R.id.meeting_time_staff);
        mMeetLocation = (EditText) findViewById(R.id.input_location);
        mSetLocation = (Button) findViewById(R.id.set_location);
        mLocationDisplay = (TextView) findViewById(R.id.meeting_location_staff);
        mRouteName = (TextView) findViewById(R.id.route_name_staff);

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        ref.child("routes").child(routeID_).child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String DefaultLocation = snapshot.getValue().toString();
                System.out.println(DefaultLocation);
                mMeetLocation.setText(DefaultLocation);
                mMeetLocation.setTextColor(Color.parseColor("#a9a9a9"));
                mLocationDisplay.setText(DefaultLocation);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                mMeetLocation.setText("Fail to read defalut location");
            }
        });

        ref.child("routes").child(routeID_).child("Time").addListenerForSingleValueEvent(new ValueEventListener() {
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

        mSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMeetLocation.setError(null);
                View focusView = null;
                boolean cancel = false;
                meetLocation = mMeetLocation.getText().toString();

                if(TextUtils.isEmpty(meetLocation)) {
                    mMeetLocation.setError("Please enter the meeting location");
                    focusView = mMeetLocation;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    mLocationDisplay.setText(meetLocation);
                    pushToDataBase(routeID_, meetLocation, calendar);
                }
            }
        });


        //Pick time's click event listener
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

    }


        //Datepicker dialog generation

        private DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        showDialog(TIME_DIALOG_ID);
                    }
                };


        // Timepicker dialog generation
        private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mhour = hourOfDay;
                        mminute = minute;
                        calendar.set(mYear, mMonth, mDay, mhour, mminute);
                        String timeString =
                                android.text.format.DateFormat.format("yyyy-MM-dd hh:mm", calendar).toString();
                        mTimeDisplay.setText(timeString);
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

    private void pushToDataBase(String routeID, String meet_loc, GregorianCalendar meet_time) {
        Firebase ref = new Firebase(FIREBASE_URL);
        String timeString =
                android.text.format.DateFormat.format("yyyy-MM-dd hh:mm", calendar).toString();
        String locString = meet_loc;
        ref.child("routes").child(routeID).child("Time").setValue(timeString);
        ref.child("routes").child(routeID).child("Location").setValue(locString);
    }
}

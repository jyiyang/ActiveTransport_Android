package activetransportation.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class CustomListAdapter extends BaseAdapter implements ListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<Student> list = new ArrayList<Student>();
    private Context context;
    private Boolean isStaff;
    private String staffID;
    private static final String FIREBASE_URL = "https://walkingschoolbus.firebaseIO.com";

    public CustomListAdapter(ArrayList<Student> list, Context context) {
        this.isStaff = Boolean.parseBoolean(list.get(0).getName());
        list.remove(0);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
        //return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_row_layout, null);
        }

        final Student student = list.get(position);

        //Handle TextView and display string from your list
        TextView studentName = (TextView)view.findViewById(R.id.student_name);
        studentName.setText(student.getName());

        //Handle checkboxes and add setOnCheckedChangeListeners
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkbox);
        checkBox.setChecked(false);
        if (!isStaff) { checkBox.setEnabled(false); }

        Firebase ref = new Firebase(FIREBASE_URL);
        //Query queryRef = studentsRef.orderByKey().equalTo(student.getID());

        logHelper(student.getID(),checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
                Firebase ref = new Firebase(FIREBASE_URL);
                GregorianCalendar time = new GregorianCalendar();
                if (time.get(Calendar.AM_PM) == 1) {
                    final String timeOfDay = "afternoon";
                }
                final String timeOfDay = "morning";

                final String timeString =
                        android.text.format.DateFormat.format("yyyy-MM-dd", time).toString();
                Firebase logRef = ref.child("logs");
                logRef.child(timeString).child(timeOfDay).child(student.getID()).setValue(isChecked);
                student.setIsArrived(isChecked);
            }
        });

        //Handle buttons and add onClickListeners
        Button contactBtn = (Button)view.findViewById(R.id.contact_btn);
        if (isStaff) {
            contactBtn.setText("Contact Parent");
        } else {
            contactBtn.setText("Contact Staff");
        }

        contactBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ContactInfoActivity.class);
                String name = student.getName();  // position is index in the list
                String parentID = student.getParentID();
                String routeID = student.getRouteID();
                Firebase ref = new Firebase(FIREBASE_URL);

                if (isStaff) {
                    intent.putExtra(CONTACT_INFO, "1" + parentID);
                } else {
                    intent.putExtra(CONTACT_INFO, "0" + routeID);
                }
                v.getContext().startActivity(intent);

            }
        });

        return view;
    }

    private void logHelper(String studentID, final CheckBox checkBox) {
        Firebase ref = new Firebase(FIREBASE_URL);
        GregorianCalendar time = new GregorianCalendar();
        final String id = studentID;
        if (time.get(Calendar.AM_PM) == 1) {
            final String timeOfDay = "afternoon";
        }
        final String timeOfDay = "morning";

        final String timeString =
                android.text.format.DateFormat.format("yyyy-MM-dd", time).toString();
        final Firebase logRef = ref.child("logs");

        logRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean logArrived = false;
                if (dataSnapshot.child(timeString).child(timeOfDay).hasChild(id)) {
                    logArrived = (Boolean) dataSnapshot.child(timeString).child(timeOfDay).child(id).getValue();
                } else {
                    Map<String, Object> amOrPm = new HashMap<String, Object>();
                    Boolean isArrived = false;
                    amOrPm.put(id, isArrived);
                    logRef.child(timeString).child(timeOfDay).updateChildren(amOrPm);
                    logArrived = false;
                }
                checkBox.setChecked(logArrived);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Fail to read from log");
            }
        });
    }
}


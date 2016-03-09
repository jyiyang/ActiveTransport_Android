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
import java.util.Map;


public class CustomListAdapter extends BaseAdapter implements ListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<Student> list = new ArrayList<Student>();
    private Context context;
    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";

    public CustomListAdapter(ArrayList<Student> list, Context context) {
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
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
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

        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase studentsRef = ref.child("students");
        //Query queryRef = studentsRef.orderByKey().equalTo(student.getID());

        // Attach an listener to read the data at our posts reference
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key == student.getID()) {
                        Map<String, Object> stuMap = (Map<String, Object>) postSnapshot.getValue();
                        checkBox.setChecked((Boolean) stuMap.get("isArrived"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
                Firebase ref = new Firebase(FIREBASE_URL);
                Firebase studentsRef = ref.child("students");
                studentsRef.child(student.getID()).child("isArrived").setValue(isChecked);
                student.setIsArrived(isChecked);
            }
        });

        //Handle buttons and add onClickListeners
        Button contactBtn = (Button)view.findViewById(R.id.contact_btn);

        contactBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ContactInfoActivity.class);
                String name = student.getName();  // position is index in the list
                intent.putExtra(CONTACT_INFO, name);
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }
}

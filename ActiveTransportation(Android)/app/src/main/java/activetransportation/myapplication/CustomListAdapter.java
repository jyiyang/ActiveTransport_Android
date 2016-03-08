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

import java.util.ArrayList;


public class CustomListAdapter extends BaseAdapter implements ListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<Student> list = new ArrayList<Student>();
    private Context context;

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

        Student student = list.get(position);

        //Handle TextView and display string from your list
        TextView studentName = (TextView)view.findViewById(R.id.student_name);
        studentName.setText(student.getName());

        //Handle checkboxes and add setOnCheckedChangeListeners
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkbox);
        checkBox.setChecked(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
            }
        });

        //Handle buttons and add onClickListeners
        Button contactBtn = (Button)view.findViewById(R.id.contact_btn);

        contactBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ContactInfoActivity.class);
                Student student = list.get(position);
                String name = student.getName();  // position is index in the list
                intent.putExtra(CONTACT_INFO, name);
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }
}

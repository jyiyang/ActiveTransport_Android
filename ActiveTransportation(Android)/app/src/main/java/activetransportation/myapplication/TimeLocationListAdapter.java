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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class TimeLocationListAdapter extends BaseAdapter implements ListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<Route> routeList = new ArrayList<Route>();
    private Context context;
    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";

    public TimeLocationListAdapter(ArrayList<Route> routeList, Context context) {
//        this.isStaff = Boolean.parseBoolean(list.get(0).getName());
        this.routeList = routeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Object getItem(int pos) {
        return routeList.get(pos);
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
            view = inflater.inflate(R.layout.loc_row_layout, null);
        }

        Route route = routeList.get(position);

        TextView routeLoc = (TextView) view.findViewById(R.id.m_location);
        routeLoc.setText(route.getLocation());

        TextView routeTime = (TextView) view.findViewById(R.id.m_time);
        routeTime.setText(route.getInputTimeString());

        ListView student = (ListView) view.findViewById(R.id.m_student_name);


        return view;
    }

}
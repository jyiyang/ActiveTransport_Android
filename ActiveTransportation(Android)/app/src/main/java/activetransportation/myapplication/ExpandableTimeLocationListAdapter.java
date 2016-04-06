package activetransportation.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


public class ExpandableTimeLocationListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<Route> routeList = new ArrayList<Route>();
    private Context context;
    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    private LayoutInflater inflater;

    private static final class ViewHolder {
        TextView textLabel;
    }

    public ExpandableTimeLocationListAdapter(Context context, ArrayList<Route> routeList) {
//        this.isStaff = Boolean.parseBoolean(list.get(0).getName());
        this.routeList = routeList;
        this.context = context;
        System.out.print("Number of routes (from constructor) are: ");
        System.out.println(routeList.size());


    }
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return routeList.get(groupPosition).getStudents().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return routeList.get(groupPosition).getStudents().size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        View resultView = convertView;
        ViewHolder stuNameHolder;

        if (resultView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resultView = inflater.inflate(R.layout.loc_item_layout, parent, false);

            stuNameHolder = new ViewHolder();
            stuNameHolder.textLabel = (TextView) resultView.findViewById(R.id.loc_stu_name);
            resultView.setTag(stuNameHolder);
        }
        else {
            stuNameHolder = (ViewHolder) resultView.getTag();
        }
        final String student = getChild(groupPosition, childPosition);
        stuNameHolder.textLabel.setText(student);
        return resultView;

    }

    @Override
    public Route getGroup(int groupPosition) {
        return routeList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return routeList.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View resultView = convertView;
        ViewHolder holder;
        System.out.print("Number of routes (from GroupView) are: ");
        System.out.println(getGroupCount());

        if (resultView == null) {
//            System.out.println(0);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            System.out.println(1);
            resultView = inflater.inflate(R.layout.loc_row_layout, parent, false);

//            System.out.println(2);
            holder = new ViewHolder();
//            System.out.println(3);
            holder.textLabel = (TextView) resultView.findViewById(R.id.m_location);
//            System.out.println(4);
            resultView.setTag(holder);
//            System.out.println(5);
        }
        else {
//            System.out.println(6);
            holder = (ViewHolder) resultView.getTag();
        }

//        System.out.println(7);
        final Route route = getGroup(groupPosition);
//        System.out.println(8);
        holder.textLabel.setText(route.getLocation());
//        System.out.println(9);
        return resultView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
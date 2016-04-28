package activetransportation.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpandableTimeLocationListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    private ArrayList<Route> routeList;
    private Context context;

    private static final class ViewHolder {
        TextView textLabel;
    }
    private static final class ViewHolderGroup {
        TextView text1;
        TextView text2;
    }

    public ExpandableTimeLocationListAdapter(ArrayList<Route> routeList, Context context) {
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
        ViewHolderGroup holder;
        System.out.print("Number of routes (from GroupView) are: ");
        System.out.println(getGroupCount());

        if (resultView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resultView = inflater.inflate(R.layout.loc_row_layout, parent, false);

            holder = new ViewHolderGroup();
            holder.text1 = (TextView) resultView.findViewById(R.id.m_location);
            holder.text2 = (TextView) resultView.findViewById(R.id.m_time);
            resultView.setTag(holder);
        }
        else {
            holder = (ViewHolderGroup) resultView.getTag();
        }

        final Route route = getGroup(groupPosition);
        holder.text1.setText(route.getLocation());
        holder.text2.setText(route.getInputTimeString());
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
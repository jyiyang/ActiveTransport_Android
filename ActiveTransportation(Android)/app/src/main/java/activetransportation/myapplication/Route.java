package activetransportation.myapplication;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Route {
    private String routeName;
    private String routeID;
    private String staffID;
    private String location;
    private ArrayList<String> students;
    private GregorianCalendar time;
    private String timeStr;

    public Route(String name) {
        this.routeName = name;
        this.students = new ArrayList<String>();
    }

    public Route(String name, String location, String time, ArrayList<String> students) {
        this.routeName = name;
        this.location = location;
        this.timeStr = time;
        this.students = students;
    }

    public String getRouteID() { return routeID; }
    public String getRouteName() { return routeName; }
    public String getStaffID() { return staffID; }
    public String getLocation() { return location; }
    public GregorianCalendar getTimeRaw() { return time; }
    public String getTimeString() {
        String str = DateFormat.getDateTimeInstance().format(time.getTime());
        return str;
    }
    public String getInputTimeString() {
        return timeStr;
    }
    public ArrayList<String> getStudents() { return students; }


    public void setTime(GregorianCalendar time) { this.time = time; }
    public void setLocation(String location) { this.location = location; }
    public void setStaff(String staffID) { this.staffID = staffID; }
    public void addStudent(String studentID) { this.students.add(studentID); }
    public void setID(String routeID) { this.routeID = routeID; }
    public void setName(String name) { this.routeName = name; }
}

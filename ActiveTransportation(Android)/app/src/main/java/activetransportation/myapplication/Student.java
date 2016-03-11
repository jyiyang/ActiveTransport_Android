package activetransportation.myapplication;

public class Student {

    private String ID;
    private String name;
    private String parentID;
    private String staffID;
    private String routeID;
    private boolean isArrived;
    private String leaderNotes;
    private String parentNotes;

    public Student(String name) {
        this.name = name;
        this.isArrived = false;
    }

    public String getID() { return ID; }
    public String getName() { return name; }
    public String getParentID() { return parentID; }
    public String getStaffID() { return staffID; }
    public boolean getIsArrived() { return isArrived; }
    public String getLeaderNotes() { return leaderNotes; }
    public String getParentNotes() { return parentNotes; }
    public String getRouteID() { return routeID; }

    public void setID(String ID) { this.ID = ID; }
    public void setIsArrived(boolean isArrived) { this.isArrived = isArrived; }
    public void setParentID(String parentID) { this.parentID = parentID; }
    public void setStaffID(String staffID) { this.staffID = staffID; }
    public void setLeaderNotes(String leaderNotes) { this.leaderNotes = leaderNotes; }
    public void setParentNotes(String parentNotes) { this.parentNotes = parentNotes; }
    public void setRouteID(String routeID) { this.routeID = routeID; }
}
package activetransportation.myapplication;

public class Student {

    private String ID;
    private String name;
    private String parentID;
    private String staffID;
    private boolean isArrived;
    private String leaderNotes;
    private String parentNotes;

    public Student(String ID, String name, String parentID, String staffID) {
        this.ID = ID;
        this.name = name;
        this.parentID = parentID;
        this.staffID = staffID;
        this.isArrived = false;
    }

    public String getID() { return ID; }
    public String getName() { return name; }
    public String getParentID() { return parentID; }
    public String getStaffID() { return staffID; }
    public boolean getIsArrived() { return isArrived; }
    public String getLeaderNotes() { return leaderNotes; }
    public String getParentNotes() { return parentNotes; }

    public void setIsArrived(boolean isArrived) { this.isArrived = isArrived; }
    public void setLeaderNotes(String leaderNotes) { this.leaderNotes = leaderNotes; }
    public void setParentNotes(String parentNotes) { this.parentNotes = parentNotes; }
}
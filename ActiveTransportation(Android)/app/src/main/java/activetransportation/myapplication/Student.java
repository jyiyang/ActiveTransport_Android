package activetransportation.myapplication;

public class Student {

    private int ID;
    private String name;
    private int parentID;
    private boolean isArrived;
    private String leaderNotes;
    private String parentNotes;

    public Student() {}
    public Student(int ID, String name, int parentID) {
        this.ID = ID;
        this.name = name;
        this.parentID = parentID;
        this.isArrived = false;
    }

    public int getID() {
        return ID;
    }
    public int getName() {
        return name;
    }
    public int getParentID() {
        return parentID;
    }
    public int getIsArrived() {
        return isArrived;
    }
    public int getLeaderNotes() {
        return leaderNotes;
    }
    public int getParentNotes() {
        return parentNotes;
    }

    public void setIsArrived(boolean isArrived) {
        this.isArrived = isArrived;
    }
    public void setLeaderNotes(String leaderNotes) {
        this.leaderNotes = leaderNotes;
    }
    public void setParentNotes(String parentNotes) {
        this.parentNotes = parentNotes;
    }
}
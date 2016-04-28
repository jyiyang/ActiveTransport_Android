package activetransportation.myapplication;

/**
 * The Student Class
 */
public class Student {

    private String ID;
    private String name;
    private String parentID;
    private String routeID;
    private boolean isArrived;


    public Student(String name) {
        this.name = name;
        this.isArrived = false;
    }

    public String getID() { return ID; }
    public String getName() { return name; }
    public String getParentID() { return parentID; }
    public String getRouteID() { return routeID; }
    public boolean getIsArrived() { return isArrived; }


    public void setID(String ID) { this.ID = ID; }
    public void setIsArrived(boolean isArrived) { this.isArrived = isArrived; }
    public void setParentID(String parentID) { this.parentID = parentID; }
    public void setRouteID(String routeID) { this.routeID = routeID; }

}
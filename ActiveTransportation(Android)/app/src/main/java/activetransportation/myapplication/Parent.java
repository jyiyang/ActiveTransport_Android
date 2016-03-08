package activetransportation.myapplication;

import java.util.ArrayList;

public class Parent extends User {
    private ArrayList<String> childrenIDs;

    public Parent(String userID, String email, String name, String contactInfo,
                  boolean isStaff, String routeID, ArrayList<String> studentIDs) {
        super(userID, email, name, contactInfo, isStaff, routeID);
        this.childrenIDs = studentIDs;
    }

    public ArrayList<String> getChildren() { return childrenIDs; }
}

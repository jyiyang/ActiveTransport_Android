package activetransportation.myapplication;

import java.util.ArrayList;

/**
 * The Parent Class
 * It is a subclass of the user class.
 */
public class Parent extends User {

    private ArrayList<String> childrenIDs;

    public Parent(String id, String email, String name, String contactInfo,
                  boolean isStaff) {
        super(id, email, name, contactInfo, isStaff);
    }

    public ArrayList<String> getChildren() { return childrenIDs; }
    public void setChildren(ArrayList<String> childrenIDs) {this.childrenIDs = childrenIDs; }

}

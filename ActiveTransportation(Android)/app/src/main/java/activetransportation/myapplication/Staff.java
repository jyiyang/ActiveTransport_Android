package activetransportation.myapplication;

import java.util.ArrayList;

public class Staff extends User {

    public Staff(String id, String email, String name, String contactInfo,
                 boolean isStaff) {
        super(id, email, name, contactInfo, isStaff);
    }
    private String routeID;

    public String getRouteID() { return routeID; }

    public void setRouteID(String routeID) { this.routeID = routeID; }
}
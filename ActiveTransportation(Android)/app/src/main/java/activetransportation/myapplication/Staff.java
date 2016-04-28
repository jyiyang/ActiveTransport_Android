package activetransportation.myapplication;

/**
 * The Staff Class
 * It is a subclass of the user class.
 */
public class Staff extends User {

    private String routeID;

    public Staff(String id, String email, String name, String contactInfo,
                 boolean isStaff) {
        super(id, email, name, contactInfo, isStaff);
    }

    public String getRouteID() { return routeID; }
    public void setRouteID(String routeID) { this.routeID = routeID; }
}

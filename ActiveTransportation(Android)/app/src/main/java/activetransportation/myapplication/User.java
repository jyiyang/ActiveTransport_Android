package activetransportation.myapplication;

public class User {
    private String userID;
    private String name;
    private String email;
    private boolean isStaff;
    private String contactInfo;
    private String routeID;

    public User(String userID, String email, String name, String contactInfo, boolean isStaff, String routeID) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.contactInfo = contactInfo;
        this.isStaff = isStaff;
        this.routeID = routeID;
    }

    public String getUserID() { return userID; }
    public String name() { return name; }
    public String email() { return email; }
    public String getContactInfo() { return contactInfo; }
    public boolean getIsStaff() { return isStaff; }
    public String getRouteID() { return routeID; }

    public void setRouteID(String routeID) { this.routeID = routeID; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

}

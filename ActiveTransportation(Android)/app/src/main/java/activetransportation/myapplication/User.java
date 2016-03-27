package activetransportation.myapplication;

import java.util.ArrayList;

public class User {
    private String userID;
    private String name;
    private String email;
    private boolean isStaff;
    private String contactInfo;
    private ArrayList<String> studentIDs;

    public User(String id, String email, String name, String contactInfo, boolean isStaff) {
        this.userID = id;
        this.email = email;
        this.name = name;
        this.contactInfo = contactInfo;
        this.isStaff = isStaff;
    }

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactInfo() { return contactInfo; }
    public boolean getIsStaff() { return isStaff; }
    public ArrayList<String> getStudents() { return studentIDs; }

    public void setUserID(String userID) { this.userID = userID; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setChildren(ArrayList<String> studentIDs) {this.studentIDs = studentIDs; }
    public void setStaff(boolean isStaff) {this.isStaff = isStaff; }

}

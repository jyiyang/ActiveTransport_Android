package activetransportation.myapplication;

public class CheckListItem {

    private String studentName;
    private String parentName;
    private String parentContactInfo;
    private Boolean isArrived;

    public CheckListItem(String studentName, String parentName, String parentContactInfo) {
        this.studentName = studentName;
        this.parentName = parentName;
        this.parentContactInfo = parentContactInfo;
        this.isArrived = false;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentContactInfo(String parentContactInfo) {
        this.parentContactInfo = parentContactInfo;
    }

    public String getParentContactInfo() {
        return parentContactInfo;
    }

    public void setArrivalInfo(Boolean isArrived) {
        this.isArrived = isArrived;
    }

    public Boolean getArrivalInfo() {
        return isArrived;
    }
}
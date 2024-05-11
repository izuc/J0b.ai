package ai.j0b.model;

public class ExperienceModel {
    private String organization;
    private String fromDate;
    private String toDate;
    private boolean isContinue;
    private String role;

    public ExperienceModel() {
    }

    public ExperienceModel(String organization, String fromDate, String toDate, boolean isContinue, String role) {
        this.organization = organization;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.isContinue = isContinue;
        this.role = role;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExperienceModel that = (ExperienceModel) obj;
        return isContinue == that.isContinue &&
                java.util.Objects.equals(organization, that.organization) &&
                java.util.Objects.equals(fromDate, that.fromDate) &&
                java.util.Objects.equals(toDate, that.toDate) &&
                java.util.Objects.equals(role, that.role);
    }

    public int hashCode() {
        return java.util.Objects.hash(organization, fromDate, toDate, isContinue, role);
    }

    public String toString() {
        return "ExperienceModel{" +
                "organization='" + organization + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", isContinue=" + isContinue +
                ", role='" + role + '\'' +
                '}';
    }
}
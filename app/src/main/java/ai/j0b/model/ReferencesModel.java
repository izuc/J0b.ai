package ai.j0b.model;

public class ReferencesModel {
    private String name;
    private String email;
    private String number;
    private String organization;
    private String designation;

    public ReferencesModel() {
    }

    public ReferencesModel(String name, String email, String number, String organization, String designation) {
        this.name = name != null ? name : "";
        this.email = email != null ? email : "";
        this.number = number != null ? number : "";
        this.organization = organization != null ? organization : "";
        this.designation = designation != null ? designation : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number != null ? number : "";
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization != null ? organization : "";
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation != null ? designation : "";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReferencesModel that = (ReferencesModel) obj;
        return java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(number, that.number) &&
                java.util.Objects.equals(organization, that.organization) &&
                java.util.Objects.equals(designation, that.designation);
    }

    public int hashCode() {
        return java.util.Objects.hash(name, email, number, organization, designation);
    }

    public String toString() {
        return "ReferencesModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", number='" + number + '\'' +
                ", organization='" + organization + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}

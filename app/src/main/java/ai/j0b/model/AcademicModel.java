package ai.j0b.model;

public class AcademicModel {
    private String name;
    private String institute;
    private String percentage;
    private String cgpa;
    private String year;

    public AcademicModel() {
    }

    public AcademicModel(String name, String institute, String percentage, String cgpa, String year) {
        this.name = name;
        this.institute = institute;
        this.percentage = percentage;
        this.cgpa = cgpa;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AcademicModel that = (AcademicModel) obj;
        return java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(institute, that.institute) &&
                java.util.Objects.equals(percentage, that.percentage) &&
                java.util.Objects.equals(cgpa, that.cgpa) &&
                java.util.Objects.equals(year, that.year);
    }

    public int hashCode() {
        return java.util.Objects.hash(name, institute, percentage, cgpa, year);
    }

    public String toString() {
        return "AcademicModel{" +
                "name='" + name + '\'' +
                ", institute='" + institute + '\'' +
                ", percentage='" + percentage + '\'' +
                ", cgpa='" + cgpa + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
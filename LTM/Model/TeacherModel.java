package LTM.Model;

public class TeacherModel {
    private String id;
    private String name;
    private String dateOfBirth;
    private String university;

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getUniversity() {
        return university;
    }

}

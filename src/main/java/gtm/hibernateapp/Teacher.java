package gtm.hibernateapp;

public class Teacher {
    String name, surname;
    TeacherCondition condition;
    int birth_year;
    double salary;
    String phone_number;
    String email;

    public Teacher(String na, String sna, TeacherCondition con, int by, double sa, String pn, String em) {
        this.name = na;
        this.surname = sna;
        this.condition = con;
        this.birth_year = by;
        this.salary = sa;
        this.phone_number = pn;
        this.email = em;
    }
    public double getSalary() {
        return salary;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getSurname() {
        return surname;
    }

    public TeacherCondition getCondition() {
        return condition;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }

    public void setCondition(TeacherCondition condition) {
        this.condition = condition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}

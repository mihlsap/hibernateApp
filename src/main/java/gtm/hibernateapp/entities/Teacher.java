package gtm.hibernateapp.entities;

import gtm.hibernateapp.TeacherCondition;
import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name, surname;
    TeacherCondition condition;
    int birth_year;
    double salary;
    String phone_number;
    String email;

    @OneToOne
    Group group;

    public Teacher() {}

    public void setId(Long id) {
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", condition=" + condition +
                ", birth_year=" + birth_year +
                ", salary=" + salary +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", group=" + group +
                '}';
    }
}

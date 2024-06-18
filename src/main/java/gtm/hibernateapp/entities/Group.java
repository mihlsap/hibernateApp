package gtm.hibernateapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name_of_group;
    int max_occupancy;
    String occupancy;
    int number_of_rates;
    double average_rate;
    @Transient
    int number_of_teachers;

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public int getNumber_of_teachers() {
        return number_of_teachers;
    }

    public void setNumber_of_teachers(int number_of_teachers) {
        this.number_of_teachers = number_of_teachers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getNumber_of_rates() {
        return number_of_rates;
    }

    public void setNumber_of_rates(int number_of_rates) {
        this.number_of_rates = number_of_rates;
    }

    public double getAverage_rate() {
        return average_rate;
    }

    public void setAverage_rate(double average_rate) {
        this.average_rate = average_rate;
    }

    public int getMax_occupancy() {
        return max_occupancy;
    }

    public String getName_of_group() {
        return name_of_group;
    }

    public void setMax_occupancy(int max_occupancy) {
        this.max_occupancy = max_occupancy;
    }

    public void setName_of_group(String name_of_group) {
        this.name_of_group = name_of_group;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name_of_group='" + name_of_group + '\'' +
                ", max_occupancy=" + max_occupancy +
                ", occupancy='" + occupancy + '\'' +
                ", number_of_rates=" + number_of_rates +
                ", average_rate=" + average_rate +
                ", number_of_teachers=" + number_of_teachers +
                '}';
    }
}
package gtm.hibernateapp.entities;

import gtm.hibernateapp.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.*;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name_of_group;
    int max_occupancy;
    String occupancy = "0.0%";
    int number_of_rates;
    double average_rate;

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
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

    public List<Teacher> getTeachers () {
        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new
                CustomPersistenceUnitInfo(), new HashMap<>());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Teacher> teachers;
        try {
            entityManager.getTransaction().begin();

            TypedQuery<Teacher> teacherTypedQuery = entityManager.createQuery("select t from Teacher t where t.group.id = :number", Teacher.class);
            teacherTypedQuery.setParameter("number", getId());
            teachers = teacherTypedQuery.getResultList();
        }
        finally {
            entityManager.close();
        }

        return teachers;
    }

    public List<Rate> getRates () {
        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new
                CustomPersistenceUnitInfo(), new HashMap<>());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Rate> rates;
        try {
            entityManager.getTransaction().begin();

            TypedQuery<Rate> rateTypedQuery = entityManager.createQuery("select r.value from Rate r where r.group.id = :number", Rate.class);
            rateTypedQuery.setParameter("number", getId());
            rates = rateTypedQuery.getResultList();
        }
        finally {
            entityManager.close();
        }

        return rates;
    }

    public void calculate_average_rate() {
        double average = 0;
        List<Rate> rates = getRates();

        for (Rate rate : rates)
            average += rate.value;
        average /= rates.size();

        average_rate = average;
    }

    public void calculate_occupancy() {
        List<Teacher> teachers = getTeachers();
        occupancy = teachers.size() / max_occupancy + " %";
    }

    @Override
    public String toString() {
        return "GroupEntity{" +
                "id=" + id +
                ", name_of_group='" + name_of_group + '\'' +
                ", max_occupancy=" + max_occupancy +
                ", occupancy='" + occupancy + '\'' +
                ", number_of_rates=" + number_of_rates +
                ", average_rate=" + average_rate +
                '}';
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
}
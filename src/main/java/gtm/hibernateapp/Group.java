package gtm.hibernateapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Group {
    String name_of_group;
    ObservableList<Teacher> teachersList;
    int max_occupancy;
    SimpleStringProperty occupancy;

    public Group(String name_of_group, int max_occupancy) {
        this.name_of_group = name_of_group;
        this.max_occupancy = max_occupancy;
        teachersList = FXCollections.observableArrayList();
        occupancy = new SimpleStringProperty();
        calculateOccupancy();
    }

    public void addTeacher(Teacher teacher) {
        teachersList.add(teacher);
        calculateOccupancy();
    }

    public void removeTeacher(Teacher teacher) {
        teachersList.remove(teacher);
        calculateOccupancy();
    }

    public void calculateOccupancy() {
        double percentage = (double) teachersList.size() / max_occupancy * 100;
        occupancy.set(percentage + "%");
    }

    @Override
    public String toString() {
        return name_of_group;
    }

    public int getMax_occupancy() {
        return max_occupancy;
    }

    public String getName_of_group() {
        return name_of_group;
    }

    public ObservableList<Teacher> getTeachersList() {
        return teachersList;
    }

    public SimpleStringProperty occupancyProperty() {
        return occupancy;
    }

    public String getOccupancy() {
        return occupancy.get();
    }

    public void setMax_occupancy(int max_occupancy) {
        this.max_occupancy = max_occupancy;
    }

    public void setName_of_group(String name_of_group) {
        this.name_of_group = name_of_group;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy.set(occupancy);
    }

    public void setTeachersList(ObservableList<Teacher> teachersList) {
        this.teachersList = teachersList;
    }

}
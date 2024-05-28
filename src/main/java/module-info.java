module gtm.hibernateapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jakarta.persistence;
    requires com.zaxxer.hikari;
    requires org.hibernate.orm.core;
    requires org.slf4j;

    opens gtm.hibernateapp to javafx.fxml;
    exports gtm.hibernateapp;
    exports gtm.hibernateapp.entities;
    opens gtm.hibernateapp.entities to org.hibernate.orm.core, javafx.fxml;
}

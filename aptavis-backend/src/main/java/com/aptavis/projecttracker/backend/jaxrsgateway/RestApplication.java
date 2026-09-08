package com.aptavis.projecttracker.backend.jaxrsgateway;

import com.aptavis.projecttracker.backend.application.ProjectApplication;
import com.aptavis.projecttracker.backend.application.TaskApplication;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@DataSourceDefinition(
    name = "java:jboss/datasources/ProjectTrackerDS",
    className = "org.postgresql.ds.PGSimpleDataSource",
    url = "jdbc:postgresql://${env.POSTGRES_HOST:dpg-dag1l20n74is73c5227g-a}:5432/${env.POSTGRES_DB:projecttracker}",
    user = "${env.POSTGRES_USER:postgres}",
    password = "${env.POSTGRES_PASSWORD:postgres}"
)
@ApplicationPath("/rest")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
            ProjectApplication.class,
            TaskApplication.class
        );
    }
}

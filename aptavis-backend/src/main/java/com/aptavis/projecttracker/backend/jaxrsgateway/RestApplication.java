package com.aptavis.projecttracker.backend.jaxrsgateway;

import com.aptavis.projecttracker.backend.application.ProjectApplication;
import com.aptavis.projecttracker.backend.application.TaskApplication;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@DataSourceDefinition(
    name = "java:app/datasources/ProjectTrackerDS",
    className = "org.postgresql.ds.PGSimpleDataSource",
    serverName = "dpg-dag1l20n74is73c5227g-a.singapore-postgres.render.com",
    portNumber = 5432,
    databaseName = "projecttracker_ctuo",
    user = "wafi",
    password = "5Cu4AG8W66PDP4B4SkNS3xu8zDQ7kXI1",
    url = "jdbc:postgresql://dpg-dag1l20n74is73c5227g-a.singapore-postgres.render.com:5432/projecttracker_ctuo"
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

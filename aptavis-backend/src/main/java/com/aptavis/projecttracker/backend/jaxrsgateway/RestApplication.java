package com.aptavis.projecttracker.backend.jaxrsgateway;

import com.aptavis.projecttracker.backend.application.ProjectApplication;
import com.aptavis.projecttracker.backend.application.TaskApplication;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

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

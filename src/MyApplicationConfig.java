import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("introsde.business-logic-service")
public class MyApplicationConfig extends ResourceConfig {
    public MyApplicationConfig () {
        packages("resources");
    }
}

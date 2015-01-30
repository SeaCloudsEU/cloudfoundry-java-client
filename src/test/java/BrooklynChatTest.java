import connectors.cloudfoundry.CloudFoundryConnector;
import connectors.cloudfoundry.db.SQLScripts;

/**
 * Created by Jose on 28/01/15.
 */
public class BrooklynChatTest {


    public static void main(String[] args)
    {
        try {

            //Target and credentials
            String target = "https://api.run.pivotal.io";
            String user = "***";
            String password = "***";
            String organization = "***";
            String space = "development";


            //Databaser Service
            String databaseServie = "cleardb";
            String serviceName = "mycleardb";
            String servicePlan ="spark";
            boolean freePlan = true;

            //ChatApplication Configuration
            String applicationName = "client-example";
            String applicationDomain = "";
            String applicationWar = "brooklyn-example-hello-world-webapp.war";
            String sqlScript = "database.sql";
            String buildPack = "https://github.com/cloudfoundry/java-buildpack.git";
            String warPath = BrooklynChatTest.class.getResource(applicationWar).getFile();
            String sqlPath = BrooklynChatTest.class.getResource(sqlScript).getFile();

            // Connect to CF: OK
            CloudFoundryConnector cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);

            cfClient.deployAppWithDatabasePopulate(
                    applicationName,applicationDomain, warPath, buildPack,
                    databaseServie, serviceName, servicePlan, freePlan,
                    sqlPath, SQLScripts.SQL_TYPE.MySQL);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}

import connectors.cloudfoundry.CloudFoundryConnector;
import connectors.cloudfoundry.db.SQLScripts;

/**
 * 
 * @author 
 *
 */
public class CustomTests 
{

	
	public static void main(String[] args) 
	{
		try {
			// https://api.run.pivotal.io
			String target = "https://api.run.pivotal.io"; 
			String user = "";
			String password = "";
			String organization = "org"; 				
			String space = "development";						
		
			// Connect to CF: OK
			System.out.println("---------------------------------------------------------");
			CloudFoundryConnector cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);

			// DEPLOY APP: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployApp("newApp1", "", 
							   "C:\\Users\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
							   "https://github.com/java-buildpack.git");*/

			// DEPLOY APP + DATABASE BINDING - cleardb: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabase("newApp12", 
					   "C:\\Users\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true);*/
			
			// DEPLOY APP + DATABASE BINDING - elephantsql: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabase("newApp123", "", 
					   "C:\\Users\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/java-buildpack.git",
					   "elephantsql", "myelephantsql", "turtle", true);*/
			
			// DELETE APP and SERVICES: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deleteApp("newApp1");
			System.out.println("---------------------------------------------------------");
			cfClient.deleteApp("newApp12");
			System.out.println("---------------------------------------------------------");
			cfClient.deleteApp("newApp123");*/
			//cfClient.deleteApp("newApp1asdasd");
			
			// DEPLOY APP - wrong parameters: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployApp("newApp1", "", 
							   "C:\\Users\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui123.war", 
							   "https://github.com/java-buildpack.git");*/
			
			
			// DEPLOY APP + DATABASE BINDING + EXECUTE SCRIPT: OK
			System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabasePopulate("newApp1", "", 
					   "C:\\Users\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true,
					   "C:\\PROYECTOS\\SEACLOUDS\\mysql_db_struct.sql",
					   SQLScripts.SQL_TYPE.MySQL);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
}

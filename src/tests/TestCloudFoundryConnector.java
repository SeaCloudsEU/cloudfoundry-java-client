package tests;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import connectors.cloudfoundry.CloudFoundryConnector;
import connectors.cloudfoundry.DataBaseParameters;
import connectors.cloudfoundry.db.SQLScripts;


/**
 * 
 * @author a572832
 *
 */
public class TestCloudFoundryConnector 
{

	
	public static void main(String[] args) 
	{
		try {
			// https://api.run.pivotal.io
			String target = "https://api.run.pivotal.io"; 
			String user = "rsucasas@gmail.com"; 				
			String password = "scss852ATOS"; 					
			String organization = "rsucasas-org"; 				
			String space = "development";
			
			// https://api.run.pivotal.io
			// 		--> 	cf login -a https://api.95.211.172.243.xip.io -u admin -p c1oudc0w --skip-ssl-validation
			/*String target = "https://api.95.211.172.243.xip.io"; 
			String user = "admin"; 							
			String password = "c1oudc0w"; 						
			String organization = "DevBox"; 					 
			String space = "ATOS";*/ 								
		
			// Connect to CF: OK
			System.out.println("---------------------------------------------------------");
			CloudFoundryConnector cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);

			// DEPLOY APP: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployApp("newApp1", "", 
							   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
							   "https://github.com/rsucasas/java-buildpack.git");*/

			// DEPLOY APP + DATABASE BINDING - cleardb: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabase("newApp12", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true);*/
			
			// DEPLOY APP + DATABASE BINDING - elephantsql: OK
			/*System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabase("newApp123", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
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
							   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui123.war", 
							   "https://github.com/rsucasas/java-buildpack.git");*/
			
			
			// DEPLOY APP + DATABASE BINDING + EXECUTE SCRIPT: OK
			System.out.println("---------------------------------------------------------");
			cfClient.deployAppWithDatabasePopulate("newApp1", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
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

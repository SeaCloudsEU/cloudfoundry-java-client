package tests;

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





public class TestCloudFoundryConnector 
{

	
	public static void main(String[] args) 
	{
		try {
			/*String url = "jdbc:mysql://bc686c6a95e7a4:ac77e270@us-cdbr-iron-east-01.cleardb.net:3306/ad_83822612f2b3cdd";
			// jdbc:mysql://bc686c6a95e7a4:ac77e270@us-cdbr-iron-east-01.cleardb.net:3306/ad_83822612f2b3cdd
			//		-->
			// jdbc:mysql://us-cdbr-iron-east-01.cleardb.net:3306/ad_f842632d7060bf2
			
			int pos1 = url.indexOf("://") + 3;
			int pos2 = url.indexOf("@");
			
			String res = url.substring(0, pos1) + url.substring(pos2 + 1);*/
			
			
			
			String target = "https://api.run.pivotal.io"; 
			String user = "rsucasas@gmail.com"; 			//"admin" "rsucasas"
			String password = "scss852ATOS"; 	//"c1oudc0w" "1234qwer"
			String organization = "rsucasas-org"; //"DevBox" 
			String space = "development"; // "ATOS"
			
			CloudFoundryConnector cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);
			//DataBaseParameters db = cfClient.getDBEnvValues("newApp123");
			
			// DEPLOY APP: OK
			/*
			cfClient.deployApp("newApp123", "", 
							   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
							   "https://github.com/rsucasas/java-buildpack.git");
			*/
			
			// DEPLOY APP + DATABASE BINDING: OK
			/*cfClient.deployAppWithMySQL("newApp123", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true);
			
			cfClient.deployAppWithMySQL("newApp1234", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "elephantsql", "myelephantsql", "turtle", true);*/
			
			// DELETE APP and SERVICES: OK
			//cfClient.deleteApp("newApp123");
			//cfClient.deleteApp("newApp1234");
			
			// DEPLOY APP + DATABASE BINDING + EXECUTE SCRIPT: OK
			/*cfClient.deployAppWithDatabaseAndData("newApp123", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true,
					   "C:\\PROYECTOS\\SEACLOUDS\\ehealth20141217.sql",
					   SQLScripts.SQL_TYPE.MySQL);*/
			
			// jdbc:mysql://us-cdbr-iron-east-01.cleardb.net:3306/ad_f842632d7060bf2
			SQLScripts sqlS = new SQLScripts();
			sqlS.createDB("jdbc:mysql://us-cdbr-iron-east-01.cleardb.net:3306/ad_f842632d7060bf2", 
						  "bafb17bf9e8337", "03cc3aa2", 
						  "C:\\PROYECTOS\\SEACLOUDS\\mysql_db_struct.sql",
					      SQLScripts.SQL_TYPE.MySQL);

			//String s = "123123";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
}

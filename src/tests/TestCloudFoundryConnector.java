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





public class TestCloudFoundryConnector 
{

	
	public static void main(String[] args) 
	{
		try {
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
			cfClient.deployAppWithMySQL("newApp123", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "cleardb", "mycleardb", "spark", true);
			
			cfClient.deployAppWithMySQL("newApp1234", "", 
					   "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
					   "https://github.com/rsucasas/java-buildpack.git",
					   "elephantsql", "myelephantsql", "turtle", true);
			

			//Thread.sleep(30000);
			//cfClient.deleteApp("newApp1234");
			
			//DataBaseParameters dbparams = cfClient.getDBEnvValues("newApp1234", "elephantsql");
			//String s = "123123";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
}

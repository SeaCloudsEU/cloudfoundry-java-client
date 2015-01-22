package tests;

import static org.junit.Assert.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import connectors.cloudfoundry.CloudFoundryConnector;


/**
 * 
 * @author a572832
 *
 */
public class TestCloudFoundryV2Connector
{


	String target = "https://api.run.pivotal.io"; 
	String user = "rsucasas@gmail.com"; 				
	String password = "scss852ATOS"; 					
	String organization = "rsucasas-org"; 				
	String space = "development";
	CloudFoundryConnector cfClient;
	
	private static final Logger logAdapter = Logger.getLogger(TestCloudFoundryV2Connector.class.getName());
	
	
	@Before
	public void setUp() throws Exception
	{
	}
	
	
	@After
	public void tearDown() throws Exception
	{
		logAdapter.log(Level.INFO, ">> closing connection");
		if (cfClient != null)
			cfClient.getConnectedClient().logout();
	}


	@Test
	public void testCloudFoundryConnectorStringStringStringBoolean()
	{
		cfClient = new CloudFoundryConnector(target, user, password, true);
		
		assertTrue(cfClient != null);
	}


	@Test
	public void testCloudFoundryConnectorStringStringStringStringString()
	{
		cfClient = new CloudFoundryConnector(target, user, password, organization, space);
		
		assertTrue(cfClient != null);
	}


	@Test
	public void testCloudFoundryConnectorStringStringStringStringStringBoolean()
	{
		cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);
		
		assertTrue(cfClient != null);
	}

	
	@Test
	public void testDeployAppStringStringStringString()
	{
		cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);
		boolean res = cfClient.deployApp("newApp1", 
										  "", 
								   		  "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
								   		  "https://github.com/rsucasas/java-buildpack.git");
		cfClient.deleteApp("newApp1");
		
		assertTrue(res);
	}
	

	@Test
	public void testDeployAppStringStringString()
	{
		cfClient = new CloudFoundryConnector(target, user, password, organization, space, true);
		boolean res = cfClient.deployApp("newApp1", 
								   		  "C:\\Users\\A572832\\git\\seaclouds_gui\\WebGUI_v2\\target\\softcare-gui.war", 
								   		  "https://github.com/rsucasas/java-buildpack.git");
		cfClient.deleteApp("newApp1");
		
		assertTrue(res);
	}


	/*@Test
	public void testDeployAppWithDatabaseStringStringStringStringStringStringStringBoolean()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testDeployAppWithDatabaseStringStringStringStringStringStringBoolean()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testDeployAppWithDatabasePopulate()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testDeleteApp()
	{
		fail("Not yet implemented");
	}
*/
	
}

package tests;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


/**
 * 
 * 
 * @author a572832
 *
 */
public final class JavaSample {

	
	
	public static void main(String[] args) 
	{
		try {
			String target = "https://api.95.211.172.243.xip.io"; //args[0];
			String user = "rsucasas"; //"admin"; //args[1];
			String password = "1234qwer"; //"c1oudc0w"; //args[2];
			String organization = "DevBox"; 
			String space1 = "ATOS";
			
	
			CloudCredentials credentials = new CloudCredentials(user, password);
			CloudFoundryClient client = new CloudFoundryClient(credentials, getTargetURL(target), organization, space1, true);
			client.login();
	
			System.out.printf("%nSpaces:%n");
			for (CloudSpace space : client.getSpaces()) {
				System.out.printf("  %s\t(%s)%n", space.getName(), space.getOrganization().getName());
			}
	
			System.out.printf("%nApplications:%n");
			for (CloudApplication application : client.getApplications()) {
				System.out.printf("  %s%n", application.getName());
			}
	
			System.out.printf("%nServices%n");
			for (CloudService service : client.getServices()) {
				System.out.printf("  %s\t(%s)%n", service.getName(), service.getLabel());
			}

			//client.
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static URL getTargetURL(String target) {
		try 
		{
			return URI.create(target).toURL();
		} 
		catch (MalformedURLException e) {
			throw new RuntimeException("The target URL is not valid: " + e.getMessage());
		}
	}
	

}

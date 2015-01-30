package connectors.cloudfoundry.libadapter;

import java.net.URL;
import java.util.Map;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;


/**
 * 
 * @author a572832
 *
 */
public class CustomCloudFoundryClient
{

	
	private CustomCloudControllerClientImpl cc;


	/**
	 * Construct client without a default org and space.
	 * 
	 * @param credentials
	 * @param cloudControllerUrl
	 */
	public CustomCloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl) {
		this(credentials, cloudControllerUrl, null, (HttpProxyConfiguration) null, false);
	}

	
	/**
	 * 
	 * @param credentials
	 * @param cloudControllerUrl
	 * @param trustSelfSignedCerts
	 */
	public CustomCloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl, boolean trustSelfSignedCerts) {
		this(credentials, cloudControllerUrl, null, (HttpProxyConfiguration) null, trustSelfSignedCerts);
	}
	
	
	/**
	 * 
	 * @param credentials
	 * @param cloudControllerUrl
	 * @param sessionSpace
	 * @param httpProxyConfiguration
	 * @param trustSelfSignedCerts
	 */
	public CustomCloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace,
			HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		Assert.notNull(cloudControllerUrl, "URL for cloud controller cannot be null");
		CustomCloudControllerClientFactory cloudControllerClientFactory = 
				new CustomCloudControllerClientFactory(httpProxyConfiguration, trustSelfSignedCerts);
		
		this.cc = (CustomCloudControllerClientImpl) cloudControllerClientFactory.newCloudController(cloudControllerUrl, credentials, sessionSpace);
	}

	
	/**
	 * Construct a client with a pre-configured CloudControllerClient
	 */
	public CustomCloudFoundryClient(CustomCloudControllerClientImpl cc) {
		this.cc = cc;
	}

	
	/**
	 * 
	 * @return
	 */
	public OAuth2AccessToken login() {
		return cc.login();
	}
	

	/**
	 * 
	 */
	public void logout() {
		cc.logout();
	}
	
	
	// New methods
	
	/**
	 * Get Environment variables
	 * 
	 * @param appId
	 * @return
	 */
	public Map<String, Object> getEnvValues(String appId) 
	{
		return cc.getEnvValues(appId);
	}
	
	
	/**
	 * 
	 * @param appId
	 * @return
	 */
	public String getEnvValuesAsString(String appId) {
		return cc.getEnvValuesAsString(appId);
	}
	
	
}
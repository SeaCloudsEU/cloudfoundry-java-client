package connectors.cloudfoundry.libadapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.LoggregatorClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.client.RestTemplate;


/**
 * 
 * @author 
 *
 */
public class CustomCloudControllerClientFactory {
	
	
	private final RestUtil restUtil;
	private final RestTemplate restTemplate;
	private OauthClient oauthClient;
	private final HttpProxyConfiguration httpProxyConfiguration;
	private final boolean trustSelfSignedCerts;
	private ObjectMapper objectMapper;
	private final Map<URL, Map<String, Object>> infoCache = new HashMap<URL, Map<String, Object>>();

	
	/**
	 * 
	 * @param httpProxyConfiguration
	 * @param trustSelfSignedCerts
	 */
	public CustomCloudControllerClientFactory(HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		this.restUtil = new RestUtil();
		this.restTemplate = restUtil.createRestTemplate(httpProxyConfiguration, trustSelfSignedCerts);
		this.httpProxyConfiguration = httpProxyConfiguration;
		this.trustSelfSignedCerts = trustSelfSignedCerts;
		this.objectMapper = new ObjectMapper();
	}

	
	/**
	 * 
	 * @param cloudControllerUrl
	 * @param cloudCredentials
	 * @param sessionSpace
	 * @return
	 */
	public CustomCloudControllerClientImpl newCloudController(URL cloudControllerUrl, CloudCredentials cloudCredentials, CloudSpace sessionSpace) {
		createOauthClient(cloudControllerUrl);
		LoggregatorClient loggregatorClient = new LoggregatorClient(trustSelfSignedCerts);

		return new CustomCloudControllerClientImpl(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient,
				cloudCredentials, sessionSpace);
	}

	
	/**
	 * 
	 * @return
	 */
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	
	/**
	 * 
	 * @return
	 */
	public OauthClient getOauthClient() {
		return oauthClient;
	}
	

	/**
	 * 
	 * @param cloudControllerUrl
	 */
	private void createOauthClient(URL cloudControllerUrl) {
		Map<String, Object> infoMap = getInfoMap(cloudControllerUrl);
		URL authorizationEndpoint = getAuthorizationEndpoint(infoMap, cloudControllerUrl);
		this.oauthClient = restUtil.createOauthClient(authorizationEndpoint, httpProxyConfiguration, trustSelfSignedCerts);
	}

	
	/**
	 * 
	 * @param cloudControllerUrl
	 * @return
	 */
	private Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		if (infoCache.containsKey(cloudControllerUrl)) {
			return infoCache.get(cloudControllerUrl);
		}

		String s = restTemplate.getForObject(cloudControllerUrl + "/info", String.class);

		try {
			return objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			throw new RuntimeException("Error getting /info from Cloud Controller", e);
		}
	}

	
	/**
	 * 
	 * @param infoMap
	 * @param cloudControllerUrl
	 * @return
	 */
	private URL getAuthorizationEndpoint(Map<String, Object> infoMap, URL cloudControllerUrl) {
		String authEndPoint = (String) infoMap.get("authorization_endpoint");
		
		try {
			return new URL(authEndPoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Error creating auth endpoint URL for endpoint " + authEndPoint, e);
		}
	}
	

}

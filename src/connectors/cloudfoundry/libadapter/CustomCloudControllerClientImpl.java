package connectors.cloudfoundry.libadapter;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.LoggregatorClient;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;


/**
 * 
 * @author a572832
 *
 */
public class CustomCloudControllerClientImpl 
{

	
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";
	private OauthClient oauthClient;
	private RestTemplate restTemplate;
	private URL cloudControllerUrl;
	protected CloudCredentials cloudCredentials;


	/**
	 * 
	 * @param cloudControllerUrl
	 * @param restTemplate
	 * @param oauthClient
	 * @param loggregatorClient
	 * @param cloudCredentials
	 * @param sessionSpace
	 */
	public CustomCloudControllerClientImpl(URL cloudControllerUrl, RestTemplate restTemplate,
	                                 OauthClient oauthClient, LoggregatorClient loggregatorClient,
	                                 CloudCredentials cloudCredentials, CloudSpace sessionSpace) {
		initialize(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient, cloudCredentials);
	}

	
	/**
	 * 
	 * @param cloudControllerUrl
	 * @param restTemplate
	 * @param oauthClient
	 * @param loggregatorClient
	 * @param cloudCredentials
	 */
	private void initialize(URL cloudControllerUrl, RestTemplate restTemplate, OauthClient oauthClient,
	                        LoggregatorClient loggregatorClient, CloudCredentials cloudCredentials) {
		Assert.notNull(cloudControllerUrl, "CloudControllerUrl cannot be null");
		Assert.notNull(restTemplate, "RestTemplate cannot be null");
		Assert.notNull(oauthClient, "OauthClient cannot be null");

		oauthClient.init(cloudCredentials);

		this.cloudCredentials = cloudCredentials;

		this.cloudControllerUrl = cloudControllerUrl;

		this.restTemplate = restTemplate;
		configureCloudFoundryRequestFactory(restTemplate);

		this.oauthClient = oauthClient;
	}

	
	/**
	 * 
	 * @return
	 */
	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	
	/**
	 * 
	 * @param restTemplate
	 */
	protected void configureCloudFoundryRequestFactory(RestTemplate restTemplate) {
		ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
		if (!(requestFactory instanceof CloudFoundryClientHttpRequestFactory)) {
			restTemplate.setRequestFactory(
					new CloudFoundryClientHttpRequestFactory(requestFactory));
		}
	}

	
	/**
	 * 
	 * @author 
	 *
	 */
	private class CloudFoundryClientHttpRequestFactory implements ClientHttpRequestFactory {

		private ClientHttpRequestFactory delegate;
		private Integer defaultSocketTimeout = 0;

		public CloudFoundryClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
			this.delegate = delegate;
			captureDefaultReadTimeout();
		}

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			ClientHttpRequest request = delegate.createRequest(uri, httpMethod);

			String authorizationHeader = oauthClient.getAuthorizationHeader();
			if (authorizationHeader != null) {
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, authorizationHeader);
			}

			if (cloudCredentials != null && cloudCredentials.getProxyUser() != null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, cloudCredentials.getProxyUser());
			}

			return request;
		}

        private void captureDefaultReadTimeout() {
            if (defaultSocketTimeout == null) {
                try {
                    defaultSocketTimeout = new Socket().getSoTimeout();
                } catch (SocketException e) {
                    defaultSocketTimeout = 0;
                }
            }
        }

	}


	/**
	 * 
	 * @return
	 */
	public OAuth2AccessToken login() {
		oauthClient.init(cloudCredentials);
		return oauthClient.getToken();
	}
	

	/**
	 * 
	 */
	public void logout() {
		oauthClient.clear();
	}

	
	/**
	 * Get all the environment variables
	 * 
	 * Example call:
	 * https://api.95.211.172.243.xip.io/v2/apps/7741aade-efdd-4045-8827-5f3ef1b1072e/env
	 * 
	 * @param appId
	 */
	public Map<String, Object> getEnvValues(String appId) {
		String url = getUrl("/v2/apps/{guid}/env");
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", appId);
		String resp = restTemplate.getForObject(url, String.class, urlVars);
		return JsonUtil.convertJsonToMap(resp);
	}
	
	
	/**
	 * 
	 * @param appId
	 * @return
	 */
	public String getEnvValuesAsString(String appId) {
		String url = getUrl("/v2/apps/{guid}/env");
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", appId);
		String resp = restTemplate.getForObject(url, String.class, urlVars);
		return resp;
	}


	/**
	 * 
	 * @param path
	 * @return
	 */
	private String getUrl(String path) {
		return cloudControllerUrl + (path.startsWith("/") ? path : "/" + path);
	}
	
	
}

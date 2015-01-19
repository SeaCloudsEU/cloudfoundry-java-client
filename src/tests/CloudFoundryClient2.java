package tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.ClientHttpResponseCallback;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.StreamingLogToken;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * A Java client to exercise the Cloud Foundry API.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 * @author Alexander Orlov
 */
public class CloudFoundryClient2 implements CloudFoundryOperations {

	private CloudControllerClientImpl2 cc;

	private CloudInfo info;

	/**
	 * Construct client for anonymous user. Useful only to get to the '/info' endpoint.
	 */

	public CloudFoundryClient2(URL cloudControllerUrl) {
		this(null, cloudControllerUrl, null, (HttpProxyConfiguration) null, false);
	}

	public CloudFoundryClient2(URL cloudControllerUrl, boolean trustSelfSignedCerts) {
		this(null, cloudControllerUrl, null, (HttpProxyConfiguration) null, trustSelfSignedCerts);
	}

	public CloudFoundryClient2(URL cloudControllerUrl, HttpProxyConfiguration httpProxyConfiguration) {
		this(null, cloudControllerUrl, null, httpProxyConfiguration, false);
	}

	public CloudFoundryClient2(URL cloudControllerUrl, HttpProxyConfiguration httpProxyConfiguration,
	                          boolean trustSelfSignedCerts) {
		this(null, cloudControllerUrl, null, httpProxyConfiguration, trustSelfSignedCerts);
	}

	/**
	 * Construct client without a default org and space.
	 */

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl) {
		this(credentials, cloudControllerUrl, null, (HttpProxyConfiguration) null, false);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl,
	                          boolean trustSelfSignedCerts) {
		this(credentials, cloudControllerUrl, null, (HttpProxyConfiguration) null, trustSelfSignedCerts);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl,
	                          HttpProxyConfiguration httpProxyConfiguration) {
		this(credentials, cloudControllerUrl, null, httpProxyConfiguration, false);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl,
	                          HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		this(credentials, cloudControllerUrl, null, httpProxyConfiguration, trustSelfSignedCerts);
	}

	/**
	 * Construct a client with a default CloudSpace.
	 */

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace) {
		this(credentials, cloudControllerUrl, sessionSpace, null, false);
    }

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace,
	                          boolean trustSelfSignedCerts) {
		this(credentials, cloudControllerUrl, sessionSpace, null, trustSelfSignedCerts);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace,
	                          HttpProxyConfiguration httpProxyConfiguration) {
		this(credentials, cloudControllerUrl, sessionSpace, httpProxyConfiguration, false);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace,
	                          HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		Assert.notNull(cloudControllerUrl, "URL for cloud controller cannot be null");
		CloudControllerClientFactory2 cloudControllerClientFactory =
				new CloudControllerClientFactory2(httpProxyConfiguration, trustSelfSignedCerts);
		this.cc = (CloudControllerClientImpl2) cloudControllerClientFactory.newCloudController(cloudControllerUrl, credentials, sessionSpace);
		
		
		
		
		try {
			cc.login();
			
			CloudApplication app = cc.getApplication("newApp123");
			String appId = app.getMeta().getGuid().toString();
			
			RestUtil restUtil = new RestUtil();
			RestTemplate restTemplate = restUtil.createRestTemplate(null, trustSelfSignedCerts);
			
			//
			// https://api.95.211.172.243.xip.io/v2/apps/7741aade-efdd-4045-8827-5f3ef1b1072e/env
			//
			/*String url = getUrl(getTargetURL(APIEndPoint), "/v2/apps/{guid}/env");
			Map<String, Object> urlVars = new HashMap<String, Object>();
			urlVars.put("guid", appId);
			String resp = restTemplate.getForObject(url, String.class, urlVars);*/
			//return JsonUtil.convertJsonToMap(resp);
			
			List<CloudApplication> l = cc.getApplications();
			
			
			cc.getApplications2();
			cc.getApplications3(appId);
			
			
			//
			// /v2/spaces/70bfbf07-3f34-4f85-9d43-31171d4dd71b/apps?q=name%3AnewApp123&inline-relations-depth=1
			// /v2/spaces/70bfbf07-3f34-4f85-9d43-31171d4dd71b/summary
			// /v2/spaces/{space}/apps?inline-relations-depth=1
			//
			String url = getUrl(cloudControllerUrl, "/v2/spaces/{guid}/apps?inline-relations-depth=1");
			Map<String, Object> urlVars = new HashMap<String, Object>();
			urlVars.put("guid", "70bfbf07-3f34-4f85-9d43-31171d4dd71b");
			String resp = restTemplate.getForObject(url, String.class, urlVars);
			//return JsonUtil.convertJsonToMap(resp);
			
			String ss = "";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected String getUrl(URL cloudControllerUrl, String path) {
		return cloudControllerUrl + (path.startsWith("/") ? path : "/" + path);
	}
	

	/**
	 * Construct a client with a default space name and org name.
	 */

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, String orgName, String spaceName) {
		this(credentials, cloudControllerUrl, orgName, spaceName, null, false);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, String orgName, String spaceName,
	                          boolean trustSelfSignedCerts) {
		this(credentials, cloudControllerUrl, orgName, spaceName, null, trustSelfSignedCerts);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, String orgName, String spaceName,
							  HttpProxyConfiguration httpProxyConfiguration) {
		this(credentials, cloudControllerUrl, orgName, spaceName, httpProxyConfiguration, false);
	}

	public CloudFoundryClient2(CloudCredentials credentials, URL cloudControllerUrl, String orgName, String spaceName,
	                          HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		Assert.notNull(cloudControllerUrl, "URL for cloud controller cannot be null");
		CloudControllerClientFactory2 cloudControllerClientFactory =
				new CloudControllerClientFactory2(httpProxyConfiguration, trustSelfSignedCerts);
		this.cc = (CloudControllerClientImpl2) cloudControllerClientFactory.newCloudController(cloudControllerUrl, credentials, orgName, spaceName);
	}

	/**
	 * Construct a client with a pre-configured CloudControllerClient
	 */
	public CloudFoundryClient2(CloudControllerClientImpl2 cc) {
		this.cc = cc;
	}

	public void setResponseErrorHandler(ResponseErrorHandler errorHandler) {
		cc.setResponseErrorHandler(errorHandler);
	}

	public URL getCloudControllerUrl() {
		return cc.getCloudControllerUrl();
	}

	public CloudInfo getCloudInfo() {
		if (info == null) {
			info = cc.getInfo();
		}
		return info;
	}

	public List<CloudSpace> getSpaces() {
		return cc.getSpaces();
	}

	public List<CloudOrganization> getOrganizations() {
		return cc.getOrganizations();
	}

	public void register(String email, String password) {
		cc.register(email, password);
	}

	public void updatePassword(String newPassword) {
		cc.updatePassword(newPassword);
	}

	public void updatePassword(CloudCredentials credentials, String newPassword) {
		cc.updatePassword(credentials, newPassword);
	}

	public void unregister() {
		cc.unregister();
	}

	public OAuth2AccessToken login() {
		return cc.login();
	}

	public void logout() {
		cc.logout();
	}

	public List<CloudApplication> getApplications() {
		return cc.getApplications();
	}

	public CloudApplication getApplication(String appName) {
		return cc.getApplication(appName);
	}

	public CloudApplication getApplication(UUID appGuid) {
		return cc.getApplication(appGuid);
	}

	public ApplicationStats getApplicationStats(String appName) {
		return cc.getApplicationStats(appName);
	}

	public void createApplication(String appName, Staging staging, Integer memory, List<String> uris,
								  List<String> serviceNames) {
		cc.createApplication(appName, staging, memory, uris, serviceNames);
	}

	public void createApplication(String appName, Staging staging, Integer disk, Integer memory, List<String> uris,
								  List<String> serviceNames) {
		cc.createApplication(appName, staging, disk, memory, uris, serviceNames);
	}

	public void createService(CloudService service) {
		cc.createService(service);
	}

	public void createUserProvidedService(CloudService service, Map<String, Object> credentials) {
		cc.createUserProvidedService(service, credentials);
	}

	public void createUserProvidedService(CloudService service, Map<String, Object> credentials, String syslogDrainUrl) {
		cc.createUserProvidedService(service, credentials, syslogDrainUrl);
	}

	@Override
	public List<CloudRoute> deleteOrphanedRoutes() {
    	return cc.deleteOrphanedRoutes();
	}

	public void uploadApplication(String appName, String file) throws IOException {
		cc.uploadApplication(appName, new File(file), null);
	}

	public void uploadApplication(String appName, File file) throws IOException {
		cc.uploadApplication(appName, file, null);
	}

	public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		cc.uploadApplication(appName, file, callback);
	}

	public void uploadApplication(String appName, String fileName, InputStream inputStream) throws IOException {
		cc.uploadApplication(appName, fileName, inputStream, null);
	}

	public void uploadApplication(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback) throws IOException {
		cc.uploadApplication(appName, fileName, inputStream, callback);
	}

	public void uploadApplication(String appName, ApplicationArchive archive) throws IOException {
		cc.uploadApplication(appName, archive, null);
	}

	public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException {
		cc.uploadApplication(appName, archive, callback);
	}

	public StartingInfo startApplication(String appName) {
		return cc.startApplication(appName);
	}

	public void debugApplication(String appName, DebugMode mode) {
		cc.debugApplication(appName, mode);
	}

	public void stopApplication(String appName) {
		cc.stopApplication(appName);
	}

	public StartingInfo restartApplication(String appName) {
		return cc.restartApplication(appName);
	}

	public void deleteApplication(String appName) {
		cc.deleteApplication(appName);
	}

	public void deleteAllApplications() {
		cc.deleteAllApplications();
	}

	public void deleteAllServices() {
		cc.deleteAllServices();
	}

	public void updateApplicationDiskQuota(String appName, int disk) {
		cc.updateApplicationDiskQuota(appName, disk);
	}

	public void updateApplicationMemory(String appName, int memory) {
		cc.updateApplicationMemory(appName, memory);
	}

	public void updateApplicationInstances(String appName, int instances) {
		cc.updateApplicationInstances(appName, instances);
	}

	public void updateApplicationServices(String appName, List<String> services) {
		cc.updateApplicationServices(appName, services);
	}

	public void updateApplicationStaging(String appName, Staging staging) {
		cc.updateApplicationStaging(appName, staging);
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		cc.updateApplicationUris(appName, uris);
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		cc.updateApplicationEnv(appName, env);
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		cc.updateApplicationEnv(appName, env);
	}

	/**
	 * @deprecated use {@link #streamLogs(String, ApplicationLogListener)} or {@link #getRecentLogs(String)}
	 */
	public Map<String, String> getLogs(String appName) {
		return cc.getLogs(appName);
	}

	public StreamingLogToken streamLogs(String appName, ApplicationLogListener listener) {
	    return cc.streamLogs(appName, listener);
	}

	public List<ApplicationLog> getRecentLogs(String appName) {
		return cc.getRecentLogs(appName);
	}

	/**
	 * @deprecated use {@link #streamLogs(String, ApplicationLogListener)} or {@link #getRecentLogs(String)}
	 */
	public Map<String, String> getCrashLogs(String appName) {
		return cc.getCrashLogs(appName);
	}

	public String getStagingLogs(StartingInfo info, int offset) {
		return cc.getStagingLogs(info, offset);
	}

	public String getFile(String appName, int instanceIndex, String filePath) {
		return cc.getFile(appName, instanceIndex, filePath, 0, -1);
	}

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition) {
		Assert.isTrue(startPosition >= 0,
				startPosition + " is not a valid value for start position, it should be 0 or greater.");
		return cc.getFile(appName, instanceIndex, filePath, startPosition, -1);
	}

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {
		Assert.isTrue(startPosition >= 0,
				startPosition + " is not a valid value for start position, it should be 0 or greater.");
		Assert.isTrue(endPosition > startPosition,
				endPosition + " is not a valid value for end position, it should be greater than startPosition " +
						"which is " + startPosition + ".");
		return cc.getFile(appName, instanceIndex, filePath, startPosition, endPosition - 1);
	}

	public void openFile(String appName, int instanceIndex, String filePath, ClientHttpResponseCallback callback) {
		cc.openFile(appName, instanceIndex, filePath, callback);
	}

	public String getFileTail(String appName, int instanceIndex, String filePath, int length) {
		Assert.isTrue(length > 0, length + " is not a valid value for length, it should be 1 or greater.");
		return cc.getFile(appName, instanceIndex, filePath, -1, length);
	}

	// list services, un/provision services, modify instance

	public List<CloudService> getServices() {
		return cc.getServices();
	}

	public List<CloudServiceBroker> getServiceBrokers() {
		return cc.getServiceBrokers();
	}

	public CloudServiceBroker getServiceBroker(String name) {
		return cc.getServiceBroker(name);
	}

	public void createServiceBroker(CloudServiceBroker serviceBroker) {
		cc.createServiceBroker(serviceBroker);
	}

	public void updateServiceBroker(CloudServiceBroker serviceBroker) {
		cc.updateServiceBroker(serviceBroker);
	}

	@Override
	public void deleteServiceBroker(String name) {
		cc.deleteServiceBroker(name);
	}

	@Override
	public void updateServicePlanVisibilityForBroker(String name, boolean visibility) {
		cc.updateServicePlanVisibilityForBroker(name, visibility);
	}

    public CloudService getService(String service) {
		return cc.getService(service);
	}

	public void deleteService(String service) {
		cc.deleteService(service);
	}

	public List<CloudServiceOffering> getServiceOfferings() {
		return cc.getServiceOfferings();
	}

	public void bindService(String appName, String serviceName) {
		cc.bindService(appName, serviceName);
	}

	public void unbindService(String appName, String serviceName) {
		cc.unbindService(appName, serviceName);
	}

	public InstancesInfo getApplicationInstances(String appName) {
		return cc.getApplicationInstances(appName);
	}

	public InstancesInfo getApplicationInstances(CloudApplication app) {
		return cc.getApplicationInstances(app);
	}

	public CrashesInfo getCrashes(String appName) {
		return cc.getCrashes(appName);
	}

	public List<CloudStack> getStacks() {
		return cc.getStacks();
	}

	public CloudStack getStack(String name) {
		return cc.getStack(name);
	}

	public void rename(String appName, String newName) {
		cc.rename(appName, newName);
	}

	public List<CloudDomain> getDomainsForOrg() {
		return cc.getDomainsForOrg();
	}

	public List<CloudDomain> getPrivateDomains() {
		return cc.getPrivateDomains();
	}

	public List<CloudDomain> getSharedDomains() {
		return cc.getSharedDomains();
	}

	public List<CloudDomain> getDomains() {
		return cc.getDomains();
	}

	public CloudDomain getDefaultDomain() {
		return cc.getDefaultDomain();
	}

	public void addDomain(String domainName) {
		cc.addDomain(domainName);
	}

	public void deleteDomain(String domainName) {
		cc.deleteDomain(domainName);
	}

	public void removeDomain(String domainName) {
		cc.removeDomain(domainName);
	}

	public List<CloudRoute> getRoutes(String domainName) {
		return cc.getRoutes(domainName);
	}

	public void addRoute(String host, String domainName) {
		cc.addRoute(host, domainName);
	}

	public void deleteRoute(String host, String domainName) {
		cc.deleteRoute(host, domainName);
	}

	public void registerRestLogListener(RestLogCallback callBack) {
		cc.registerRestLogListener(callBack);
	}

	public void unRegisterRestLogListener(RestLogCallback callBack) {
		cc.unRegisterRestLogListener(callBack);
	}
	
	public CloudOrganization getOrgByName(String orgName, boolean required){
    	return cc.getOrgByName(orgName, required);
    }

	public List<CloudQuota> getQuotas() {
		return cc.getQuotas();
	}

	public CloudQuota getQuotaByName(String quotaName, boolean required) {
		return cc.getQuotaByName(quotaName, required);
	}

	public void setQuotaToOrg(String orgName, String quotaName) {
		cc.setQuotaToOrg(orgName, quotaName);
	}

	public void createQuota(CloudQuota quota) {
		cc.createQuota(quota);
	}

	public void deleteQuota(String quotaName) {
		cc.deleteQuota(quotaName);
	}

	public void updateQuota(CloudQuota quota, String name) {
		cc.updateQuota(quota, name);
	}
	@Override
	public void createSpace(String spaceName) {
		cc.createSpace(spaceName);
	}

	@Override
	public void deleteSpace(String spaceName) {
		cc.deleteSpace(spaceName);
	}

	
	@Override
	public CloudSpace getSpace(String spaceName) {
		return cc.getSpace(spaceName);
	}
}
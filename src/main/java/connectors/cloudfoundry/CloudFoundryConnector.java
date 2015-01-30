package connectors.cloudfoundry;

import connectors.cloudfoundry.db.SQLScripts;
import connectors.cloudfoundry.libadapter.CustomCloudFoundryClient;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author 
 *
 */
public class CloudFoundryConnector
{

	
	// CF client
	private CloudFoundryClient _cfclient;
	// default values
	private final int DEFAULT_MEMORY = 512; 		// MB
	// used by lib-adapter classes in order to get all the application environment values
	private String _APIEndPoint;
	private String _login;
	private String _passwd;
	private boolean _trustSelfSignedCerts;
	// logger
	private static final Logger logAdapter = Logger.getLogger(CloudFoundryConnector.class.getName());
	
	
	/**
	 * 
	 * @param APIEndPoint
	 * @param login
	 * @param passwd
	 * @param trustSelfSignedCerts
	 */
	public CloudFoundryConnector(String APIEndPoint, String login, String passwd, boolean trustSelfSignedCerts) 
	{
		this(APIEndPoint, login, passwd, "", "", trustSelfSignedCerts);
	}
	
	
	/**
	 * 
	 * @param APIEndPoint
	 * @param login
	 * @param passwd
	 * @param organization
	 * @param space
	 */
	public CloudFoundryConnector(String APIEndPoint, String login, String passwd, String organization, String space) 
	{
		this(APIEndPoint, login, passwd, organization, space, false);
	}
	
	
	/**
	 * 
	 * @param APIEndPoint
	 * @param login
	 * @param passwd
	 * @param organization
	 * @param space
	 * @param trustSelfSignedCerts
	 */
	public CloudFoundryConnector(String APIEndPoint, String login, String passwd, String organization, 
					String space, boolean trustSelfSignedCerts) 
	{
		_APIEndPoint = APIEndPoint;
		_login = login;
		_passwd = passwd;
		_trustSelfSignedCerts = true;

		logAdapter.log(Level.INFO, ">> Connecting to CloudFoundry [" + APIEndPoint + "] ...");
		try {
			if ((organization != null && !organization.isEmpty()) && (space != null && !space.isEmpty())) {
				_cfclient = new CloudFoundryClient(new CloudCredentials(login, passwd), getTargetURL(APIEndPoint), 
												   organization, space, trustSelfSignedCerts);
			}
			else {
				_cfclient = new CloudFoundryClient(new CloudCredentials(login, passwd), getTargetURL(APIEndPoint), trustSelfSignedCerts);
			}
			
			_cfclient.login();
			logAdapter.log(Level.INFO, ">> Connection established");
		}
		catch (CloudFoundryException ex) {
			logAdapter.log(Level.WARNING, ">> " + ex.getMessage());
			throw ex;
		}
	}

	
	/**
	 * Returns the cloud foundry client object
	 * @return
	 */
	public CloudFoundryClient getConnectedClient() 
	{
		return _cfclient;
	}
	
	
	/**
	 * DEPLOY an application in CF
	 * 	1. Create application
	 * 	2. Upload application
	 * 	3. Start application
	 * 
	 * -push command examples (CLI):
	 * 		cf push ehealthgui 	-p ./apps/WebGUI_v3.war 	-d 62.14.219.157.xip.io -b https://github.com/rsucasas/java-buildpack.git -m 512MB -i 1
	 * 		cf push softcare-ws -p ./apps/softcare-ws.war 	-b https://github.com/rsucasas/java-buildpack.git -m 512MB -i 1
	 *
	 * @param applicationName Application name
	 * @param domainName 
	 * @param warFile 
	 * @param buildpackUrl For example: https://github.com/rsucasas/java-buildpack.git
	 * @return
	 */
	public boolean deployApp(String applicationName, String domainName, String warFile, String buildpackUrl)
	{
		// 1. Create application
		CloudApplication app = createApplication(applicationName, domainName, buildpackUrl);
		
		// 2. Upload application
		if ((app != null) && (uploadApplication(app, warFile))) {
			// 3. Start application
			logAdapter.log(Level.INFO, ">> Starting application ... ");
			_cfclient.startApplication(app.getName());
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 
	 * @param applicationName
	 * @param warFile
	 * @param buildpackUrl
	 */
	public boolean deployApp(String applicationName, String warFile, String buildpackUrl)
	{
		return deployApp(applicationName, "", warFile, buildpackUrl);
	}
	
	
	/**
	 * DEPLOY an application with database (only binding) in CF
	 * 
	 * 	1. Create service
	 * 	2. Create application
	 * 	3. bind to service
	 * 	4. get database parameters and setup application environment variables
	 *  5. Upload application
	 *  6. Start application
	 * 
	 * @param applicationName
	 * @param domainName
	 * @param warFile
	 * @param buildpackUrl
	 * @param serviceOffered
	 * @param serviceName
	 * @param servicePlan
	 * @param freePlan
	 */
	public void deployAppWithDatabase(String applicationName, String domainName, String warFile, String buildpackUrl, 
									String serviceOffered, String serviceName, String servicePlan, boolean freePlan) 
	{
		// 1. Create service
	    // http://docs.connectors.cloudfoundry.org/devguide/services/managing-services.html
		CloudService cs = createService(serviceOffered, serviceName, servicePlan, freePlan);
		
		// 2. Create application
		CloudApplication app = createApplication(applicationName, domainName, buildpackUrl);
		
		if (app != null) {
			if (cs != null) {
				// 3. bind to service
				logAdapter.log(Level.INFO, ">> Binding application to service [" + serviceName + "] ... ");
				_cfclient.bindService(app.getName(), serviceName);
				
				// 4. get database parameters and setup application envionment variables
				setupEnvironment(app, serviceName, servicePlan, serviceOffered);
			}
			
			// 5. Upload application
			if (uploadApplication(app, warFile)) {
				// 6. Start application
				logAdapter.log(Level.INFO, ">> Starting application ... ");
				_cfclient.startApplication(app.getName());
			}
		}
	}
	
	
	/**
	 * 
	 * @param applicationName
	 * @param warFile
	 * @param buildpackUrl
	 * @param serviceOffered
	 * @param serviceName
	 * @param servicePlan
	 * @param freePlan
	 */
	public void deployAppWithDatabase(String applicationName, String warFile, String buildpackUrl, 
			String serviceOffered, String serviceName, String servicePlan, boolean freePlan) 
	{
		deployAppWithDatabase(applicationName, "", warFile, buildpackUrl, serviceOffered, serviceName, servicePlan, freePlan);
	}
	
	
	/**
	 * DEPLOY an application with database in CF and also creates the database schema and tables (executes a sql script)
	 * 
	 * 	1. DEPLOY an application with database (only binding) in CF
	 *  2. Execute sql script
	 *  
	 * @param applicationName
	 * @param domainName
	 * @param warFile
     * @param buildpackUrl
     * @param serviceOffered
     * @param serviceName
     * @param servicePlan
	 * @param freePlan
	 * @param sqlFilePath
	 * @param dbType
	 */
	public void deployAppWithDatabasePopulate(String applicationName, String domainName, String warFile, String buildpackUrl, 
			String serviceOffered, String serviceName, String servicePlan, boolean freePlan, String sqlFilePath, SQLScripts.SQL_TYPE dbType) 
	{
		// 1. DEPLOY an application with database (only binding) in CF
		deployAppWithDatabase(applicationName, domainName, warFile, buildpackUrl, serviceOffered, serviceName, servicePlan, freePlan);
		
		// 2. Execute sql script
		DataBaseParameters dbparams = getDBEnvValues(applicationName, serviceOffered);
		SQLScripts sqls = new SQLScripts();
		//sqls.createDB(dbparams.getUri(), "", "", sqlFilePath, dbType);
		//sqls.createDB(dbparams.getJdbcUrl(), dbparams.getUsername(), dbparams.getPassword(), sqlFilePath, dbType);
		sqls.createDB(dbparams.getUrl_conn(), dbparams.getUsername(), dbparams.getPassword(), sqlFilePath, dbType);
	}
	
	
	/**
	 * Deletes an application and the services binded to this application
	 * @param applicationName
	 */
	public void deleteApp(String applicationName) 
	{
		CloudApplication app = null;
		
		try {
			app = _cfclient.getApplication(applicationName);
		}
		catch (CloudFoundryException ex) {
			logAdapter.log(Level.WARNING, ">> [" + applicationName + "] not found ");
		}

		if (app != null)
		{
			// 1. delete application
			logAdapter.log(Level.INFO, ">> Deleting application [" + applicationName + "] ... ");
			_cfclient.deleteApplication(applicationName);

			// 2. delete services if not attached to other applications
			List<String> lServices = app.getServices();								// services used by the deleted application

			if (lServices.size() > 0) {
				List<CloudApplication> lApplications = _cfclient.getApplications();	// other client applications
				
				for (String serviceName : lServices) {
					// if there are other applications, check if the services is being used by any of them
					if (lApplications.size() > 0) {
						for (CloudApplication capp : lApplications) {
							List<String> lServices2 = capp.getServices();
							
							if (!lServices2.contains(serviceName)) {
								logAdapter.log(Level.INFO, ">> Deleting service [" + serviceName + "] ... ");
								_cfclient.deleteService(serviceName);
							}
							else {
								logAdapter.log(Level.INFO, ">> Service [" + serviceName + "] is used by other applications ");
							}
						}
					}
					// if there are no more applications, then it's safe to delete the service
					else {
						logAdapter.log(Level.INFO, ">> Deleting service [" + serviceName + "] ... ");
						_cfclient.deleteService(serviceName);
					}
				}
			}
			logAdapter.log(Level.INFO, ">> [" + applicationName + "] deleted ");
		}
	}
	
	
	/**
	 * 
	 * @param applicationName
	 * @param domainName
	 * @param buildpackUrl
	 * @return
	 */
	private CloudApplication createApplication(String applicationName, String domainName, String buildpackUrl) 
	{
		try 
		{
			// initialize parameters ...
			// buildpack: -b https://github.com/connectors.cloudfoundry/java-buildpack.git
			Staging staging = null;
			if (buildpackUrl != null) {
				staging = new Staging(null, buildpackUrl);
			}
			else {
				staging = new Staging();
			}
			
			// uris
			List<String> uris = new ArrayList<String>();
		    uris.add(computeAppUrl(applicationName, domainName));
		    
		    // serviceNames
		    List<String> serviceNames = new ArrayList<String>();

		    // 1. Create application
		    logAdapter.log(Level.INFO, ">> Creating application ... ");
		    
			_cfclient.createApplication(applicationName, staging, DEFAULT_MEMORY, uris, serviceNames);
			CloudApplication app = _cfclient.getApplication(applicationName);
			
	        logAdapter.log(Level.INFO, ">> Application details: " + app.toString());
	        
	        return app;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param app
	 * @param warFile
	 * @return
	 */
	private boolean uploadApplication(CloudApplication app, String warFile) 
	{
		try {
			// 2. Upload application
            logAdapter.log(Level.INFO, ">> Uploading application from " + new File(warFile).getCanonicalPath() + " ... ");
            _cfclient.uploadApplication(app.getName(), new File(warFile).getCanonicalPath());
            return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param serviceOffered
	 * @param serviceName
	 * @param servicePlan
	 * @param freePlan
	 * @return
	 */
	private CloudService createService(String serviceOffered, String serviceName, String servicePlan, boolean freePlan) 
	{
		logAdapter.log(Level.INFO, ">> Looking for installed services ... ");
		CloudService cs = _cfclient.getService(serviceName);
		
		if ((cs != null) && (cs.getLabel().equalsIgnoreCase(serviceOffered))) 
		{
			logAdapter.log(Level.INFO, ">> Service already installed ... ");
			return cs;
		}
		
		logAdapter.log(Level.INFO, ">> Creating service [" + serviceOffered + ", " + servicePlan + "] ... ");
		
		try 
		{
			List<CloudServiceOffering> l = _cfclient.getServiceOfferings();
			
			for (CloudServiceOffering cservice : l)
			{
				if (cservice.getName().equalsIgnoreCase(serviceOffered))
				{
					// create service object
					CloudService newService = new CloudService();
					
					newService.setLabel(cservice.getName());
					newService.setName(serviceName);
					newService.setProvider(cservice.getProvider());
					newService.setVersion(cservice.getVersion());
					newService.setMeta(cservice.getMeta());
					newService.setPlan(null);

					List<CloudServicePlan> lPlans = cservice.getCloudServicePlans();
					for (CloudServicePlan plan : lPlans)
					{
						// select the first free plan
						if (freePlan) {
							if (plan.isFree()) 
							{
								newService.setPlan(plan.getName());
								break;
							}
						}
						// look for a plan with name = 'servicePlan'
						else if (plan.getName().equalsIgnoreCase(servicePlan)) {
							newService.setPlan(servicePlan);
							break;
						}
					}
					
					if (newService.getPlan() != null)
					{
						_cfclient.createService(newService);
						logAdapter.log(Level.INFO, ">> Service created ... ");
						return newService;
					}
					
					break;
				}
		    }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		logAdapter.log(Level.WARNING, ">> Service was not created");
		return null;
	}
	
	
	/**
	 * 
	 * @param app
	 * @param serviceName
	 * @param servicePlan
	 * @param serviceOfferedRealName
	 */
	private void setupEnvironment(CloudApplication app, String serviceName, String servicePlan, String serviceOfferedRealName) 
	{
		try 
		{
			logAdapter.log(Level.INFO, ">> Getting database connection parameters ... ");
			DataBaseParameters dbparams = getDBEnvValues(app.getName(), serviceOfferedRealName);
			
			logAdapter.log(Level.INFO, ">> Setting up application environment ... ");
			HashMap<String, String> mEnvValues = new HashMap<String, String>(13);
			
			mEnvValues.put("jdbcurl", dbparams.getJdbcUrl());
			mEnvValues.put("uri", dbparams.getUri());
			mEnvValues.put("name", dbparams.getName());
			mEnvValues.put("hostname", dbparams.getHostname());
			mEnvValues.put("port", dbparams.getPort());
			mEnvValues.put("username", dbparams.getUsername());
			mEnvValues.put("password", dbparams.getPassword());
			mEnvValues.put("max_conns", dbparams.getMax_conns());
			
			// create application database environment variables
			_cfclient.updateApplicationEnv(app.getName(), mEnvValues);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	private URL getTargetURL(String target) {
		try {
			return URI.create(target).toURL();
		} 
		catch (MalformedURLException e) {
			throw new RuntimeException("The target URL is not valid: " + e.getMessage());
		}
	}
	
	
	/**
	 * 
	 * @param appName
	 * @param domainName
	 * @return
	 */
	private String computeAppUrl(String appName, String domainName) 
	{
        return appName + "." + ((domainName.equals("") || domainName.equals(appName))? _cfclient.getDefaultDomain().getName() : domainName);
    }
	
	
	/**
	 * 
	 * @param applicationName
	 * @param serviceOfferedRealName
	 * @return
	 */
	private DataBaseParameters getDBEnvValues(String applicationName, String serviceOfferedRealName) 
	{
		DataBaseParameters res = new DataBaseParameters();
		
		try
		{
			CloudApplication app = _cfclient.getApplication(applicationName);
			String appId = app.getMeta().getGuid().toString();
			
			// get env values from CF - application --> use of libadapter
			CustomCloudFoundryClient cfclientTmp =
					new CustomCloudFoundryClient(new CloudCredentials(_login, _passwd), getTargetURL(_APIEndPoint), _trustSelfSignedCerts);
	        cfclientTmp.login();
	        String envVars = cfclientTmp.getEnvValuesAsString(appId);
	        cfclientTmp.logout();

	        // get object with database parameters
	        CloudFoundryEnvParser parser = new CloudFoundryEnvParser(envVars);
	        res = parser.parseDBEnvCredentialsValues(serviceOfferedRealName);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
        return res;
	}

	
}
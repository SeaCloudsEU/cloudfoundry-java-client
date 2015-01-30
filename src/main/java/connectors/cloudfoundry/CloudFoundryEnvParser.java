package connectors.cloudfoundry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * 
 * @author 
 *
 */
public class CloudFoundryEnvParser 
{
	
	
	private JsonObject _jobj;
	
	
	/**
	 * 
	 * @param str_json
	 */
	public CloudFoundryEnvParser(String str_json) 
	{
		_jobj = new Gson().fromJson(str_json, JsonObject.class);
	}
	
	
	/**
	 * 
	 * @param serviceOfferedRealName
	 * @return
	 */
	public DataBaseParameters parseDBEnvCredentialsValues(String serviceOfferedRealName)
	{
		if ("elephantsql".equalsIgnoreCase(serviceOfferedRealName)) 
		{
			return parseElephantSQLEnvCredentialsValues();
		}
		else if ("cleardb".equalsIgnoreCase(serviceOfferedRealName)) 
		{
			return parseClearDBEnvCredentialsValues();
		}
		
		return new DataBaseParameters();
	}
	
	/**
	 * 
	 *  
	 * {
     * 	"elephantsql": [{
     * 		"name": "myelephantsql",
     * 	    "label": "elephantsql",
     * 	    "tags": [
     * 	      	"New Product",
     * 	       	"Data Stores",
     * 	       	"relational",
     * 	       	"Data Store",
     * 	       	"postgresql"
     * 	    ],
     * 	    "plan": "turtle",
     * 	    "credentials": {
     * 	       	"uri": "postgres://zgeleggb:9KTwlMCtZaHil_dT0uYyVCHNMWhGK6I-@babar.elephantsql.com:5432/zgeleggb",
     * 	       	"max_conns": "5"
     * 	    }
     * 	   }
     * 	 ]
     * }
	 * 
	 * 
	 */
	private DataBaseParameters parseElephantSQLEnvCredentialsValues() 
	{
		String[] paths = {"system_env_json", "VCAP_SERVICES", "elephantsql", "credentials"};
		JsonObject resJsonObject = getJsonObjectValues(_jobj, paths, 0);
		
		DataBaseParameters res = new DataBaseParameters();
		
		if (resJsonObject != null) {
			res.setMax_conns(getValue(resJsonObject, "max_conns"));
			res.setUri(getValue(resJsonObject, "uri"));
		}
		
		return res;
	}
	
	
	/**
	 * 
	 * 
	 * {
	 * 	cleardb=[{
	 * 		name=mycleardb,
	 * 		label=cleardb,
	 * 		tags=[DataStores,
	 * 			relational,
	 * 			DataStore,
	 *	 		mysql],
	 * 		plan=spark,
	 * 		credentials={
	 * 			jdbcUrl=jdbc: mysql: //badab523ff0667: dcfd0c53@us-cdbr-iron-east-01.cleardb.net: 3306/ad_c2b3de816d30d24,
	 * 			uri=mysql: //badab523ff0667: dcfd0c53@us-cdbr-iron-east-01.cleardb.net: 3306/ad_c2b3de816d30d24?reconnect=true,
	 * 			name=ad_c2b3de816d30d24,
	 * 			hostname=us-cdbr-iron-east-01.cleardb.net,
	 * 			port=3306,
	 * 			username=badab523ff0667,
	 * 			password=dcfd0c53
	 * 		}
	 * 	}]
	 * }
	 * 
	 */
	private DataBaseParameters parseClearDBEnvCredentialsValues() 
	{
		String[] paths = {"system_env_json", "VCAP_SERVICES", "cleardb", "credentials"};
		JsonObject resJsonObject = getJsonObjectValues(_jobj, paths, 0);
		
		DataBaseParameters res = new DataBaseParameters();
		
		if (resJsonObject != null) {
			res.setJdbcUrl(getValue(resJsonObject, "jdbcUrl"));
			res.setUri(getValue(resJsonObject, "uri"));
			res.setName(getValue(resJsonObject, "name"));
			res.setHostname(getValue(resJsonObject, "hostname"));
			res.setPort(getValue(resJsonObject, "port"));
			res.setUsername(getValue(resJsonObject, "username"));
			res.setPassword(getValue(resJsonObject, "password"));
			
			String jdbcUrlParsed = res.parseUrlString(res.getJdbcUrl());
			res.setUrl_conn(jdbcUrlParsed);
		}
		
		return res;
	}

	
	/**
	 * 
	 * String[] paths = {"system_env_json", "VCAP_SERVICES", "cleardb", "credentials", "jdbcUrl"};
	 * 
	 * @param json
	 * @param path
	 * @param posPath
	 * @return
	 */
	private String getJsonValue(JsonObject json, String[] path, int posPath) {
		JsonElement elm = json.get(path[posPath]);
		JsonObject jobj2;
		
		if (posPath == path.length-1) {
			return elm.getAsString();
		}
		
		if (elm instanceof JsonArray) {
			jobj2 = elm.getAsJsonArray().get(0).getAsJsonObject();
		}
		else {
			jobj2 = elm.getAsJsonObject();
		}

		posPath++;
		return getJsonValue(jobj2, path, posPath);
	}
	
	
	/**
	 * 
	 * @param json
	 * @param path
	 * @param posPath
	 * @return
	 */
	private JsonObject getJsonObjectValues(JsonObject json, String[] path, int posPath) {
		try {
			JsonElement elm = json.get(path[posPath]);
			JsonObject jobj2;

			if (elm instanceof JsonArray) {
				jobj2 = elm.getAsJsonArray().get(0).getAsJsonObject();
			}
			else {
				jobj2 = elm.getAsJsonObject();
			}
			
			if (posPath == path.length-1) {
				return jobj2;
			}

			posPath++;
			return getJsonObjectValues(jobj2, path, posPath);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param json
	 * @param path
	 * @return
	 */
	private String getValue(JsonObject json, String path) {
		JsonElement elm_jdbcUrl = json.get(path);
		if (elm_jdbcUrl != null)
		{
			return elm_jdbcUrl.getAsString();
		}
		return "";
	}
	
	
}

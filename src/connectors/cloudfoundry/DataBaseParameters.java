package connectors.cloudfoundry;


/**
 * 
 * @author a572832
 *
 */
public class DataBaseParameters 
{

	
	private String jdbcUrl;
	private String uri;
	private String name;
	private String hostname;
	private String port;
	private String username;
	private String password;
	private String max_conns;
	
	
	/**
	 * 
	 */
	public DataBaseParameters() { 
		this.jdbcUrl = "";
		this.uri = "";
		this.name = "";
		this.hostname = "";
		this.port = "";
		this.username = "";
		this.password = "";
		this.max_conns = "";
	}
	
	
	/**
	 * 
	 * @param jdbcUrl
	 * @param uri
	 * @param name
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @param max_conns
	 */
	public DataBaseParameters(String jdbcUrl, String uri, String name,
			String hostname, String port, String username, String password, String max_conns) {
		super();
		
		this.jdbcUrl = jdbcUrl;
		this.uri = uri;
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.max_conns = max_conns;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	
	
	/**
	 * 
	 * @param jdbcUrl
	 */
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getUri() {
		return uri;
	}
	
	
	/**
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getHostname() {
		return hostname;
	}
	
	
	/**
	 * 
	 * @param hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getPort() {
		return port;
	}
	
	
	/**
	 * 
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	
	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * 
	 * @return
	 */
	public String getMax_conns() {
		return max_conns;
	}


	/**
	 * 
	 * @param max_conns
	 */
	public void setMax_conns(String max_conns) {
		this.max_conns = max_conns;
	}
	
	
}

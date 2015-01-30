package connectors.cloudfoundry.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author a572832
 *
 */
public class SQLScripts 
{


	/**
	 * 
	 * @author a572832
	 *
	 */
	public enum SQL_TYPE {
		MySQL,
		Other
	}
	
	// logger
	private static final Logger logAdapter = Logger.getLogger(SQLScripts.class.getName());
	
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @param sqlFilePath
	 * @param dbType
	 * @return
	 */
	public boolean createDB(String url, String user, String password, String sqlFilePath, SQLScripts.SQL_TYPE dbType)
	{
		if (dbType.equals(SQL_TYPE.MySQL))
		{
			logAdapter.log(Level.INFO, ">> Executing MySQL script ... ");
			return createDB_MySQ(url, user, password, sqlFilePath);
		}
		else 
		{
			logAdapter.log(Level.WARNING, ">> Script not supported ");
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @param sqlFile
	 * @return
	 */
	private boolean createDB_MySQ(String url, String user, String password, String sqlFilePath)
	{
		Connection conn = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			
			boolean booleanAutoCommit = false;
			boolean booleanStopOnerror = true;
			JDBCScriptRunner runner = new JDBCScriptRunner(conn, booleanAutoCommit, booleanStopOnerror);
			
			logAdapter.log(Level.INFO, ">> Executing script [" + sqlFilePath + "] ...");
			runner.runScript(new BufferedReader(new FileReader(sqlFilePath)));
			
			conn.close();
			
			logAdapter.log(Level.INFO, ">> ...");
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return false;
		}
		finally {
			try
			{
				conn.close();
			}
			catch (SQLException e) {}
		}
	}
	
	
}

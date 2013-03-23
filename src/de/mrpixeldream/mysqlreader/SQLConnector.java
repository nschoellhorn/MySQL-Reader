package de.mrpixeldream.mysqlreader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnector
{
	private boolean		isInitialized;
	private boolean		isConnected;
	private boolean 	isFailed;

	private String		server;
	private String		user;
	private String		password;

	private Connection	con;
	private Statement	stmt;

	public SQLConnector()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		
		isInitialized = false;
		isConnected = false;
		isFailed = false;
	}

	public SQLConnector(String server, String user, String password, String database, int port)
	{
		isInitialized = true;
		isConnected = false;
		
		this.user = user;
		this.password = password;

		if (!server.startsWith("jdbc:mysql://"))
		{
			server = ( "jdbc:mysql://" + server + ":" + port + "/" + database);
		}
		
		this.server = server;

		try
		{
			con = DriverManager.getConnection(server, user, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			if (e.getMessage().contains("denied"))
			{
				System.err.println("[MySQL-Reader] CAN'T CONNECT! WRONG USER AND/OR PASSWORD!");
				isFailed = true;
				return;
			}
			
			return;
		}
		
		try
		{
			stmt = con.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.err.println("[MySQL-Reader] AN UNKNOWN ERROR OCCURRED!");
			isFailed = true;
			return;
		}
		
		isFailed = false;
		isConnected = true;
		return;
	}
	
	public boolean connect()
	{
		if (isConnected || isFailed || !isInitialized)
		{
			return false;
		}
		else
		{
			try
			{
				con = DriverManager.getConnection(server, user, password);
				isConnected = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage().contains("denied"))
				{
					System.err.println("[MySQL-Reader] CAN'T CONNECT! WRONG USER AND/OR PASSWORD!");
					isFailed = true;
					return false;
				}
				if (e.getMessage().contains("refused"))
				{
					System.err.println("[MySQL-Reader] CAN'T CONNECT! SERVER NOT AVAILABLE!");
					isFailed = true;
					return false;
				}
				
				return false;
			}
			
			return true;
		}
	}
	
	public boolean disconnect()
	{
		if (!isConnected || isFailed || !isInitialized)
		{
			return false;
		}
		else
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				System.err.println("[MySQL-Reader] CAN'T CLOSE CONNECTION!");
				e.printStackTrace();
				return false;
			}
			con = null;
			
			return true;
		}
	}
	
	public void init(String server, String user, String password, String database, int port)
	{
		isInitialized = true;
		isConnected = false;
		
		this.user = user;
		this.password = password;

		if (!server.startsWith("jdbc:mysql://"))
		{
			server = ( "jdbc:mysql://" + server + ":" + port + "/" + database);
		}
		
		this.server = server;
	}
	
	public ResultSet query(String sql_query) throws SQLException
	{
		if (!isInitialized || isFailed || !isConnected)
		{
			System.err.println("[MySQL-Reader] NO CONNECTION FOUND!");
			System.err.println("[MySQL-Reader] Fail: " + isFailed);
			System.err.println("[MySQL-Reader] Connection: " + isConnected);
			System.err.println("[MySQL-Reader] Init: " + isInitialized);
			return null;
		}
		
		try
		{
			stmt = con.createStatement();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		
		try
		{
			if (sql_query.startsWith("DELETE"))
			{
				this.stmt.executeUpdate(sql_query);
				return null;
			}
			return this.stmt.executeQuery(sql_query);
		}
		catch (SQLException e)
		{
			System.err.println("[MySQL-Reader] CAN'T EXECUTE QUERY! ERROR:");
			e.printStackTrace();
			return null;
		}
	}

	public void close()
	{
		try
		{
			this.con.close();
		}
		catch (Exception e)
		{
			System.err.println("[MySQL-Reader] CAN'T CLOSE CONNECTION!");
		}
	}
}
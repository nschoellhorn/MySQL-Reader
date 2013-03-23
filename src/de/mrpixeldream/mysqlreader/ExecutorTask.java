package de.mrpixeldream.mysqlreader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.ConsoleCommandSender;

public class ExecutorTask extends Thread
{
	private SQLConnector		connector;
	private MySQLReader				plugin;
	
	private ResultSet 			result = null;

	private final String	SERVER		= "Config.server";
	private final String	TABLE		= "Config.table";
	private final String	DATABASE	= "Config.database";
	private final String	USER		= "Config.user";
	private final String	PASSWORD	= "Config.password";
	private final String	COLUMN		= "Config.column_name";
	private final String	PORT		= "Config.port";
	private final String	SINGLEUSE	= "Config.singleuse";
	
	private String server;
	private String user;
	private String db;
	private String pass;
	private String table;
	
	private ConsoleCommandSender op;
	
	private int run;

	public ExecutorTask(MySQLReader plugin)
	{
		this.plugin = plugin;

		this.plugin.reloadConfig();
		
		run = 0;
		
		connector = new SQLConnector();
		
		op = this.plugin.getServer().getConsoleSender();
	}

	@Override
	public void run()
	{
		run++;
		
		plugin.reloadConfig();
		
		db = plugin.getConfig().getString(DATABASE);
		server = plugin.getConfig().getString(SERVER);
		user = plugin.getConfig().getString(USER);
		pass = plugin.getConfig().getString(PASSWORD);
		table = plugin.getConfig().getString(TABLE);
		
		System.out.println("[MySQL-Reader] Running the task! Execution number: " + run);
		
		connector.init(server, user, pass, db, plugin.getConfig().getInt(PORT));
		
		connector.connect();
		try
		{
			if ((result = connector.query("SELECT * FROM " + table + ";")) == null)
			{
				System.err
						.println("[MySQL-Reader] FEHLER IN DER MYSQL-KLASSE!");
				return;
			}
		}
		catch (SQLException e1)
		{
			System.err.println("[MySQL-Reader] QUERY FAILED:");
			e1.printStackTrace();
		}
		
		try
		{
			while (result.next())
			{
				this.plugin.getServer().dispatchCommand(op, result.getString(plugin.getConfig().getString(COLUMN)));
			}

			if (plugin.getConfig().getBoolean(SINGLEUSE))
			{
				connector.query("DELETE FROM " + table + ";"); // Should work now
			}
		}
		catch (SQLException e)
		{
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		finally
		{
			connector.close();
		}
	}
}
package de.mrpixeldream.mysqlreader;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQLReader extends JavaPlugin
{
	// Defining config-paths
	private final String	SERVER		= "Config.server";
	private final String	TABLE		= "Config.table";
	private final String	DATABASE	= "Config.database";
	private final String	USER		= "Config.user";
	private final String	PASSWORD	= "Config.password";
	private final String	INTERVAL	= "Config.interval";
	private final String	COLUMN		= "Config.column_name";
	private final String	PORT		= "Config.port";
	private final String	SINGLEUSE	= "Config.singleuse";

	// Defining plugin prefix
	private final String	PREFIX		= "[MySQL-Reader] ";

	@Override
	public void onEnable()
	{
		// Call the loadConfig() function from file bottom to load the config
		loadConfig();

		// Printing some messages for admins information
		System.out.println(PREFIX + "Loading MySQL-Reader...");
		System.out.println(PREFIX + "Config loaded! Starting task...");

		// Creating task object
		ExecutorTask task = new ExecutorTask(this);

		// Starting task in Bukkit scheduler
		long ticks = this.getConfig().getInt(INTERVAL) * 60 * 20;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, 0L, ticks);

		// Printing one more message
		System.out.println(PREFIX + "Successfully startet task!");
	}

	@Override
	public void onDisable()
	{
		// Printing a message for information
		System.out.println(PREFIX + "Successfully disabled the MySQL-Reader!");
	}

	private void loadConfig()
	{
		// Check the config files existence
		boolean configFileExistant = false;

		if (new File("plugins/MySQL-Reader/").exists())
		{
			configFileExistant = true;
		}

		// Allowing to set default values
		this.getConfig().options().copyDefaults(true);

		// Adding default values
		if (!configFileExistant)
		{
			this.getConfig().addDefault(SERVER, "localhost");
			this.getConfig().addDefault(TABLE, "bukkit");
			this.getConfig().addDefault(DATABASE, "bukkit");
			this.getConfig().addDefault(USER, "root");
			this.getConfig().addDefault(PASSWORD, "password");
			this.getConfig().addDefault(INTERVAL, 5);
			this.getConfig().addDefault(COLUMN, "commands");
			this.getConfig().addDefault(PORT, 3306);
			this.getConfig().addDefault(SINGLEUSE, false);
		}

		// Saving and reloading config from file
		this.saveConfig();
		this.reloadConfig();
	}
}
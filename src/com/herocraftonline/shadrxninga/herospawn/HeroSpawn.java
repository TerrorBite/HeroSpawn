package com.herocraftonline.shadrxninga.herospawn;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class HeroSpawn extends JavaPlugin {
	static String mainDirectory = "plugins/HeroSpawn";
	File file = new File( mainDirectory + File.separator + "spawnlocations.yml" );
	// Set up Listener Classes
	private final HeroSpawnPlayerListener playerListener = new HeroSpawnPlayerListener(
			this );

	public static final Logger log = Logger.getLogger( "Minecraft" );
	// Permissions Vairiables
	public static PermissionHandler Permissions;
	public boolean UsePermissions;

	@Override
	public void onDisable () {
		log.info( "[HeroSpawn] Version 0.2 Disabled" );
	}

	@Override
	public void onEnable () {
		setupPermissions();
		new File( mainDirectory ).mkdir();
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			} catch ( Exception ex ) {
				ex.printStackTrace();
			}
		} else {
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvent( Event.Type.PLAYER_LOGIN, playerListener,
					Event.Priority.Highest, this );
			pm.registerEvent( Event.Type.PLAYER_JOIN, playerListener,
					Event.Priority.Highest, this );
			log.info( "[HeroSpawn] Version 0.2 Enabled" );
		}
	}

	public void write ( String root, Object x ) {
		Configuration config = load();
		config.setProperty( root, x );
		config.save();
	}

	public String read ( String root ) {
		Configuration config = load();
		return config.getString( root );
	}

	public Configuration load () {

		try {
			Configuration config = new Configuration( file );
			config.load();
			return config;

		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	private void setupPermissions () {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin( "Permissions" );
		if ( HeroSpawn.Permissions == null ) {
			if ( test != null ) {
				UsePermissions = true;
				HeroSpawn.Permissions = ( (Permissions) test ).getHandler();
				System.out.println( "[HeroSpawn] Permissions system detected!" );
			} else {
				log.info( "Permission system not detected, defaulting to OP" );
				UsePermissions = false;
			}
		}
	}

	public boolean canSetSpawn ( Player p ) {
		if ( UsePermissions ) {
			return HeroSpawn.Permissions.has( p, "herospawn.set" );
		}
		return p.isOp();
	}

	public boolean canTP ( Player p ) {
		if ( UsePermissions ) {
			return HeroSpawn.Permissions.has( p, "herospawn.tp" );
		}
		return p.isOp();
	}

	public boolean onCommand ( CommandSender sender, Command command,
			String commandLabel, String[] args ) {

		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( ChatColor.RED
					+ "This command has to be called by a player!" );
			return true;
		}
		Player player = (Player) sender;
		if ( command.getName().equalsIgnoreCase( "herospawn" ) ) {
			if ( args.length == 0 ) {
				if ( !canTP( player ) ) {
					player.sendMessage( ChatColor.RED
							+ "You don't have permission to do that!" );
					return true;
				}
				double z = Double.parseDouble( read( player.getWorld()
						.getName() + ".Z" ) );
				double x = Double.parseDouble( read( player.getWorld()
						.getName() + ".X" ) );
				double y = Double.parseDouble( read( player.getWorld()
						.getName() + ".Y" ) );
				Location loc = new Location( player.getWorld(), x, y, z );
				player.teleport( loc );
				player.sendMessage( "¤f[¤9HeroSpawn¤f]¤e Welcome to First Spawn" );
				return true;

			}
			if ( args[ 0 ].equalsIgnoreCase( "set" ) ) {
				if ( !canSetSpawn( player ) ) {
					player.sendMessage( ChatColor.RED
							+ "You don't have permission to do that!" );
					return true;
				}
				String x1 = Double.toString( player.getLocation().getX() );
				String y1 = Double.toString( player.getLocation().getY() );
				String z1 = Double.toString( player.getLocation().getZ() );
				write( player.getWorld().getName() + ".X", x1 );
				write( player.getWorld().getName() + ".Y", y1 );
				write( player.getWorld().getName() + ".Z", z1 );
				player.sendMessage( ChatColor.BLUE + "¤f[¤9HeroSpawn¤f] "
						+ ChatColor.YELLOW
						+ "Players will now spawn here on their first login" );

			} else {
				return false;
			}
			return true;
		}
		return false;
	}
}

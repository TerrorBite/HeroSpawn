package com.herocraftonline.shadrxninga.herospawn;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HeroSpawn extends JavaPlugin {
	public static final String PERM_TP = "herospawn.tp";
	public static final String PERM_SET = "herospawn.set";
	static String mainDirectory = "plugins/HeroSpawn";
	File file = new File( mainDirectory + File.separator + "spawnlocations.yml" );
	public static final Logger log = Logger.getLogger( "Minecraft" );
	// Permissions Vairiables
	public Permission permission = null;
	private String group;
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
			group = read ("group");
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents( new HeroSpawnPlayerListener( this ), this );
			log.info( "[HeroSpawn] Version 0.3 Enabled" );
		}
	}

	public void write ( String root, Object x ) {
		try {
			this.getConfig().load( file );
			this.getConfig().set( root, x );
			this.getConfig().save( file );
		} catch ( Exception e ) {
			// TODO Handle this better?
			e.printStackTrace();
		} 
	}

	public String read ( String root ) {
		try {
			this.getConfig().load( file );
		} catch ( Exception e ) {
			// TODO Handle this A LOT better
			e.printStackTrace();
		} 
		return this.getConfig().getString( root );
	}

	private void setupPermissions () {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            UsePermissions = true;
            System.out.println( "[HeroSpawn] Permissions system detected!" );
        } else {
        	UsePermissions = false;
        	log.info( "Permission system not detected, defaulting to OP" );
        }
	}

	public boolean canSetSpawn ( Player p ) {
		if ( UsePermissions ) {
			return permission.has( p, PERM_SET );
		}
		return p.isOp();
	}

	public boolean canTP ( Player p ) {
		if ( UsePermissions ) {
			return permission.has( p, PERM_TP );
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

	public String getGroup () {
		return group;
	}
}

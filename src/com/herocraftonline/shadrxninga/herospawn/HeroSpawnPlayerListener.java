package com.herocraftonline.shadrxninga.herospawn;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

// TODO actually add the @ListenerEvent annotations

public class HeroSpawnPlayerListener implements Listener {
	public static final Logger log = Logger.getLogger( "Minecraft" );
	HashMap<String, Integer> login = new HashMap<String, Integer>();
	private HeroSpawn plugin;

	public HeroSpawnPlayerListener ( HeroSpawn plugin ) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLogin ( PlayerLoginEvent e ) {
		Player p = e.getPlayer();
		String name = p.getName();
		File file = new File( p.getWorld().getName() + "/players/" + name
				+ ".dat" );
		boolean exists = file.exists();
		if ( !exists ) {
			login.put( name, 1 );
			System.out
					.println( "[HeroSpawn] "
							+ name
							+ ": logged in for first time. Teleporting them to First Spawn" );

		} else {

			if ( login.containsKey( name ) )
				login.remove( name );
		}
	}

	@EventHandler
	public void onPlayerJoin ( PlayerJoinEvent e ) {
		Player p = e.getPlayer();
		String name = p.getName();
		if ( login.containsKey( name ) ) {
			double z = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".Z" ) );
			double x = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".X" ) );
			double y = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".Y" ) );
			Location loc = new Location( p.getWorld(), x, y, z );
			p.teleport( loc );
		} else {
		}
	}

	@EventHandler
	public void onPlayerRespawn ( PlayerRespawnEvent e ) {
		Player p = e.getPlayer();
		System.out.println("\"" + plugin.permission.getPrimaryGroup( p ) + "\"");
		
		if ( plugin.permission != null && plugin.getGroup() != null && 
				plugin.permission.getPrimaryGroup( p ).equalsIgnoreCase( plugin.getGroup() ) ) {
			double z = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".Z" ) );
			double x = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".X" ) );
			double y = Double.parseDouble( plugin.read( p.getWorld().getName()
					+ ".Y" ) );
			Location loc = new Location( p.getWorld(), x, y, z );
			e.setRespawnLocation( loc );
		}
	}
}

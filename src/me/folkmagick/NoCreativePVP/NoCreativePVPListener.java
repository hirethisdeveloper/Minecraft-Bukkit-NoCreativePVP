package me.folkmagick.NoCreativePVP;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sk89q.worldedit.Vector;

import org.bukkit.GameMode;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class NoCreativePVPListener implements Listener {

	public static NoCreativePVP plugin;

	WorldGuardPlugin worldGuard = NoCreativePVP.getWorldGuard();
	public static final List<String> vipGroups = Arrays.asList("vip", "vip5",
			"moderator", "admins", "owner");

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		World world = event.getPlayer().getWorld();

		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		RegionManager regionManager = worldGuard.getRegionManager(world);
		Vector pt = new Vector(event.getTo().getBlockX(), event.getTo()
				.getBlockY(), event.getTo().getBlockZ());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

		boolean inPVP = set.allows(DefaultFlag.PVP, localPlayer);
		boolean hasVIP = NoCreativePVP.permission.playerInGroup(player, "VIP");
		//boolean canPVP = NoCreativePVP.checkPermission(player,
		//		"nocreativepvp.nofight");

		// nocreativepvp.nofight == false -- means person can override and be in
		// creative in pvp area

		if (player.getGameMode() == GameMode.CREATIVE && inPVP && hasVIP ) {

			player.setGameMode(GameMode.SURVIVAL);
			NoCreativePVP.msg(player, "- You've entered a PVP Warzone.");

		}
		// else if ( player.getGameMode() == GameMode.SURVIVAL && !inPVP &&
		// hasVIP ) {
		//
		// player.setGameMode(GameMode.CREATIVE);
		//
		// }

	}

	// ============================================================================
	// BLOCK COMMAND SWITCHING TO CREATIVE WHEN IN PVP AREAS
	//
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();
		World world = event.getPlayer().getWorld();
		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		RegionManager regionManager = worldGuard.getRegionManager(world);

		Vector pt = new Vector(player.getLocation().getX(), player
				.getLocation().getY(), player.getLocation().getZ());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

		boolean inPVP = set.allows(DefaultFlag.PVP, localPlayer);
		boolean hasVIP = NoCreativePVP.permission.playerInGroup(player, "VIP");

		String event_s = event.getMessage().toString();

		if (
				(
						event_s.equalsIgnoreCase("/gm") || 
						event_s.equalsIgnoreCase("/creative")
				) 
				&& inPVP 
				&& hasVIP 
			) {

			NoCreativePVP.msg(player, "Sorry you are in a PVP Warzone. Creative is disabled here.");
			event.setCancelled(true);

		}
	}

}

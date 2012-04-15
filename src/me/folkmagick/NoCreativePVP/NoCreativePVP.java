package me.folkmagick.NoCreativePVP;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class NoCreativePVP extends JavaPlugin implements Listener {

	public static NoCreativePVP plugin;
	public static final Logger log = Logger.getLogger("Minecraft");

	public static Permission permission = null;

	public void onEnable() {
		if (setupPermission()) {

			PluginManager pm = this.getServer().getPluginManager();
			pm.registerEvents(new NoCreativePVPListener(), this);

			this.logMessage("is now enabled!");
		}
	}

	public void onDisable() {
		this.logMessage("is now disabled!");
	}

	

	static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager()
				.getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	// ========================================================================
	// HELPERS

	public static boolean checkPermission(CommandSender sender,
			String permission) {
		if (NoCreativePVP.permission != null) { // use Vault to check
			// permissions -
			// actually does nothing different
			// than sender.hasPermission at the
			// moment, but might change
			try {
				return NoCreativePVP.permission.has(sender, permission);
			} catch (NoSuchMethodError e) {
				// if for some reason there is a problem with Vault, fall back
				// to default permissions
				NoCreativePVP.log
				.info("[NoCreativePVP] Checking Vault permission threw an exception. Are you using the most recent version? Falling back to to default permission checking.");
			}
		}
		// fallback to default Bukkit permission checking system
		return sender.hasPermission(permission)
				|| sender.hasPermission("NoCreativePVP.*");
	}

	private boolean setupPermission() {
		if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Permission> permissionProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			}

			NoCreativePVP.log
			.info("[NoCreativePVP] Vault hooked as permission plugin.");
			return (permission != null);
		}
		permission = null; // if the plugin is reloaded during play, possibly
		// kill permissions
		NoCreativePVP.log
		.info("[NoCreativePVP] Vault plugin not found - defaulting to Bukkit permission system.");
		return false;
	}

	public void sendMultilineMessage(Player player, String message) {
		if (player != null && message != null && player.isOnline()) {
			String[] s = message.split("\n");
			for (String m : s) {
				player.sendMessage(m);
			}
		}
	}

	public static void msg(Player player, String msg) {
		player.sendMessage(ChatColor.YELLOW +"[NoCreativePVP] - " + ChatColor.WHITE + msg);
	}

	public void logMessage(String msg) {
		PluginDescriptionFile pdFile = this.getDescription();
		NoCreativePVP.log.info(pdFile.getName() + " " + pdFile.getVersion()
				+ ": " + msg);
	}

	String capitalCase(String s) {
		return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
	}

}

package me.DuppyIsCool.Main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	//Variable Declaration
	public Main plugin = Plugin.plugin;
	private ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
	private static FileConfiguration playerscfg;
	private static File playersfile;
	
	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		playersfile = new File(plugin.getDataFolder(), "players.yml");
		

		if (!playersfile.exists()) {
			plugin.saveResource("players.yml", false);
			sender.sendMessage(ChatColor.GREEN + "players.yml has been created");

		}
		
		playerscfg = YamlConfiguration.loadConfiguration(playersfile);
		
		}
	
	public FileConfiguration getPlayers() {
		return playerscfg;
	}
	

	public void savePlayers() {
		try {
			playerscfg.save(playersfile);
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "players.yml has been saved");

		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Could not save the players.yml file");
		}
	}
	
	public void reloadPlayers() {
			playerscfg = YamlConfiguration.loadConfiguration(playersfile);
			sender.sendMessage(ChatColor.BLUE + "players.yml has been reload");
	}	
}
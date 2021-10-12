package me.DuppyIsCool.Main;

import java.io.File;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements CommandExecutor{
	Mail mailObject = new Mail();
	private ConfigManager cfgm;
	public void onEnable() {
		Plugin.plugin = this;
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
          this.saveDefaultConfig();
          getLogger().info("No config.yml found! Loading default config!");
        }
        cfgm = new ConfigManager();
        cfgm.setup();
        getServer().getPluginManager().registerEvents(new AuthenticatedPlayers(), this);
		mailObject.setup();
		this.getCommand("authenticate").setExecutor(this);
		this.getCommand("code").setExecutor(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Enabling EmailVerification");
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Disabling EmailVerification");
		cfgm.savePlayers();
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("authenticate")) {
			
			if(args.length == 1) {
				if(sender instanceof Player) {
					Player p = (Player) sender;
					
					//Check if player is already authenticated
					if(!AuthenticatedPlayers.onlineDefaultPlayers.contains(p)) {
						p.sendMessage(ChatColor.RED + "You are already authenticated.");
						return true;
					}
					
					try{
						InternetAddress internetAddress = new InternetAddress(args[0]);
						internetAddress.validate();
					}catch(AddressException e){
						p.sendMessage(ChatColor.RED + "You have entered an invalid email");
						return true;
					}
					
					try {
						mailObject.createTask(p, p.getUniqueId(), args[0]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED+"Invalid Arguments. Please use /authenticate [email]");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("code")) {
			
			if(args.length == 1) {
				if(sender instanceof Player) {
					Player p = (Player) sender;
					int code = 0;
					Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
					
					if(pattern.matcher(args[0]).matches()) {
						code = Integer.parseInt(args[0]);
					}
					
					else {
						p.sendMessage(ChatColor.RED + "You have entered an invalid code");
						return true;
					}
					
					
					
					if(Mail.codesInUse.containsKey(code)) {
						if(Mail.codesInUse.get(code).equals(p.getUniqueId())) {
							p.sendMessage(ChatColor.GREEN + "You have been authenticated. Thank you and welcome!");
							AuthenticatedPlayers.onlineDefaultPlayers.remove(p);
							//Add the authenticated player to the file with their email they used to authenticate
							cfgm.getPlayers().set("players."+p.getUniqueId().toString(), AuthenticatedPlayers.emailCode.get(code));
							cfgm.savePlayers();
							AuthenticatedPlayers.emailCode.remove(code);
							Mail.codesInUse.remove(code);
							for(Player player : Bukkit.getOnlinePlayers()) {
								player.showPlayer(this,p);
							}
							p.setPlayerListName(p.getDisplayName());
							return true;
						}
						else {
							p.sendMessage(ChatColor.RED + "You have entered an invalid code");
							return true;
						}
					}else {
						p.sendMessage(ChatColor.RED + "You have entered an invalid code");
						return true;
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED+"Invalid Arguments. Please use /code [code]");				
				return true;
			}
		}

		return true;
	}
}

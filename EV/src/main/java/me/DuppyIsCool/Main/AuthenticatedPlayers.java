package me.DuppyIsCool.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;

public class AuthenticatedPlayers implements Listener {
	public static ArrayList<Player> onlineDefaultPlayers = new ArrayList<Player>();;
	private static ConfigManager cfgm = new ConfigManager();
	//This map temporarily holds codes and the e-mail they were sent to
	public static HashMap<Integer,String> emailCode = new HashMap<Integer,String>();
	
	@EventHandler
	public void onJoin(PlayerLoginEvent e) {
		//If the player is authenticated, has bypass permission, or is an OP
		if(isAuthenticated(e.getPlayer().getUniqueId()) || e.getPlayer().isOp() || e.getPlayer().hasPermission("emailauth.bypass")){
			//If the server is full
			if(Bukkit.getServer().getMaxPlayers() == Bukkit.getOnlinePlayers().size()) {
				 //if the slot is being used by a default player
				if(onlineDefaultPlayers.size() > 0) {
					//Kick the default player who was first and allow the login
					onlineDefaultPlayers.get(0).kickPlayer(ChatColor.RED + "You were kicked to make room for an authenticated user");
					e.allow();
				}
				else if(e.getPlayer().isOp()){
					e.allow();
				}
				else {
					e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.RED + "The server is currently full");
				} 
			}
			
			//If the server is not full
			else {
				e.allow();
			}
		}
		//If the player is not authenticated
		else {
			//If the server is full
			if(Bukkit.getServer().getMaxPlayers() == Bukkit.getOnlinePlayers().size()) {
				e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.RED + "The server is currently full");
			}
			else {
				e.allow();
				//Hide the player
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(!p.isOp()) {
						p.hidePlayer(Plugin.plugin, e.getPlayer());
						e.getPlayer().setPlayerListName(null);
					}
				}
				onlineDefaultPlayers.add(e.getPlayer());
				e.getPlayer().setWalkSpeed(0.5f);
			}
		}
	}
	
	//Hide default players from new players.
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for(Player p : onlineDefaultPlayers) {
			if(!e.getPlayer().isOp())
				e.getPlayer().hidePlayer(Plugin.plugin,p);
		}
	}
	
	
	//Remove the default players from the list of online default players on leave
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		//If the player was an online default player, remove them
		if(onlineDefaultPlayers.contains(e.getPlayer()))
			onlineDefaultPlayers.remove(e.getPlayer());
	}
	
	//Block all interaction from default players
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(onlineDefaultPlayers.contains(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You are not authenticated. Type /authenticate [email] to authenticate");
			e.setCancelled(true);
		}
	}
	
	//Block all chat from default players
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(onlineDefaultPlayers.contains(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You are not authenticated. Type /authenticate [email] to authenticate");
			e.setCancelled(true);
		}
	}
	
	//Prevent default players from picking up itmes
	@EventHandler
	public void onCollect(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(onlineDefaultPlayers.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	//Prevent default players from receiving damage
	@EventHandler
	public void onReceiveDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(onlineDefaultPlayers.contains(p)) {
				p.sendMessage(ChatColor.RED + "You are not authenticated. Type /authenticate [email] to authenticate");
				e.setCancelled(true);
			}
		}
	}
	//Prevent default players from dealing damage
	@EventHandler
	public void onDealDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			
			if(onlineDefaultPlayers.contains(p)) {
				p.sendMessage(ChatColor.RED + "You are not authenticated. Type /authenticate [email] to authenticate");
				e.setCancelled(true);
			}
		}
	}
	
	//Hide default players from new players.
	@EventHandler
	public void onEntityTarger(EntityTargetLivingEntityEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			if(onlineDefaultPlayers.contains(p)) {
				e.setCancelled(true);
				e.setTarget(null);
			}
		}
	}
	
	public static boolean isAuthenticated(UUID userID) {
		if(cfgm.getPlayers().contains("players."+userID.toString(), false)) {
			return true;
		}
		return false;
	}
}

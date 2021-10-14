package me.DuppyIsCool.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Mail {
	//Move these to config based
	private String username,password,subject,from,message,host;
	private ArrayList<String> extensions;
	private int port,time;
	public static HashMap<Integer,UUID> codesInUse = new HashMap<Integer,UUID>();
	public void setup() {
		//Grab the values from the config
		FileConfiguration config = Plugin.plugin.getConfig();
		try {
			username = config.getString("mailserver.username");
			password = config.getString("mailserver.password");
			from = config.getString("mailserver.from");
			subject = config.getString("mailserver.subject");
			message = config.getString("mailserver.message");
			extensions = (ArrayList<String>) config.getStringList("mailserver.extensions");
			host = config.getString("mailserver.host");
			port = config.getInt("mailserver.port");
			
			time = config.getInt("authentication.time");
		}
		//Error grabing values (User's fault most likely)
		catch(Exception e) {
			e.printStackTrace();
			Bukkit.getServer().getPluginManager().disablePlugin(Plugin.plugin);
		}
	}
	
	public void createTask(Player p, UUID userID, String email) throws Exception{
		//Validate e-mail
		boolean isValid = false;
		try{
			InternetAddress internetAddress = new InternetAddress(email);
			internetAddress.validate();
			isValid = true;
		}catch(AddressException e){
			System.out.println("Invalid email");
		}
		
		//See if the e-mail provided is valid and has a valid e-mail extension (if provided)
		if((extensions.size() == 0 || extensions.contains(email.substring(email.indexOf("@")+1).toLowerCase())) && isValid){
			
			if(!codesInUse.containsValue(userID)) {
				int code = generateCode();
				//Run a task to put the command on cooldown for that user
				@SuppressWarnings("unused")
				BukkitTask task = new AuthTask(code,this.time).runTask(Plugin.plugin);
				AuthenticatedPlayers.emailCode.put(code, email);
				codesInUse.put(code, userID);
				//Send the email with the code
				sendMail(p, email, code);
			}
			
			//Error message to send if e-mail has already been sent (to prevent spam)
			else {
				int timetowait = this.time/60;
				if(timetowait == 0) { 
					timetowait = this.time;
					p.sendMessage(ChatColor.RED + "An e-mail has already been sent. Please wait "+timetowait+ " seconds before authenticating." );
				}
				else {
					p.sendMessage(ChatColor.RED + "An e-mail has already been sent. Please wait "+timetowait+ " minutes before authenticating." );
				}
			}				
		}
		
		else{
			p.sendMessage(ChatColor.RED + "The email entered is not a valid email for authentication. Please try again.");
		}
		
	}
	
	//Generates a valid code for the system to use
	private int generateCode(){
		int code = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);
		
		while(codesInUse.containsKey(code)){
			code = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);
		}
		
		return code;
	}
	
	//Sends the e-mail to the player Asynchronously
	private void sendMail(Player p, String userEmail, int code) throws Exception{
		//Run mail send on a seperate thread.
		new MailTask(p,host,port+"",message,from,subject,username,password,userEmail,code+"").runTaskAsynchronously(Plugin.plugin);
	}
	
}

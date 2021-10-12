package me.DuppyIsCool.Main;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MailTask extends BukkitRunnable {
	private String host,from,message,subject,port,username,password,userEmail,code;
	private Player p;
	
	public MailTask(Player p, String host,String port, String message, String from, String subject, String username, String password, String userEmail, String code) {
		this.message = message;
		this.p = p;
		this.host = host;
		this.username = username;
		this.password = password;
		this.userEmail = userEmail;
		this.code = code;
		this.port = port;
		this.subject = subject;
		this.from = from;
	}
	
	public void run() {
		
		try {
			Properties props = new Properties();
			
			//Setup Properties
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			System.out.println("port: "+port);
			//Create Session using username and password
			Session session = Session.getDefaultInstance(props);
			
			Message message = prepareMessage(session,userEmail,code);
			
			Transport transport = session.getTransport("smtps");
			transport.connect(host,username,password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			p.sendMessage(ChatColor.GREEN + "A message with your code has been e-mailed to: "+userEmail);
			p.sendMessage(ChatColor.GREEN + "Once you receive your code type /code [code] to authenticate");
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + p.getDisplayName()+ "Requested code:"+code+" be sent to "+userEmail);
			this.cancel();
		} catch (MessagingException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+ "Failed to send mail to: "+userEmail);
			p.sendMessage(ChatColor.RED + "Failed to send mail to: "+userEmail +"\nPlease Contact Duppy.");
			e.printStackTrace();
			this.cancel();
		}
		
		
	}
	
	//Prepares the message to be sent by Transport
		private Message prepareMessage(Session session, String recipient, String code) {
			try {
				Message message = new MimeMessage(session);
				
				//Setup From and Recipients
				message.setFrom(new InternetAddress(from));	
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				
				message.setSubject(subject);
				
				//Replace the phrase %CODE% with the code
				String reformattedmessage = this.message.replaceAll("%CODE%", code);
				message.setText(reformattedmessage);
				
				//Return the message to be sent
				return message;
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

}

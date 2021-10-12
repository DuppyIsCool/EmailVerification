package me.DuppyIsCool.Main;

import org.bukkit.scheduler.BukkitRunnable;

public class AuthTask extends BukkitRunnable {
	private int time;
	private int code;
	public AuthTask(int code, int time) {
		if(time < 1) {
			throw new IllegalArgumentException("Counter must be greater than 1");
		}
		else {
			this.time = time;
			this.code = code;
		}
	}
	
	public void run() {
		if(time > 0 && Mail.codesInUse.containsKey(code)) {
			time--;
		}
		
		else {
			
			Mail.codesInUse.remove(code);
			AuthenticatedPlayers.emailCode.remove(code);
			this.cancel();
		}
	}
}

package com.comze_instancelabs.mgarcade;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.ClassesConfig;

public class IClassesConfig extends ClassesConfig {

	public IClassesConfig(JavaPlugin plugin) {
		super(plugin, true);
    	this.getConfig().options().header("Arcade doesn't have any classes.");
    	this.getConfig().options().copyDefaults(true);
    	this.saveConfig();
	}

}

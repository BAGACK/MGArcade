package com.comze_instancelabs.mgarcade;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class Main extends JavaPlugin implements Listener {

	MinigamesAPI api = null;
	Main m = null;
	static ArrayList<PluginInstance> minigames = new ArrayList<PluginInstance>();

	public void onEnable() {
		m = this;
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				minigames = new ArrayList<PluginInstance>(api.pinstances.values());
			}
		}, 10L);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				api = MinigamesAPI.getAPI().setupAPI(m, "arcade", IArena.class, new ArenasConfig(m), new MessagesConfig(m), new IClassesConfig(m), new StatsConfig(m, false), new DefaultConfig(m, false), false);
				PluginInstance pinstance = api.pinstances.get(m);
				pinstance.addLoadedArenas(loadArenas(m, pinstance.getArenasConfig()));
				pinstance.scoreboardManager = new IArenaScoreboard();
			}
		}, 20L);
		Bukkit.getPluginManager().registerEvents(m, m);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return api.getCommandHandler().handleArgs(this, "arcade", "/" + cmd.getName(), sender, args);
	}

	public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
		ArrayList<Arena> ret = new ArrayList<Arena>();
		FileConfiguration config = cf.getConfig();
		if (!config.isSet("arenas")) {
			return ret;
		}
		for (String arena : config.getConfigurationSection("arenas.").getKeys(false)) {
			if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
				ret.add(initArena(plugin, arena));
			}
		}
		return ret;
	}

	public static IArena initArena(JavaPlugin plugin, String arena) {
		IArena a = new IArena(plugin, arena, minigames);
		ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(plugin).arenaSetup;
		a.init(Util.getSignLocationFromArena(plugin, arena), Util.getAllSpawns(plugin, arena), Util.getMainLobby(plugin), Util.getComponentForArena(plugin, arena, "lobby"), s.getPlayerCount(plugin, arena, true), s.getPlayerCount(plugin, arena, false), s.getArenaVIP(plugin, arena));
		return a;
	}

}

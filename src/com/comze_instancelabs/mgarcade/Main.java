package com.comze_instancelabs.mgarcade;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
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

	ICommandHandler ic;
	
	public void onEnable() {
		m = this;
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				minigames = new ArrayList<PluginInstance>(api.pinstances.values());
			}
		}, 10L);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				api = MinigamesAPI.getAPI().setupAPI(m, "arcade", IArena.class, new ArenasConfig(m), new MessagesConfig(m), new IClassesConfig(m), new StatsConfig(m, false), new DefaultConfig(m, false), true);
				PluginInstance pinstance = api.pinstances.get(m);
				pinstance.addLoadedArenas(loadArenas(m, pinstance.getArenasConfig()));
				pinstance.scoreboardManager = new IArenaScoreboard();
				pinstance.setClassesHandler(new IClasses(m));
				IArenaListener ia = new IArenaListener(m, pinstance, "arcade");
				pinstance.setArenaListener(ia);
				MinigamesAPI.getAPI().registerArenaListenerLater(m, ia);
			}
		}, 20L);
		Bukkit.getPluginManager().registerEvents(m, m);

		this.getConfig().addDefault("config.arcade.min_players", 2);
		this.getConfig().addDefault("config.arcade.lobby_countdown", 30);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		ic = new ICommandHandler();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ic.handleArgs(this, "arcade", "/" + cmd.getName(), sender, args);
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("nextminigame")) {
				if (sender.hasPermission("arcade.nextminigame")) {
					if (args.length > 1) {
						IArena a = (IArena) api.pinstances.get(m).getArenaByName(args[1]);
						if (a != null) {
							a.ai.stopCurrentMinigame();
							// a.ai.nextMinigame();
						}
					} else {
						sender.sendMessage(ChatColor.RED + "/arcade nextminigame <arena>");
					}
				}
			} else if (args[0].equalsIgnoreCase("setenabled")) {
				if (sender.hasPermission("arcade.setenabled")) {
					if (args.length > 2) {
						String mg = args[1];
						String bool = args[2];

						if (bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("true")) {
							Plugin plugin = Bukkit.getPluginManager().getPlugin(mg);
							if (plugin != null) {
								plugin.getConfig().set("config.arcade.enabled", Boolean.parseBoolean(bool));
								plugin.saveConfig();
							} else {
								sender.sendMessage(ChatColor.RED + "Minigame not found. Try /arcade listminigames and don't forget that caps matter!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "/arcade setenabled <minigame> <true/false>");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "/arcade setenabled <minigame> <true/false>");
					}
				}
			} else if (args[0].equalsIgnoreCase("listminigames") || args[0].equalsIgnoreCase("listgames")) {
				for (PluginInstance pli : minigames) {
					if (pli.getPlugin().getConfig().getBoolean("config.arcade.enabled")) {
						sender.sendMessage(ChatColor.GREEN + pli.getPlugin().getName());
					} else {
						sender.sendMessage(ChatColor.RED + pli.getPlugin().getName());
					}
				}
			}
		}
		return true;
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

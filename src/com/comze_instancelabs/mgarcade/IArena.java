package com.comze_instancelabs.mgarcade;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.arcade.ArcadeInstance;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArena extends Arena {

	public static JavaPlugin plugin;

	ArcadeInstance ai;

	public IArena(JavaPlugin plugin, String arena_id, ArrayList<PluginInstance> minigames) {
		super(plugin, arena_id);
		this.plugin = plugin;
		ai = new ArcadeInstance(plugin, minigames, this);
		this.setAlwaysPvP(true);
	}

	@Override
	public void joinPlayerLobby(String p) {
		PluginInstance pli = MinigamesAPI.getAPI().pinstances.get(plugin);
		pli.global_players.put(p, this);
		ai.joinArcade(p);
		this.updateSign(ai);
	}

	@Override
	public void startLobby() {
		start(false);
	}

	@Override
	public void start(boolean tp) {
		ai.startArcade();
		this.setArenaState(ArenaState.STARTING);
		this.updateSign(ai);
	}

	@Override
	public void stop() {
		ai.stopArcade(true);
		this.setArenaState(ArenaState.JOIN);
		this.updateSign(ai);
	}

	@Override
	public void leavePlayer(String playername, boolean fullLeave) {
		PluginInstance pli = MinigamesAPI.getAPI().pinstances.get(plugin);
		if (pli.global_players.containsKey(playername)) {
			pli.global_players.remove(playername);
		}
		Player p = Bukkit.getPlayer(playername);
		if (p != null) {
			if (!p.isOp()) {
				p.setFlying(false);
				p.setAllowFlight(false);
			}
		}
		ai.leaveArcade(playername);
	}

	public void updateSign(ArcadeInstance ai) {
		Arena arena = this;
		Sign s = Util.getSignFromArena(plugin, this.getName());
		int count = ai.players.size();
		int maxcount = this.getMaxPlayers(); // TODO change this
		if (s != null) {
			s.setLine(0, MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().getConfig().getString("signs." + arena.getArenaState().toString().toLowerCase() + ".0").replaceAll("&", "�").replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount)).replace("<arena>", arena.getName()).replace("[]", MessagesConfig.squares));
			s.setLine(1, MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().getConfig().getString("signs." + arena.getArenaState().toString().toLowerCase() + ".1").replaceAll("&", "�").replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount)).replace("<arena>", arena.getName()).replace("[]", MessagesConfig.squares));
			s.setLine(2, MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().getConfig().getString("signs." + arena.getArenaState().toString().toLowerCase() + ".2").replaceAll("&", "�").replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount)).replace("<arena>", arena.getName()).replace("[]", MessagesConfig.squares));
			s.setLine(3, MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().getConfig().getString("signs." + arena.getArenaState().toString().toLowerCase() + ".3").replaceAll("&", "�").replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount)).replace("<arena>", arena.getName()).replace("[]", MessagesConfig.squares));
			s.update();
		}
	}

}

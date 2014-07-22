package com.comze_instancelabs.mgarcade;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.arcade.ArcadeInstance;

public class IArena extends Arena {

	public static JavaPlugin plugin;

	ArcadeInstance ai;

	public IArena(JavaPlugin plugin, String arena_id, ArrayList<PluginInstance> minigames) {
		super(plugin, arena_id);
		this.plugin = plugin;
		ai = new ArcadeInstance(minigames, this);
	}

	@Override
	public void joinPlayerLobby(String p) {
		ai.joinArcade(p);
	}

	@Override
	public void startLobby() {
		start(false);
	}

	@Override
	public void start(boolean tp) {
		ai.startArcade();
	}

	@Override
	public void stop() {
		ai.stopArcade();
	}

	@Override
	public void leavePlayer(String playername, boolean fullLeave) {
		ai.leaveArcade(playername);
	}

}

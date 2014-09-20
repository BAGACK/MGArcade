package com.comze_instancelabs.mgarcade;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaListener;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;

public class IArenaListener extends ArenaListener {

	JavaPlugin plugin;

	public IArenaListener(JavaPlugin plugin, PluginInstance pinstance, String minigame) {
		super(plugin, pinstance, minigame);
		this.plugin = plugin;
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		PluginInstance pli = MinigamesAPI.getAPI().pinstances.get(plugin);
		if (pli.global_players.containsKey(event.getEntity().getName())) {
			final Arena arena = pli.global_players.get(event.getEntity().getName());
			if (arena.getArenaState() != ArenaState.JOIN) {
				super.onPlayerDeath(event);
			}
		}
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event){
		
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event){
		
	}

}

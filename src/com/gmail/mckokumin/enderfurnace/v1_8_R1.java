package com.gmail.mckokumin.enderfurnace;

import net.minecraft.server.v1_8_R1.TileEntityFurnace;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.mckokumin.enderfurnace.events.TickUpdateEvent;

public class v1_8_R1 implements Listener{
	public static Main plugin;
	
	@SuppressWarnings("static-access")
	public v1_8_R1(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onTick(TickUpdateEvent event){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (!plugin.furnaces.containsKey(p.getName())
					&& plugin.getFurnaceLocation() != null){
				int x = 10000000;
				int y = 255;
				int z = 10000000;
				TileEntityFurnace furnace;
				plugin.getFurnaceLocation().getBlock().getWorld().getBlockAt(x, y, z).setType(Material.FURNACE);
				furnace = ((TileEntityFurnace) ((CraftWorld) plugin.getFurnaceLocation().getBlock().getWorld())
						.getTileEntityAt(x, y, z));
				plugin.furnaces.put(p.getName(), new CraftInventoryFurnace(furnace));
				plugin.getFurnaceLocation().getBlock().getWorld().getBlockAt(x, y, z).setType(Material.AIR);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void furnaceListener(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| event.getClickedBlock().getLocation() == null){
			return;
		}
		Location clicked = event.getClickedBlock().getLocation();
		if (p.getItemInHand() != null){
			if (plugin.isFurnaceSetter(p.getItemInHand())){
				plugin.setFurnaceLocation(clicked);
				p.sendMessage("§eエンダーかまどの位置を設定しました");
				return;
			}
		}
		if (plugin.getFurnaceLocation() != null){
			if (clicked.getBlockX() == plugin.getFurnaceLocation().getBlockX()
					&& clicked.getBlockY() == plugin.getFurnaceLocation().getBlockY()
					&& clicked.getBlockZ() == plugin.getFurnaceLocation().getBlockZ()
					&& clicked.getWorld().getName() == plugin.getFurnaceLocation().getWorld().getName()){
				event.setCancelled(true);
				p.openInventory(plugin.furnaces.get(p.getName()));
			}
		}
	}

	@EventHandler
	public void breakCanceller(BlockBreakEvent event){
		Location broke = event.getBlock().getLocation();
		if (broke.getBlockX() == plugin.getFurnaceLocation().getBlockX()
				&& broke.getBlockY() == plugin.getFurnaceLocation().getBlockY()
				&& broke.getBlockZ() == plugin.getFurnaceLocation().getBlockZ()
				&& broke.getWorld().getName() == plugin.getFurnaceLocation().getWorld().getName()){
			event.setCancelled(true);
		}
	}

}

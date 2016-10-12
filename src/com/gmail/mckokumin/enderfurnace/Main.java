package com.gmail.mckokumin.enderfurnace;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.mckokumin.enderfurnace.events.TickUpdateEvent;

public class Main extends JavaPlugin implements Listener, CommandExecutor{
	public HashMap<String, FurnaceInventory> furnaces = new HashMap<String, FurnaceInventory>();
	public int tick = 1;

	@Override
	public void onEnable(){
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (tick < 20){
					tick++;
				} else {
					tick = 1;
				}
				getServer().getPluginManager().callEvent(new TickUpdateEvent(tick));
			}
		}, 0L, 1L);
		saveDefaultConfig();
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
		load();
	}

	@Override
	public void onDisable(){

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		switch(cmd.getName()){
		case "enderfurnace":
			if (!(sender instanceof Player)){
				return true;
			}
			((Player)sender).getInventory().addItem(furnaceSetter());
			sender.sendMessage("§eFurnaceSetterを手に入れました");
			break;
		default:
			break;
		}
		return true;
	}

	public ItemStack furnaceSetter(){
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(org.bukkit.ChatColor.YELLOW + "FurnaceSetter");
		item.setItemMeta(im);
		return item;
	}

	public boolean isFurnaceSetter(ItemStack item){
		if (item.getType() == Material.STICK
				&& item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "FurnaceSetter")){
			return true;
		}
		return false;
	}

	public void setFurnaceLocation(Location loc){
		getConfig().set("furnace.world", loc.getWorld().getName());
		getConfig().set("furnace.x", loc.getBlockX());
		getConfig().set("furnace.y", loc.getBlockY());
		getConfig().set("furnace.z", loc.getBlockZ());
		saveConfig();
	}

	public Location getFurnaceLocation(){
		try {
			World world = Bukkit.getWorld(getConfig().getString("furnace.world"));
			int x = getConfig().getInt("furnace.x");
			int y = getConfig().getInt("furnace.y");
			int z = getConfig().getInt("furnace.z");
			Location furnace = new Location(world, x, y, z);
			return furnace;
		} catch (NullPointerException e){
			return null;
		}
	}

	private void load() {
		Server s = Bukkit.getServer();
		String pg = s.getClass().getPackage().getName();
		pg = pg.substring(pg.lastIndexOf('.') + 1);
		switch(pg){
		case "v1_8_R1":
			getServer().getPluginManager().registerEvents(new v1_8_R1(this), this);
			break;
		case "v1_8_R2":
			getServer().getPluginManager().registerEvents(new v1_8_R2(this), this);
			break;
		case "v1_8_R3":
			getServer().getPluginManager().registerEvents(new v1_8_R3(this), this);
			break;
		case "v1_9_R1":
			getServer().getPluginManager().registerEvents(new v1_9_R1(this), this);
			break;
		case "v1_9_R2":
			getServer().getPluginManager().registerEvents(new v1_9_R2(this), this);
			break;
		case "v1_10_R1":
			getServer().getPluginManager().registerEvents(new v1_10_R1(this), this);
			break;
		default:
			break;
		}
	}


}

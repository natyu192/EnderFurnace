package com.gmail.mckokumin.enderfurnace;

import java.util.HashMap;

import net.minecraft.server.v1_9_R1.TileEntityFurnace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
	}

	@Override
	public void onDisable(){

	}

	@EventHandler
	public void onTick(TickUpdateEvent event){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (!furnaces.containsKey(p.getName())
					&& getFurnaceLocation() != null){
				int x = 10000000;
				int y = 255;
				int z = 10000000;
				TileEntityFurnace furnace;
				getFurnaceLocation().getBlock().getWorld().getBlockAt(x, y, z).setType(Material.FURNACE);
				furnace = ((TileEntityFurnace) ((CraftWorld) getFurnaceLocation().getBlock().getWorld())
						.getTileEntityAt(x, y, z));
				furnaces.put(p.getName(), new CraftInventoryFurnace(furnace));
				getFurnaceLocation().getBlock().getWorld().getBlockAt(x, y, z).setType(Material.AIR);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void furnaceListener(PlayerInteractEvent event){
		Player p = event.getPlayer();
		Location clicked = event.getClickedBlock().getLocation();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| clicked == null){
			return;
		}
		if (p.getItemInHand() != null){
			if (isFurnaceSetter(p.getItemInHand())){
				setFurnaceLocation(clicked);
				p.sendMessage("§eエンダーかまどの位置を設定しました");
				return;
			}
		}
		if (getFurnaceLocation() != null){
			if (clicked.getBlockX() == getFurnaceLocation().getBlockX()
					&& clicked.getBlockY() == getFurnaceLocation().getBlockY()
					&& clicked.getBlockZ() == getFurnaceLocation().getBlockZ()
					&& clicked.getWorld().getName() == getFurnaceLocation().getWorld().getName()){
				p.openInventory(furnaces.get(p.getName()));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakCanceller(BlockBreakEvent event){
		Location broke = event.getBlock().getLocation();
		if (broke.getBlockX() == getFurnaceLocation().getBlockX()
				&& broke.getBlockY() == getFurnaceLocation().getBlockY()
				&& broke.getBlockZ() == getFurnaceLocation().getBlockZ()
				&& broke.getWorld().getName() == getFurnaceLocation().getWorld().getName()){
			event.setCancelled(true);
		}
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

	public boolean isFurnaceSetter(ItemStack item){
		if (item.getType() == Material.STICK
				&& item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "FurnaceSetter")){
			return true;
		}
		return false;
	}

	public ItemStack furnaceSetter(){
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(org.bukkit.ChatColor.YELLOW + "FurnaceSetter");
		item.setItemMeta(im);
		return item;
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


}
